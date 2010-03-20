package org.mycore.frontend.workflowengine.jbpm.person;

import org.jbpm.graph.exe.ExecutionContext;
import org.mycore.common.MCRException;
import org.mycore.frontend.workflowengine.jbpm.MCRCommitObjectAction;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowConstants;

public class MCRCommitPersonAction extends MCRCommitObjectAction {
	
	private static final long serialVersionUID = 1L;

	
	private String varnameOBJID;
	private String varnameERROR;
	
	public void execute(ExecutionContext executionContext) throws MCRException {
		executionContext.setVariable(MCRWorkflowConstants.WFM_VAR_BOOL_TEMPORARY_IN_DATABASE, Boolean.FALSE);
		super.varnameOBJID = varnameOBJID;
		super.varnameERROR = varnameERROR;
		super.execute(executionContext);
	}
}
