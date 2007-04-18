package org.mycore.frontend.jsp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRSession;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.servlets.MCRServletJob;


public class LogoutServlet extends MCRServlet {

	private static final long serialVersionUID = 1L;
	// The configuration
    private static Logger LOGGER = Logger.getLogger(LogoutServlet.class);

    protected void doGetPost(MCRServletJob job) throws Exception
    {
        HttpServletRequest request = job.getRequest();
        HttpServletResponse response = job.getResponse();
        MCRSession session = MCRServlet.getSession(request);
        String uid = session.getCurrentUserID();
        LOGGER.debug("Log out user "+uid);
        session.setCurrentUserID(MCRConfiguration.instance().getString("MCR.Users.Guestuser.UserName", "gast"));
        this.getServletContext().getRequestDispatcher("/content/index.jsp").include(request,response);
    }
}
