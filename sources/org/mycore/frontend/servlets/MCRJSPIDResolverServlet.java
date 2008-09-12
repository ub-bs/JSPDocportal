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

import java.io.StringReader;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.hibernate.Transaction;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.mycore.backend.hibernate.MCRHIBConnection;
import org.mycore.datamodel.ifs.MCRDirectory;
import org.mycore.datamodel.ifs.MCRFile;
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
        
        String img = request.getParameter("img");
        String page= request.getParameter("page");
        String nr = request.getParameter("nr");
         
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
        				/* the following code does not change the URL in the browser, but I cannot set additional parameter to open the pdf */
        				/*
        				response.setContentType( "application/pdf" );
        				response.setHeader("Content-Disposition", "attachment; filename=" + myfiles[0].getName());
        				if (myfiles[0] instanceof MCRFile) {
        					((MCRFile)myfiles[0]).getContentTo(response.getOutputStream());
        				}*/
        				if(myfiles[0] instanceof MCRFile && myfiles[0].getAbsolutePath().endsWith(".pdf")){
        					StringBuffer sbPath = new StringBuffer(getBaseURL());
        					sbPath.append("file/").append(myfiles[0].getPath());
        					if(page!=null){
        						sbPath.append("#page=").append(page);
        					}
        					else if(nr!=null){
        						sbPath.append("#page=").append(nr);
        					}
        					response.sendRedirect(sbPath.toString());
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
    			else if(img!=null){
    				//Create URL for DFG ImageViewer and Forward to it
    				//http://dfg-viewer.de/v1/?set%5Bmets%5D=http%3A%2F%2Frosdok.uni-rostock.de%2Fdata%2Fetwas%2Fetwas1737%2Fetwas1737.mets.xml&set%5Bzoom%5D=min
    			
    				StringBuffer sbDFGViewerURL = null; new StringBuffer("http://dfg-viewer.de/v1/"); //?set%5Bmets%5D=http%3A%2F%2Frosdok.uni-rostock.de%2Fdata%2Fetwas%2Fetwas1737%2Fetwas1737.mets.xml&set%5Bzoom%5D=min";
    			
    				MCRObject o = new MCRObject();
    		    	o.receiveFromDatastore(mcrID);
    				MCRObjectStructure structure = o.getStructure(); 
    				MCRMetaLinkID derMetaLink = structure.getDerivate(0);
        			MCRObjectID derID = derMetaLink.getXLinkHrefID();
        			MCRDirectory root;
        			root = MCRDirectory.getRootDirectory(derID.getId());;
        			MCRFilesystemNode[] myfiles = root.getChildren();
        			for (MCRFilesystemNode f: myfiles){
    					if((f instanceof MCRFile) && ((MCRFile) f).getAbsolutePath().endsWith(".mets.xml")){
    						sbDFGViewerURL = new StringBuffer("http://dfg-viewer.de/v1/");
    						sbDFGViewerURL.append("?set%5Bmets%5D=");
    						sbDFGViewerURL.append(URLEncoder.encode(getBaseURL()+"file/"+f.getPath(), "UTF-8"));
    						Document docMETS = ((MCRFile)f).getContentAsJDOM();
    				
    						if(page!=null){
    							while (page.startsWith("0")){
    								page=page.substring(1);
    							}
        						Namespace nsMets=Namespace.getNamespace("mets", "http://www.loc.gov/METS/");
        						XPath xpID = XPath.newInstance("/mets:mets/mets:structMap[@TYPE='PHYSICAL']" +
        				    		"/mets:div[@TYPE='physSequence']/mets:div[starts-with(@ORDERLABEL, '" +page+"')]/@ORDER");
        						xpID.addNamespace(nsMets);
        						Attribute a = (Attribute)xpID.selectSingleNode(docMETS);
        						if(a!=null){
        							sbDFGViewerURL.append("&set[image]=").append(a.getValue());
        						}        						
    						}
    						else if (nr!=null){
    							sbDFGViewerURL.append("&set[image]=").append(nr);
    						}
    						sbDFGViewerURL.append("&set[zoom]=min");
    						break;
    					}
        			}
        			LOGGER.debug("DFGViewer URL: "+sbDFGViewerURL.toString());
        			response.sendRedirect(sbDFGViewerURL.toString());
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
