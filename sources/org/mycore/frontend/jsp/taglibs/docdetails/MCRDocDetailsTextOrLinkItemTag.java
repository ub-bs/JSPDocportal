package org.mycore.frontend.jsp.taglibs.docdetails;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MCRDocDetailsTextOrLinkItemTag extends SimpleTagSupport {
	private static Logger LOGGER=Logger.getLogger(MCRDocDetailsTextOrLinkItemTag.class);
	private String xp;
	private String css=null;

	public void setXpath(String xpath) {
		this.xp = xpath;
	}

	public void setStyleName(String style){
		this.css=style;
	}

	public void doTag() throws JspException, IOException {
		MCRDocDetailsRowTag docdetailsRow = (MCRDocDetailsRowTag) findAncestorWithClass(this, MCRDocDetailsRowTag.class);
		if(docdetailsRow==null){
			throw new JspException("This tag must be nested in tag called 'row' of the same tag library");
		}
		MCRDocDetailsTag docdetails = (MCRDocDetailsTag) findAncestorWithClass(this, MCRDocDetailsTag.class);
		StringBuffer result = new StringBuffer();
		try {
			XPath xpath = XPathFactory.newInstance().newXPath();
			xpath.setNamespaceContext(docdetails.getNamespaceContext());
			xpath.compile(xp);
				
	    	NodeList nodes = (NodeList)xpath.evaluate(xp, docdetailsRow.getContext(), XPathConstants.NODESET);
	    	if(nodes.getLength()>0){
	    		Node n = nodes.item(0);
	    		if(n instanceof Element){
	    			Element e = (Element) n;
	    			if(e.hasAttribute("xlink:href") && e.hasAttribute("xlink:title")){
	    				String href = e.getAttribute("xlink:href");
		    			String title = e.getAttribute("xlink:title");
		    			if(href.length()==0 || href.equals("#")){
		    				result.append(title);
		    			}
		    			else{
		    				String baseurl = getJspContext().getAttribute("WebApplicationBaseURL", PageContext.APPLICATION_SCOPE).toString();
		    				result.append("<a href=\""+baseurl+"resolve?id="+href+"\">"+title+"</a>");
		    			}
	    			}
	    			else{
	    				result.append(e.getTextContent());
		    		}
    			}		
    		}
	    	if(result.length()>0){
	    		if(css!=null && !"".equals(css)){
	    			getJspContext().getOut().print("<td class=\""+css+"\">");
	    		}
	    		else{
	    			getJspContext().getOut().print("<td class=\""+docdetails.getStylePrimaryName()+"-value\">");
	    		}
	    		getJspContext().getOut().print(result.toString());		
	    		getJspContext().getOut().print("</td>");
	    	}
	    } catch (Exception e) {
		   LOGGER.debug("wrong xpath expression: " + xp);
		}
	}
}