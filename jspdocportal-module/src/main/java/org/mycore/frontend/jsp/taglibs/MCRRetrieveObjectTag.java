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

package org.mycore.frontend.jsp.taglibs;

import java.io.File;
import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.jdom2.JDOMException;
import org.jdom2.output.DOMOutputter;
import org.mycore.activiti.MCRActivitiUtils;
import org.mycore.common.MCRException;
import org.mycore.common.content.MCRFileContent;
import org.mycore.datamodel.metadata.MCRMetadataManager;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.xml.sax.SAXException;

/**
 * part of the MCRDocdetails Tag Library
 * 
 * provides the outer tag, which retrieves the document from database or
 * workflow
 * 
 * If a request parameter debug=true is found, the xml syntax of the 
 * MCR object will be displayed on the website
 * 
 * @author Robert Stephan
 * 
 * @version $Revision: 16360 $ $Date: 2010-01-06 00:54:02 +0100 (Mi, 06 Jan
 *          2010) $
 */
public class MCRRetrieveObjectTag extends SimpleTagSupport {
    private String mcrid;

    private boolean fromWorkflow = false;

    private String varDOM;

    private String varJDOM;

    /**
     * sets the MCR Object ID (mandatory)
     * 
     * @param mcrID
     *            the MCR object ID
     * 
     */
    public void setMcrid(String mcrid) {
        this.mcrid = mcrid;
    }

    /**
     * if set to true, the MCR object is retrieved from workflow directory and
     * not from MyCoRe repository
     * 
     * @param fromWorkflow
     *            true or false
     * 
     */
    public void setFromWorkflow(boolean fromWorkflow) {
        this.fromWorkflow = fromWorkflow;
    }

    /**
     * sets the name of the variable which stores the XML Document as
     * org.w3c.dom.Document for further use in JSP processing 
     * (stored in request scope)
     * 
     * @param var -
     *            the name of the variable
     */
    public void setVarDOM(String var) {
        this.varDOM = var;
    }

    /**
     * sets the name of the variable which stores the XML Document as
     * org.jdom2.Document for further use in JSP processing 
     * (stored in request scope)
     * 
     * @param var -
     *            the name of the variable
     */
    
    public void setVarJDOM(String var) {
        this.varJDOM = var;
    }

    /**
     * executes the tag
     */
    public void doTag() throws JspException, IOException {
        try {
            MCRObjectID mcrObjID = MCRObjectID.getInstance(mcrid);
            org.jdom2.Document doc = null;
            if (fromWorkflow) {
                File wfFile = new File(MCRActivitiUtils.getWorkflowDirectory(mcrObjID), mcrid + ".xml");
                MCRFileContent mfc = new MCRFileContent(wfFile);
                doc = mfc.asXML();

            } else {
                doc = MCRMetadataManager.retrieve(mcrObjID).createXML();
            }

            DOMOutputter output = new DOMOutputter();
            org.w3c.dom.Document dom = output.output(doc);
            if (varDOM != null) {
                getJspContext().setAttribute(varDOM, dom, PageContext.REQUEST_SCOPE);
            }
            if (varJDOM != null) {
                getJspContext().setAttribute(varJDOM, dom, PageContext.REQUEST_SCOPE);
            }
        } catch (SAXException | JDOMException e) {
            throw new MCRException(e);
        }
    }
}
