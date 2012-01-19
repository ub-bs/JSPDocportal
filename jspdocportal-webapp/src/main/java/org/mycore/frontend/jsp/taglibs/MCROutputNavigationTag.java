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
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;
import org.mycore.access.MCRAccessManager;
import org.mycore.common.MCRSessionMgr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Tag that renders the navigation
 * 
 * @author Robert Stephan
 *
 */
public class MCROutputNavigationTag extends SimpleTagSupport
{
	private static final String NS_NAVIGATION = "http://www.mycore.org/jspdocportal/navigation";
	private static final List<String> MODES = Arrays.asList(new String[]{"left", "top", "breadcrumbs", "toc"});

	private String mode;
	private String currentPath;
	private boolean expanded = false;
	private String id;
	private String separatorString="";
	
	private ResourceBundle rbMessages;
	private String baseURL;
		
	private static Logger LOGGER = Logger.getLogger(MCROutputNavigationTag.class);

	public void doTag() throws JspException, IOException {
		if(!MODES.contains(mode)){
			return;
		}
		
		String lang = MCRSessionMgr.getCurrentSession().getCurrentLanguage();
		if(lang == null){lang = "de";}
		rbMessages = ResourceBundle.getBundle("messages", new Locale(lang));
		baseURL = (String)getJspContext().getAttribute("WebApplicationBaseURL", PageContext.APPLICATION_SCOPE);
		
		Element nav = retrieveNavigation();
		String[] path = currentPath.split("\\.");
		if(path.length>0){
			if(path[0].equals(id)){
				path = Arrays.copyOfRange(path, 1, path.length);
			}
			else{
				path=new String[]{};
			}
		}
		if(mode.equals("left")){
			printLeftNav(path, nav, getJspContext().getOut());
		}
		if(mode.equals("toc")){
			Element eNav = findNavItem(nav, path);
			printTOC(eNav, getJspContext().getOut());
		}
		if(mode.equals("top")){
			Element eNav = findNavItem(nav, path);
			printTopNav(eNav, getJspContext().getOut());
		}
		if(mode.equals("breadcrumbs")){
			Element eNav = findNavItem(nav, path);
			printBreadcrumbs(eNav, getJspContext().getOut());
		}
	}
	
	/**
	 * set the current navigation path
	 * @param currentPath - the path, separated by "."
	 */
	public void setCurrentPath(String currentPath) {
		this.currentPath = currentPath;
	}
	
	/**
	 * sets the current mode
	 * @param mode - the current mode. Allowed values are: left, top, breadcrumbs, toc
	 */
	public void setMode(String mode) {
		this.mode = mode;
	}	
	
	
	/**
	 * set the expanded option
	 * @param expaned - set true, if the whole navigation tree should be displayed expanded
	 */
	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}
	
	/**
	 * set the separator string
	 * only used in modes "top" and "breadcrumbs" 
	 * @param separatorString - the String which should be printed between items 
	 */
	public void setSeparatorString(String separatorString) {
		this.separatorString = separatorString;
	}
	
	/**
	 * sets the navigation id
	 * @param id - the navigation id
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * retrieves the proper navigation element from navigation DOM object in application scope 
	 */
	private Element retrieveNavigation(){
		Document navDom = (org.w3c.dom.Document)getJspContext().getAttribute("navDom", PageContext.APPLICATION_SCOPE);
		NodeList nl = navDom.getElementsByTagNameNS(NS_NAVIGATION, "navigations");
		if(nl.getLength()==0) return null;
		
		Element navigations = (Element)(nl.item(0));
		nl = navigations.getElementsByTagNameNS(NS_NAVIGATION, "navigation");
		for(int i=0;i<nl.getLength(); i++){
			Element e = (Element)nl.item(i);
			if(e.getAttribute("id").equals(id)){
				return e;
			}
		}
		return null;
	}
	
	private Element findNavItem(Element currentNode, String[] path){
		if(path.length==0){
			return currentNode;
		}
		
		NodeList nl = currentNode.getChildNodes();
		for(int i=0;i<nl.getLength(); i++){
			if(!(nl.item(i) instanceof Element)){
				continue;
			}
			Element el = (Element)nl.item(i);
			if(!el.getNodeName().equals("navitem")){
				continue;
			}
			if(path.length>0){
			String id = path[0];
			if(el.getAttribute("id").equals(id)){
				return findNavItem(el, Arrays.copyOfRange(path, 1, path.length));				
			}			
			}
		}
		//if the path is wrong - return the give node
		return currentNode;

	}
	
	
	/**
	 * prints the navigation items as left side main navigation
	 * can be called recursively
	 * @param path - the navigation path (separated by ".")
	 * @param currentNode - the current navigation item
	 * @param out - the JSPOutputWriter
	 */
	private void printLeftNav(String[] path, Element currentNode, JspWriter out){
		if(currentNode == null){
			if(path==null || path.length==0){
				LOGGER.error("No navigation item found for navigation: "+id+", path: "+currentPath);
			}
			else{
				LOGGER.error("No navigation item found for navigation: "+id+", path: "+currentPath+", item: "+path[0]);
			}
			return;
		}
		NodeList nl = currentNode.getChildNodes();
		
		for(int i=0;i<nl.getLength(); i++){
			if(!(nl.item(i) instanceof Element)){
				continue;
			}
			Element el = (Element)nl.item(i);
			if(!el.getNodeName().equals("navitem")){
				continue;
			}
			boolean hidden = "true".equals(el.getAttribute("hidden"));
			if(hidden){
				continue;
			}					
			String permission = el.getAttribute("permission");
			if(!permission.equals("")){
				if(!MCRAccessManager.checkPermission(permission)){
					continue;
				}
			}
			try{
				String cssClass = "navi_left_subentry";
				if("1".equals(el.getAttribute("_level"))){
					cssClass = "navi_left_mainentry";
				}
			
				String msg = retrieveI18N(el.getAttribute("i18n"));
				out.append("<div class=\""+cssClass+"\">");
				out.append("   <a target=\"_self\" href=\""+baseURL+"nav?path="+el.getAttribute("_path")+"\">"+msg+"</a>");
				
				if(expanded || (path.length>0 && path[0].equals(el.getAttribute("id")))){
					String[] subpath = path;
					if(path.length>0){
						subpath = Arrays.copyOfRange(path, 1, path.length);
					}
					printLeftNav(subpath, el, out);				
				}
				
				out.append("</div>");
				out.flush();
			}
			catch(Exception e){
				LOGGER.error(e);
			}
		}
	}
	
	/**
	 * prints top nav (horizontal navigation)
	 * (only with direct sub items of the given navigation item
	 * @param currentNode - the current navigation item
	 * @param out - the JSPOutputWriter
	 */
	private void printTopNav(Element currentNode, JspWriter out){
		if(currentNode==null){
			return;
		}
		NodeList nl = currentNode.getChildNodes();
		try{
			boolean beforeFirst = true;
			for(int i=0;i<nl.getLength(); i++){
				if(!(nl.item(i) instanceof Element)){
					continue;
				}
				Element el = (Element)nl.item(i);
				if(!el.getNodeName().equals("navitem")){
					continue;
				}
				boolean hidden = "true".equals(el.getAttribute("hidden"));
				if(hidden){
					continue;
				}					
				String permission = el.getAttribute("permission");
				if(!permission.equals("")){
					if(!MCRAccessManager.checkPermission(permission)){
						continue;
					}	
				}
				
				if(!beforeFirst){
					out.append("\n<div class=\"separator\">"+separatorString+"</div>");					
				}
				else {
					beforeFirst = false;
				}
				String msg = retrieveI18N(el.getAttribute("i18n"));
				out.append("\n<div class=\"item\">");							
			    out.append("\n   <a target=\"_self\" href=\""+baseURL+"nav?path="+el.getAttribute("_path")+"\">"+msg+"</a>");
			    out.append("\n</div>");				
			}
			out.flush();
		}
		catch(IOException e){
			LOGGER.error(e);
		}
	}
	/*
	<x:forEach
	select="$Navigation//nav:navigation[@name='top']/nav:navitem[not(@hidden = 'true')]">
	<x:set var="href1" select="string(./@href)" />
	<x:set var="labelKey1" select="string(./@i18n)" />
	<a target="_self" href="${href1}"><fmt:message key="${labelKey1}" /></a>
	<x:choose>
		<x:when select="../nav:navitem[last()]/@nodeID != ./@nodeID">
			<img alt="" style="width:6px; height:1px;" src="${WebApplicationBaseURL}images/emtyDot1Pix.gif">
			|
			<img alt="" style="width:6px; height:1px;" src="${WebApplicationBaseURL}images/emtyDot1Pix.gif">
		</x:when>
	</x:choose>
</x:forEach>
	*/
	
	/**
	 * prints the navigation items as left side main navigation
	 * can be called recursively
	 * @param path - the navigation path (separated by ".")
	 * @param currentNode - the current navigation item
	 * @param out - the JSPOutputWriter
	 */
	private void printTOC(Element currentNode, JspWriter out){
		if(currentNode == null){
			LOGGER.error("No navigation item found for navigation: "+id+", path: "+currentPath);
			return;
		}
		NodeList nl = currentNode.getChildNodes();
		try{
			out.append("\n<ul style=\"list-style-image: url("+baseURL+"images/greenArrow.gif);\">"); 
			for(int i=0;i<nl.getLength(); i++){
				if(!(nl.item(i) instanceof Element)){
					continue;
				}
				Element el = (Element)nl.item(i);
				if(!el.getNodeName().equals("navitem")){
					continue;
				}
				boolean hidden = "true".equals(el.getAttribute("hidden"));
				if(hidden){
					continue;
				}					
				String permission = el.getAttribute("permission");
				if(!permission.equals("")){
					if(!MCRAccessManager.checkPermission(permission)){
						continue;
					}	
				}
							
				String msg = retrieveI18N(el.getAttribute("i18n"));
				out.append("\n<li>");
				out.append("\n<a target=\"_self\" href=\""+baseURL+"nav?path="+el.getAttribute("_path")+"\">"+msg+"</a>");
				if(expanded){
						printTOC(el, out);				
				}
				out.append("\n</li>");
			}
			out.append("\n</ul>");
			out.flush();
		}
		catch(IOException e){
			LOGGER.error(e);
		}
	}
	
	/**
	 * prints a breadcrumb navigation for the given node
	 * by retrieving its parents.
	 * @param currentNode - the current navigation item
	 * @param out - the JSPOutputWriter
	 */
	
	private void printBreadcrumbs(Element currentNode, JspWriter out){
	    if(currentNode==null){
	    	return;
	    }
		StringBuffer sbOut = new StringBuffer();
		String msg = retrieveI18N(currentNode.getAttribute("i18n"));
		sbOut.append("\n<span style=\"padding-left:6px; padding-right:6px;\">");
		sbOut.append("\n   <a target=\"_self\" href=\""+baseURL+"nav?path="+currentNode.getAttribute("_path")+"\">"+msg+"</a>");
	    sbOut.append("\n</span>");
		while(currentNode.getParentNode().getLocalName().equals("navitem")){
	    	currentNode = (Element) currentNode.getParentNode();
	    	msg = retrieveI18N(currentNode.getAttribute("i18n"));
	    	sbOut.insert(0, separatorString);
	    	sbOut.insert(0, "\n</span>");		
	    	sbOut.insert(0, "\n   <a target=\"_self\" href=\""+baseURL+"nav?path="+currentNode.getAttribute("_path")+"\">"+msg+"</a>");
	    	sbOut.insert(0, "\n<span style=\"padding-left:6px; padding-right:6px;\">");
	    }
	    try{
	    	out.append(sbOut.toString());
	    }
	    catch(IOException e){
	    	LOGGER.error(e);
	    }
	}
	
	private String retrieveI18N(String key){
		if(key==null || key.equals("")){
			return "";
		}
		else{
			if(rbMessages.containsKey(key)){
				return rbMessages.getString(key);
			}
			else{
				return "???"+key+"???";
			}
		}
	}
	
}