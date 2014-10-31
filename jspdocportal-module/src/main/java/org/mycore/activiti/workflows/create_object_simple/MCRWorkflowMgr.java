package org.mycore.activiti.workflows.create_object_simple;

import org.activiti.engine.delegate.DelegateExecution;
import org.mycore.datamodel.metadata.MCRObject;

public interface MCRWorkflowMgr {
	public MCRObject createMCRObject(DelegateExecution execution);
	
	public MCRObject loadMCRObject(DelegateExecution execution);
	
	public MCRObject dropMCRObject(DelegateExecution execution);

	public boolean deleteProcessInstance(String processInstanceId);
	
	public boolean commitMCRObject(DelegateExecution execution);
	
	public boolean rollbackMCRObject(DelegateExecution execution);
	
	public boolean validateMCRObject(DelegateExecution execution);
}
