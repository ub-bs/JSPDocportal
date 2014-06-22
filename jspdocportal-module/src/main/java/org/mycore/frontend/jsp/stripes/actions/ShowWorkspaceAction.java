package org.mycore.frontend.jsp.stripes.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.controller.LifecycleStage;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.log4j.Logger;
import org.mycore.access.MCRAccessManager;
import org.mycore.activiti.MCRActivitiMgr;
import org.mycore.common.MCRSessionMgr;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.user2.MCRUser;
import org.mycore.user2.MCRUserManager;

@UrlBinding("/showWorkspace.action")
public class ShowWorkspaceAction extends MCRAbstractStripesAction implements ActionBean {
	private static Logger LOGGER = Logger.getLogger(ShowWorkspaceAction.class);
	ForwardResolution fwdResolution = new ForwardResolution(
			"/content/workspace/workspace.jsp");

	private List<String> messages = new ArrayList<String>();
	private String objectType = "";
	private String projectID = "";
	private List<Task> myTasks = new ArrayList<Task>();
	private List<Task> availableTasks = new ArrayList<Task>();

	

	public ShowWorkspaceAction() {

	}

	@Before(stages = LifecycleStage.BindingAndValidation)
	public void rehydrate() {
		super.rehydrate();
		if (getContext().getRequest().getParameter("objectType") != null) {
			String type = getContext().getRequest().getParameter("objectType");
			if(MCRObjectID.isValidType(type)){
				objectType = type;
			}
		}
		if (getContext().getRequest().getParameter("projectID") != null) {
			projectID = getContext().getRequest().getParameter("projectID");
		}
	}

	@DefaultHandler
	public Resolution defaultRes() {
		if(!MCRSessionMgr.getCurrentSession().isTransactionActive()){
			MCRSessionMgr.getCurrentSession().beginTransaction();
		}
		for(String s: getContext().getRequest().getParameterMap().keySet()){
			if(s.startsWith("doAcceptTask_")){
				String id = s.substring(s.indexOf("_")+1);
				acceptTask(id);
			}
			if(s.startsWith("doReleaseTask_")){
				String id = s.substring(s.indexOf("_")+1);
				releaseTask(id);
			}
		}
		
		MCRUser user = MCRUserManager.getCurrentUser();
		
		TaskService ts = MCRActivitiMgr.getWorfklowProcessEngine()
				.getTaskService();
		myTasks = ts
				.createTaskQuery()
				.taskAssignee(user.getUserID())
				.processVariableValueEquals(MCRActivitiMgr.WF_VAR_OBJECT_TYPE,
						objectType).orderByTaskCreateTime().desc().list();
		availableTasks = ts
				.createTaskQuery()
				.taskCandidateUser(user.getUserID())
				.processVariableValueEquals(MCRActivitiMgr.WF_VAR_OBJECT_TYPE,
						objectType).orderByTaskCreateTime().desc().list();
	
		MCRSessionMgr.getCurrentSession().commitTransaction();
		return fwdResolution;

	}

	public Resolution doCreateNewTask() {
		MCRSessionMgr.getCurrentSession().beginTransaction();
		if(MCRAccessManager.checkPermission("create-"+objectType)){
			Map<String, Object> variables = new HashMap<String, Object>();
			variables.put(MCRActivitiMgr.WF_VAR_OBJECT_TYPE, objectType);
			variables.put(MCRActivitiMgr.WF_VAR_PROJECT_ID, projectID);
			
			RuntimeService rs = MCRActivitiMgr.getWorfklowProcessEngine().getRuntimeService();
			ProcessInstance pi = rs.startProcessInstanceByKey("create_object_simple", variables);
			messages.add("New Activiti Process Instance " + pi.getId() +" created.");
			TaskService ts = MCRActivitiMgr.getWorfklowProcessEngine().getTaskService();
			for(Task t: ts.createTaskQuery().processInstanceId(pi.getId()).list()){
				ts.setAssignee(t.getId(), MCRUserManager.getCurrentUser().getUserID());
			}
		}
		else{
			messages.add("You don't have the Permission to create a new workflow instance");
		}

	
		return defaultRes();
	}
	
	private void acceptTask(String taskId) {
		LOGGER.debug("Accepted Task" +taskId );
		TaskService ts = MCRActivitiMgr.getWorfklowProcessEngine().getTaskService();
		ts.setAssignee(taskId, MCRUserManager.getCurrentUser().getUserID());
	}

	
	private void releaseTask(String taskId) {
		LOGGER.debug("Accepted Task" +taskId );
		TaskService ts = MCRActivitiMgr.getWorfklowProcessEngine().getTaskService();
		ts.setAssignee(taskId, null);
	}
	
	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getProjectID() {
		return projectID;
	}

	public void setProjectID(String projectID) {
		this.projectID = projectID;
	}
	
	public List<Task> getMyTasks() {
		return myTasks;
	}

	public List<Task> getAvailableTasks() {
		return availableTasks;
	}

}
