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
package org.mycore.frontend.restapi.v1.upload;

import static org.mycore.access.MCRAccessManager.PERMISSION_WRITE;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
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
import org.mycore.common.MCRPersistenceException;
import org.mycore.common.MCRSession;
import org.mycore.common.MCRSessionMgr;
import org.mycore.common.MCRUserInformation;
import org.mycore.common.MCRUtils;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.common.content.MCRStringContent;
import org.mycore.common.xml.MCRXMLParserFactory;
import org.mycore.datamodel.common.MCRXMLMetadataManager;
import org.mycore.datamodel.ifs.MCRDirectory;
import org.mycore.datamodel.ifs.MCRFileImportExport;
import org.mycore.datamodel.ifs.MCRFilesystemNode;
import org.mycore.datamodel.metadata.MCRDerivate;
import org.mycore.datamodel.metadata.MCRMetaIFS;
import org.mycore.datamodel.metadata.MCRMetaLinkID;
import org.mycore.datamodel.metadata.MCRMetadataManager;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.datamodel.niofs.MCRPath;
import org.mycore.frontend.cli.MCRObjectCommands;
import org.mycore.frontend.restapi.v1.utils.MCREncryptionHelper;
import org.mycore.frontend.restapi.v1.utils.MCRRestAPIObjectsHelper;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManager;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManagerFactory;
import org.mycore.frontend.workflowengine.strategies.MCRDerivateStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRWorkflowDirectoryManager;
import org.mycore.user2.MCRUserManager;
import org.xml.sax.SAXParseException;

import com.sun.jersey.core.header.FormDataContentDisposition;

public class MCRRestAPIUploadHelper {
    public static final String FORMAT_XML = "xml";

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

    /**
     
     @Path("/objects/")
     @Produces({ MediaType.TEXT_XML + ";charset=UTF-8" })
     @Consumes(MediaType.MULTIPART_FORM_DATA)
     */
    public static Response uploadObject(UriInfo info, HttpServletRequest request, InputStream uploadedInputStream,
            FormDataContentDisposition fileDetails) {

        if (checkAccess(request)) {
            MCRSession session = MCRServlet.getSession(request);
            session.beginTransaction();
            File fXML = null;
            try {
                SAXBuilder sb = new SAXBuilder();
                Document docOut = sb.build(uploadedInputStream);

                MCRObjectID mcrID = MCRObjectID.getInstance(docOut.getRootElement().getAttributeValue("ID"));
                MCRWorkflowManager wfm = MCRWorkflowManagerFactory.getImpl(mcrID);
                if (wfm != null) {
                    String saveDirectory = MCRWorkflowDirectoryManager.getWorkflowDirectory(wfm.getMainDocumentType());

                    boolean isUpdate = MCRXMLMetadataManager.instance().exists(mcrID);
                    if (!isUpdate) {
                        mcrID = wfm.getNextFreeID(mcrID.getTypeId());
                    }

                    fXML = new File(new File(saveDirectory), mcrID.toString() + ".xml");

                    docOut.getRootElement().setAttribute("ID", mcrID.toString());
                    docOut.getRootElement().setAttribute("label", mcrID.toString());
                    XMLOutputter xmlOut = new XMLOutputter(Format.getPrettyFormat());
                    try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fXML),
                            "UTF-8"))) {
                        xmlOut.output(docOut, bw);
                    }

                    MCRSession mcrSession = MCRSessionMgr.getCurrentSession();
                    MCRUserInformation currentUser = mcrSession.getUserInformation();
                    mcrSession.setUserInformation(MCRUserManager.getUser("api"));
                    MCRObjectCommands.updateFromFile(fXML.getPath(), false); // handles "create" as well
                    setDefaultPermission(mcrID, wfm.getWorkflowProcessType(), "api");
                    mcrSession.setUserInformation(currentUser);

                    return Response.created(info.getBaseUriBuilder().path("v1/objects/" + mcrID.toString()).build())
                            .type("application/xml; charset=UTF-8").build();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                session.commitTransaction();
                if (fXML != null) {
                    fXML.delete();
                }
            }

        }
        return Response.status(Status.FORBIDDEN).build();

    }

    /**
    @POST
    @Path("/objects/id/{mcrObjID}/derivates")
    @Produces({ MediaType.TEXT_XML + ";charset=UTF-8" })
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    */
    public static Response uploadDerivate(UriInfo info, HttpServletRequest request, String pathParamMcrObjID,
            String formParamlabel) {

        Response response = Response.status(Status.FORBIDDEN).build();
        if (checkAccess(request)) {
            MCRSession session = MCRServlet.getSession(request);
            session.beginTransaction();
            File fXML = null;

            try {
                MCRObjectID mcrObjID = MCRObjectID.getInstance(pathParamMcrObjID);
                MCRObject mcrObj = MCRMetadataManager.retrieveMCRObject(mcrObjID);
                MCRObjectID derID = null;
                for (MCRMetaLinkID derLink : mcrObj.getStructure().getDerivates()) {
                    if (formParamlabel.equals(derLink.getXLinkLabel())
                            || formParamlabel.equals(derLink.getXLinkTitle())) {
                        derID = derLink.getXLinkHrefID();
                    }
                }

                if (derID == null) {
                    MCRWorkflowManager wfm = MCRWorkflowManagerFactory.getImpl(mcrObjID);
                    if (wfm != null) {
                        File saveDir = new File(MCRWorkflowDirectoryManager.getWorkflowDirectory(wfm
                                .getMainDocumentType()));

                        derID = MCRDerivateStrategy.setNextFreeDerivateID(saveDir);

                        MCRDerivate mcrDerivate = new MCRDerivate();
                        mcrDerivate.setLabel(formParamlabel);
                        mcrDerivate.setId(derID);
                        mcrDerivate.setSchema("datamodel-derivate.xsd");
                        mcrDerivate.getDerivate().setLinkMeta(new MCRMetaLinkID("linkmeta", mcrObjID, null, null));
                        mcrDerivate.getDerivate().setInternals(
                                new MCRMetaIFS("internal", new File(saveDir, derID.toString()).getPath()));

                        MCRMetadataManager.create(mcrDerivate);
                        MCRMetadataManager.addOrUpdateDerivateToObject(mcrObjID, new MCRMetaLinkID("derobject", derID, null, formParamlabel));

                        fXML = new File(saveDir, derID.toString() + ".xml");

                        session.commitTransaction();
                        session.beginTransaction();
                        setDefaultPermission(derID, wfm.getWorkflowProcessType(), "api");
                    }
                }
                response = Response
                        .created(
                                info.getBaseUriBuilder()
                                        .path("v1/objects/" + mcrObjID.toString() + "/derivates/" + derID.toString())
                                        .build()).type("application/xml; charset=UTF-8").build();

            } catch (Exception e) {
                Logger.getLogger(MCRRestAPIUploadHelper.class).error("Exeption while uploading derivate", e);
            }

            session.commitTransaction();
            if (fXML != null) {
                fXML.delete();
            }

        }
        return response;

    }

    /**
    @POST
    @Path("/objects/id/{mcrObjID}/derivates/id/{mcrDerID}/files")
    @Produces({ MediaType.TEXT_XML + ";charset=UTF-8" })
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    */
    public static Response uploadFile(UriInfo info, HttpServletRequest request, String pathParamMcrObjID,
            String pathParamMcrDerID, InputStream uploadedInputStream, FormDataContentDisposition fileDetails,
            String formParamPath, boolean formParamMaindoc, String formParamMD5,
            Long formParamSize) {

        Response response = Response.status(Status.FORBIDDEN).build();
        if (checkAccess(request)) {

            SortedMap<String, String> parameter = new TreeMap<>();
            
            parameter.put("mcrObjectID", pathParamMcrObjID);
            parameter.put("mcrDerivateID", pathParamMcrDerID);
            parameter.put("path", formParamPath);
            parameter.put("maindoc", Boolean.toString(formParamMaindoc));
            parameter.put("md5", formParamMD5);
            parameter.put("size", Long.toString(formParamSize));

            
            String clientID = request.getHeader("X-MyCoRe-RestAPI-ClientID");;
            String keyFileLocation = MCRConfiguration.instance().getString(
                    "MCR.RestAPI.v1.Client." + clientID + ".PublicKeyFile");
            if (keyFileLocation == null) {
                //ToDo error
            }
            String base64Signature = request.getHeader("X-MyCoRe-RestAPI-Signature");
            if (base64Signature == null) {
                //ToDo error handling
            }
            if (!MCREncryptionHelper.verifyPropertiesWithSignature(parameter, base64Signature,
                    new File(keyFileLocation))) {
                //validation failed -> error handling

            } else {

                //MCRSession session = MCRServlet.getSession(request);
                MCRSession session = MCRSessionMgr.getCurrentSession();
                MCRUserInformation currentUser = session.getUserInformation();

                session.beginTransaction();
                session.setUserInformation(MCRUserManager.getUser("api"));
                MCRObjectID objID = MCRObjectID.getInstance(pathParamMcrObjID);
                MCRObjectID derID = MCRObjectID.getInstance(pathParamMcrDerID);

                //MCRAccessManager.checkPermission(uses CACHE, which seems to be dirty from other calls and cannot be deleted)????
                if (MCRAccessManager.getAccessImpl().checkPermission(derID.toString(), PERMISSION_WRITE)) {

                    MCRDerivate der = MCRMetadataManager.retrieveMCRDerivate(derID);

                    File derDir = null;
                    File saveFile = null;
                    String path = null;
                    if (der.getOwnerID().equals(objID)) {

                        MCRWorkflowManager wfm = MCRWorkflowManagerFactory.getImpl(objID);
                        if (wfm != null) {
                            File saveDir = new File(MCRWorkflowDirectoryManager.getWorkflowDirectory(wfm
                                    .getMainDocumentType()));
                            try {

                                derDir = new File(saveDir, derID.toString());
                                MCRUtils.deleteDirectory(derDir);
                                path = formParamPath.replace("\\", "/").replace("../", "");
                                saveFile = new File(derDir, path);

                                saveFile.getParentFile().mkdirs();
                                Files.copy(uploadedInputStream, saveFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                                MCRDirectory difs = MCRDirectory.getRootDirectory(derID.toString());
                                if (difs == null) {
                                    difs = new MCRDirectory(derID.toString());
                                }

                                //delete old file
                                MCRFilesystemNode fsn = difs.getChildByPath(path);
                                if (fsn != null) {
                                    try {
                                        fsn.delete();
                                    } catch (MCRPersistenceException pe) {
                                        Logger.getLogger(MCRRestAPIUploadHelper.class).error(pe);
                                    }
                                }
                                MCRFileImportExport.importFiles(derDir, difs);
                                der.getDerivate().getInternals().setIFSID(difs.getID());
                                der.getDerivate().getInternals().setSourcePath(derDir.getPath());

                                if (formParamMaindoc) {
                                    der.getDerivate().getInternals().setMainDoc(path);
                                }

                                MCRMetadataManager.update(der);
                                MCRUtils.deleteDirectory(derDir);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    session.commitTransaction();
                    session.setUserInformation(currentUser);
                    response = Response
                            .created(
                                    info.getBaseUriBuilder()
                                            .path("v1/objects/" + objID.toString() + "/derivates/" + derID.toString()
                                                    + "/files").build()).type("application/xml; charset=UTF-8").build();

                }
            }
        }
        return response;

    }

    /**
    @DELETE
    @Path("/objects/id/{mcrObjID}/derivates/id/{mcrDerID}/files")
    @Produces({ MediaType.TEXT_XML + ";charset=UTF-8" })
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    */
    public static Response deleteAllFiles(UriInfo info, HttpServletRequest request, String pathParamMcrObjID,
            String pathParamMcrDerID) {

        Response response = Response.status(Status.FORBIDDEN).build();
        if (checkAccess(request)) {

            SortedMap<String, String> parameter = new TreeMap<>();
            parameter.put("mcrObjectID", pathParamMcrObjID);
            parameter.put("mcrDerivateID", pathParamMcrDerID);

            String clientID = request.getHeader("X-MyCoRe-RestAPI-ClientID");
            String keyFileLocation = MCRConfiguration.instance().getString(
                    "MCR.RestAPI.v1.Client." + clientID + ".PublicKeyFile");
            if (keyFileLocation == null) {
                //ToDo error
            }
            String base64Signature = request.getHeader("X-MyCoRe-RestAPI-Signature");
            if (base64Signature == null) {
                //ToDo error handling
            }
            if (!MCREncryptionHelper.verifyPropertiesWithSignature(parameter, base64Signature,
                    new File(keyFileLocation))) {
                //validation failed -> error handling

            } else {

                //MCRSession session = MCRServlet.getSession(request);
                MCRSession session = MCRSessionMgr.getCurrentSession();
                MCRUserInformation currentUser = session.getUserInformation();

                session.beginTransaction();
                session.setUserInformation(MCRUserManager.getUser("api"));
                MCRObjectID objID = MCRObjectID.getInstance(pathParamMcrObjID);
                MCRObjectID derID = MCRObjectID.getInstance(pathParamMcrDerID);

                //MCRAccessManager.checkPermission(uses CACHE, which seems to be dirty from other calls and cannot be deleted)????
				if (MCRAccessManager.getAccessImpl().checkPermission(derID.toString(), PERMISSION_WRITE)) {
					MCRDerivate der = MCRMetadataManager.retrieveMCRDerivate(derID);

					final MCRPath rootPath = MCRPath.getPath(der.getId().toString(), "/");
					try {
						Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
							@Override
							public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
								Files.delete(file);
								return FileVisitResult.CONTINUE;
							}

							@Override
							public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
								if (exc == null && !rootPath.equals(dir)) {
									Files.delete(dir);
									return FileVisitResult.CONTINUE;
								} else {
									throw exc;
								}
							}

						});
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

                session.commitTransaction();
                session.setUserInformation(currentUser);
                response = Response
                        .created(
                                info.getBaseUriBuilder()
                                        .path("v1/objects/" + objID.toString() + "/derivates/" + derID.toString()
                                                + "/files").build()).type("application/xml; charset=UTF-8").build();
            }
        }
        return response;
    }

    private static boolean checkAccess(HttpServletRequest request) {
        return ",127.0.0.1,::1,".contains("," + request.getRemoteAddr() + ",");
    }

    private static void setDefaultPermission(MCRObjectID objID, String workflowProcessType, String userid) {
        MCRConfiguration config = MCRConfiguration.instance();
        String[] defaultPermissionTypes = config.getString("MCR.WorkflowEngine.DefaultPermissionTypes",
                "read,commitdb,writedb,deletedb,deletewf").split(",");

        for (int i = 0; i < defaultPermissionTypes.length; i++) {
            String propName = new StringBuffer("MCR.WorkflowEngine.defaultACL.").append(objID.getTypeId()).append(".")
                    .append(defaultPermissionTypes[i]).append(".").append(workflowProcessType).toString();

            String strRule = config.getString(propName,
                    "<condition format=\"xml\"><boolean operator=\"false\" /></condition>");
            strRule = strRule.replaceAll("\\$\\{user\\}", userid);
            try {
                Element rule = (Element) MCRXMLParserFactory.getParser(false).parseXML(new MCRStringContent(strRule))
                        .getRootElement().detach();
                String permissionType = defaultPermissionTypes[i];
                //  MCRAccessManager.getAccessImpl().createRule(rule,  "System", "");
                if (MCRAccessManager.hasRule(objID.toString(), permissionType)) {
                    MCRAccessManager.updateRule(objID.toString(), permissionType, rule, "");
                } else {
                    MCRAccessManager.addRule(objID.toString(), permissionType, rule, "");
                }
            } catch (SAXParseException spe) {
                Logger.getLogger(MCRRestAPIObjectsHelper.class).error("SAXParseException: ", spe);
            }
        }

        //set read to true;
        if (MCRAccessManager.hasRule(objID.toString(), MCRAccessManager.PERMISSION_READ)) {
            MCRAccessManager.updateRule(objID.toString(), MCRAccessManager.PERMISSION_READ,
                    MCRAccessManager.getTrueRule(), "");
        } else {
            MCRAccessManager.addRule(objID.toString(), MCRAccessManager.PERMISSION_READ,
                    MCRAccessManager.getTrueRule(), "");
        }

    }
}
