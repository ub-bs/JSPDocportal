package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;
import org.apache.log4j.Logger;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRSession;
import org.mycore.frontend.servlets.MCRServlet;


public class MCRSessionTag extends SimpleTagSupport
{
	private String method;
	private String var;
	private String type;
	private String key;

	public void setMethod(String inputMethod) {
		method = inputMethod;
		return;
	}
	public void setVar(String inputVar) {
		var = inputVar;
		return;
	}
	public void setType(String inputType) {
		type = inputType;
		return;
	}
	public void setKey(String inputKey) {
		key = inputKey;
		return;
	}	
	public void doTag() throws JspException, IOException {
		PageContext pageContext = (PageContext) getJspContext();
		MCRSession mcrSession = MCRServlet.getSession((HttpServletRequest)pageContext.getRequest());
		if (!method.equals("set") && !method.equals("get")) {
			Logger.getLogger(MCRSessionTag.class).error("unknown method: " + method);
			return;
		}
		if (type != null && !type.equals("")) {
			if (type.equals("userID")) {
				if (method.equals("get"))
					pageContext.setAttribute(var, mcrSession.getCurrentUserID());
				else
					mcrSession.setCurrentUserID((String)pageContext.getAttribute(var));
				
			} else if (type.equals("language")) {
				if (method.equals("get"))
					pageContext.setAttribute(var, mcrSession.getCurrentLanguage());
				else
					mcrSession.setCurrentLanguage((String)pageContext.getAttribute(var));
			} else if (type.equals("IP")) {
				if (method.equals("get"))
					pageContext.setAttribute(var, mcrSession.getCurrentIP());
				else
					mcrSession.setCurrentIP((String)pageContext.getAttribute(var));
			} else if (type.equals("userName")) {
				if (method.equals("get")) {
					String userName = mcrSession.getCurrentUserName();
					if (userName == null) userName = MCRConfiguration.instance().getString("MCR.users_guestuser_username","Gast");
					pageContext.setAttribute(var, userName);
				}else
					mcrSession.setCurrentUserName((String)pageContext.getAttribute(var));
			} else if (type.equals("ID")) {
				if (method.equals("get"))
					pageContext.setAttribute(var, mcrSession.getID());
				else
					Logger.getLogger(MCRSessionTag.class).error("set not possible for type ID!");
			} else {
				Logger.getLogger(MCRSessionTag.class).error("unknown type: " + type);
			}
			return;
		}else if(key != null && !key.equals("")){
			if (method.equals("get"))
				pageContext.setAttribute(var, mcrSession.get(key));
			else
				mcrSession.put(key,pageContext.getAttribute(var));
			return;
		}
		return;
	}	

}