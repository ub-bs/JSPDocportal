/*
 * $RCSfile$
 * $Revision: 19974 $ $Date: 2011-02-20 12:23:20 +0100 (So, 20 Feb 2011) $
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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.mycore.access.MCRAccessManager;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRException;
import org.mycore.common.MCRSessionMgr;
import org.mycore.datamodel.ifs.MCRDirectory;
import org.mycore.datamodel.ifs.MCRFile;
import org.mycore.datamodel.ifs.MCRFileNodeServlet;
import org.mycore.datamodel.ifs.MCRFilesystemNode;
import org.mycore.datamodel.metadata.MCRDerivate;
import org.mycore.datamodel.metadata.MCRMetaLinkID;
import org.mycore.datamodel.metadata.MCRMetadataManager;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
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
public class MCRJSPGlobalResolverServlet extends MCRServlet {

	private static final long serialVersionUID = 1L;

	private static Logger LOGGER = Logger.getLogger(MCRJSPGlobalResolverServlet.class);


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
    	
    	String path = request.getPathInfo();
       	
    	LOGGER.debug("path to resolve: "+path);
    	//last entry contains whole path of file
    	if(path.startsWith("/")){
    		path = path.substring(1);
    	}
    	String[] data = (path).split("/", 4);
    	
    	String open = request.getParameter("open");
    	MCRObjectID mcrObjID = null;
    	MCRObjectID mcrDerID=null;
    	if(data.length>2){
    		try{
    			mcrDerID = MCRObjectID.getInstance(data[2]);
    		}
    		catch(MCRException e){
    			//do nothing
    		}
    	}
    	
        if(mcrDerID==null){
        	if(data.length>1){
        		try{
        			if(data[0].equals("id")){
        				mcrObjID = MCRObjectID.getInstance(data[1]);
        			}
        			else{
        				if(" id pnd ppn urn ".contains(data[0])){
        					String queryString = createQuery(data[0], data[1]);
        					StringReader stringReader=new StringReader(queryString.toString());
        					SAXBuilder builder = new SAXBuilder();
        					Document input = builder.build(stringReader);
        					MCRResults result = MCRQueryManager.search(MCRQuery.parseXML(input));
        					if(result.getNumHits()>0){
        						mcrObjID = MCRObjectID.getInstance(result.getHit(0).getID());
        					}
        				}
        			}
        		}
        		catch(MCRException e){
        			//do nothing
        		}
    		}
        	if(data.length==2){
        		//show the metadata as xml or in docdetails
        		if("asXML".equals(open)){
        			Document doc = MCRMetadataManager.retrieveMCRObject(mcrObjID).createXML();    	    		 
    	    		response.setContentType("text/xml");
    	    		XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
    	    		xout.output(doc, response.getOutputStream());
    	    		return;
        		}
        		this.getServletContext().getRequestDispatcher("/nav?path=~docdetail&id=" +mcrObjID.toString()).forward(request, response);
        		return;
        	}
        	if(data.length>2 && mcrObjID!=null){
        		String label = data[2];
        		MCRObject o = MCRMetadataManager.retrieveMCRObject(mcrObjID);
        		for(MCRMetaLinkID der: o.getStructure().getDerivates()){
        			if(der.getXLinkLabel().startsWith(label)){
        					mcrDerID = der.getXLinkHrefID();
        			}
        		}
        	}
        }
        
        
        if(data.length==3 && "asXML".equals(open)){
        	Document doc = MCRMetadataManager.retrieveMCRDerivate(mcrDerID).createXML();    	    		 
    		response.setContentType("text/xml");
    		XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
    		xout.output(doc, response.getOutputStream());
    		return;
        }
        
        MCRFilesystemNode mainFile = null;
        if(mcrDerID!=null){
        	MCRDirectory root = MCRDirectory.getRootDirectory(mcrDerID.toString());
        	if(data.length>3){
        		mainFile = root.getChildByPath(data[3]);
        	}
        	else{
        		MCRDerivate der = MCRMetadataManager.retrieveMCRDerivate(mcrDerID);
        		String mainDoc = der.getDerivate().getInternals().getMainDoc();
        		if(mainDoc!=null){
        			mainFile = root.getChildByPath(mainDoc);
        		}
    	    	if(mainFile==null){
    	    		MCRFilesystemNode[] myfiles = root.getChildren(MCRDirectory.SORT_BY_NAME);
    	    		if(myfiles.length==1){        				
    	    			mainFile = myfiles[0];
    	    		}
    	    	}
    		}    		
    	}
    	if(mainFile!=null){
    		if (!MCRAccessManager.checkPermissionForReadingDerivate(mainFile.getOwnerID())) {
                LOGGER.info("MCRFileNodeServlet: AccessForbidden to " + mainFile.getName());
                response.sendRedirect(response.encodeRedirectURL(getBaseURL() + accessErrorPage));
                return;
            }
        	if("DFGViewer".equals(open)||((open==null && mainFile.getName().endsWith(".mets.xml")))){
        		openDFGViewer(request, response, (MCRFile)mainFile);
        		return;
        	}
        	if(mainFile.getPath().endsWith(".pdf")){
        		openPDF(request, response, (MCRFile)mainFile);
        		return;
        	}
        	sendFile(response, (MCRFile)mainFile);
        	return;
    	}
    	
    	response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        getServletContext().getRequestDispatcher("/nav?path=~mycore-error&messageKey=MCRJSPGlobalResolver.error.notfound").forward(request,response);
    }
	
	//openPDF	
	private void openPDF(HttpServletRequest request, HttpServletResponse response, MCRFile mcrFile) throws IOException{
		String page= request.getParameter("page");
	    String nr = request.getParameter("nr");
		
	  	StringBuffer sbURL = new StringBuffer(getBaseURL());
		sbURL.append("file/").append(mcrFile.getPath());
		if(page!=null){
			sbURL.append("#page=").append(page);
		}
		else if(nr!=null){
			sbURL.append("#page=").append(nr);
		}
		String url = sbURL.toString();
		if(url.length()>0){
			response.sendRedirect(url);
		}
		
		
	}

	
	//Create URL for DFG ImageViewer and Forward to it
	//http://dfg-viewer.de/v1/?set%5Bmets%5D=http%3A%2F%2Frosdok.uni-rostock.de%2Fdata%2Fetwas%2Fetwas1737%2Fetwas1737.mets.xml&set%5Bzoom%5D=min
	private void openDFGViewer(HttpServletRequest request, HttpServletResponse response, MCRFile mcrFile) throws IOException{

		String thumb = request.getParameter("thumb");
		String page= request.getParameter("page");
	    String nr = request.getParameter("nr");
		StringBuffer sbURL = new StringBuffer("");
		try{
			Namespace nsMets=Namespace.getNamespace("mets", "http://www.loc.gov/METS/");
			Namespace nsXlink=Namespace.getNamespace("xlink", "http://www.w3.org/1999/xlink");
			Document docMETS = mcrFile.getContentAsJDOM();
			Element eMETSPhysDiv = null;
			if(page!=null){
				while (page.startsWith("0")){
					page=page.substring(1);
				}
				XPath xpID = XPath.newInstance("/mets:mets/mets:structMap[@TYPE='PHYSICAL']" +
						"/mets:div[@TYPE='physSequence']/mets:div[starts-with(@ORDERLABEL, '" +page+"')]");
				xpID.addNamespace(nsMets);
				eMETSPhysDiv = (Element)xpID.selectSingleNode(docMETS);
			}
			else if (nr!=null){
				while (nr.startsWith("0")){
					nr=nr.substring(1);
				}
				XPath xpID = XPath.newInstance("/mets:mets/mets:structMap[@TYPE='PHYSICAL']" +
						"/mets:div[@TYPE='physSequence']/mets:div[@ORDER='" +nr+"']");
				xpID.addNamespace(nsMets);
				eMETSPhysDiv = (Element)xpID.selectSingleNode(docMETS);
			}
			if(thumb == null){
					//display in DFG-Viewer
				sbURL = new StringBuffer("http://dfg-viewer.de/v1/");
				sbURL.append("?set%5Bmets%5D=");
				sbURL.append(URLEncoder.encode(getBaseURL()+"file/"+mcrFile.getPath(), "UTF-8"));
				if(eMETSPhysDiv!=null){
					sbURL.append("&set[image]=").append(eMETSPhysDiv.getAttributeValue("ORDER"));
				}
				sbURL.append("&set[zoom]=min");
			}
			else {
				//return thumb image    										
				@SuppressWarnings("unchecked")
				List<Element> l = (List<Element>) eMETSPhysDiv.getChildren();
				String fileid = null;
				for(Element e: l){
					if(e.getAttributeValue("FILEID").startsWith("THUMB")){
							fileid = e.getAttributeValue("FILEID");
					}
				}
				if(fileid !=null){
					// <mets:file MIMETYPE="image/jpeg" ID="THUMBS.matrikel1760-1789-Buetzow_c0001">
				        //		<mets:FLocat LOCTYPE="URL" xlink:href="http://rosdok.uni-rostock.de/data/matrikel_handschriften/matrikel1760-1789-Buetzow/THUMBS/matrikel1760-1789-Buetzow_c0001.jpg" />
				        //  </mets:file>
									
				XPath xpFLocat = XPath.newInstance("//mets:file[@ID='"+fileid+"']/mets:FLocat");
				xpFLocat.addNamespace(nsMets);
				Element eFLocat = (Element)xpFLocat.selectSingleNode(docMETS);
				if(eFLocat!=null){
					sbURL = new StringBuffer(eFLocat.getAttributeValue("href", nsXlink));
				}
			}
		}
		
		
		}catch(Exception e){
			
		}
		String url = sbURL.toString();
		if(url.length()>0){
			response.sendRedirect(url);
		}    		
	}
	
	private String createQuery(String key, String value) {
		StringBuffer queryString = new StringBuffer();
		queryString = new StringBuffer();
		queryString.append("<query>");
		queryString.append("   <conditions format=\"xml\">");
		queryString.append("      <boolean operator=\"and\">");
		queryString.append("         <condition field=\""+key+"\" operator=\"=\" value=\"" + value + "\" />");
		queryString.append("      </boolean>");
		queryString.append("   </conditions>");
		queryString.append("</query>");
		return queryString.toString();
	}
	private static String accessErrorPage = MCRConfiguration.instance().getString("MCR.Access.Page.Error", "");
	 /**
     * Sends the contents of an MCRFile to the client. 
     * @see MCRFileNodeServlet for implementation details
     * 
     * 
     */
    private void sendFile(HttpServletResponse res, MCRFile file) throws IOException {
        LOGGER.info("Sending file " + file.getName());

        res.setContentType(file.getContentType().getMimeType());
        res.setContentLength((int) file.getSize());
        res.addHeader("Accept-Ranges", "none"); // Advice client not to attempt range requests
        
        // no transaction needed to copy long streams over slow connections
        MCRSessionMgr.getCurrentSession().commitTransaction();
        OutputStream out = new BufferedOutputStream(res.getOutputStream());
        file.getContentTo(out);
        out.close();
     }
    
}
