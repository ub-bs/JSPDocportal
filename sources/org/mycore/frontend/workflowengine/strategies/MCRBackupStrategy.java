package org.mycore.frontend.workflowengine.strategies;

import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowProcess;

public interface MCRBackupStrategy {
		
	public boolean backupWorkflowFiles(MCRWorkflowProcess wfp);

}
