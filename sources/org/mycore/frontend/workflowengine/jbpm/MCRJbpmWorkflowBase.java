package org.mycore.frontend.workflowengine.jbpm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import org.hibernate.Session;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.db.ContextSession;
import org.jbpm.db.GraphSession;
import org.jbpm.db.LoggingSession;
import org.jbpm.db.MessagingSession;
import org.jbpm.db.SchedulerSession;
import org.jbpm.db.TaskMgmtSession;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.mycore.common.MCRException;

public class MCRJbpmWorkflowBase {
	
	private static Logger logger = Logger.getLogger(MCRJbpmWorkflowBase.class);
	protected static JbpmConfiguration jbpmConfiguration = 
        JbpmConfiguration.parseResource("jbpm.cfg.xml");
	// help cache for finding the workflow-processes of a given user
	// not yet implemented in jbpm, can be removed one day
	protected static HashMap initiatorMap = null;

	static{
		initializeInitiators();
	}
	
	public MCRJbpmWorkflowBase(){
	}
	
	
	
	/**
	 * initializes the initiator-map
	 * that contains for each userid a list
	 * of initiated processids (Long)
	 *
	 */
	protected static void initializeInitiators(){
		initiatorMap = new HashMap();
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try{
			GraphSession graphSession = jbpmContext.getGraphSession();
			for (Iterator it = graphSession.findAllProcessDefinitions().iterator(); it.hasNext();) {
				ProcessDefinition def = (ProcessDefinition) it.next();
				for (Iterator it2 = graphSession.findProcessInstances(def.getId()).iterator(); it2.hasNext();) {
					ProcessInstance process = (ProcessInstance) it2.next();
					String initiator = (String)process.getContextInstance().getVariable("initiator");
					if(initiator != null && !initiator.equals("")){
						addToInitiatorMap(initiator, process.getId());
					}
				}
			}
		}catch(MCRException e){
			logger.error("workflow error",e);
		}finally{
			jbpmContext.close();
		}
	}
	
	protected static synchronized void addToInitiatorMap(String initiator, long processID) {
		if(initiator != null && !initiator.equals("")){
			List ids = new ArrayList();
			if(initiatorMap.containsKey(initiator)) {
				ids = (List)initiatorMap.get(initiator);
			}
			ids.add(new Long(processID));
			initiatorMap.put(initiator, ids);
		}		
	}
	
	public static List getCurrentProcessIDs(String initiator, String processType) {
		List ret = new ArrayList();
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try{
			if(initiatorMap.containsKey(initiator)){
				for (Iterator it = ((List)initiatorMap.get(initiator)).iterator(); it.hasNext();) {
					long processID = ((Long)it.next()).longValue();
					ProcessInstance processInstance = jbpmContext.loadProcessInstance(processID);
					if(processInstance.getProcessDefinition().getName().equals(processType)) {
						ret.add(new Long(processInstance.getId()));
					}
				}
			}
		}catch(MCRException e){
			logger.error("error in fetching the actual process ids", e);
		}finally{
			jbpmContext.close();
		}
		return ret;		
	}
	
	public static List getCurrentProcessIDs(String initiator) {
		List ret = new ArrayList();
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try{
			if(initiatorMap.containsKey(initiator)){
				for (Iterator it = ((List)initiatorMap.get(initiator)).iterator(); it.hasNext();) {
					long processID = ((Long)it.next()).longValue();
					ProcessInstance processInstance = jbpmContext.loadProcessInstance(processID);
					ret.add(new Long(processInstance.getId()));
				}
			}
		}catch(MCRException e){
			logger.error("error in fetching the actual process ids", e);
		}finally{
			jbpmContext.close();
		}
		return ret;		
	}	
	
	public static String getWorkflowStatus(long processID){
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		String nodeName = "";
		try{
			ProcessInstance processInstance = jbpmContext.loadProcessInstance(processID);
			Node curNode = processInstance.getRootToken().getNode();
			if(curNode != null) {
				nodeName = (curNode.getName() == null)? "noname" : curNode.getName();
			}
		}catch(MCRException e){
			logger.error("error in fetching the actual process node", e);
		}finally{
			jbpmContext.close();
		}
		return nodeName;			

	}	
	
	protected static JbpmConfiguration getJbpmConfiguration() {
		return jbpmConfiguration;
	}
	
	void createSchema() {
		getJbpmConfiguration().createSchema();
	}	
	
	void deployProcess(String resource) {
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try{
			ProcessDefinition processDefinition = 
				ProcessDefinition.parseXmlResource("workflow/xmetadiss.par/processdefinition.xml");
				jbpmContext.deployProcessDefinition(processDefinition);
		}catch(MCRException e){
			logger.error("error in deployin a workflow processdefinition", e);
		}finally{
			jbpmContext.close();
		}
	}	
	
}
