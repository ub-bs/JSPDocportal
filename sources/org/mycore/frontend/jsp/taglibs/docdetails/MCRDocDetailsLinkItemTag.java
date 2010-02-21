package org.mycore.frontend.jsp.taglibs.docdetails;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MCRDocDetailsLinkItemTag extends SimpleTagSupport {
	private static Logger LOGGER = Logger.getLogger(MCRDocDetailsLinkItemTag.class.getName());

	private String xp;

	
	public void doTag() throws JspException, IOException {
		MCRDocDetailsRowTag docdetailsRow = (MCRDocDetailsRowTag) findAncestorWithClass(this, MCRDocDetailsRowTag.class);
		if(docdetailsRow==null){
			throw new JspException("This tag must be nested in tag called 'row' of the same tag library");
		}
		MCRDocDetailsTag docdetails = (MCRDocDetailsTag) findAncestorWithClass(this, MCRDocDetailsTag.class);
		Element result = null;
		try {
			XPath xpath = XPathFactory.newInstance().newXPath();
			xpath.setNamespaceContext(docdetails.getNamespaceContext());
			xpath.compile(xp);
				JspWriter out = getJspContext().getOut();
	    		NodeList nodes = (NodeList)xpath.evaluate(xp, docdetailsRow.getContext(), XPathConstants.NODESET);
	    		if(nodes.getLength()>0){
	    			Node n = nodes.item(0);
	    			if(n instanceof Element){
	    				result = (Element)n;
	    				if(result.hasAttribute("xlink:href") && result.hasAttribute("xlink:title")){
	    					String href = result.getAttribute("xlink:href");
	    					String title = result.getAttribute("xlink:title");
	    					if(href.length()==0 || href.equals("#")){
	    						out.write("<td class=\""+docdetails.getStylePrimaryName()+"-value\">"+title+"</td>");
	    					}
	    					else{
	    						out.write("<td class=\""+docdetails.getStylePrimaryName()+"-value\"><a href=\""+href+"\">"+title+"</a></td>");
	    					}
	    					
	    					return;
	    				}
	    			}
	    		}
	    		//error
	    		throw new JspException("The XPath expression must match a classification element");
		}catch(Exception e){
			LOGGER.error("Error processing docdetails:classificationitem tag", e);
		}
	}

	public void setXpath(String xpath) {
		this.xp = xpath;
	}
}
