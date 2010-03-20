package org.mycore.frontend.workflowengine.jbpm;

import org.apache.log4j.Logger;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.db.GraphSession;
import org.jbpm.persistence.JbpmPersistenceException;

public class MCRAbstractWorkflowObject {
	protected final static JbpmConfiguration jbpmConfiguration = 
        JbpmConfiguration.parseResource("jbpm.cfg.xml");
	protected JbpmContext jbpmContext = null;
	protected GraphSession graphSession = null;
	
	protected void open(){
		jbpmContext = jbpmConfiguration.createJbpmContext();
		graphSession = jbpmContext.getGraphSession();
	}
	
	protected void close(){
		try{
		if(jbpmContext != null)
			jbpmContext.close();
		}
		catch(JbpmPersistenceException e){
			Logger.getLogger(this.getClass()).error(e.getMessage(), e);
		}
	}
}
