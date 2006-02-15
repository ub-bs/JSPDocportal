package org.mycore.frontend.jsp;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.mycore.common.MCRSession;
import org.mycore.common.MCRConfiguration;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.servlets.MCRServletJob;
import org.mycore.user2.MCRUserMgr;
import org.mycore.user2.MCRUser;

public class LoginServlet extends MCRServlet {

	private static final long serialVersionUID = 1L;
	// The configuration
    private static Logger LOGGER = Logger.getLogger(LoginServlet.class);

    protected void doGetPost(MCRServletJob job) throws Exception
    {
        HttpServletRequest request = job.getRequest();
        HttpServletResponse response = job.getResponse();    
        
        ServletContext context = this.getServletContext();
        
        boolean loginOk = false;

        MCRSession session = MCRServlet.getSession(request);

        String uid = request.getParameter("uid");
        String pwd = request.getParameter("pwd");

        if (uid != null)
            uid = (uid.trim().length() == 0) ? null : uid.trim();
        if (pwd != null)
            pwd = (pwd.trim().length() == 0) ? null : pwd.trim();

        if(uid==null || pwd == null) {
            context.getRequestDispatcher("/content/login.jsp").include(request, response);
            return;
        }

        LOGGER.debug("Trying to log in user "+uid);

        try {
            loginOk = ((uid != null) && (pwd != null) && MCRUserMgr.instance().login(uid, pwd));
            if(!loginOk)
                LOGGER.debug("Login failed");
        } catch(Throwable e) {
            LOGGER.error("error while verifiying user "+uid, e);
            loginOk = false;
        }

        if (loginOk) {
            session.setCurrentUserID(uid);
            MCRUser user = MCRUserMgr.instance().getCurrentUser();

            LOGGER.info("MCRLoginServlet: user " + uid + " ("+user.getNumID()+") logged in successfully (session "+session.getID()+")");

            request.setAttribute("user", user);
            String authorid = getAuthorID(user.getNumID());
            request.setAttribute("authorid", authorid);

            context.getRequestDispatcher("/content/loggedin.jsp").include(request, response);
        } else {
            request.setAttribute("error", "Die angegebene Benutzerkennung ist unbekannt");
            context.getRequestDispatcher("/content/login.jsp").include(request, response);
        }
    }

    public String getAuthorID(long id)
    {
        String pattern = MCRConfiguration.instance().getString("MCR.metadata_objectid_number_pattern");
        String num = ""+id;
        String project = MCRConfiguration.instance().getString("MCR.default_project_id");
        return project+"_author_"+pattern.substring(num.length())+num;
    }
}
