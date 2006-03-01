package org.mycore.frontend.workflowengine.jbpm;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jbpm.JbpmConfiguration;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.SwimlaneInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.jbpm.taskmgmt.exe.TaskMgmtInstance;
import org.mycore.common.MCRException;

public class MCRJbpmWorkflowObject extends MCRJbpmWorkflowBase {
	
	private static Logger logger = Logger.getLogger(MCRJbpmWorkflowObject.class);
	JbpmConfiguration jbpmConfiguration = null;
	ProcessDefinition processDefinition = null;
	ProcessInstance processInstance = null;
	ContextInstance contextInstance = null;
	TaskMgmtInstance taskMgmtInstance = null;
	long processInstanceId = -1;

	public MCRJbpmWorkflowObject(String processType) {
		createNewProcessInstance(processType);
	}
	
	private void createNewProcessInstance(String processType) {
	    processDefinition = graphSession.findLatestProcessDefinition(processType);
	    processInstance = new ProcessInstance(processDefinition);
	    contextInstance = processInstance.getContextInstance();
	    taskMgmtInstance = processInstance.getTaskMgmtInstance();
	} 
	
	public void testFunction() {
		createJbpmContext();
		try {
		   TaskInstance taskInstance = null;
		    
		    jbpmContext.setActorId("heiko");
		    // create a task to start the xmetadiss
		    taskInstance = taskMgmtInstance.createStartTaskInstance();
		    

		    Map taskVariables = new HashMap();
		    taskVariables.put("item", "cookies");
		    taskVariables.put("quantity", "lots of them");
		    taskVariables.put("address", "sesamestreet 46");
		    
		    taskInstance.addVariables(taskVariables);
		    System.out.println(taskInstance.getActorId());
		    
		    taskInstance.end();
		    jbpmContext.save(taskInstance);
		    System.out.println("saved");
		    
		    System.out.println(taskInstance.getVariable("processOwner"));
		    SwimlaneInstance swi = taskMgmtInstance.getSwimlaneInstance("author");

		}catch(MCRException e){
			logger.error("error:", e);
		}finally {
			closeJbpmContext();
		}
		
	}
}
