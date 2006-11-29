package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;

import org.apache.log4j.Logger;
import org.mycore.access.MCRAccessInterface;
import org.mycore.access.MCRAccessManager;
import org.mycore.common.MCRSession;
import org.mycore.frontend.servlets.MCRServlet;

public class MCRCheckAccessTag extends SimpleTagSupport
{
	private String permission;
	private String var;
	private String key;
	
	private static MCRAccessInterface AI = MCRAccessManager.getAccessImpl();
	private static Logger LOGGER = Logger.getLogger(MCRCheckAccessTag.class);

	public void setPermission(String inputPermission) {
		permission = inputPermission;
		return;
	}
	public void setVar(String inputVar) {
		var = inputVar;
		return;
	}
	public void setKey(String inputKey) {
		key = inputKey;
		return;
	}	
	public void doTag() throws JspException, IOException {
		try{

			PageContext pageContext = (PageContext) getJspContext();			
			MCRSession mcrSession = MCRServlet.getSession((HttpServletRequest)pageContext.getRequest());
			
			if ( mcrSession.getCurrentUserID().equals("guest") )
				pageContext.setAttribute(var, new Boolean(false));	
			
			else if ( key == null || "".equals(key)) // allgemeiner check des aktuellen Users
				pageContext.setAttribute(var, new Boolean(AI.checkPermission(permission)));
			else 
				pageContext.setAttribute(var, new Boolean(AI.checkPermission(key, permission)));
			return;
		}catch(Exception e){
			LOGGER.error("could not check access", e);
		}
	}	

}