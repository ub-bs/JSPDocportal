package org.mycore.frontend.jsp.taglibs.docdetails;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import org.mycore.frontend.jsp.taglibs.docdetails.helper.MCRDocdetailsXMLHelper;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MCRDocDetailsOutputItemTag extends SimpleTagSupport {
	private String xp;
	private String varxml;
	private String varxmldoc;
	private Node xmlnode;
	
	public Node getXmlnode() {
		return xmlnode;
	}

	public void doTag() throws JspException, IOException {
			
		MCRDocDetailsRowTag docdetailsRow = (MCRDocDetailsRowTag) findAncestorWithClass(this, MCRDocDetailsRowTag.class);
		if(docdetailsRow==null){
			throw new JspException("This tag must be nested in tag called 'row' of the same tag library");
		}
		MCRDocDetailsTag docdetails= (MCRDocDetailsTag) findAncestorWithClass(this, MCRDocDetailsTag.class);
		try {
				XPath xpath = MCRDocdetailsXMLHelper.createXPathObject();
				xpath.compile(xp);

	    		NodeList nodes = (NodeList)xpath.evaluate(xp, docdetailsRow.getXML(), XPathConstants.NODESET);
	    		JspWriter out = getJspContext().getOut();
	    		if(nodes.getLength()>0){
	    			Node n = nodes.item(0);
	    			xmlnode=n;
	    			getJspContext().setAttribute(varxml, n);
	    			if(varxmldoc!=null){
	    				getJspContext().setAttribute(varxmldoc, n.getOwnerDocument());
	    			}
	    			out.write("<td class=\""+docdetails.getStylePrimaryName()+"-value\">");
	    			getJspBody().invoke(out);
	    			out.write("</td>");	    					
	    		}
	    		//error
	    }catch(Exception e){
			throw new JspException("Error executing docdetails:outputitem tag", e);
		}
	}

	public void setXpath(String xpath) {
		this.xp = xpath;
	}

	public String getVarxml() {
		return varxml;
	}

	public void setVarxml(String varxml) {
		this.varxml = varxml;
	}

	public void setVarxmldoc(String varxmldoc) {
		this.varxmldoc = varxmldoc;
	}
}
