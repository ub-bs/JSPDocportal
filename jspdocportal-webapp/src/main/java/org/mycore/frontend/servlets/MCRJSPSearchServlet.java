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
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.output.XMLOutputter;
import org.mycore.common.MCRException;
import org.mycore.common.MCRSession;
import org.mycore.common.MCRSessionMgr;
import org.mycore.services.fieldquery.MCRCachedQueryData;
import org.mycore.services.fieldquery.MCRFieldDef;
import org.mycore.services.fieldquery.MCRSearchServlet;
import org.mycore.services.fieldquery.MCRSortBy;

/**
 * This servlet executes queries and presents result pages.
 * 
 * @author Anja Schaar
 * @author Robert Stephan
 * 
 */
public class MCRJSPSearchServlet extends MCRSearchServlet {
    private static final long serialVersionUID = 1L;
    protected static final Logger LOGGER = Logger.getLogger(MCRJSPSearchServlet.class);
    private String resultlistType = "simple";
    
    public void doGetPost(MCRServletJob job) throws IOException, ServletException {
        String mode = job.getRequest().getParameter("mode");
        
        if ("resort".equals(mode)){
        	resortQuery(job.getRequest(), job.getResponse());
        }
        else if ("refine".equals(mode)){
        	refineQuery(job.getRequest(), job.getResponse());
       	}
        else if ("renew".equals(mode)){
        	renewQuery(job.getRequest(), job.getResponse());
        }
        else if("load".equals(mode)){
        	String sessionID = job.getRequest().getParameter("MCRSessionID");
        	if((sessionID!=null) && !sessionID.equals("")){
        		MCRSessionMgr.setCurrentSession(MCRSessionMgr.getSession(sessionID));
        	}
        	super.doGetPost(job);
        }
        else {
        	super.doGetPost(job);
        }
    }

    // this calls the editor of the searchmask with the inputfield
    private void refineQuery(HttpServletRequest req, HttpServletResponse res) throws IOException {
    	String furl = "/nav?path=";    
		String id = req.getParameter("id");
		String editormask = req.getParameter("mask");
		MCRSession session = MCRSessionMgr.getCurrentSession();
		
		String mcrSessionID =  session.getID();
		if ( mcrSessionID == null ){
			 mcrSessionID = (String) req.getAttribute("XSL.MCRSessionID");
		}
		if ( editormask.startsWith("~searchstart-class")) {
			// this comes from the browserclass and we have no searchmask
			//String browseuri  = session.BData.getUri();
			//furl +=editormask+"&actUriPath="+browseuri;
			furl +=editormask;
		} else if ( editormask.startsWith("~searchstart-index")) {
				// this comes from the indexbrowser and we have no searchmask
				furl +=editormask; 
		} else {
			// we must set the session, cause in the next request for the editor call we need the right 
			// session to get the query from the cache
			//furl += editormask+"&sourceid="+id+"&session="+session.getID();
			if ( mcrSessionID == null ){
				LOGGER.error("session is null - can't load the query from the Cache ");
			}
			furl += editormask+"&sourceid="+id+"&session="+mcrSessionID;
		}
		
		try {
			this.getServletContext().getRequestDispatcher(furl).forward(req, res);
		} 
		catch ( ServletException se) {
			LOGGER.error("error forward ", se);
    	}
    }
    
    // this calls the editor start address of the searchmask with the inputfield or the classbrowser start adresse
    private void renewQuery(HttpServletRequest req, HttpServletResponse res) throws IOException  {
		String editormask = req.getParameter("mask");
    	String furl = "/nav?path="+editormask;    
		try {
			this.getServletContext().getRequestDispatcher(furl).forward(req, res);
		} catch ( ServletException se) {
			LOGGER.error("error forward ", se);
    	}

    }    
    
    //this mode comes from the resort form in the resultlist
    private void resortQuery(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
    	String id = req.getParameter("id");
        MCRCachedQueryData qd = MCRCachedQueryData.getData( id );
        if(qd==null){
        	showResults(req,  res);
        }

		List<MCRSortBy> sortByList = qd.getQuery().getSortBy();
		sortByList.clear();
		int i = 1;
		for ( i = 1; i < 4; i++) {
			if (req.getParameter("field" + i) != null && !req.getParameter("field" + i).equals("")) {
				String fieldname = req.getParameter("field" + i);
				String order = (req.getParameter("order" + i) != null) ?
						req.getParameter("order" + i) : "ascending" ;
				MCRSortBy sortBy = new MCRSortBy(MCRFieldDef.getDef(fieldname),order.equals("ascending"));
				sortByList.add(sortBy);
			
			} else {break;}
		}
		qd.getQuery().setSortBy(sortByList);	
		// Show incoming query document
        if (LOGGER.isDebugEnabled()) {
            XMLOutputter out = new XMLOutputter(org.jdom.output.Format.getPrettyFormat());
            LOGGER.debug(out.outputString(qd.getQuery().buildXML()));
        }
        showResults(req, res);
    }
    
    /** 
     *  Forwards the document to the output
     *  @author A.Schaar
     *  @see its from mycore and overwritten here 
     */

	protected void sendToLayout(HttpServletRequest req, HttpServletResponse res, Document jdom) throws IOException {
    	if ( "results".equalsIgnoreCase(jdom.getRootElement().getName()) ) {
    		String path = "/nav?path=";

    		//<mcr:results xmlns:mcr="http://www.mycore.org/" id="1iljqgz8zqp6merg8xiel" 
            // Show incoming result document
            if (LOGGER.isDebugEnabled()) {
                XMLOutputter out = new XMLOutputter(org.jdom.output.Format.getPrettyFormat());
                LOGGER.debug(out.outputString(jdom));
            }
    		//String id = jdom.getRootElement().getAttributeValue("id");
    		String mask = jdom.getRootElement().getAttributeValue("mask");
    		String id = req.getParameter("id");
    		if(id==null) {id= jdom.getRootElement().getAttributeValue("id");}
    		 MCRCachedQueryData qd = MCRCachedQueryData.getData( id );
    	      if( qd == null )
    	      {
    	        throw new MCRException( "Result list is not in cache any more, please re-run query" );
    	      }
    	    	   
    		String[] maskarray = mask.split("-");
    		
    		if ( maskarray.length > 1 ) {
    			 resultlistType = maskarray[1];
    			 path += "~searchresult-" + resultlistType;
    		}
    		else path += mask; 

    		try {
        		this.getServletContext().getRequestDispatcher(path).forward(req, res);	
    		} catch ( ServletException se) {
    			LOGGER.error("error forward ", se);
        	}   			
    	}
    	else {
    		// reload the searchmask with in the query
            // Send query XML to editor
            getLayoutService().sendXML(req, res, jdom);
    	}
    }
}
