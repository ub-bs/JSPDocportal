package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;
import org.mycore.common.MCRSession;
import org.mycore.common.MCRSessionMgr;
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
		//"HttpJspBase" is the name of the servlet that handles JSPs
		if (!method.equals("set") && !method.equals("get") && !method.equals("init")) {
			Logger.getLogger(MCRSessionTag.class).error("unknown method: " + method);
			return;
		}
		if(method.equals("init")){
		    MCRSession mcrSession = MCRServlet.getSession((HttpServletRequest)pageContext.getRequest());
		    if(!mcrSession.getID().equals(MCRSessionMgr.getCurrentSessionID())){
		        MCRSessionMgr.setCurrentSession(mcrSession);
		    }
		    return;
		}
		
		MCRSession mcrSession = MCRServlet.getSession((HttpServletRequest)pageContext.getRequest());
		if (type != null && !type.equals("")) {
			if (type.equals("userID")) {
				if (method.equals("get"))
					pageContext.setAttribute(var, mcrSession.getUserInformation().getUserID());								
				
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
			/*never used? - commented 20101203 
			 } else if (type.equals("userName")) {
			 
				if (method.equals("get")) {
					String userName = mcrSession.getCurrentUserName();
					if (userName == null) userName = MCRConfiguration.instance().getString("MCR.Users.Guestuser.UserName","Gast");
					pageContext.setAttribute(var, userName);
				}else
					mcrSession.setCurrentUserName((String)pageContext.getAttribute(var));
			*/
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