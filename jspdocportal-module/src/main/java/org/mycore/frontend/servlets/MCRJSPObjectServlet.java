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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.mycore.datamodel.metadata.MCRMetadataManager;
import org.mycore.datamodel.metadata.MCRObjectID;

/**
 * This servlet response the MCRObject certain by the call path
 * <em>.../receive/MCRObjectID</em> or
 * <em>.../servlets/MCRObjectServlet/id=MCRObjectID[&XSL.Style=...]</em>.
 * 
 * @author Heiko Helmbrecht
 * 
 */
public class MCRJSPObjectServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Logger LOGGER = Logger.getLogger(MCRJSPObjectServlet.class);


    /**
     * The initalization of the servlet.
     * 
     * @see javax.servlet.GenericServlet#init()
     */
    public void init() throws ServletException {
        super.init();
    }

    /**
     * The method replace the default form MCRSearchServlet and redirect the
     * 
     * @param job
     *            the MCRServletJob instance
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    	LOGGER.debug("contextPath=" + request.getContextPath());
    	LOGGER.debug("servletPath=" + request.getServletPath());

        String uri = request.getPathInfo();
        String id = null;

        if (uri != null) {
            LOGGER.debug(" Path = " + uri);

            int j = uri.length();
            LOGGER.debug(" id = " + uri.substring(1, j));
            id = uri.substring(1, j);
        } else {
            id = request.getParameter("id");
        }
        id = id.replace("cpr_staff_0000", "cpr_person_").replace("cpr_professor_0000", "cpr_person_");
        if (id == null) {
        	getServletContext().getRequestDispatcher("/nav?path=~mycore-error&messageKey=IdNotGiven").forward(request,response);
        }
        String style;
        style = request.getParameter("XSL.Style");
        if (style != null && style.equals("xml")) {
            response.setContentType("text/xml");
            OutputStream out = response.getOutputStream();
            Document jdom = MCRMetadataManager.retrieve(MCRObjectID.getInstance(id)).createXML();
            if (jdom != null) {
                new org.jdom2.output.XMLOutputter().output(jdom, out);        	
            }
            out.close();
            return;
        }
        
        //request.setAttribute("id", id);
        
        this.getServletContext().getRequestDispatcher("/nav?path=~docdetail&id=" +id).forward(request, response);	
    }
}
