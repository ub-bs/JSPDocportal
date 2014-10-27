package org.mycore.frontend.restapi.v1.utils;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
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
import org.mycore.common.config.MCRConfiguration;
import org.mycore.datamodel.common.MCRObjectIDDate;
import org.mycore.datamodel.common.MCRXMLMetadataManager;
import org.mycore.datamodel.ifs.MCRDirectory;
import org.mycore.datamodel.ifs.MCRFile;
import org.mycore.datamodel.ifs.MCRFilesystemNode;
import org.mycore.datamodel.metadata.MCRDerivate;
import org.mycore.datamodel.metadata.MCRMetaLinkID;
import org.mycore.datamodel.metadata.MCRMetadataManager;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.MCRFrontendUtil;
import org.mycore.frontend.restapi.v1.MCRRestAPIObjects;
import org.mycore.frontend.restapi.v1.errors.MCRRestAPIError;
import org.mycore.frontend.restapi.v1.errors.MCRRestAPIException;
import org.mycore.frontend.restapi.v1.errors.MCRRestAPIFieldError;
import org.mycore.frontend.restapi.v1.utils.MCRRestAPISortObject.SortOrder;
import org.mycore.frontend.servlets.MCRServlet;

import com.google.gson.stream.JsonWriter;

public class MCRRestAPIObjectsHelper {
    private static SimpleDateFormat SDF_UTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);

    public static Response showMCRObject(String pathParamId, String queryParamStyle, HttpServletRequest request) {
        try {
            MCRObject mcrO = retrieveMCRObject(pathParamId);
            Document doc = mcrO.createXML();
            Element eStructure = doc.getRootElement().getChild("structure");
            if (queryParamStyle != null && !MCRRestAPIObjects.STYLE_DERIVATEDETAILS.equals(queryParamStyle)) {
                throw new MCRRestAPIException(MCRRestAPIError.create(Response.Status.BAD_REQUEST,
                        "The value of parameter {style} is not allowed.", "Allowed values for {style} parameter are: "
                                + MCRRestAPIObjects.STYLE_DERIVATEDETAILS));
            }

            if (MCRRestAPIObjects.STYLE_DERIVATEDETAILS.equals(queryParamStyle) && eStructure != null) {
                Element eDerObjects = eStructure.getChild("derobjects");
                if (eDerObjects != null) {
                    MCRSession session = MCRServlet.getSession(request);
                    session.beginTransaction();
                    for (Element eDer : (List<Element>) eDerObjects.getChildren("derobject")) {
                        String derID = eDer.getAttributeValue("href", MCRConstants.XLINK_NAMESPACE);
                        try {
                            MCRDerivate der = MCRMetadataManager.retrieveMCRDerivate(MCRObjectID.getInstance(derID));
                            eDer.addContent(der.createXML().getRootElement().detach());

                            //<mycorederivate xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xlink="http://www.w3.org/1999/xlink" xsi:noNamespaceSchemaLocation="datamodel-derivate.xsd" ID="cpr_derivate_00003760" label="display_image" version="1.3">
                            //  <derivate display="true">

                            eDer = eDer.getChild("mycorederivate").getChild("derivate");
                            eDer.addContent(listDerivateContent(MCRMetadataManager.retrieveMCRDerivate(MCRObjectID.getInstance(derID)), request));
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
                throw new MCRRestAPIException(MCRRestAPIError.create(Response.Status.INTERNAL_SERVER_ERROR,
                        "Unable to retrieve MyCoRe object", e.getMessage()));
            }
            return Response.ok(sw.toString()).type("application/xml").build();
        }

        catch (MCRRestAPIException rae) {
            return rae.getError().createHttpResponse();
        }

    }

    public static Response showMCRDerivate(String pathParamMcrID, String pathParamDerID, HttpServletRequest request) {
        MCRSession session = MCRServlet.getSession(request);
        session.beginTransaction();
        try {
            MCRObject mcrObj = retrieveMCRObject(pathParamMcrID);
            MCRDerivate derObj = retrieveMCRDerivate(mcrObj, pathParamDerID);

            Document doc = derObj.createXML();
            doc.getRootElement().addContent(listDerivateContent(derObj, request));

            StringWriter sw = new StringWriter();
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            try {
                outputter.output(doc, sw);
            } catch (IOException e) {
                throw new MCRRestAPIException(MCRRestAPIError.create(Response.Status.INTERNAL_SERVER_ERROR,
                        "Unable to display derivate content", e.getMessage()));
            }

            return Response.ok(sw.toString()).type("application/xml").build();

        } catch (MCRRestAPIException e) {
            return e.getError().createHttpResponse();
        } finally {
            session.commitTransaction();
        }

        // return MCRRestAPIError.create(Response.Status.INTERNAL_SERVER_ERROR, "Unexepected program flow termination.",
        //       "Please contact a developer!").createHttpResponse();
    }

    private static Element listDerivateContent(MCRDerivate derObj, HttpServletRequest request) {
        Element eContents = new Element("files");
        MCRFilesystemNode root = MCRFilesystemNode.getRootNode(derObj.getId().toString());
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
                 String baseurl = MCRFrontendUtil.getBaseURL()+MCRConfiguration.instance().getString("MCR.RestAPI.v1.Files.baseurl.path", "");
                 if(!baseurl.endsWith("/")){baseurl+="/";}
                listDirectoryContent(eContents, dir, baseurl+derObj.getOwnerID().toString()+"/"+derObj.getId().toString()+"/");
            }
        }
        return eContents;
    }

    private static String listDerivateContentAsJson(MCRDerivate derObj) throws MCRRestAPIException {
        StringWriter sw = new StringWriter();
        try {

            MCRFilesystemNode root = MCRFilesystemNode.getRootNode(derObj.getId().toString());
            if (root != null) {
                JsonWriter writer = new JsonWriter(sw);
                writer.setIndent("    ");
                writer.beginObject();

                writer.name("ID").value(root.getID());
                writer.name("ownerID").value(root.getOwnerID());
                writer.name("path").value(root.getPath());
                writer.name("size").value(String.valueOf(root.getSize()));
                writer.name("lastModified").value(SDF_UTC.format(root.getLastModified().getTime()));
                String label = root.getLabel();
                if (label != null) {
                    writer.name("label").value(label);
                }
                if (root instanceof MCRDirectory) {
                    MCRDirectory dir = (MCRDirectory) root;
                    writer.name("total_directories").value(
                            dir.getNumChildren(MCRDirectory.DIRECTORIES, MCRDirectory.TOTAL));
                    writer.name("total_files").value(dir.getNumChildren(MCRDirectory.FILES, MCRDirectory.TOTAL));
                    String baseurl = MCRFrontendUtil.getBaseURL()+MCRConfiguration.instance().getString("MCR.RestAPI.v1.Files.baseurl.path", "");
                    if(!baseurl.endsWith("/")){baseurl+="/";}
                    listDirectoryContentAsJson(writer, dir, baseurl+derObj.getOwnerID().toString()+"/"+derObj.getId().toString()+"/");
                }
                writer.endObject();

                writer.close();
            }
        } catch (IOException e) {
            throw new MCRRestAPIException(MCRRestAPIError.create(Response.Status.INTERNAL_SERVER_ERROR,
                    "A problem occurred while fetching the data", e.getMessage()));
        }
        return sw.toString();
    }

    private static void listDirectoryContent(Element current, MCRDirectory dir, String filesBaseURL) {
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
                node.setAttribute("href", filesBaseURL+element.getName());
               
            }
            if (element instanceof MCRDirectory) {
                listDirectoryContent(node, (MCRDirectory) element, filesBaseURL+element.getName()+"/");
            }
        }
    }

    private static void listDirectoryContentAsJson(JsonWriter writer, MCRDirectory dir, String baseURL) throws IOException {
        if (dir.getChildren().length > 0) {
            writer.name("files");
            writer.beginArray();

            for (MCRFilesystemNode element : dir.getChildren()) {
                writer.beginObject();
                if (element instanceof MCRFile) {
                    writer.name("node").value("file");
                } else {
                    writer.name("node").value("directory");
                }

                writer.name("ID").value(element.getID());
                writer.name("name").value(element.getName());
                String label = element.getLabel();
                if (label != null) {
                    writer.name("label").value(label);
                }
                writer.name("size").value(element.getSize());
                writer.name("lastModified").value(SDF_UTC.format(element.getLastModified().getTime()));

                if (element instanceof MCRFile) {
                    MCRFile file = (MCRFile) element;
                    writer.name("contentType").value(file.getContentTypeID());
                    writer.name("md5").value(file.getMD5());
                    writer.name("href").value(baseURL+element.getName());
                }
                if (element instanceof MCRDirectory) {
                    listDirectoryContentAsJson(writer, (MCRDirectory) element, baseURL+element.getName()+"/");
                }
                writer.endObject();
            }
            writer.endArray();
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
        MCRRestAPIError error = MCRRestAPIError.create(Response.Status.BAD_REQUEST,
                "The syntax of one or more query parameters is wrong.", null);

        MCRRestAPISortObject sortObj = null;
        try {
            sortObj = createSortObject(sort);
        } catch (MCRRestAPIException rae) {
            for (MCRRestAPIFieldError fe : rae.getError().getFieldErrors()) {
                error.addFieldError(fe);
            }
        }

        //analyze format

        if (format.equals(MCRRestAPIObjects.FORMAT_JSON) || format.equals(MCRRestAPIObjects.FORMAT_XML)) {
            //ok
        } else {
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
                    if (!validateDateInput(s.substring(19))) {
                        error.addFieldError(MCRRestAPIFieldError
                                .create("filter",
                                        "The value of lastModifiedBefore could not be parsed. Please use UTC syntax: yyyy-MM-dd'T'HH:mm:ss'Z'."));
                        continue;
                    }
                    if (lastModifiedBefore == null) {
                        lastModifiedBefore = s.substring(19);
                    } else if (s.substring(19).compareTo(lastModifiedBefore) < 0) {
                        lastModifiedBefore = s.substring(19);
                    }
                    continue;
                }

                if (s.startsWith("lastModifiedAfter:")) {
                    if (!validateDateInput(s.substring(18))) {
                        error.addFieldError(MCRRestAPIFieldError
                                .create("filter",
                                        "The value of lastModifiedAfter could not be parsed. Please use UTC syntax: yyyy-MM-dd'T'HH:mm:ss'Z'."));
                        continue;
                    }
                    if (lastModifiedAfter == null) {
                        lastModifiedAfter = s.substring(18);
                    } else if (s.substring(18).compareTo(lastModifiedAfter) > 0) {
                        lastModifiedAfter = s.substring(18);
                    }
                    continue;
                }

                error.addFieldError(MCRRestAPIFieldError
                        .create("filter",
                                "The syntax of the filter '"
                                        + s
                                        + "'could not be parsed. The syntax should be [filterName]:[value]. Allowed filterNames are 'project', 'type', 'lastModifiedBefore' and 'lastModifiedAfter'."));
            }
        }

        if (error.getFieldErrors().size() > 0) {
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
        if (sortObj != null) {
            Collections.sort(objIdDates, new MCRRestAPISortObjectComparator(sortObj));
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
                return MCRRestAPIError.create(Response.Status.INTERNAL_SERVER_ERROR,
                        "A problem occurred while fetching the data", e.getMessage()).createHttpResponse();
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
                return MCRRestAPIError.create(Response.Status.INTERNAL_SERVER_ERROR,
                        "A problem occurred while fetching the data", e.getMessage()).createHttpResponse();
            }
        }
        return MCRRestAPIError.create(Response.Status.INTERNAL_SERVER_ERROR, "A problem in programm flow", null)
                .createHttpResponse();
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
    public static Response listDerivates(UriInfo info, String mcrIDString, String format, String sort) {
        //analyze sort
        try {
            MCRRestAPIError error = MCRRestAPIError.create(Response.Status.BAD_REQUEST,
                    "The syntax of one or more query parameters is wrong.", null);

            MCRRestAPISortObject sortObj = null;
            try {
                sortObj = createSortObject(sort);
            } catch (MCRRestAPIException rae) {
                for (MCRRestAPIFieldError fe : rae.getError().getFieldErrors()) {
                    error.addFieldError(fe);
                }
            }

            //analyze format

            if (format.equals(MCRRestAPIObjects.FORMAT_JSON) || format.equals(MCRRestAPIObjects.FORMAT_XML)) {
                //ok
            } else {
                error.addFieldError(MCRRestAPIFieldError.create("format",
                        "Allowed values for format are 'json' or 'xml'."));
            }

            if (error.getFieldErrors().size() > 0) {
                throw new MCRRestAPIException(error);
            }

            //Parameters are checked - continue to retrieve data

            MCRObject mcrO = retrieveMCRObject(mcrIDString);
            List<String> l = new ArrayList<String>();
            for (MCRMetaLinkID mcrmetaID : mcrO.getStructure().getDerivates()) {
                if (MCRMetadataManager.exists(mcrmetaID.getXLinkHrefID())) {
                    l.add(mcrmetaID.getXLinkHref());
                }
            }
            List<MCRObjectIDDate> objIdDates = new ArrayList<MCRObjectIDDate>();
            try {
                objIdDates = MCRXMLMetadataManager.instance().retrieveObjectDates(l);
            } catch (IOException e) {
                //TODO
            }

            //sort if necessary
            if (sortObj != null) {
                Collections.sort(objIdDates, new MCRRestAPISortObjectComparator(sortObj));
            }

            //output as XML
            if (MCRRestAPIObjects.FORMAT_XML.equals(format)) {
                Element eDerObjects = new Element("derobjects");
                Document docOut = new Document(eDerObjects);
                eDerObjects.setAttribute("numFound", Integer.toString(objIdDates.size()));
                for (MCRObjectIDDate oid : objIdDates) {
                    Element eDerObject = new Element("derobject");
                    eDerObject.setAttribute("ID", oid.getId());
                    MCRDerivate der = MCRMetadataManager.retrieveMCRDerivate(MCRObjectID.getInstance(oid.getId()));
                    String mcrID = der.getDerivate().getMetaLink().getXLinkHref();
                    eDerObject.setAttribute("metadata", mcrID);
                    if (der.getLabel() != null) {
                        eDerObject.setAttribute("label", der.getLabel());
                    }
                    eDerObject.setAttribute("lastModified", SDF_UTC.format(oid.getLastModified()));
                    eDerObject.setAttribute("href",
                            info.getAbsolutePathBuilder().path(oid.getId()).build((Object[]) null).toString());

                    eDerObjects.addContent(eDerObject);
                }
                try {
                    StringWriter sw = new StringWriter();
                    XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
                    xout.output(docOut, sw);
                    return Response.ok(sw.toString()).type("application/xml; charset=UTF-8").build();
                } catch (IOException e) {
                    return MCRRestAPIError.create(Response.Status.INTERNAL_SERVER_ERROR,
                            "A problem occurred while fetching the data", e.getMessage()).createHttpResponse();
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
                        MCRDerivate der = MCRMetadataManager.retrieveMCRDerivate(MCRObjectID.getInstance(oid.getId()));
                        String mcrID = der.getDerivate().getMetaLink().getXLinkHref();
                        writer.name("metadata").value(mcrID);
                        if (der.getLabel() != null) {
                            writer.name("label").value(der.getLabel());
                        }
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
                    throw new MCRRestAPIException(MCRRestAPIError.create(Response.Status.INTERNAL_SERVER_ERROR,
                            "A problem occurred while fetching the data", e.getMessage()));
                }
            }
        } catch (MCRRestAPIException rae) {
            return rae.getError().createHttpResponse();
        }

        return MCRRestAPIError.create(Response.Status.INTERNAL_SERVER_ERROR, "Unexepected program flow termination.",
                "Please contact a developer!").createHttpResponse();
    }

    public static Response listFiles(HttpServletRequest request, String mcrIDString, String derIDString,
            String format) {
        MCRSession session = MCRServlet.getSession(request);
        session.beginTransaction();
        try {

            if (format.equals(MCRRestAPIObjects.FORMAT_JSON) || format.equals(MCRRestAPIObjects.FORMAT_XML)) {
                //ok
            } else {
                MCRRestAPIError error = MCRRestAPIError.create(Response.Status.BAD_REQUEST,
                        "The syntax of one or more query parameters is wrong.", null);
                error.addFieldError(MCRRestAPIFieldError.create("format",
                        "Allowed values for format are 'json' or 'xml'."));
                throw new MCRRestAPIException(error);
            }
            MCRObject mcrObj = retrieveMCRObject(mcrIDString);
            MCRDerivate derObj = retrieveMCRDerivate(mcrObj, derIDString);

            //output as XML
            if (MCRRestAPIObjects.FORMAT_XML.equals(format)) {
                Document docOut = new Document(listDerivateContent(derObj, request));
                try {
                    StringWriter sw = new StringWriter();
                    XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
                    xout.output(docOut, sw);
                    return Response.ok(sw.toString()).type("application/xml; charset=UTF-8").build();
                } catch (IOException e) {
                    return MCRRestAPIError.create(Response.Status.INTERNAL_SERVER_ERROR,
                            "A problem occurred while fetching the data", e.getMessage()).createHttpResponse();
                }
            }

            //output as JSON
            if (MCRRestAPIObjects.FORMAT_JSON.equals(format)) {
                String result = listDerivateContentAsJson(derObj);
                return Response.ok(result).type("application/json; charset=UTF-8").build();
            }

            return MCRRestAPIError.create(Response.Status.INTERNAL_SERVER_ERROR,
                    "Unexepected program flow termination.", "Please contact a developer!").createHttpResponse();

        } catch (MCRRestAPIException rae) {
            return rae.getError().createHttpResponse();
        } finally {
            session.commitTransaction();
        }
    }

    /**
     * validates the given String if it matches the UTC syntax or the beginning of it
     * @param test
     * @return true, if it is valid
     */
    private static boolean validateDateInput(String test) {
        String base = "0000-00-00T00:00:00Z";
        if (test.length() > base.length())
            return false;
        test = test + base.substring(test.length());
        try {
            SDF_UTC.parse(test);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    private static MCRRestAPISortObject createSortObject(String input) throws MCRRestAPIException {
        if (input == null) {
            return null;
        }
        MCRRestAPIError error = MCRRestAPIError.create(Response.Status.BAD_REQUEST, "", null);
        MCRRestAPISortObject result = new MCRRestAPISortObject();

        String[] data = input.split(":");
        if (data.length == 2) {
            result.setField(data[0].replace("|", ""));
            String sortOrder = data[1].toLowerCase(Locale.GERMAN).replace("|", "");
            if (!"ID".equals(result.getField()) && !"lastModified".equals(result.getField())) {
                error.addFieldError(MCRRestAPIFieldError.create("sort",
                        "Allowed values for sortField are 'ID' and 'lastModified'."));
            }

            if ("asc".equals(sortOrder)) {
                result.setOrder(SortOrder.ASC);
            }
            if ("desc".equals(sortOrder)) {
                result.setOrder(SortOrder.DESC);
            }
            if (result.getOrder() == null) {
                error.addFieldError(MCRRestAPIFieldError.create("sort",
                        "Allowed values for sortOrder are 'asc' and 'desc'."));
            }

        } else {
            error.addFieldError(MCRRestAPIFieldError.create("sort", "The syntax should be [sortField]:[sortOrder]."));
        }
        if (error.getFieldErrors().size() > 0) {
            throw new MCRRestAPIException(error);
        }
        return result;
    }

    private static MCRObject retrieveMCRObject(String idString) throws MCRRestAPIException {
        String key = "mcr"; // the default value for the key
        if (idString.contains(":")) {
            int pos = idString.indexOf(":");
            key = idString.substring(0, pos);
            idString = idString.substring(pos + 1);
            if (!key.equals("mcr")) {
                try{
                    idString = URLDecoder.decode(idString, "UTF-8");
                }
                catch(UnsupportedEncodingException e){
                    //will not happen
                }
                //ToDo - Shall we restrict the key set with a property?
                
                //throw new MCRRestAPIException(MCRRestAPIError.create(Response.Status.BAD_REQUEST,
                //        "The ID is not valid.", "The prefix is unkown. Only 'mcr' is allowed."));
            }
        }
        if (key.equals("mcr")) {

            MCRObjectID mcrID = null;
            try {
                mcrID = MCRObjectID.getInstance(idString);
            } catch (Exception e) {
                throw new MCRRestAPIException(MCRRestAPIError.create(Response.Status.BAD_REQUEST, "The MyCoRe ID '"
                        + idString + "' is not valid. Did you use the proper format: '{project}_{type}_{number}'?",
                        e.getMessage()));
            }

            if (!MCRMetadataManager.exists(mcrID)) {
                throw new MCRRestAPIException(MCRRestAPIError.create(Response.Status.NOT_FOUND,
                        "There is no object with the given MyCoRe ID '" + idString + "'.", null));
            }

            return MCRMetadataManager.retrieveMCRObject(mcrID);
        }
        else{
        	//TODO SOLR Migration
           /*
        	MCRQuery mcrQuery = new MCRQuery((new MCRQueryParser()).parse("("+key+" = "+idString+")"));
            MCRResults result = MCRQueryManager.search(mcrQuery);
            
            if(result.getNumHits()==1){
                String id = result.getHit(0).getID();
                return MCRMetadataManager.retrieveMCRObject(MCRObjectID.getInstance(id));
            }
            else{
                if(result.getNumHits()==0){
                    throw new MCRRestAPIException(MCRRestAPIError.create(Response.Status.NOT_FOUND,
                            "There is no object with the given ID '" + key+":"+idString + "'.", null));
                }
                else{
                    throw new MCRRestAPIException(MCRRestAPIError.create(Response.Status.NOT_FOUND,
                            "The ID is not unique. There are "+result.getNumHits()+" objecst fore the given ID '" + key+":"+idString + "'.", null));
                }
            }
            */
        	return null;
        }
    }

    private static MCRDerivate retrieveMCRDerivate(MCRObject mcrObj, String derIDString) throws MCRRestAPIException {

        String derKey = "mcr"; // the default value for the key
        if (derIDString.contains(":")) {
            int pos = derIDString.indexOf(":");
            derKey = derIDString.substring(0, pos);
            derIDString = derIDString.substring(pos + 1);
            if (!derKey.equals("mcr") && !derKey.equals("label")) {
                throw new MCRRestAPIException(MCRRestAPIError.create(Response.Status.BAD_REQUEST,
                        "The ID is not valid.", "The prefix is unkown. Only 'mcr' or 'label' are allowed."));
            }
        }

        String matchedDerID = null;
        for (MCRMetaLinkID check : mcrObj.getStructure().getDerivates()) {
            if (derKey.equals("mcr")) {
                if (check.getXLinkHref().equals(derIDString)) {
                    matchedDerID = check.getXLinkHref();
                    break;
                }
            }
            if (derKey.equals("label")) {
                if (derIDString.equals(check.getXLinkLabel()) || derIDString.equals(check.getXLinkTitle())) {
                    matchedDerID = check.getXLinkHref();
                    break;
                }
            }
        }

        if (matchedDerID == null) {
            throw new MCRRestAPIException(MCRRestAPIError.create(Response.Status.NOT_FOUND, "Derivate not found.",
                    "The MyCoRe Object with id '" + mcrObj.getId().toString()
                            + "' does not contain a derivate with id '" + derIDString + "'."));
        }

        MCRObjectID derID = MCRObjectID.getInstance(matchedDerID);
        if (!MCRMetadataManager.exists(derID)) {
            throw new MCRRestAPIException(MCRRestAPIError.create(Response.Status.NOT_FOUND,
                    "There is no derivate with the id '" + matchedDerID + "'.", null));
        }

        return MCRMetadataManager.retrieveMCRDerivate(derID);
    }
}
