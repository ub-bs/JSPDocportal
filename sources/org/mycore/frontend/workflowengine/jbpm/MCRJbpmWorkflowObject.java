package org.mycore.frontend.workflowengine.jbpm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.db.GraphSession;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.PooledActor;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.mycore.common.MCRException;
import org.mycore.common.MCRSessionMgr;

public class MCRJbpmWorkflowObject {
	
	private static JbpmConfiguration jbpmConfiguration = 
        JbpmConfiguration.parseResource("jbpm.cfg.xml");	
	
	private static Logger logger = Logger.getLogger(MCRJbpmWorkflowObject.class);
	private long processInstanceID = -1;
	private String workflowProcessType;
	
	public MCRJbpmWorkflowObject(String processType) {
		createNewProcessInstance(processType);
	}
	
	public MCRJbpmWorkflowObject(long processID){
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try{
			ProcessInstance pI = jbpmContext.getGraphSession().loadProcessInstance(processID);
			processInstanceID = processID;
			workflowProcessType = pI.getProcessDefinition().getName();
			
		}catch(MCRException e){
			logger.error("workflow error",e);
		}finally{
			jbpmContext.close();
		}
	}
	
	public MCRWorkflowEngineManagerInterface getCurrentWorkflowManager(){
		return MCRWorkflowEngineManagerFactory.getImpl(workflowProcessType);
	}
	
	public String getWorkflowProcessType(){
		return workflowProcessType;
	}
	
	private void createNewProcessInstance(String processType) {
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try {
			jbpmContext.setActorId(MCRSessionMgr.getCurrentSession().getCurrentUserID());
			GraphSession graphSession = jbpmContext.getGraphSession();
		    ProcessDefinition processDefinition = graphSession.findLatestProcessDefinition(processType);
		    ProcessInstance processInstance = new ProcessInstance(processDefinition);
		    workflowProcessType = processInstance.getProcessDefinition().getName();
		    processInstanceID = processInstance.getId();
		    jbpmContext.save(processInstance);
		}catch(MCRException e){
			logger.error("could not create new processIinstance '", e);
		}finally {
			jbpmContext.close();
		}		
	} 
	
	public void initialize(String initiator){
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try {
			jbpmContext.setActorId(initiator);
			ProcessInstance processInstance = jbpmContext.loadProcessInstance(processInstanceID);
			processInstance.getTaskMgmtInstance().createStartTaskInstance();
			processInstance.getContextInstance().setVariable(MCRJbpmWorkflowBase.varINITIATOR, initiator);
			//jbpmContext.save(processInstance);
		}catch(MCRException e){
			logger.error("could not create new processIinstance '", e);
		}finally {
			jbpmContext.close();
		}		
		lockStringVariable(MCRJbpmWorkflowBase.varINITIATOR);
	}
	
	public boolean endTask(String taskName, String curUserID, String transitionName){
		boolean ret = false;
		logger.debug("try to end task " + taskName);
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try {
			ProcessInstance processInstance = jbpmContext.loadProcessInstance(processInstanceID);
			TaskInstance taskInstance = null;
			for(Iterator taskIt = processInstance.getTaskMgmtInstance().getTaskInstances().iterator(); taskIt.hasNext();){
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
			//jbpmContext.save(taskInstance);
		}catch(MCRException e){
			logger.error("could not create new processIinstance '", e);
		}finally {
			jbpmContext.close();
		}	
		return ret;
	}
	
	public String getDocumentType() {
		
		String mcrid = getStringVariableValue("createdDocID");
		
		try {
			String parts[] = mcrid.split("_");
			return parts[1];
		} catch(Exception anyEx) {
			return "";
		}
	}

	public String getStringVariableValue(String varName) {
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try {
			ContextInstance contextInstance = jbpmContext.getGraphSession().loadProcessInstance(processInstanceID).getContextInstance();
			String value = (String)contextInstance.getVariable(varName);
			return value;
		}catch(MCRException e){
			logger.error("could not get variable '", e);
			return "";
		}finally {
			jbpmContext.close();
		}		
	}
	
	/**
	 * sets workflow-process variables to a given value, if the variable is not locked
	 * 
	 * be careful, don't use this function in actionhandlers (persistence problems),
	 * set variables with contextInstance there... 
	 * @param varName
	 * @param value
	 */
	public void setStringVariableValue(String varName, String value) {
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try {
			ProcessInstance processInstance = jbpmContext.getGraphSession().loadProcessInstance(processInstanceID);
			ContextInstance contextInstance = processInstance.getContextInstance();
			Set lockedSet = getSetOfLockedVariables((String)contextInstance.getVariable(MCRJbpmWorkflowBase.lockedVariablesIdentifier));
			if(!lockedSet.contains(varName)){
				contextInstance.setVariable(varName, value);
				jbpmContext.save(processInstance);
			}else{
				logger.debug("variable [" + varName + "] could not be set, it is locked");
			}
		}catch(MCRException e){
			logger.error("could not set variable '" + varName + 
					"' to the value [" + value + "]", e);
		}finally {
			jbpmContext.close();
		}		
	}
	
	public void lockStringVariable(String varName){
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try {
			ProcessInstance processInstance = jbpmContext.getGraphSession().loadProcessInstance(processInstanceID);
			ContextInstance contextInstance = processInstance.getContextInstance();
			String lockedVariables = (String)contextInstance.getVariable(MCRJbpmWorkflowBase.lockedVariablesIdentifier);
			if(lockedVariables == null || lockedVariables.equals("")) {
				contextInstance.setVariable(MCRJbpmWorkflowBase.lockedVariablesIdentifier, varName);
			}else{
				contextInstance.setVariable(MCRJbpmWorkflowBase.lockedVariablesIdentifier, lockedVariables + "," + varName);
			}
			jbpmContext.save(processInstance);
		}catch(MCRException e){
			logger.error("could not lock variable '" + varName, e);
		}finally {
			jbpmContext.close();
		}		
	}
	
	public void unlockStringVariable(String varName){
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try {
			ProcessInstance processInstance = jbpmContext.getGraphSession().loadProcessInstance(processInstanceID);
			ContextInstance contextInstance = processInstance.getContextInstance();
			Set lockedSet = getSetOfLockedVariables((String)contextInstance.getVariable(MCRJbpmWorkflowBase.lockedVariablesIdentifier));
			lockedSet.remove(varName);
			boolean first = true;
			StringBuffer sb = new StringBuffer("");
			for (Iterator it = lockedSet.iterator(); it.hasNext();) {
				if(!first)
					sb.append(",");
				sb.append((String) it.next());
				first = false;
			}		
			contextInstance.setVariable(MCRJbpmWorkflowBase.lockedVariablesIdentifier, sb.toString());
			jbpmContext.save(processInstance);
		}catch(MCRException e){
			logger.error("could not lock variable '" + varName, e);
		}finally {
			jbpmContext.close();
		}			
	}
	
	private Set getSetOfLockedVariables(String lockedVariables){
		Set ret = new HashSet();
		if(lockedVariables != null && !lockedVariables.equals("")){
			ret.addAll(Arrays.asList(lockedVariables.split(",")));
		}
		return ret;
	}
	
	/**
	 * deletes a workflow process variable
	 *  deleting this way delivers dead data in the 
	 *  workflow tables, better set a given variable
	 *  to empty string ""
	 * @param varName
	 */
	public void deleteVariable(String varName){
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try {
			ProcessInstance processInstance = jbpmContext.getGraphSession().loadProcessInstance(processInstanceID);
			ContextInstance contextInstance = processInstance.getContextInstance();
			contextInstance.deleteVariable(varName);
			jbpmContext.save(processInstance);
		}catch(MCRException e){
			logger.error("could not delete variable '" + varName + 
					"' from processInstance [" + processInstanceID + "]", e);
		}finally {
			jbpmContext.close();
		}				
	}
	
	public boolean setWorkflowStatus(String newStatus){
		boolean statusIsSet = false;
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try {
			ProcessInstance processInstance = jbpmContext.getGraphSession().loadProcessInstance(processInstanceID);
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
		}catch(MCRException e){
			logger.error("could not set workflow status [" + newStatus + "]", e);
		}finally {
			jbpmContext.close();
		}		
		return statusIsSet;
	}
	
	public long getProcessInstanceID() {
		return processInstanceID;
	}	
	
	public void signal(){
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try {
			ProcessInstance processInstance = jbpmContext.getGraphSession().loadProcessInstance(processInstanceID);
			processInstance.signal();
			jbpmContext.save(processInstance);
		}catch(MCRException e){
			logger.error("could not signal to root token of processid " + processInstanceID, e);
		}finally {
			jbpmContext.close();
		}		
	}
	
	public void signal(String transitionName){
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try {
			ProcessInstance processInstance = jbpmContext.getGraphSession().loadProcessInstance(processInstanceID);
			logger.debug("before transition [" + transitionName + "] node [" + processInstance.getRootToken().getNode().getName() + "]");
			processInstance.signal(transitionName);
			logger.debug("after transition [" + transitionName + "] node [" + processInstance.getRootToken().getNode().getName() + "]");
			jbpmContext.save(processInstance);
		}catch(Exception e){
			logger.error("could not signal to root token of processid " + processInstanceID, e);
		}finally {
			jbpmContext.close();
		}		
	}	

	
	public void testFunction() {
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try {
			ProcessInstance processInstance = jbpmContext.getGraphSession().loadProcessInstance(processInstanceID);
			
//		   TaskInstance taskInstance = null;
//		    
//		    jbpmContext.setActorId("heiko");
//		    // create a task to start the xmetadiss
//		    taskInstance = taskMgmtInstance.createStartTaskInstance();
//		    
//
//		    Map taskVariables = new HashMap();
//		    taskVariables.put("item", "cookies");
//		    taskVariables.put("quantity", "lots of them");
//		    taskVariables.put("address", "sesamestreet 46");
//		    
//		    taskInstance.addVariables(taskVariables);
//		    System.out.println(taskInstance.getActorId());
//		    
//		    taskInstance.end();
//		    jbpmContext.save(taskInstance);
//		    System.out.println("saved");
//		    
//		    System.out.println(taskInstance.getVariable("processOwner"));
//		    SwimlaneInstance swi = taskMgmtInstance.getSwimlaneInstance("author");

		}catch(MCRException e){
			logger.error("error:", e);
		}finally {
			jbpmContext.close();
		}
		
	}
}
