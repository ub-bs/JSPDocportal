/*
 * $RCSfile$
 * $Revision: 19696 $ $Date: 2011-01-04 13:45:05 +0100 (Di, 04 Jan 2011) $
 *
 * This file is part of ***  M y C o R e  ***
 * See http://www.mycore.de/ for details.
 *
 * This program is free software; you can use it, redistribute it
 * and / or modify it under the terms of the GNU General Public License
 * (GPL) as published by the Free Software Foundation; either version 2
 * of the License or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program, in a file called gpl.txt or license.txt.
 * If not, write to the Free Software Foundation Inc.,
 * 59 Temple Place - Suite 330, Boston, MA  02111-1307 USA
 */
package org.mycore.frontend.restapi.v1;

import static org.mycore.access.MCRAccessManager.PERMISSION_WRITE;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.mycore.access.MCRAccessManager;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRException;
import org.mycore.common.MCRPersistenceException;
import org.mycore.common.MCRSession;
import org.mycore.common.MCRSessionMgr;
import org.mycore.common.MCRUserInformation;
import org.mycore.common.MCRUtils;
import org.mycore.common.content.MCRStringContent;
import org.mycore.common.xml.MCRXMLParserFactory;
import org.mycore.datamodel.ifs.MCRDirectory;
import org.mycore.datamodel.ifs.MCRFileImportExport;
import org.mycore.datamodel.ifs.MCRFilesystemNode;
import org.mycore.datamodel.metadata.MCRDerivate;
import org.mycore.datamodel.metadata.MCRMetaIFS;
import org.mycore.datamodel.metadata.MCRMetaLinkID;
import org.mycore.datamodel.metadata.MCRMetadataManager;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.cli.MCRObjectCommands;
import org.mycore.frontend.restapi.v1.utils.MCREncryptionHelper;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManager;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManagerFactory;
import org.mycore.frontend.workflowengine.strategies.MCRDerivateStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRWorkflowDirectoryManager;
import org.xml.sax.SAXParseException;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Path("/v1/uploads")
public class MCRRestAPIUploads {
    public static final String FORMAT_XML = "xml";
    @Resource
    ServletContext context;

    //private static SimpleDateFormat SDF_UTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    /** returns a list of mcrObjects 
     *
     * Parameter
     * ---------
     * filter - parameter with filters as colon-separated key-value-pairs, repeatable, allowed values are
     *     * project - the MyCoRe ProjectID - first Part of a MyCoRe ID
     *     * type - the MyCoRe ObjectType - middle Part of a MyCoRe ID 
     *     * lastModifiedBefore - last modified date in UTC is lesser than or equals to given value 
     *     * lastModifiedAfter - last modified date in UTC is greater than or equals to given value;
     * 
     * sort - sortfield and sortorder combined by ':'
     *     * sortfield = ID | lastModified
     *     * sortorder = asc | desc 
     * 
     * format - parameter for return format, values are
     *     * xml (default value)
     *     * json
     *     
     * based upon:    
     * http://puspendu.wordpress.com/2012/08/23/restful-webservice-file-upload-with-jersey/
     */

    @POST
    @Path("/objects/")
    @Produces({ MediaType.TEXT_XML + ";charset=UTF-8" })
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadObject(@Context UriInfo info, @Context HttpServletRequest request,
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetails) {

        if (checkAccess(request)) {
            MCRSession session = MCRServlet.getSession(request);
            session.beginTransaction();
            File fXML = null;
            try {
                SAXBuilder sb = new SAXBuilder();
                Document docOut = sb.build(uploadedInputStream);

                MCRObjectID oldID = MCRObjectID.getInstance(docOut.getRootElement().getAttributeValue("ID"));
                MCRWorkflowManager wfm = MCRWorkflowManagerFactory.getImpl(oldID);
                if (wfm != null) {
                    String saveDirectory = MCRWorkflowDirectoryManager.getWorkflowDirectory(wfm.getMainDocumentType());
                    MCRObjectID mcrObjID = wfm.getNextFreeID(oldID.getTypeId());
                    fXML = new File(new File(saveDirectory), mcrObjID.toString() + ".xml");

                    docOut.getRootElement().setAttribute("ID", mcrObjID.toString());
                    docOut.getRootElement().setAttribute("label", mcrObjID.toString());
                    XMLOutputter xmlOut = new XMLOutputter(Format.getPrettyFormat());
                    try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fXML),
                            "UTF-8"))) {
                        xmlOut.output(docOut, bw);
                    }

                    MCRObjectCommands.loadFromFile(fXML.getPath());

                    setDefaultPermission(mcrObjID,  wfm.getWorkflowProcessType(), "admin" );
                    
                    return Response
                            .created(info.getBaseUriBuilder().path("v1/objects/id/" + mcrObjID.toString()).build())
                            .type("application/xml; charset=UTF-8").build();
                }
            } catch (Exception e) {
                //do nothing
            }

            session.commitTransaction();
            if (fXML != null) {
                fXML.delete();
            }

        }
        return Response.status(Status.FORBIDDEN).build();

    }

    @POST
    @Path("/objects/id/{mcrObjID}/derivates")
    @Produces({ MediaType.TEXT_XML + ";charset=UTF-8" })
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadDerivate(@Context UriInfo info, @Context HttpServletRequest request,
            @PathParam("mcrObjID") String mcrObjID, @FormDataParam("label") String label) {

        Response response = Response.status(Status.FORBIDDEN).build();
        if (checkAccess(request)) {
            MCRSession session = MCRServlet.getSession(request);
            session.beginTransaction();
            File fXML = null;
            try {
                MCRObjectID objID = MCRObjectID.getInstance(mcrObjID);
                MCRWorkflowManager wfm = MCRWorkflowManagerFactory.getImpl(objID);
                if (wfm != null) {
                    File saveDir = new File(MCRWorkflowDirectoryManager.getWorkflowDirectory(wfm.getMainDocumentType()));

                    MCRObjectID derID = MCRDerivateStrategy.setNextFreeDerivateID(saveDir);

                    MCRDerivate mcrDerivate = new MCRDerivate();
                    mcrDerivate.setLabel(label);
                    mcrDerivate.setId(derID);
                    mcrDerivate.setSchema("datamodel-derivate.xsd");
                    mcrDerivate.getDerivate().setLinkMeta(new MCRMetaLinkID("linkmeta", objID, null, null));
                    mcrDerivate.getDerivate().setInternals(
                            new MCRMetaIFS("internal", new File(saveDir, derID.toString()).getPath()));

                    MCRMetadataManager.create(mcrDerivate);
                    MCRMetadataManager.addDerivateToObject(objID, new MCRMetaLinkID("derObject", derID, null, label));

                    fXML = new File(saveDir, derID.toString() + ".xml");
                    
                    session.commitTransaction();
                    session.beginTransaction();
                    setDefaultPermission(derID,  wfm.getWorkflowProcessType(), "admin" );

                    response = Response
                            .created(
                                    info.getBaseUriBuilder()
                                            .path("v1/objects/id/" + objID.toString() + "/derivates/id/"
                                                    + derID.toString()).build()).type("application/xml; charset=UTF-8")
                            .build();
                }
            } catch (Exception e) {
                Logger.getLogger(this.getClass()).error("Exeption while uploading derivate", e);
            }

            session.commitTransaction();
            if (fXML != null) {
                fXML.delete();
            }

        }
        return response;

    }

    @POST
    @Path("/objects/id/{mcrObjID}/derivates/id/{mcrDerID}/files")
    @Produces({ MediaType.TEXT_XML + ";charset=UTF-8" })
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@Context UriInfo info, @Context HttpServletRequest request,
            @PathParam("mcrObjID") String mcrObjID, @PathParam("mcrDerID") String mcrDerID,
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetails, 
            @FormDataParam("rest-client") String clientID,
            @FormDataParam("path") String path,
            @DefaultValue("false") @FormDataParam("maindoc") boolean maindoc,
            @FormDataParam("sha1") String sha1,
            @FormDataParam("size") Long size){
        
        Response response = Response.status(Status.FORBIDDEN).build();
        if (checkAccess(request)) {
            
        SortedMap<String, String> parameter = new TreeMap<>();
        parameter.put("rest-client", clientID);
        parameter.put("path", path);
        parameter.put("maindoc", Boolean.toString(maindoc));
        parameter.put("sha1", sha1);
        parameter.put("size", Long.toString(size));
    
        
        String keyFileLocation = MCRConfiguration.instance().getString("MCR.RestAPI.v1.Client."+clientID+".PublicKeyFile");
        if(keyFileLocation == null){
            //ToDo error
        }
        String base64Signature = request.getHeader("X-MyCoRe-RestAPI-Signature");
        if(base64Signature== null){
            //ToDo error handling
        }
        if(!MCREncryptionHelper.verifyPropertiesWithSignature(parameter, base64Signature, new File(keyFileLocation))){
            //validation failed -> error handling
  
        }
        else {
        
        //MCRSession session = MCRServlet.getSession(request);
        MCRSession session = MCRSessionMgr.getCurrentSession();
        MCRUserInformation currentUser = session.getUserInformation();
       
     
          
            session.beginTransaction();
            MCRUserInformation workingUser = new MCRUserInformation() {
                @Override
                public boolean isUserInRole(String role) {
                    return role.equals("editorP");                            
                }
                
                @Override
                public String getUserID() {
                    return "admin";
                }
                
                @Override
                public String getUserAttribute(String attribute) {
                    return null;
                }

            }; 
            session.setUserInformation(workingUser);
            MCRObjectID objID = MCRObjectID.getInstance(mcrObjID);
            MCRObjectID derID = MCRObjectID.getInstance(mcrDerID);
            
            //MCRAccessManager.checkPermission(uses CACHE, which seems to be dirty from other calls and cannot be deleted)????
            if (MCRAccessManager.getAccessImpl().checkPermission(derID.toString(), PERMISSION_WRITE)) {
                
            
            MCRDerivate der = MCRMetadataManager.retrieveMCRDerivate(derID);

            File derDir = null;
            File saveFile = null;
            if (der.getOwnerID().equals(objID)) {

                MCRWorkflowManager wfm = MCRWorkflowManagerFactory.getImpl(objID);
                if (wfm != null) {
                    File saveDir = new File(MCRWorkflowDirectoryManager.getWorkflowDirectory(wfm
                            .getMainDocumentType()));
                    try {
                        
                        derDir = new File(saveDir, derID.toString());
                        MCRUtils.deleteDirectory(derDir);
                        path = path.replace("\\","/").replace("../", "");
                        saveFile = new File(derDir, path);
                        
                        saveFile.getParentFile().mkdirs();
                        Files.copy(uploadedInputStream, saveFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        uploadedInputStream.close();
                        
                        MCRDirectory difs = MCRDirectory.getRootDirectory(derID.toString());
                        if(difs==null){
                            difs = new MCRDirectory(derID.toString());
                        }
                        
                        //delete old file
                        MCRFilesystemNode fsn = difs.getChildByPath(path);
                        if(fsn!=null){
                            try{
                                fsn.delete();
                            }
                            catch(MCRPersistenceException pe){
                                Logger.getLogger(this.getClass()).error(pe);
                            }
                        }
                        MCRFileImportExport.importFiles(derDir, difs);
                        der.getDerivate().getInternals().setIFSID(difs.getID());
                        
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
              
                
                MCRUtils.deleteDirectory(derDir);
                
                if (maindoc) {
                    der.getDerivate().getInternals().setMainDoc(path);
                    try{
                        derDir = new File(saveDir, derID.toString());
                        derDir.mkdirs();
                        MCRMetadataManager.update(der);
                        MCRUtils.deleteDirectory(derDir);
                    }
                    catch(MCRException e){
                        //will be handled tomorrow
                        Logger.getLogger(this.getClass()).error(e);
                    }
                }

                }
            }

            session.commitTransaction();
            session.setUserInformation(currentUser);
            response = Response
                    .created(
                            info.getBaseUriBuilder()
                                    .path("v1/objects/id/" + objID.toString() + "/derivates/id/"
                                            + derID.toString()+"/files").build()).type("application/xml; charset=UTF-8")
                    .build();

        
        }
        }
        }
        return response;
        
    }

    private boolean checkAccess(HttpServletRequest request) {
        return ",127.0.0.1,::1,".contains("," + request.getRemoteAddr() + ",");
    }
    
    
    private void setDefaultPermission(MCRObjectID objID, String workflowProcessType, String userid){
        MCRConfiguration config = MCRConfiguration.instance();
        String[] defaultPermissionTypes = config
                .getString("MCR.WorkflowEngine.DefaultPermissionTypes", 
                        "read,commitdb,writedb,deletedb,deletewf").split(",");
        
        for (int i = 0; i < defaultPermissionTypes.length; i++) {
            String propName = new StringBuffer(
                    "MCR.WorkflowEngine.defaultACL.").append(
                    objID.getTypeId()).append(".").append(
                    defaultPermissionTypes[i]).append(".").append(
                    workflowProcessType).toString();

            String strRule = config
                    .getString(propName,
                            "<condition format=\"xml\"><boolean operator=\"false\" /></condition>");
            strRule = strRule.replaceAll("\\$\\{user\\}", userid);
            try{
                Element rule = (Element) MCRXMLParserFactory.getParser(false).parseXML(new MCRStringContent(strRule))
                        .getRootElement().detach();
                String permissionType = defaultPermissionTypes[i];
              //  MCRAccessManager.getAccessImpl().createRule(rule,  "System", "");
                if (MCRAccessManager.hasRule(objID.toString(), permissionType)) {
                    MCRAccessManager.updateRule(objID.toString(), permissionType, rule, "");
                } else {
                    MCRAccessManager.addRule(objID.toString(), permissionType, rule, "");
                }
            } catch(SAXParseException spe){
                Logger.getLogger(this.getClass()).error("SAXParseException: ", spe);
            }
        }
    }
    /*
    MyCoRe-Objekte: /upload/objects
    MyCoRe-Derivate /uploads/id/rosdok_document_123/derivate
    Dateien /uploads/id/rosdok_document_123/derivate/rosdok_derivate_12346/file
        as multipart_formdata name="" content=""
    
    */
}
