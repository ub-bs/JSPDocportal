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

package org.mycore.frontend.servlets;

import java.io.FileOutputStream;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import org.mycore.common.MCRSession;
import org.mycore.common.MCRSessionMgr;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.datamodel.metadata.MCRObjectStructure;
import org.mycore.frontend.cli.MCRDerivateCommands;
import org.mycore.frontend.editor.MCRRequestParameters;


/**
 * 
 * @author Anja Schaar
 * @version $Revision$ $Date$
 */

public class MCRPutDocumentToWorkflow extends MCRCheckBase {
	protected static Logger logger = Logger.getLogger(MCRPutDocumentToWorkflow.class);
	private static final long serialVersionUID = 1L;
    private String mcrid;
    

	/**
	 * This method overrides doGetPost of MCRServlet. <br />
	 */
	public void doGetPost(MCRServletJob job) throws Exception {
		HttpServletRequest request = job.getRequest();
		HttpServletResponse response = job.getResponse();
		// read the parameter
		MCRRequestParameters parms;
		parms = new MCRRequestParameters(request);
		mcrid = parms.getParameter("mcrid");
		logger.debug("Document MCRID = " + mcrid);
		String url = parms.getParameter("page");
		MCRSession mcrSession = MCRSessionMgr.getCurrentSession();

		String lang   = mcrSession.getCurrentLanguage();
		String userid = mcrSession.getCurrentUserID();
		String usererrorpage = "mycore-error.jsp?messageKey=SWF.PrivilegesError&lang=" + lang;
		if (!AI.checkPermission(mcrid, "writedb" )) {
			logger.debug("Access denied for Current user for create = " + userid);				
			response.sendRedirect(getBaseURL() + usererrorpage);
			return;
		}		
		
		if ( mcrid != null && MCRObject.existInDatastore(mcrid) ) {
			// Store Object in Workflow - Filesystem
			MCRObject mob = new MCRObject();
			mob.receiveFromDatastore(mcrid);
			String type = mob.getId().getTypeId();
			String savedir = CONFIG.getString("MCR.editor_" + type + "_directory");
			MCRPutDocumentToWorkflow.saveToDirectory(mob, savedir);			
		}		
		response.sendRedirect(response.encodeRedirectURL(getBaseURL() + url));
	}

	public String getNextURL(MCRObjectID ID) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public void sendMail(MCRObjectID ID) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	// static method to save any Document Object to an give directory - uses to put idt into
	// the workflow directory or to save it before deleteing - look to MCRStartEditorServlet - sdelobj
	public static void	saveToDirectory(MCRObject mob, String savedir){
	
		MCRObjectStructure structure = mob.getStructure();
		String mcrid = mob.getId().getId();
		int derSize = structure.getDerivateSize();
		FileOutputStream fos =null;
		
		for(int i = 0; i < derSize; i++) {
			String derivateID = structure.getDerivate(i).getXLinkHref();
	        String derDir  = savedir ;
	        if ( derivateID != null && MCRObject.existInDatastore(derivateID) ) {
		        MCRDerivateCommands.show(derivateID, derDir);
	        }				
		}
		for(int i = 0; i < derSize; i++) {
			structure.removeDerivate(0);
		}	
		try {
			fos = new FileOutputStream(savedir + "/" + mcrid + ".xml");
			(new XMLOutputter(Format.getPrettyFormat())).output(mob.createXML(),fos);
			fos.close();
		} catch (Exception ex){
			logger.debug(ex);
			logger.info("Cant save Object" + mcrid + " to directory " + savedir);
			;
		} finally{
			if ( fos != null ){
				try {		fos.close(); }			
				catch ( IOException io ) {; // cant clos the fos
				}
			}
		}
	 }
}
