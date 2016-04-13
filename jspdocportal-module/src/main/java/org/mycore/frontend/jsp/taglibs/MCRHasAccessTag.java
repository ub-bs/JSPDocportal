package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;
import org.hibernate.Transaction;
import org.mycore.access.MCRAccessManager;
import org.mycore.backend.hibernate.MCRHIBConnection;
import org.mycore.common.MCRSession;
import org.mycore.frontend.servlets.MCRServlet;

public class MCRHasAccessTag extends SimpleTagSupport
{
	private String permission;
	private String var;
	private String mcrid;
	
	private static Logger LOGGER = Logger.getLogger(MCRHasAccessTag.class);

	public void setPermission(String permission) {
		this.permission = permission;

	}
	public void setVar(String var) {
		this.var = var;
	}
	
	public void setMcrid(String mcrid) {
		this.mcrid = mcrid;
	}
	
	public void doTag() throws JspException, IOException {
		Transaction t1=null;
		try {
    		Transaction tx  = MCRHIBConnection.instance().getSession().getTransaction();
	   		if(tx==null || !tx.isActive()){
				t1 = MCRHIBConnection.instance().getSession().beginTransaction();
			}
			PageContext pageContext = (PageContext) getJspContext();
			MCRSession mcrSession = MCRServlet.getSession((HttpServletRequest)pageContext.getRequest());
		
			String userID = mcrSession.getUserInformation().getUserID();
			if ("guest gast".contains(userID)){
				pageContext.setAttribute(var, new Boolean(false));	
			}
			else if ( mcrid == null || "".equals(mcrid)){ // allgemeiner check des aktuellen Users
				pageContext.setAttribute(var, new Boolean(MCRAccessManager.checkPermission(permission)));
			}
			else{ 
				pageContext.setAttribute(var, new Boolean(MCRAccessManager.checkPermission(mcrid, permission)));
			}
		}catch(Exception e){
			LOGGER.error("could not check access", e);
		}
		finally{
    		if(t1!=null){
    			t1.commit();
    		}
    	}
	}	

}