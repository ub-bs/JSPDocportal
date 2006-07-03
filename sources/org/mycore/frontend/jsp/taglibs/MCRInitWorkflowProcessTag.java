package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;

import org.apache.log4j.Logger;
import org.mycore.access.MCRAccessManager;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRException;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManager;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManagerFactory;

public class MCRInitWorkflowProcessTag extends MCRSimpleTagSupport
{
    //input vars
	private String userid;
	private String workflowProcessType;
	private String transition;
	
	private String status;
	private String processidVar;
	
	private String scope;

	private static Logger logger = Logger.getLogger(MCRInitWorkflowProcessTag.class);
	private static String GUEST_ID = MCRConfiguration.instance().getString("MCR.users_guestuser_username","gast");

	public void setUserid(String userid){
		this.userid = userid;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}
		
	public void setWorkflowProcessType(String workfowProcessType){
		this.workflowProcessType = workfowProcessType;
	}
	
	public void setTransition(String transition){
		this.transition = transition;
	}
	
	public void setStatus(String status){
		this.status = status;
	}
	
	public void setProcessidVar(String processidVar){
		this.processidVar = processidVar;
	}	
	
	public void doTag() throws JspException, IOException {		
		JspContext jspContext = getJspContext();
		MCRWorkflowManager WFM = null;
    	if(scope == null)
    		scope = "page";
    	try {
			 WFM = MCRWorkflowManagerFactory.getImpl(workflowProcessType);
		} catch (MCRException noWFM) {
			logger.error("could not instantiate workflow manager", noWFM);
			jspContext.setAttribute(status, "errorWfM", getScope(scope));
			return;
		}			
		String perm = "create-" + WFM.getMainDocumentType();
		if ( !(MCRAccessManager.getAccessImpl().checkPermission(perm)) ){
			jspContext.setAttribute(status, "errorPermission", getScope(scope));
			return;
		}	
		
		try{
			long pid = WFM.initWorkflowProcess(userid, transition);
			jspContext.setAttribute(processidVar, String.valueOf(pid), getScope(scope));
			jspContext.setAttribute(status, WFM.getStatus(pid), getScope(scope));
		}catch(Exception noWFM){
			logger.error("could not initialize Workflow Process", noWFM);
			jspContext.setAttribute(status, "errorWfM", getScope(scope));
			return;			
		}
	}	  

}