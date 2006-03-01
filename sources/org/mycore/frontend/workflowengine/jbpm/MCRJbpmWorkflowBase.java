package org.mycore.frontend.workflowengine.jbpm;

import java.util.HashMap;
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
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;

public class MCRJbpmWorkflowBase {
	
	private static Logger logger = Logger.getLogger(MCRJbpmWorkflowBase.class);
	protected static JbpmConfiguration jbpmConfiguration = 
        JbpmConfiguration.parseResource("jbpm.cfg.xml");
	protected JbpmContext jbpmContext = null;
	protected Session session = null;
	protected GraphSession graphSession = null;
	protected TaskMgmtSession taskMgmtSession = null;
	protected ContextSession contextSession = null;
	protected SchedulerSession schedulerSession = null;
	protected LoggingSession loggingSession = null;
	protected MessagingSession messagingSession = null;	
	
	private static HashMap workflowObjects;

	public MCRJbpmWorkflowBase(){
		createJbpmContext();
		initializeMembers();
	}
	
	protected static JbpmConfiguration getJbpmConfiguration() {
		return jbpmConfiguration;
	}
	
	protected void createJbpmContext() {
	    jbpmContext = getJbpmConfiguration().createJbpmContext();
	}	
	
	protected void closeJbpmContext() {
		jbpmContext.close();
	}	
	
	protected void initializeMembers() {
	    session = jbpmContext.getSession();
	    graphSession = jbpmContext.getGraphSession();
	    taskMgmtSession = jbpmContext.getTaskMgmtSession();
	    loggingSession = jbpmContext.getLoggingSession();
	    schedulerSession = jbpmContext.getSchedulerSession();
	    contextSession = jbpmContext.getContextSession();
	    messagingSession = jbpmContext.getMessagingSession();
	}
		  
	protected void resetMembers() {
	    session = null;
	    graphSession = null;
	    taskMgmtSession = null;
	    loggingSession = null;
	    schedulerSession = null;
	    contextSession = null;
	    messagingSession = null;
	}
	
	void createSchema() {
		getJbpmConfiguration().createSchema();
	}	
	
	void deployProcess(String resource) {
		createJbpmContext();
		try{
			ProcessDefinition processDefinition = 
				ProcessDefinition.parseXmlResource("workflow/xmetadiss.par/processdefinition.xml");
				jbpmContext.deployProcessDefinition(processDefinition);
		}catch(Exception e){
			logger.error("error in deployin a workflow processdefinition", e);
		}finally{
			jbpmContext.close();
		}

	}	
	
}
