package org.mycore.frontend.workflowengine.jbpm.xmetadiss;

import org.apache.log4j.Logger;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.mycore.common.MCRException;
import org.mycore.frontend.workflowengine.jbpm.MCRJbpmWorkflowBase;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerFactory;

public class MCRCreateAuthorAction implements ActionHandler{
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(MCRCreateAuthorAction.class);
	private static MCRWorkflowEngineManagerXmetadiss WFI = (MCRWorkflowEngineManagerXmetadiss)MCRWorkflowEngineManagerFactory.getImpl("xmetadiss");

	public void execute(ExecutionContext executionContext) throws MCRException {
		ContextInstance contextInstance;
		contextInstance = executionContext.getContextInstance();
		String initiator = (String)contextInstance.getVariable(MCRJbpmWorkflowBase.varINITIATOR);
		String authorID = WFI.createAuthorFromInitiator(initiator);
		if(authorID != null && !authorID.equals("")) {
			contextInstance.setVariable("authorID", authorID);
			logger.debug("workflow changed state to " + executionContext.getProcessInstance().getRootToken().getName());
		}else{
			logger.error("no authorID could be generated");
			throw new MCRException("no authorID could be generated");
		}
	}

}
