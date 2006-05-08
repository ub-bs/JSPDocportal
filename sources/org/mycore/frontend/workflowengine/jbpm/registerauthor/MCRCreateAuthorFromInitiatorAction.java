package org.mycore.frontend.workflowengine.jbpm.registerauthor;

import org.apache.log4j.Logger;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.mycore.common.MCRException;
import org.mycore.frontend.workflowengine.jbpm.MCRJbpmWorkflowBase;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerFactory;

public class MCRCreateAuthorFromInitiatorAction implements ActionHandler{
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(MCRCreateAuthorFromInitiatorAction.class);
	private static MCRWorkflowEngineManagerRegisterauthor WFI = (MCRWorkflowEngineManagerRegisterauthor)MCRWorkflowEngineManagerFactory.getImpl("registerauthor");

	public void execute(ExecutionContext executionContext) throws MCRException {
		ContextInstance contextInstance;
		contextInstance = executionContext.getContextInstance();
		String initiator = (String)contextInstance.getVariable(MCRJbpmWorkflowBase.varINITIATOR);
		long pid = executionContext.getProcessInstance().getId();
		String authorID = WFI.createAuthorFromInitiator(initiator, pid);
		if(authorID != null && !authorID.equals("")) {
			//contextInstance.setVariable("authorID", authorID);
			contextInstance.setVariable("createdDocID", authorID);
			logger.debug("workflow changed state to " + executionContext.getProcessInstance().getRootToken().getName());
		}else{
			logger.error("no authorID could be generated");
			throw new MCRException("no authorID could be generated");
		}
	}

}
