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
package org.mycore.frontend.jsp.taglibs.browsing;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.hibernate.Transaction;
import org.mycore.backend.hibernate.MCRHIBConnection;
import org.mycore.datamodel.ifs.MCRDirectory;
import org.mycore.datamodel.ifs.MCRFile;
import org.mycore.datamodel.ifs.MCRFilesystemNode;
import org.mycore.datamodel.metadata.MCRMetaLinkID;
import org.mycore.datamodel.metadata.MCRMetadataManager;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
/**
 * Displays a preview image
 * The implementation looks for derivate with predefined labels and
 * 
 * 
 * @author Robert Stephan
 *
 */
public class MCRDerivateImageBrowserTag extends SimpleTagSupport {
	
	private int imgWidth=0;
	private String labelSubstring="";
	private String mcrid="";

	public void doTag() throws JspException, IOException {
		Transaction t1=null;
		try {
			Transaction tx  = MCRHIBConnection.instance().getSession().getTransaction();
			if(tx==null || !tx.isActive()){
				t1 = MCRHIBConnection.instance().getSession().beginTransaction();
			}
			JspWriter out = getJspContext().getOut();
			Object o =  getJspContext().getAttribute("WebApplicationBaseURL", PageContext.APPLICATION_SCOPE);
			if(o==null){
				o = new String("");
			}
			StringBuffer sbUrl = new StringBuffer(o.toString());
			sbUrl.append("file/");
    	
			MCRObject obj = MCRMetadataManager.retrieveMCRObject(MCRObjectID.getInstance(mcrid));
			if(obj==null){
				out.write("<b>No object found for id: "+mcrid+".</b>");
				return;
			}

			for(MCRMetaLinkID derId: obj.getStructure().getDerivates()){
				if(derId.getXLinkLabel().contains(labelSubstring)){
					MCRDirectory root = MCRDirectory.getRootDirectory(derId.getXLinkHref());
	    			if(root!=null){
	    				MCRFilesystemNode[] myfiles = root.getChildren();
	    				for ( int j=0; j< myfiles.length; j++) {
	    					MCRFile theFile = (MCRFile) myfiles[j];
	    					if ( theFile.getContentTypeID().indexOf("jpeg")>= 0 ||
	    							theFile.getContentTypeID().indexOf("gif")>= 0 ||
	    							theFile.getContentTypeID().indexOf("png")>= 0) {
	    						String url = sbUrl.toString()+derId.getXLinkHref()+"/"+myfiles[j].getName();
	    						out.write("<img src=\""+url+"\" border=\"0\" width=\""+getImageWidth()+"\" alt=\""+myfiles[j].getName()+"\" />");  
	    						out.write("<br />");
	    					}
	    				}
	    			}			
					
				}
			}
			
	   	
	        	
	  //error
	}catch(Exception e){
		throw new JspException("Error executing docdetails:outputitem tag", e);
	}
	finally{
		if(t1!=null){
			t1.commit();
		}
	}
	}

	/**
	 * only derivate with the given string as part of their label will be displayed
	 * @param substring - the string
	 */
	public void setLabelContains(String substring) {
		this.labelSubstring = substring;
	}
	
	/**
	 * the MCRObject id
	 * @param substring - the string
	 */
	public void setMcrid(String mcrid) {
		this.mcrid = mcrid;
	}

	/**
	 * the width (in pixel) to which the images shall be resized
	 * @param imgWidth
	 */
	public void setImageWidth(int imgWidth) {
		this.imgWidth = imgWidth;
	}
	
	public int getImageWidth() {
		return imgWidth;
	}


}
