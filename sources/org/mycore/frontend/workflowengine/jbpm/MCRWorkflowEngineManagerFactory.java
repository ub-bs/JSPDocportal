package org.mycore.frontend.workflowengine.jbpm;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import org.mycore.common.MCRConfiguration;



public class MCRWorkflowEngineManagerFactory {

	private static final String defaultImpl = "org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerBaseImpl";
	private static HashMap workflowManagerImpls;
	static{
		workflowManagerImpls = new HashMap();
		MCRConfiguration config = MCRConfiguration.instance();
		Properties props = config.getProperties("MCR.WorkflowEngine.ManagerImpl.");
		for (Enumeration e = props.keys(); e.hasMoreElements();) {
			String prop = (String) e.nextElement();
			String workflowProcessType = prop.substring("MCR.WorkflowEngine.ManagerImpl.".length());
			workflowManagerImpls.put(workflowProcessType, config.getInstanceOf(prop, defaultImpl));
		}
	}

	
    public static MCRWorkflowEngineManagerInterface getImpl(String workflowType) {
    	return (MCRWorkflowEngineManagerInterface) workflowManagerImpls.get(workflowType);
    }
    
    public static MCRWorkflowEngineManagerInterface getDefaultImpl() {
    	return (MCRWorkflowEngineManagerInterface) MCRConfiguration.instance().getInstanceOf("MCR.WorkflowEngine.ManagerDefaultImpl", defaultImpl);
    }       
    


}
