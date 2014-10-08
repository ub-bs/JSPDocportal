package org.mycore.frontend.jsp.stripes.actions;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.controller.LifecycleStage;

import org.mycore.frontend.jsp.search.MCRSearchResultDataBean;

@UrlBinding("/searchresult.action")
public class SearchresultAction extends MCRAbstractStripesAction implements ActionBean {
	ForwardResolution fwdResolution = new ForwardResolution("/content/searchresult.jsp");

	private MCRSearchResultDataBean result;
	private Integer hit;
	private Integer start;

	public SearchresultAction() {

	}

	@Before(stages = LifecycleStage.BindingAndValidation)
	public void rehydrate() {
		super.rehydrate();
		if (getContext().getRequest().getParameter("_search") != null) {
			result = MCRSearchResultDataBean.retrieveSearchresultFromSession(getContext().getRequest(),  getContext().getRequest().getParameter("_search"));
		}
		if (getContext().getRequest().getParameter("_hit") != null) {
			try{
				hit = Integer.parseInt(getContext().getRequest().getParameter("_hit"));
			}
			catch(NumberFormatException nfe){
				hit = null;
			}
		}
		if (getContext().getRequest().getParameter("_start") != null) {
			try{
				start = Integer.parseInt(getContext().getRequest().getParameter("_start"));
				result.setStart(start);
			}
			catch(NumberFormatException nfe){
				start = null;
			}
		}
	}

	@DefaultHandler
	public Resolution defaultRes() {
		if(hit!=null && result!=null && hit<result.getNumFound()){
			String mcrid = result.getHit((int)hit);
			return new RedirectResolution("/resolve/id/"+mcrid+"?_search="+result.getId());
		}
		if(start!=null && result!=null){
			result.setStart(start);
			result.doSearch();
		}
		return fwdResolution;
	}

	
	public MCRSearchResultDataBean getResult() {
		return result;
	}

}
