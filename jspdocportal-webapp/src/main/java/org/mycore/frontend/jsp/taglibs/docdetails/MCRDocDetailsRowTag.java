/*
 * $RCSfile$
 * $Revision: 16360 $ $Date: 2010-01-06 00:54:02 +0100 (Mi, 06 Jan 2010) $
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

package org.mycore.frontend.jsp.taglibs.docdetails;

import java.io.IOException;
import java.util.List;
import java.util.MissingResourceException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.taglibs.standard.tag.common.xml.XPathUtil;
import org.w3c.dom.Node;

/**
 * Creates a row in the docdetails table
 * 
 * @author Robert Stephan
 * 
 */
public class MCRDocDetailsRowTag extends SimpleTagSupport {
	private static final long serialVersionUID = -2264968071280327072L;
	private boolean showinfo = false;
	private String xpath;
	private String labelkey;
	private String colWidths;

	private Node xml;

	/**
	 * the XPath epxression that leads to this row
	 * 
	 * @param s
	 *            an XPath expression
	 */
	public void setSelect(String s) {
		xpath = s;
	}

	/**
	 * the key, which will be looked up in the resourcebundle to retrieve the
	 * label, that will be displayed on the left side
	 * 
	 * @param s
	 */
	public void setLabelkey(String s) {
		labelkey = s;
	}

	/**
	 * predefined column widths for item tables see colWidths attribute in HTML
	 * Element &lt;tr&gt; for details
	 * 
	 * @param s
	 */
	public void setColWidths(String s) {
		colWidths = s;
	}


	/**
	 * if set to true an infobox will be displayed in front of the label which
	 * can contain detailed information for the field
	 * 
	 * @param b
	 */
	public void setShowInfo(boolean b) {
		showinfo = b;
	}

	protected Node getContext() {
		return xml;
	}

	private MCRDocDetailsTag docdetails;

	public void doTag() throws JspException, IOException {
		docdetails = (MCRDocDetailsTag) findAncestorWithClass(this, MCRDocDetailsTag.class);
		if (docdetails == null) {
			throw new JspException("This tag must be nested in tag called 'docdetails' of the same tag library");
		}
		String xp = xpath;

		PageContext ctx = (PageContext) getJspContext();

		JspWriter out = getJspContext().getOut();
		XPathUtil xu = new XPathUtil((PageContext) getJspContext());
		@SuppressWarnings("unchecked")
		List nodes = xu.selectNodes(docdetails.getContext(), xp);
		if (nodes.size() > 0) {
			docdetails.setPreviousOutput(docdetails.getPreviousOutput() + 1);
			out.write("<tr>");
			out.write("   <td class=\"" + docdetails.getStylePrimaryName() + "-infolabel\">");
			boolean print = "true".equals(ctx.getRequest().getParameter("print"));
			if (showinfo && !print) {
				String info = "";
				try {
					info = docdetails.getMessages().getString(labelkey + ".info");
				} catch (MissingResourceException e) {
					info = "???" + labelkey + ".info???<br /><i>Eine Beschreibung f�r das Feld wird gerade erstellt.</i>";
				}
				out.write("<div class=\"" + docdetails.getStylePrimaryName() + "-infohover\">" + "<a href=\"#\">i<span>" + info + "</span></a></div>");
			}
			out.write("</td>\n");

			String label = "";
			if (labelkey.length() > 0) {
				try {
					label = docdetails.getMessages().getString(labelkey);
				} catch (MissingResourceException e) {
					label = "???" + labelkey + "???";
				}
			}
			out.write("   <td class=\"" + docdetails.getStylePrimaryName() + "-label\">" + label + ":</td>\n");
			out.write("   <td class=\"" + docdetails.getStylePrimaryName() + "-values\">\n");
			out.write("   		<table class=\"" + docdetails.getStylePrimaryName() + "-values-table\">\n");
			if (colWidths != null && !colWidths.equals("")) {
				String[] ss = colWidths.split("\\s");
				out.write("   	   	<colgroup>");
				for (String s : ss) {
					out.write(" <col width=\"" + s + "\">");
				}
				out.write("   	   	</colgroup>\n");
			}
			out.write("   	   	<tbody>\n");
			for (int i = 0; i < nodes.size(); i++) {
				xml = (Node) nodes.get(i);
				out.write("<tr>");
				getJspBody().invoke(out);
				out.write("</tr>");
			}

			out.write("   	   	</tbody></table>\n");
			out.write("</td></tr>");
		}
	}
}