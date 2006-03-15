package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;

import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;

import org.apache.log4j.Logger;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerFactory;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerInterface;

public class MCRGetWorkflowDocumentID extends SimpleTagSupport
{
	private String userid;	
	private String workflowProcessType;	
	private String mcrid;
	private String status;
	private String valid;
	
	private static Logger logger = Logger.getLogger(MCRGetWorkflowDocumentID.class); 


	public void setStatus(String status) {
		this.status = status;
	}

	public void setMcrid(String mcrid) {
		this.mcrid = mcrid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public void setWorkflowProcessType(String workfowProcessType){
		this.workflowProcessType = workfowProcessType;
	}	
	
	public void setValid(String valid){
		this.valid = valid;
	}
	
	public void doTag() throws JspException, IOException {	
		try{
			PageContext pageContext = (PageContext) getJspContext();
			pageContext.setAttribute(mcrid, "");
    	
			MCRWorkflowEngineManagerInterface WFM = null;
			try {
				WFM = MCRWorkflowEngineManagerFactory.getImpl(workflowProcessType);
			} catch (Exception noWFM) {
				logger.error("could not build workflow interface", noWFM);
				pageContext.setAttribute(status, "errorWfM");
				return;
			}
			String smcrid = WFM.getMetadataDocumentID(userid);
			pageContext.setAttribute(mcrid, smcrid);
			if ( smcrid.length() <= 0 ) {
				pageContext.setAttribute(status, "errorNoDocument");
				pageContext.setAttribute(valid, "false");
			}
			else {
				pageContext.setAttribute(status, WFM.getStatus(userid));
				pageContext.setAttribute(valid, Boolean.toString(WFM.checkMetadataValidFlag(smcrid)));
			}
			return;
		}catch(Exception e){
			logger.error("could not get workflow document id", e);
		}
	}	  

}