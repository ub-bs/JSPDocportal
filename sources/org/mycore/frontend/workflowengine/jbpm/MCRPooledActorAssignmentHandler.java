package org.mycore.frontend.workflowengine.jbpm;

import java.util.Iterator;
import java.util.List;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.def.AssignmentHandler;
import org.jbpm.taskmgmt.exe.Assignable;
import org.mycore.user2.MCRUserMgr;

public class MCRPooledActorAssignmentHandler implements AssignmentHandler {
	
	private static final long serialVersionUID = 1L;
	private String groupName;

	public void assign(Assignable assignable, ExecutionContext executionContext) throws Exception {
		List members = MCRUserMgr.instance().retrieveGroup(groupName).getMemberUserIDs();
		String[] pooledActors = new String[members.size()];
		int i = 0;
		for (Iterator it = members.iterator(); it.hasNext();) {
			pooledActors[i] = (String) it.next();
			i++;
		}
		assignable.setPooledActors( pooledActors );
	}

}
