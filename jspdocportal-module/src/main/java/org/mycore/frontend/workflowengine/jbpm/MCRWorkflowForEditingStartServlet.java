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

package org.mycore.frontend.workflowengine.jbpm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.mycore.access.MCRAccessManager;
import org.mycore.common.MCRSession;
import org.mycore.common.MCRSessionMgr;
import org.mycore.datamodel.metadata.MCRMetadataManager;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.MCRFrontendUtil;
import org.mycore.frontend.editor.MCRRequestParameters;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.servlets.MCRServletJob;


/**
 * This servlets put all metadata and derivates to the workflow directory and redirects 
 * to the next page  
 * @param mcrid, page in request 
 * 
 * @author Anja Schaar
 * @version $Revision$ $Date$
 */

public class MCRWorkflowForEditingStartServlet extends MCRServlet {
	protected static Logger logger = Logger.getLogger(MCRWorkflowForEditingStartServlet.class);
	private static final long serialVersionUID = 1L;
    private String mcrid;
    
	/**
	 * This method overrides doGetPost of MCRServlet. <br />
	 */
	public void doGetPost(MCRServletJob job) throws Exception {
		boolean bOK = false;
		HttpServletRequest request = job.getRequest();
		HttpServletResponse response = job.getResponse();
		// read the parameter
		MCRRequestParameters parms;
		parms = new MCRRequestParameters(request);
		mcrid = parms.getParameter("mcrid");
		MCRSession sessionFromRequest = MCRServlet.getSession(request);
		MCRSessionMgr.switchCurrentSession(sessionFromRequest);
		
		if (!MCRAccessManager.checkPermission(mcrid, "writedb" )) {
			String lang   = MCRSessionMgr.getCurrentSession().getCurrentLanguage();
			String usererrorpage = "nav?path=~mycore-error?messageKey=WF.common.PrivilegesError&lang=" + lang;
			logger.debug("Access denied for current user to start workflow for object " + mcrid);				
			response.sendRedirect(MCRFrontendUtil.getBaseURL() + usererrorpage);
			return;
		}
		
		logger.debug("Document MCRID = " + mcrid);
		
		if ( mcrid != null){
		    MCRObjectID mcrObjID = MCRObjectID.getInstance(mcrid);
		    MCRWorkflowManager wfm = MCRWorkflowManagerFactory.getImpl(mcrObjID);
		    if(wfm!=null && MCRMetadataManager.exists(mcrObjID) ) {
				bOK = true;
				//initiator, mcrid, transition name
				wfm.initWorkflowProcessForEditing(MCRSessionMgr.getCurrentSession().getUserInformation().getUserID(),	mcrid);	
				String url = "nav?path=~workflow-" + wfm.getWorkflowProcessType();
				logger.debug("nextpage = " + url);
				response.sendRedirect(response.encodeRedirectURL(MCRFrontendUtil.getBaseURL() + url));
			}
		}
		
		if ( !bOK) {
			String lang   = MCRSessionMgr.getCurrentSession().getCurrentLanguage();
			String usererrorpage = "mycore-error.jsp?messageKey=WF.xmetadiss.errorWfM&lang=" + lang;
			logger.debug("The document (to open for editing) is not in the database: " + mcrid);				
			response.sendRedirect(MCRFrontendUtil.getBaseURL() + usererrorpage);
		}
	}
}	
