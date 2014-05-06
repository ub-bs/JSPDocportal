package org.mycore.activiti;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
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

	public void notify(DelegateTask delegateTask) {
		String projectID = String.valueOf(delegateTask.getVariable(MCRActivitiWorkflowMgr.WF_VAR_PROJECT_ID));
		String objectType = String.valueOf(delegateTask.getVariable(MCRActivitiWorkflowMgr.WF_VAR_OBJECT_TYPE));
		
	
		String propKey = "MCR.Activiti.TaskAssignment.CandidateGroups."+delegateTask.getId()+"."+projectID+"_"+objectType;
		try{
			String groups = MCRConfiguration.instance().getString(propKey);
			for(String g: groups.split(",")){
				delegateTask.addCandidateGroup(g.trim());
			}
		}
		catch(MCRConfigurationException e){
			//do nothing
		}
		
		propKey = "MCR.Activiti.TaskAssignment.CandidateUsers."+delegateTask.getId()+"."+projectID+"_"+objectType;
		try{
			String users = MCRConfiguration.instance().getString(propKey);
			for(String u: users.split(",")){
				delegateTask.addCandidateGroup(u.trim());
			}
		}
		catch(MCRConfigurationException e){
			//do nothing
		}
	}
}
