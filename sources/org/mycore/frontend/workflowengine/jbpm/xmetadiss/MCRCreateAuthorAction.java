package org.mycore.frontend.workflowengine.jbpm.xmetadiss;

import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.exe.ExecutionContext;
import org.mycore.common.MCRException;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.workflowengine.jbpm.MCRAbstractAction;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowConstants;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManagerFactory;

public class MCRCreateAuthorAction extends MCRAbstractAction{
	
	private static final long serialVersionUID = 1L;
	private static MCRWorkflowManagerXmetadiss WFM = (MCRWorkflowManagerXmetadiss)MCRWorkflowManagerFactory.getImpl("xmetadiss");

	public void executeAction(ExecutionContext executionContext) throws MCRException {
		ContextInstance contextInstance;
		contextInstance = executionContext.getContextInstance();
		String initiator = (String)contextInstance.getVariable(MCRWorkflowConstants.WFM_VAR_INITIATOR);
		String authorID = (String)contextInstance.getVariable(MCRWorkflowConstants.WFM_VAR_AUTHOR_IDS);
		if(authorID == null || authorID.equals("")){
			MCRObjectID newAuthorID = WFM.getNextFreeID("author");
			//TODO
			authorID = WFM.createAuthor(initiator, newAuthorID,true,true).getId();
			if(newAuthorID.getId().equals(authorID)){
				//we have a new author -> must set default permissions
				//Attention, workflowprocesstype is "author" because we want to set the default permissions from author workflow
				//otherwise another set of properties is necessary
				WFM.setPermissions(authorID, initiator, "author", contextInstance, MCRWorkflowConstants.PERMISSION_MODE_DEFAULT);
			}
			
		}
		if(authorID != null && !authorID.equals("")) {
			contextInstance.setVariable(MCRWorkflowConstants.WFM_VAR_AUTHOR_IDS, authorID);
		}else{
			logger.error("no authorID could be generated");
			throw new MCRException("no authorID could be generated");
		}
	}

}
