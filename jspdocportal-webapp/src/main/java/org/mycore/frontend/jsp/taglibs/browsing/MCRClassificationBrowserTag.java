/**
 * 
 * $Revision: 13468 $ $Date: 2008-04-28 17:49:59 +0200 (Mo, 28 Apr 2008) $
 *
 * This file is part of ** M y C o R e **
 * Visit our homepage at http://www.mycore.de/ for details.
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
 * along with this program, normally in the file license.txt.
 * If not, write to the Free Software Foundation Inc.,
 * 59 Temple Place - Suite 330, Boston, MA  02111-1307 USA
 *
 **/
package org.mycore.frontend.jsp.taglibs.browsing;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.constructs.blocking.CacheEntryFactory;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

import org.apache.log4j.Logger;
import org.hibernate.Transaction;
import org.mycore.backend.hibernate.MCRHIBConnection;
import org.mycore.common.MCRConfiguration;
import org.mycore.datamodel.classifications2.MCRCategLinkService;
import org.mycore.datamodel.classifications2.MCRCategLinkServiceFactory;
import org.mycore.datamodel.classifications2.MCRCategory;
import org.mycore.datamodel.classifications2.MCRCategoryDAO;
import org.mycore.datamodel.classifications2.MCRCategoryDAOFactory;
import org.mycore.datamodel.classifications2.MCRCategoryID;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.parsers.bool.MCRCondition;
import org.mycore.services.fieldquery.MCRQuery;
import org.mycore.services.fieldquery.MCRQueryManager;
import org.mycore.services.fieldquery.MCRQueryParser;
import org.mycore.services.fieldquery.MCRResults;
import org.mycore.services.i18n.MCRTranslation;

/**
 * A JSP tag, that includes a classification browser. The displayed content is highly configurable.
 * Look into the TLD file and the bean-style setters for details. 
 * 
 * Future implementations could include the following additional features:
 *  - sortby : sort by ID or label
 *  - objecttype: consider only the specified object type 
 *                (can currently be done by specifing a searchrestriction)
 *  - cssStylePrimaryName: add CSS class attributes for layout (look into Google Web Toolkit's conventions)
 * 
 * @author Robert Stephan
 * @version $Revision: 11610 $ $Date: 2007-05-31 16:39:41 +0200 (Do, 31 Mai 2007) $
 * @since 2.0
 */
public class MCRClassificationBrowserTag extends SimpleTagSupport {
	private static final Logger LOGGER = Logger.getLogger(MCRClassificationBrowserTag.class);
	private String classification;
	private String category="";
	
	private MCRCategoryID rootClassifID = null;
	private boolean count = false;
	private boolean hideemptyleaves = true;
	private Vector<String> path = new Vector<String>();
	private int level=-1;
	private boolean expand=false;
	private String searchfield;
	private String searchmask;
	
	private boolean showdescription=false;
	private boolean showuri=false;
	private boolean showid=false;
	private boolean linkall=false;
	private String searchrestriction=null;

	
	private static MCRCategoryDAO CATEGDAO = MCRCategoryDAOFactory.getInstance();
	private static MCRCategLinkService CATEGLINKSERVICE = MCRCategLinkServiceFactory.getInstance();
	
   
	private Map<MCRCategoryID, Number> countLinkMap = new HashMap<MCRCategoryID, Number>();
	private Map<MCRCategoryID, Boolean> hasLinkMap = new HashMap<MCRCategoryID, Boolean>();
	
	public static final String CACHE_NAME = "ClassBrowserTagHitCountCache";
	
	/**
	 *  a special cache, that can create objects, that are not cached, by itself 
	 *  - key is a MCR Query as String
	 *  - value the number of hits of the query
	 *  I used a LRU Cache with a timeToLive of 5 min. That means all objects are obsolete after this time.
	 *  This is a workaround, because the cache won't know if the data in the background changes.
	 */		
	private static SelfPopulatingCache cbHitCountCache;
	
	static{
		/* Create an ehcache object, 
		 * if we start using ehcache more often a config file would be a better choice.
		 * @see http://ehcache.sourceforge.net/EhcacheUserGuide.html#id.s9
		 * Constructor: public Cache(java.lang.String name, int maxElementsInMemory,
	             			MemoryStoreEvictionPolicy memoryStoreEvictionPolicy,
	             			boolean overflowToDisk, java.lang.String diskStorePath,
	             			boolean eternal, long timeToLiveSeconds, long timeToIdleSeconds,
	             			boolean diskPersistent, long diskExpiryThreadIntervalSeconds,
	             			RegisteredEventListeners registeredEventListeners)
		*/
		Cache cache = new Cache(CACHE_NAME, 1000, MemoryStoreEvictionPolicy.LRU, false, "", false, 0, 300, false, 120, null);
		CacheManager.getInstance().addCache(cache);
		cbHitCountCache = new SelfPopulatingCache(cache, new CacheEntryFactory(){
			
			/* (non-Javadoc)
			 * @see net.sf.ehcache.constructs.blocking.CacheEntryFactory#createEntry(java.lang.Object)
			 * retrieves the numberOfHits for the given query string(key)
			 * it will be stored in cache automatically
			 */
			public Object createEntry(Object key) throws Exception {
				MCRCondition cond = (new MCRQueryParser()).parse(key.toString());
				MCRQuery query = new MCRQuery(cond);
				MCRResults result = MCRQueryManager.search(query);
				return result.getNumHits();
			}
		});
	}
	
	/**
	 * required: set the ID of the classification to be shown
	 * @param classifID - the classification ID as string
	 */
	public void setClassification(String classifID){
		classification = classifID;
	}
	
	/**
	 * optional: a category ID, which should be uses as parent
	 * Only child objects of this category will be displayed
	 * @param categID - the category ID as string
	 *                - default: empty string
	 */
	public void setCategory(String categID){
		category = categID;
	}
	
	/**
	 * optional: Decide, if the number of items within the classification shall be shown.
	 * Counting is a time consuming process 
	 * @param b - a boolean value
	 *          - default: false
	 */
	public void setCount(boolean b){
		count = b;
	}
	
	/**
	 * optional: Decide, if empty leaves shall be hidden
	 * Only categories, that contain items will be displayed
	 * @param b - a boolean value
	 *          - default: true
	 */
	public void setHideemptyleaves(boolean b){
		hideemptyleaves = b;
	}
	
    /** 
     * required: the searchfield, that should be used to query the items, that
     * belong to a category
     * @param searchfield - as string
     */
    public void setSearchfield(String searchfield) {
		this.searchfield = searchfield;
	}
	
    /**
     * required: the searchmask (result page) where the query for items
     * shall be displayed
	 * @param searchmask - as string
	 */
	public void setSearchmask(String searchmask) {
		this.searchmask = searchmask;
	}
	
	/**
	 * optional: Decide, how deep the classification shall be displayed
	 * @param level - an integer value
	 *              - default is "-1" for infinite
	 *               
	 */
	public void setLevel(int level) {
		this.level = level;
	}
	
	/**
	 * optional: Decide, if the description of a category shall be displayed
	 * @param showdescription - a boolean value
	 *                        - default is "false"
	 */
	public void setShowdescription(boolean showdescription) {
		this.showdescription = showdescription;
	}
	/**
	 * optional: Decide, if the URI of a category shall be displayed
	 * @param showuri - a boolean value
	 *                - default is "false"
	 */
	public void setShowuri(boolean showuri) {
		this.showuri = showuri;
	}
	
	/**
	 * optional: Decide, if the ID of a category shall be displayed
	 * @param showid - a boolean value
	 *               - default is "false"
	 */
	public void setShowid(boolean showid) {
		this.showid = showid;
	}
	
	/**
	 * optional: Decide, if the whole classification tree shall be display at once
	 * All items will be visible at the first call
	 * @param expand - a boolean value
	 *               - default is "false"
	 */
	public void setExpand(boolean expand) {
		this.expand = expand;
	}
	
	/**
	 * optional: Specify a restriction for the search for items in a category
	 * This allows you to hide certain items.
	 * @param searchrestriction a string, which must be a valid query in MCR textual syntax
	 */
	public void setSearchrestriction(String searchrestriction) {
		this.searchrestriction = searchrestriction;
	}
	/**
	 * optional: Decide, if for all items (including empty ones) a link shall be created
	 * Could be used in subselects.
	 * All items will be visible at the first call
	 * @param linkall - a boolean value
	 *               - default is "false"
	 */
	public void setLinkall(boolean linkall) {
		this.linkall = linkall;
	}
	
	/**
	 * @see javax.servlet.jsp.tagext.SimpleTagSupport#doTag()
	 */
	public void doTag() throws JspException, IOException {
		long start = System.currentTimeMillis();
		if(level==-1){level = 1000000;}
		rootClassifID = new MCRCategoryID(classification, category);
	
		PageContext context = (PageContext) getJspContext();
		HttpServletRequest request = (HttpServletRequest) context.getRequest();
		String requestPath = request.getParameter("cbpath");
		StringBuffer url = request.getRequestURL();
		url.append("?");
 
		@SuppressWarnings("rawtypes")
		Enumeration paramNames = request.getParameterNames();
		while(paramNames.hasMoreElements()){
			String s = paramNames.nextElement().toString();
			if(!s.equals("cbpath")){
				url.append(s).append("=").append(URLEncoder.encode(request.getParameter(s), Charset.defaultCharset().name())).append("&");
			}
		}
		
		if(requestPath==null){	requestPath="";}
		url.append("cbpath=").append(clearPath(requestPath));
			
		JspWriter out = getJspContext().getOut();
		Transaction tx = MCRHIBConnection.instance().getSession().beginTransaction();
	
		out.write("\n\n<!-- ClassificationBrowser START -->");
		out.write(rootClassifID.getID()+"\n\n");
		if(!CATEGDAO.exist(rootClassifID)){
			LOGGER.error("Classification does not exist"+rootClassifID.getID());
			out.write(rootClassifID.getID()+"does not exist!");
			return;
		}
		MCRCategory rootCateg = CATEGDAO.getCategory(rootClassifID, 0);
		
		
		List<MCRCategory> categories = CATEGDAO.getChildren(rootClassifID);
		
		if(count){
			if(searchrestriction==null){
				countLinkMap.putAll(CATEGLINKSERVICE.countLinks(rootCateg, true));
			}
		
		}
		else{
			hasLinkMap.putAll(CATEGLINKSERVICE.hasLinks(rootCateg));
		}
		String webApplicationBaseURL = MCRServlet.getBaseURL();
		String subselect_webpage = context.getRequest().getParameter("XSL.subselect.webpage.SESSION");
	    if(subselect_webpage==null){subselect_webpage="";}
	    
	    String subselect_session = context.getRequest().getParameter("XSL.subselect.session.SESSION");
	    if(subselect_session==null){subselect_session="";}
	    String subselect_varpath = context.getRequest().getParameter("XSL.subselect.varpath.SESSION");
	    if(subselect_varpath==null){subselect_varpath="";}
	    boolean isSubselect =!subselect_varpath.equals(""); 
	       		
	    //cancel subselect
		if(isSubselect){
			out.write("<form action=\""+webApplicationBaseURL+subselect_webpage+"XSL.editor.session.id="+subselect_session+"\" method=\"post\">\n");
			out.write("   <input type=\"submit\" class=\"submit\" value=\""+MCRTranslation.translate("Editor.Common.button.CancelSelect")+"\" />\n");
			out.write("</form><br/><br/>\n");
		}
		
		out.write("\n<ul style=\"list-style-type: none\">\n");
		boolean didIt = false;
		for (MCRCategory categ:categories){
			didIt = outputCategory(categ, MCRServlet.getBaseURL(), url.toString(),0, didIt);
		}
		if(!didIt){
			out.write("<li><b>"+MCRTranslation.translate("Webpage.browse.empty")+"</b></li>\n");
		}
		out.write("\n</ul>\n");	
		long d = System.currentTimeMillis()-start;
		out.write("\n\n<!-- ClassificationBrowser ENDE ("+Long.toString(d)+"ms) -->");
		Logger.getLogger(this.getClass()).debug("ClassificationBrowser displayed for: "+rootCateg.getId().getID()+"   ("+d+" ms)");
		tx.commit();
	}
	
	/**
	 * prints a category as li HTML object with a table
	 * @param categ - the category
	 * @param baseURL - the baseURL of the web application
	 * @param cbURL - the Classification Browser with all parameters 
	 * @param curLevel - the current level to calculate the depth
	 * @throws IOException
	 */
	private boolean outputCategory(MCRCategory categ, String baseURL, String cbURL, int curLevel, boolean didIt) throws IOException{
		JspWriter out = getJspContext().getOut();
		boolean result = didIt;
		boolean hasChildren = categ.hasChildren();
		boolean hasLinks = hasLinks(categ);
		boolean opened =  path.contains(categ.getId().getID());
		if(!(hideemptyleaves && !hasLinks)){
			result = true;
			StringBuffer sbIndent = new StringBuffer("   ");
			for(int i=0;i<curLevel;i++){sbIndent.append("   ");}
			String indent = sbIndent.toString();
			out.write(indent+"<li>\n");
			out.write(indent+"   <table valign=\"center\">\n");
			out.write(indent+"      <tr><td rowspan=\"3\">\n");
			String iconURL = retrieveIconURL(hasChildren, curLevel, hasLinks(categ), (expand || opened));
			if(!expand && hasChildren && curLevel+1<level){
				String title = "";
				if(iconURL.endsWith("_plus.gif")){
					title = MCRTranslation.translate("Webpage.browse.open");
				}
				if(iconURL.endsWith("_minus.gif")){
					title = MCRTranslation.translate("Webpage.browse.close");
				}
				out.write(indent+"         <a href=\""+cbURL+"/"+categ.getId().getID()+"\" title=\""+title+"\">\n");
			}
			out.write(indent+"            <img class=\"borderless\" src=\""+baseURL+iconURL+"\" />\n");
			if(!expand && hasChildren && curLevel+1<level){
				out.write("                </a>");
			}
			out.write(indent+"      </td>\n");
			out.write(indent+"      <td>");
			if(showid){
				out.write(categ.getId().getID()+":&nbsp;");
			}
			
			out.write(categ.getCurrentLabel().getText());			
			out.write("</td>\n");
			
			if(count){
				out.write(indent+"      <td>");
				if(searchrestriction!=null){
					out.write("&nbsp;&nbsp;&nbsp;("+countBySearch(categ.getId().getID())+")&nbsp;&nbsp;&nbsp;");
				}
				else{
					countLinkMap.putAll(CATEGLINKSERVICE.countLinks(categ, true));
					Number n = countLinkMap.get(categ.getId());
					if(n!=null){
						out.write("&nbsp;&nbsp;&nbsp;("+n.toString()+")&nbsp;&nbsp;&nbsp;" );
					}
					else{
						out.write("&nbsp;&nbsp;&nbsp;(0)&nbsp;&nbsp;&nbsp;" );
					}
				}
				out.write("</td>\n");
			}
			
			out.write(indent+"      <td>");
			writeLinkedCategoryItemText(categ, baseURL, out);
			out.write(indent+"      </td>");
			out.write(indent+"      </tr>\n");		
		
			if(showdescription){
				String descr = categ.getCurrentLabel().getDescription();
				if(descr!=null && descr.length()>0){
					out.write(indent+"      <tr><td>"+descr+"</td></tr>\n");
				}
			}
			if(showuri){
				URI uri = categ.getURI();
				if(uri!=null && uri.toString().length()>0){
					out.write(indent+"      <tr><td>"+uri.toString()+"</td></tr>\n");
				}
			}
			out.write(indent+"   </table>\n");
			if((expand || opened) && hasChildren){
				if(curLevel+1<level){
					out.write(indent+"   <ul style=\"list-style-type:none; margin:0px; padding-top:0px;padding-bottom:0px\">\n");
			for(MCRCategory c: categ.getChildren()){
				outputCategory(c, baseURL, cbURL, curLevel+1, didIt);
			}
			out.write(indent+"   </ul>\n");
			}
		}
		
		out.write(indent+"</li>\n");
		}
		return result;
	}
	
	/**
	 * retrieves a URL for the proper category icon to be displayed, based on certain asumption
	 * @param hasChildren - does the current category has child categories
	 * @param curLevel - the current level
	 * @param hasLinks - are there items in the category
	 * @param opened - is the category already displayed "opened"
	 * @return
	 */
	private String retrieveIconURL(boolean hasChildren, int curLevel, boolean hasLinks, boolean opened){
		if(expand){
			if(opened && hasChildren && hasLinks) return "images/folder_open.gif";
			if(opened && hasChildren && !hasLinks) return "images/folder_open_empty.gif";
		}
		if(curLevel+1<level){
			if(!opened && hasChildren && hasLinks) return "images/folder_plus.gif";
			if(!opened && hasChildren && !hasLinks) return "images/folder_plus_empty.gif";
			if(opened && hasChildren && hasLinks) return "images/folder_minus.gif";
			if(opened && hasChildren && !hasLinks) return "images/folder_minus_empty.gif";
		}
		if(hasLinks) return "images/folder_plain.gif";
		if(!hasLinks) return "images/folder_plain_empty.gif";
		
		return "";
	}
	
	/**
	 * checks, if there are items in the given category
	 * if counting is enabled, this information will be retrieved from the count map
	 * otherwise from the hasLinkMaps (faster execution time)
	 * @param category - the category object
	 * @return if there are items in the category
	 */
	private boolean hasLinks(MCRCategory category){
		if(searchrestriction!=null){
			return countBySearch(category.getId().getID())>0;
		}
		
		if(count){
			Number n = countLinkMap.get(category.getId());
			if(n == null){
				return false;
			}
			else{
				return n.longValue()>0;
			} 
		}
		else{
			Boolean b = hasLinkMap.get(category.getId());
			if(b == null){
				return false;
			}
			else{
				return b.booleanValue();
			} 
		}
	}
	
	
	/**
	 * create the text for a category and if necessary a link around it
	 * @param categ - the MCRCategory to be displayed
	 * @param baseURL - the baseurl
	 * @param out - the JSPWriter
	 * @throws IOException
	 */
	private void writeLinkedCategoryItemText(MCRCategory categ, String baseURL, JspWriter out) throws IOException{
		boolean showLinks = linkall || hasLinks(categ);
		out.write("&nbsp;&nbsp;&nbsp;");
		if(showLinks){
			PageContext context = (PageContext) getJspContext();
			
			HttpServletRequest request = (HttpServletRequest) context.getRequest();
			StringBuffer url =  new StringBuffer(baseURL);
			if(request.getParameter("XSL.subselect.session.SESSION")!=null){
				//do a subselect / create a url, that returns to an editor
				url.append("servlets/XMLEditor");
				url.append("?_action=end.subselect");
				url.append("&subselect.session="+request.getParameter("XSL.subselect.session.SESSION"));
				url.append("&subselect.varpath="+request.getParameter("XSL.subselect.varpath.SESSION"));
				url.append("&subselect.webpage="+URLEncoder.encode(request.getParameter("XSL.subselect.webpage.SESSION"), "ISO-8859-1"));
				url.append("&_var_@categid="+categ.getId().getID());
				url.append("&_var_@type="+URLEncoder.encode(categ.getCurrentLabel().getText(), "ISO-8859-1"));
				
			}
			else{
				//"normal" classification browser - do a search
				url.append("servlets/MCRJSPSearchServlet");
				url.append("?query="+generateQuery(categ.getId().getID()));
				url.append("&mask="+searchmask);
			}			
			out.write("<a href=\""+url.toString()+"\">");
		}
		out.write(MCRTranslation.translate("Editor.Common.button.Choose"));
		if(showLinks){
			out.write("</a>");
		}
	}
	
    /**
     * clears the path of opened / closed categories of the browser
     * @param uri
     * @return the cleaned path
     */
    private String clearPath(String uri){
    	 final String[] uriParts = uri.split("/"); 
    
        // remove double entries from path
        // (if an entry appears the 2nd time it is "closed" and should not be displayed 
    	// -> so we can remove it here)
       path.clear();
        for (int i = 0; i < uriParts.length; i++) {
            String x = uriParts[i];
            if(x.length()>0){
            	if (path.contains(x)) {
                	path.remove(x);
            	} else {
            		path.add(x);
            	}
            }
        }
        String result = "";
          for (String uriPart : path) {
          result += "/" + uriPart;
        }
        return result;
    }
    
    /**
     * creates a MCRQuery string with the given searchrestriction and the categoryID
     * which can be used for counting and to create the displayed query link
     * @param categid - the category id
     * @return a string, representing a MCRQuery in textual syntax
     */
    private String generateQuery(String categid){
    	StringBuffer result = new StringBuffer();
    	if(searchrestriction!=null){
    		result.append("("+searchrestriction.replace("=", "+=+").replace(" ", "+")+")");
    		result.append("+and+");
    		result.append("("+searchfield+"+=+"+categid+")");
    	}
    	else{
    		result.append(searchfield+"+=+"+categid);
    	}
    	return result.toString();
    	
    }
    
    /**
     * counts using MCRQueries instead of information about links
     * To improve performance a "pull through cache" is used. That means
     * the cache knows how to generate items it does not contain
     *   
     * @param categid - the category iD
     * @return the number of results of the query for the given ID
     */
    private int countBySearch(String categid) {
		String qs = generateQuery(categid).replace("+"," ");
    	return ((Integer)cbHitCountCache.get(qs).getObjectValue()).intValue();
	}
}
