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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
import org.mycore.datamodel.common.MCRObjectIDDate;
import org.mycore.datamodel.common.MCRXMLMetadataManager;
import org.mycore.frontend.restapi.v1.utils.MCRRestAPIObjectsHelper;
import org.mycore.frontend.restapi.v1.utils.MCRRestAPISortFieldComparator;

import com.google.gson.stream.JsonWriter;

@Path("/v1/objects")
public class MCRRestAPIObjects {
	public static final String STYLE_DERIVATEDETAILS="derivatedetails";
	public static final String FORMAT_JSON = "json";
	public static final String FORMAT_XML = "xml";
	public static final String SORT_ASC = "asc";
	public static final String SORT_DESC = "desc";

	private static SimpleDateFormat SDF_UTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    
	
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
	 */
	
	@GET
	@Produces({ MediaType.TEXT_XML + ";charset=UTF-8", MediaType.APPLICATION_JSON + ";charset=UTF-8" })
	public Response listObjects(@Context UriInfo info, 
								@QueryParam("format") @DefaultValue("xml") String format,
								@QueryParam("filter")                      String filter,
								@QueryParam("sort")  @DefaultValue("ID:asc") String sort)
								{
						
		List<String> projectIDs = new ArrayList<String>();
		List<String> typeIDs = new ArrayList<String>();
		String lastModifiedBefore = null;
		String lastModifiedAfter=null;
		String sortField=null;
		String sortOrder=null;
		if(sort!=null){
			String[] data = sort.split(":");
			if(data.length==2){
				sortField=data[0];
				sortOrder = data[1].toLowerCase();
			}
		}
		
		if(filter!=null){
			for(String s: filter.split(";")){
				if(s.startsWith("project:")){projectIDs.add(s.substring(8)); }
				if(s.startsWith("type:")){typeIDs.add(s.substring(5)); }
				if(s.startsWith("lastModifiedBefore:")){lastModifiedBefore = s.substring(19); }
				if(s.startsWith("lastModifiedAfter:")){lastModifiedAfter = s.substring(18); }
			}
		}
		Set<String> mcrIDs = new HashSet<String>();
		if(projectIDs.isEmpty()){
			if(typeIDs.isEmpty()){
				for(String id: MCRXMLMetadataManager.instance().listIDs()){
					if(!id.contains("_derivate_")){mcrIDs.add(id);}
				}
			}
			else{
				for(String t: typeIDs){
					mcrIDs.addAll(MCRXMLMetadataManager.instance().listIDsOfType(t));
				}
			}
		}
		else{
			
				if(typeIDs.isEmpty()){
					for(String id: MCRXMLMetadataManager.instance().listIDs()){
						String[] split = id.split("_");
						if(!split[1].equals("derivate") && projectIDs.contains(split[0])){mcrIDs.add(id);}
					}					
				}
				else{
					for(String p: projectIDs){
						for(String t: typeIDs){
							mcrIDs.addAll(MCRXMLMetadataManager.instance().listIDsForBase(p+"_"+t));
						}
					}
				}
			
		}
		List<String> l = new ArrayList<String>();
		l.addAll(mcrIDs);
		List<MCRObjectIDDate> objIdDates = new ArrayList<MCRObjectIDDate>();
		try{
			objIdDates = MCRXMLMetadataManager.instance().retrieveObjectDates(l);
		}
		catch(IOException e){
			//TODO
		}
		if(lastModifiedAfter!=null || lastModifiedBefore!=null){
			List<MCRObjectIDDate> testObjIdDates = objIdDates;
			objIdDates = new ArrayList<MCRObjectIDDate>();
			for(MCRObjectIDDate oid: testObjIdDates){
				String test = SDF_UTC.format(oid.getLastModified());
				if(lastModifiedAfter!=null && test.compareTo(lastModifiedAfter)<0) continue;
				if(lastModifiedBefore!=null && lastModifiedBefore.compareTo(test.substring(0,lastModifiedBefore.length()))<0) continue;
				objIdDates.add(oid);
			}
		}
		if(sortOrder != null && sortField!=null){
			Collections.sort(objIdDates, new MCRRestAPISortFieldComparator(sortField, sortOrder));
		}
		
		if (FORMAT_XML.equals(format)) {
			Element eMcrobjects = new Element("mycoreobjects");
			Document docOut = new Document(eMcrobjects);
			eMcrobjects.setAttribute("numFound", Integer.toString(objIdDates.size()));
			for(MCRObjectIDDate oid: objIdDates){
				Element eMcrObject = new Element("mycoreobject");
				eMcrObject.setAttribute("ID", oid.getId());
				eMcrObject.setAttribute("lastModified", SDF_UTC.format(oid.getLastModified()));
				eMcrObject.setAttribute("href", info.getAbsolutePathBuilder().path(oid.getId()).build((Object[]) null).toString());
					
				eMcrobjects.addContent(eMcrObject);
			}
			try {
				StringWriter sw = new StringWriter();
				XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
				xout.output(docOut, sw);
				return Response.ok(sw.toString()).type("application/xml; charset=UTF-8").build();
			} catch (IOException e) {
				//ToDo
			}
		}
			
		if (FORMAT_JSON.equals(format)) {
			StringWriter sw = new StringWriter();
			try {
				JsonWriter writer = new JsonWriter(sw);
				writer.setIndent("    ");
				writer.beginObject();
				writer.name("numFound").value(Integer.toString(objIdDates.size()));
				writer.name("mycoreobject");
				writer.beginArray();
				for(MCRObjectIDDate oid: objIdDates){
					writer.beginObject();
					writer.name("ID").value(oid.getId());
					writer.name("lastModified").value(SDF_UTC.format(oid.getLastModified()));
					writer.name("href").value(info.getAbsolutePathBuilder().path(oid.getId()).build((Object[]) null).toString());
					writer.endObject();
				}	
				writer.endArray();
				writer.endObject();

				writer.close();

				return Response.ok(sw.toString()).type("application/json; charset=UTF-8").build();
				} catch (IOException e) {
					//toDo
				}
			}
			return Response.status(com.sun.jersey.api.client.ClientResponse.Status.BAD_REQUEST).build();
	}
	/**
	 * returns a single object in XML Format
	 * @param an object identifier of syntax [id] or [prefix]:[id]
	 * 
	 * Allowed Prefixes are "mcr"
	 * "mcr" is the default prefix for MyCoRe IDs.
	 * 
	 * @param style allowed values are "derivatedetails"
	 * derivate details will be integrated into the output.
	 * 
	 * @return
	 */
	@GET
	@Produces(MediaType.TEXT_XML)
	@Path("/{value}")	
	public Response returnXMLObject(
	        @Context HttpServletRequest request,
	        @PathParam("value") String id,
			@QueryParam("style") String style){
	    return MCRRestAPIObjectsHelper.showMCRObject(id, style, request);
		
	}
	
}
