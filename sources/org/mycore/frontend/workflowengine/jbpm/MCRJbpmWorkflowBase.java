package org.mycore.frontend.workflowengine.jbpm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import org.hibernate.Query;
import org.hibernate.Session;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.db.GraphSession;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.mycore.common.MCRException;

public class MCRJbpmWorkflowBase {
	
	// A VARIABLE USED IN ALL WORKFLOW PROCESSES
	public final static String varINITIATOR = "initiator";
	private static Logger logger = Logger.getLogger(MCRJbpmWorkflowBase.class);
	protected static JbpmConfiguration jbpmConfiguration = 
        JbpmConfiguration.parseResource("jbpm.cfg.xml");
	// help cache for finding the workflow-processes of a given user
	// not yet implemented in jbpm, can be removed one day

	public MCRJbpmWorkflowBase(){
	}
	
	
	protected void deleteProcessInstance(long procID){
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

	public static List getCurrentProcessIDs(String initiator) {
		List ret = new ArrayList();
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try{
			Session hibSession = jbpmContext.getSession();
			Query hibQuery = hibSession.getNamedQuery("MCRJbpmWorkflowBase.getCurrentProcessIDsForInitiator");
			hibQuery.setString(varINITIATOR , initiator);
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
				ProcessDefinition.parseXmlResource("workflow/xmetadiss.par/processdefinition.xml");
				jbpmContext.deployProcessDefinition(processDefinition);
		}catch(MCRException e){
			logger.error("error in deployin a workflow processdefinition", e);
		}finally{
			jbpmContext.close();
		}
	}	
	
}
