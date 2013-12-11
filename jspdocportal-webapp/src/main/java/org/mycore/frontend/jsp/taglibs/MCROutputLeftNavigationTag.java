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
import java.util.LinkedList;
import java.util.List;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;

import org.apache.log4j.Logger;
import org.hibernate.Transaction;
import org.mycore.access.MCRAccessManager;
import org.mycore.backend.hibernate.MCRHIBConnection;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;



import javax.servlet.jsp.PageContext;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Appends a JavaBean to the PAGE_SCOPE, that can be traversed with
 * JSTL tags.
 * 
 * The name of the variable is "navigation"
 * 
 * Example usage:
 * <mcr:outputLeftNavigation expanded="true" id="left" var="navigation" />
 * 
 * <ul>
 * 	<c:forEach var="n1" items="${navigation}">
 *  <li>
 *	<a href="${n1.href }" target="_self">${n1.label }</a>
 *	<c:if test="${n1.active }">
 *		<ul>
 *		<li>
 *		<c:forEach var="n2" items="${n1.children }">
 *			<a href="${n2.href }" target="_self" >${n2.label }</a>
 *		</c:forEach>
 *		</ul>
 *		</c:if>
 *		</li>
 *	</c:forEach>
 * </ul>
 * @author Robert Stephan, Christian Windolf
 * @version $Revision: 1.8 $ $Date: 2008/05/28 13:43:31 $
 *
 */
public class MCROutputLeftNavigationTag extends MCRAbstractNavigationTag {
	protected static Logger LOGGER = Logger
			.getLogger(MCROutputLeftNavigationTag.class);

	public void doTag() throws JspException, IOException {
		JspContext context = getJspContext();
		init();
		Transaction t1 = null;
		try {
			Transaction tx = MCRHIBConnection.instance().getSession().getTransaction();
			if (tx == null || !tx.isActive()) {
				t1 = MCRHIBConnection.instance().getSession().beginTransaction();
			}

			if (nav.getChildNodes().getLength() == 0) {
				return;
			}
			context.setAttribute(var, getNavigation(nav), PageContext.PAGE_SCOPE);

		} catch (Exception e) {
			LOGGER.error(e);
		} finally {
			if (t1 != null && t1.isActive()) {
				t1.commit();
			}
		}
	}

	private List<MCRAbstractNavigationTag.NavigationVariables> getNavigation(
			Element e, int index) {
		List<MCRAbstractNavigationTag.NavigationVariables> navigation = new LinkedList<>();
		NodeList nl = e.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			if (!(nl.item(i) instanceof Element)) {
				continue;
			}

			Element el = (Element) nl.item(i);
			if (!el.getNodeName().equals("navitem")) {
				continue;
			}

			if (BooleanUtils.toBoolean(el.getAttribute("hidden"))) {
				continue;
			}
			String permission = el.getAttribute("permission");
			if (!StringUtils.isEmpty(permission)) {
				if (!MCRAccessManager.checkPermission(permission)) {
					continue;
				}
			}

			MCRAbstractNavigationTag.NavigationVariables n = new MCRAbstractNavigationTag.NavigationVariables();
			String id = el.getAttribute("id");
			n.setId(id);
			n.setLabel(retrieveI18N(el.getAttribute("i18n")));
			n.setHref(baseURL + "nav?path=" + el.getAttribute("_path"));

			if (index < path.length) {
				if (expanded && path[index].equals(id)) {
					n.setChildren(getNavigation(el, index + 1));
				}
			}
			navigation.add(n);
		}
		return navigation;
	}

	private List<MCRAbstractNavigationTag.NavigationVariables> getNavigation(Element e) {
		return getNavigation(e, 0);
	}

}
