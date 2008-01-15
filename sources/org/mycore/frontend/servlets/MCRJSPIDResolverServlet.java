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

import java.io.File;
import java.io.OutputStream;
import java.io.StringReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.mycore.datamodel.ifs.MCRDirectory;
import org.mycore.datamodel.ifs.MCRFile;
import org.mycore.datamodel.ifs.MCRFileNodeServlet;
import org.mycore.datamodel.ifs.MCRFilesystemNode;
import org.mycore.datamodel.metadata.MCRMetaLinkID;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.datamodel.metadata.MCRObjectStructure;
import org.mycore.services.fieldquery.MCRQuery;
import org.mycore.services.fieldquery.MCRQueryManager;
import org.mycore.services.fieldquery.MCRResults;


/**
 * This servlet response the MCRObject certain by the call path
 * <em>.../receive/MCRObjectID</em> or
 * <em>.../servlets/MCRObjectServlet/id=MCRObjectID[&XSL.Style=...]</em>.
 * 
 * @author Robert Stephan
 * 
 * @see org.mycore.frontend.servlets.MCRServlet
 */
public class MCRJSPIDResolverServlet extends MCRServlet {

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
    public void doGetPost(MCRServletJob job) throws ServletException, Exception {
        // the urn with information about the MCRObjectID
    	HttpServletRequest request = job.getRequest();
    	HttpServletResponse response = job.getResponse();
    
    	String id = request.getParameter("id");
        String ppn = request.getParameter("ppn");
        String urn = request.getParameter("urn");
        String pdf = request.getParameter("pdf");
        String xml = request.getParameter("xml");
         
        StringBuffer queryString = null;
        if(id!=null){
        	queryString = new StringBuffer();
        	queryString.append("<query>");
           	queryString.append("   <conditions format=\"xml\">");
           	queryString.append("      <boolean operator=\"AND\">");
           	//queryString.append("       <condition field=\"objectType\" operator=\"=\" value=\"professor\" />");
           	queryString.append("         <condition field=\"id\" operator=\"=\" value=\""+id+"\" />");
           	queryString.append("      </boolean>");
           	queryString.append("   </conditions>");
    		queryString.append("</query>");
    	}
    	if(urn!=null){
    		queryString = new StringBuffer();
    		queryString.append("<query>");
    		queryString.append("   <conditions format=\"xml\">");
    		queryString.append("      <boolean operator=\"AND\">");
    		//queryString.append("       <condition field=\"objectType\" operator=\"=\" value=\"professor\" />");
    		queryString.append("         <condition field=\"urn\" operator=\"=\" value=\""+urn+"\" />");
    		queryString.append("      </boolean>");
    		queryString.append("   </conditions>");
    		queryString.append("</query>");
		}
    	if(ppn!=null){
    		queryString = new StringBuffer();
    		queryString.append("<query>");
    		queryString.append("   <conditions format=\"xml\">");
    		queryString.append("      <boolean operator=\"AND\">");
    		//queryString.append("       <condition field=\"objectType\" operator=\"=\" value=\"professor\" />");
    		queryString.append("         <condition field=\"ppn\" operator=\"=\" value=\""+ppn+"\" />");
    		queryString.append("      </boolean>");
    		queryString.append("   </conditions>");
    		queryString.append("</query>");
		}    
    
    	if(queryString!=null){
    		StringReader stringReader=new StringReader(queryString.toString());
    		SAXBuilder builder = new SAXBuilder();
    		Document input = builder.build(stringReader);
    		MCRResults result = MCRQueryManager.search(MCRQuery.parseXML(input));
    		if(result.getNumHits()>0){
    			String mcrID = result.getHit(0).getID();
    			if(pdf!=null){
    				MCRObject o = new MCRObject();
    		    	o.receiveFromDatastore(mcrID);
    				MCRObjectStructure structure = o.getStructure(); 
    				MCRMetaLinkID derMetaLink = structure.getDerivate(0);
        			MCRObjectID derID = derMetaLink.getXLinkHrefID();
        			MCRDirectory root;
        			root = MCRDirectory.getRootDirectory(derID.getId());;
        			MCRFilesystemNode[] myfiles = root.getChildren();
        			if(myfiles.length==1){
        				response.setContentType( "application/pdf" );
        				response.setHeader("Content-Disposition", "attachment; filename=" + myfiles[0].getName());
        				if (myfiles[0] instanceof MCRFile) {
        					((MCRFile)myfiles[0]).getContentTo(response.getOutputStream());
        				}
        			}   				
    			}
    			else if(xml!=null){
    				MCRObject o = new MCRObject();
    		    	o.receiveFromDatastore(id);
    	    		Document doc = o.createXML();
    	    		response.setContentType("text/xml");
    	    		XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
    	    		xout.output(doc, response.getOutputStream());
    			}
    			else{
    				this.getServletContext().getRequestDispatcher("/nav?path=~docdetail&id=" +mcrID).forward(request, response);
    			}    			
    		}
    	}   	
    	
        
        if (id == null && ppn==null && urn==null) {
        	getServletContext().getRequestDispatcher("/nav?path=~mycore-error&messageKey=IdNotGiven").forward(request,response);
        }

       
    }
}
