package org.mycore.frontend.workflowengine.jbpm;

import org.apache.log4j.Logger;
import org.jbpm.graph.exe.ExecutionContext;

public class MCRSetDefaultAclsAction extends MCRAbstractAction{
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(MCRSetDefaultAclsAction.class);
	
	private String varmcrid;
	private String varuserid;

	public void executeAction(ExecutionContext executionContext) {
		logger.debug("setting default access rights");
		MCRWorkflowManager WFM = MCRWorkflowManagerFactory.getImpl(MCRJbpmWorkflowBase.getWorkflowProcessType(executionContext));
		WFM.setDefaultPermissions((String)executionContext.getVariable(varmcrid), 
				(String)executionContext.getVariable(varuserid));	}

}
