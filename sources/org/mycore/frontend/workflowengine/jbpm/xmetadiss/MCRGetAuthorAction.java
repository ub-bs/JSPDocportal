package org.mycore.frontend.workflowengine.jbpm.xmetadiss;

import org.apache.log4j.Logger;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.mycore.common.MCRException;
import org.mycore.frontend.workflowengine.jbpm.MCRJbpmWorkflowBase;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerFactory;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerInterface;

public class MCRGetAuthorAction implements ActionHandler{
	
	String variableName;

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(MCRGetAuthorAction.class);
	private static MCRWorkflowEngineManagerInterface WFI = MCRWorkflowEngineManagerFactory.getImpl("xmetadiss");

	public void execute(ExecutionContext executionContext) throws MCRException {
		ContextInstance contextInstance;
		contextInstance = executionContext.getContextInstance();
		String initiator = (String)contextInstance.getVariable(MCRJbpmWorkflowBase.varINITIATOR);
		String authorID = WFI.getAuthorFromUniqueWorkflow(initiator);
		if(authorID != null && !authorID.equals("")) {
			contextInstance.setVariable(variableName, authorID);
			logger.debug("workflow changed state to " + executionContext.getProcessInstance().getRootToken().getName());
		}else{
			logger.error("no authorID could be generated");
			throw new MCRException("no authorID could be generated");
		}
	}

}
