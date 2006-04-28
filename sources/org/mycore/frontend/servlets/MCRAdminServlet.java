package org.mycore.frontend.servlets;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.mycore.access.MCRAccessManager;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRException;
import org.mycore.frontend.servlets.MCRServlet;

/**
 * 
 * @deprecated
 */
public class MCRAdminServlet extends MCRServlet{
	
	private static final long serialVersionUID = 1L;
	private static boolean permissionsAreSet = false;
	protected final static Logger LOGGER = Logger.getLogger(MCRAdminServlet.class);

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        try{
        	if(!permissionsAreSet) createNonExistingAdminPermissions();
        	
            ServletContext context = this.getServletContext();
            
            String page = request.getParameter("path");
            if (page == null || page.equals("")){
                page = "main.jsp";
            }else{
                page += ".jsp";
            }
            request.setAttribute("page", page );
            
            
            
            
            String requestPath = request.getPathInfo();
            if (requestPath == null || requestPath.equals("/")){
                requestPath = "/main";
            }
            request.setAttribute("path", requestPath );
            
            //Stylepath
            String stylepath = "/";
            java.util.StringTokenizer st = new java.util.StringTokenizer(request.getRequestURI(), "/");
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                if (token.equals("admin")){
                    break;
                }else{
                    stylepath += token + "/";
                }
             }

            request.setAttribute("basepath", stylepath + "administration/" );
            stylepath +="administration/css/admin.css";
            request.setAttribute("stylepath", stylepath );

            context.getRequestDispatcher("/admin/index.jsp").forward(request, response);

            
        }catch(MCRException e){
            LOGGER.error("Catched Error Stacktrace", e);
        }
        
    }
    
	/**
	 * sets a default-rules for the use of the admin functions
	 * 
	 * @param objid
	 * @param userid
	 * @return boolean  false if there was an Exception
	 */
	public static boolean createNonExistingAdminPermissions() {
		try{
			List savedPermissions = AI.getPermissions();
			String permissions = MCRConfiguration.instance().getString("MCR.AccessAdminInterfacePermissions","admininterface-access,admininterface-user,admininterface-accessrules");
			for (Iterator it = Arrays.asList(permissions.split(",")).iterator(); it.hasNext();) {
				String permission = ((String) it.next()).trim().toLowerCase();
				if(!permission.equals("") && !savedPermissions.contains(permission)) {
					AI.addRule(permission, MCRAccessManager.getFalseRule(), "");
				}
			}
		}catch(MCRException e) {
			LOGGER.error("could not create admin interface permissions", e);
			return false;
		}
		return true;
	} 
}
