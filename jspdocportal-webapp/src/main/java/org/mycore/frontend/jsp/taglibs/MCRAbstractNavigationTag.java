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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;
import org.codehaus.plexus.util.StringUtils;
import org.mycore.access.MCRAccessManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Even though this class extends the SimpleTagSupport class,
 * it is not meant to actually use it as a custom tag.
 * 
 * It does not even provide a doTag() method.
 * 
 * It is meant to provide useful methods and variables for the {@link MCRCustomNavigationTag} and {@link MCROutputNavigationTag} class.
 * 
 * @author Robert Stephan, Christian Windolf
 * @version $Revision: 1.8 $ $Date: 2008/05/28 13:43:31 $
 *
 */
public abstract class MCRAbstractNavigationTag extends MCRAbstractTag{
	private static final Logger LOGGER = Logger.getLogger(MCRAbstractNavigationTag.class);
	protected static final String NS_NAVIGATION = "http://www.mycore.org/jspdocportal/navigation";
	
	protected String currentPath;
	
	/**
	 * the current navigation node
	 */
	protected Element nav;
	
	/**
	 * The path. It's elements are separated by dots.
	 * In this array, they are already separated and the dots are omitted
	 */
	protected String[] path;
	
	/**
	 * The id of the navigation that should be retrieved.
	 * It is set by subclasses tag attribute
	 */
	protected String id;
	
	/**
	 * if the whole navigation should be shown, this should be true.
	 * It is set by subclasses tag attribute
	 */
	protected boolean expanded = false;
	
	
	/**
	 * retrieves all information needed to create a navigation bar, line or whatever
	 */
	protected void init(){
		super.init();
		
		currentPath = (String) mcrSession.get("navPath");
		if (currentPath == null) {
			currentPath = "";
		}
		
		nav = retrieveNavigation();
		
		if(nav == null){
			if (path == null || path.length == 0) {
				LOGGER.error("No navigation item found for navigation: " + id + ", path: " + currentPath);
			} else {
				LOGGER.error("No navigation item found for navigation: " + id + ", path: " + currentPath + ", item: "
				        + path[0]);
			}
			return;
		}
		
		path = currentPath.split("\\.");
		if (path.length > 0) {
			if (path[0].equals(id)) {
				path = Arrays.copyOfRange(path, 1, path.length);
			} else {
				path = new String[] {};
			}
		}
		
	}
	
	/**
	 * retrieves the proper navigation element from navigation DOM object in application scope 
	 */
	protected Element retrieveNavigation() {
		Document navDom = (org.w3c.dom.Document) getJspContext().getAttribute("navDom", PageContext.APPLICATION_SCOPE);
		NodeList nl = navDom.getElementsByTagNameNS(NS_NAVIGATION, "navigations");
		if (nl.getLength() == 0)
			return null;

		Element navigations = (Element) (nl.item(0));
		nl = navigations.getElementsByTagNameNS(NS_NAVIGATION, "navigation");
		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);
			if (e.getAttribute("id").equals(id)) {
				return e;
			}
		}
		return null;
	}
	
	
	/**
	 * Looks up for a matching key in the message_**.properties.
	 * @param key A valid key
	 * @return if the key is not found, it returns "???<key>???"
	 */
	protected String retrieveI18N(String key) {
		if (key == null || key.equals("")) {
			return "";
		} else {
			if (rbMessages.containsKey(key)) {
				return rbMessages.getString(key);
			} else {
				return "???" + key + "???";
			}
		}
	}
	
	/**
	 * The id is needed to know, which navigation from the navigation.xml should be processed
	 * @param id
	 */
	public void setId(String id){
		this.id = id;
	}

	
	/**
	 * If the NavigationVariables should have children, set it to true. If not, set it to false
	 * The default value is false, if this method is not invoked i.e. in the attribute "expanded" was
	 * not set in the custom tag, that inherits from this class. 
	 * @param expanded
	 */
	public void setExpanded(boolean expanded){
		this.expanded = expanded;
	}
	
	/**
	 * retrieves the element inside the currentNode on which the path is pointing to.
	 * @param currentNode
	 * @param path
	 * @return
	 */
	protected Element findNavItem(Element currentNode, String[] path) {
		if (path.length == 0) {
			return currentNode;
		}

		NodeList nl = currentNode.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			if (!(nl.item(i) instanceof Element)) {
				continue;
			}
			Element el = (Element) nl.item(i);
			if (!el.getNodeName().equals("navitem")) {
				continue;
			}
			if (path.length > 0) {
				String id = path[0];
				if (el.getAttribute("id").equals(id)) {
					return findNavItem(el, Arrays.copyOfRange(path, 1, path.length));
				}
			}
		}
		//if the path is wrong - return the give node
		return currentNode;

	}
	

	
	/**
	 * retrieves all child elements, that are printable in this context.
	 * Printable means, that first, the user has permission to see this link
	 * and second, it is not marked as hidden. 
	 * Child elements, that are not "navitem"-elements are filtered out as well.
	 * 
	 * This method just retrieves elements one level below. It does not traverse them recursively!
	 * @param e
	 * @return An array list with all elements that should be visible for this user in the current session.
	 */
	protected List<Element> printableElements(Element e){
		List<Element> peList = new ArrayList<>();
		NodeList nl = e.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			if (!(nl.item(i) instanceof Element)) {
				continue;
			}
			Element el = (Element) nl.item(i);
			if (!el.getNodeName().equals("navitem")) {
				continue;
			}
			boolean hidden = "true".equals(el.getAttribute("hidden"));
			if (hidden) {
				continue;
			}
			String permission = el.getAttribute("permission");
			if (StringUtils.isNotEmpty(permission)) {
				if (!MCRAccessManager.checkPermission(permission)) {
					continue;
				}
			}
			peList.add(el);
		}
		return peList;
	}
}