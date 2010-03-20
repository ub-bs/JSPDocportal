package org.mycore.frontend.workflowengine.jbpm;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.mycore.common.MCRConfiguration;


public class MCRWorkflowManagerFactory {

	private static HashMap<String, Object> workflowManagerImpls;
	private static Logger logger = Logger.getLogger(MCRWorkflowManagerFactory.class);
	static{
		workflowManagerImpls = new HashMap<String, Object>();
		MCRConfiguration config = MCRConfiguration.instance();
		Properties props = config.getProperties("MCR.WorkflowEngine.ManagerImpl.");
		for (Enumeration e = props.keys(); e.hasMoreElements();) {
			String prop = (String) e.nextElement();
			String workflowProcessType = prop.substring("MCR.WorkflowEngine.ManagerImpl.".length());
			workflowManagerImpls.put(workflowProcessType, config.getInstanceOf(prop));
		}
	}

	
    public static MCRWorkflowManager getImpl(String workflowType) {
    	return (MCRWorkflowManager) workflowManagerImpls.get(workflowType);
    }
    
    public static MCRWorkflowManager getImpl(long processID) {
    	String workflowType = MCRJbpmWorkflowBase.getWorkflowProcessType(processID);
    	if(workflowType != null && !workflowType.equals("")){
    		return getImpl(workflowType);
    	}else{
    		String errMsg = "no workflow manager given for process id [" + processID + "]";
    		logger.error(errMsg);
    		throw new IllegalStateException(errMsg);
    	}
    }    
}
