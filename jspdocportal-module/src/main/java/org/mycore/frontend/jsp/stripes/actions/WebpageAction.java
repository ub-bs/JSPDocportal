package org.mycore.frontend.jsp.stripes.actions;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.controller.LifecycleStage;

/**
 * Action that can be used to display html content.
 * 
 * The following parameters can be used:
 * 
 * - show: redirects to a jsp in content directory
 *         The suffix ".jsp" will be added automatically
 *         Subdirectories can be simmulated by "." 
 *         ?show=info.aktuelles would redirect to /content/info/aktuelles.jsp
 *         
 *   main: identifier for text block (stored in mcr-data directory)
 *         which defines the main text content
 *  
 *  info: comma separated list of identifiers for text blocks (stored in mcr-data-directory)
 *         which can be used for info blocks (at the right side)
 *          
 * @author Stephan
 *
 */
@UrlBinding("/webpage.action")
public class WebpageAction extends MCRAbstractStripesAction implements ActionBean {
	
	private String show = "start";
	
	String main = null;
	String info = null;
	

	public WebpageAction() {

	}

	@Before(stages = LifecycleStage.BindingAndValidation)
	public void rehydrate() {
		super.rehydrate();
		if (getContext().getRequest().getParameter("show") != null) {
			show = cleanParameter(getContext().getRequest().getParameter("show"));
		}
		if (getContext().getRequest().getParameter("main") != null) {
			main = cleanParameter(getContext().getRequest().getParameter("main"));
		}
		if (getContext().getRequest().getParameter("info") != null) {
			info = cleanParameter(getContext().getRequest().getParameter("info"));
		}
	}

	@DefaultHandler
	public Resolution defaultRes() {
		if(main == null){
			return new ForwardResolution("/content/"+show.replace(".", "/")+".jsp");
		}
		else{
			return new ForwardResolution("/content/webpage.jsp");
		}
	}
	
	private String cleanParameter(String s){
		return s.replaceAll("[^a-zA-Z_0-9.,]", "");
	}

	public String getShow() {
		return show;
	}

	public String getMain() {
		return main;
	}

	public String getInfo() {
		return info;
	}
	
}
