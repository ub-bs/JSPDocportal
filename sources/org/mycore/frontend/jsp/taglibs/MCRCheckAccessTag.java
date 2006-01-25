package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;
import org.mycore.access.MCRAccessManagerBase;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRSession;
import org.mycore.frontend.servlets.MCRServlet;


public class MCRCheckAccessTag extends SimpleTagSupport
{
	private String pool;
	private String var;
	private String key;
	
	private static MCRAccessManagerBase AM = (MCRAccessManagerBase) MCRConfiguration.instance().getInstanceOf("MCR.Access_class_name");

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
		MCRSession mcrSession = MCRServlet.getSession((HttpServletRequest)pageContext.getRequest());
		boolean accessAllowed = AM.checkAccess(pool, key, mcrSession);
		if(accessAllowed)
			pageContext.setAttribute(var, "true");
		else
			pageContext.setAttribute(var, "false");
		return;
	}	

}