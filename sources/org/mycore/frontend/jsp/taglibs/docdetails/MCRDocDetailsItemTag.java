package org.mycore.frontend.jsp.taglibs.docdetails;

import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.NodeList;

public class MCRDocDetailsItemTag extends SimpleTagSupport {
	private static Logger LOGGER=Logger.getLogger(MCRDocDetailsItemTag.class);
	private String xp;
	private String messagekey="";
	private String datepattern="";
	private String css=null;

	public void setXpath(String xpath) {
		this.xp = xpath;
	}

	public void setMessagekey(String messagekey) {
		this.messagekey = messagekey;
	}
	
	public void setStyleName(String style){
		this.css=style;
	}
	
	public void setDatePattern(String pattern){
		this.datepattern=pattern;
	}

	public void doTag() throws JspException, IOException{
		MCRDocDetailsRowTag docdetailsRow = (MCRDocDetailsRowTag) findAncestorWithClass(this, MCRDocDetailsRowTag.class);
		if(docdetailsRow==null){
			throw new JspException("This tag must be nested in tag called 'row' of the same tag library");
		}
		MCRDocDetailsTag docdetails = (MCRDocDetailsTag) findAncestorWithClass(this, MCRDocDetailsTag.class);
		String result ="";
		try {
			XPath xpath = XPathFactory.newInstance().newXPath();
			xpath.setNamespaceContext(docdetails.getNamespaceContext());
			xpath.compile(xp);
				
	    		NodeList nodes = (NodeList)xpath.evaluate(xp, docdetailsRow.getContext(), XPathConstants.NODESET);
	    		if(nodes.getLength()>0){
	    			result = nodes.item(0).getTextContent();
	    			if(!"".equals(messagekey)){
	    				String key = messagekey+result;
	    				result = docdetails.getMessages().getString(key);
	    			}
	    			if(!"".equals(datepattern)){
	    				try{
	    					SimpleDateFormat indf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	    					SimpleDateFormat outdf = new SimpleDateFormat(datepattern);
	    					result = outdf.format(indf.parse(result));
	    				}
	    				catch(Exception e){
	    					try{
		    					SimpleDateFormat indf = new SimpleDateFormat("yyyy-MM-dd");
		    					SimpleDateFormat outdf = new SimpleDateFormat(datepattern);
		    					result = outdf.format(indf.parse(result));
		    				}
		    				catch(Exception e2){
		    					try{
			    					SimpleDateFormat indf = new SimpleDateFormat("yyyy");
			    					SimpleDateFormat outdf = new SimpleDateFormat(datepattern);
			    					result = outdf.format(indf.parse(result));
			    				}
			    				catch(Exception e3){
			    					result = nodes.item(0).getTextContent();
			    				}	    	
		    				}	    				
	    				}	    				
	    			}
	    			
	    		}
	    	} catch (Exception e) {
			   LOGGER.debug("wrong xpath expression: " + xp);
			}
	    	if(result.equals("#")){
	    		result = "";
	    	}
	    	String td = null;
	    	if(css!=null && !"".equals(css)){
	    		td = "<td class=\""+css+"\">";
	    	}
	    	else{
	    		td = "<td class=\""+docdetails.getStylePrimaryName()+"-value\">";
	    	}
	    	getJspContext().getOut().print(td+result+"</td>");	
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

