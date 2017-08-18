package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManager;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowProcess;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowProcessManager;

public class MCRCheckDecisionNodeTag extends MCRSimpleTagSupport
{
	private static Logger LOGGER = LogManager.getLogger(MCRCheckDecisionNodeTag.class);
	
	private String var;
	private long processID;
	private String decision;
//	private String workflowType;
	
	
	public void setDecision(String decision) {
		this.decision = decision;
	}

	public void setVar(String var) {
		this.var = var;
	}
	
	public void setProcessID(long processID){
		this.processID = processID;
	}

	//no longer needed
	public void setWorkflowType(String workflowType) {
	//	this.workflowType = workflowType;
	}


	public void doTag() throws JspException, IOException {
		try{
			PageContext pageContext = (PageContext) getJspContext();
			MCRWorkflowProcess wfp = MCRWorkflowProcessManager.getInstance().getWorkflowProcess(processID);
			MCRWorkflowManager WFM = wfp.getCurrentWorkflowManager();
			pageContext.setAttribute(var, WFM.checkDecisionNode(decision, wfp.getContextInstance()));
			wfp.close();
		}catch(Exception e){
			LOGGER.error("could not check boolean decision node [" + 
					decision + "] for processid [" + processID + "]");
		}
	}	

}