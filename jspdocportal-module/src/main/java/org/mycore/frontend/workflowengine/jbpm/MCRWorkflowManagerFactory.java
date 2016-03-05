package org.mycore.frontend.workflowengine.jbpm;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.datamodel.metadata.MCRObjectID;


public class MCRWorkflowManagerFactory {

	private static HashMap<String, Object> workflowManagerImpls;
	private static Logger logger = Logger.getLogger(MCRWorkflowManagerFactory.class);
	static{
		workflowManagerImpls = new HashMap<String, Object>();
		MCRConfiguration config = MCRConfiguration.instance();
		Map<String, String> props = config.getPropertiesMap("MCR.WorkflowEngine.ManagerImpl.");
		for (String prop: props.keySet()) {
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
    
    public static MCRWorkflowManager getImpl(MCRObjectID mcrid){
        String type = mcrid.getTypeId();
        String workflowTypes[] = (MCRConfiguration.instance().getString("MCR.WorkflowEngine.WorkflowTypes")).split(",");
        for ( int i = 0; i< workflowTypes.length; i++ ) {
            workflowTypes[i]=workflowTypes[i].trim();
            MCRWorkflowManager wfm = MCRWorkflowManagerFactory.getImpl(workflowTypes[i]);
            if ( wfm != null && wfm.getDocumentTypes().contains(type)) {
                return wfm;
            }            
        }
        return null;
    }
}
