package org.mycore.frontend.workflowengine.jbpm.author;
        
import org.apache.log4j.Logger;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.mycore.common.MCRException;
import org.mycore.frontend.workflowengine.jbpm.MCRAbstractAction;
import org.mycore.frontend.workflowengine.jbpm.MCRJbpmWorkflowBase;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowConstants;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerFactory;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManagerFactory;
        
public class MCRCreateNewAuthorAction extends MCRAbstractAction {
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(MCRCreateNewAuthorAction.class);
	private static MCRWorkflowManagerAuthor WFM = (MCRWorkflowManagerAuthor)MCRWorkflowManagerFactory.getImpl("author");

	public void executeAction(ExecutionContext executionContext) throws MCRException {
		ContextInstance contextInstance;
		contextInstance = executionContext.getContextInstance();
		String initiator = (String)contextInstance.getVariable(	MCRWorkflowConstants.WFM_VAR_INITIATOR);
		long pid = executionContext.getProcessInstance().getId();
		
		String authorID = WFM.createNewAuthor(initiator,pid,false);
		if(authorID != null && !authorID.equals("")) {
			contextInstance.setVariable(MCRWorkflowConstants.WFM_VAR_METADATA_OBJECT_IDS, authorID);
			logger.debug("workflow changed state to " + executionContext.getProcessInstance().getRootToken().getName());
		}else{
			logger.error("no authorID could be generated");
			throw new MCRException("no authorID could be generated");
		}
	}

}