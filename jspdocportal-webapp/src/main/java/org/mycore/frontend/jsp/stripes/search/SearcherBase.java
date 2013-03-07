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
package org.mycore.frontend.jsp.stripes.search;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * base class for Searcher Object
 * 
 * @author Robert Stephan
 *
 */
public abstract class SearcherBase {
	private static DocumentBuilderFactory DOC_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();
	private String solrBaseURL;
	private String id;
	private String searchmaskURL;

	//Stripes seems to have problems reloading form content with other datatypes than Strings
	private int rows = 50;
	private int start = 0;
	private SearcherResultDataBean result = null;

	public SearcherBase() {

	}

	public SearcherBase(String solrBaseURL, String id, String searchmaskURL) {
		this.solrBaseURL = solrBaseURL;
		this.searchmaskURL = searchmaskURL;
		this.id = id;
	}

	protected void deepCopy(SearcherBase result) {
		result.solrBaseURL = this.solrBaseURL;
		result.id = this.id;
		result.searchmaskURL = this.searchmaskURL;
		result.rows = this.rows;
		result.start = this.start;
	}

	public abstract SearcherBase clone();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getRows() {
		return rows;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public SearcherResultDataBean getResult() {
		return result;
	}

	public String getSearchmaskURL() {
		return searchmaskURL + "?_searcher=" + getId();
	}

	public void reset() {
		start = 0;
		rows = 50;
		result = null;
	}

	public String getSolrQuery() {
		StringBuffer sb = new StringBuffer();
		sb.append(solrBaseURL);
		sb.append("?q=");
		sb.append(getGeneratedSearchQuery());
		String sort = getGeneratedSortQuery();
		if (sort != null) {
			sb.append("&sort=");
			sb.append(sort);
		}
		sb.append("&rows=").append(rows);
		sb.append("&start=").append(start);

		return sb.toString();
	}

	public void doSearch() {
		result = new SearcherResultDataBean();
		try {
			DocumentBuilder docBuilder = DOC_BUILDER_FACTORY.newDocumentBuilder();
			URL url = new URL(getSolrQuery());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			InputStream is = null;
			if (conn.getResponseCode() == 400) {
				is = conn.getErrorStream();
			} else {
				is = conn.getInputStream();
			}
			Document doc = docBuilder.parse(is);
			doc.getDocumentElement().normalize();

			result.setXml(doc);
			result.setNumFound(Integer.parseInt((((Element) doc.getDocumentElement().getElementsByTagName("result")
			        .item(0)).getAttribute("numFound"))));

		} catch (Exception e) {
			result.setErrorMsg(e.getMessage());
		}
	}

	protected String prepareSearchterm(String term) {

		// replace special characters:
		// http://lucene.apache.org/java/3_6_0/queryparsersyntax.html#Escaping%20Special%20Characters
		// + - && || ! ( ) { } [ ] ^ " ~ * ? : \" +
		term = term.replace("\\", "\\\\").replace("+", "\\+").replace("-", "\\-").replace("&", "\\&");
		term = term.replace("|", "\\|").replace("!", "\\!").replace("(", "\\(").replace(")", "\\)");
		term = term.replace("{", "\\{").replace("}", "\\}").replace("[", "\\[").replace("]", "\\]");
		term = term.replace("^", "\\^").replace("~", "\\~").replace("?", "\\?").replace(":", "\\:");

		if (term.contains(" ")) {
			term = "(" + term + ")";
		}
		return term;
	}

	public abstract String getGeneratedSearchQuery();

	public abstract String getGeneratedSortQuery();

	public String getSolrBaseURL() {
		return solrBaseURL;
	}
}
