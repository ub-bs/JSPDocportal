package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;

import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;

import org.apache.log4j.Logger;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerFactory;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerInterface;

public class MCRGetURNForAuthor extends SimpleTagSupport
{
	private String userid;	
	private String workflowProcessType;	
	private String urn;
	private String status;
	
	private static Logger logger = Logger.getLogger(MCRGetAuthorFromUser.class); 


	public void setStatus(String status) {
		this.status = status;
	}

	public void setUrn(String urn) {
		this.urn = urn;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public void setWorkflowProcessType(String workfowProcessType){
		this.workflowProcessType = workfowProcessType;
	}	
	
	public void doTag() throws JspException, IOException {		
		PageContext pageContext = (PageContext) getJspContext();
    	pageContext.setAttribute(urn, "");
    	
    	MCRWorkflowEngineManagerInterface WFM = null;
		try {
			 WFM = MCRWorkflowEngineManagerFactory.getImpl(workflowProcessType);
		} catch (Exception noWFM) {
			logger.error("could not build workflow interface", noWFM);
			pageContext.setAttribute(status, "errorWfM");
			return;
		}
		String surn = WFM.getURNReservation(userid);
	    pageContext.setAttribute(urn, surn);
	    if ( surn.length() <= 0 ) 
    		pageContext.setAttribute(status, "errorNoAuthor");
	    else 
			pageContext.setAttribute(status, WFM.getStatus(userid));
		return;
	}	  

}