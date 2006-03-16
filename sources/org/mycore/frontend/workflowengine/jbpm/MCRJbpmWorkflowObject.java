package org.mycore.frontend.workflowengine.jbpm;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.jbpm.JbpmContext;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.db.GraphSession;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ProcessInstance;
import org.mycore.common.MCRException;

public class MCRJbpmWorkflowObject extends MCRJbpmWorkflowBase {
	
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
	
	private void createNewProcessInstance(String processType) {
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try {
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
		setStringVariableValue(varINITIATOR, initiator);
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
			logger.error("could not set variable '", e);
			return "";
		}finally {
			jbpmContext.close();
		}		
	}
	
	/**
	 * sets workflow-process variables to a given value
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
			contextInstance.setVariable(varName, value);
			jbpmContext.save(processInstance);
		}catch(MCRException e){
			logger.error("could not set variable '" + varName + 
					"' to the value [" + value + "]", e);
		}finally {
			jbpmContext.close();
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
			Node curNode = processInstance.getRootToken().getNode();
			logger.error(curNode.getName());
			processInstance.signal(transitionName);
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
