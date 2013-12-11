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

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.mycore.common.MCRSession;
import org.mycore.frontend.servlets.MCRServlet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Even though this class extends the SimpleTagSupport class,
 * it is not meant to actually use it as a custom tag.
 * 
 * It does not even provide a doTag() method.
 * 
 * It is meant to provide useful methods and variables for the {@link MCROutputLeftNavigationTag} class.
 * 
 * @author Robert Stephan, Christian Windolf
 * @version $Revision: 1.8 $ $Date: 2008/05/28 13:43:31 $
 *
 */
public abstract class MCRAbstractNavigationTag extends SimpleTagSupport{
	protected static final String NS_NAVIGATION = "http://www.mycore.org/jspdocportal/navigation";
	
	protected String currentPath;
	protected ResourceBundle rbMessages;
	protected String baseURL;
	
	
	protected Element nav;
	protected String[] path;
	
	protected String id;
	protected boolean expanded = false;
	protected String var;
	
	/**
	 * retrieves all information needed to create a {@link NavigationVariables} object.
	 */
	protected void init(){
		MCRSession mcrSession = MCRServlet.getSession((HttpServletRequest)((PageContext) getJspContext()).getRequest());
		currentPath = (String) mcrSession.get("navPath");
		if (currentPath == null) {
			currentPath = "";
		}
		
		String lang = mcrSession.getCurrentLanguage();
		rbMessages = ResourceBundle.getBundle("messages", new Locale(lang));
		
		nav = retrieveNavigation();
		baseURL = (String) getJspContext().getAttribute("WebApplicationBaseURL", PageContext.APPLICATION_SCOPE);
		
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
	 * The name of the variable to store the result
	 * @param var - the variable name
	 */
	public void setVar(String var){
		this.var = var;
	}
	
	/**
	 * If the NavigationVariables should have children, set it to true. If not, set it to false
	 * The default value is false, if this method is not invoked i.e. in the attribute "expanded" was
	 * not set in the custom tag, that inherits from this class. 
	 * @param expanded
	 */
	public void setExpanded(boolean expanded){
		this.expanded = true;
	}
	
	/**
	 * Holds all navigation variables for the JSP to build the navigation
	 * @author Christian Windolf
	 *
	 */
	public static class NavigationVariables{
		private String label;
		private String href;
		private List<NavigationVariables> children;
		private String id;
		
		/**
		 * The localized link label for this item.
		 * @return
		 */
		public String getLabel() {
			return label;
		}
		public void setLabel(String label) {
			this.label = label;
		}
		
		/**
		 * The URL that links to the target
		 * @return
		 */
		public String getHref() {
			return href;
		}
		public void setHref(String href) {
			this.href = href;
		}
		
		/**
		 * If this navigation item has children they are stored here
		 * @return may be null, if it has no children.
		 */
		public List<NavigationVariables> getChildren() {
			return children;
		}
		public void setChildren(List<NavigationVariables> children) {
			this.children = children;
		}
		/**
		 * 
		 * @return true, if {@link MCRNavigation.NavigationVariables#getChildren()} ist not null or empty
		 */
		public boolean isActive() {
			if(children == null){
				return false;
			}
			return !children.isEmpty();
		}
		
		public void setId(String id){
			this.id = id;
		}
		
		/**
		 * The id of this navigation element
		 * @return
		 */
		public String getId(){
			return this.id;
		}
		
	}
}
