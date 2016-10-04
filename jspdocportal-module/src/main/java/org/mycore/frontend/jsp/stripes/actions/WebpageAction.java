package org.mycore.frontend.jsp.stripes.actions;

import org.apache.commons.lang3.StringUtils;
import org.mycore.common.config.MCRConfiguration;

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
@UrlBinding("/site/{path}")
public class WebpageAction extends MCRAbstractStripesAction implements ActionBean {
	
    private String path;
	
	String info = null;
	

	public WebpageAction() {

	}

	@Before(stages = LifecycleStage.BindingAndValidation)
	public void rehydrate() {
		super.rehydrate();
		if (getContext().getRequest().getParameter("info") != null) {
			info = cleanParameter(getContext().getRequest().getParameter("info"));
		}
	}

	@DefaultHandler
	public Resolution defaultRes() {
	    if(path!=null){
	        path = path.replace("\\", "/");
	        if(!path.contains("..") && StringUtils.countMatches(path, "/")<=3){
	            String navPath = MCRConfiguration.instance().getString("MCR.Webpage.Navigation."+path.replace("/",  "."), null);
	            if(navPath!=null){
	                getContext().getRequest().setAttribute("org.mycore.navigation.path",  navPath);
	            }
	            return new ForwardResolution(MCRConfiguration.instance().getString("MCR.Webpage.Resolution.default", "/content/webpage.jsp"));
	        }
	    }
	    return new ForwardResolution("/");
	    
	}
	
	private String cleanParameter(String s){
		return s.replaceAll("[^a-zA-Z_0-9.,]", "");
	}


	public String getInfo() {
		return info;
	}

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
	
}
