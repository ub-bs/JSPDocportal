package org.mycore.activiti.workflows.create_object_simple;

import org.activiti.engine.delegate.DelegateExecution;
import org.mycore.datamodel.metadata.MCRObject;

public interface MCRWorkflowMgr {
	public MCRObject createMCRObject(DelegateExecution execution);
}
