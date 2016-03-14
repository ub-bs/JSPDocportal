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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * This class sets Variables in a jbpm workflow process
 *  the variables must be defined in a 
 *  comma separated parameter <i>jbpmVariableNames</i> 
 * and is redirecting to the workflow page
 * 
 * @author Heiko Helmbrecht
 * @version $Revision$ $Date$
 */
public class MCRJbpmVariableSetterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger LOGGER = Logger.getLogger(MCRJbpmVariableSetterServlet.class);
		
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    	
    	@SuppressWarnings("rawtypes")
		Map map = new HashMap();
    	String jbpmVariableNames = request.getParameter("jbpmVariableNames");
    	String strProcessID = request.getParameter("processID");
    	String nextPath = request.getParameter("dispatcherForward");
    	
    	if(jbpmVariableNames != null && !jbpmVariableNames.equals("") && strProcessID != null && !strProcessID.equals("")){
    		long processid = Long.parseLong(strProcessID);
    		String[] array = jbpmVariableNames.split(",");
    		for (int i = 0; i < array.length; i++) {
				String variableName = array[i];
				String variableValue = request.getParameter(variableName);
				variableName = variableName.replaceAll("/","");
				LOGGER.debug("setting workflow variable " + variableName + "=" + variableValue);
				map.put(variableName, variableValue);
			}
    		
    		MCRWorkflowProcess wfp = MCRWorkflowProcessManager.getInstance().getWorkflowProcess(processid);
    		wfp.setStringVariables(map);
    		wfp.close();
    	}
    	
    	request.getRequestDispatcher(nextPath).forward(request, response);
    }
}
