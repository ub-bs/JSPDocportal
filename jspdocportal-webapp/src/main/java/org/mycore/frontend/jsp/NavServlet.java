package org.mycore.frontend.jsp;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.DOMBuilder;
import org.jdom.xpath.XPath;
import org.mycore.common.MCRSession;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.servlets.MCRServletJob;

public class NavServlet extends MCRServlet
{
	private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(NavServlet.class);

    private static org.jdom.Document navJdom;
   
    protected void doGetPost(MCRServletJob job) throws Exception
    {
    	HttpServletRequest request = job.getRequest();
    	HttpServletResponse response = job.getResponse();
    	response.setCharacterEncoding("UTF-8");
    	response.setContentType("text/html");
    	
    	ServletContext context = this.getServletContext();
    	MCRSession session = MCRServlet.getSession(request);
    	
    	setBaseURL(request);
    	
        if(navJdom==null){
        	DOMBuilder domB = new DOMBuilder();
        	navJdom = domB.build((org.w3c.dom.Document)context.getAttribute("navDom"));
        }
        
        String path = request.getParameter("path");
        if(path == null) {
            path = "left.start";
        }
        logger.debug("Navigation servlet called with path "+path+" (session "+session.getID()+")");

        Element navitem = null;
        try{
	        if(path.startsWith("~")) {
	        	
	        	XPath xp = XPath.newInstance("/n:navigations//n:refitem[@id='" + path + "']");
	        	xp.addNamespace(Namespace.getNamespace("n", "http://www.mycore.org/jspdocportal/navigation"));
	        	org.jdom.Element refitem = (Element) xp.selectSingleNode(navJdom);
	        	navitem = refitem.getParentElement();
	        }else{
	        	String[] nodes = path.split("\\.");
	        	StringBuffer xpath = new StringBuffer("/n:navigations");
	        	for (int i = 0; i < nodes.length; i++) {
	        		logger.debug("i = " + i);
	        		if(i==0){
	        			xpath.append("/n:navigation[@id='").append(nodes[i]).append("']");
	        		}
	        		else{
	        			xpath.append("/n:navitem[@id='").append(nodes[i]).append("']");
	        		}
				}
	        	XPath xp = XPath.newInstance(xpath.toString());
	        	xp.addNamespace(Namespace.getNamespace("n", "http://www.mycore.org/jspdocportal/navigation"));
	        	navitem = (Element) xp.selectSingleNode(navJdom);
	        }
        }catch(Exception ex){
        	logger.warn("wrong path" + path+" : "+ex.getMessage());
        	XPath xp = XPath.newInstance("/n:navigations//n:navitem[1]");
        	xp.addNamespace(Namespace.getNamespace("n", "http://www.mycore.org/jspdocportal/navigation"));
        	navitem = (Element) xp.selectSingleNode(navJdom);
        }
        if(navitem==null){
        	logger.error("Path could not be resolved: "+path );
        	XPath xp = XPath.newInstance("/n:navigations//n:navitem[1]");
        	xp.addNamespace(Namespace.getNamespace("n", "http://www.mycore.org/jspdocportal/navigation"));
        	navitem = (Element) xp.selectSingleNode(navJdom);
        }
        
        String contentPage = navitem.getAttributeValue("href");
        String extern = navitem.getAttributeValue("extern");
        if ( extern != null && extern.equals("true")) {
       		response.sendRedirect(navitem.getAttributeValue("href"));
        	return;
        }        
        
        org.w3c.dom.Document domYouAreHere = null;
		try {
			domYouAreHere = new org.jdom.output.DOMOutputter().output(new org.jdom.Document((Element)navitem.clone()));
		} catch (org.jdom.JDOMException e) {
			Logger.getLogger(NavServlet.class).error("Domoutput failed: ", e);
		}  
        
		request.setAttribute("youAreHere",domYouAreHere);
        
        if(contentPage == null || contentPage.equals("")) {
            contentPage = "content/index.jsp";
        }
        
        try{
        	context.getRequestDispatcher("/"+contentPage).include(request, response);
        }catch(Exception e) {
        
        	logger.error("catched error: ", e);
        }
        
    }
    
    /**
     * retrieves the baseURL - this can only be done by a servlet
     */
    public void setBaseURL(HttpServletRequest request) {
    	ServletContext context = getServletContext();
    	if (context.getAttribute("WebApplicationBaseURL") == null) {
    		logger.debug("baseURL is null");
    		
    		String contextPath = request.getContextPath();
    		if (contextPath == null){
    			contextPath = "";
    		}
    		contextPath += "/";
    		String requestURL = request.getRequestURL().toString();
    		int pos = requestURL.indexOf(contextPath, 9);
    		String baseURL = requestURL.substring(0, pos) + contextPath;
    	
    		context.setAttribute("WebApplicationBaseURL", baseURL);
    		logger.debug("baseURL now set to: " + baseURL);
    	}    		
    }	
}
