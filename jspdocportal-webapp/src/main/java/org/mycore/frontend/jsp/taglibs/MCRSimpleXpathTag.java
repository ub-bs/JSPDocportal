package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;
import org.jdom2.xpath.XPath;


public class MCRSimpleXpathTag extends SimpleTagSupport
{
	private static Logger logger = Logger.getLogger(MCRSimpleXpathTag.class);
	private org.jdom2.Document jdom;
	private String xpath;
	
	public void setJdom(org.jdom2.Document inputDoc) {
		jdom = inputDoc;
		return;
	}
	public void setXpath(String inputXpath) {
		xpath = inputXpath;
		return;
	}

	public void doTag() throws JspException, IOException {
		String value = getSingleXPathValue(jdom,xpath);
		if (value == null) {
			Logger.getLogger(MCRSimpleXpathTag.class).debug("no xpath value found for xpath-expression ###" + xpath + "### in jdom-Document");
		}
		PageContext pageContext = (PageContext) getJspContext();
        JspWriter out = pageContext.getOut();
        out.println(value);
        
		return;
	}	
	
	/**
	 * returns the value of a given jdom-Content and the relative xpath expression
	 * @param jdom a jdom Element
	 * @param xpath xpath-expression, namespaces includable
	 * @return String
	 */
    public String getSingleXPathValue(org.jdom2.Document jdom,String xpath) {
    	try {
    		Object obj = XPath.selectSingleNode( jdom, xpath);
    		if ( obj instanceof org.jdom2.Attribute) 
    			return ((org.jdom2.Attribute) obj).getValue();
    		if ( obj instanceof org.jdom2.Element)
    			return ((org.jdom2.Element) obj).getText();
		} catch (Exception e) {
		   logger.debug("wrong xpath expression: " + xpath);
		}
    	return "";
    }


}