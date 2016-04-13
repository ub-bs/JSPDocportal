package org.mycore.frontend.jsp.stripes.actions;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;

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
	
	public void rehydrate() {
		//place for initalization code
	}
}
