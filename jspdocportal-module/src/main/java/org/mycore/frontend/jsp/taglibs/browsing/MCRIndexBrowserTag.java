package org.mycore.frontend.jsp.taglibs.browsing;

import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.Filters;
import org.jdom2.output.DOMOutputter;
import org.jdom2.xpath.XPathFactory;
import org.mycore.common.MCRSession;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.services.i18n.MCRTranslation;

import com.ibm.icu.text.MessageFormat;

/**
 * Tag to include an IndexBrowser into a web page implementation based on
 * 
 * @see org.mycore.frontend.indexbrowser.lucene.MCRIndexBrowserServlet
 * 
 * @author Robert Stephan
 * 
 */
public class MCRIndexBrowserTag extends SimpleTagSupport {
	private static final Logger LOGGER = Logger.getLogger(MCRIndexBrowserTag.class);
	
	private String indexname;
	private String varURLName;
	private String varXMLName;
	private String docdetailsURL;

	//protected MCRIndexBrowserIncomingData incomingBrowserData;
	//protected MCRIndexBrowserConfig config;

	/**
	 * required: the url which should be used to link to docdetails page of a
	 * single item
	 * 
	 * @param url
	 *            - the url should contain a parameter "{0}", which will be
	 *            filled with the current document id
	 */
	public String getDocdetailsurl() {
		return docdetailsURL;
	}

	public void setDocdetailsurl(String url) {
		this.docdetailsURL = url;
	}

	/**
	 * required: the searchclass, the identifier which is used in MCR properties
	 * to set parameters belong to a category
	 * 
	 * @param searchfield
	 *            - as string
	 */
	public String getIndex() {
		return indexname;
	}

	public void setIndex(String indexname) {
		this.indexname = indexname;
	}

	/**
	 * required: the name of the variable which returns the index item as xml
	 * 
	 * @param name
	 *            - as string
	 */
	public String getVarxml() {
		return varXMLName;
	}

	public void setVarxml(String name) {
		this.varXMLName = name;
	}

	/**
	 * required: the name of the variable which returns the url to docdetails
	 * belong to a category
	 * 
	 * @param searchfield
	 *            - as string
	 */
	public String getVarurl() {
		return varURLName;
	}

	public void setVarurl(String name) {
		this.varURLName = name;
	}

	public void doTag() {
		try {
		    MCRSession mcrSession = MCRServlet.getSession((HttpServletRequest)((PageContext) getJspContext()).getRequest());
	        String lang = mcrSession.getCurrentLanguage();
			ResourceBundle messages = MCRTranslation.getResourceBundle("messages", new Locale(lang));
			
			PageContext ctx = (PageContext) getJspContext();
			Document pageContent = null;
			
/*			incomingBrowserData = MCRIndexBrowserUtils.getIncomingBrowserData((HttpServletRequest) ctx.getRequest());
			config = new MCRIndexBrowserConfig(incomingBrowserData.getSearchclass());
			
			// if init is true, then create an empty document, otherwise create
			// the result list
			if (!incomingBrowserData.isInit()) {
				pageContent = MCRIndexBrowserUtils.createResultListDocument(incomingBrowserData, config);
			} else {
				pageContent = MCRIndexBrowserUtils.createEmptyDocument(incomingBrowserData);
			}
*/
			String webApplicationBaseURL = MCRServlet.getBaseURL();

			String subselect_varpath = ctx.getRequest().getParameter("XSL.subselect.varpath.SESSION");
			if (subselect_varpath == null) {
				subselect_varpath = "";
			}
			String subselect_session = ctx.getRequest().getParameter("XSL.subselect.session.SESSION");
			if (subselect_session == null) {
				subselect_session = "";
			}
			String subselect_webpage = ctx.getRequest().getParameter("XSL.subselect.webpage.SESSION");
			if (subselect_webpage == null) {
				subselect_webpage = "";
			}
			String prevFromTo = ctx.getRequest().getParameter("prevFromTo");
			if (prevFromTo == null) {
				prevFromTo = "";
			}
			boolean isSubselect = !subselect_varpath.equals("");
			// start creating output
			String search = "";
			Attribute attrSearch = XPathFactory.instance().compile("/indexpage/results/@search", Filters.attribute()).evaluateFirst(pageContent);
			if (attrSearch != null) {
				search = attrSearch.getValue();
			}

			String mode = XPathFactory.instance().compile("/indexpage/results/@mode", Filters.attribute()).evaluateFirst(pageContent).getValue();

			StringBuffer sbParams = new StringBuffer();
			sbParams.append("XSL.subselect.session.SESSION=");
			sbParams.append(subselect_session);
			sbParams.append("&amp;XSL.subselect.varpath.SESSION=");
			sbParams.append(subselect_varpath);
			sbParams.append("&amp;XSL.subselect.webpage.SESSION=");
			sbParams.append(URLEncoder.encode(subselect_webpage, "UTF-8"));

			String subselect_params = sbParams.toString();
			JspWriter out = getJspContext().getOut();
			out.write("\n<div class=\"index-browser\">\n");
			// cancel subselect
			if (isSubselect) {
				out.write("<form action=\"" + webApplicationBaseURL + subselect_webpage + "XSL.editor.session.id=" + subselect_session
						+ "\" method=\"post\">\n");
				out.write("   <input type=\"submit\" class=\"submit\" value=\"" + messages.getString("Editor.Common.button.CancelSelect") + "\" />\n");
				out.write("</form><br/><br/>\n");
			}

			// A-Z search bar
			String[] atoz = new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W",
					"X", "Y", "Z" };
			out.write("|");
			for (String prefix : atoz) {
				StringBuffer sbUrl = new StringBuffer(webApplicationBaseURL);
				sbUrl.append("nav?path=~searchstart-index_");
				sbUrl.append(indexname);
				sbUrl.append("&amp;search=");
				sbUrl.append(prefix);
				if (isSubselect) {
					sbUrl.append("&amp;" + subselect_params);
				}
				out.write("  <a href=\"" + sbUrl.toString() + "\">" + prefix + "</a> |\n");
			}
			out.write("<br/><br/>\n");
			// Searchbox
			out.write("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tr>\n");
			out.write("  <td class=\"metavalue\">\n");
			out.write("    <form action=\"" + webApplicationBaseURL + "nav?path=~searchstart-index_" + indexname + "\" method=\"post\">\n");
			out.write("       " + messages.getString("Editor.Common.index.filter") + "\n");
			out.write("         <select name=\"mode\" size=\"1\" class=\"button\">\n");
			out.write("             <option value=\"contains\"");
			if ("contains".equals(mode)) {
				out.write(" selected=\"selected\"");
			}
			out.write(">" + messages.getString("Editor.Common.index.contains") + "</option>");

			out.write("             <option value=\"prefix\"");
			if ("prefix".equals(mode)) {
				out.write(" selected=\"selected\"");
			}
			out.write(">" + messages.getString("Editor.Common.index.prefix") + "</option></select>");
			out.write("         <input type=\"text\" class=\"button\" size=\"30\" name=\"search\" value=\"" + search + "\" />\n");
			if (isSubselect) {
				out.write("         <input type=\"hidden\" name=\"XSL.subselect.session.SESSION\" value=\"" + subselect_session + "\" />\n");
				out.write("         <input type=\"hidden\" name=\"XSL.subselect.varpath.SESSION\" value=\"" + subselect_varpath + "\" />\n");
				out.write("         <input type=\"hidden\" name=\"XSL.subselect.webpage.SESSION\" value=\"" + subselect_webpage + "\" />\n");
			}
			out.write("         <input type=\"submit\" class=\"button\" value=\"" + messages.getString("Editor.Search.search") + "\" />\n");
			out.write("     </form>\n");
			out.write("  </td>\n");
			if (search.length() > 0) {
				out.write("  <td class=\"metavalue\">\n");
				out.write("      <form action=\"" + webApplicationBaseURL + "nav?path=~searchstart-index_" + indexname + "\" method=\"post\">\n");
				out.write(XPathFactory.instance().compile("/indexpage/results/@numHits", Filters.attribute()).evaluateFirst(pageContent).getValue() + " "
						+ messages.getString("Editor.Common.index.searchhits") + "\n");
				if (isSubselect) {
					out.write("         <input type=\"hidden\" name=\"XSL.subselect.session.SESSION\" value=\"" + subselect_session + "\" />\n");
					out.write("         <input type=\"hidden\" name=\"XSL.subselect.varpath.SESSION\" value=\"" + subselect_varpath + "\" />\n");
					out.write("         <input type=\"hidden\" name=\"XSL.subselect.webpage.SESSION\" value=\"" + subselect_webpage + "\" />\n");
				}
				out.write("         <input type=\"submit\" class=\"button\" value=\"" + messages.getString("Editor.Common.index.releasefilter") + "\" />\n");
				out.write("      </form>\n");
				out.write("  </td>\n");
			}
			out.write("</tr>\n");

			// Results
			String upFromTo = "";
			Attribute attrLast = XPathFactory.instance().compile("/indexpage/results/range[last()]/to/@pos", Filters.attribute()).evaluateFirst(pageContent);
			Attribute attrFirst = XPathFactory.instance().compile("/indexpage/results/range[1]/from/@pos", Filters.attribute()).evaluateFirst(pageContent);

			if (attrLast != null && attrFirst != null) {
				int last = Integer.parseInt(attrLast.getValue());
				int first = Integer.parseInt(attrFirst.getValue());
				int numHits = Integer.parseInt(XPathFactory.instance().compile("/indexpage/results/@numHits", Filters.attribute()).evaluateFirst(pageContent)
						.getValue());
				if (last + 1 - first != numHits) {
					upFromTo = "" + first + "-" + last;
				}
			}
			StringBuffer sbUpUrl = new StringBuffer(webApplicationBaseURL);
			sbUpUrl.append("nav?path=~searchstart-index_" + getIndex());
			sbUpUrl.append("&amp;fromTo=");
			try {
				sbUpUrl.append(prevFromTo.substring(0, prevFromTo.indexOf(".")));
			} catch (IndexOutOfBoundsException e) {
				sbUpUrl.append("");
			}
			sbUpUrl.append("&amp;prevFromTo=");
			try {
				sbUpUrl.append(prevFromTo.substring(prevFromTo.indexOf(".") + 1));
			} catch (IndexOutOfBoundsException e) {
				sbUpUrl.append("");
			}
			if (isSubselect) {
				sbUpUrl.append("&amp;" + subselect_params);
			}
			// http://localhost/cpr/nav?path=~searchstart-index_professorum&fromTo=24-31&prevFromTo=.
			// &search=M&mode=prefix

			if (search.length() > 0) {
				sbUpUrl.append("&amp;search=").append(search);
				sbUpUrl.append("&amp;mode=").append(mode);
			}
			out.write("<tr><td class=\"metavalue\">");
			out.write("<dl><dt>");
			String path = XPathFactory.instance().compile("/indexpage/@path", Filters.attribute()).evaluateFirst(pageContent).getValue();
			out.write("<img border=\"0\" src=\"" + webApplicationBaseURL + "images/folder_plain.gif\" align=\"middle\"/>");
			if (path.contains("-")) {
				out.write("<span class=\"return\"><a class=\"nav\" href=\"" + sbUpUrl.toString() + "\">" + messages.getString("Editor.Common.index.return")
						+ "</a></span>");
			} else if (search.length() > 0) {
				out.write("<span class=\"title\">" + messages.getString("Editor.Common.index.result.filtered") + "</span>");
			} else {
				out.write("<span class=\"title\">" + messages.getString("Editor.Common.index.result") + "</span>");
			}
			out.write("</dt>\n");
			List<Element> lE = pageContent.getRootElement().getChild("results").getChildren();
			MessageFormat formDocdetailsURL = new MessageFormat(getDocdetailsurl());

			DOMOutputter domOut = new DOMOutputter();
			for (Element e : lE) {
				if (e.getName().equals("range")) {
					StringBuffer sbUrl = new StringBuffer(webApplicationBaseURL);
					sbUrl.append("nav?path=~searchstart-index_").append(indexname);
					sbUrl.append("&amp;fromTo=").append(e.getChild("from").getAttributeValue("pos")).append("-")
							.append(e.getChild("to").getAttributeValue("pos"));
					sbUrl.append("&amp;prevFromTo=").append(upFromTo).append(".").append(prevFromTo);
					if (isSubselect) {
						sbUrl.append("&amp;").append(subselect_params);
					}
					if (search.length() > 0) {
						sbUrl.append("&amp;search=").append(search);
						sbUrl.append("&amp;mode=").append(mode);
					}
					out.write("<dd>\n");
					out.write("<a href=\"" + sbUrl.toString() + "\" class=\"nav\">");
					out.write("<img border=\"0\" style=\"vertical-align:middle;padding-right:10px\" src=\"" + webApplicationBaseURL
							+ "images/folder_plus.gif\" align=\"middle\"/></a>");
					out.write(e.getChild("from").getAttributeValue("short") + " - " + e.getChild("to").getAttributeValue("short") + "\n");
					out.write("</dd>\n");
				}

				if (e.getName().equals("value")) {
					String title = e.getChildText("idx");
					String titleEnc = URLEncoder.encode(title, "UTF-8");
					StringBuffer sbUrl = new StringBuffer(webApplicationBaseURL);
					if (isSubselect) {
						sbUrl.append("servlets/").append("XMLEditor?_action=end.subselect");
						sbUrl.append("&amp;subselect.session=").append(subselect_session);
						sbUrl.append("&amp;subselect.varpath=").append(subselect_varpath);
						sbUrl.append("&amp;subselect.webpage=").append(URLEncoder.encode(subselect_webpage, "UTF-8"));

						getJspContext().setAttribute(varURLName,
								sbUrl.toString() + "&amp;_var_@xlink:href=" + e.getChildText("id") + "&amp;_var_@xlink:title=" + titleEnc);
					} else {
						sbUrl.append(formDocdetailsURL.format(new Object[] { e.getChildText("id") }));
						if (sbUrl.toString().contains("?")) {
							sbUrl.append("&amp;");
						} else {
							sbUrl.append("?");
						}
						sbUrl.append("offset=").append(e.getAttributeValue("pos"));
						getJspContext().setAttribute(varURLName, sbUrl.toString());
					}
					getJspContext().setAttribute(varXMLName, domOut.output(new Document((Element) e.clone())));
					out.write("<dd>\n");
					getJspBody().invoke(out);
					out.write("</dd>\n");
				}
			}
			out.write("</dl>");
			out.write("\n</td></tr></table>");
			out.write("\n</div>");
		} catch (Exception e) {
			LOGGER.error("The following exception was thrown in MCRIndexBorserTag: ", e);
		}

	}
}
