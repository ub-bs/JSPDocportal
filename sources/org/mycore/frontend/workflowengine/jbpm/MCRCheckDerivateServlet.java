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

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.mycore.common.MCRSession;
import org.mycore.common.MCRSessionMgr;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.editor.MCREditorSubmission;
import org.mycore.frontend.editor.MCRRequestParameters;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.servlets.MCRServletJob;
import org.mycore.frontend.workflowengine.strategies.MCRWorkflowDirectoryManager;

/**
 * This class checks uploades files
 * 
 * @author Jens Kupferschmidt
 * @version $Revision$ $Date$
 */

public class MCRCheckDerivateServlet extends MCRServlet {

	private static final long serialVersionUID = 1L;
	private static Logger LOGGER = Logger.getLogger("MCRCheckDerivateServlet");
	
	/**
	 * This method overrides doGetPost of MCRServlet. <br />
	 */
	public void doGetPost(MCRServletJob job) throws Exception {
		HttpServletRequest request = job.getRequest();
		HttpServletResponse response = job.getResponse();
		// read the XML data
		MCREditorSubmission sub = (MCREditorSubmission) (job.getRequest()
				.getAttribute("MCREditorSubmission"));

		List files = sub.getFiles();

		// read the parameter
		MCRRequestParameters parms;
		if (sub == null)
			parms = new MCRRequestParameters(job.getRequest());
		else
			parms = sub.getParameters();
		String objid = parms.getParameter("mcrid2");
		String derid = parms.getParameter("mcrid");
		long processID = Long.parseLong(parms.getParameter("processid"));
		String type = parms.getParameter("type");
		String step = parms.getParameter("step");
		String nextPath = parms.getParameter("nextPath");
		if(nextPath == null) nextPath = "";
		
		LOGGER.debug("type = " + type);
		LOGGER.debug("step = " + step);
		LOGGER.debug("mcrid (derid)= " + derid);
		LOGGER.debug("mcrid2 (objid)= " + objid);		
		LOGGER.debug("nextPath = " + nextPath);
		
		// get the MCRSession object for the current thread from the session
		// manager.
		MCRSession mcrSession = MCRSessionMgr.getCurrentSession();
		String lang = mcrSession.getCurrentLanguage();		

		MCRWorkflowManager WFM;
		try{
			WFM = MCRWorkflowManagerFactory.getImpl(processID);
		}catch(IllegalStateException ex){
			request.setAttribute("messageKey", "WorkflowEngine.DocumentNotAvailable");
			request.setAttribute("lang", lang);
			request.getRequestDispatcher("/nav?path=~mycore-error").forward(request,response);
			return;
		}

		if (!AI.checkPermission(derid, "writedb" )) {
			request.setAttribute("messageKey", "WorkflowEngine.PrivilegesError");
			request.setAttribute("lang", lang);
			request.getRequestDispatcher("/nav?path=~mycore-error").forward(request,response);
			return;
		}
		String mylang = mcrSession.getCurrentLanguage();
		LOGGER.info("LANG = " + mylang);

		// prepare the disshab MCRObjectID of the document the derivate belongs to
		MCRObjectID ID = new MCRObjectID(objid);

		String workdir = MCRWorkflowDirectoryManager.getWorkflowDirectory(ID.getTypeId());
		
		String dirname = workdir + "/" + derid;
		if(nextPath.equals("")){
			nextPath = "~workflow-" + ID.getTypeId();
		}
		
		try{
			WFM.saveUploadedFiles(files, dirname, processID);
		}catch(Exception ex){
			request.setAttribute("messageKey", "WorkflowEngine.UploadNotSuccessful");
			request.setAttribute("lang", lang);
			request.getRequestDispatcher("/nav?path=~mycore-error").forward(request,response);
			return;
		}
		
		request.getRequestDispatcher("/nav?path=" + nextPath).forward(request,response);
		return;
	}

}
