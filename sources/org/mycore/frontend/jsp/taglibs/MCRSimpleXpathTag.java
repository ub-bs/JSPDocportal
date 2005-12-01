package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;

import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;

import org.apache.log4j.Logger;
import org.mycore.common.MCRConfiguration;
import org.mycore.frontend.jsp.format.MCRResultFormatter;


public class MCRSimpleXpathTag extends SimpleTagSupport
{
	private static MCRResultFormatter formatter;
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
	public void initialize() {
		formatter = (MCRResultFormatter) MCRConfiguration.instance().getSingleInstanceOf("MCR.ResultFormatter_class_name","org.mycore.frontend.jsp.format.MCRResultFormatter");
	}	
	public void doTag() throws JspException, IOException {
		if (formatter == null) initialize();
		String value = formatter.getSingleXPathValue(jdom,xpath);
		if (value == null) {
			Logger.getLogger(MCRSimpleXpathTag.class).debug("no xpath value found for xpath-expression ###" + xpath + "### in jdom-Document");
		}
		PageContext pageContext = (PageContext) getJspContext();
        JspWriter out = pageContext.getOut();
        out.println(value);
        
		return;
	}	

}