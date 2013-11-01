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

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.mycore.frontend.restapi.v1.utils.MCRRestAPIObjectsHelper;
import org.mycore.frontend.restapi.v1.utils.MCRRestAPIUploadsHelper;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Path("/v1/objects")
public class MCRRestAPIObjects {
	public static final String STYLE_DERIVATEDETAILS="derivatedetails";
	public static final String FORMAT_JSON = "json";
	public static final String FORMAT_XML = "xml";
	public static final String SORT_ASC = "asc";
	public static final String SORT_DESC = "desc";

   /** returns a list of mcrObjects 
	 *
	 * Parameter
	 * ---------
	 * filter - parameter with filters as colon-separated key-value-pairs, pair separator is semicolon, allowed values are
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
						
			return MCRRestAPIObjectsHelper.listObjects(info, format, filter, sort);
	}
	
   /** returns a list of derivates for a given MyCoRe Object 
    *
    * Parameter
    * ---------
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
   @Path("/{mcrid}/derivates")    
   public Response listDerivates(@Context UriInfo info,
                               @PathParam("mcrid") String mcrID,
                               @QueryParam("format") @DefaultValue("xml") String format,
                               @QueryParam("sort")  @DefaultValue("ID:asc") String sort)
                               {
                       
           return MCRRestAPIObjectsHelper.listDerivates(info, mcrID, format, sort);
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
	public Response returnMCRObject(
	        @Context HttpServletRequest request,
	        @PathParam("value") String id,
			@QueryParam("style") String style){
	    return MCRRestAPIObjectsHelper.showMCRObject(id, style, request);
		
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
    @Path("/{mcrid}/derivates/{derid}")   
    public Response returnDerivate(
            @Context HttpServletRequest request,
            @PathParam("mcrid") String mcrid,
            @PathParam("derid") String derid,
            @QueryParam("style") String style){
        return MCRRestAPIObjectsHelper.showMCRDerivate(mcrid, derid, request);
        
    }
    
    /** returns a list of derivates for a given MyCoRe Object 
    *
    * Parameter
    * ---------
    * 
    * format - parameter for return format, values are
    *     * xml (default value)
    *     * json
    */
   
   @GET
   @Produces({ MediaType.TEXT_XML + ";charset=UTF-8", MediaType.APPLICATION_JSON + ";charset=UTF-8" })
   @Path("/{mcrid}/derivates/{derid}/files")    
   public Response listFiles(@Context UriInfo info,
           @Context HttpServletRequest request,
           @PathParam("mcrid") String mcrID,
           @PathParam("derid") String derID,
           @QueryParam("format") @DefaultValue("xml") String format){
           return MCRRestAPIObjectsHelper.listFiles(request, mcrID, derID, format);
   }

   /*********************************************************************/
   
   @POST
   @Path("/objects/")
   @Produces({ MediaType.TEXT_XML + ";charset=UTF-8" })
   @Consumes(MediaType.MULTIPART_FORM_DATA)
   public Response uploadObject(@Context UriInfo info, @Context HttpServletRequest request,
           @FormDataParam("file") InputStream uploadedInputStream,
           @FormDataParam("file") FormDataContentDisposition fileDetails) {
       return MCRRestAPIUploadsHelper.uploadObject(info, request, uploadedInputStream, fileDetails);

   }
   
   @POST
   @Path("/objects/{mcrObjID}/derivates")
   @Produces({ MediaType.TEXT_XML + ";charset=UTF-8" })
   @Consumes(MediaType.MULTIPART_FORM_DATA)
   public Response uploadDerivate(@Context UriInfo info, @Context HttpServletRequest request,
           @PathParam("mcrObjID") String mcrObjID, @FormDataParam("label") String label) {
       return MCRRestAPIUploadsHelper.uploadDerivate(info, request, mcrObjID, label);
   }
   
   @POST
   @Path("/objects/{mcrObjID}/derivates/{mcrDerID}/files")
   @Produces({ MediaType.TEXT_XML + ";charset=UTF-8" })
   @Consumes(MediaType.MULTIPART_FORM_DATA)
   public Response uploadFile(@Context UriInfo info, @Context HttpServletRequest request,
           @PathParam("mcrObjID") String mcrObjID, @PathParam("mcrDerID") String mcrDerID,
           @FormDataParam("file") InputStream uploadedInputStream,
           @FormDataParam("file") FormDataContentDisposition fileDetails, 
           @FormDataParam("rest-client") String clientID,
           @FormDataParam("path") String path,
           @DefaultValue("false") @FormDataParam("maindoc") boolean maindoc,
           @FormDataParam("md5") String md5,
           @FormDataParam("size") Long size){
       return MCRRestAPIUploadsHelper.uploadFile(info, request, mcrObjID, mcrDerID, uploadedInputStream, fileDetails, clientID, path, maindoc, md5, size);
       
   }
	
}
