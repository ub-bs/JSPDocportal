/**
 * $RCSfile$
 * $Revision$ $Date$
 *
 * This file is part of ** M y C o R e **
 * Visit our homepage at http://www.mycore.de/ for details.
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
 * along with this program, normally in the file license.txt.
 * If not, write to the Free Software Foundation Inc.,
 * 59 Temple Place - Suite 330, Boston, MA  02111-1307 USA
 *
 **/

package org.mycore.frontend.admin;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.servlets.MCRServletJob;
import org.mycore.user2.MCRUser;
import org.mycore.user2.MCRUserMgr;

/**
 * This class is the superclass of servlets which checks the MCREditorServlet
 * output XML for metadata object and derivate objects.
 * 
 * @author Heiko Helmbrecht
 * @author Arne Seifert
 * @version $Revision$ $Date$
 */

public class MCRUserValidateServlet extends MCRServlet {

	private static final long serialVersionUID = 1L;
	protected static Logger logger = Logger.getLogger(MCRUserValidateServlet.class);

    /**
     * The method replace the default form MCRServlet 
     * 
     * @param job
     *            the MCRServletJob instance
     */
    public void doGetPost(MCRServletJob job) throws ServletException, Exception {
    	HttpServletRequest request = job.getRequest();
    	HttpServletResponse response = job.getResponse();
        String WebApplicationBaseURL = MCRServlet.getBaseURL();
    	String operation = request.getParameter("operation");
    	String paramName = "";
    	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    try {
    	if(operation.equals("detail")){
    		Enumeration paramNames = request.getParameterNames();
    		while(paramNames.hasMoreElements()) {
    	      paramName = (String)paramNames.nextElement();
    	        //System.out.println("PARAM:" + paramName );
    			if(paramName.indexOf(".x")!=-1){
    				 break;
    			}
    		}
    		String val = paramName.substring(0,paramName.indexOf(".x"));
    		String op = val.substring(0,1);

    		if (op.equals("e")){
    			// edit userdata
    			response.sendRedirect(WebApplicationBaseURL + "nav?path=admin.usermanagement.user.edit&id=" + val.substring(1));
    		}else if (op.equals("r")){
    			String fn = request.getParameter("filename");
    			response.sendRedirect(WebApplicationBaseURL + "nav?path=admin.usermanagement.user.edit&id=" +  val.substring(1)+"&filename="+fn+"&step=register");
    		}else if (op.equals("y")){
    			String fn = request.getParameter("filename");
    			response.sendRedirect(WebApplicationBaseURL + "nav?path=admin.usermanagement.user.edit&id=" +  val.substring(1)+"&filename="+fn+"&step=delete");
    		}else if (op.equals("d")){
    			MCRUserMgr.instance().deleteUser(val.substring(1));
    			response.sendRedirect(WebApplicationBaseURL + "nav?path=admin.usermanagement.user");
    		}else if (op.equals("n")){
    			// new user
    			response.sendRedirect(WebApplicationBaseURL +"nav?path=admin.usermanagement.user.edit");
    		}


    	}else if (operation.equals("edit")){
    		// save
    		boolean idEnabled=false;
    		boolean updateAllowed=false;
    		ArrayList l = new ArrayList();
    		String[] values = request.getParameterValues("ugroups");
    		
    		if (values!=null && values[0].substring(0,1)!="("){
    			for(int i=0; i< values.length; i++){
    				l.add(values[i]);
    			}
    		} else l.add("gastgroup");

    		if(request.getParameter("uenabled")!=null)
    			idEnabled=true;
    		if(request.getParameter("uupdate")!=null)
    			updateAllowed=true;

    		MCRUser user = null;
    		int id = 0;
    		String creationdate = "";
    		
    		
    		if (request.getParameter("uid_orig").equals("")){
    			id = MCRUserMgr.instance().getMaxUserNumID()+1;
    			creationdate  = request.getParameter("creationtime");
    		}else{
    			MCRUser db_user = MCRUserMgr.instance().retrieveUser(request.getParameter("uid_orig"));
    			id = db_user.getNumID();
    			creationdate = df.format(db_user.getCreationDate());
    		}

    		user = new MCRUser(
    			id,
    			request.getParameter("uid"),
    			request.getParameter("creator"),
    			Timestamp.valueOf(creationdate),
    			Timestamp.valueOf(request.getParameter("creationtime")),
    			idEnabled,
    			updateAllowed,			
    			request.getParameter("udescr"),
    			request.getParameter("upass"),
    			request.getParameter("uprimgroup"),
    			l,
    			request.getParameter("usalutation"),
    			request.getParameter("ufirstname"),
    			request.getParameter("uname"),
    			request.getParameter("uaddress"),
    			request.getParameter("ucity"),
    			request.getParameter("upostal"),
    			request.getParameter("ucountry"),
    			request.getParameter("ucountry"),
    			request.getParameter("uinstitution"),
    			request.getParameter("ufaculty"),
    			request.getParameter("udept"),
    			request.getParameter("uinstitute"),
    			request.getParameter("utel"),
    			request.getParameter("ufax"),
    			request.getParameter("uemail"),
    			request.getParameter("umobile"));

    		MCRUserMgr manager = MCRUserMgr.instance();
    		
    		if (request.getParameter("uid_orig").equals("")){
    			// create new user
    			manager.createUser(user);
        		// encrypt the passwort if propertiy is set, only for new Users!!!, the others are crypted
        		manager.setPassword(user.getID(), user.getPassword());    			
    		}else{
    			// update user    			
    			manager.updateUser(user);
    		}
    		    		
    		response.sendRedirect(WebApplicationBaseURL + "nav?path=admin.usermanagement.user");
    	}
    	
      } catch ( Exception uErr ) {
    	  logger.error(uErr.getMessage(),uErr);
    	  return;
    }
    }
}
