package org.mycore.frontend.jsp.stripes.actions;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.controller.LifecycleStage;

@UrlBinding("/webpage.action")
public class WebpageAction extends MCRAbstractStripesAction implements ActionBean {
	
	private String id = "start";

	public WebpageAction() {

	}

	@Before(stages = LifecycleStage.BindingAndValidation)
	public void rehydrate() {
		super.rehydrate();
		if (getContext().getRequest().getParameter("id") != null) {
			id = getContext().getRequest().getParameter("id");
		}
	}

	@DefaultHandler
	public Resolution defaultRes() {
	    ForwardResolution fwdResolution = new ForwardResolution("/content/"+id.replace(".", "/")+".jsp");
	    return fwdResolution;
	}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
