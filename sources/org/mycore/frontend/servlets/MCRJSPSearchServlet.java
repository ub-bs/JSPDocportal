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
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.jdom.output.XMLOutputter;
import org.mycore.common.JSPUtils;
import org.mycore.common.MCRCache;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRSessionMgr;
import org.mycore.frontend.editor.MCREditorSubmission;
import org.mycore.parsers.bool.MCRCondition;
import org.mycore.services.fieldquery.MCRQuery;
import org.mycore.services.fieldquery.MCRQueryManager;
import org.mycore.services.fieldquery.MCRQueryParser;
import org.mycore.services.fieldquery.MCRResults;
import org.mycore.services.fieldquery.MCRSearchServlet;

/**
 * This servlet executes queries and presents result pages.
 * 
 * @author Frank Lützenkirchen
 * @author Harald Richter
 */
public class MCRJSPSearchServlet extends MCRSearchServlet {
    private static final long serialVersionUID = 1L;

    private static String resultlistType = "simple";
    /** 
     *  Forwards the document to the output
     *  @author A.Schaar
     *  @see its overwritten in jspdocportal 
     */
    protected void forwardRequest(HttpServletRequest req, HttpServletResponse res, Document jdom) throws IOException, ServletException {
        req.setAttribute("query", jdom);
        this.getServletContext().getRequestDispatcher("/nav?path=~searchstart-" + resultlistType).forward(req, res);        
    }

    /** 
     *  Redirect browser to results page
     *  @author A.Schaar
     *  @see its overwritten in jspdocportal 
     */
    protected void sendRedirect( HttpServletRequest req, HttpServletResponse res, String id, String numPerPage) throws IOException{
// 		Redirect browser to first results page
//	    String url = "MCRSearchServlet?mode=results&id=" + id + "&numPerPage=" + numPerPage;
//	    res.sendRedirect(res.encodeRedirectURL(url));

    	
    	Document query = (Document) (getCache(getQueriesKey()).get(id));
    	
	    String resultlistType = "simple";
		if (query.getRootElement().getAttributeValue("resultlistType") != null) {
		    	resultlistType = query.getRootElement().getAttributeValue("resultlistType");
		    	query.getRootElement().removeAttribute("resultlistType") ;
		    }
	    req.setAttribute("query", query);

		req.setAttribute("resultlistType",resultlistType);
	    req.setAttribute("mode","results");
	    req.setAttribute("id",id);
	    req.setAttribute("numPerPage",numPerPage);
	    String url = "/nav?path=~searchresult-" + resultlistType;
	    try {
	    	this.getServletContext().getRequestDispatcher(url).forward(req, res);
	    } catch ( ServletException sx) {
	    	;
	    }
    }


}
