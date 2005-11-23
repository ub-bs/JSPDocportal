package org.mycore.frontend.jsp;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.mycore.frontend.jsp.navigation.NavEntry;
import org.mycore.frontend.jsp.navigation.NavNode;
import org.mycore.frontend.jsp.navigation.NavTree;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.common.MCRSession;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import javax.servlet.RequestDispatcher;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


import org.apache.log4j.Logger;

public class NavServlet extends HttpServlet
{
    private static Logger log = Logger.getLogger(NavServlet.class);
    private static NavTree navTree;
    private static String baseURL ;

    public static void initialize()
    {
        getNewTree(); //precache
    }
    
    public static void deinitialize()
    {
        navTree = null; //free memory
        baseURL = null;
    }    
    
    private static NavTree getTree(boolean force) {
    	if (!force && navTree != null) {
    		return navTree;
    	} else {
    		return getNewTree();
    	}
    }
    
    public static String getNavigationBaseURL() {
    	return baseURL ;
    }
    
    private static NavTree getNewTree() 
    {
    	
        Document document;
        NavTree tree = new NavTree();
        
        
        try {
        	InputStream in = Class.forName("org.mycore.frontend.jsp.NavServlet").getResourceAsStream("/navigation.xml");
        	if (in == null) {
                log.error("Missing file navigation.xml");
                return null;
            }            	

        	SAXBuilder builder = new SAXBuilder(false);
            document = builder.build(in);

            XPath navigationXP = XPath.newInstance("/navigations/navigation");
			XPath navitemXP = XPath.newInstance("navitem");
			XPath refitemXP = XPath.newInstance("refitem");            
            
            for (Iterator navIT = navigationXP.selectNodes(document).iterator();
            		navIT.hasNext();) {
				Element navigation = (Element) navIT.next();

				LinkedList lElements = new LinkedList();
				LinkedList lPaths = new LinkedList();
				
				// Stack-Initialization
				for (Iterator it = navitemXP.selectNodes(navigation).iterator(); it.hasNext();) {
					Element navitem = (Element) it.next();
					lElements.addLast(navitem);
					lPaths.addLast(navitem.getAttributeValue("name"));
				}
				while(!lElements.isEmpty() && !lPaths.isEmpty()) {
					Element navitem = (Element) lElements.removeFirst();
					String path = (String) lPaths.removeFirst();
					String description = navitem.getAttributeValue("label");
					String page = navitem.getAttributeValue("href");
					if ( (navitem.getAttributeValue("hidden") != null) && 
						 (navitem.getAttributeValue("hidden").equals("true"))	) {
						tree.addHiddenNode(path,description,page);
					} else if ( (navitem.getAttributeValue("extern") != null) && 
							    (navitem.getAttributeValue("extern").equals("true")) ) {
						tree.addExternalNode(path,description,page);
					} else {
						tree.addNode(path,description,page);
					}
					for (Iterator it = navitemXP.selectNodes(navitem).iterator(); it.hasNext();) {
						Element childNavitem = (Element) it.next();
						lElements.addLast(childNavitem);
						lPaths.addLast(path + "." + childNavitem.getAttributeValue("name"));
					}
					for (Iterator it = refitemXP.selectNodes(navitem).iterator(); it.hasNext();) {
						Element refitem = (Element) it.next();
						String refPath = refitem.getAttributeValue("name");
						String refDescription = refitem.getAttributeValue("label");
						String refLink = path;
						tree.addReference(refPath,refDescription,refLink);
					}					
				}
				 
			}
            navTree = tree;
            
        } catch (Exception e) {
            log.error(e);
        }
    	return tree;
    }    	
    	


    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	if ((baseURL == null) || baseURL.equals(""))  {
    		log.debug("baseURL is null");
    		String contextPath = request.getContextPath();
    		if (contextPath == null)
    			contextPath = "";
    		contextPath += "/";
    		String requestURL = request.getRequestURL().toString();
    		int pos = requestURL.indexOf(contextPath, 9);
    		baseURL = requestURL.substring(0, pos) + contextPath;
    		log.debug("baseURL = " + baseURL);
    	}
    	
        MCRSession session = MCRServlet.getSession(request);

        ServletContext context = this.getServletContext();
        if(!"yes".equals(context.getAttribute("startup_done"))) {
            PrintWriter out = response.getWriter();
            out.write("<html><body><h1>Application offline</h1>The application is curerntly offline. Probably it's still starting up, or has been shut down.</body></html>");
            return;
        }
        
        NavTree tree = getTree(false);
        String path = request.getParameter("path");
        if(path==null) {
            path = "left";
        }

	log.debug("Navigation servlet called with path "+path+" (session "+session.getID()+")");

        NavNode node;
        NavEntry e;
        while(true) {
            node = tree.getChild(path);
            e = node.getValue();
            if(e.isReference()) {
                path = e.getLink();
            } else break;
        }

        log.debug("Processing navigation path "+path);
        
        if (e.isExtern()) {
       		response.sendRedirect(e.getPage());
        	return;
        }
        
        tree.flag(path);

        request.setAttribute("node", node);
        request.setAttribute("path", path);

        request.setAttribute("nav", tree);

        if(e==null || e.getPage()==null) {
            request.setAttribute("content", "content/dummy.jsp");
        } else {
            request.setAttribute("content", e.getPage());
        }
        //response.setHeader("Expires", "Thu, 08 Dec 2005 16:00:00 GMT");
        context.getRequestDispatcher("/frame.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doGet(request, response);
    }
    protected void doGetPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doGet(request, response);
    }    
}
