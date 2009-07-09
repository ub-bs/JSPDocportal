package org.mycore.frontend.jsp.taglibs.docdetails;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import org.apache.log4j.Logger;
import org.mycore.datamodel.classifications2.MCRCategoryDAO;
import org.mycore.datamodel.classifications2.MCRCategoryDAOFactory;
import org.mycore.datamodel.classifications2.MCRCategoryID;
import org.mycore.frontend.jsp.taglibs.docdetails.helper.MCRDocdetailsXMLHelper;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MCRDocDetailsClassificationItemTag extends SimpleTagSupport {
	private static MCRCategoryDAO categoryDAO = MCRCategoryDAOFactory.getInstance();
	private static Logger LOGGER = Logger.getLogger(MCRDocDetailsClassificationItemTag.class.getName());

	private String xp;

	
	public void doTag() throws JspException, IOException {
		MCRDocDetailsRowTag docdetailsRow = (MCRDocDetailsRowTag) findAncestorWithClass(this, MCRDocDetailsRowTag.class);
		if(docdetailsRow==null){
			throw new JspException("This tag must be nested in tag called 'row' of the same tag library");
		}
		MCRDocDetailsTag docdetails = (MCRDocDetailsTag) findAncestorWithClass(this, MCRDocDetailsTag.class);
		Element result = null;
		try {
				XPath xpath = MCRDocdetailsXMLHelper.createXPathObject();
				xpath.compile(xp);
				
	    		NodeList nodes = (NodeList)xpath.evaluate(xp, docdetailsRow.getXML(), XPathConstants.NODESET);
	    		if(nodes.getLength()>0){
	    			Node n = nodes.item(0);
	    			if(n instanceof Element){
	    				result = (Element)n;
	    				if(result.hasAttribute("classid") && result.hasAttribute("categid")){
	    					String lang = docdetails.getLang();
	    					String classid = result.getAttribute("classid");
	    					String categid = result.getAttribute("categid");
	    					String text="";
	    					try{
	    						text = categoryDAO.getCategory(new MCRCategoryID(classid, categid), 0).getLabel(lang).getText();
	    					}
	    					catch(NullPointerException npe){
	    						text = "No classification entry found for id: "+categid+" in classfication: "+classid+"!";
	    					}
	    					getJspContext().getOut().write("<td class=\""+docdetails.getStylePrimaryName()+"-value\">"+text+"</td>");
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
