package org.mycore.frontend.workflowengine.jbpm;


public class MCRJbpmTaskBean {

	private long processID;
	private String workflowProcessType;
	private String taskName;
	private String workflowStatus;
	private org.w3c.dom.Document variables;
	
	public MCRJbpmTaskBean(long processID, String workflowProcessType,
				String taskName, String workflowStatus,
				org.w3c.dom.Document variables){
		setProcessID(processID);
		setWorkflowProcessType(workflowProcessType);
		setTaskName(taskName);
		setWorkflowStatus(workflowStatus);
		setVariables(variables);
	}
	
	public long getProcessID() {
		return processID;
	}
	public void setProcessID(long processID) {
		this.processID = processID;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getWorkflowProcessType() {
		return workflowProcessType;
	}
	public void setWorkflowProcessType(String workflowProcessType) {
		this.workflowProcessType = workflowProcessType;
	}
	public String getWorkflowStatus() {
		return workflowStatus;
	}
	public void setWorkflowStatus(String workflowStatus) {
		this.workflowStatus = workflowStatus;
	}
	
	public org.w3c.dom.Document getVariables(){
		return variables;
	}
	
	public void setVariables(org.w3c.dom.Document variables){
		this.variables = variables;
	}
}
