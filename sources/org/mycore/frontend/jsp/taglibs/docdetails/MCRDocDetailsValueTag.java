package org.mycore.frontend.jsp.taglibs.docdetails;

import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import org.apache.log4j.Logger;
import org.mycore.frontend.jsp.taglibs.docdetails.helper.MCRDocdetailsXMLHelper;
import org.w3c.dom.NodeList;

public class MCRDocDetailsValueTag extends SimpleTagSupport {
	private static Logger LOGGER=Logger.getLogger(MCRDocDetailsValueTag.class);
	private String xpath;
	private String messagekey="";
	private String dateformat="";


	public void setXpath(String xpath) {
		this.xpath = xpath;
	}

	public void setMessageKeyPrefix(String s){
		messagekey = s;
	}
	
	public void setDatePattern(String s){
		dateformat = s;
	}
	public void doTag() throws JspException, IOException {
		MCRDocDetailsTag docdetails = (MCRDocDetailsTag) findAncestorWithClass(this, MCRDocDetailsTag.class);
		if(docdetails==null){
			throw new JspException("This tag must be nested in tag called 'docdetails' of the same tag library");
		}
		MCRDocDetailsOutputItemTag item = (MCRDocDetailsOutputItemTag) findAncestorWithClass(this, MCRDocDetailsOutputItemTag.class);
		String xp = xpath;
		
		String result = "";
		try {
			XPath xpath = MCRDocdetailsXMLHelper.createXPathObject();
			xpath.compile(xp);
			
    		NodeList nodes;
    		if(item!=null){
    			nodes = (NodeList)xpath.evaluate(xp, item.getXmlnode(), XPathConstants.NODESET);
    		}
    		else{
    			nodes = (NodeList)xpath.evaluate(xp, docdetails.getXMLDocument(), XPathConstants.NODESET);    			
    		}
    		if(nodes.getLength()>0){
    			result = nodes.item(0).getTextContent();
    		}
    		if(!messagekey.equals("")){
    			String key = messagekey+result;
    			result = docdetails.getMessages().getString(key);
    		}
    		if(!dateformat.equals("")){
    			SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
    			                                            //2009-06-17T15:08:21.628Z
    			SimpleDateFormat sdfIso=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    			result = sdf.format(sdfIso.parse(result));
    		}
    	} catch (Exception e) {
		   LOGGER.error("wrong xpath expression: " + xpath, e);
		}
    	getJspContext().getOut().print(result);	
	}
}
