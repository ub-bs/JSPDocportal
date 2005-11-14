/**
 * $RCSfile$
 * $Revision$ $Date$
 *
 * This file is part of ** M y C o R e **
 * Visit our homepage at http://www.mycore.de/ for details.
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
 * along with this program, normally in the file license.txt.
 * If not, write to the Free Software Foundation Inc.,
 * 59 Temple Place - Suite 330, Boston, MA  02111-1307 USA
 *
 **/

package org.mycore.frontend.jsp.query;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.mycore.backend.query.MCRQueryManager;
import org.mycore.datamodel.classifications.MCRClassification;
import org.mycore.frontend.editor.MCREditorSubmission;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.servlets.MCRServletJob;
import org.mycore.services.fieldquery.MCRResults;
import org.xml.sax.InputSource;

/**
 * The servlet store the MCREditorServlet output XML in a file of a MCR type
 * dependencies directory, check it dependence of the MCR type and store the XML
 * in a file in this directory or if an error was occured start the editor again
 * with <b>todo </b> <em>repair</em>.
 * 
 * @author Heiko Helmbrecht
 * @version $Revision$ $Date$
 */

public class MCRResortQueryServlet extends MCRServlet {
	
	protected static Logger logger = Logger.getLogger(MCRResortQueryServlet.class);

	public void doGetPost(MCRServletJob job) throws Exception {
		
		XMLOutputter out = new XMLOutputter(org.jdom.output.Format.getPrettyFormat());
		HttpServletRequest request = job.getRequest();
		

		// read the incoming query parametes and build the sortby-element
		String query = request.getParameter("query");
		String resultlistType = request.getParameter("resultlistType");
		
		Element sortby = new Element("sortby");
		int i = 1;
		for ( i = 1; i < 4; i++) {
			if (request.getParameter("field" + i) != null && !request.getParameter("field" + i).equals("")) {
				Element sortField = new Element("field");
				sortField.setAttribute("field",request.getParameter("field" + i));
				String order = (request.getParameter("order" + i) != null) ?
						request.getParameter("order" + i) : "ascending" ;
				sortField.setAttribute("order",order);
				sortby.addContent(sortField);
			} else {break;}
		}
		
		// build the jdomQuery-Object
		query = query.replaceAll("&lt;","<");
		query = query.replaceAll("&gt;",">");
		query = query.replaceAll("&quot;","\"");
		query = query.replaceAll("&#xD;","");
		
		SAXBuilder builder = new SAXBuilder();
        org.jdom.Document jdomQuery = builder.build(new InputSource(new StringReader(query)));

		if ( i != 1) {
			jdomQuery.getRootElement().removeChild("sortby");
			jdomQuery.getRootElement().addContent(sortby);
		}
		
        request.setAttribute("query", jdomQuery);
        request.setAttribute("resultlistType",resultlistType);

        logger.debug("HH:next navpath=~resortresult-" + resultlistType );

		this.getServletContext().getRequestDispatcher("/nav?path=~resortresult-" + resultlistType).forward(request, job.getResponse());
	}
	


}
