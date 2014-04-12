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

package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.hibernate.Transaction;
import org.mycore.backend.hibernate.MCRHIBConnection;
import org.w3c.dom.Element;

/**
 * <p>
 * Tag that renders the navigation.
 * </p>
 * <p>
 * It receives the following attribute:
 * </p>
 * <ul>
 * <li>id: The id of the navigation, that should be printed</li>
 * <li>mode: The mode the is used for the output. Valid values are:
 * <ul>
 * <li>left</li>
 * <li>top</li>
 * <li>toc</li>
 * <li>breadcrumbs</li>
 * </ul>
 * </li>
 * <li>expanded: Should the whole navigation tree be printed or just</li>
 * </ul>
 * 
 * 
 * @author Robert Stephan
 * 
 * 
 */
public class MCROutputNavigationTag extends MCRAbstractNavigationTag {
	private static final List<String> MODES = Arrays.asList(new String[] {
			"left", "top", "breadcrumbs", "toc" });
	private static final String INDENT = "\n       ";

	private String mode;

	private static Logger LOGGER = Logger
			.getLogger(MCROutputNavigationTag.class);

	public void doTag() throws JspException, IOException {
		init();
		if (!MODES.contains(mode)) {
			LOGGER.warn("The attribute mode has to be one of these values: "
					+ MODES);
			return;
		}

		if (mode.equals("left")) {
			printLeftNav(path, nav, getJspContext().getOut());
		}
		if (mode.equals("toc")) {
			Element eNav = findNavItem(nav, path);
			printTOC(eNav, getJspContext().getOut());
		}
		if (mode.equals("top")) {
			printTopNav(nav, getJspContext().getOut());
		}
		if (mode.equals("breadcrumbs")) {
			Element eNav = findNavItem(nav, path);
			printBreadcrumbs(eNav, getJspContext().getOut());
		}
	}

	/**
	 * sets the current mode
	 * 
	 * @param mode
	 *            - the current mode. Allowed values are: left, top,
	 *            breadcrumbs, toc
	 */
	public void setMode(String mode) {
		this.mode = mode;
	}

	/**
	 * prints the navigation items as left side main navigation can be called
	 * recursively
	 * 
	 * @param path
	 *            - the navigation path (separated by ".")
	 * @param currentNode
	 *            - the current navigation item
	 * @param out
	 *            - the JSPOutputWriter
	 */
	private void printLeftNav(String[] path, Element currentNode, JspWriter out) {
		Transaction t1 = null;
		try {
			Transaction tx = MCRHIBConnection.instance().getSession()
					.getTransaction();
			if (tx == null || !tx.isActive()) {
				t1 = MCRHIBConnection.instance().getSession()
						.beginTransaction();
			}
			List<Element> printableElements = printableElements(currentNode);
			if (printableElements.isEmpty()) {
				return;
			}
			int level = NumberUtils
					.toInt(currentNode.getAttribute("_level"), 0);
			StringBuffer indentBuffer = new StringBuffer(INDENT);

			for (int j = 0; j < level; j++) {
				indentBuffer.append("    ");
			}
			String indent = indentBuffer.toString();
			out.append(indent).append("  <ul>");

			for (Element el : printableElements) {
				String cssClass = "";
				String id = el.getAttribute("id");
				boolean active = path.length > 0 && path[0].equals(id);
				if (active) {
					cssClass = "active";
				}

				String msg = retrieveI18N(el.getAttribute("i18n"));
				if (cssClass.length() > 0) {
					out.append(indent).append(
							" <li class=\"" + cssClass + "\">");
				} else {
					out.append(indent).append(" <li>");
				}
				out.append(indent);
				out.append(" <a target=\"_self\" href=\"" + baseURL
						+ "nav?path=" + el.getAttribute("_path") + "\">" + msg
						+ "</a>");

				if (expanded || active) {
					String[] subpath = path;
					if (path.length > 0) {
						subpath = Arrays.copyOfRange(path, 1, path.length);
					}
					printLeftNav(subpath, el, out);
				}
				out.append(indent).append(" </li>");
			}

			out.append(indent).append(" </ul>");
		} catch (Exception ex) {
			LOGGER.error(ex);
		} finally {
			if (t1 != null && t1.isActive()) {
				t1.commit();
			}
		}
	}

	/**
	 * prints top nav (horizontal navigation) (only with direct sub items of the
	 * given navigation item
	 * 
	 * @param currentNode
	 *            - the current navigation item
	 * @param out
	 *            - the JSPOutputWriter
	 */
	private void printTopNav(Element currentNode, JspWriter out) {
		if (currentNode == null) {
			return;
		}
		List<Element> printableElements = printableElements(currentNode);

		if (!printableElements.isEmpty()) {
			try {
				out.append("<ul>");
				for (Element el : printableElements) {

					String msg = retrieveI18N(el.getAttribute("i18n"));
					out.append(INDENT).append("    <li>");
					out.append(INDENT).append(
							"    <a target=\"_self\" href=\"" + baseURL
									+ "nav?path=" + el.getAttribute("_path")
									+ "\">" + msg + "</a>");
					out.append(INDENT).append("   </li>");
				}
				out.append(INDENT).append("</ul>");
				out.flush();
			}

			catch (IOException e) {
				LOGGER.error(e);
			}
		}
	}

	/**
	 * prints the navigation items as left side main navigation can be called
	 * recursively
	 * 
	 * @param path
	 *            - the navigation path (separated by ".")
	 * @param currentNode
	 *            - the current navigation item
	 * @param out
	 *            - the JSPOutputWriter
	 */
	private void printTOC(Element currentNode, JspWriter out) {
		if (currentNode == null) {
			LOGGER.error("No navigation item found for navigation: " + id
					+ ", path: " + currentPath);
			return;
		}
		List<Element> printableElements = printableElements(currentNode);
		if (!printableElements.isEmpty()) {
			try {
				out.append(INDENT).append("<ul>");
				for (Element el : printableElements) {
					String msg = retrieveI18N(el.getAttribute("i18n"));
					out.append(INDENT).append("<li>");
					out.append(INDENT).append(
							"<a target=\"_self\" href=\"" + baseURL
									+ "nav?path=" + el.getAttribute("_path")
									+ "\">" + msg + "</a>");
					if (expanded) {
						printTOC(el, out);
					}
					out.append(INDENT).append("</li>");
				}
				out.append(INDENT).append("</ul>");
				out.flush();
			} catch (IOException e) {
				LOGGER.error(e);
			}
		}
	}

	/**
	 * prints a breadcrumb navigation for the given node by retrieving its
	 * parents.
	 * 
	 * @param currentNode
	 *            - the current navigation item
	 * @param out
	 *            - the JSPOutputWriter
	 */

	private void printBreadcrumbs(Element currentNode, JspWriter out) {
	    if(currentNode==null || !currentNode.getLocalName().equals("navitem")){
	        return;
	    }
	    
		StringBuffer sbOut = new StringBuffer();
		String msg = retrieveI18N(currentNode.getAttribute("i18n"));
		sbOut.append(INDENT).append("   <li>");
		sbOut.append(INDENT).append(
				"      <a target=\"_self\" href=\"" + baseURL + "nav?path="
						+ currentNode.getAttribute("_path") + "\">" + msg
						+ "</a>");
		sbOut.append(INDENT).append("   </li>");
		sbOut.append(INDENT).append("</ul>");
		while (currentNode.getParentNode().getLocalName().equals("navitem")) {
			currentNode = (Element) currentNode.getParentNode();
			msg = retrieveI18N(currentNode.getAttribute("i18n"));
			sbOut.insert(0, INDENT + "   </li>");
			sbOut.insert(0, INDENT + "      <a target=\"_self\" href=\""
					+ baseURL + "nav?path=" + currentNode.getAttribute("_path")
					+ "\">" + msg + "</a>");
			sbOut.insert(0, INDENT + "   <li>");
		}
		sbOut.insert(0, INDENT + "<ul>");
		try {
			out.append(sbOut.toString());
		} catch (IOException e) {
			LOGGER.error(e);
		}
	}

}