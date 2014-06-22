package org.mycore.activiti;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.delegate.DelegateExecution;
import org.mycore.activiti.workflows.create_object_simple.MCRWorkflowMgr;
import org.mycore.common.MCRException;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.common.config.MCRConfigurationException;

public class MCRActivitiMgr {
	public static final String WF_VAR_PROJECT_ID = "projectID";
	public static final String WF_VAR_OBJECT_TYPE = "objectType";
	public static final String WF_VAR_OBJECT_ID = "objectID";
	
	private static ProcessEngine activitiProcessEngine;

	//Workflow Engine
		public static synchronized ProcessEngine getWorfklowProcessEngine(){
			if(activitiProcessEngine == null){
				activitiProcessEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("/config/workflow/activiti.cfg.xml").buildProcessEngine();
			}
			return activitiProcessEngine;
		}
		
		public static MCRWorkflowMgr getWorkflowMgr(DelegateExecution execution){
			 String projectID = String.valueOf(execution.getVariable(MCRActivitiMgr.WF_VAR_PROJECT_ID));
			 String objectType = String.valueOf(execution.getVariable(MCRActivitiMgr.WF_VAR_OBJECT_TYPE));
			
			MCRWorkflowMgr mgr = null;
			String prop="";
			try{
			try{
				prop = "MCR.Activiti.WorkflowMgr.class.create_object_simple."+projectID+"_"+objectType;
				mgr = (MCRWorkflowMgr)MCRConfiguration.instance().getInstanceOf(prop);
			}
			catch(MCRConfigurationException cnfe){
				//use default;
				prop = "MCR.Activiti.WorkflowMgr.class.create_object_simple.default_"+objectType;
				mgr = (MCRWorkflowMgr)MCRConfiguration.instance().getInstanceOf(prop);
			}
			}
			catch(ClassCastException cce){
				throw new MCRException("Class Cast Exception - the specified MCRWorkflowMgr in property "+prop+" could not be casted to MCRWorkflowMgr", cce);
			}
			
			return mgr;
		}		
}
