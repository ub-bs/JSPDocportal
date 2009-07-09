package org.mycore.frontend.jsp.taglibs.docdetails;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import org.apache.log4j.Logger;
import org.mycore.frontend.jsp.taglibs.docdetails.helper.MCRDocdetailsXMLHelper;
import org.w3c.dom.NodeList;

public class MCRDocDetailsItemTag extends SimpleTagSupport {
	private static Logger LOGGER=Logger.getLogger(MCRDocDetailsItemTag.class);
	private String xp;
	private String messagekey="";

	public void setXpath(String xpath) {
		this.xp = xpath;
	}

	public void setMessagekey(String messagekey) {
		this.messagekey = messagekey;
	}

	public void doTag() throws JspException, IOException{
		MCRDocDetailsRowTag docdetailsRow = (MCRDocDetailsRowTag) findAncestorWithClass(this, MCRDocDetailsRowTag.class);
		if(docdetailsRow==null){
			throw new JspException("This tag must be nested in tag called 'row' of the same tag library");
		}
		MCRDocDetailsTag docdetails = (MCRDocDetailsTag) findAncestorWithClass(this, MCRDocDetailsTag.class);
		String result ="";
		try {
				XPath xpath = MCRDocdetailsXMLHelper.createXPathObject();
				xpath.compile(xp);
				
	    		NodeList nodes = (NodeList)xpath.evaluate(xp, docdetailsRow.getXML(), XPathConstants.NODESET);
	    		if(nodes.getLength()>0){
	    			result = nodes.item(0).getTextContent();
	    			if(messagekey!=null && !"".equals(messagekey)){
	    				String key = messagekey+result;
	    				result = docdetails.getMessages().getString(key);
	    			}
	    		}
	    	} catch (Exception e) {
			   LOGGER.debug("wrong xpath expression: " + xp);
			}
	    	getJspContext().getOut().print("<td class=\""+docdetails.getStylePrimaryName()+"-value\">"+result+"</td>");	
		}
	}
	

	/*
		<tr>
			<td class="metaname">akademische Selbstverwaltung:</td>
			<td class="metavalue">
				<table border="0" cellpadding="0" cellspacing="4">
				<colgroup>
					<col width="80">
				</colgroup>
				<tbody>
				<tr>
					<td style="text-align: left;" valign="top">1975-79</td>
					<td style="text-align: left;" valign="top">Mitglied im
					Senat</td>
				</tr>
				<tr>
					<td style="text-align: left;" valign="top">1981-84</td>
					<td style="text-align: left;" valign="top">Dekan</td>
				</tr>
 				</tbody></table>
			</td>
	</tr>
	*/

