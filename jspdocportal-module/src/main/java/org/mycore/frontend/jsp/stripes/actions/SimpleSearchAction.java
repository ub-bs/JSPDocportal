package org.mycore.frontend.jsp.stripes.actions;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.controller.LifecycleStage;

import org.apache.commons.lang.StringUtils;
import org.mycore.frontend.jsp.search.MCRSearchResultDataBean;

@UrlBinding("/simpleSearch.action")
public class SimpleSearchAction extends MCRAbstractStripesAction implements ActionBean {
	public static int DEFAULT_ROWS = Integer.MAX_VALUE;
	
                    
	ForwardResolution fwdResolutionForm = new ForwardResolution("/content/search/searchSimple.jsp");

	private String q = "";
	private String sort="";
	private String searchfieldName = "";
	private String searchfieldValue = "";
	private String sortfieldName="";
	private String sortfieldDirection="";
	private int start = 0;
	private int rows = DEFAULT_ROWS;
	private MCRSearchResultDataBean result;

	public SimpleSearchAction() {

	}

	@Before(stages = LifecycleStage.BindingAndValidation)
	public void rehydrate() {
		super.rehydrate();
		if (getContext().getRequest().getParameter("q") != null) {
			q = getContext().getRequest().getParameter("q");
		}
		if (getContext().getRequest().getParameter("sort") != null) {
			sort = getContext().getRequest().getParameter("sort");
		}
		
		if (getContext().getRequest().getParameter("rows") != null) {
			try {
				rows = Integer.valueOf(getContext().getRequest().getParameter("rows"));
			} catch (NumberFormatException nfe) {
				rows = DEFAULT_ROWS;
			}
		}
		if (getContext().getRequest().getParameter("start") != null) {
			try {
				start = Integer.valueOf(getContext().getRequest().getParameter("start"));
			} catch (NumberFormatException nfe) {
				start = 0;
			}
		}

	}

	@DefaultHandler
	public Resolution defaultRes() {
		result = new MCRSearchResultDataBean();
		if (q.isEmpty()) {
			if(StringUtils.isNotEmpty(searchfieldName) && StringUtils.isNotEmpty(searchfieldValue)){
				q = searchfieldName + ":" + searchfieldValue;
			}
		}
		if(q.length()==0){
			return fwdResolutionForm;
		}
		if (sort.isEmpty()) {
			if(StringUtils.isNotEmpty(sortfieldName) && StringUtils.isNotEmpty(sortfieldDirection)){
				sort = sortfieldName + " " + sortfieldDirection;
			}
		}
		result.setQuery(q);
		result.setSort(sort);
		result.setStart(start);
		result.setRows(rows);
		MCRSearchResultDataBean.addSearchresultToSession(getContext().getRequest(), result);
		result.doSearch();
		return new ForwardResolution("/searchresult.action?_search="+result.getId());
	}

	public String getQ() {
		return q;
	}

	public void setQ(String q) {
		this.q = q;
	}

	public String getSearchfieldName() {
		return searchfieldName;
	}

	public void setSearchfieldName(String searchfieldName) {
		this.searchfieldName = searchfieldName;
	}

	public String getSearchfieldValue() {
		return searchfieldValue;
	}

	public void setSearchfieldValue(String searchfieldValue) {
		this.searchfieldValue = searchfieldValue;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public MCRSearchResultDataBean getResult() {
		return result;
	}

	public void setResult(MCRSearchResultDataBean result) {
		this.result = result;
	}

	public String getSortfieldName() {
		return sortfieldName;
	}

	public void setSortfieldName(String sortfieldName) {
		this.sortfieldName = sortfieldName;
	}

	public String getSortfieldDirection() {
		return sortfieldDirection;
	}

	public void setSortfieldDirection(String sortfieldDirection) {
		this.sortfieldDirection = sortfieldDirection;
	}

}
