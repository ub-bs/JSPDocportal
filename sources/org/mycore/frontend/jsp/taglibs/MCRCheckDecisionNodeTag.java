package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManager;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManagerFactory;

public class MCRCheckDecisionNodeTag extends MCRSimpleTagSupport
{
	private static Logger logger = Logger.getLogger(MCRCheckDecisionNodeTag.class);
	
	private String var;
	private long processID;
	private String decision;
	private String workflowType;
	
	
	public void setDecision(String decision) {
		this.decision = decision;
	}

	public void setVar(String var) {
		this.var = var;
	}
	
	public void setProcessID(long processID){
		this.processID = processID;
	}

	public void setWorkflowType(String workflowType) {
		this.workflowType = workflowType;
	}


	public void doTag() throws JspException, IOException {
		try{
			PageContext pageContext = (PageContext) getJspContext();
			MCRWorkflowManager WFM = MCRWorkflowManagerFactory.getImpl(workflowType);
			pageContext.setAttribute(var, WFM.checkDecisionNode(processID, decision, null));
		}catch(Exception e){
			logger.error("could not check boolean decision node [" + 
					decision + "] for processid [" + processID + "]");
		}
	}	

}