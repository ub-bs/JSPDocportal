package org.mycore.activiti;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mycore.common.config.MCRConfiguration;

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

    private static Logger LOGGER = LogManager.getLogger(MCRActivitiAssignmentHandler.class);

    public void notify(DelegateTask delegateTask) {
        String projectID = String.valueOf(delegateTask.getVariable(MCRActivitiMgr.WF_VAR_PROJECT_ID));
        String objectType = String.valueOf(delegateTask.getVariable(MCRActivitiMgr.WF_VAR_OBJECT_TYPE));

        int errorCount = 0;
        String wfID = delegateTask.getProcessDefinitionId().split(":")[0];

        String groups = null;
        String propKeyGrp = "MCR.Activiti.TaskAssignment.CandidateGroups." + wfID + "." + projectID + "_" + objectType;
        groups = MCRConfiguration.instance().getString(propKeyGrp, null);
        if (groups == null) {
            propKeyGrp = "MCR.Activiti.TaskAssignment.CandidateGroups." + wfID + ".default_" + objectType;
            groups = MCRConfiguration.instance().getString(propKeyGrp, null);
        }
        if (groups != null) {
            for (String g : groups.split(",")) {
                delegateTask.addCandidateGroup(g.trim());
            }
        } else {
            errorCount++;
        }

        String users = null;
        String propKeyUser = "MCR.Activiti.TaskAssignment.CandidateUsers." + wfID + "." + projectID + "_" + objectType;
        users = MCRConfiguration.instance().getString(propKeyUser, null);
        if (users == null) {
            propKeyUser = "MCR.Activiti.TaskAssignment.CandidateUsers." + wfID + ".default_" + objectType;
            users = MCRConfiguration.instance().getString(propKeyUser, null);
        }
        if (users != null) {
            for (String u : users.split(",")) {
                delegateTask.addCandidateUser(u.trim());
            }
        } else {
            errorCount++;
        }
        if (errorCount == 2) {
            LOGGER.error("Please define candidate users or groups for the following workflow: "
                    + delegateTask.getProcessDefinitionId().split(":")[0]);
            LOGGER.error("Set at least one of the following properties: " + propKeyGrp + " or " + propKeyUser + ".");
        }
    }
}
