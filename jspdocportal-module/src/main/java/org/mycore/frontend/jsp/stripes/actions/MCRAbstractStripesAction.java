package org.mycore.frontend.jsp.stripes.actions;

import java.lang.reflect.Field;

import org.apache.log4j.Logger;
import org.mycore.common.MCRSession;
import org.mycore.common.MCRSessionMgr;
import org.mycore.frontend.servlets.MCRServlet;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.controller.LifecycleStage;

public class MCRAbstractStripesAction implements ActionBean {
	private static Logger LOGGER = Logger.getLogger(MCRAbstractStripesAction.class);
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
		if (mcrSessionFromRequest == null) {
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
	}
}
