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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * This servlet forwards the Editor Redirect Pages 
 * 
 * 
 */
public class MCREditorForwardServlet extends MCRServlet {

	private static final long serialVersionUID = 1L;

	private static Logger LOGGER = Logger.getLogger(MCREditorForwardServlet.class);

    /**
     * The initalization of the servlet.
     * 
     * @see javax.servlet.GenericServlet#init()
     */
    public void init() throws ServletException {
        super.init();
    }

    /**
     * The method replace the default form MCRServlet 
     * 
     * @param job
     *            the MCRServletJob instance
     */
    public void doGetPost(MCRServletJob job) throws ServletException, Exception {
    	HttpServletRequest request = job.getRequest();
    	HttpServletResponse response = job.getResponse();
    	String requestPath = request.getPathInfo();
        LOGGER.info("MCREditorForwardServlet: request path = " + requestPath);

        if (requestPath == null) {
            String msg = "Error: HTTP request path is null";
            LOGGER.error(msg);
            response.sendRedirect(getBaseURL() + "mycore-error.jsp?messageKey=SWF.EditorError&lang=" + 
            		MCRServlet.getSession(request, getServletName()).getCurrentLanguage());
            return;
        }
        String editorFile = requestPath.substring(requestPath.lastIndexOf("/")+1);
        if(editorFile.contains("?")){
        	editorFile = editorFile.split("?")[0];
        }
        String editorPath = requestPath.substring(requestPath.indexOf("/")+1);//+ ".xml";
        if (editorFile != null && !editorFile.equals("")) {
        	this.getServletContext().getRequestDispatcher("/nav?path=~editor-include&id=" + editorFile+"&editorPath="+editorPath).forward(request, response);
        	return;
        }
        String msg = "Error: HTTP request path has wrong format, no '/' given";
        LOGGER.error(msg);
        response.sendRedirect(getBaseURL() + "mycore-error.jsp?messageKey=SWF.EditorError&lang=" + 
        		MCRServlet.getSession(request, getServletName()).getCurrentLanguage());
        return;
    }
}
