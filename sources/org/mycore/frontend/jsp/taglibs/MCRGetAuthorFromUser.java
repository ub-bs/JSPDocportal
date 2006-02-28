package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.DOMOutputter;
import org.mycore.access.MCRAccessInterface;
import org.mycore.access.MCRAccessManager;
import org.mycore.common.JSPUtils;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRException;
import org.mycore.datamodel.metadata.MCRXMLTableManager;
import org.mycore.frontend.workflow.MCRDisshabWorkflowManager;
import org.mycore.services.nbn.MCRNBN;
import org.mycore.user2.MCRUser;
import org.mycore.user2.MCRUserMgr;


public class MCRGetAuthorFromUser extends SimpleTagSupport
{
    //input vars
	private String userid;
	// output vars	
	private String var;
	private String status;
	private static MCRConfiguration CONFIG = MCRConfiguration.instance();
	private static MCRDisshabWorkflowManager dhwf; 

	public void setUserid(String inputUserid){
		userid = inputUserid;
	}
	
	public void setVar(String inputVar){
		var = inputVar;
	}

	public void setStatus(String inputStatus){
		status = inputStatus;
	}

	public void doTag() throws JspException, IOException {		
		PageContext pageContext = (PageContext) getJspContext();
    	pageContext.setAttribute(var, null);
    	
		String GUEST_ID = CONFIG.getString("MCR.users_guestuser_username","gast");
		try {
			dhwf = MCRDisshabWorkflowManager.instance();
		} catch (Exception noWfM) {
			pageContext.setAttribute(status, "errorWfM");
			return;
		}			
		if ( GUEST_ID.equals(userid) ){
			pageContext.setAttribute(status, "errorUserGuest");
			return;
		}									
    	String ID = dhwf.getDisshabAuthor(userid);   	
		pageContext.setAttribute(status,dhwf.getActualStatus());
		
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