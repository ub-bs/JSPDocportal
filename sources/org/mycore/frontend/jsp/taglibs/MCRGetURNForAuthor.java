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


public class MCRGetURNForAuthor extends SimpleTagSupport
{
	private String authorid;	
	private String urn;
	private String status;
	private static MCRConfiguration CONFIG = MCRConfiguration.instance();
	private static MCRDisshabWorkflowManager dhwf; 

	public void setAuthorid(String inputAuthorid){
		authorid = inputAuthorid;
	}
	public void setStatus(String inputStatus){
		status = inputStatus;
	}
	public void setUrn(String inputUrn){
		urn = inputUrn;
	}
	
	
	public void doTag() throws JspException, IOException {		
		PageContext pageContext = (PageContext) getJspContext();
    	pageContext.setAttribute(urn, "");
    	
		try {
			dhwf = MCRDisshabWorkflowManager.instance();
		} catch (Exception noWfM) {
			pageContext.setAttribute(status, "errorWfM");
			return;
		}			
	    if ( authorid.length() > 0 ) {
		    String surn = dhwf.getURNReservationForAuthor(authorid);
		    pageContext.setAttribute(urn, surn);
		    pageContext.setAttribute(status, "urnCreated");
	    } else { 
    		pageContext.setAttribute(status, "errorNoAuthor");
	    }	    	    	    
		return;
	}	  

}