package org.mycore.frontend.workflowengine.jbpm;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.PooledActor;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.jbpm.taskmgmt.exe.TaskMgmtInstance;
import org.mycore.common.MCRException;
import org.mycore.common.MCRSessionMgr;
import org.mycore.datamodel.metadata.MCRMetaISO8601Date;

/**
 * 
 * 
 */
public class MCRWorkflowProcess extends MCRAbstractWorkflowObject{
	
	private static Logger logger = Logger.getLogger(MCRWorkflowProcess.class);
    //private ProcessDefinition processDefinition = null;
	private ProcessInstance processInstance = null;
	private ContextInstance contextInstance = null;
	private TaskMgmtInstance taskMgmtInstance = null;
	private long processInstanceID = -1;
	
	private String workflowProcessType;
	private boolean closed=true;
	
	MCRWorkflowProcess(String processType) {
		super.open();
		try{
			createNewProcessInstance(processType);
		}catch(MCRException e){
			logger.error("workflow constructor error",e);
			super.close();
		}
		closed=false;
	}
	
	protected MCRWorkflowProcess(long processID){
		super.open();
		try{
			initVariables(processID);
		}catch(MCRException e){
			logger.error("workflow constructor error",e);
			super.close();
		}
	}
	
	private void initVariables(long processID){
		processInstance = jbpmContext.getGraphSession().loadProcessInstance(processID);
		taskMgmtInstance = processInstance.getTaskMgmtInstance();
		contextInstance = processInstance.getContextInstance();
		processInstanceID = processID;
		workflowProcessType = processInstance.getProcessDefinition().getName();		
	}
	
	private void createNewProcessInstance(String processType) {
		jbpmContext.setActorId(MCRSessionMgr.getCurrentSession().getCurrentUserID());
		ProcessDefinition processDefinition = graphSession.findLatestProcessDefinition(processType);
	    processInstance = new ProcessInstance(processDefinition);
		taskMgmtInstance = processInstance.getTaskMgmtInstance();
		contextInstance = processInstance.getContextInstance();	    
		workflowProcessType = processInstance.getProcessDefinition().getName();
		processInstanceID = processInstance.getId();
		setProcessCreatedVariable();
		jbpmContext.save(processInstance);
	} 	
	
	public MCRWorkflowManager getCurrentWorkflowManager(){
		return MCRWorkflowManagerFactory.getImpl(workflowProcessType);
	}
	
	public String getWorkflowProcessType(){
		return workflowProcessType;
	}
	
	public void initialize(String initiator){
		jbpmContext.setActorId(initiator);
		taskMgmtInstance.createStartTaskInstance();
		contextInstance.setVariable(MCRWorkflowConstants.WFM_VAR_INITIATOR, initiator);
	}
	
	public boolean endTask(String taskName, String curUserID, String transitionName){
		boolean ret = false;
		logger.debug("try to end task " + taskName);
		TaskInstance taskInstance = null;
		for(Iterator taskIt = taskMgmtInstance.getTaskInstances().iterator(); taskIt.hasNext();){
			taskInstance = (TaskInstance)taskIt.next();
			if(taskInstance.isOpen())
				break;
		}
			
		if(taskInstance == null){
			logger.warn("could not end task " + taskName + ", no task was found");
		}else{
			if(taskInstance != null && taskInstance.getName().equalsIgnoreCase(taskName)){
				Set allowedUsers = new HashSet();
				Set pooledActors = taskInstance.getPooledActors();
				if(pooledActors != null){
					for (Iterator it = pooledActors.iterator(); it
							.hasNext();) {
						allowedUsers.add(((PooledActor) it.next()).getActorId());
					}
				}
				if(taskInstance.getActorId() != null)
					allowedUsers.add(taskInstance.getActorId());
				if(allowedUsers.contains(curUserID)){
					if(transitionName != null && !transitionName.equals("")){						
						taskInstance.end(transitionName);
					}else{
						taskInstance.end();
					}
					logger.debug(taskName + " has been ended");	
					ret = true;
				}else{
					logger.debug("user [" + curUserID + "] cannot end the task [" + taskName + "]");
				}
			}else{
				String err = "current task " + taskInstance.getName() + " is different from " + taskName;
				throw new MCRException(err);
			}
		}
		return ret;
	}
	
	public String getDocumentType() {
		
		String mcrid = getStringVariable(MCRWorkflowConstants.WFM_VAR_METADATA_OBJECT_IDS);
		try {
			String parts[] = mcrid.split("_");
			return parts[1];
		} catch(Exception anyEx) {
			return "";
		}
	}

	public String getStringVariable(String varName) {		
		return (String)contextInstance.getVariable(varName);
	}
	
	protected final void deleteWorkflowProcessInstance(long processID){
		try{
	    	MCRJbpmWorkflowBase.deleteProcessInstance(processID);
		}catch(Exception e){
			String errMsg = "could not delete process [" + processID + "]"; 
			logger.error(errMsg);
			throw new MCRException(errMsg);
		}		
	}
	
	/**
	 * @return Returns the processInstance.
	 */
	protected final ProcessInstance getProcessInstance() {
		return processInstance;
	}

	public Map getStringVariableMap() {		
		Map varMap = contextInstance.getVariables();
		return varMap;
	}
	
	/**
	 * sets workflow-process variables to a given value, if the variable is not locked
	 * 
	 * be careful, don't use this function in actionhandlers (persistence problems),
	 * set variables with contextInstance there... 
	 * @param varName
	 * @param value
	 */
	public void setStringVariable(String varName, String value) {
		contextInstance.setVariable(varName, value);
	}
	
	public final void setProcessCreatedVariable(){
		MCRMetaISO8601Date d= new MCRMetaISO8601Date();
		d.setType("createdate");
		d.setDate(new Date());
		setStringVariable(MCRWorkflowConstants.WFM_VAR_CREATED, d.getISOString()); 
		logger.debug("process created on " + d.getISOString());
	}
	
	
	/**
	 * sets workflow-process variables
	 * 
	 * be careful, don't use this function in actionhandlers (persistence problems),
	 * set variables with contextInstance there... 
	 * @param varName
	 * @param value
	 */
	public void setStringVariables(Map map) {
		for (Iterator it = map.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			String value = (String)map.get(key);
			if(value == null)
				value = "";
			contextInstance.setVariable(key, (String)map.get(key));
			jbpmContext.save(processInstance);
		}		
	}
	

	/**
	 * deletes a workflow process variable
	 *  deleting this way delivers dead data in the 
	 *  workflow tables, better set a given variable
	 *  to empty string ""
	 * @param varName
	 */
	public void deleteVariable(String varName){
		contextInstance.deleteVariable(varName);
	}

	/**
	 * 
	 * @param newStatus
	 * @return
	 * @deprecated
	 */
	public boolean setWorkflowStatus(String newStatus){
		boolean statusIsSet = false;
		Node curNode = processInstance.getRootToken().getNode();
		if(curNode.getName().equals(newStatus)){
			logger.debug("status is already set to " + newStatus);
			statusIsSet = true;
		}
		for (Iterator it = curNode.getLeavingTransitions().iterator(); it.hasNext();) {
			Transition transition = (Transition) it.next();
			if(transition.getTo().getName().equals(newStatus)) {
				processInstance.getRootToken().signal(transition);
				jbpmContext.save(processInstance);
				statusIsSet = true;
				break;
			}
			
		}
		return statusIsSet;
	}
	
	public long getProcessInstanceID() {
		return processInstanceID;
	}	
	
	public ContextInstance getContextInstance(){
		return contextInstance;
	}
	
	public void signal(){
		processInstance.signal();
		jbpmContext.save(processInstance);
	}
	
	public void signal(String transitionName){
		logger.debug("before transition [" + transitionName + "] node [" + processInstance.getRootToken().getNode().getName() + "]");
		processInstance.signal(transitionName);
		logger.debug("after transition [" + transitionName + "] node [" + processInstance.getRootToken().getNode().getName() + "]");
		jbpmContext.save(processInstance);
	}	
	
	public void close(){
		closed=true;
		super.close();
		MCRWorkflowProcessManager.getInstance().removeWorkflowProcess(this);
	}
	
	public boolean wasClosed(){
		return closed;
	}
	
	public void save(){
		long processID = getProcessInstanceID();
		super.close();
		super.open();
		initVariables(processID);
	}
}
