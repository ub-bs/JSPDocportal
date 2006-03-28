package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;

import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;

import org.apache.log4j.Logger;
import org.mycore.frontend.workflowengine.jbpm.MCRJbpmWorkflowObject;

public class MCRSetWorkflowEngineVariableTag extends SimpleTagSupport
{
	private String value;	
	private long pid;	
	private String workflowVar;
	
	private static Logger logger = Logger.getLogger(MCRSetWorkflowEngineVariableTag.class); 

	public void setPid(long pid) {
		this.pid = pid;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setWorkflowVar(String workflowVar) {
		this.workflowVar = workflowVar;
	}	  
	
	public void doTag() throws JspException, IOException {
		try {		
	    	MCRJbpmWorkflowObject wfo = new MCRJbpmWorkflowObject(pid);
	    	wfo.setStringVariableValue(workflowVar, value);
		}catch (Exception e) {
			logger.error("could not fetch workflow variable", e);
		}
	}






}