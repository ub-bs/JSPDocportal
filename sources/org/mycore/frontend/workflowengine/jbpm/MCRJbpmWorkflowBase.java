package org.mycore.frontend.workflowengine.jbpm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.db.GraphSession;
import org.jbpm.db.TaskMgmtSession;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.DOMOutputter;
import org.mycore.common.MCRException;

public class MCRJbpmWorkflowBase {
	
	// A VARIABLE USED IN ALL WORKFLOW PROCESSES
	/*
	 * now they are in MCRWorkflowConstants
	public final static String varINITIATOR = "initiator";
	public final static String varINITIATOREMAIL = "initiatorEmail";
	public final static String varINITIATORSALUTATION = "initiatorSalutation";	
	public final static String varSIGNED_AFFIRMATION_AVAILABLE = "signedAffirmationAvailable";
	*/
	// not used public static final String lockedVariablesIdentifier = "MCRJBPMLOCKEDVARIABLES";
	private static Logger logger = Logger.getLogger(MCRJbpmWorkflowBase.class);
	private static JbpmConfiguration jbpmConfiguration =   JbpmConfiguration.parseResource("jbpm.cfg.xml");
	// help cache for finding the workflow-processes of a given user
	// not yet implemented in jbpm, can be removed one day

	public MCRJbpmWorkflowBase(){
	}
	
	
	static void deleteProcessInstance(long procID){
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try{
			GraphSession graphSession = jbpmContext.getGraphSession();
			graphSession.deleteProcessInstance(procID);
		}catch(MCRException e){
			logger.error("could not delete process [" + procID + "]",e);
		}finally{
			jbpmContext.close();
		}
	}

	public static List getCurrentProcessIDsForProcessType(String processType) {
		List ret = new ArrayList();
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try{					
			GraphSession graphSession = jbpmContext.getGraphSession();
			ProcessDefinition  processDefinition = graphSession.findLatestProcessDefinition(processType);
			String processDefinitionID = Long.toString( processDefinition.getId()); 
			
			Session hibSession = jbpmContext.getSession();	
			Query hibQuery = hibSession.getNamedQuery("MCRJbpmWorkflowBase.getCurrentProcessIDsForProcessType");
			hibQuery.setString("processDefinitionId" , processDefinitionID);
			List processInstances = hibQuery.list();
			for (Iterator it = processInstances.iterator(); it.hasNext();) {
				ProcessInstance processInstance = (ProcessInstance) it.next();
				if (   !processInstance.hasEnded() ){
					ret.add(new Long(processInstance.getId()));
				}
			}
		}catch(MCRException e){
			logger.error("error in fetching the current process ids for processType " + processType, e);
		}finally{
			jbpmContext.close();
		}
		return ret;		
	}

	/**
	 * 
	 * @param initiator
	 * @return
	 * not depecated, because for registering a new user we have no other chance to get the right id
	 */
	public static List getCurrentProcessIDs(String initiator) {
		List ret = new ArrayList();
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try{
			Session hibSession = jbpmContext.getSession();
			Query hibQuery = hibSession.getNamedQuery("MCRJbpmWorkflowBase.getCurrentProcessIDsForInitiator");
			hibQuery.setString(MCRWorkflowConstants.WFM_VAR_INITIATOR , initiator);
			List processInstances = hibQuery.list();
			for (Iterator it = processInstances.iterator(); it.hasNext();) {
				ProcessInstance processInstance = (ProcessInstance) it.next();
				ret.add(new Long(processInstance.getId()));
			}
		}catch(MCRException e){
			logger.error("error in fetching the current process ids for initiator " + initiator, e);
		}finally{
			jbpmContext.close();
		}
		return ret;		
	}
	
	public static List getCurrentProcessIDsForProcessVariable(String varName, String value){
		List ret = new ArrayList();
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try{
			Session hibSession = jbpmContext.getSession();
			Query hibQuery = hibSession.getNamedQuery("MCRJbpmWorkflowBase.getCurrentProcessIDsForProcessVariable");
			hibQuery.setString("var" , varName);
			hibQuery.setString("value", value);
			List processInstances = hibQuery.list();
			for (Iterator it = processInstances.iterator(); it.hasNext();) {
				ProcessInstance processInstance = (ProcessInstance) it.next();
				ret.add(new Long(processInstance.getId()));
			}
		}catch(MCRException e){
			logger.error("error in fetching the current process ids for varname " + varName + " and value " + value, e);
		}finally{
			jbpmContext.close();
		}
		return ret;			
	}
	
	public static List getCurrentProcessIDs(String initiator, String processType) {
		List ret = new ArrayList();
		List allProcessIDs = getCurrentProcessIDs(initiator);
	
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try{
			for (Iterator it = allProcessIDs.iterator(); it.hasNext();) {
				long processID = ((Long)it.next()).longValue();
				ProcessInstance processInstance = jbpmContext.loadProcessInstance(processID);
				if(processInstance.getProcessDefinition().getName().equals(processType)) {
					ret.add(new Long(processInstance.getId()));
				}
				
			}
		}catch(MCRException e){
			logger.error("error in fetching the current process ids for initiator " + initiator + " and processType " + processType, e);
		}finally{
			jbpmContext.close();
		}
		return ret;
	}
	
	public static List getTasks(String userid, List workflowProcessTypes){
		List ret = new ArrayList();
		HashSet listOfIDs = new HashSet();
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try{
			TaskMgmtSession taskMgmtSession = jbpmContext.getTaskMgmtSession();
			List taskInstances = new ArrayList();
			taskInstances.addAll(taskMgmtSession.findTaskInstances(userid));
			taskInstances.addAll(taskMgmtSession.findPooledTaskInstances(userid));
			for (Iterator it = taskInstances.iterator(); it.hasNext();) {
				TaskInstance taskInstance = (TaskInstance) it.next();
				ProcessInstance processInstance = taskInstance.getTaskMgmtInstance().getProcessInstance();
				long processID = processInstance.getId();
				if(listOfIDs.contains(new Long(processID))){
					continue;
				}
				String curNodeName = "";
				Node curNode = processInstance.getRootToken().getNode();
				if(curNode != null) {
					curNodeName = (curNode.getName() == null)? "noname" : curNode.getName();
				}
				String workflowProcessType = processInstance.getProcessDefinition().getName();
				if(workflowProcessTypes == null || workflowProcessTypes.size() == 0 || workflowProcessTypes.contains(workflowProcessType)){
					String taskName = taskInstance.getName();
					String workflowStatus = curNodeName;
					org.w3c.dom.Document variables = 
						buildTaskVariablesXML(taskInstance.getVariables(),
								processInstance.getContextInstance().getVariables());
					ret.add(new MCRJbpmTaskBean(processID, workflowProcessType, taskName, workflowStatus, variables));
					listOfIDs.add(new Long(processID));
				}
			}
		}catch(MCRException e){
			logger.error("error in fetching the task lists", e);
		}finally{
			jbpmContext.close();
		}		
		return ret;
	}
	
	public static List getProcessesByInitiator(String userid, List workflowProcessTypes){
		List ret = new ArrayList();
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try{
			Session hibSession = jbpmContext.getSession();
			Query hibQuery = hibSession.getNamedQuery("MCRJbpmWorkflowBase.getCurrentProcessIDsForInitiator");
			hibQuery.setString(MCRWorkflowConstants.WFM_VAR_INITIATOR , userid);
			List processInstances = hibQuery.list();
			for (Iterator it = processInstances.iterator(); it.hasNext();) {
				ProcessInstance processInstance = (ProcessInstance)it.next();
				String curNodeName = "";
				Node curNode = processInstance.getRootToken().getNode();
				if(curNode != null) {
					curNodeName = (curNode.getName() == null)? "noname" : curNode.getName();
				}
				long processID = processInstance.getId();
				String workflowProcessType = processInstance.getProcessDefinition().getName();
				if(workflowProcessTypes == null || workflowProcessTypes.size() == 0 || workflowProcessTypes.contains(workflowProcessType)){
					String taskName = "initialization";
					String workflowStatus = curNodeName;
					org.w3c.dom.Document variables = 
						buildTaskVariablesXML(new HashMap(),
								processInstance.getContextInstance().getVariables());
					ret.add(new MCRJbpmTaskBean(processID, workflowProcessType, taskName, workflowStatus, variables)); 
				}
			}
		}catch(MCRException e){
			logger.error("error in fetching the current process ids for initiator " + userid, e);
		}finally{
			jbpmContext.close();
		}
		return ret;			
	}
	
	private static org.w3c.dom.Document buildTaskVariablesXML(Map taskVariables, Map contextVariables){
		Element variables = new Element("variables");
		for (Iterator it = contextVariables.keySet().iterator(); it.hasNext();) {
			String var = (String) it.next();
			String value = (String)contextVariables.get(var);
			if ( value !=null ){
				Element variable = new Element("variable");
				variable.setAttribute("name",var);
				variable.setAttribute("type","context");
				variable.setAttribute("value",value);
				variables.addContent(variable);				
			}
		}		
		for (Iterator it = taskVariables.keySet().iterator(); it.hasNext();) {
			String var = (String) it.next();
			String value = (String)taskVariables.get(var);
			if ( value !=null ){
				Element variable = new Element("variable");
				variable.setAttribute("name",var);
				variable.setAttribute("type","task");
				variable.setAttribute("value",value);
				variables.addContent(variable);
			}
		}
		try{
			return new DOMOutputter().output(new Document(variables));
		}catch(Exception e){
			logger.error("could not build org.w3c.dom.Document", e);
			return null;
		}
	}
	
	/**
	 * 
	 * @param processID
	 * @return
	 * @deprecated
	 */
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
	
	static void createSchema() {
		getJbpmConfiguration().createSchema();
	}	
	
	void deployProcess(String resource) {
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try{
			ProcessDefinition processDefinition = 
				ProcessDefinition.parseXmlResource(resource);
				jbpmContext.deployProcessDefinition(processDefinition);
		}catch(MCRException e){
			logger.error("error in deployin a workflow processdefinition", e);
		}finally{
			jbpmContext.close();
		}
	}
	
	public static String getWorkflowProcessType(long processID){
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try{
			return jbpmContext.loadProcessInstance(processID).getProcessDefinition().getName();
		}catch(MCRException e){
			logger.error("could not get workflow process type for [" + processID + "]",e);
		}finally{
			jbpmContext.close();
		}	
		return "";
	}
	
	public static String getWorkflowProcessType(ExecutionContext executionContext){
		return executionContext.getProcessInstance().getProcessDefinition().getName();
	}	
	
	public static String getStringVariable(String varName, long processID){
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try{
			return (String)jbpmContext.loadProcessInstance(processID)
				.getContextInstance().getVariable(varName);
		}catch(MCRException e){
			logger.error("could not get workflow variable [" + varName + "] for process [" + processID + "]",e);
		}finally{
			jbpmContext.close();
		}	
		return "";		
	}
	
}
