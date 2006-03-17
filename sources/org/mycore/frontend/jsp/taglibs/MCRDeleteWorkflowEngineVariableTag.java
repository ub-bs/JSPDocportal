package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;

import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;

import org.apache.log4j.Logger;
import org.mycore.frontend.workflowengine.jbpm.MCRJbpmWorkflowObject;

public class MCRDeleteWorkflowEngineVariableTag extends SimpleTagSupport
{
	private String var;	
	private String pid;	
	private String workflowVar;
	
	private static Logger logger = Logger.getLogger(MCRDeleteWorkflowEngineVariableTag.class); 

	public void setPid(String pid) {
		this.pid = pid;
	}

	public void setVar(String var) {
		this.var = var;
	}

	public void setWorkflowVar(String workflowVar) {
		this.workflowVar = workflowVar;
	}	  
	
	public void doTag() throws JspException, IOException {
		PageContext pageContext = (PageContext) getJspContext();
		try {		
	    	MCRJbpmWorkflowObject wfo = new MCRJbpmWorkflowObject(Long.parseLong(pid));
	    	wfo.deleteVariable(workflowVar);
	    	pageContext.setAttribute(var, "variable.deleted");
		}catch (Exception e) {
			logger.error("could not fetch workflow variable", e);
	    	pageContext.setAttribute(var, "variable.deleted.error");
		}
	}






}