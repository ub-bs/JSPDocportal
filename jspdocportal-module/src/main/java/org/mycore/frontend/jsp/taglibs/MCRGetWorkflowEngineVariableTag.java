package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mycore.frontend.workflowengine.jbpm.MCRJbpmWorkflowBase;

public class MCRGetWorkflowEngineVariableTag extends SimpleTagSupport
{
	private static Logger logger = LogManager.getLogger(MCRGetWorkflowEngineVariableTag.class);
	
	private String var;	
	private long pid;	
	private String workflowVar;
	
	public void setPid(long pid) {
		this.pid = pid;
	}

	public void setVar(String var) {
		this.var = var;
	}

	public void setWorkflowVar(String workflowVar) {
		this.workflowVar = workflowVar;
	}	  
	
	public void doTag() throws JspException, IOException {
		try {		
			PageContext pageContext = (PageContext) getJspContext();
			String value = MCRJbpmWorkflowBase.getStringVariable(workflowVar, pid);
	    	pageContext.setAttribute(var, value);
		}catch (Exception e) {
			logger.error("could not fetch workflow variable", e);
		}
	}






}