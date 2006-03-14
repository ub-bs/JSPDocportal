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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;

import org.mycore.common.MCRSession;
import org.mycore.common.MCRSessionMgr;
import org.mycore.common.MCRUtils;
import org.mycore.datamodel.metadata.MCRDerivate;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.editor.MCREditorSubmission;
import org.mycore.frontend.editor.MCRRequestParameters;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.servlets.MCRServletJob;

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
		String objid = parms.getParameter("mcrid");
		String derid = parms.getParameter("mcrid2");
		String type = parms.getParameter("type");
		String step = parms.getParameter("step");
		String nextPath = parms.getParameter("nextPath");
		if(nextPath == null) nextPath = "";
		
		LOGGER.debug("mcrid (objid)= " + objid);
		LOGGER.debug("type = " + type);
		LOGGER.debug("step = " + step);
		LOGGER.debug("mcrid2 (derid)= " + derid);
		LOGGER.debug("nextPath = " + nextPath);

		List lpids = MCRJbpmWorkflowBase.getCurrentProcessIDsForProcessVariable("createdDocID", objid);
		MCRJbpmWorkflowObject wfo = new MCRJbpmWorkflowObject(((Long)lpids.get(0)).longValue());

		// get the MCRSession object for the current thread from the session
		// manager.
		MCRSession mcrSession = MCRSessionMgr.getCurrentSession();
		String lang = mcrSession.getCurrentLanguage();

		if (!AI.checkPermission(derid, "writedb" )) {
			request.setAttribute("messageKey", "SWF.PrivilegesError");
			request.setAttribute("lang", lang);
			request.getRequestDispatcher("/nav?path=~mycore-error").forward(request,response);
			return;
		}
		String mylang = mcrSession.getCurrentLanguage();
		LOGGER.info("LANG = " + mylang);

		// prepare the derivate MCRObjectID
		MCRObjectID ID = new MCRObjectID(derid);

		String workdir = wfo.getCurrentWorkflowManager().getWorkflowDirectory(ID.getTypeId());
		
		String dirname = workdir + File.separator + objid;
		if(nextPath.equals("")){
			nextPath = "~workflow-" + ID.getTypeId();
		}		

		// save the files

		ArrayList ffname = new ArrayList();
		String mainfile = "";
		for (int i = 0; i < files.size(); i++) {
			FileItem item = (FileItem) (files.get(i));
			String fname = item.getName().trim();
			int j = 0;
			int l = fname.length();
			while (j < l) {
				int k = fname.indexOf("\\", j);
				if (k == -1) {
					k = fname.indexOf("/", j);
					if (k == -1) {
						fname = fname.substring(j, l);
						break;
					} else {
						j = k + 1;
					}
				} else {
					j = k + 1;
				}
			}
			fname.replace(' ', '_');
			ffname.add(fname);
			File fout = new File(dirname, fname);
			FileOutputStream fouts = new FileOutputStream(fout);
			MCRUtils.copyStream(item.getInputStream(), fouts);
			fouts.close();
			LOGGER.info("Data object stored under " + fout.getName());
		}
		if ((mainfile.length() == 0) && (ffname.size() > 0)) {
			mainfile = (String) ffname.get(0);
		}

		// add the mainfile entry
		MCRDerivate der = new MCRDerivate();
		try {
			der.setFromURI(dirname + ".xml");
			if (der.getDerivate().getInternals().getMainDoc().equals("#####")) {
				der.getDerivate().getInternals().setMainDoc(mainfile);
				byte[] outxml = MCRUtils.getByteArray(der.createXML());
				try {
					FileOutputStream out = new FileOutputStream(dirname
							+ ".xml");
					out.write(outxml);
					out.flush();
				} catch (IOException ex) {
					LOGGER.error(ex.getMessage());
					LOGGER.error("Exception while store to file " + dirname
							+ ".xml");
				}
			}
		} catch (Exception e) {
			LOGGER.warn("Can't open file " + dirname + ".xml");
		}
		// TODO check uploaded data via workflow-specific implementations
		//wfo.getCurrentWorkflowManager().

		request.getRequestDispatcher("/nav?path=" + nextPath).forward(request,response);
		return;
	}

}
