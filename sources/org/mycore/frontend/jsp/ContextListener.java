package org.mycore.frontend.jsp;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;
import org.mycore.common.JSPUtils;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.xml.MCRURIResolver;
import org.mycore.frontend.servlets.MCRServlet;

import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

public class ContextListener implements ServletContextListener
{
    public void contextInitialized(ServletContextEvent event)
    {
    	
    	
        MCRConfiguration.class.getName();

        ServletContext context = event.getServletContext();
        try {context.removeAttribute("startup_done");} catch(Throwable nevermind) {}

//        final Properties p = new Properties();
//        String level = "DEBUG";
//        //String pattern = "%d{yyyy-MM-dd HH:mm:ss} %t %-5p %29.29c - %m%n";
//        String pattern = "%d{yyyy-MM-dd HH:mm:ss} %t %-5p %c - %m%n";
//        boolean append = false;
//        String logFileName = MCRConfiguration.instance().getString("MCR.LogFile", "mcrjsp.log");
//
//        p.setProperty("log4j.rootLogger", level+", logger");
//        p.setProperty("log4j.appender.logger", "org.apache.log4j.DailyRollingFileAppender");
//        p.setProperty("log4j.appender.logger.DatePattern", "'.'yyyyMMdd");
//        p.setProperty("log4j.appender.logger.DatePattern", "'.'yyyyMMdd");
//        p.setProperty("log4j.appender.logger.layout", "org.apache.log4j.PatternLayout");
//        p.setProperty("log4j.appender.logger.layout.ConversionPattern", pattern);
//        p.setProperty("log4j.appender.logger.Append", ""+append);
//        p.setProperty("log4j.appender.logger.File", logFileName);
//        p.setProperty("log4j.logger.org.apache", "WARN, stdout");
//        p.setProperty("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender");
//        p.setProperty("log4j.appender.stdout.layout", "org.apache.log4j.PatternLayout");
//        p.setProperty("log4j.appender.stdout.layout.ConversionPattern", pattern);
//        p.setProperty("log4j.logger.org.mycore.backend.hibernate", "ERROR");
//        p.setProperty("log4j.logger.org.hibernate", "ERROR");        
//        
//        PropertyConfigurator.configure(p);

        Map map = new HashMap();
        new MyMCRServlet("jspkey", map);

        Logger.getLogger(ContextListener.class).fatal("Context started");
        Logger.getLogger(ContextListener.class).error("Context started");
        Logger.getLogger(ContextListener.class).warn("Context started");
        Logger.getLogger(ContextListener.class).info("Context started");
        Logger.getLogger(ContextListener.class).debug("Context started");
        
        MCRURIResolver.init(context,NavServlet.getNavigationBaseURL());  
        JSPUtils.initialize();
//Logger.getLogger(ContextListener.class).debug((new GregorianCalendar()).toString());
//        SearchMaskServlet.initialize();
//Logger.getLogger(ContextListener.class).debug((new GregorianCalendar()).toString());
        //NavServlet.initialize();
        

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

    
    public class MyMCRServlet extends MCRServlet
    {
        MyMCRServlet(String key, Object value) {
            MCRServlet.requestParamCache.put(key, value);
        }
    }
}
