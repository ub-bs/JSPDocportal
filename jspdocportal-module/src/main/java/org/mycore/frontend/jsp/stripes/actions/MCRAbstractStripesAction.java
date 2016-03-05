package org.mycore.frontend.jsp.stripes.actions;

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
		MCRSessionMgr.switchCurrentSession(MCRServlet.getSession(context.getRequest()));
		/*
		 * old code - used Java Reflection to set the MCRSession into the thread
		MCRSession mcrSessionFromRequest = MCRServlet.getSession(context.getRequest());
		if (mcrSessionFromRequest == null || mcrSessionFromRequest.getID() == null ) {
			LOGGER.debug("The HTTP Session does not contain an MCRSession object");
		} else {

			if (!mcrSessionFromRequest.getID().equals(MCRSessionMgr.getCurrentSessionID())) {
				if (MCRSessionMgr.hasCurrentSession()) {
					MCRSessionMgr.releaseCurrentSession();
				}
				
				// there seems to be no other way than setting the current MCRSession
				// with Java Reflection into the MCRSessionMgr
				try{
					Field fS = MCRSessionMgr.class.getDeclaredField("theThreadLocalSession");
					fS.setAccessible(true);
					@SuppressWarnings("unchecked")
					ThreadLocal<MCRSession> thlS = (ThreadLocal<MCRSession>)fS.get(null);
					thlS.set(mcrSessionFromRequest);
					
					Field fB = MCRSessionMgr.class.getDeclaredField("isSessionAttached");
					fB.setAccessible(true);
					@SuppressWarnings("unchecked")
					ThreadLocal<Boolean> thlB = (ThreadLocal<Boolean>)fB.get(null);
					thlB.set(Boolean.TRUE);
				}
				catch(ReflectiveOperationException e){
					LOGGER.error(e);
				}
			}
		}
		*/
	}
}
