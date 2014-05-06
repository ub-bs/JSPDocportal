package org.mycore.activiti;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;

public class MCRActivitiMgr {
	private static ProcessEngine activitiProcessEngine;

	//Workflow Engine
		public static synchronized ProcessEngine getWorfklowProcessEngine(){
			if(activitiProcessEngine == null){
				activitiProcessEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("/config/workflow/activiti.cfg.xml").buildProcessEngine();
			}
			return activitiProcessEngine;
		}
}
