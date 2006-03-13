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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.DOMOutputter;
import org.mycore.access.MCRAccessInterface;
import org.mycore.access.MCRAccessManager;
import org.mycore.common.JSPUtils;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRDefaults;
import org.mycore.common.MCRSession;
import org.mycore.common.MCRSessionMgr;
import org.mycore.common.MCRUtils;
import org.mycore.common.xml.MCRURIResolver;
import org.mycore.common.xml.MCRXMLHelper;
import org.mycore.frontend.jsp.format.MCRResultFormatter;
import org.mycore.frontend.workflow.MCRWorkflowManager;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerFactory;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerInterface;

/**
 * 
 * @author Heiko Helmbrecht, Anja Schaar
 * @version $Revision$ $Date$
 */

public class MCRListWorkflowProcessTag extends SimpleTagSupport {
	private static Logger LOGGER = Logger.getLogger(MCRListWorkflowProcessTag.class.getName());
	private String var;
	private String userid;
	private String workflowProcessType;	
	private String status;

	public void setUserid(String userid){
		this.userid = userid;
	}
	
	public void setWorkflowProcessType(String workfowProcessType){
		this.workflowProcessType = workfowProcessType;
	}
	
	public void setStatus(String status){
		this.status = status;
	}

	public void setVar(String inputVar) {
		this.var = inputVar;
		return;
	}

	
	public void doTag() throws JspException, IOException {
		PageContext pageContext = (PageContext) getJspContext();
    	pageContext.setAttribute(var, "");
    	
    	MCRWorkflowEngineManagerInterface WFM = null;
		try {
			 WFM = MCRWorkflowEngineManagerFactory.getImpl(workflowProcessType);
		} catch (Exception noWFM) {
			LOGGER.error("could not build workflow interface", noWFM);
			pageContext.setAttribute(status, "errorWfM");
			return;
		} 
		org.jdom.Document workflow_doc = WFM.getListWorkflowProcess(userid,workflowProcessType);
		LOGGER.debug(JSPUtils.getPrettyString(workflow_doc));
		org.w3c.dom.Document domDoc = null;
		try {
			domDoc = new DOMOutputter().output(workflow_doc);
		} catch (JDOMException e) {
			LOGGER.error("Domoutput failed: ", e);
			pageContext.setAttribute(status, "errorNoWorkflowList");
		}
		pageContext.setAttribute(var, domDoc);
		if(pageContext.getAttribute("debug") != null && pageContext.getAttribute("debug").equals("true")) {
			JspWriter out = pageContext.getOut();
			StringBuffer debugSB = new StringBuffer("<textarea cols=\"100\" rows=\"30\">")
				.append("this is the jdom for the browse-control delivered by mcr:MCRListWorkflowCtrlTag\r\n")
				.append(JSPUtils.getPrettyString(workflow_doc))
				.append("</textarea>");
			out.println(debugSB.toString());
		}        
		pageContext.setAttribute(status, WFM.getStatus(userid));
		return;
		
	}

}

