package org.mycore.frontend.jsp.taglibs.docdetails;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.mycore.datamodel.ifs.MCRDirectory;
import org.mycore.datamodel.ifs.MCRFile;
import org.mycore.datamodel.ifs.MCRFilesystemNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MCRDocDetailsPreviewTag extends SimpleTagSupport {
	
	private int imgWidth=0;
	private String labelSubstring="";

	public void doTag() throws JspException, IOException {
		MCRDocDetailsTag docdetails= (MCRDocDetailsTag) findAncestorWithClass(this, MCRDocDetailsTag.class);
		if(docdetails==null){
			throw new JspException("This tag must be nested in tag called 'docdetails' of the same tag library");
		}
		try {
			XPath xpath = XPathFactory.newInstance().newXPath();
			xpath.setNamespaceContext(docdetails.getNamespaceContext());
			String xp = "/mycoreobject/structure/derobjects/derobject[contains(@xlink:label, '"+labelSubstring+"')]";
			xpath.compile(xp);
   		  	Object o =  getJspContext().getAttribute("WebApplicationBaseURL", PageContext.APPLICATION_SCOPE);
   		  	if(o==null){
   		  		o = new String("");
   		  	}
   		  	
			NodeList nodes = (NodeList)xpath.evaluate(xp, docdetails.getContext(), XPathConstants.NODESET);
	    	JspWriter out = getJspContext().getOut();
	    	out.write("<tr><td colspan=\"3\">");
    		out.write("<td rowspan=\"1000\" class=\""+docdetails.getStylePrimaryName()+"-preview\">");
    	   	if(nodes.getLength()>0){
	    		for(int i=0;i<nodes.getLength();i++){
	    			Node n = (Node)nodes.item(i);
	    			 
	   		        //<img src="<x:out select="concat($WebApplicationBaseURL,'file/',./@derivid,'/',./@name,'?hosts=',$host)" />" 
	   	     		//	border="0"  width="150" />      		
	    			StringBuffer sbUrl = new StringBuffer(o.toString());
	    			sbUrl.append("file/");
	    			Element eN = (Element)n;
	    			String derID = eN.getAttributeNS(docdetails.getNamespaceContext().getNamespaceURI("xlink"), "href");
	    			sbUrl.append(derID);
	    			sbUrl.append("/");
	    		
	    			MCRDirectory root = MCRDirectory.getRootDirectory(derID);
	    			MCRFilesystemNode[] myfiles = root.getChildren();
	    			for ( int j=0; j< myfiles.length; j++) {
	    				MCRFile theFile = (MCRFile) myfiles[j];
	    				if ( theFile.getContentTypeID().indexOf("jpeg")>= 0 ||
	    						theFile.getContentTypeID().indexOf("gif")>= 0 ||
	    						theFile.getContentTypeID().indexOf("png")>= 0) {
	    					String url = sbUrl.toString()+myfiles[j].getName();
	    					out.write("<img src=\""+url+"\" border=\"0\" width=\""+getImageWidth()+"\" alt=\""+myfiles[j].getName()+"\" />");  
	    					out.write("<br />");
	    				}
	    			}
	   		   				    					
	    		}
    	   	}
	   	else{
	   		String url = o.toString()+"images/emtyDot1Pix.gif";
			out.write("<img src=\""+url+"\" border=\"0\" width=\""+getImageWidth()+"\" />");  
		    
	   	}
	    out.write("</td></tr>");    	
	    		//error
	}catch(Exception e){
		throw new JspException("Error executing docdetails:outputitem tag", e);
	}
	}

	public void setLabelContains(String substring) {
		this.labelSubstring = substring;
	}

	
	public int getImageWidth() {
		return imgWidth;
	}

	public void setImageWidth(int imgWidth) {
		this.imgWidth = imgWidth;
	}
}
