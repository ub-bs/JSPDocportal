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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;

import org.jbpm.graph.exe.ProcessInstance;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.DOMOutputter;
import org.mycore.access.MCRAccessInterface;
import org.mycore.access.MCRAccessManager;
import org.mycore.common.JSPUtils;
import org.mycore.common.MCRDefaults;
import org.mycore.frontend.workflowengine.jbpm.MCRJbpmCommands;
import org.mycore.frontend.workflowengine.jbpm.MCRJbpmWorkflowBase;
import org.mycore.frontend.workflowengine.jbpm.MCRJbpmWorkflowObject;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerFactory;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerInterface;

/**
 * 
 * @author Heiko Helmbrecht, Anja Schaar
 * @version $Revision$ $Date$
 */

public class MCRDeleteProcessTag extends SimpleTagSupport {
	private static Logger LOGGER = Logger.getLogger(MCRDeleteProcessTag.class.getName());
	private String result;
	private String pid;
	private String workflowProcessType;
	
	private static MCRAccessInterface AI = MCRAccessManager.getAccessImpl();

	public void setworkflowProcessType(String workflowProcessType) {
		this.workflowProcessType = workflowProcessType;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	
	public void setResult(String result) {
		this.result = result;
	}
	
	public void doTag() throws JspException, IOException {
		PageContext pageContext = (PageContext) getJspContext();
    	pageContext.setAttribute(result, "Admin.Process.deleted.successfull");

    	if ( AI.checkPermission("administrate-" + workflowProcessType) ) {
			try{ 
				MCRJbpmCommands.deleteProcess(pid);
			} catch (java.lang.NullPointerException noObject) {
		    	pageContext.setAttribute(result, "Admin.Process.deleted.noprocess");				
			} catch (Exception allEx) {
		    	pageContext.setAttribute(result, "Admin.Process.deleted.error");	
		    	LOGGER.error("error:", allEx);
			}
    	} else {
	    	pageContext.setAttribute(result, "Admin.Process.deleted.norights");	    		
    	}
		return;		
	}
}

