package org.mycore.frontend.jsp;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;
import org.mycore.common.MCRSession;
import org.mycore.common.MCRConfiguration;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.servlets.MCRServletJob;


public class LogoutServlet extends MCRServlet {
    // The configuration
    private static Logger LOGGER = Logger.getLogger(LogoutServlet.class);

    protected void doGetPost(MCRServletJob job) throws Exception
    {
        HttpServletRequest request = job.getRequest();
        HttpServletResponse response = job.getResponse();
        MCRSession session = MCRServlet.getSession(request);
        String uid = session.getCurrentUserID();
        LOGGER.debug("Log out user "+uid);
        session.setCurrentUserID(MCRConfiguration.instance().getString("MCR.users_guestuser_username", "gast"));
        this.getServletContext().getRequestDispatcher("/content/index.jsp").include(request,response);
    }
}
