package org.mycore.frontend.jsp.stripes.actions;

import java.util.Iterator;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.controller.LifecycleStage;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.SortClause;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.mycore.frontend.jsp.stripes.search.MCRSearcherResultDataBean;
import org.mycore.solr.MCRSolrServerFactory;

@UrlBinding("/simpleSearch.action")
public class SimpleSearchAction extends MCRAbstractStripesAction implements ActionBean {
	public static int DEFAULT_ROWS = Integer.MAX_VALUE;
	private static Logger LOGGER = Logger.getLogger(SimpleSearchAction.class);
	ForwardResolution fwdResolution = new ForwardResolution("/content/searchresult.jsp");
	                                                             
	ForwardResolution fwdResolutionForm = new ForwardResolution("/content/search/searchSimple.jsp");

	private String q = "";
	private String sort="";
	private String searchfieldName = "";
	private String searchfieldValue = "";
	private String sortfieldName="";
	private String sortfieldDirection="";
	private int start = 0;
	private int rows = DEFAULT_ROWS;
	private MCRSearcherResultDataBean result;

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
		SolrServer solrServer = MCRSolrServerFactory.getSolrServer();
		SolrQuery query = new SolrQuery();
		query.setQuery(q);
		query.setRows(rows);
		query.setStart(start);
		if(!sort.isEmpty()){
			String[] x = sort.split("\\s|,");
			if(x.length>1){
				query.setSort(SortClause.create(x[0],  x[1]));
			}
		}

		try {
			if(q.length() > 0){
			QueryResponse response = solrServer.query(query);
			SolrDocumentList solrResults = response.getResults();

			result = new MCRSearcherResultDataBean();
			result.setStart(start);
			result.setRows(rows);
			result.setQuery(q);
			result.setSort(sort);
			
			result.setCurrent(0);
			result.setNumFound(solrResults.getNumFound());
			Iterator<SolrDocument> it = solrResults.iterator();
			while (it.hasNext()) {
				SolrDocument doc = it.next();
				result.getMcrIDs().add(String.valueOf(doc.getFirstValue("returnId")));
			}
			}
		} catch (SolrServerException e) {
			LOGGER.error(e);
		}
		return fwdResolution;
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

	public MCRSearcherResultDataBean getResult() {
		return result;
	}

	public void setResult(MCRSearcherResultDataBean result) {
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
