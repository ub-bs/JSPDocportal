package org.mycore.frontend.jsp.stripes.actions;

import org.mycore.common.MCRSession;
import org.mycore.common.MCRSessionMgr;
import org.mycore.frontend.servlets.MCRServlet;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.controller.LifecycleStage;

public class MCRAbstractStripesAction implements ActionBean {
	private ActionBeanContext context;
	
	@Override
	public ActionBeanContext getContext() {
		return context;
	}

	@Override
	public void setContext(ActionBeanContext context) {
		this.context = context;
	}
	
	@Before(stages = LifecycleStage.BindingAndValidation)
	public void rehydrate() {
		MCRSession mcrSessionFromRequest = MCRServlet.getSession(context.getRequest());
		if(!mcrSessionFromRequest.getID().equals(MCRSessionMgr.getCurrentSessionID())){
			MCRSessionMgr.releaseCurrentSession();
			MCRSessionMgr.setCurrentSession(mcrSessionFromRequest);
		}
	}
}
