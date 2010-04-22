/*
 * $RCSfile$
 * $Revision: 16360 $ $Date: 2010-01-06 00:54:02 +0100 (Mi, 06 Jan 2010) $
 *
 * This file is part of ***  M y C o R e  ***
 * See http://www.mycore.de/ for details.
 *
 * This program is free software; you can use it, redistribute it
 * and / or modify it under the terms of the GNU General Public License
 * (GPL) as published by the Free Software Foundation; either version 2
 * of the License or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program, in a file called gpl.txt or license.txt.
 * If not, write to the Free Software Foundation Inc.,
 * 59 Temple Place - Suite 330, Boston, MA  02111-1307 USA
 */
package org.mycore.frontend.jsp.taglibs.docdetails;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.taglibs.standard.tag.common.xml.XPathUtil;
import org.mycore.access.MCRAccessInterface;
import org.mycore.access.MCRAccessManager;
import org.mycore.common.MCRConstants;
import org.mycore.datamodel.ifs.MCRDirectory;
import org.mycore.datamodel.ifs.MCRFile;
import org.mycore.datamodel.ifs.MCRFilesystemNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * display the list of attached derivates
 * @author Robert Stephan
 *
 */
public class MCRDocDetailsDerivateListTag extends SimpleTagSupport {
	private static MCRAccessInterface AI = MCRAccessManager.getAccessImpl();
	private String xp;
	private boolean showsize=false;

	
	public void doTag() throws JspException, IOException {
		MCRDocDetailsTag docdetails = (MCRDocDetailsTag) findAncestorWithClass(this, MCRDocDetailsTag.class);
		if(docdetails==null){
			throw new JspException("This tag must be nested in tag called 'docdetails' of the same tag library");
		}
		MCRDocDetailsRowTag docdetailsRow= (MCRDocDetailsRowTag) findAncestorWithClass(this, MCRDocDetailsRowTag.class);
		if(docdetailsRow==null){
			throw new JspException("This tag must be nested in tag called 'row' of the same tag library");
		}
		try {
			JspWriter out = getJspContext().getOut();
			
			XPathUtil xu = new XPathUtil((PageContext)getJspContext());
			@SuppressWarnings("unchecked")
			List nodes = xu.selectNodes(docdetailsRow.getContext(), xp);
			if(nodes.size()>0){
	   		  	Object o =  getJspContext().getAttribute("WebApplicationBaseURL", PageContext.APPLICATION_SCOPE);
	   		  	if(o==null){
	   		  		o = new String("");
	   		  	}
	   		  	out.write("<td class=\""+docdetails.getStylePrimaryName()+"-value\">");	
	    		
	    		
	    		for(int i=0;i<nodes.size();i++){
	    		 	out.write("<dl class=\""+docdetails.getStylePrimaryName()+"-derivate-list\">");
		    		
		    		
	    			Node n = (Node)nodes.get(i);
	    			 
	   		        //<img src="<x:out select="concat($WebApplicationBaseURL,'file/',./@derivid,'/',./@name,'?hosts=',$host)" />" 
	   	     		//	border="0"  width="150" />      		

	    		Element eN = (Element)n;
	    		String derID = eN.getAttributeNS(MCRConstants.XLINK_NAMESPACE.getURI(), "href");
	    		String title = eN.getAttributeNS(MCRConstants.XLINK_NAMESPACE.getURI(), "label");
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
	    			if(root!=null){
	    			MCRFilesystemNode[] myfiles = root.getChildren(MCRDirectory.SORT_BY_NAME);
	    			boolean accessAllowed = AI.checkPermission(derID, "read");	   		    
	    			for ( int j=0; j< myfiles.length; j++) {
	    				MCRFilesystemNode theFile = (MCRFilesystemNode) myfiles[j];
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
	    	}
	    	out.write("</td>");    	
	    }
	    		//error
	   }catch(Exception e){
		throw new JspException("Error executing docdetails:derivatelist tag", e);
	   }
	}

	/**
	 * the XPath expression to the element that shall be displayed
	 * @param xpath
	 */
	public void setSelect(String xpath) {
		this.xp = xpath;
	}

	/**
	 * if set to true, the file size is displayed
	 * @param showsize
	 */
	public void setShowsize(boolean showsize) {
		this.showsize = showsize;
	}
}
