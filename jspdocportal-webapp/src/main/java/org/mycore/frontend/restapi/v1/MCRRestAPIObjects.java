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
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.mycore.common.MCRConstants;
import org.mycore.datamodel.common.MCRObjectIDDate;
import org.mycore.datamodel.common.MCRXMLMetadataManager;
import org.mycore.datamodel.ifs.MCRDirectoryXML;
import org.mycore.datamodel.metadata.MCRMetadataManager;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;

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
			Collections.sort(objIdDates, new SortFieldComparator(sortField, sortOrder));
		}
		
		if (FORMAT_XML.equals(format)) {
			Element eMcrobjects = new Element("mycoreobjects");
			Document docOut = new Document(eMcrobjects);
			eMcrobjects.setAttribute("numFound", Integer.toString(objIdDates.size()));
			for(MCRObjectIDDate oid: objIdDates){
				Element eMcrObject = new Element("mycoreobject");
				eMcrObject.setAttribute("ID", oid.getId());
				eMcrObject.setAttribute("lastModified", SDF_UTC.format(oid.getLastModified()));
				eMcrObject.setAttribute("href", info.getAbsolutePathBuilder().path("id").path(oid.getId()).build((Object[]) null).toString());
					
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
				writer.name("mycoreobject");
				writer.beginArray();
				for(MCRObjectIDDate oid: objIdDates){
					writer.beginObject();
					writer.name("ID").value(oid.getId());
					writer.name("lastModified").value(SDF_UTC.format(oid.getLastModified()));
					writer.name("href").value(info.getAbsolutePathBuilder().path("id").path(oid.getId()).build((Object[]) null).toString());
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
	 * @param id the MCRObjectID
	 * 
	 * Parameter style (derivatedetails)
	 * @return
	 */
	@GET
	@Produces(MediaType.TEXT_XML)
	@Path("/id/{value}")	
	public Response returnXMLObject(@PathParam("value") String id,
			@QueryParam("style") String style){
		try{
		MCRObjectID mcrID = MCRObjectID.getInstance(id);
		if(MCRMetadataManager.exists(mcrID)){
			MCRObject mcrO = MCRMetadataManager.retrieveMCRObject(mcrID);
			Document doc = mcrO.createXML();
			Element eStructure = doc.getRootElement().getChild("structure");
			if(STYLE_DERIVATEDETAILS.equals(style) && eStructure!=null){
				Element eDerObjects = eStructure.getChild("derobjects");
				if(eDerObjects != null){
					for(Element eDer: (List<Element>)eDerObjects.getChildren("derobject")){
						String derID = eDer.getAttributeValue("href", MCRConstants.XLINK_NAMESPACE);
						Document docDer = MCRMetadataManager.retrieveMCRDerivate(MCRObjectID.getInstance(derID)).createXML();
						eDer.addContent(docDer.getRootElement().detach());
				
						//<mycorederivate xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xlink="http://www.w3.org/1999/xlink" xsi:noNamespaceSchemaLocation="datamodel-derivate.xsd" ID="cpr_derivate_00003760" label="display_image" version="1.3">
						//  <derivate display="true">

						eDer = eDer.getChild("mycorederivate").getChild("derivate");
						Document fileDoc = MCRDirectoryXML.getInstance().getDirectory("/"+derID, false);
						eDer.addContent(fileDoc.getRootElement().detach());			
					}
				}
			}

			StringWriter sw = new StringWriter();
			XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
			outputter.output(doc, sw);
			return Response.ok(sw.toString()).type("application/xml").build();
		}
		}
		catch(IOException e){
			//TODO
		}
		
		return Response.status(com.sun.jersey.api.client.ClientResponse.Status.BAD_REQUEST).build();
	}
	
	class SortFieldComparator implements Comparator<MCRObjectIDDate> {
		private String _sortField = null;
		private String _sortOrder = null;
		public SortFieldComparator(String sortField, String sortOrder){
			_sortField = sortField.toLowerCase();
			_sortOrder = sortOrder.toLowerCase();
		}
		@Override
        public int compare(MCRObjectIDDate o1, MCRObjectIDDate o2) {
			if("id".equals(_sortField)){
				if("asc".equals(_sortOrder)){
					return o1.getId().compareTo(o2.getId());
				}
				if("desc".equals(_sortOrder)){
					return o2.getId().compareTo(o1.getId());
				}
			}
			if("lastmodified".equals(_sortField)){
				if("asc".equals(_sortOrder)){
					return o1.getLastModified().compareTo(o2.getLastModified());
				}
				if("desc".equals(_sortOrder)){
					return o2.getLastModified().compareTo(o1.getLastModified());
				}
			}
			
            return 0;
        }
	}
	
}
