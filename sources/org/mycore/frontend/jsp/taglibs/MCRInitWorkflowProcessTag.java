package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;

import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;

import org.apache.log4j.Logger;
import org.mycore.common.MCRConfiguration;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerFactory;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerInterface;

public class MCRInitWorkflowProcessTag extends SimpleTagSupport
{
    //input vars
	private String userid;
	private String workflowProcessType;
	
	private String status;

	private static Logger logger = Logger.getLogger(MCRInitWorkflowProcessTag.class);
	private static String GUEST_ID = MCRConfiguration.instance().getString("MCR.users_guestuser_username","gast");

	public void setUserid(String userid){
		this.userid = userid;
	}
	
	public void setWorkflowProcessType(String workfowProcessType){
		this.workflowProcessType = workfowProcessType;
	}
	
	public void setStatus(String status){
		this.status = status;
	}
	
	public void doTag() throws JspException, IOException {		
		PageContext pageContext = (PageContext) getJspContext();
    	MCRWorkflowEngineManagerInterface WFM = null;
		try {
			 WFM = MCRWorkflowEngineManagerFactory.getImpl(workflowProcessType);
		} catch (Exception noWFM) {
			logger.error("could not instantiate workflow manager", noWFM);
			pageContext.setAttribute(status, "errorWfM");
			return;
		}			
		if ( GUEST_ID.equals(userid) ){
			pageContext.setAttribute(status, "errorUserGuest");
			return;
		}	
		try{
			WFM.initWorkflowProcess(userid);
		}catch(Exception noWFM){
			logger.error("could not initialize Workflow Process", noWFM);
			pageContext.setAttribute(status, "errorWfM");
			return;			
		}
	}	  

}