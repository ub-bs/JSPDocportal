package org.mycore.frontend.workflowengine.jbpm.person;
    

import org.apache.log4j.Logger;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.node.DecisionHandler;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManagerFactory;
public class MCRDecisionHandlerPerson implements DecisionHandler {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(MCRDecisionHandlerPerson.class);
	private static MCRWorkflowManagerPerson WFM = (MCRWorkflowManagerPerson)MCRWorkflowManagerFactory.getImpl("person");

	/**
	* decides which transition to take. returnes the name of this transition
	*/
	public String decide(ExecutionContext executionContext) {
		
		long pid = executionContext.getContextInstance().getProcessInstance().getId();
		String decisionNode = executionContext.getNode().getName();
		logger.debug("checking boolean decision node [" + decisionNode + "] for pid [" + pid + "]");
		return WFM.checkDecisionNode(decisionNode, executionContext.getContextInstance());
	}
}