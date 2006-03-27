package org.mycore.frontend.workflowengine.jbpm.xmetadiss;

import org.apache.log4j.Logger;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.mycore.common.MCRException;
import org.mycore.frontend.workflowengine.jbpm.MCRJbpmWorkflowBase;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerFactory;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerInterface;

public class MCRCreateDisshabAction implements ActionHandler{
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(MCRCreateDisshabAction.class);
	private static MCRWorkflowEngineManagerInterface WFI = MCRWorkflowEngineManagerFactory.getImpl("xmetadiss");

	public void execute(ExecutionContext executionContext) throws MCRException {
		logger.debug("creating empty disshab");
		ContextInstance contextInstance;
		contextInstance = executionContext.getContextInstance();
		String initiator = (String)contextInstance.getVariable(MCRJbpmWorkflowBase.varINITIATOR);
		String createdDocID = WFI.getMetadataDocumentID(initiator);
		if(createdDocID != null && !createdDocID.equals("")){
			executionContext.setVariable("createdDocID", createdDocID);
			executionContext.setVariable("attachedDerivates", "");
			executionContext.setVariable("containsPDF","");
			executionContext.setVariable("containsZIP","");
		}else{
			String errMsg = "could not create a docID for dissertation";
			logger.error(errMsg);
			throw new MCRException(errMsg);
		}
	}

}
