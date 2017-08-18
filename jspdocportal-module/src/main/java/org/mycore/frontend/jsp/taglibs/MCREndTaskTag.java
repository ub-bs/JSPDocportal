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

// package
package org.mycore.frontend.jsp.taglibs;

// Imported java classes
import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManager;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManagerFactory;

/**
 * 
 * @author Heiko Helmbrecht
 * @version $Revision$ $Date$
 */

public class MCREndTaskTag extends MCRSimpleTagSupport {
	private static Logger LOGGER = LogManager.getLogger(MCREndTaskTag.class);
	
	private String success;
	private long processID;
	private String taskName;
	private String transition;
	
	public void setProcessID(long processID) {
		this.processID = processID;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	
	public void setTransition(String transition){
		this.transition = transition;
	}
	

	public void doTag() throws JspException, IOException {
		if(transition == null) transition = "";
		try{
			MCRWorkflowManager WFM = MCRWorkflowManagerFactory.getImpl(processID);
			boolean result = WFM.endTask(processID,taskName,transition);
			getJspContext().setAttribute(success, new Boolean(result), getScope("page"));
		}catch(Exception e){
			LOGGER.error("stacktrace", e);
		}
	}
}

