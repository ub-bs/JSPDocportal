package org.mycore.frontend.jsp.stripes.actions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

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
import org.mycore.activiti.MCRActivitiUtils;
import org.mycore.common.MCRSessionMgr;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.MCRFrontendUtil;
import org.mycore.frontend.xeditor.MCREditorSession;
import org.mycore.frontend.xeditor.MCREditorSessionStore;
import org.mycore.frontend.xeditor.MCREditorSessionStoreFactory;
import org.mycore.user2.MCRUser;
import org.mycore.user2.MCRUserManager;

@UrlBinding("/showWorkspace.action")
public class ShowWorkspaceAction extends MCRAbstractStripesAction implements ActionBean {
	private static Logger LOGGER = Logger.getLogger(ShowWorkspaceAction.class);
	ForwardResolution fwdResolution = new ForwardResolution(
			"/content/workspace/workspace.jsp");

	private List<String> messages = new ArrayList<String>();
	private String mcrobjid_base = "";
	private List<Task> myTasks = new ArrayList<Task>();
	private List<Task> availableTasks = new ArrayList<Task>();

	private String editorPath;
	private String sourceURI;
	private String cancelURL;

	public ShowWorkspaceAction() {

	}

	@Before(stages = LifecycleStage.BindingAndValidation)
	public void rehydrate() {
		super.rehydrate();
		if (getContext().getRequest().getParameter("mcrobjid_base") != null) {
			mcrobjid_base = getContext().getRequest().getParameter("mcrobjid_base");
		}
	}

	@DefaultHandler
	public Resolution defaultRes() {
		if(getContext().getRequest().getParameter(MCREditorSessionStore.XEDITOR_SESSION_PARAM)!=null){
			String xEditorStepID =  getContext().getRequest().getParameter(MCREditorSessionStore.XEDITOR_SESSION_PARAM);
			String sessionID = xEditorStepID.split("-")[0];
		        MCREditorSession session = MCREditorSessionStoreFactory.getSessionStore().getSession(sessionID);

		        if (session == null) {
		            LOGGER.error("Editor session invalid !!!");
		            //ToDo - Forward to error page
		        	//String msg = getErrorI18N("xeditor.error", "noSession", sessionID);
		            try{
		            getContext().getResponse().sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "EditorSession not found: "+sessionID);
		            }
		            catch(IOException e){
		            	LOGGER.error(e);
		            }
		            return null;
		        }

		        String mcrID = session.getEditedXML().getRootElement().getAttributeValue("ID");
		        return editObject(mcrID, null);
		}

		for(String s: getContext().getRequest().getParameterMap().keySet()){
			if(s.startsWith("doAcceptTask-task_")){
				String id = s.substring(s.indexOf("_")+1);
				acceptTask(id);
			}
			if(s.startsWith("doReleaseTask-task_")){
				String id = s.substring(s.indexOf("_")+1);
				releaseTask(id);
			}
			//doFollowt-task_[ID]-[mcrObjID]
			if(s.startsWith("doGoto-task_")){
				String id = s.substring(s.indexOf("-")+1);
				String taskID = id.substring(0,id.indexOf("-"));
				taskID = taskID.substring(taskID.indexOf("_")+1);
				String transactionID = id.substring(id.indexOf("-")+1);
				followTransaction(taskID, transactionID);
			}

			
			//doEditObject-task_[ID]-[mcrObjID]
			if(s.startsWith("doEditObject-task_")){
				String id = s.substring(s.indexOf("-")+1);
				String taskID = id.substring(0,id.indexOf("-"));
				taskID = taskID.substring(taskID.indexOf("_")+1);
				String mcrObjID = id.substring(id.indexOf("-")+1);
				return editObject(mcrObjID, taskID);
			}
			
			//doEditDerivates-task_[ID]-[mcrObjID]
			if(s.startsWith("doEditDerivates-task_")){
				String id = s.substring(s.indexOf("-")+1);
				String taskID = id.substring(0,id.indexOf("-"));
				taskID = taskID.substring(taskID.indexOf("_")+1);
				String mcrObjID = id.substring(id.indexOf("-")+1);
				ForwardResolution res = new ForwardResolution("/editDerivates.action?taskid="+taskID+"&mcrobjid="+mcrObjID);
				
				return res;
			}
			
	
		}
		
		boolean doCommitTransaction = false;
		if(!MCRSessionMgr.getCurrentSession().isTransactionActive()){
			doCommitTransaction = true;
			MCRSessionMgr.getCurrentSession().beginTransaction();
		}
		MCRUser user = MCRUserManager.getCurrentUser();
		
		String objectType = mcrobjid_base.substring(mcrobjid_base.indexOf("_")+1);
		TaskService ts = MCRActivitiMgr.getWorfklowProcessEngine().getTaskService();
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
	
		if(doCommitTransaction){
			MCRSessionMgr.getCurrentSession().commitTransaction();
		}
		return fwdResolution;

	}

	public Resolution doCreateNewTask() {
		if(mcrobjid_base!=null){
		MCRSessionMgr.getCurrentSession().beginTransaction();
		String projectID = mcrobjid_base.substring(0, mcrobjid_base.indexOf("_"));
		String objectType = mcrobjid_base.substring(mcrobjid_base.indexOf("_")+1);
		if(MCRAccessManager.checkPermission("create-"+objectType)){
			Map<String, Object> variables = new HashMap<String, Object>();
			variables.put(MCRActivitiMgr.WF_VAR_OBJECT_TYPE, objectType);
			variables.put(MCRActivitiMgr.WF_VAR_PROJECT_ID, projectID);
			
			RuntimeService rs = MCRActivitiMgr.getWorfklowProcessEngine().getRuntimeService();
			//ProcessInstance pi = rs.startProcessInstanceByKey("create_object_simple", variables);
			ProcessInstance pi = rs.startProcessInstanceByMessage("start_create", variables);
			messages.add("New Activiti Process Instance " + pi.getId() +" created.");
			TaskService ts = MCRActivitiMgr.getWorfklowProcessEngine().getTaskService();
			for(Task t: ts.createTaskQuery().processInstanceId(pi.getId()).list()){
				ts.setAssignee(t.getId(), MCRUserManager.getCurrentUser().getUserID());
			}
		}
		else{
			messages.add("You don't have the Permission to create a new workflow instance");
		}
		MCRSessionMgr.getCurrentSession().commitTransaction();
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
	
	private Resolution editObject(String mcrID, String taskID){
		MCRObjectID mcrObjID = MCRObjectID.getInstance(mcrID);
		editorPath = "/editor/metadata/editor-"+mcrObjID.getTypeId()+"-default.xed";
		File wfFile = new File(MCRActivitiUtils.getWorkflowDirectory(mcrObjID), mcrID+".xml");
		sourceURI = wfFile.toURI().toString();
		ForwardResolution res = new ForwardResolution("/content/editor/fullpageEditor.jsp");
		StringBuffer sbCancel = new StringBuffer(MCRFrontendUtil.getBaseURL()+"showWorkspace.action?");
		if(!mcrobjid_base.isEmpty()){sbCancel.append("&mcrobjid_base=").append(mcrobjid_base);}
		if(taskID!=null){
			sbCancel.append("#task_").append(taskID);
		}
		cancelURL = sbCancel.toString();
	
		
		return res;
	}
	
	private void followTransaction(String taskId, String transactionID){
		TaskService ts = MCRActivitiMgr.getWorfklowProcessEngine().getTaskService();
		ts.setVariable(taskId, "goto", transactionID);
		ts.complete(taskId);
		
		
		

	}
	
	public String getMcrobjid_base() {
		return mcrobjid_base;
	}
	
	public String getObjectType() {
		return mcrobjid_base.substring(mcrobjid_base.indexOf("_")+1);
	}

	public String setMcrobjid_base() {
		return mcrobjid_base;
	}
	
	public List<Task> getMyTasks() {
		return myTasks;
	}

	public List<Task> getAvailableTasks() {
		return availableTasks;
	}

	public static Logger getLOGGER() {
		return LOGGER;
	}

	public ForwardResolution getFwdResolution() {
		return fwdResolution;
	}

	public List<String> getMessages() {
		return messages;
	}

	public String getEditorPath() {
		return editorPath;
	}

	public String getSourceURI() {
		return sourceURI;
	}

	public String getCancelURL() {
		return cancelURL;
	}

}
