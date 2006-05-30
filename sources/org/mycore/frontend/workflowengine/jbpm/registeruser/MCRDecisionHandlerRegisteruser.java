package org.mycore.frontend.workflowengine.jbpm.registeruser;

import org.apache.log4j.Logger;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.node.DecisionHandler;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManager;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManagerFactory;

public class MCRDecisionHandlerRegisteruser implements DecisionHandler {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(MCRDecisionHandlerRegisteruser.class);
	private static MCRWorkflowManager WFM = MCRWorkflowManagerFactory.getImpl("registeruser");

	
	/**
	* decides which transition to take. returnes the name of this transition
	*/
	public String decide(ExecutionContext executionContext) {
		
		long pid = executionContext.getContextInstance().getProcessInstance().getId();
		String decisionNode = executionContext.getNode().getName();
		logger.debug("DECISION: checking decision node [" + decisionNode + "] for pid [" + pid + "]");
		
		String decision = WFM.checkDecisionNode(pid, decisionNode, executionContext);
		logger.debug("DECISION: result is " + decision);
		
		return decision;
	}
}
