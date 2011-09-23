package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;
import org.hibernate.Transaction;
import org.mycore.access.MCRAccessManager;
import org.mycore.backend.hibernate.MCRHIBConnection;
import org.mycore.common.MCRSession;
import org.mycore.common.MCRSessionMgr;

public class MCRCheckAccessTag extends SimpleTagSupport
{
	private String permission;
	private String var;
	private String key;
	
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
		Transaction t1=null;
		try {
    		Transaction tx  = MCRHIBConnection.instance().getSession().getTransaction();
	   		if(tx==null || !tx.isActive()){
				t1 = MCRHIBConnection.instance().getSession().beginTransaction();
			}
			PageContext pageContext = (PageContext) getJspContext();			
			MCRSession mcrSession = MCRSessionMgr.getCurrentSession();
			if ( mcrSession.getUserInformation().getCurrentUserID().equals("guest") ){
				pageContext.setAttribute(var, new Boolean(false));	
			}
			else if ( key == null || "".equals(key)){ // allgemeiner check des aktuellen Users
				pageContext.setAttribute(var, new Boolean(MCRAccessManager.checkPermission(permission)));
			}
			else{ 
				pageContext.setAttribute(var, new Boolean(MCRAccessManager.checkPermission(key, permission)));
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