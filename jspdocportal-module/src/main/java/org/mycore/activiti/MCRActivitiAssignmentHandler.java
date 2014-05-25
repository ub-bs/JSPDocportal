package org.mycore.activiti;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.log4j.Logger;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.common.config.MCRConfigurationException;


/**
 * MCRActivitiAssignmentHandler assigns the proper users and groups to the given task
 * by looking them up in MyCoRe properties
 * 
 * they are defined as follows:
 * groups:	
 * 		"MCR.Activiti.TaskAssignment.CandidateGroups."+taskID+"."+mcrObjectIDBase
 * 		e.g.: MCR.Activiti.TaskAssignment.CandidateGroups.edit_object.cpr_person=editProfessorum
 * 
 * users:
 * 		"MCR.Activiti.TaskAssignment.CandidateUsers."+taskID+"."+mcrObjectIDBase
 * 		e.g.: MCR.Activiti.TaskAssignment.CandidateUsers.edit_object.cpr_person=administrator
 * 
 * @author Robert Stephan
 *
 */
public class MCRActivitiAssignmentHandler implements TaskListener {
	private static final long serialVersionUID = 1L;
	private static Logger LOGGER = Logger.getLogger(MCRActivitiAssignmentHandler.class);

	public void notify(DelegateTask delegateTask) {
		String projectID = String.valueOf(delegateTask.getVariable(MCRActivitiMgr.WF_VAR_PROJECT_ID));
		String objectType = String.valueOf(delegateTask.getVariable(MCRActivitiMgr.WF_VAR_OBJECT_TYPE));
		
		int errorCount=0;
		String propKeyGroups = "MCR.Activiti.TaskAssignment.CandidateGroups."+delegateTask.getProcessDefinitionId().split(":")[0]+"."+projectID+"_"+objectType;
		try{
			String groups = MCRConfiguration.instance().getString(propKeyGroups);
			for(String g: groups.split(",")){
				delegateTask.addCandidateGroup(g.trim());
			}
		}
		catch(MCRConfigurationException e){
			errorCount++;
		}
		
		String propKeyUser = "MCR.Activiti.TaskAssignment.CandidateUsers."+delegateTask.getProcessDefinitionId().split(":")[0]+"."+projectID+"_"+objectType;
		try{
			String users = MCRConfiguration.instance().getString(propKeyUser);
			for(String u: users.split(",")){
				delegateTask.addCandidateUser(u.trim());
			}
		}
		catch(MCRConfigurationException e){
			errorCount++;
		}
		if(errorCount==2){
			LOGGER.error("Please define candidate users or groups for the following workflow: "+delegateTask.getProcessDefinitionId().split(":")[0]);
			LOGGER.error("Set one of the following properties: "+propKeyGroups+" or "+propKeyUser+".");
		}
	}
}
