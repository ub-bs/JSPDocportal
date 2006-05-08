package org.mycore.frontend.workflowengine.jbpm;

import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.db.GraphSession;

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
		if(jbpmContext != null)
			jbpmContext.close();
	}
}
