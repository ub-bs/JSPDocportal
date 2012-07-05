package org.mycore.frontend.workflowengine.jbpm;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.def.AssignmentHandler;
import org.jbpm.taskmgmt.exe.Assignable;
import org.mycore.user2.MCRRoleManager;

public class MCRPooledActorAssignmentHandler implements AssignmentHandler {
	
	private static final long serialVersionUID = 1L;
	
	//value injected by JBPM
	private String groupName;

	public void assign(Assignable assignable, ExecutionContext executionContext) throws Exception {
		String[] members = MCRRoleManager.listUserIDs(MCRRoleManager.getRole(groupName)).toArray(new String[]{});
		assignable.setPooledActors( members );
	}

}
