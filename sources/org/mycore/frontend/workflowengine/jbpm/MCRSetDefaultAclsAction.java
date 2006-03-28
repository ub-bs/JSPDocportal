package org.mycore.frontend.workflowengine.jbpm;

import org.apache.log4j.Logger;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.mycore.common.MCRException;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerFactory;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerInterface;

public class MCRSetDefaultAclsAction implements ActionHandler{
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(MCRSetDefaultAclsAction.class);
	
	private String varmcrid;
	private String varuserid;
	
	
	private MCRWorkflowEngineManagerInterface WFI;

	public void execute(ExecutionContext executionContext) throws MCRException {
		logger.debug("setting default access rights");
		WFI = MCRWorkflowEngineManagerFactory.getImpl(executionContext.getProcessDefinition().getName());
		WFI.setDefaultPermissions((String)executionContext.getVariable(varmcrid), 
				(String)executionContext.getVariable(varuserid));
	}

}
