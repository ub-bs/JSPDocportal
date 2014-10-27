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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.taglibs.standard.tag.common.xml.XPathUtil;
import org.mycore.access.MCRAccessManager;
import org.mycore.common.MCRConstants;
import org.mycore.datamodel.ifs.MCRDirectory;
import org.mycore.datamodel.ifs.MCRFilesystemNode;
import org.mycore.datamodel.metadata.MCRDerivate;
import org.mycore.datamodel.metadata.MCRMetadataManager;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * display the list of attached derivates
 * @author Robert Stephan
 *
 */
public class MCRDocDetailsDerivateListTag extends SimpleTagSupport {
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
			@SuppressWarnings({"rawtypes" })
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
	    			String label = eN.getAttributeNS(MCRConstants.XLINK_NAMESPACE.getURI(), "title");
	    			String baseurl = getJspContext().getAttribute("WebApplicationBaseURL", PageContext.APPLICATION_SCOPE).toString();
    				MCRObjectID oid = MCRObjectID.getInstance(derID);
    				if(!MCRMetadataManager.exists(oid)){
    				    out.write("<span class=\"error\" >Derivate with id "+oid.toString()+" does not exist.</span>");
    				    continue;
    				}
    				
    				MCRDerivate der = MCRMetadataManager.retrieveMCRDerivate(oid);
	    			if(label.equals("Cover")){
	    				//do nothing - handled elsewhere
	    			}
	    			else if(label.equals("METS")){
	    				//show mets
	    				String mcrid=der.getDerivate().getMetaLink().getXLinkHrefID().toString();	
	    				String metsurl = baseurl +"resolve/id/"+mcrid+"/image";
	    				out.write("<span class=\"button\" style=\"display:inline-block\" >");
	    				out.write("<a href=\""+metsurl+"\" target=\"_blank\">");
	    				out.write("<img style=\"vertical-align:middle\" src=\""+baseurl+"images/dfgviewer.gif\" title = \"Dokument anzeigen\" alt=\"Dokument anzeigen\" />");
	    				out.write(docdetails.getMessages().getString("Webpage.docdetails.showInDFGViewer")+"</a>");
	    				out.write("</span>");	    				
	    			}
	    		
	    			else if(label.equals("MJB")){
	    				//show HMTL	    			
	    				String mcrid=der.getDerivate().getMetaLink().getXLinkHrefID().toString();	
	    				String htmlURL = baseurl +"resolve/id/"+mcrid+"/fulltext";
	    			
	    				out.write("<a href=\""+htmlURL+"\" target=\"blank\">");
	    				out.write("<img src=\""+baseurl+"images/fulltext.gif\" title = \"Volltext anzeigen\" alt=\"Volltext anzeigen\" />");
	    				out.write(docdetails.getMessages().getString("Webpage.docdetails.showFulltext")+"</a>");
	    			
	    			}
	    			else{
	    				String displayLabel = label;
	    				try{
	    					displayLabel=docdetails.getMessages().getString("OMD.derivatedisplay."+label);
	    				}
	    				catch(MissingResourceException e){
	    					//use the default
	    				}
	    				
	    				out.write("\n<dt title=\""+displayLabel+"\">");
	    				ArrayList<String>titles = der.getService().getFlags("title"); 
						for(String t: titles){
							out.write(t);	    							
						}
						out.write("</dt>");
	    				StringBuffer sbUrl = new StringBuffer(o.toString());
	    				sbUrl.append("file/");
	    				sbUrl.append(der.getOwnerID().toString()).append("/");
	    				sbUrl.append(derID);
	    				sbUrl.append("/");
	    		
	    				MCRDirectory root = MCRDirectory.getRootDirectory(derID);
	    				if(root!=null){
	    					MCRFilesystemNode[] myfiles = root.getChildren(MCRDirectory.SORT_BY_NAME);
	    					boolean accessAllowed = MCRAccessManager.checkPermission(derID, "read");	   		    
	    					for ( int j=0; j< myfiles.length; j++) {
	    						MCRFilesystemNode theFile = (MCRFilesystemNode) myfiles[j];
	    						out.write("<dd>");
	    						
	    						if(accessAllowed){
	    							String fURL = sbUrl.toString()+theFile.getName();
	    							out.write("<a href=\""+fURL+"\" target=\"_blank\">");
	    							String imgURL = o.toString()+"images/derivate_unknown.gif";
	    							if(theFile.getName().toLowerCase(Locale.GERMAN).endsWith(".pdf")){
	    								imgURL = o.toString()+"images/derivate_pdf.gif";
	    							}
	    							if(theFile.getName().toLowerCase(Locale.GERMAN).endsWith(".jpg")||
	    									theFile.getName().toLowerCase(Locale.GERMAN).endsWith(".jpeg")){
	    								imgURL = o.toString()+"images/derivate_portrait.gif";
	    							}
	    							if(theFile.getName().toLowerCase(Locale.GERMAN).endsWith(".doc")||
	    									theFile.getName().toLowerCase(Locale.GERMAN).endsWith(".txt")){
	    								imgURL = o.toString()+"images/derivate_doc.gif";
	    							}
	    							out.write("<img src=\""+imgURL+"\" />");
	    							out.write(theFile.getName());
	    							out.write("</a>");
	    							if(showsize){out.write("&#160;("+theFile.getSizeFormatted()+")");}
	    						}
	    						else{
	    							out.write(theFile.getName());
	    							if(showsize){out.write("&#160;("+theFile.getSizeFormatted()+")<br />");}
	    							out.write("&#160;---&#160;"+docdetails.getMessages().getString("OMD.fileaccess.denied"));
	    						}
	    						out.write("</dd></dl>");
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
