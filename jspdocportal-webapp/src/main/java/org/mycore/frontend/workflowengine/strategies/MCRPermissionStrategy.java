package org.mycore.frontend.workflowengine.strategies;

import org.jbpm.context.exe.ContextInstance;
import org.mycore.common.MCRConfiguration;

public interface MCRPermissionStrategy {

	final public static String[] defaultPermissionTypes = 
		MCRConfiguration.instance()
		.getString("MCR.WorkflowEngine.DefaultPermissionTypes", 
				"read,commitdb,writedb,deletedb,deletewf").split(",");
	
	/**
	 * sets default permissions for a given mcrobj 
	 * and a given user in the specific workflow
	 * 
	 * @param mcrid
	 * @param userid
	 * @param workflowProcessType
	 */
	public void setPermissions(String mcrid, String userid, String workflowProcessType, ContextInstance ctxI, int mode);	
	
}
