package org.mycore.frontend.jsp.stripes.actions;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.controller.LifecycleStage;

@UrlBinding("/classbrowser.action")
public class ClassBrowserAction extends MCRAbstractStripesAction implements ActionBean {
	
	private String modus = "";
	
	public ClassBrowserAction() {

	}

	@Before(stages = LifecycleStage.BindingAndValidation)
	public void rehydrate() {
		super.rehydrate();
		if (getContext().getRequest().getParameter("modus") != null) {
			modus = getContext().getRequest().getParameter("modus");
		}
		
	}

	@DefaultHandler
	public Resolution defaultRes() {
	

		
		return new ForwardResolution("/content/classbrowser.jsp");

	}

	public String getModus() {
		return modus;
	}


}
