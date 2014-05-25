package org.mycore.activiti;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;

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
}
