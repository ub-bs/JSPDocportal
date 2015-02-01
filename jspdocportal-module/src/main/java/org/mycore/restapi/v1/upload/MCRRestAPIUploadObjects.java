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
package org.mycore.restapi.v1.upload;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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

import org.mycore.restapi.v1.utils.MCRRestAPIObjectsHelper;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Path("/v1/upload/objects")
public class MCRRestAPIUploadObjects {
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
	
   
   
   @POST
   @Path("/")
   @Produces({ MediaType.TEXT_XML + ";charset=UTF-8" })
   @Consumes(MediaType.MULTIPART_FORM_DATA)
   public Response uploadObject(@Context UriInfo info, @Context HttpServletRequest request,
           @FormDataParam("file") InputStream uploadedInputStream,
           @FormDataParam("file") FormDataContentDisposition fileDetails) {
       return MCRRestAPIUploadHelper.uploadObject(info, request, uploadedInputStream, fileDetails);

   }
   
   @POST
   @Path("/{mcrObjID}/derivates")
   @Produces({ MediaType.TEXT_XML + ";charset=UTF-8" })
   @Consumes(MediaType.MULTIPART_FORM_DATA)
   public Response uploadDerivate(@Context UriInfo info, @Context HttpServletRequest request,
           @PathParam("mcrObjID") String mcrObjID, @FormDataParam("label") String label) {
       return MCRRestAPIUploadHelper.uploadDerivate(info, request, mcrObjID, label);
   }
   
   @POST
   @Path("/{mcrObjID}/derivates/{mcrDerID}/files")
   @Produces({ MediaType.TEXT_XML + ";charset=UTF-8" })
   @Consumes(MediaType.MULTIPART_FORM_DATA)
   public Response uploadFile(@Context UriInfo info, @Context HttpServletRequest request,
           @PathParam("mcrObjID") String mcrObjID, @PathParam("mcrDerID") String mcrDerID,
           @FormDataParam("file") InputStream uploadedInputStream,
           @FormDataParam("file") FormDataContentDisposition fileDetails, 
           @FormDataParam("path") String path,
           @DefaultValue("false") @FormDataParam("maindoc") boolean maindoc,
           @FormDataParam("md5") String md5,
           @FormDataParam("size") Long size){
       return MCRRestAPIUploadHelper.uploadFile(info, request, mcrObjID, mcrDerID, uploadedInputStream, fileDetails, path, maindoc, md5, size);
   }
   
   @DELETE
   @Path("/{mcrObjID}/derivates/{mcrDerID}/files")
   public Response deleteFiles(@Context UriInfo info, @Context HttpServletRequest request,
           @PathParam("mcrObjID") String mcrObjID, @PathParam("mcrDerID") String mcrDerID){
       return MCRRestAPIUploadHelper.deleteAllFiles(info, request, mcrObjID, mcrDerID);
       
   }
	
}
