package org.mycore.frontend.workflowengine.jbpm.registeruser;


import org.apache.log4j.Logger;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.exe.ExecutionContext;
import org.mycore.common.MCRException;
import org.mycore.frontend.workflowengine.jbpm.MCRAbstractAction;
import org.mycore.frontend.workflowengine.jbpm.MCRJbpmWorkflowBase;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowConstants;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManager;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManagerFactory;
import org.mycore.user2.MCRUser;
import org.mycore.user2.MCRUserMgr;

public class MCRUserSubmittedAction extends MCRAbstractAction{
	
	String lockedVariables;

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(MCRUserSubmittedAction.class);

	public void executeAction(ExecutionContext executionContext) throws MCRException {
		logger.debug("locking workflow variables and setting the access control to the admin mode");
		ContextInstance contextInstance = executionContext.getContextInstance();
		// set access control to admin mode, the user  has no rights anymore
		String id = (String) contextInstance.getVariable("userID");
		String initiator = contextInstance.getVariable(MCRWorkflowConstants.WFM_VAR_INITIATOR).toString();
		MCRUser user = MCRUserMgr.instance().retrieveUser(initiator);
	
		long processID = contextInstance.getProcessInstance().getId();
		String workflowType = MCRJbpmWorkflowBase.getWorkflowProcessType(processID);
		MCRWorkflowManager wfm = MCRWorkflowManagerFactory.getImpl(workflowType);
		
		 						//(mcrid, userid, wftype, mode)
		wfm.permissionStrategy.setPermissions(id, user.getID(), workflowType,MCRWorkflowConstants.PERMISSION_MODE_EDITING);
	}


}
