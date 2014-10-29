package org.mycore.frontend.jsp.stripes.actions;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
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
import org.mycore.common.MCRSession;
import org.mycore.common.MCRSessionMgr;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.user2.MCRUserManager;

@UrlBinding("/startedit.action")
public class StartEditAction extends MCRAbstractStripesAction implements ActionBean {
	private static Logger LOGGER = Logger.getLogger(StartEditAction.class);
	
	private String mcrid = null;
	public StartEditAction() {

	}

	@Before(stages = LifecycleStage.BindingAndValidation)
	public void rehydrate() {
		super.rehydrate();
		if (getContext().getRequest().getParameter("mcrid") != null) {
			mcrid = getContext().getRequest().getParameter("mcrid");
		}		
	}

	@DefaultHandler
	public Resolution defaultRes() {
		MCRSession sessionFromRequest = MCRServlet.getSession(getContext().getRequest());
		if(!sessionFromRequest.getID().equals(MCRSessionMgr.getCurrentSessionID())){
		    MCRSessionMgr.releaseCurrentSession();
		    MCRSessionMgr.setCurrentSession(sessionFromRequest);
		}
		
		if (!MCRAccessManager.checkPermission(mcrid, "writedb" )) {
			String lang   = MCRSessionMgr.getCurrentSession().getCurrentLanguage();
			String usererrorpage = "/nav?path=~mycore-error?messageKey=WF.common.PrivilegesError&lang=" + lang;
			LOGGER.debug("Access denied for current user to start workflow for object " + mcrid);				
			return new ForwardResolution(usererrorpage);
			
		}
		
		LOGGER.debug("Document MCRID = " + mcrid);
		
		if ( mcrid != null){
			if(MCRAccessManager.checkPermission(mcrid, "writedb")){
				MCRObjectID mcrObjID = MCRObjectID.getInstance(mcrid);
				Map<String, Object> variables = new HashMap<String, Object>();
				variables.put(MCRActivitiMgr.WF_VAR_OBJECT_TYPE, mcrObjID.getTypeId());
				variables.put(MCRActivitiMgr.WF_VAR_PROJECT_ID, mcrObjID.getProjectId());
				variables.put(MCRActivitiMgr.WF_VAR_MCR_OBJECT_ID, mcrObjID.toString());
					
				RuntimeService rs = MCRActivitiMgr.getWorfklowProcessEngine().getRuntimeService();
				//ProcessInstance pi = rs.startProcessInstanceByKey("create_object_simple", variables);
				ProcessInstance pi = rs.startProcessInstanceByMessage("start_load", variables);
				TaskService ts = MCRActivitiMgr.getWorfklowProcessEngine().getTaskService();
				for(Task t: ts.createTaskQuery().processInstanceId(pi.getId()).list()){
					ts.setAssignee(t.getId(), MCRUserManager.getCurrentUser().getUserID());
				}
				return new RedirectResolution("/showWorkspace.action?mcrobjid_base="+mcrObjID.getBase());
			}
		}
		return null;
	}
}
