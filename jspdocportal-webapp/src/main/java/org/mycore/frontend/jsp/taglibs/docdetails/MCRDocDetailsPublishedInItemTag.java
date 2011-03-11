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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;
import org.apache.taglibs.standard.tag.common.xml.XPathUtil;
import org.jdom.Element;
import org.jdom.input.DOMBuilder;

import org.w3c.dom.Node;

/**
 * displays a docdetails link item or simple text
 * 
 * @author Robert Stephan
 * 
 */
public class MCRDocDetailsPublishedInItemTag extends SimpleTagSupport {
	private static Logger LOGGER = Logger.getLogger(MCRDocDetailsPublishedInItemTag.class);
	private String xp;
	private String css = null;

	public void setSelect(String xpath) {
		this.xp = xpath;
	}

	public void setStyleName(String style) {
		this.css = style;
	}

	public void doTag() throws JspException, IOException {
		MCRDocDetailsRowTag docdetailsRow = (MCRDocDetailsRowTag) findAncestorWithClass(this, MCRDocDetailsRowTag.class);
		if (docdetailsRow == null) {
			throw new JspException("This tag must be nested in tag called 'row' of the same tag library");
		}
		MCRDocDetailsTag docdetails = (MCRDocDetailsTag) findAncestorWithClass(this, MCRDocDetailsTag.class);
		try {
			XPathUtil xu = new XPathUtil((PageContext) getJspContext());

			@SuppressWarnings("rawtypes")
			List nodes = xu.selectNodes(docdetailsRow.getContext(), xp);
			if (nodes.size() > 0) {
				Node n = (Node) nodes.get(0);
				DOMBuilder domBuilder = new DOMBuilder();
				if (n instanceof org.w3c.dom.Element) {
					Element el = domBuilder.build((org.w3c.dom.Element) n);
					String result = formatOutput(el);
	
					if (result.length() > 0) {
						if (css != null && !"".equals(css)) {
							getJspContext().getOut().print("<td class=\"" + css + "\">");
						} else {
							getJspContext().getOut().print("<td class=\"" + docdetails.getStylePrimaryName() + "-value\">");
						}
						getJspContext().getOut().print(result.toString());
						getJspContext().getOut().print("</td>");
					}
				}
			}
		} catch (Exception e) {
			LOGGER.debug("wrong xpath expression: " + xp);
		}
	}
		
	
	/**
	 * Formatiert den Inhalt des Feldes
	 * in Anlehnung an Katalogisierungsrichtlinie des GBV, Kat. 4070
	 * Eingabeformat:
	 * 	<title>Titel</title> <v>Band</v><j>Jahr</j><a>Heft</a>
	 * <d>Tag</d><m>Monat</m><n>Sonderheft</n>
	 * <p>Seitenangabe</p><t>Gesamtzahl Seiten</t>
	 * <display>modifizierte
	 * Anzeigeform</display></publishdetail>
	
	 * @param el
	 * @return
	 */
	private String formatOutput(Element el){
		StringBuffer result = new StringBuffer();
		String s = null;
		s = el.getChildTextNormalize("title");
		if (s != null) {
			result.append(s);
		}

		s = el.getChildTextNormalize("display");
		if (s == null) {
			s = el.getChildTextNormalize("v");
			if (s != null) {
				result.append(", Bd. ").append(s);
			}
			s = el.getChildTextNormalize("j");
			if (s != null) {
				result.append(" (").append(s).append(")");
			}
			s = el.getChildTextNormalize("a");
			if (s != null) {
				result.append(", Nr. ").append(s);
			}
			String tag = el.getChildTextNormalize("d");
			String monat = el.getChildTextNormalize("m");
			int iMonat = 0;
			try {
				iMonat = Integer.parseInt(monat);
			} catch (NumberFormatException nfe) {
				// do nothing
			}
			String[] monate = new String[] { "", "Jan.", "Febr.", "März", "Apr.", "Mai", "Jun.", "Jul.", "Aug.", "Sept.", "Okt.", "Nov.", "Dez." };
			if (tag != null && monat != null) {
				result.append(", ");
				for (char c : tag.toCharArray()) {
					if (Character.isDigit(c)) {
						result.append(c);
					} else {
						result.append(".").append("c");
					}
				}
				result.append(". ");
				result.append(monate[iMonat]);
			} else if (monat != null) {
				result.append(", ");
				if (monat.contains("-")) {
					String[] x = monat.split("\\-");
					for (String xx : x) {
						int i = 0;
						try {
							i = Integer.parseInt(xx);
							result.append(monate[i]);
							if (xx != x[x.length - 1]) {
								result.append("-");
							}
						} catch (NumberFormatException e) {

						}
					}
				} else if (monat.contains("/")) {
					String[] x = monat.split("\\/");
					for (String xx : x) {
						int i = 0;
						try {
							i = Integer.parseInt(xx);
							result.append(monate[i]);
							if (xx != x[x.length - 1]) {
								result.append("/");
							}
						} catch (NumberFormatException e) {

						}
					}
				} else {
					int i = 0;
					try {
						i = Integer.parseInt(monat);
						result.append(monate[i]);

					} catch (NumberFormatException e) {

					}
				}

			}
			s = el.getChildTextNormalize("p");
			if (s != null) {
				result.append(", S. ").append(s);
			}
			s = el.getChildTextNormalize("t");
			if (s != null) {
				result.append(" insges. ").append(s).append(" S.");
			}

		}
		else{
			result.append(", ").append(s);
		}
		return result.toString();
	}
}