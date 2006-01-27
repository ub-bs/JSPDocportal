package org.mycore.frontend.servlets;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.mycore.access.MCRAccessInterface;
import org.mycore.access.MCRAccessManager;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRException;
import org.mycore.common.xml.MCRXMLHelper;
import org.mycore.frontend.servlets.MCRServlet;

public class MCRAdminServlet extends MCRServlet{
	
	private static final long serialVersionUID = 1L;
	private static boolean isDefaultAccessRuleCreated ;
	protected final static Logger LOGGER = Logger.getLogger(MCRAdminServlet.class);

	public void init() throws ServletException {
		super.init();
		isDefaultAccessRuleCreated = createAdminDefaultRule();
	}
    
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        try{
        	if(!isDefaultAccessRuleCreated) createAdminDefaultRule();
        	
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
	 * sets a default-rule for the use of the MCRAdminServlet
	 * 
	 * @param objid
	 * @param userid
	 * @return boolean  false if there was an Exception
	 */
	public static boolean createAdminDefaultRule() {
		try {
			String strStandardrule = MCRConfiguration.instance().getString("MCR.AccessRule.ADMININTERFACE-DEFAULTRULE","<condition format=\"xml\"><condition field=\"user\" operator=\"=\" value=\"administrator\" /></condition>");
			Element standardrule = (Element)MCRXMLHelper.parseXML(strStandardrule).getRootElement().detach();
			MCRAccessInterface AI = MCRAccessManager.getAccessImpl();
			AI.addRule("use-admininterface", standardrule);
		} catch (MCRException e) {
			LOGGER.debug("catched error", e);
			return false;
		}				
		return true;
	}    
}
