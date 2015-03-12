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

import java.io.StringWriter;

import javax.servlet.http.HttpServlet;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.mycore.services.fieldquery.MCRFieldValue;
import org.mycore.services.fieldquery.MCRHit;
import org.mycore.services.fieldquery.MCRQuery;
import org.mycore.services.fieldquery.MCRQueryManager;
import org.mycore.services.fieldquery.MCRQueryParser;
import org.mycore.services.fieldquery.MCRResults;
import org.mycore.services.fieldquery.MCRSortBy;

/**
 * Rest API for messages.
 * Allowes access to language properties
 * 
 *  
 * @author Robert Stephan
 *
 */
@Path("/v1/search")
public class MCRRestAPISearch extends HttpServlet {
    private static final long serialVersionUID = 1L;
    public static final String FORMAT_JSON = "json";
    public static final String FORMAT_XML = "xml";

    /**
     * see http://wiki.apache.org/solr/CommonQueryParameters for syntax of parameters
     * 
     * @param info - a Jersey Context Object for URI
     *      
     * @param q
     * 		the Query in SOLR Query Syntax
     * @param sort
     * 		only one sort field with sort direction "asc" or "desc" is currently supported
     * @return
     */
    @GET
    @Produces({ MediaType.TEXT_XML + ";charset=UTF-8", MediaType.APPLICATION_JSON + ";charset=UTF-8",
            MediaType.TEXT_PLAIN + ";charset=ISO-8859-1" })
    public Response search(@Context UriInfo info, @QueryParam("q") String query,
            @QueryParam("sort") String sort,
            @QueryParam("format") @DefaultValue("xml") String format) {

        try {

            String q = "(allMeta like *)";
            if (query.contains(":")) {
            	q="(";
            	String[] terms = query.split(" ");
            	for(int i=0;i<terms.length;i++){
            		if(i>0){
            			q=q+" ";
            		}
            		q = q+ terms[i].replaceFirst(":", " = ");
            		
            	}
                q = q + ")";
            } else {
                q = "(allMeta = " + query + ")";
            }
            if (q.contains("*")) {
                q = q.replace(" = ", " like ");
            }
            MCRQuery mcrQuery = new MCRQuery((new MCRQueryParser()).parse(q));
            
            if(sort!=null && sort.length()>0){
                String[] sortData = sort.split(" ");
                if(sortData.length==2){
                    MCRSortBy sortBy = new MCRSortBy(sortData[0], "asc".equals(sortData[1]));
                    mcrQuery.setSortBy(sortBy);
                }
            }
            
            MCRResults result = MCRQueryManager.search(mcrQuery);

            if (FORMAT_XML.equals(format)) {
                Document doc = new Document();
                Element root = new Element("response");
                doc.addContent(root);
                Element eResult = new Element("result");
                root.addContent(eResult);
                eResult.setAttribute("name", "response");
                eResult.setAttribute("numFound", Integer.toString(result.getNumHits()));
                eResult.setAttribute("start", "0");

                for (int i = 0; i < result.getNumHits(); i++) {
                    MCRHit hit = result.getHit(i);
                    String mcrID = hit.getID();
                    MCRFieldValue field = hit.getMetaData("recordIdentifier");
                    Element eDoc = new Element("doc");
                    eResult.addContent(eDoc);
                    eDoc.setAttribute("href", info.getAbsolutePathBuilder().path(mcrID).build((Object[]) null)
                                            .toString().replace("/search/", "/objects/"));
                    eDoc.addContent(new Element("str").setAttribute("name", "id").setText(mcrID));
                    eDoc.addContent(new Element("str").setAttribute("name", "returnId").setText(mcrID));
                    eDoc.addContent(new Element("str").setAttribute("name", "objectProject").setText(
                            mcrID.split("_")[0]));
                    eDoc.addContent(new Element("str").setAttribute("name", "objectType").setText(mcrID.split("_")[1]));
                    if(field!=null){
                        eDoc.addContent(new Element("str").setAttribute("name", "recordIdentifier").setText(field.getValue()));
                    }
                }
                StringWriter sw = new StringWriter();
                XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
                outputter.output(doc, sw);
                return Response.ok(sw.toString()).type("application/xml; charset=UTF-8").build();
            }

        } catch (Exception e) {
            //toDo
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

}