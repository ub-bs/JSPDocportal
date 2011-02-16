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
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;
import org.mycore.access.MCRAccessInterface;
import org.mycore.access.MCRAccessManager;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManager;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManagerFactory;

/**
 * 
 * @author Heiko Helmbrecht, Anja Schaar
 * @version $Revision$ $Date$
 */

public class MCRDeleteProcessTag extends SimpleTagSupport {
	private static Logger LOGGER = Logger.getLogger(MCRDeleteProcessTag.class.getName());
	private String result;
	private long pid;
	private String workflowProcessType;
	
	public void setworkflowProcessType(String workflowProcessType) {
		this.workflowProcessType = workflowProcessType;
	}
	public void setPid(long pid) {
		this.pid = pid;
	}
	
	public void setResult(String result) {
		this.result = result;
	}
	
	public void doTag() throws JspException, IOException {
		PageContext pageContext = (PageContext) getJspContext();
    	pageContext.setAttribute(result, "Webpage.admin.Process.deleted.successfull");

    	MCRWorkflowManager WFM = null;
		try {
			 WFM = MCRWorkflowManagerFactory.getImpl(workflowProcessType);
			 
		} catch (Exception noWFM) {
			LOGGER.error("could not create MCRWorkflowManager", noWFM);
			return;
		}  

    	if ( MCRAccessManager.checkPermission("administrate-" + WFM.getMainDocumentType()) ) {
			try{ 
				WFM.deleteWorkflowProcessInstance(pid);
			} catch (Exception allEx) {
		    	pageContext.setAttribute(result, "Webpage.admin.Process.deleted.error");	
		    	LOGGER.error("error:", allEx);
			}
    	} else {
	    	pageContext.setAttribute(result, "Webpage.admin.Process.deleted.norights");	    		
    	}
		return;		
	}
}

