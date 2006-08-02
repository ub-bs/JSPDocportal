package org.mycore.frontend.workflowengine.jbpm.institution;

import org.apache.log4j.Logger;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.exe.ExecutionContext;
import org.mycore.common.MCRException;
import org.mycore.frontend.workflowengine.jbpm.MCRAbstractAction;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowConstants;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManagerFactory;


public class MCRCreateNewInstitutionAction extends MCRAbstractAction {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(MCRCreateNewInstitutionAction.class);
	private static MCRWorkflowManagerInstitution WFM = (MCRWorkflowManagerInstitution)MCRWorkflowManagerFactory.getImpl("institution");

	public void executeAction(ExecutionContext executionContext) throws MCRException {
		ContextInstance contextInstance;
		contextInstance = executionContext.getContextInstance();
		String initiator = (String)contextInstance.getVariable(	MCRWorkflowConstants.WFM_VAR_INITIATOR);
		long pid = executionContext.getProcessInstance().getId();
		
		String institutionID = WFM.createNewInstitution(initiator,pid);
		if(institutionID != null && !institutionID.equals("")) {
			contextInstance.setVariable(MCRWorkflowConstants.WFM_VAR_METADATA_OBJECT_IDS, institutionID);
			logger.debug("workflow changed state to " + executionContext.getProcessInstance().getRootToken().getName());
		}else{
			logger.error("no institutionID could be generated");
			throw new MCRException("no institutionID could be generated");
		}
	}
}
