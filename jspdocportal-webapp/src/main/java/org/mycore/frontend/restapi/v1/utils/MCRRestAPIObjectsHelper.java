package org.mycore.frontend.restapi.v1.utils;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.jdom2.Comment;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.mycore.common.MCRConstants;
import org.mycore.common.MCRException;
import org.mycore.common.MCRSession;
import org.mycore.datamodel.ifs.MCRDirectory;
import org.mycore.datamodel.ifs.MCRFile;
import org.mycore.datamodel.ifs.MCRFilesystemNode;
import org.mycore.datamodel.metadata.MCRDerivate;
import org.mycore.datamodel.metadata.MCRMetadataManager;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.restapi.v1.MCRRestAPIObjects;
import org.mycore.frontend.restapi.v1.errors.MCRRestAPIError;
import org.mycore.frontend.servlets.MCRServlet;

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
        if(root!=null){
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
}
