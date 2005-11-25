package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;

import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;

import org.apache.log4j.Logger;
import org.mycore.frontend.jsp.query.MCRResultFormatter;


public class MCRSimpleXpathTag extends SimpleTagSupport
{
	private org.jdom.Document jdom;
	private String xpath;
	
	public void setJdom(org.jdom.Document inputDoc) {
		jdom = inputDoc;
		return;
	}
	public void setXpath(String inputXpath) {
		xpath = inputXpath;
		return;
	}
	public void doTag() throws JspException, IOException {
		String value = MCRResultFormatter.getSingleXPathValue(jdom,xpath);
		if (value == null) {
			Logger.getLogger(MCRSimpleXpathTag.class).debug("no xpath value found for xpath-expression ###" + xpath + "### in jdom-Document");
		}
		PageContext pageContext = (PageContext) getJspContext();
        JspWriter out = pageContext.getOut();
        out.println(value);
        
		return;
	}	

}