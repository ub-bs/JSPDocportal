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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.jdom.output.XMLOutputter;
import org.mycore.backend.query.MCRQueryManager;
import org.mycore.common.JSPUtils;
import org.mycore.common.MCRSession;
import org.mycore.frontend.editor.MCREditorSubmission;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.servlets.MCRServletJob;
import org.mycore.services.fieldquery.MCRResults;

/**
 * This servlet is checking the incoming query for sorted fields and
 *  is adding some default fields
 * @author Heiko Helmbrecht
 * @version $Revision$ $Date$
 */

public class MCRCheckQueryServlet extends MCRServlet {
	
	protected static Logger logger = Logger.getLogger(MCRCheckQueryServlet.class);

	public void doGetPost(MCRServletJob job) throws Exception {
		
		HttpServletRequest request = job.getRequest();
		
		// read the XML data
		MCREditorSubmission sub = (MCREditorSubmission) (job.getRequest()
				.getAttribute("MCREditorSubmission"));
		Document jdomQuery = sub.getXML();
		
		Element root = jdomQuery.getRootElement();
		
	    logger.debug("unchecked query: \n" + JSPUtils.getPrettyString(jdomQuery));		

	    // eliminate <condition> without value
	    Iterator it = jdomQuery.getDescendants(new ElementFilter("condition"));
	    List valuelessConditions = new ArrayList();
	    while ( it.hasNext() )
	    {
	      Element el = (Element) it.next();
          String value = el.getAttributeValue("value");
	      if ((value == null) || value.equals("") ) {
	      	    valuelessConditions.add(el);
	      }
	    }
	    for (Iterator iter = valuelessConditions.iterator(); iter.hasNext();) {
			Element el = (Element) iter.next();
			el.getParent().removeContent(el);
		}
	    
	    if(root.getChild("sortby") == null) {
	    	Element sortby = new Element("sortby");
	    	

	    	Element sortfield1 = new Element("field");
	    	sortfield1.setAttribute("field","modified");
	    	sortfield1.setAttribute("order","descending");	    	
	    	sortby.addContent(sortfield1);
	    	
	    	Element sortfield2 = new Element("field");
	    	sortfield2.setAttribute("field","title");
	    	sortfield2.setAttribute("order","ascending");
	    	Element sortfield3 = new Element("field");
	    	sortfield3.setAttribute("field","author");
	    	sortfield3.setAttribute("order","ascending");
	    	

	    	sortby.addContent(sortfield2);
	    	sortby.addContent(sortfield3);	    	
	    	
	    	root.removeChild("sortby");
	    	root.addContent(sortby);
	    }
	    
	    String resultlistType = "simple";
	    if (root.getAttributeValue("resultlistType") != null) {
	    	resultlistType = root.getAttributeValue("resultlistType");
	    	root.removeAttribute("resultlistType") ;
	    }
	    
	    logger.debug("checked query: \n" + JSPUtils.getPrettyString(jdomQuery));

        request.setAttribute("query", jdomQuery);
        request.setAttribute("resultlistType",resultlistType);

        this.getServletContext().getRequestDispatcher("/nav?path=~searchresult-" + resultlistType).forward(request, job.getResponse());	
	}
}
