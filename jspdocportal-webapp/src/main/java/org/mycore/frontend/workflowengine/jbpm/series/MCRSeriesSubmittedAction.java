package org.mycore.frontend.workflowengine.jbpm.series;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.exe.ExecutionContext;
import org.mycore.common.MCRException;
import org.mycore.frontend.workflowengine.jbpm.MCRAbstractAction;
import org.mycore.frontend.workflowengine.jbpm.MCRJbpmWorkflowBase;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowConstants;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManagerFactory;
import org.mycore.user2.MCRUser;
import org.mycore.user2.MCRUserManager;

public class MCRSeriesSubmittedAction extends MCRAbstractAction{
	String lockedVariables;
	private static final long serialVersionUID = 1L;
	private static MCRWorkflowManagerSeries WFM = (MCRWorkflowManagerSeries)MCRWorkflowManagerFactory.getImpl("series");
	
	public void executeAction(ExecutionContext executionContext) throws MCRException {
		logger.debug("locking workflow variables and setting the access control to the editor mode");
		ContextInstance contextInstance = executionContext.getContextInstance();
		// set access control to editor mode, the dissertand has no rights anymore
	
		String initiator = contextInstance.getVariable(MCRWorkflowConstants.WFM_VAR_INITIATOR).toString();
		MCRUser user = MCRUserManager.getUser(initiator);
		long processID = contextInstance.getProcessInstance().getId();
		String workflowType = MCRJbpmWorkflowBase.getWorkflowProcessType(processID);
		

		List<Object> ids = new ArrayList<Object>();
		ids.addAll(Arrays.asList(((String)contextInstance.getVariable("attachedDerivates")).split(",")));
		ids.add(contextInstance.getVariable("createdDocID"));
		for (Iterator it = ids.iterator(); it.hasNext();) {
			String id = (String) it.next();
			 						//(mcrid, userid, wftype, mode)
			WFM.setPermissions(id, user.getUserID(), workflowType, contextInstance, MCRWorkflowConstants.PERMISSION_MODE_EDITING);
			
		}
	}

}
