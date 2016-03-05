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

import org.apache.log4j.Logger;
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

public class MCRWorkflowAccessRuleEditorServlet extends MCRServlet {
	protected static Logger logger = Logger.getLogger(MCRWorkflowAccessRuleEditorServlet.class);
	private static final long serialVersionUID = 1L;
    
	/**
	 * This method overrides doGetPost of MCRServlet. <br />
	 */
	public void doGetPost(MCRServletJob job) throws Exception {
		HttpServletRequest request = job.getRequest();
		// read the parameter
		MCRRequestParameters parms;
		parms = new MCRRequestParameters(request);
		String mcrid = parms.getParameter("id");
		String rule  = parms.getParameter("rule");
		String processid  = parms.getParameter("processid");		
		String[] selectedGroups  = parms.getParameterValues("selectedGroups");
		String path  = parms.getParameter("path");		
		
		if ( parms.getParameter("finish") != null) {
			MCRWorkflowAccessRuleEditorUtils.saveAccessRule(mcrid, rule, processid, selectedGroups);
			path = parms.getParameter("returnPath");
		}
		request.getRequestDispatcher("/nav?path=" + path).forward(request,job.getResponse());
		
	}
}	
