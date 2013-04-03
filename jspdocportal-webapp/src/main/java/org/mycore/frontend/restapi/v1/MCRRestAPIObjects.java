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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.mycore.datamodel.metadata.MCRMetadataManager;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;

@Path("/objects")
public class MCRRestAPIObjects {
	
	/** returns a list of mcrObjects 
	 *
	 * Parameter
	 * ---------
	 * filter - parameter with filters as colon-separated key-value-pairs, repeatable, allowed values are
	 *     * projectID - the MyCoRe ProjectID - first Part of a MyCoRe ID
	 *     * objectType - the MyCoRe ObjectType - middle Part of a MyCoRe ID 
	 *     * createdBefore - creation date in UTC is lesser than or equals to given value 
	 *     * createdAfter - creation date in UTC is greater than or equals to given value 
	 *     * lastmodifiedBefore - last modified date in UTC is lesser than or equals to given value 
	 *     * lastmodifiedAfter - last modified date in UTC is greater than or equals to given value;
	 * start -first element in lists (default = 0)
	 * rows - number of rows to be returned (default = 10, maximum defined in Property) ....
	 * 
	 * expand - parameter for level of details, allowed values are:
	 *     * objects
	 *     * derivates (implies objects)
	 *     * files (implies derivates)
	 *     * classifications (implies objects)
	 *     * acls 
	 * 
	 * format - parameter for return format, values are
	 *     * xml (default value)
	 *     * json
	 */
	
	@GET
	@Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
	public String listObjects(@QueryParam("format") @DefaultValue("xml") String format,
	                          @QueryParam("filter")                      Set<String> filter,
	                          @QueryParam("expand")                      Set<String> expand,
	                          @QueryParam("start")  @DefaultValue("0")   int start,
                              @QueryParam("rows")   @DefaultValue("10")  int rows){
		Document doc = new Document();
		Element eResult = new Element("results");
		eResult.addContent(new Element("numFound").setText("44"));
		eResult.addContent(new Element("start").setText("0"));
		eResult.addContent(new Element("rows").setText("100"));
		Element eMcrobjects = new Element("mcrobjects");
		eResult.addContent(eMcrobjects);
		List<String> data = Arrays.asList("classifications, derivates".split(","));
		Set<String> expands = new HashSet<String>();
		for(int i=0;i<data.size();i++){
			expands.add(data.get(i).trim());
		}
		if(expand.contains("files")){
		    expands.add("derivates");
		}
		for(String id: "rosdok_document_123,rosdok_document_1234".split(",")){
			
			if(expand.contains("object")){
					MCRObject mcrO = MCRMetadataManager.retrieveMCRObject(MCRObjectID.getInstance(id));
					Element eMcrobject = mcrO.createXML().getRootElement(); 
					eMcrobjects.addContent(eMcrobject);
					if(expands.contains("derivate")){
					
					}
					if(expands.contains("classification")){
						
					}
			}
			else{
				Element eMcrobject = new Element("mcrobject");
				eMcrobject.setAttribute("ID",  id);
				eMcrobjects.addContent(eMcrobject);
			}
		}
		
		
		
		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        return outputter.outputString(doc);
		
	}
	/**
	 * returns a single object in XML Format
	 * @param id the MCRObjectID
	 * 
	 * Parameter expand (derivate, classification)
	 * @return
	 */
	@GET
	@Produces(MediaType.TEXT_XML)
	@Path("/id/${value}")	
	public String returnXMLObject(@PathParam("value") String id){
		MCRObjectID mcrID = MCRObjectID.getInstance(id);
		if(MCRMetadataManager.exists(mcrID)){
			MCRObject mcrO = MCRMetadataManager.retrieveMCRObject(mcrID);
			if(mcrO!=null){
				XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
	            return outputter.outputString(mcrO.createXML());
			}
		}
		return null;
	}
	
}
