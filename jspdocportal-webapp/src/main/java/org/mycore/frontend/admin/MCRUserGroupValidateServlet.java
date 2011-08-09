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

import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.servlets.MCRServletJob;
import org.mycore.user.MCRGroup;
import org.mycore.user.MCRUserMgr;

/**
 * This class is the superclass of servlets which checks the MCREditorServlet
 * output XML for metadata object and derivate objects.
 * 
 * @author Heiko Helmbrecht
 * @author Arne Seifert
 * @version $Revision$ $Date$
 */

public class MCRUserGroupValidateServlet extends MCRServlet {

	private static final long serialVersionUID = 1L;
	protected static Logger logger = Logger.getLogger(MCRUserGroupValidateServlet.class);

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

    	String groupid = "";
    	if (request.getParameter("gid_orig")!=null)
    		groupid = request.getParameter("gid_orig");
    	MCRGroup group = null;
        logger.debug("editing group " + groupid);
    	

    try{
	    	if (groupid.equals("-")){
	    		// create new group
	    		group =  new MCRGroup(request.getParameter("gid"), request.getParameter("creator"), null, null, request.getParameter("description"), new ArrayList(), new ArrayList(), new ArrayList());
	    		MCRUserMgr.instance().createGroup(group);
	    		operation="detail";
	    		groupid=request.getParameter("gid");
	    	}
	
	    	if(operation.equals("detail")){
	    		Enumeration paramNames = request.getParameterNames();
	    		while(paramNames.hasMoreElements()) {
	    			paramName = (String)paramNames.nextElement();
	    			if(paramName.indexOf(".x")!=-1){
	    				break;
	    			}
	    		}
	
	    		String val = "";
	    		String op = "";
	    		if (paramName!="" && paramName.indexOf(".x")!=-1){
	    			val = paramName.substring(0,paramName.indexOf(".x"));
	    			op = val.substring(0,1);
	    			if (groupid==null && val.length()>0)
	    				groupid = val.substring(1);
	    		}
	
	    		if (op.equals("e")){
	    			// edit
	    			response.sendRedirect(WebApplicationBaseURL + "nav?path=admin.usermanagement.usergroup.edit&id=" + val.substring(1));
	    		}else if (op.equals("d")){
	    			// delete
	    			MCRUserMgr.instance().deleteGroup(val.substring(1));
	    			response.sendRedirect(WebApplicationBaseURL + "nav?path=admin.usermanagement.usergroup");
	    		}else if(op.equals("n")){
	    			// new
	    			response.sendRedirect(WebApplicationBaseURL + "nav?path=admin.usermanagement.usergroup.edit&operation=new");
	    		}else{
	
	    			group = MCRUserMgr.instance().retrieveGroup(groupid);
	    			String[] values = null;
	    			ArrayList templ = null;
	
	    			// admingroup
	    			logger.debug(" save admingroup");
	    			templ = group.getAdminGroupIDs();
	    			while(templ.size() > 0){
	    				group.removeAdminGroupID((String) templ.get(0));
	    			}
	    			if ( request.getParameterValues("admingroup")!=null){
	    				values = request.getParameterValues("admingroup");
	    				for(int i=0; i< values.length; i++){
	    					group.addAdminGroupID(values[i]);
	    				}
	    			}
	
	    			// adminuser
	    			logger.debug(" save adminuser");
	    			templ = group.getAdminUserIDs();
	    			for (int i=0; i< templ.size(); i++){
	    				group.removeAdminUserID((String) templ.get(i));
	    			}
	    			if(request.getParameterValues("adminuser")!=null){
	    				values = request.getParameterValues("adminuser");
	    				for(int i=0; i< values.length; i++){
	    					group.addAdminUserID(values[i]);
	    				}
	    			}
	
	    			// memberuser
	    			logger.debug(" save memberuser");
	    			templ = group.getMemberUserIDs();
	    			while(templ.size() > 0){
	    				logger.debug("removed user " + (String) templ.get(0));
	    				group.removeMemberUserID((String) templ.get(0));
	    			}
	    			if (request.getParameterValues("memberuser")!=null){
	    				values = request.getParameterValues("memberuser");
	    				for(int i=0; i< values.length; i++){
	    					group.addMemberUserID(values[i]);
	    					logger.debug("added user " + values[i]);
	    				}
	    			}
	    			
	    			 
	    			// Name of a group is not editable
	    			group.setDescription(request.getParameter("description"));
	    			MCRUserMgr.instance().updateGroup(group);
	    			response.sendRedirect(WebApplicationBaseURL + "nav?path=admin.usermanagement.usergroup");
	    		}
	    	}
	    }catch(Exception e){
	    	logger.error("user validate error", e);
	    }
    }
}
