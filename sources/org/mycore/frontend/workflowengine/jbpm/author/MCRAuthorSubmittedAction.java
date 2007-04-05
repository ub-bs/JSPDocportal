package org.mycore.frontend.workflowengine.jbpm.author;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.exe.ExecutionContext;
import org.mycore.common.MCRException;
import org.mycore.frontend.workflowengine.jbpm.MCRAbstractAction;
import org.mycore.frontend.workflowengine.jbpm.MCRJbpmWorkflowBase;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowConstants;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManager;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManagerFactory;
import org.mycore.user.MCRUser;
import org.mycore.user.MCRUserMgr;

public class MCRAuthorSubmittedAction extends MCRAbstractAction {
	
	String lockedVariables;

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(MCRAuthorSubmittedAction.class);

	public void executeAction(ExecutionContext executionContext) throws MCRException {
		logger.debug("locking workflow variables and setting the access control to the editor mode");
		ContextInstance contextInstance = executionContext.getContextInstance();
		// contextInstance.setVariable(MCRJbpmWorkflowBase.lockedVariablesIdentifier, lockedVariables);
		// set access control to editor mode, the dissertand has no rights anymore
		contextInstance.setVariable(MCRWorkflowConstants.WFM_VAR_BOOL_TEMPORARY_IN_DATABASE, Boolean.TRUE);
		List<Object> ids = new ArrayList<Object>();
		ids.add(contextInstance.getVariable(MCRWorkflowConstants.WFM_VAR_METADATA_OBJECT_IDS));
		
		String initiator = contextInstance.getVariable(MCRWorkflowConstants.WFM_VAR_INITIATOR).toString();
		MCRUser user = MCRUserMgr.instance().retrieveUser(initiator);
		long processID = contextInstance.getProcessInstance().getId();
		String workflowType = MCRJbpmWorkflowBase.getWorkflowProcessType(processID);
		MCRWorkflowManager wfm = MCRWorkflowManagerFactory.getImpl(workflowType);
		
		for (Iterator it = ids.iterator(); it.hasNext();) {
			String id = (String) it.next();
			 						//(mcrid, userid, wftype, mode)
			wfm.permissionStrategy.setPermissions(id, user.getID(), workflowType, contextInstance, MCRWorkflowConstants.PERMISSION_MODE_EDITING);
			wfm.permissionStrategy.setPermissions(id, user.getID(), workflowType, contextInstance, MCRWorkflowConstants.PERMISSION_MODE_CREATORRREAD);;
		}
	}

}
