package org.mycore.frontend.workflowengine.jbpm.xmetadiss;

import org.apache.log4j.Logger;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.exe.ExecutionContext;
import org.mycore.common.MCRException;
import org.mycore.frontend.workflowengine.jbpm.MCRAbstractAction;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowConstants;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManagerFactory;

public class MCRCreateAuthorAction extends MCRAbstractAction{
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(MCRCreateAuthorAction.class);
	private static MCRWorkflowManagerXmetadiss WFM = (MCRWorkflowManagerXmetadiss)MCRWorkflowManagerFactory.getImpl("xmetadiss");

	public void executeAction(ExecutionContext executionContext) throws MCRException {
		ContextInstance contextInstance;
		contextInstance = executionContext.getContextInstance();
		String initiator = (String)contextInstance.getVariable(MCRWorkflowConstants.WFM_VAR_INITIATOR);
		String authorID = (String)contextInstance.getVariable(MCRWorkflowConstants.WFM_VAR_AUTHOR_IDS);
		if(authorID == null || authorID.equals("")){
			//TODO
			authorID = WFM.getAuthorStrategy().createAuthor(initiator, WFM.getNextFreeID("author")).getId();
		}
		if(authorID != null && !authorID.equals("")) {
			contextInstance.setVariable(MCRWorkflowConstants.WFM_VAR_AUTHOR_IDS, authorID);
		}else{
			logger.error("no authorID could be generated");
			throw new MCRException("no authorID could be generated");
		}
	}

}
