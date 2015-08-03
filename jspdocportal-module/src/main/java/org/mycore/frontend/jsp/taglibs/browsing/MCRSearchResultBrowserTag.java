package org.mycore.frontend.jsp.taglibs.browsing;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;
import org.hibernate.Transaction;
import org.mycore.backend.hibernate.MCRHIBConnection;
import org.mycore.common.MCRSession;
import org.mycore.frontend.MCRFrontendUtil;
import org.mycore.frontend.jsp.search.MCRSearchResultDataBean;
import org.mycore.frontend.jsp.search.MCRSearchResultEntry;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.services.i18n.MCRTranslation;

/**
 * Tag to include an IndexBrowser into a web page implementation based on
 * 
 * @see org.mycore.frontend.indexbrowser.lucene.MCRIndexBrowserServlet
 * 
 * @author Robert Stephan
 * 
 */
public class MCRSearchResultBrowserTag extends SimpleTagSupport {
	private static final Logger LOGGER = Logger.getLogger(MCRSearchResultBrowserTag.class);

	private String varMCRID;
	private String varURL;
	private String varEntry;
	private String sortfields = "";
	private MCRSearchResultDataBean result;

	/**
	 * required: the name of the variable which holds the MCRObject as xml
	 * 
	 * @param name
	 *            - as string
	 */
	public String getVarmcrid() {
		return varMCRID;
	}

	public void setVarmcrid(String name) {
		this.varMCRID = name;
	}

	/**
	 * required: the name of the variable which holds the MCRObject as xml
	 * 
	 * @param name
	 *            - as string
	 */

	public String getVarurl() {
		return varURL;
	}

	public void setVarurl(String name) {
		this.varURL = name;
	}

	public void setVarentry(String name) {
		this.varEntry = name;
	}

	public void doTag() {

		Transaction t1 = null;
		String baseurl = MCRFrontendUtil.getBaseURL();
		try {
			Transaction tx = MCRHIBConnection.instance().getSession().getTransaction();
			if (tx == null || !tx.isActive()) {
				t1 = MCRHIBConnection.instance().getSession().beginTransaction();
			}

			PageContext ctx = (PageContext) getJspContext();
			MCRSession mcrSession = MCRServlet.getSession((HttpServletRequest) ctx.getRequest());

			JspWriter out = ctx.getOut();

			long numHits = result.getNumFound();

			if (sortfields.length() > 0 && numHits > 0) {
				writeResortForm(result, out, sortfields);
			}

			out.write("<div class=\"panel panel-default ur-searchresult-panel\">");
			if (numHits >= 0) {
				out.write("<div class=\"panel-heading\">");
				writePageNavigation(out, result);
				out.write("</div>");
			}
			if (numHits > 0) {
				out.write("<div class=\"panel-body ur-text\">");
				String path = (String) mcrSession.get("navPath");
				if (path == null) {
					path = "";
				}
				for (int j = 0; j < result.getEntries().size(); j++) {
					// out.write("<li class=\"list-group-item\">");
					MCRSearchResultEntry entry = result.getEntries().get(j);
					ctx.setAttribute(varEntry, entry);

					String mcrid = entry.getMcrid();
					ctx.setAttribute(varMCRID, mcrid);

					// http://localhost:8080/cpr/nav?id=cpr_professor_000000001451&offset=9&path=left.search.allmeta.searchresult-allmeta.docdetail&resultid=-xst2nllmkdqafx2bcdj2
					StringBuffer sbURL = new StringBuffer(baseurl);
					sbURL.append("resolve/id/").append(mcrid);
					sbURL.append("?_search=").append(result.getId());

					ctx.setAttribute(varURL, sbURL.toString());

					getJspBody().invoke(out);
					// out.write("</li>");
				}
				out.write("</div>");

				out.write("<div class=\"panel-footer\">");
				writePageNavigation(out, result);
				out.write("</div>");
			}
			out.write("</div>");
		} catch (Exception e) {
			LOGGER.error("The following exception was thrown in MCRSearchResultBrowserTag: ", e);
		} finally {
			if (t1 != null) {
				t1.commit();
			}
		}

	}

	private void writeResortForm(MCRSearchResultDataBean result, JspWriter out, String sortfields) throws IOException {
		String webBaseURL = MCRFrontendUtil.getBaseURL();

		out.write("<div class=\"panel panel-default\">");
		out.write("<div class=\"panel-body\">");
		out.write("<form style=\"margin-bottom:0px;\" action=\"" + webBaseURL + result.getAction()
				+ "\"	method=\"get\" accept-charset=\"UTF-8\">");
		out.write("   <input type=\"hidden\" name=\"_search\" value=\"" + URLEncoder.encode(result.getId(), "UTF-8")
				+ "\" />");
		String[] fieldnames = sortfields.trim().split("\\s");

		out.write(MCRTranslation.translate("Webpage.Searchresult.resort-label"));
		out.write("   <select name=\"sortfieldName\">");
		out.write("      <option value=\"" + "\"></option>");
		for (String fieldname : fieldnames) {
			if (fieldname.length() == 0) {
				continue;
			}
			out.write("      <option value=\"" + fieldname + "\"");
			if (result.getSort().startsWith(fieldname)) {
				out.write(" selected=\"true\"");
			}
			out.write(">");
			out.write(MCRTranslation.translate("Webpage.searchresults.sortfield." + fieldname));
			out.write("</option>");
		}
		out.write("   </select>&#160;&#160;&#160;");

		out.write("   <select name=\"sortfieldValue\">");
		for (String ordername : "asc desc".split("\\s")) {
			out.write("      <option value=\"" + ordername + "\"");
			if (result.getSort().endsWith(ordername)) {
				out.write(" selected=\"true\"");
			}
			out.write(">");
			out.write(MCRTranslation.translate("Webpage.Searchresult.order." + ordername));
			out.write("</option>");
		}
		out.write("   </select>&#160;&#160;&#160;");
		out.write("<input class=\"btn btn-primary btn-sm\" value=\""
				+ MCRTranslation.translate("Webpage.Searchresult.resort") + "\" type=\"submit\" />");
		out.write("</form></div></div>");

	}

	// 36.168 Publications Erste Seite | 11-20 | 21-30 | 31-40 | 41-50 | 51-60 |
	// Nächste Seite
	private void writePageNavigation(JspWriter out, MCRSearchResultDataBean result) throws IOException {
		String webBaseURL = MCRFrontendUtil.getBaseURL();
		PageContext pageContext = (PageContext) getJspContext();
		MCRSession mcrSession = MCRServlet.getSession((HttpServletRequest) pageContext.getRequest());
		String lang = mcrSession.getCurrentLanguage();
		ResourceBundle messages = MCRTranslation.getResourceBundle("messages", new Locale(lang));

		out.write("<!-- Searchresult PageNavigation -->");
		out.write("\n <ul class=\"pagination pull-right\" style=\"margin-top:-7px \">");

		if (result.getNumPages() > 1) {
			long page = Math.round(Math.floor((double) result.getStart() / result.getRows()) + 1);
			int start = 0;
			out.write("\n      <li><a href=\"" + webBaseURL + result.getAction() + "?_search=" + result.getId()
					+ "&amp;_start=" + start + "\">" + messages.getString("Webpage.Searchresult.firstPage")
					+ "</a></li>");
			if (page - 2 > 0) {
				start = result.getStart() - result.getRows() - result.getRows();
				out.write("\n      <li><a href=\"" + webBaseURL + result.getAction() + "?_search=" + result.getId()
						+ "&amp;_start=" + start + "\">" + Integer.toString(start + 1) + "-"
						+ Integer.toString(start + result.getRows()) + "</a></li>");
			}
			if (page - 1 > 0) {
				start = result.getStart() - result.getRows();
				out.write("\n      <li><a href=\"" + webBaseURL + result.getAction() + "?_search=" + result.getId()
						+ "&amp;_start=" + start + "\">" + Integer.toString(start + 1) + "-"
						+ Integer.toString(start + result.getRows()) + "</a></li>");
			}

			start = result.getStart();
			out.write("\n      <li class=\"active\"><a href=\"" + webBaseURL + result.getAction() + "?_search="
					+ result.getId() + "&amp;_start=" + start + "\">" + Integer.toString(start + 1) + "-"
					+ Long.toString(Math.min(start + result.getRows(), result.getNumFound())) + "</a></li>");

			if (page + 1 <= result.getNumPages()) {
				start = result.getStart() + result.getRows();
				out.write("\n      <li><a href=\"" + webBaseURL + result.getAction() + "?_search=" + result.getId()
						+ "&amp;_start=" + start + "\">" + Integer.toString(start + 1) + "-"
						+ Long.toString(Math.min(start + result.getRows(), result.getNumFound())) + "</a></li>");
			}
			if (page + 2 <= result.getNumPages()) {
				start = result.getStart() + result.getRows() + result.getRows();
				out.write("\n      <li><a href=\"" + webBaseURL + result.getAction() + "?_search=" + result.getId()
						+ "&amp;_start=" + start + "\">" + Integer.toString(start + 1) + "-"
						+ Long.toString(Math.min(start + result.getRows(), result.getNumFound())) + "</a></li>");
			}
			start = Math.round((result.getNumPages() - 1) * result.getRows());
			out.write("\n      <li class=\"item\"><a href=\"" + webBaseURL + result.getAction() + "?_search="
					+ result.getId() + "&amp;_start=" + start + "\">"
					+ messages.getString("Webpage.Searchresult.lastPage") + "</a></li>");
		}
		out.write("\n   </ul>");
		out.write(Long.toString(result.getNumFound()) + " " + messages.getString("Webpage.Searchresult.numHits"));
	}

	public String getSortfields() {
		return sortfields;
	}

	public void setSortfields(String sortfields) {
		this.sortfields = sortfields;
	}

	public MCRSearchResultDataBean getResult() {
		return result;
	}

	public void setResult(MCRSearchResultDataBean result) {
		this.result = result;
	}
}
