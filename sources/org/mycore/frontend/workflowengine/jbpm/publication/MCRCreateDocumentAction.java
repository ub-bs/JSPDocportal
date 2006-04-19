package org.mycore.frontend.workflowengine.jbpm.publication;

import org.apache.log4j.Logger;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.mycore.common.MCRException;
import org.mycore.frontend.workflowengine.jbpm.MCRJbpmWorkflowBase;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerFactory;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerInterface;

public class MCRCreateDocumentAction implements ActionHandler{
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(MCRCreateDocumentAction.class);
	private static MCRWorkflowEngineManagerInterface WFI = MCRWorkflowEngineManagerFactory.getImpl("publication");

	public void execute(ExecutionContext executionContext) throws MCRException {
		logger.debug("creating empty document");
		ContextInstance contextInstance;
		contextInstance = executionContext.getContextInstance();
		String initiator = (String)contextInstance.getVariable(MCRJbpmWorkflowBase.varINITIATOR);
		String createdDocID = WFI.createMetadataDocumentID(initiator);
		if(createdDocID != null && !createdDocID.equals("")){
			executionContext.setVariable("createdDocID", createdDocID);
			executionContext.setVariable("attachedDerivates", "");
			
		}else{
			String errMsg = "could not create a docID for publication";
			logger.error(errMsg);
			throw new MCRException(errMsg);
		}
	}

}
