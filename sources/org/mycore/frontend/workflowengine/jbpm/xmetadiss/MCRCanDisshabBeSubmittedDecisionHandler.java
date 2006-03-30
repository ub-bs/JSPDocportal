package org.mycore.frontend.workflowengine.jbpm.xmetadiss;

import org.apache.log4j.Logger;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.node.DecisionHandler;
import org.mycore.common.MCRException;
import org.mycore.frontend.workflowengine.jbpm.MCRJbpmWorkflowBase;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerFactory;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerInterface;

public class MCRCanDisshabBeSubmittedDecisionHandler implements DecisionHandler {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(MCRCanDisshabBeSubmittedDecisionHandler.class);
	private static MCRWorkflowEngineManagerInterface WFI = MCRWorkflowEngineManagerFactory.getImpl("xmetadiss");
	
	/**
	* decides which transition to take. returnes the name of this transition
	*/
	public String decide(ExecutionContext executionContext) {
		
		long pid = executionContext.getContextInstance().getProcessInstance().getId();
		String decisionNode = executionContext.getNode().getName();
		logger.debug("checking boolean decision node [" + decisionNode + "] for pid [" + pid + "]");
		return WFI.checkDecisionNode(pid, decisionNode);
	}
}
