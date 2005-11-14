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

import java.io.OutputStream;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRException;
import org.mycore.common.MCRSession;
import org.mycore.common.MCRSessionMgr;
import org.mycore.common.xml.MCRXMLContainer;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.datamodel.metadata.MCRXMLTableManager;

/**
 * This servlet response the MCRObject certain by the call path
 * <em>.../receive/MCRObjectID</em> or
 * <em>.../servlets/MCRObjectServlet/id=MCRObjectID[&XSL.Style=...]</em>.
 * 
 * @author Heiko Helmbrecht
 * 
 * @see org.mycore.frontend.servlets.MCRServlet
 */
public class MCRJSPObjectServlet extends MCRServlet {
    private static Logger LOGGER = Logger.getLogger(MCRJSPObjectServlet.class);

    private static MCRConfiguration CONFIG = null;


    /**
     * The initalization of the servlet.
     * 
     * @see javax.servlet.GenericServlet#init()
     */
    public void init() throws ServletException {
        super.init();
        CONFIG = MCRConfiguration.instance();
    }

    /**
     * The method replace the default form MCRServlet and redirect the
     * MCRLayoutServlet.
     * 
     * @param job
     *            the MCRServletJob instance
     */
    public void doGetPost(MCRServletJob job) throws ServletException, Exception {
        // the urn with information about the MCRObjectID
    	HttpServletRequest request = job.getRequest();
    	HttpServletResponse response = job.getResponse();
    	LOGGER.debug("HH: contextPath=" + request.getContextPath());
    	LOGGER.debug("HH: servletPath=" + request.getServletPath());

        String uri = request.getPathInfo();
        String id = null;

        if (uri != null) {
            LOGGER.debug(this.getClass() + " Path = " + uri);

            int j = uri.length();
            LOGGER.debug(this.getClass() + " " + uri.substring(1, j));
            id = uri.substring(1, j);
        } else {
            id = getProperty(job.getRequest(), "id");
        }
        
        if (id == null) {
        	getServletContext().getRequestDispatcher("/nav?path=~mycore-error&messageKey=IdNotGiven").forward(request,response);
        }
        String style;
        style = request.getParameter("XSL.Style");
        if (style != null && style.equals("xml")) {
            response.setContentType("text/xml");
            OutputStream out = response.getOutputStream();
            Document jdom = new MCRObject().receiveJDOMFromDatastore(id);
            if (jdom != null) {
                new org.jdom.output.XMLOutputter().output(jdom, out);        	
            }
            out.close();
            return;
        }
        
        request.setAttribute("id", id);
        
        this.getServletContext().getRequestDispatcher("/nav?path=~docdetail").forward(request, response);	
    }
}
