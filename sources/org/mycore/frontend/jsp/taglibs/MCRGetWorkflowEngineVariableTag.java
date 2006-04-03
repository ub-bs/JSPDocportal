package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;

import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;

import org.apache.log4j.Logger;
import org.mycore.frontend.workflowengine.jbpm.MCRJbpmWorkflowObject;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerFactory;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerInterface;

public class MCRGetWorkflowEngineVariableTag extends SimpleTagSupport
{
	private String var;	
	private long pid;	
	private String workflowVar;
	
	private static Logger logger = Logger.getLogger(MCRGetWorkflowEngineVariableTag.class);
	private static MCRWorkflowEngineManagerInterface defaultWFI = MCRWorkflowEngineManagerFactory.getDefaultImpl();

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
	    	pageContext.setAttribute(var, defaultWFI.getStringVariable(workflowVar, pid));
		}catch (Exception e) {
			logger.error("could not fetch workflow variable", e);
		}
	}






}