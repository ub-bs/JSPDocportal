package org.mycore.frontend.jsp.taglibs.docdetails;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.mycore.access.MCRAccessInterface;
import org.mycore.access.MCRAccessManager;
import org.mycore.datamodel.ifs.MCRDirectory;
import org.mycore.datamodel.ifs.MCRFile;
import org.mycore.datamodel.ifs.MCRFilesystemNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MCRDocDetailsIncludeDerivateTag extends SimpleTagSupport {
	private static MCRAccessInterface AI = MCRAccessManager.getAccessImpl();
	private String xp;
	private String width="500px";
	private String encoding="UTF-8";

	
	public void doTag() throws JspException, IOException {
		MCRDocDetailsTag docdetails = (MCRDocDetailsTag) findAncestorWithClass(this, MCRDocDetailsTag.class);
		if(docdetails==null){
			throw new JspException("This tag must be nested in tag called 'docdetails' of the same tag library");
		}
		MCRDocDetailsRowTag docdetailRow= (MCRDocDetailsRowTag) findAncestorWithClass(this, MCRDocDetailsRowTag.class);
		if(docdetailRow==null){
			throw new JspException("This tag must be nested in tag called 'row' of the same tag library");
		}
		try {
			XPath xpath = XPathFactory.newInstance().newXPath();
			xpath.setNamespaceContext(docdetails.getNamespaceContext());
			xpath.compile(xp);

			NodeList nodes = (NodeList)xpath.evaluate(xp, docdetailRow.getContext(), XPathConstants.NODESET);
	    	JspWriter out = getJspContext().getOut();
	    	if(nodes.getLength()>0){
	   		  	Object o =  getJspContext().getAttribute("WebApplicationBaseURL", PageContext.APPLICATION_SCOPE);
	   		  	if(o==null){
	   		  		o = new String("");
	   		  	}
	   		  		
	    		
	    		Node n = (Node)nodes.item(0);
	    		Element eN = (Element)n;
	    		String derID = eN.getAttributeNS(docdetails.getNamespaceContext().getNamespaceURI("xlink"), "href");
	    		String title = eN.getAttributeNS(docdetails.getNamespaceContext().getNamespaceURI("xlink"), "label");
	    		

	    		out.write("<td class=\""+docdetails.getStylePrimaryName()+"-value\">");
	    		
	    		
	    		StringBuffer sbUrl = new StringBuffer(o.toString());
	    		sbUrl.append("file/");
	    		sbUrl.append(derID);
	    		sbUrl.append("/");
	    		
	    		MCRDirectory root = MCRDirectory.getRootDirectory(derID);
	   		    MCRFilesystemNode[] myfiles = root.getChildren();
	   			boolean accessAllowed = AI.checkPermission(derID, "read");	   		    
	   		    for ( int j=0; j< myfiles.length; j++) {
	   		    	MCRFile theFile = (MCRFile) myfiles[j];
	   		    	
	   		    	if(accessAllowed){
	   		    		String fURL = sbUrl.toString()+theFile.getName();
	   		    		
	   		    	
	   		    
	   		    	String contentType = theFile.getContentTypeID();
	   		    	
	   		    	if(contentType.contains("html") || contentType.contains("xml")) {
	   		    		out.write("<font size=\"+1\" face=\"times\">");
						String content = theFile.getContentAsString(encoding);
						out.write(content);
						out.write("</font>");
	   		    	}
	   		    	if(contentType.contains("jpeg")){
	   					out.write("<a href=\""+fURL+"\" target=\"_blank\" title=\""+docdetails.getMessages().getString("OMD.showLargerImage")
	   							+"\"  alt=\""+docdetails.getMessages().getString("OMD.showLargerImage")+"\">");
	   					out.write("<img src=\""+fURL+"\" width=\""+width+"\" alt=\""+title+"\" /></a>");
	   					
	   		    	}   		    	
	   		    	
	   		    }
	    	}
	   		 out.write("</td>");   
	    		
	    	 	
	    
	    	}		//error
	   }catch(Exception e){
		throw new JspException("Error executing docdetails:derivatelist tag", e);
	   }
	}

	public void setXpath(String xpath) {
		this.xp = xpath;
	}


	public void setWidth(String width) {
		this.width = width;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
}
