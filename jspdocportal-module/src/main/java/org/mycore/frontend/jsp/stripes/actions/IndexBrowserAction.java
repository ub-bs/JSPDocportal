package org.mycore.frontend.jsp.stripes.actions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.frontend.jsp.search.MCRSearchResultDataBean;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.controller.LifecycleStage;

@UrlBinding("/indexbrowser.action")
public class IndexBrowserAction extends MCRAbstractStripesAction implements ActionBean {
	private static Logger LOGGER = Logger.getLogger(IndexBrowserAction.class);
	ForwardResolution fwdResolution = new ForwardResolution("/content/indexbrowser.jsp");

	private List<String> firstSelector = Arrays.asList(new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
			"K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" });
	private Map<String, Long> secondSelector = new TreeMap<String, Long>();
	private String modus = "";
	private String select;
	private MCRSearchResultDataBean mcrSearchResult;

	public IndexBrowserAction() {

	}

	@Before(stages = LifecycleStage.BindingAndValidation)
	public void rehydrate() {
		super.rehydrate();
		if (getContext().getRequest().getParameter("modus") != null) {
			modus = getContext().getRequest().getParameter("modus");
		}
		if (getContext().getRequest().getParameter("select") != null) {
			select = getContext().getRequest().getParameter("select");
		}

	}

	@DefaultHandler
	public Resolution defaultRes() {
		MCRConfiguration config = MCRConfiguration.instance();

		if (select != null) {
			SolrQuery query = new SolrQuery();
			String searchfield = config.getString("MCR.IndexBrowser." + modus + ".Searchfield");
			String facetfield = config.getString("MCR.IndexBrowser." + modus + ".Facetfield");
			query.setQuery(searchfield + ":" + select + "*");
			query.addFacetField(facetfield);
			query.addSort(searchfield, ORDER.asc);

			
			mcrSearchResult = new MCRSearchResultDataBean();
			mcrSearchResult.setSolrQuery(query);
			mcrSearchResult.setRows(Integer.MAX_VALUE);
			mcrSearchResult.setStart(0);
			mcrSearchResult.setAction("search");
			mcrSearchResult.doSearch();
			MCRSearchResultDataBean.addSearchresultToSession(getContext().getRequest(), mcrSearchResult);

			QueryResponse response = mcrSearchResult.getSolrQueryResponse();
			if (response != null) {
				SolrDocumentList solrResults = response.getResults();

				List<FacetField> facets = response.getFacetFields();
				secondSelector.clear();
				if (solrResults.getNumFound() > 20 || select.length() > 1) {
					for (Count c : facets.get(0).getValues()) {
						if (c.getCount() > 0) {
							secondSelector.put(c.getName(), c.getCount());
						}
					}
				}
				if (solrResults.getNumFound() > 20 && select.length() <= 1) {
					//do not display entries, show 2nd selector instead
					mcrSearchResult.getEntries().clear();
				}
			}
		}
		return fwdResolution;

	}

	public List<String> getFirstSelector() {
		return firstSelector;
	}

	public String getModus() {
		return modus;
	}

	public void setModus(String modus) {
		this.modus = modus;
	}

	public String getSelect() {
		return select;
	}

	public void setSelect(String select) {
		this.select = select;
	}

	public Map<String, Long> getSecondSelector() {
		return secondSelector;
	}

	public MCRSearchResultDataBean getResult() {
		return mcrSearchResult;
	}

}
