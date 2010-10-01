/*
 * $RCSfile$
 * $Revision: 11306 $ $Date: 2007-04-05 16:32:11 +0200 (Do, 05 Apr 2007) $
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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.mycore.services.fieldquery.MCRQuery;
import org.mycore.services.fieldquery.MCRQueryManager;
import org.mycore.services.fieldquery.MCRQueryParser;
import org.mycore.services.fieldquery.MCRResults;

/**
 * This servlet opens retrieves the object for the given PND and opens the docdetails view
 * @author Robert Stephan
 * 
 * @see org.mycore.frontend.servlets.MCRServlet
 */
public class MCRJSPPNDServlet extends MCRServlet {

	private static final long serialVersionUID = 1L;

	private static Logger LOGGER = Logger.getLogger(MCRJSPPNDServlet.class);


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
    public void doGetPost(MCRServletJob job) throws ServletException, Exception {
        // the urn with information about the MCRObjectID
    	HttpServletRequest request = job.getRequest();
    	HttpServletResponse response = job.getResponse();
    	LOGGER.debug("contextPath=" + request.getContextPath());
    	LOGGER.debug("servletPath=" + request.getServletPath());

        String uri = request.getPathInfo();
        String pnd = null;
        if (uri != null) {
            pnd = uri.substring(1);
        }
        if (pnd == null || pnd.length()==0) {
        	//getServletContext().getRequestDispatcher("/nav?path=~mycore-error&messageKey=IdNotGiven").forward(request,response);
        	getServletContext().getRequestDispatcher("/").forward(request,response);
        	return;
        }
       
        MCRQuery query = new MCRQuery((new MCRQueryParser()).parse("(pnd = "+pnd+")"));
		MCRResults result = MCRQueryManager.search(query);
		if(result.getNumHits()>0){
			String mcrID = result.getHit(0).getID();
			this.getServletContext().getRequestDispatcher("/nav?path=~docdetail&id=" +mcrID).forward(request, response);
		}
		else{
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "No data found for PND " +pnd+"!");
		}
	}   
}
