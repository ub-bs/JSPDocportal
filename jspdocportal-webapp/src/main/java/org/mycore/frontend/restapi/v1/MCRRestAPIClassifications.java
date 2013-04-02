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
import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.http.HttpServlet;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.hibernate.Transaction;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.mycore.backend.hibernate.MCRHIBConnection;
import org.mycore.datamodel.classifications2.MCRCategory;
import org.mycore.datamodel.classifications2.MCRCategoryDAO;
import org.mycore.datamodel.classifications2.MCRCategoryID;
import org.mycore.datamodel.classifications2.impl.MCRCategoryDAOImpl;
import org.mycore.datamodel.classifications2.utils.MCRCategoryTransformer;

import com.google.gson.stream.JsonWriter;

/**
 * Rest API to download classification objects in different formats.
 * 
 *  Possible request parameters are:
 *  - classid - the ID of the MyCoRe classification (required)
 *  - categid - the root category, if only a part of the classification should be returned
 *  - format = json | xml (required)
 *  - style = checkboxtree - JSON format used as input for a Dijit CheckBox Tree (lang attribute must be provided!)
 *  - lang - the language of the returned labels, if ommited all labels in all languages will be returned
 *  
 * @author Robert Stephan
 *
 */
@Path("/classifications")
public class MCRRestAPIClassifications extends HttpServlet {
    private static final long serialVersionUID = 1L;
    public static final String FORMAT_JSON = "json";
    public static final String FORMAT_XML = "xml";

    private static final MCRCategoryDAO DAO = new MCRCategoryDAOImpl();

    @GET
    @Path("/id/{value}")  
    @Produces({MediaType.TEXT_XML +";charset=UTF-8", MediaType.APPLICATION_JSON +";charset=UTF-8"})
    public Response showObject(@PathParam("value") String classID,
                               @QueryParam("filter") String filter,
                               @QueryParam("style") String style){
        return showObject(classID, "html", filter, style);
    }
             
    @GET
    //@Path("/id/{value}{format:(\\.[^/]+?)?}")  -> working, but returns empty string instead of default value
    @Path("/id/{value}.{format:([^/]*)}")  
    @Produces({MediaType.TEXT_XML +";charset=UTF-8", MediaType.APPLICATION_JSON +";charset=UTF-8"})
    public Response showObject(@PathParam("value") String classID,
                               @PathParam("format") String format,
                               @QueryParam("filter") String filter,
                               @QueryParam("style") String style){
             
        String rootCateg = null;
        String lang = null;
     
        if(filter!=null){
        for(String f: filter.split(";")){
            if(f.startsWith("root:")){
                rootCateg = f.substring(5);
            }
            if(f.startsWith("lang:")){
                lang = f.substring(5);
            }
        }
        }
        if (format == null || classID == null) {
            return Response.serverError().status(Status.BAD_REQUEST).build();
            //TODO response.sendError(HttpServletResponse.SC_NOT_FOUND, "Please specify parameters format and classid.");
           
        }
        Transaction tx = MCRHIBConnection.instance().getSession().beginTransaction();
        try {
            MCRCategory cl = DAO.getCategory(MCRCategoryID.rootID(classID), -1);
            Document docClass = MCRCategoryTransformer.getMetaDataDocument(cl, false);
            Element eRoot = docClass.getRootElement();
            if (rootCateg != null) {
                XPathExpression<Element> xpe = XPathFactory.instance().compile("//category[@ID='" + rootCateg + "']",
                        Filters.element());
                Element e = xpe.evaluateFirst(docClass);
                if (e != null) {
                    eRoot = e;
                }
            }
            if (FORMAT_JSON.equals(format)) {
                String json = writeJSON(eRoot, lang, style);
                return Response.ok(json).type("application/json").build(); 
               
            }

            if (FORMAT_XML.equals(format)) {
                String xml = writeXML(eRoot, lang);
                return Response.ok(xml).type("application/xml").build(); 
            }
        }

        catch (Exception e) {
            Logger.getLogger(this.getClass()).error("Error outputting classification", e);
            //TODO response.sendError(HttpServletResponse.SC_NOT_FOUND, "Error outputting classification");
        } finally {
            tx.commit();
        }
        return null;
    }

    private static String writeXML(Element eRoot, String lang) throws IOException {
       StringWriter sw = new StringWriter();

        if (lang != null) {
            // <label xml:lang="en" text="part" />
            XPathExpression<Element> xpE = XPathFactory.instance().compile("//label[xml:lang!='" + lang + "']",
                    Filters.element(), null, Namespace.XML_NAMESPACE);
            for (Element e : xpE.evaluate(eRoot)) {
                e.getParentElement().removeContent(e);
            }
        }
        XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
        Document docOut = new Document(eRoot.detach());
        xout.output(docOut, sw);
        return sw.toString();
    }

    private String writeJSON(Element eRoot, String lang, String style)
            throws IOException {
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        writer.setIndent("  ");
        if ("checkboxtree".equals(style)) {
            if (lang == null) {
                lang = "de";
            }
            if (eRoot.equals(eRoot.getDocument().getRootElement())) {
                eRoot = eRoot.getChild("categories");
            }
            writer.beginObject(); // {
            writer.name("identifier").value("ID");
            writer.name("label").value("ID");
            writer.name("items");

            writeChildrenAsJSONCBTree(eRoot, writer, lang);

            writer.endObject(); // }
            
        } else {
            writer.beginObject(); // {
            writer.name("ID").value("ID");
            writer.name("label");
            writer.beginArray();
            for (Element eLabel : eRoot.getChildren("label")) {
                writer.beginObject();
                writer.name("lang").value(eLabel.getAttributeValue("lang", Namespace.XML_NAMESPACE));
                writer.name("text").value(eLabel.getAttributeValue("text"));
                writer.endObject();
            }
            writer.endArray();
            writer.name("categories");
            
            writeChildrenAsJSON(eRoot.getChild("categories"), writer);
            
            writer.endObject();
        }
        writer.close();
        return sw.toString();
    }

    private static void writeChildrenAsJSON(Element eParent, JsonWriter writer) throws IOException {
        writer.beginArray();
        for (Element e : eParent.getChildren("category")) {
            writer.beginObject();
            writer.name("ID").value(e.getAttributeValue("ID"));
            writer.name("labels").beginArray();
            for (Element eLabel : e.getChildren("label")) {
                writer.beginObject();
                writer.name("lang").value(eLabel.getAttributeValue("lang", Namespace.XML_NAMESPACE));
                writer.name("text").value(eLabel.getAttributeValue("text"));
                writer.endObject();
            }
            writer.endArray();

            if (e.getChildren("category").size() > 0) {
                writer.name("categories");
                writeChildrenAsJSON(e, writer);
            }
            writer.endObject();
        }
        writer.endArray();
    }

    /**
     * output children in JSON format used as input for Dijit Checkbox Tree
     */
    private static void writeChildrenAsJSONCBTree(Element eParent, JsonWriter writer, String lang) throws IOException {
        writer.beginArray();
        for (Element e : eParent.getChildren("category")) {
            writer.beginObject();
            writer.name("ID").value(e.getAttributeValue("ID"));
            for (Element eLabel : e.getChildren("label")) {
                if (lang.equals(eLabel.getAttributeValue("lang", Namespace.XML_NAMESPACE))) {
                    writer.name("text").value(eLabel.getAttributeValue("text"));
                }
            }
            writer.name("checked").value(false);
            if (e.getChildren("category").size() > 0) {
                writer.name("children");
                writeChildrenAsJSONCBTree(e, writer, lang);
            }
            writer.endObject();
        }
        writer.endArray();
    }
}

