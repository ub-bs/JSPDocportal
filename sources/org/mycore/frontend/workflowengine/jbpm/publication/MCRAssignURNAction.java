package org.mycore.frontend.workflowengine.jbpm.publication;

import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.exe.ExecutionContext;
import org.mycore.common.MCRException;
import org.mycore.frontend.workflowengine.jbpm.MCRAbstractAction;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowConstants;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManager;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManagerFactory;
import org.mycore.frontend.workflowengine.strategies.MCRIdentifierStrategy;

public class MCRAssignURNAction extends MCRAbstractAction{
	private static final long serialVersionUID = 1L;
	private static MCRWorkflowManager WFM = MCRWorkflowManagerFactory.getImpl("publication");

	public void executeAction(ExecutionContext executionContext) throws MCRException {
		ContextInstance contextInstance;
		contextInstance = executionContext.getContextInstance();
		MCRIdentifierStrategy identifierStrategy = WFM.getIdentifierStrategy();
		String urn = (String)contextInstance.getVariable(MCRWorkflowConstants.WFM_VAR_RESERVATED_URN);
		String documentID = (String)contextInstance.getVariable(MCRWorkflowConstants.WFM_VAR_METADATA_OBJECT_IDS);
		identifierStrategy.setDocumentIDToUrn(urn,documentID); 
		logger.info("urn " + urn + " assigned for Document " + documentID);
	}

}
