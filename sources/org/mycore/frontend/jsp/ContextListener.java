package org.mycore.frontend.jsp;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.mycore.common.JSPUtils;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.xml.MCRURIResolver;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowConstants;

public class ContextListener implements ServletContextListener
{
    public void contextInitialized(ServletContextEvent event)
    {
 	
        MCRConfiguration.class.getName();

        ServletContext context = event.getServletContext();
        try {context.removeAttribute("startup_done");} catch(Throwable nevermind) {}

        Map map = new HashMap();
        new MyMCRServlet("jspkey", map);

        Logger.getLogger(ContextListener.class).fatal("Context started");
        Logger.getLogger(ContextListener.class).error("Context started");
        Logger.getLogger(ContextListener.class).warn("Context started");
        Logger.getLogger(ContextListener.class).info("Context started");
        Logger.getLogger(ContextListener.class).debug("Context started");
        
        MCRURIResolver.init(context,NavServlet.getNavigationBaseURL());  
        JSPUtils.initialize();
        
        setApplicationScope(context);
        context.setAttribute("startup_done", "yes");
    }

    public void contextDestroyed(ServletContextEvent event)
    {
        Logger.getLogger(ContextListener.class).debug("Context stopped");
        ServletContext context = event.getServletContext();

        context.removeAttribute("startup_done");
        NavServlet.deinitialize();
        JSPUtils.deinitialize();
    }

    /**
     * sets application scope attributes
     * 	nav-attributes are set in the nav servlet
     *
     */
    public final void setApplicationScope(ServletContext context){
    	context.setAttribute( "constants", new MCRWorkflowConstants() );
    }
    
    public class MyMCRServlet extends MCRServlet
    {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		MyMCRServlet(String key, Object value) {
            MCRServlet.requestParamCache.put(key, value);
        }
    }
}
