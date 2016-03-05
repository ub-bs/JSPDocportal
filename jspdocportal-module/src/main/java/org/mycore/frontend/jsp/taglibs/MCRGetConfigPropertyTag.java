package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;
import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;

import org.mycore.common.config.MCRConfiguration;

public class MCRGetConfigPropertyTag extends SimpleTagSupport
{
	private static MCRConfiguration config = MCRConfiguration.instance();
	private String var;
	private String defaultValue;
	private String prop;
	

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}


	public void setProp(String prop) {
		this.prop = prop;
	}


	public void setVar(String var) {
		this.var = var;
	}


	public void doTag() throws JspException, IOException {
		PageContext pageContext = (PageContext) getJspContext();
		pageContext.setAttribute(var, config.getString(prop, defaultValue));
		return;
	}	

}