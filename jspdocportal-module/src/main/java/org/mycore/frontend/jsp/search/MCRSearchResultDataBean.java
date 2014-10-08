/*
 * $RCSfile$
 * $Revision: 19696 $ $Date: 2011-01-04 13:45:05 +0100 (Di, 04 Jan 2011) $
 *
 * This file is part of ***  M y C o R e  ***
 * See http://www.mycore.de/ for details.
 *
 * This program is free software; you can use it, redistribute it
 * and / or modify it under the terms of the GNU General Public License
 * (GPL) as published by the Free Software Foundation; either version 2
 * of the License or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program, in a file called gpl.txt or license.txt.
 * If not, write to the Free Software Foundation Inc.,
 * 59 Temple Place - Suite 330, Boston, MA  02111-1307 USA
 */
package org.mycore.frontend.jsp.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.map.LRUMap;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.SortClause;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.mycore.solr.MCRSolrServerFactory;

/**
 * bean that holds searchresult data
 * 
 * @author Robert Stephan
 *
 */
public class MCRSearchResultDataBean {
	private static Logger LOGGER = Logger.getLogger(MCRSearchResultDataBean.class);
	private String id;
	private int current=0;
	private long numFound;
	private int start;
	private int rows;
	private String query;
	private String sort;
	
	private List<String> mcrIDs = new ArrayList<String>();
	private String errorMsg = null;

	
	public MCRSearchResultDataBean(){
		this.id = UUID.randomUUID().toString();
	}
	
	public static void addSearchresultToSession(HttpServletRequest request, MCRSearchResultDataBean searchresult){
		@SuppressWarnings("unchecked")
		LRUMap<String, MCRSearchResultDataBean> map = (LRUMap<String, MCRSearchResultDataBean>)request.getSession().getAttribute("mcrSearchResultMap");
		if(map==null){
			map = new LRUMap<String, MCRSearchResultDataBean>(16);
			request.getSession().setAttribute("mcrSearchResultMap", map);
		}
		map.put(searchresult.getId(), searchresult);	
	}
	
	public static MCRSearchResultDataBean retrieveSearchresultFromSession(HttpServletRequest request, String searchID){
		@SuppressWarnings("unchecked")
		LRUMap<String, MCRSearchResultDataBean> map = (LRUMap<String, MCRSearchResultDataBean>)request.getSession().getAttribute("mcrSearchResultMap");
		if(map==null){
			return null;
		}
		return map.get(searchID);
	}
	
	public String getHit(int hit){
		if(hit<0 || hit>numFound) return null;
		int pos = hit-start;
		if(pos<0 || pos>=rows){
			start = hit / rows;
			doSearch();
			current = hit;
			return getHit(hit);
		}
		
		return mcrIDs.get(pos);
	}
	
	public void doSearch(){
		SolrServer solrServer = MCRSolrServerFactory.getSolrServer();
		SolrQuery solrQquery = new SolrQuery();
		solrQquery.setQuery(query);
		solrQquery.setRows(rows);
		solrQquery.setStart(start);
		if(!sort.isEmpty()){
			String[] x = sort.split("\\s|,");
			if(x.length>1){
				solrQquery.setSort(SortClause.create(x[0],  x[1]));
			}
		}

		try {
			if(query.length() > 0){
			QueryResponse response = solrServer.query(solrQquery);
			SolrDocumentList solrResults = response.getResults();

			
			setCurrent(start);
			setNumFound(solrResults.getNumFound());
			getMcrIDs().clear();
			Iterator<SolrDocument> it = solrResults.iterator();
			while (it.hasNext()) {
				SolrDocument doc = it.next();
				getMcrIDs().add(String.valueOf(doc.getFirstValue("returnId")));
			}
			}
		} catch (SolrServerException e) {
			LOGGER.error(e);
		}
	}
	
	//setter and getter methods
	
	public long getNumFound() {
		return numFound;
	}

	public void setNumFound(long numFound) {
		this.numFound = numFound;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
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
	
	public int getNumPages(){
		return Math.round((float)Math.ceil((float)numFound / rows)); 
	}

	public List<String> getMcrIDs() {
		return mcrIDs;
	}

	public void setMcrIDs(List<String> mcrIDs) {
		this.mcrIDs = mcrIDs;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public int getCurrent() {
		return current;
	}

	public void setCurrent(int current) {
		this.current = current;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
}
