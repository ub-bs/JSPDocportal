package org.mycore.frontend.workflowengine.strategies;

import org.jbpm.context.exe.ContextInstance;

public interface MCRBackupStrategy {
		
	public boolean backupWorkflowFiles(ContextInstance ctxI);

}
