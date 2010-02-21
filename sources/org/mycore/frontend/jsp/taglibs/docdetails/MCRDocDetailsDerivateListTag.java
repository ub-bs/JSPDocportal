package org.mycore.frontend.jsp.taglibs.docdetails;

import java.io.IOException;
import java.net.URLEncoder;

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

public class MCRDocDetailsDerivateListTag extends SimpleTagSupport {
	private static MCRAccessInterface AI = MCRAccessManager.getAccessImpl();
	private String xp;
	private boolean showsize=false;

	
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
	   		  	out.write("<td class=\""+docdetails.getStylePrimaryName()+"-value\">");	
	    		
	    		
	    		for(int i=0;i<nodes.getLength();i++){
	    		 	out.write("<dl class=\""+docdetails.getStylePrimaryName()+"-derivate-list\">");
		    		
		    		
	    			Node n = (Node)nodes.item(i);
	    			 
	   		        //<img src="<x:out select="concat($WebApplicationBaseURL,'file/',./@derivid,'/',./@name,'?hosts=',$host)" />" 
	   	     		//	border="0"  width="150" />      		

	    		Element eN = (Element)n;
	    		String derID = eN.getAttributeNS(docdetails.getNamespaceContext().getNamespaceURI("xlink"), "href");
	    		String title = eN.getAttributeNS(docdetails.getNamespaceContext().getNamespaceURI("xlink"), "label");
	    		if(title.contains("Cover")){
	    			//do nothing - handled elsewhere
	    		}
	    		else if(title.startsWith("METS")){
	    			//show mets
	    			String derivmain ="";
    			    MCRDirectory root = MCRDirectory.getRootDirectory(derID);
    			    MCRFilesystemNode[] myfiles = root.getChildren(MCRDirectory.SORT_BY_NAME);//getChildren();
    			    if(myfiles.length>0){
    			    	derivmain=myfiles[0].getName();
    			    }
    			    String baseurl = getJspContext().getAttribute("WebApplicationBaseURL", PageContext.APPLICATION_SCOPE).toString();
				    String metsurl = baseurl +"file/"+derID+"/"+derivmain;
				    out.write("<a href=\"http://dfg-viewer.de/v1/?set%5Bmets%5D="+URLEncoder.encode(metsurl,"UTF-8")+"&set%5Bzoom%5D=min\">");
				    out.write("<img src=\""+baseurl+"images/dfgviewer.gif\" title = \"Dokument anzeigen\" alt=\"Dokument anzeigen\" />");
				    out.write(docdetails.getMessages().getString("Webpage.docdetails.showInDFGViewer")+"</a>");	    			
	    		}
	    		else{
	    			out.write("<dt>"+title+"</dt>");
	    			StringBuffer sbUrl = new StringBuffer(o.toString());
	    			sbUrl.append("file/");
	    			sbUrl.append(derID);
	    			sbUrl.append("/");
	    		
	    			MCRDirectory root = MCRDirectory.getRootDirectory(derID);
	    			MCRFilesystemNode[] myfiles = root.getChildren(MCRDirectory.SORT_BY_NAME);
	    			boolean accessAllowed = AI.checkPermission(derID, "read");	   		    
	    			for ( int j=0; j< myfiles.length; j++) {
	    				MCRFile theFile = (MCRFile) myfiles[j];
	    				out.write("<dd>");
	    				if(accessAllowed){
	    					String fURL = sbUrl.toString()+theFile.getName();
	    					out.write("<a href=\""+fURL+"\" target=\"_blank\">");
	    					String imgURL = o.toString()+"images/derivate_unknown.gif";
	    					if(theFile.getName().toLowerCase().endsWith(".pdf")){
	    						imgURL = o.toString()+"images/derivate_pdf.gif";
	    					}
	    					if(theFile.getName().toLowerCase().endsWith(".jpg")||
	    							theFile.getName().toLowerCase().endsWith(".jpeg")){
	    						imgURL = o.toString()+"images/derivate_portrait.gif";
	    					}
	    					if(theFile.getName().toLowerCase().endsWith(".doc")||
	    							theFile.getName().toLowerCase().endsWith(".txt")){
	    						imgURL = o.toString()+"images/derivate_doc.gif";
	    					}
	    					out.write("<img src=\""+imgURL+"\" />");
	    					out.write(theFile.getName());
	    					out.write("</a>");
	    					if(showsize){out.write("&nbsp;("+theFile.getSizeFormatted()+")");}
	    				}
	    				else{
	    					out.write(theFile.getName());
	    					if(showsize){out.write("&nbsp;("+theFile.getSizeFormatted()+")<br />");}
	    					out.write("&nbsp;---&nbsp;"+docdetails.getMessages().getString("OMD.fileaccess.denied"));
	    				}
	    				out.write("</dd>");
	    			}
	    		}
	    	}
	    	out.write("</td>");    	
	    }
	    		//error
	   }catch(Exception e){
		throw new JspException("Error executing docdetails:derivatelist tag", e);
	   }
	}

	public void setXpath(String xpath) {
		this.xp = xpath;
	}

	public void setShowsize(boolean showsize) {
		this.showsize = showsize;
	}
}
