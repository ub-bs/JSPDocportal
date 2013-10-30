package org.mycore.frontend.restapi.v1.utils;

import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.jdom2.Comment;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.mycore.common.MCRConstants;
import org.mycore.common.MCRException;
import org.mycore.common.MCRSession;
import org.mycore.datamodel.common.MCRObjectIDDate;
import org.mycore.datamodel.common.MCRXMLMetadataManager;
import org.mycore.datamodel.ifs.MCRDirectory;
import org.mycore.datamodel.ifs.MCRFile;
import org.mycore.datamodel.ifs.MCRFilesystemNode;
import org.mycore.datamodel.metadata.MCRDerivate;
import org.mycore.datamodel.metadata.MCRMetadataManager;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.restapi.v1.MCRRestAPIObjects;
import org.mycore.frontend.restapi.v1.errors.MCRRestAPIError;
import org.mycore.frontend.restapi.v1.errors.MCRRestAPIFieldError;
import org.mycore.frontend.servlets.MCRServlet;

import com.google.gson.stream.JsonWriter;

public class MCRRestAPIObjectsHelper {
    private static SimpleDateFormat SDF_UTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public static Response showMCRObject(String pathParamId, String queryParamStyle, HttpServletRequest request) {
        String idString = pathParamId;
        String key = "mcr"; // the default value for the key
        if (idString.contains(":")) {
            int pos = idString.indexOf(":");
            key = idString.substring(0, pos);
            idString = idString.substring(pos + 1);
            if (!key.equals("mcr")) {
                return MCRRestAPIError.create(Response.Status.BAD_REQUEST, "The ID is not valid.",
                        "The prefix is unkown. Only 'mcr' is allowed.").createHttpResponse();
            }
        }
        if (key.equals("mcr")) {

            MCRObjectID mcrID = null;
            try {
                mcrID = MCRObjectID.getInstance(idString);
            } catch (Exception e) {
                return MCRRestAPIError.create(
                        Response.Status.BAD_REQUEST,
                        "The MyCoRe ID '" + idString
                                + "' is not valid. Did you use the proper format: '{project}_{type}_{number}'?",
                        e.getMessage()).createHttpResponse();
            }

            if (MCRMetadataManager.exists(mcrID)) {
                MCRObject mcrO = MCRMetadataManager.retrieveMCRObject(mcrID);
                Document doc = mcrO.createXML();
                Element eStructure = doc.getRootElement().getChild("structure");
                if (queryParamStyle != null && !MCRRestAPIObjects.STYLE_DERIVATEDETAILS.equals(queryParamStyle)) {
                    MCRRestAPIError error = new MCRRestAPIError(Response.Status.BAD_REQUEST,
                            "The value of parameter {style} is not allowed.",
                            "Allowed values for {style} parameter are: " + MCRRestAPIObjects.STYLE_DERIVATEDETAILS);
                    return error.createHttpResponse();
                }
                if (MCRRestAPIObjects.STYLE_DERIVATEDETAILS.equals(queryParamStyle) && eStructure != null) {
                    Element eDerObjects = eStructure.getChild("derobjects");
                    if (eDerObjects != null) {
                        MCRSession session = MCRServlet.getSession(request);
                        session.beginTransaction();
                        for (Element eDer : (List<Element>) eDerObjects.getChildren("derobject")) {
                            String derID = eDer.getAttributeValue("href", MCRConstants.XLINK_NAMESPACE);
                            try {
                                MCRDerivate der = MCRMetadataManager
                                        .retrieveMCRDerivate(MCRObjectID.getInstance(derID));
                                eDer.addContent(der.createXML().getRootElement().detach());

                                //<mycorederivate xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xlink="http://www.w3.org/1999/xlink" xsi:noNamespaceSchemaLocation="datamodel-derivate.xsd" ID="cpr_derivate_00003760" label="display_image" version="1.3">
                                //  <derivate display="true">

                                eDer = eDer.getChild("mycorederivate").getChild("derivate");
                                eDer.addContent(listDerivateContent(derID));
                            } catch (MCRException e) {
                                eDer.addContent(new Comment("Error: Derivate not found."));
                            }
                        }
                        session.commitTransaction();
                    }
                }

                StringWriter sw = new StringWriter();
                XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
                try {
                    outputter.output(doc, sw);
                } catch (IOException e) {
                    return MCRRestAPIError.create(Response.Status.INTERNAL_SERVER_ERROR,
                            "Unable to retrieve MyCoRe object", e.getMessage()).createHttpResponse();
                }
                return Response.ok(sw.toString()).type("application/xml").build();
            } else {
                //id does not exist
                return MCRRestAPIError.create(Response.Status.NOT_FOUND,
                        "There is no object with the given MyCoRe ID '" + idString + "'.", null).createHttpResponse();

            }
        }

        return MCRRestAPIError.create(Response.Status.INTERNAL_SERVER_ERROR, "Unexepected program flow termination.",
                "Please contact a developer!").createHttpResponse();
    }

    private static Element listDerivateContent(String derID) {
        Element eContents = new Element("contents");

        MCRFilesystemNode root = MCRFilesystemNode.getRootNode(derID);
        if (root != null) {
            eContents.setAttribute("ID", root.getID());
            eContents.setAttribute("ownerID", root.getOwnerID());
            eContents.setAttribute("path", root.getPath());
            eContents.setAttribute("size", String.valueOf(root.getSize()));
            eContents.setAttribute("lastModified", SDF_UTC.format(root.getLastModified().getTime()));
            String label = root.getLabel();
            if (label != null) {
                eContents.setAttribute("label", label);
            }

            if (root instanceof MCRDirectory) {
                MCRDirectory dir = (MCRDirectory) root;
                eContents.setAttribute("total_directories",
                        String.valueOf(dir.getNumChildren(MCRDirectory.DIRECTORIES, MCRDirectory.TOTAL)));
                eContents.setAttribute("total_files",
                        String.valueOf(dir.getNumChildren(MCRDirectory.FILES, MCRDirectory.TOTAL)));
                listDirectoryContent(eContents, dir);
            }
        }
        return eContents;
    }

    private static void listDirectoryContent(Element current, MCRDirectory dir) {
        for (MCRFilesystemNode element : dir.getChildren()) {
            Element node = null;
            if (element instanceof MCRFile) {
                node = new Element("file");
            } else {
                node = new Element("directory");
            }
            current.addContent(node);

            node.setAttribute("ID", element.getID());
            node.setAttribute("name", element.getName());
            String label = element.getLabel();
            if (label != null) {
                node.setAttribute("label", label);
            }
            node.setAttribute("size", String.valueOf(element.getSize()));
            node.setAttribute("lastModified", SDF_UTC.format(element.getLastModified().getTime()));

            if (element instanceof MCRFile) {
                MCRFile file = (MCRFile) element;
                node.setAttribute("contentType", file.getContentTypeID());
                node.setAttribute("md5", file.getMD5());
            }
            if (element instanceof MCRDirectory) {
                MCRDirectory subDir = (MCRDirectory) element;
                listDirectoryContent(node, subDir);
            }
        }
    }

    
    /**
     * 
     * @param info
     * @param format
     * @param filter
     * @param sort
     * @return
     * 
     * @see MCRRestAPIObjects.listObjects()
     */
    public static Response listObjects(UriInfo info, String format, String filter, String sort) {
        //analyze sort
        MCRRestAPIError error = MCRRestAPIError.create(Response.Status.BAD_REQUEST, "The syntax of one or more query parameters is wrong.", null);
        String sortField = null;
        String sortOrder = null;
        if (sort != null) {
            String[] data = sort.split(":");
            if (data.length == 2) {
                sortField = data[0].replace("|", "");
                sortOrder = data[1].toLowerCase().replace("|", "");
                if(!"|ID|lastModified|".contains(sortField)){
                    error.addFieldError(MCRRestAPIFieldError.create("sort", "Allowed values for sortField are 'ID' and 'lastModified'."));
                }
                if(!"|asc|desc|".contains(sortOrder)){
                    error.addFieldError(MCRRestAPIFieldError.create("sort", "Allowed values for sortOrder are 'asc' and 'desc'."));
                }
            }
            else{
                error.addFieldError(MCRRestAPIFieldError.create("sort", "The syntax should be [sortField]:[sortOrder]."));
            }
        }
        
        //analyze format
        
        if(format.equals(MCRRestAPIObjects.FORMAT_JSON) || format.equals(MCRRestAPIObjects.FORMAT_XML)){
            //ok
        }
        else{
            error.addFieldError(MCRRestAPIFieldError.create("format", "Allowed values for format are 'json' or 'xml'."));
        }

        //analyze filter
        List<String> projectIDs = new ArrayList<String>();
        List<String> typeIDs = new ArrayList<String>();
        String lastModifiedBefore = null;
        String lastModifiedAfter = null;
        if (filter != null) {
            for (String s : filter.split(";")) {
                if (s.startsWith("project:")) {
                    projectIDs.add(s.substring(8));
                    continue;
                }
                if (s.startsWith("type:")) {
                    typeIDs.add(s.substring(5));
                    continue;
                }
                if (s.startsWith("lastModifiedBefore:")) {
                    if(!validateDateInput(s.substring(19))){
                        error.addFieldError(MCRRestAPIFieldError.create("filter", "The value of lastModifiedBefore could not be parsed. Please use UTC syntax: yyyy-MM-dd'T'HH:mm:ss'Z'."));
                        continue;
                    }
                    if(lastModifiedBefore==null){
                        lastModifiedBefore = s.substring(19);
                    }
                    else if(s.substring(19).compareTo(lastModifiedBefore)<0){
                            lastModifiedBefore = s.substring(19);
                    }
                    continue;
                }
                
                if (s.startsWith("lastModifiedAfter:")) {
                    if(!validateDateInput(s.substring(18))){
                        error.addFieldError(MCRRestAPIFieldError.create("filter", "The value of lastModifiedAfter could not be parsed. Please use UTC syntax: yyyy-MM-dd'T'HH:mm:ss'Z'."));
                        continue;
                    }
                    if(lastModifiedAfter == null){
                        lastModifiedAfter = s.substring(18);         
                    }
                    else if(s.substring(18).compareTo(lastModifiedAfter)>0){
                        lastModifiedAfter = s.substring(18);
                    }
                    continue;
                }
                
                error.addFieldError(MCRRestAPIFieldError.create("filter", "The syntax of the filter '"+s+"'could not be parsed. The syntax should be [filterName]:[value]. Allowed filterNames are 'project', 'type', 'modifiedBefore' and 'modifiedAfter'."));
            }
        }
        
        if(error.getFieldErrors().size()>0){
            return error.createHttpResponse();
        }
        
        
        //Parameters are checked - continue tor retrieve data
        
        //retrieve MCRIDs by Type and Project ID
        Set<String> mcrIDs = new HashSet<String>();
        if (projectIDs.isEmpty()) {
            if (typeIDs.isEmpty()) {
                for (String id : MCRXMLMetadataManager.instance().listIDs()) {
                    if (!id.contains("_derivate_")) {
                        mcrIDs.add(id);
                    }
                }
            } else {
                for (String t : typeIDs) {
                    mcrIDs.addAll(MCRXMLMetadataManager.instance().listIDsOfType(t));
                }
            }
        } else {

            if (typeIDs.isEmpty()) {
                for (String id : MCRXMLMetadataManager.instance().listIDs()) {
                    String[] split = id.split("_");
                    if (!split[1].equals("derivate") && projectIDs.contains(split[0])) {
                        mcrIDs.add(id);
                    }
                }
            } else {
                for (String p : projectIDs) {
                    for (String t : typeIDs) {
                        mcrIDs.addAll(MCRXMLMetadataManager.instance().listIDsForBase(p + "_" + t));
                    }
                }
            }
        }
        
        //Filter by modifiedBefore and modifiedAfter
        List<String> l = new ArrayList<String>();
        l.addAll(mcrIDs);
        List<MCRObjectIDDate> objIdDates = new ArrayList<MCRObjectIDDate>();
        try {
            objIdDates = MCRXMLMetadataManager.instance().retrieveObjectDates(l);
        } catch (IOException e) {
            //TODO
        }
        if (lastModifiedAfter != null || lastModifiedBefore != null) {
            List<MCRObjectIDDate> testObjIdDates = objIdDates;
            objIdDates = new ArrayList<MCRObjectIDDate>();
            for (MCRObjectIDDate oid : testObjIdDates) {
                String test = SDF_UTC.format(oid.getLastModified());
                if (lastModifiedAfter != null && test.compareTo(lastModifiedAfter) < 0)
                    continue;
                if (lastModifiedBefore != null
                        && lastModifiedBefore.compareTo(test.substring(0, lastModifiedBefore.length())) < 0)
                    continue;
                objIdDates.add(oid);
            }
        }
        
        //sort if necessary
        if (sortOrder != null && sortField != null) {
            Collections.sort(objIdDates, new MCRRestAPISortFieldComparator(sortField, sortOrder));
        }
        
        //output as XML
        if (MCRRestAPIObjects.FORMAT_XML.equals(format)) {
            Element eMcrobjects = new Element("mycoreobjects");
            Document docOut = new Document(eMcrobjects);
            eMcrobjects.setAttribute("numFound", Integer.toString(objIdDates.size()));
            for (MCRObjectIDDate oid : objIdDates) {
                Element eMcrObject = new Element("mycoreobject");
                eMcrObject.setAttribute("ID", oid.getId());
                eMcrObject.setAttribute("lastModified", SDF_UTC.format(oid.getLastModified()));
                eMcrObject.setAttribute("href", info.getAbsolutePathBuilder().path(oid.getId()).build((Object[]) null)
                        .toString());

                eMcrobjects.addContent(eMcrObject);
            }
            try {
                StringWriter sw = new StringWriter();
                XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
                xout.output(docOut, sw);
                return Response.ok(sw.toString()).type("application/xml; charset=UTF-8").build();
            } catch (IOException e) {
                return MCRRestAPIError.create(Response.Status.INTERNAL_SERVER_ERROR, "A problem occurred while fetching the data", e.getMessage()).createHttpResponse();
            }
        }

        //output as JSON
        if (MCRRestAPIObjects.FORMAT_JSON.equals(format)) {
            StringWriter sw = new StringWriter();
            try {
                JsonWriter writer = new JsonWriter(sw);
                writer.setIndent("    ");
                writer.beginObject();
                writer.name("numFound").value(objIdDates.size());
                writer.name("mycoreobjects");
                writer.beginArray();
                for (MCRObjectIDDate oid : objIdDates) {
                    writer.beginObject();
                    writer.name("ID").value(oid.getId());
                    writer.name("lastModified").value(SDF_UTC.format(oid.getLastModified()));
                    writer.name("href").value(
                            info.getAbsolutePathBuilder().path(oid.getId()).build((Object[]) null).toString());
                    writer.endObject();
                }
                writer.endArray();
                writer.endObject();

                writer.close();

                return Response.ok(sw.toString()).type("application/json; charset=UTF-8").build();
            } catch (IOException e) {
                return MCRRestAPIError.create(Response.Status.INTERNAL_SERVER_ERROR, "A problem occurred while fetching the data", e.getMessage()).createHttpResponse();
            }
        }
        return MCRRestAPIError.create(Response.Status.INTERNAL_SERVER_ERROR, "A problem in programm flow", null).createHttpResponse();
    }
    
    
    /**
     * validates the given String if it matches the UTC syntax or the beginning of it
     * @param test
     * @return true, if it is valid
     */
    private static boolean validateDateInput(String test){
        String base = "0000-00-00T00:00:00Z";
        if(test.length()>base.length()) return false;
        test = test + base.substring(test.length());
        try{
            SDF_UTC.parse(test);
        }
        catch(ParseException e){
            return false;
        }
        return true;
    }
}
