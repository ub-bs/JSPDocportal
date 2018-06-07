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
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.taglibs.standard.tag.common.xml.XPathUtil;
import org.mycore.common.MCRConstants;
import org.mycore.datamodel.ifs.MCRDirectory;
import org.mycore.datamodel.ifs.MCRFile;
import org.mycore.datamodel.ifs.MCRFilesystemNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Displays a preview image in docdetails table
 * The implementation looks for derivate with predefined labels and
 * shows them at the right side of the docdetails table
 * 
 * @author Robert Stephan
 * 
 * @deprecated use <mcrb:derivateImageBrowser ...> instead
 *
 */

public class MCRDocDetailsPreviewTag extends SimpleTagSupport {

    private int imgWidth = 0;
    private String labelSubstring = "";

    public void doTag() throws JspException, IOException {
        MCRDocDetailsTag docdetails = (MCRDocDetailsTag) findAncestorWithClass(this, MCRDocDetailsTag.class);
        if (docdetails == null) {
            throw new JspException("This tag must be nested in tag called 'docdetails' of the same tag library");
        }
        try {
            String xp = "/mycoreobject/structure/derobjects/derobject[contains(@xlink:title, '" + labelSubstring
                    + "')]";
            Object o = getJspContext().getAttribute("WebApplicationBaseURL", PageContext.APPLICATION_SCOPE);
            if (o == null) {
                o = new String("");
            }
            XPathUtil xu = new XPathUtil((PageContext) getJspContext());
            @SuppressWarnings("rawtypes")
            List nodes = xu.selectNodes(docdetails.getContext(), xp);
            JspWriter out = getJspContext().getOut();
            out.write("<tr><td colspan=\"3\">");
            out.write("<td rowspan=\"1000\" class=\"" + docdetails.getStylePrimaryName() + "-preview\">");
            if (nodes.size() > 0) {
                for (int i = 0; i < nodes.size(); i++) {
                    Node n = (Node) nodes.get(i);

                    //<img src="<x:out select="concat($WebApplicationBaseURL,'file/',./@derivid,'/',./@name,'?hosts=',$host)" />" 
                    //	border="0"  width="150" />      		
                    StringBuffer sbUrl = new StringBuffer(o.toString());
                    sbUrl.append("file/");
                    Element eN = (Element) n;
                    String derID = eN.getAttributeNS(MCRConstants.XLINK_NAMESPACE.getURI(), "href");
                    sbUrl.append(docdetails.getContext().getOwnerDocument().getDocumentElement().getAttribute("ID"))
                            .append("/");
                    sbUrl.append(derID);
                    sbUrl.append("/");

                    MCRDirectory root = MCRDirectory.getRootDirectory(derID);
                    if (root != null) {
                        MCRFilesystemNode[] myfiles = root.getChildren();
                        for (int j = 0; j < myfiles.length; j++) {
                            MCRFile theFile = (MCRFile) myfiles[j];
                            if (theFile.getContentTypeID().indexOf("jpeg") >= 0
                                    || theFile.getContentTypeID().indexOf("gif") >= 0
                                    || theFile.getContentTypeID().indexOf("png") >= 0) {
                                String url = sbUrl.toString() + myfiles[j].getName();
                                out.write("<img src=\"" + url + "\" border=\"0\" width=\"" + getImageWidth()
                                        + "\" alt=\"" + myfiles[j].getName() + "\" />");
                                out.write("<br />");
                            }
                        }
                    }
                }
            } else {
                String url = o.toString() + "images/emtyDot1Pix.gif";
                out.write("<img src=\"" + url + "\" border=\"0\" width=\"" + getImageWidth() + "\" />");

            }
            out.write("</td></tr>");
            //error
        } catch (Exception e) {
            throw new JspException("Error executing docdetails:outputitem tag", e);
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
