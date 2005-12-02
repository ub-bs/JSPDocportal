package org.mycore.frontend.jsp;

import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.jdom.xpath.XPath;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.servlets.MCRServletJob;
import org.mycore.common.JSPUtils;
import org.mycore.common.MCRSession;
import org.mycore.common.xml.MCRURIResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;
import java.io.PrintWriter;
import java.util.Iterator;
import org.apache.log4j.Logger;

public class NavServlet extends MCRServlet
{
    private static Logger logger = Logger.getLogger(NavServlet.class);

    private static String baseURL ;
    private static org.jdom.Document navJdom;
    private static org.w3c.dom.Document navDom;
    
    public void initializeParameters(HttpServletRequest request)
    {
        //getNewTree(); //precache
    	setBaseURL(request);
    	setNavJdom();
    	setNavDom(navJdom);
    }
    
    public static void deinitialize()
    {
        //navTree = null; //free memory
        baseURL = null;
        navJdom = null;
        navDom = null;
    }    
    
    public void setBaseURL(HttpServletRequest request) {
		logger.debug("baseURL is null");
		ServletContext context = getServletContext();
		String contextPath = request.getContextPath();
		if (contextPath == null)
			contextPath = "";
		contextPath += "/";
		String requestURL = request.getRequestURL().toString();
		int pos = requestURL.indexOf(contextPath, 9);
		baseURL = requestURL.substring(0, pos) + contextPath;
    	if (context.getAttribute("WebApplicationBaseURL") == null) {
    		context.setAttribute("WebApplicationBaseURL", baseURL);
    	}    		
		logger.debug("baseURL = " + baseURL);    	
    }
    public void setNavJdom() {
    	ServletContext context = getServletContext();
    	org.jdom.Element navigationEl = MCRURIResolver.instance().resolve("resource:navigation.xml");
    	int i = 0;
    	for (Iterator it = navigationEl.getDescendants(new ElementFilter()); it.hasNext();) {
			Element navEl = (Element) it.next();
			String path;
			String extern = navEl.getAttributeValue("extern");
			if(extern != null && extern.equals("true")) {
				if(extern.indexOf(".") > -1)
					path = navEl.getAttributeValue("href");
				else
					path = baseURL + navEl.getAttributeValue("href");
			}else {
				Element parent = navEl;
				int k = 0;
				StringBuffer pathSB = new StringBuffer("");
				while(parent != null) {
		        	if (!parent.getName().equals("navitem")) break;
		        	if (k != 0) {
		        		pathSB.insert(0,".");
		        	}
		        	pathSB.insert(0, parent.getAttributeValue("name"));
		        	parent =  parent.getParentElement();
		        	k++;					
				}
				pathSB.insert(0,"nav?path=").insert(0,baseURL);
				path = pathSB.toString();
			}
			if(navEl.getName().equals("navitem")) {
				navEl.setAttribute("path", path);
			}
			String systemID = JSPUtils.fillToConstantLength(String.valueOf(i),"0",4);
			navEl.setAttribute("systemID",systemID);
			i++;
		}
    	navJdom = navigationEl.getDocument();
    	context.setAttribute("navJdom",navJdom);
    	logger.debug(JSPUtils.getPrettyString(navJdom));
    	return;
    }
    
    public void setNavDom(org.jdom.Document jdom) {
    	ServletContext context = getServletContext();
    	org.w3c.dom.Document domDoc = null;
		try {
			domDoc = new org.jdom.output.DOMOutputter().output(jdom);
		} catch (org.jdom.JDOMException e) {
			Logger.getLogger(NavServlet.class).error("Domoutput failed: ", e);
		}  
		navDom = domDoc;
		context.setAttribute("navDom",navDom);
		return;
    }
    
    public static String getNavigationBaseURL() {
    	return baseURL ;

    }
    
    protected void doGetPost(MCRServletJob job) throws Exception
    {
    	HttpServletRequest request = job.getRequest();
    	HttpServletResponse response = job.getResponse();
    	ServletContext context = this.getServletContext();
    	if ((baseURL == null) || baseURL.equals("") || (navJdom == null) || (navDom == null))  {
    		initializeParameters(request);
    	}
        MCRSession session = MCRServlet.getSession(request);
        if(!"yes".equals(context.getAttribute("startup_done"))) {
            PrintWriter out = response.getWriter();
            out.write("<html><body><h1>Application offline</h1>The application is curerntly offline. Probably it's still starting up, or has been shut down.</body></html>");
            return;
        }

        org.jdom.Element youAreHere = new org.jdom.Element("youAreHere");
        StringBuffer pathID = new StringBuffer("");
        String nodeID = "";
        String path = request.getParameter("path");
        if(path == null) {
            path = "left";
        }
        logger.debug("Navigation servlet called with path "+path+" (session "+session.getID()+")");

        Element navitem = null;
        StringBuffer pathSB = new StringBuffer("");
        if(path.startsWith("~")) {
        	org.jdom.Element refitem = (Element) XPath.selectSingleNode(navJdom,"/navigations//refitem[@name='" + path + "']");
            navitem = refitem.getParentElement();
        }else{
        	String[] nodes = path.split("\\.");
        	StringBuffer xpath = new StringBuffer("/navigations/");
        	for (int i = 0; i < nodes.length; i++) {
        		logger.debug("i = " + i);
        		xpath.append("/navitem[@name='").append(nodes[i]).append("']");
			}
        	navitem = (Element) XPath.selectSingleNode(navJdom, xpath.toString());
        }
        String contentPage = navitem.getAttributeValue("href");
        String extern = navitem.getAttributeValue("extern");
        nodeID = navitem.getAttributeValue("systemID");
        
        if ( extern != null && extern.equals("true")) {
       		response.sendRedirect(navitem.getAttributeValue("href"));
        	return;
        }        
        
        int i = 0;
        while(navitem != null) {
        	if (!navitem.getName().equals("navitem")) break;
        	if (i != 0) {
        		pathSB.insert(0,".");
        		pathID.insert(0,".");
        	}
        	pathSB.insert(0, navitem.getAttributeValue("name"));
        	pathID.insert(0,navitem.getAttributeValue("systemID"));
        	Element youAreHereElement = (Element)navitem.clone();
        	youAreHereElement.removeChildren("navitem");
        	youAreHere.addContent(0,youAreHereElement);
        	navitem =  navitem.getParentElement();
        	i++;
        }
    	org.w3c.dom.Document domYouAreHere = null;
		try {
			domYouAreHere = new org.jdom.output.DOMOutputter().output(new org.jdom.Document(youAreHere));
		} catch (org.jdom.JDOMException e) {
			Logger.getLogger(NavServlet.class).error("Domoutput failed: ", e);
		}  
        path = pathSB.toString();        
        

        logger.debug("Processing navigation path " + path);
        logger.debug("Processing navigation pathID " + pathID.toString());
        logger.debug("Processing navigation nodeID " + nodeID);
        
        request.setAttribute("nodeID", nodeID);
        request.setAttribute("path", path);
        request.setAttribute("pathID", pathID.toString());
        request.setAttribute("youAreHere",domYouAreHere);
        
        if(contentPage == null || contentPage.equals("")) {
            request.setAttribute("content", "content/dummy.jsp");
        } else {
            request.setAttribute("content", contentPage);
        }
        //response.setHeader("Expires", "Thu, 08 Dec 2005 16:00:00 GMT");
        try{
        	context.getRequestDispatcher("/frame.jsp").forward(request, response);
        }catch(Exception e) {
        	deinitialize();
        	logger.error("catched error: ", e);
        }
        
    }
}
