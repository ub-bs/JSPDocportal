package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;

import org.mycore.access.MCRAccessInterface;
import org.mycore.access.MCRAccessManager;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRSession;
import org.mycore.frontend.servlets.MCRServlet;


public class MCRCheckAccessTag extends SimpleTagSupport
{
	private String pool;
	private String var;
	private String key;
	
	private static MCRAccessInterface AI = MCRAccessManager.getAccessImpl();

	public void setPool(String inputPool) {
		pool = inputPool;
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
		PageContext pageContext = (PageContext) getJspContext();
		boolean accessAllowed = AI.checkPermission(key, pool);
		if(accessAllowed)
			pageContext.setAttribute(var, "true");
		else
			pageContext.setAttribute(var, "false");
		return;
	}	

}