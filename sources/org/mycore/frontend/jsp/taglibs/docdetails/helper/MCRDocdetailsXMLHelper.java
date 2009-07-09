package org.mycore.frontend.jsp.taglibs.docdetails.helper;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;


public class MCRDocdetailsXMLHelper {
	private static MCRDocdetailsNamespaceContext context = new MCRDocdetailsNamespaceContext(); 
	public static XPath createXPathObject(){
		XPath xpath = XPathFactory.newInstance().newXPath();
		xpath.setNamespaceContext(context);
		return xpath;
	}
	public static String getNamespaceURI(String prefix){
		return context.getNamespaceURI(prefix);
	}
	
}
