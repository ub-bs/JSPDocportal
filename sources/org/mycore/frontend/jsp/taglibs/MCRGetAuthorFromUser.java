package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;

import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;

import org.apache.log4j.Logger;
import org.jdom.JDOMException;
import org.jdom.output.DOMOutputter;
import org.mycore.common.MCRConfiguration;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerFactory;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerInterface;

/**
 * 
 * @deprecated
 *
 */
public class MCRGetAuthorFromUser extends SimpleTagSupport
{
    //input vars
	private String userid;
	private String workflowProcessType;
	// output vars	
	private String var;
	private String status;
	private static String GUEST_ID = MCRConfiguration.instance().getString("MCR.users_guestuser_username","gast");
	private static Logger logger = Logger.getLogger(MCRGetAuthorFromUser.class);

	public void setUserid(String userid){
		this.userid = userid;
	}
	
	public void setWorkflowProcessType(String workfowProcessType){
		this.workflowProcessType = workfowProcessType;
	}
	
	public void setVar(String var){
		this.var = var;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public void doTag() throws JspException, IOException {		
		PageContext pageContext = (PageContext) getJspContext();
    	pageContext.setAttribute(var, null);
    	MCRWorkflowEngineManagerInterface WFM = null;
		try {
			 WFM = MCRWorkflowEngineManagerFactory.getImpl(workflowProcessType);
		} catch (Exception noWFM) {
			logger.error("could not build workflow interface", noWFM);
			pageContext.setAttribute(status, "errorWfM");
			return;
		}			
		if ( GUEST_ID.equals(userid) ){
			pageContext.setAttribute(status, "errorUserGuest");
			return;
		}									
    	String ID = WFM.getAuthorFromUniqueWorkflow(userid);   	
		pageContext.setAttribute(status, WFM.getStatus(userid));
		
	    if ( ID.length() > 0 ) {
	    	org.mycore.datamodel.metadata.MCRObject mcr_obj = new org.mycore.datamodel.metadata.MCRObject();
	    	mcr_obj.receiveFromDatastore(ID);		
	    	org.w3c.dom.Document domDoc = null;
	    	try {
	    		domDoc =  new DOMOutputter().output( mcr_obj.createXML());
	    		pageContext.setAttribute(var,domDoc);
	    	} catch (JDOMException e) {
	    		pageContext.setAttribute(status, "errorJDOMAuthor");
	    	}
	    } else { 
    		pageContext.setAttribute(status, "errorNoAuthor");
	    }	    
		return;
	}	  

}