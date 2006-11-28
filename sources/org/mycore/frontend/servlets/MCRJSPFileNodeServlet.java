/*
 * $RCSfile$
 * $Revision$ $Date$
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

package org.mycore.frontend.servlets;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.DOMOutputter;
import org.mycore.datamodel.ifs.MCRFileNodeServlet;
import org.mycore.datamodel.metadata.MCRDerivate;
import org.mycore.datamodel.metadata.MCRObject;

/**
 * This Servlet overides only the output methods of mcrfilenodservlet for jsp docportal use 
 * @author Anja Schaar
 * 
 *  
 *  */
public class MCRJSPFileNodeServlet extends  MCRFileNodeServlet{
    private static final long serialVersionUID = 1L; 
    // The Log4J logger
    private static Logger LOGGER = Logger.getLogger(MCRJSPFileNodeServlet.class.getName());

    
    protected void forwardRequest(HttpServletRequest req, HttpServletResponse res, Document jdom) throws IOException, ServletException {
    	//the derivate
    	String derid = jdom.getRootElement().getChild("ownerID").getText();
    	MCRDerivate mcr_der = new MCRDerivate();
    	mcr_der.receiveFromDatastore(derid);
    	
    	String mainDoc = mcr_der.getDerivate().getInternals().getMainDoc();
    	if ( mainDoc.length() < 1)
    		 mainDoc = jdom.getRootElement().getChild("children").getChild("child").getChildText("name");

    	String mcrid   =  mcr_der.getDerivate().getMetaLink().getXLinkHref();
    	MCRObject mcr_obj = new MCRObject();
    	Document jmcr_obj = mcr_obj.receiveJDOMFromDatastore(mcrid);
    	String objTitle   = jmcr_obj.getRootElement().getAttributeValue("label");
    		
   		if ( jmcr_obj.getRootElement().getChild("metadata").getChild("titles") != null )
    			objTitle   =  jmcr_obj.getRootElement().getChild("metadata").getChild("titles").getChildText("title");

   		Element addons = new Element("details");    	
    	addons.setAttribute("mainDoc", mainDoc);
    	addons.setAttribute("mcrid", mcrid);    	
    	addons.setAttribute("objTitle", objTitle);
    	
    	jdom.getRootElement().addContent(addons);

		org.w3c.dom.Document domDoc = null;
		try {
			domDoc = new DOMOutputter().output(jdom);
		} catch (JDOMException e) {
			LOGGER.error("Domoutput failed: ", e);
		}

    	req.setAttribute("jDomMcrDir", domDoc);
        String style;
        style = req.getParameter("XSL.Style");
        if (style != null && style.equals("xml")) {
            res.setContentType("text/xml");
            OutputStream out = res.getOutputStream();
            if (jdom != null) {
                new org.jdom.output.XMLOutputter().output(jdom, out);        	
            }
            out.close();
            return;
        }

        getServletContext().getRequestDispatcher("/nav?path=~derivatedetails").forward(req, res);
        return;
    }
    
    protected void errorPage ( HttpServletRequest req, HttpServletResponse res, int error, String msg, Exception ex, boolean xmlstyle)  throws IOException {
    	String path = "/nav?path=~mycore-error&messageKey=MCRJSPFileNodeServlet.error."+error+"&message="+msg;
    	try {
    		getServletContext().getRequestDispatcher(path).forward(req,res);
    	} catch (ServletException se) {
    		LOGGER.error("Error on forwarding errorpage", se);
    		
    	}
        return;
    }
}