package org.mycore.frontend.jsp.stripes.actions;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.controller.LifecycleStage;

import org.apache.log4j.Logger;
import org.mycore.access.MCRAccessManager;
import org.mycore.common.MCRSession;
import org.mycore.common.MCRSessionMgr;
import org.mycore.datamodel.metadata.MCRMetadataManager;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.MCRFrontendUtil;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManager;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManagerFactory;

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
	
		boolean bOK = false;
		MCRSession sessionFromRequest = MCRServlet.getSession(getContext().getRequest());
		if(!sessionFromRequest.getID().equals(MCRSessionMgr.getCurrentSessionID())){
		    MCRSessionMgr.releaseCurrentSession();
		    MCRSessionMgr.setCurrentSession(sessionFromRequest);
		}
		
		if (!MCRAccessManager.checkPermission(mcrid, "writedb" )) {
			String lang   = MCRSessionMgr.getCurrentSession().getCurrentLanguage();
			String usererrorpage = "nav?path=~mycore-error?messageKey=WF.common.PrivilegesError&lang=" + lang;
			LOGGER.debug("Access denied for current user to start workflow for object " + mcrid);				
			return new ForwardResolution(MCRFrontendUtil.getBaseURL() + usererrorpage);
			
		}
		
		LOGGER.debug("Document MCRID = " + mcrid);
		
		if ( mcrid != null){
		    MCRObjectID mcrObjID = MCRObjectID.getInstance(mcrid);
		   
		    MCRWorkflowManager wfm = MCRWorkflowManagerFactory.getImpl(mcrObjID);
		    if(wfm!=null && MCRMetadataManager.exists(mcrObjID) ) {
				bOK = true;
				//initiator, mcrid, transition name
				wfm.initWorkflowProcessForEditing(MCRSessionMgr.getCurrentSession().getUserInformation().getUserID(),	mcrid);	
				String url = "nav?path=~workflow-" + wfm.getWorkflowProcessType();
				LOGGER.debug("nextpage = " + url);
				return new RedirectResolution(getContext().getResponse().encodeRedirectURL(MCRFrontendUtil.getBaseURL() + url));
			}
		}
		
		if ( !bOK) {
			String lang   = MCRSessionMgr.getCurrentSession().getCurrentLanguage();
			String usererrorpage = "mycore-error.jsp?messageKey=WF.xmetadiss.errorWfM&lang=" + lang;
			LOGGER.debug("The document (to open for editing) is not in the database: " + mcrid);				
			new RedirectResolution(MCRFrontendUtil.getBaseURL() + usererrorpage);
		}
		
		
		if(mcrid!=null){
			MCRObjectID mcrObjID = MCRObjectID.getInstance(mcrid);
			return new ForwardResolution("/searchresult.action?projectID="+mcrObjID.getProjectId()+"objectType="+mcrObjID.getTypeId());
		}
		return null;
	}
}
