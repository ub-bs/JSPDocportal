package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;
import java.util.HashSet;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerFactory;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerInterface;

public class MCRDeleteWorkflowEngineVariableTag extends SimpleTagSupport
{
	private String var;	
	private long pid;	
	private String workflowVar;
	
	private static Logger logger = Logger.getLogger(MCRDeleteWorkflowEngineVariableTag.class);
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
		PageContext pageContext = (PageContext) getJspContext();
		try {	
			HashSet set = new HashSet();
			set.add(workflowVar);
			defaultWFI.deleteWorkflowVariables(set, pid);
	    	pageContext.setAttribute(var, "variable.deleted");
		}catch (Exception e) {
			logger.error("could not fetch workflow variable", e);
	    	pageContext.setAttribute(var, "variable.deleted.error");
		}
	}






}