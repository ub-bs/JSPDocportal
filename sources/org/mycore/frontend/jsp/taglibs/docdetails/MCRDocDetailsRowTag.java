package org.mycore.frontend.jsp.taglibs.docdetails;

import java.io.IOException;
import java.util.MissingResourceException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;

import org.mycore.frontend.jsp.taglibs.docdetails.helper.MCRDocdetailsXMLHelper;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MCRDocDetailsRowTag extends SimpleTagSupport
{
	private static final long serialVersionUID = -2264968071280327072L;
	private boolean uselang = false;
	private boolean showinfo = false;
	private String xpath;
	private String labelkey;
	private String colWidths;
	private String separator="</td><td>";

	private Node xml;
		
	protected String getSeparator(){
		return separator;
	}

	public void setUselanguage(boolean b) {
		uselang = b;
	}
	public void setXpath(String s){
		xpath = s;
	}
	public void setLabelkey(String s){
		labelkey = s;
	}
	public void setColWidths(String s){
		colWidths=s;
	}
	public void setSeparator(String s){
		separator = s;
	}
	public void setShowInfo(boolean b){
		showinfo=b;
	}
		
	protected Node getXML(){
		return xml;
	}
	
	private MCRDocDetailsTag docdetails;
	
	public void doTag() throws JspException, IOException {
		docdetails = (MCRDocDetailsTag) findAncestorWithClass(this,	MCRDocDetailsTag.class);
		if (docdetails == null) {
			throw new JspException(
					"This tag must be nested in tag called 'docdetails' of the same tag library");
		}
		String xp = xpath;

		if (uselang) {
			xp = xpath + "[@xml:lang='" + docdetails.getLang() + "']";
		}
		JspWriter out = getJspContext().getOut();
		try {
			XPath xpath = MCRDocdetailsXMLHelper.createXPathObject();
			NodeList l = (NodeList) xpath.evaluate(xp, docdetails
					.getXMLDocument(), XPathConstants.NODESET);

			if (l.getLength() > 0) {
				docdetails.setPreviousOutput(docdetails.getPreviousOutput() + 1);
				out.write("<tr>");
				
				out.write("   <td class=\""+docdetails.getStylePrimaryName()+"-label\">");
				if(showinfo){
					String info = "";
					try {
						info = docdetails.getMessages().getString(labelkey+".info");
					} catch (MissingResourceException e) {
						info = "???" + labelkey + ".info???<br /><i>Eine Beschreibung für das Feld wird gerade erstellt.</i>";
					}
					out.write("<div class=\""+docdetails.getStylePrimaryName()+"-infohover\">"
			    	   +"<a href=\"#\">i<span>"+info
			    	   +"</span></a></div>"); 						
				}
				out.write("</td>\n");
				
				String label = "";
				if(labelkey.length()>0){
					try {
						label = docdetails.getMessages().getString(labelkey);
					} catch (MissingResourceException e) {
						label = "???" + labelkey + "???";
					}
				}
				out.write("   <td class=\""+docdetails.getStylePrimaryName()+"-label\">" + label + ":</td>\n");
				out.write("   <td class=\""+docdetails.getStylePrimaryName()+"-values\">\n");
				out
						.write("   		<table class=\""+docdetails.getStylePrimaryName()+"-values-table\">\n");
				if (colWidths != null && !colWidths.equals("")) {
					String[] ss = colWidths.split("\\s");
					out.write("   	   	<colgroup>");
					for (String s : ss) {
						out.write(" <col width=\"" + s + "\">");
					}
					out.write("   	   	</colgroup>\n");
				}
				out.write("   	   	<tbody>\n");
				for (int i = 0; i < l.getLength(); i++) {
					xml = l.item(i);
					out.write("<tr>");
					getJspBody().invoke(out);
					out.write("</tr>");
				}

				out.write("   	   	</tbody></table>\n");
				out.write("</td></tr>");
			}
		} catch (XPathException e) {
		}
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
					<td style="text-align: left;" valign="top">Mitglied im	Senat</td>
				</tr>
				<tr>
					<td style="text-align: left;" valign="top">1981-84</td>
					<td style="text-align: left;" valign="top">Dekan</td>
				</tr>
 				</tbody></table>
			</td>
	</tr>
	*/

