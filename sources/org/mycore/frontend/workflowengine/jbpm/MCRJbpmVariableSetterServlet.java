/*
 * $RCSfile$
 * $Revision$ $Date$
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

package org.mycore.frontend.workflowengine.jbpm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.servlets.MCRServletJob;

/**
 * This class sets Variables in a jbpm workflow process
 *  the variables must be defined in a 
 *  comma separated parameter <i>jbpmVariableNames</i> 
 * and is redirecting to the workflow page
 * 
 * @author Heiko Helmbrecht
 * @version $Revision$ $Date$
 */
public class MCRJbpmVariableSetterServlet extends MCRServlet {
	private static final long serialVersionUID = 1L;
	private static Logger LOGGER = Logger.getLogger(MCRJbpmVariableSetterServlet.class);
	private static MCRWorkflowEngineManagerInterface defaultWFI = MCRWorkflowEngineManagerFactory.getDefaultImpl();
		
    /**
     * This method overrides doGetPost of MCRServlet. <br />
     */
    public void doGetPost(MCRServletJob job) throws Exception {
    	HttpServletRequest request = job.getRequest();
    	HttpServletResponse response = job.getResponse();
    	
    	Map map = new HashMap();
    	String jbpmVariableNames = request.getParameter("jbpmVariableNames");
    	String processID = request.getParameter("processID");
    	String nextPath = request.getParameter("dispatcherForward");
    	
    	if(jbpmVariableNames != null && !jbpmVariableNames.equals("") && processID != null && !processID.equals("")){
    		long pid = Long.parseLong(processID);
    		String[] array = jbpmVariableNames.split(",");
    		for (int i = 0; i < array.length; i++) {
				String variableName = array[i];
				String variableValue = request.getParameter(variableName);
				LOGGER.debug("setting workflow variable " + variableName + "=" + variableValue);
				map.put(variableName, variableValue);
			}
    		defaultWFI.setStringVariables(map, pid);
    	}
    	
    	request.getRequestDispatcher(nextPath).forward(request, response);
    }
}
