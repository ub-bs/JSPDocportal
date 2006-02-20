package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;
import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;

import org.mycore.access.MCRAccessInterface;
import org.mycore.access.MCRAccessManager;

public class MCRCheckAccessTag extends SimpleTagSupport
{
	private String permission;
	private String var;
	private String key;
	
	private static MCRAccessInterface AI = MCRAccessManager.getAccessImpl();

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
		boolean accessAllowed=false;
		PageContext pageContext = (PageContext) getJspContext();
		if ( key == null || "".equals(key)) // allgemeiner check des aktuellen Users
			accessAllowed = AI.checkPermission(permission);
		else 
			accessAllowed = AI.checkPermission(key, permission);
		
		pageContext.setAttribute(var, Boolean.toString(accessAllowed));
		return;
	}	

}