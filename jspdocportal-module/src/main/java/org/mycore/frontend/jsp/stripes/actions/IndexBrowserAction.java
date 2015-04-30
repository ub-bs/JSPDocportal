package org.mycore.frontend.jsp.stripes.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.controller.LifecycleStage;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.frontend.jsp.stripes.actions.util.IndexBrowserResultObject;
import org.mycore.solr.MCRSolrClientFactory;

@UrlBinding("/indexbrowser.action")
public class IndexBrowserAction extends MCRAbstractStripesAction implements ActionBean {
	private static Logger LOGGER = Logger.getLogger(IndexBrowserAction.class);
	ForwardResolution fwdResolution = new ForwardResolution("/content/indexbrowser.jsp");

	private List<String> firstSelector = Arrays.asList(new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
			"S", "T", "U", "V", "W", "X", "Y", "Z" });
	private Map<String, Long> secondSelector = new TreeMap<String, Long>();
	private String modus = "";
	private String select;
	private List<IndexBrowserResultObject> results = new ArrayList<IndexBrowserResultObject>();

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
	
		if(select!=null){
			SolrClient solrClient = MCRSolrClientFactory.getSolrClient();
			SolrQuery query = new SolrQuery();
			String searchfield = config.getString("MCR.IndexBrowser."+modus+".Searchfield");
		    query.setQuery(searchfield+":"+select+"*");
		    query.addFacetField(searchfield+"_facet");
		    query.addSort(searchfield, ORDER.asc);
		    query.setRows(Integer.MAX_VALUE);
		    query.setStart(0);    
		 
		    try{
		    QueryResponse response = solrClient.query(query);
		    SolrDocumentList solrResults = response.getResults();
		   
		    List<FacetField> facets = response.getFacetFields();
		    secondSelector.clear();
		    if(solrResults.getNumFound()>20 || select.length()>1){
		    for(Count c: facets.get(0).getValues()){
		    	if(c.getCount()>0){
		    		secondSelector.put(c.getName(), c.getCount());
		    	}
		     }
		    }
		    results.clear();
		    if(solrResults.getNumFound()<=20 || select.length()>1){
		    	String labelfield = config.getString("MCR.IndexBrowser."+modus+".Labelfield");
				String[]datafields = config.getString("MCR.IndexBrowser."+modus+".Datafields").split(",");
		    	for(int i=0;i<solrResults.size();i++){
		    		SolrDocument solrDoc = solrResults.get(i);
		    		IndexBrowserResultObject ibro = new IndexBrowserResultObject(String.valueOf(solrDoc.getFirstValue("id")), String.valueOf(solrDoc.getFirstValue(labelfield)));
		    		for(String df: datafields){
		    			Object o = solrDoc.getFirstValue(df);
		    			if(o!=null){
		    				ibro.addData(df, String.valueOf(o));
		    			}
		    		}
		    		results.add(ibro);
		    	}
		    			
		    }
		    }
		    catch(SolrServerException e){
		    	LOGGER.error(e);
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

	public List<IndexBrowserResultObject> getResults() {
		return results;
	}

}
