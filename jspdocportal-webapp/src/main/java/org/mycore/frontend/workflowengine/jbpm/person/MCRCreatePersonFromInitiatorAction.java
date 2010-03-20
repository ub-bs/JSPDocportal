package org.mycore.frontend.workflowengine.jbpm.person;

import org.apache.log4j.Logger;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.exe.ExecutionContext;
import org.mycore.common.MCRException;
import org.mycore.frontend.workflowengine.jbpm.MCRAbstractAction;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowConstants;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManagerFactory;

public class MCRCreatePersonFromInitiatorAction extends MCRAbstractAction {
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(MCRCreatePersonFromInitiatorAction.class);
	private static MCRWorkflowManagerPerson WFM = (MCRWorkflowManagerPerson)MCRWorkflowManagerFactory.getImpl("person");

	public void executeAction(ExecutionContext executionContext) throws MCRException {
		ContextInstance ctxI = executionContext.getContextInstance();
		String initiator = (String)ctxI.getVariable(	MCRWorkflowConstants.WFM_VAR_INITIATOR);
				
		String authorID = WFM.createNewAuthor(initiator, ctxI, true);
		if(authorID != null && !authorID.equals("")) {
			ctxI.setVariable(MCRWorkflowConstants.WFM_VAR_METADATA_OBJECT_IDS, authorID);
			logger.debug("workflow changed state to " + ctxI.getProcessInstance().getRootToken().getName());
		}else{
			logger.error("no authorID could be generated");
			throw new MCRException("no authorID could be generated");
		}
	}

}
