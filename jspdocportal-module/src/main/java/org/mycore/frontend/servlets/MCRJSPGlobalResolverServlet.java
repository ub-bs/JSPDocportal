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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.mycore.access.MCRAccessManager;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.datamodel.ifs.MCRDirectory;
import org.mycore.datamodel.ifs.MCRFile;
import org.mycore.datamodel.ifs.MCRFileNodeServlet;
import org.mycore.datamodel.ifs.MCRFilesystemNode;
import org.mycore.datamodel.metadata.MCRDerivate;
import org.mycore.datamodel.metadata.MCRMetaLinkID;
import org.mycore.datamodel.metadata.MCRMetadataManager;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.datamodel.metadata.MCRObjectStructure;
import org.mycore.services.fieldquery.MCRQuery;
import org.mycore.services.fieldquery.MCRQueryParser;


/**
 * This servlet response the MCRObject certain by the call path
 * <em>.../receive/MCRObjectID</em> or
 * <em>.../servlets/MCRObjectServlet/id=MCRObjectID[&XSL.Style=...]</em>.
 * 
 * @author Robert Stephan
 * 
 * @see org.mycore.frontend.servlets.MCRServlet
 */
public class MCRJSPGlobalResolverServlet extends MCRJSPIDResolverServlet {

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
    	HttpServletRequest request = job.getRequest();
    	HttpServletResponse response = job.getResponse();

    	String uri = request.getRequestURI();
    	String path[] = uri.substring(uri.indexOf("/resolve/")+9).split("/");

    	if(path.length<2){
    		getServletContext().getRequestDispatcher("/nav?path=~mycore-error&messageKey=Resolver.error.unknownUrlSchema").forward(request,response);
    		return;
    	}
    	String key = path[0];
    	String value = path[1];			

    	String mcrID = null;
    	if("id".equals(key)){
    		mcrID = recalculateMCRObjectID(value);
    	}
    	else{
    	    try{
    	        value = URLDecoder.decode(URLDecoder.decode(value, "UTF-8"), "UTF-8");
    	    }
    	    catch(UnsupportedEncodingException e){
    	        //will not happen
    	    }
    		String queryString = "("+key+" = "+value+")";
            MCRQuery query = new MCRQuery((new MCRQueryParser()).parse(queryString));
            //TODO SOLR Migration
            /*
            MCRResults result = MCRQueryManager.search(query);
            
            if(result.getNumHits()!=1){
    			getServletContext().getRequestDispatcher("/nav?path=~mycore-error&messageKey=Resolver.error.noObjectFound").forward(request,response);
    			return;
    		}
            
    		mcrID = result.getHit(0).getID();
    		*/
    	}

    	if(path.length==2){
    		if("xml".equals(request.getParameter("open"))){
    			Document doc = MCRMetadataManager.retrieveMCRObject(MCRObjectID.getInstance(mcrID)).createXML();    	    		 
    			response.setContentType("text/xml");
    			XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
    			xout.output(doc, response.getOutputStream());
    		}
    		else{
    			//show metadata as docdetails view
    			this.getServletContext().getRequestDispatcher("/nav?path=~docdetail&id=" +mcrID).forward(request, response);
    		}
    		return;
    	}
    	String action = path[2];
    	if(action.equals("image")){
    		String url = "";
    		if(path.length==3){
    			url = createURLForDFGViewer(request, mcrID, OpenBy.empty, "");
    		}
    		if(path.length>4 && path[3].equals("page")){
    			url = createURLForDFGViewer(request, mcrID, OpenBy.page, path[4]);
    		}
    		if(path.length>4 &&path[3].equals("nr")){
    			url = createURLForDFGViewer(request, mcrID, OpenBy.nr, path[4]);
    		}
    		if(url.length()>0){
    			LOGGER.debug("DFGViewer URL: "+url);
    			response.sendRedirect(url);				
    		}
    		return;
    	}
    	if(action.equals("pdf")){
    	    String url = "";
    	    if(path.length>4){
    	        if(path[3].equals("page")){
    	            url = createURLForPDF(request, mcrID, path[4], null);
    	        }
    	        if(path[3].equals("nr")){
    	            url = createURLForPDF(request, mcrID, null, path[4]);
    	        }
    	    }
    	    else{
    	        url = createURLForPDF(request, mcrID, null, null);
    	    }
    		if(url.length()>0){
    			LOGGER.debug("PDF URL: "+url);
    			response.sendRedirect(url);				
    		}
    		return;
    	}
    	
    	if(action.equals("pdfdownload")){
    	    this.getServletContext().getRequestDispatcher("/content/pdfdownload.jsp?id=" +mcrID).forward(request, response);
    	}
    	
    	if(action.equals("fulltext")){
    		if(mcrID.startsWith("mvdok")){
    			String url = getBaseURL()+"mjbrenderer?id="+mcrID;
    			response.sendRedirect(url);
    		}
    	}
    	
    	if(action.equals("file") && path.length>3){
    		String label = path[3];
    		long id=-1;
    		try{
    			id = Integer.parseInt(label);
    		}
    		catch(NumberFormatException nfe){
    			//do nothing -> id = -1;
    		}

    		MCRObjectID mcrDerID = null;
    		if(id==-1){
    			MCRObject o = MCRMetadataManager.retrieveMCRObject(MCRObjectID.getInstance(mcrID));
    			MCRObjectStructure structure = o.getStructure();
    			for(MCRMetaLinkID der: structure.getDerivates()){
    				if(der.getXLinkLabel().equals("label")){
    					mcrDerID = der.getXLinkHrefID();
    					break;
    				}
    			}
    		}
    		else{
    			MCRObjectID mcrMetaID = MCRObjectID.getInstance(mcrID);
    			mcrDerID = MCRObjectID.getInstance(mcrMetaID.getProjectId()+"_derivate_"+label);
    		}

    		if(mcrDerID!=null){
    			StringBuffer  filepath= new StringBuffer();
    			if(path.length==4){
    				//display main document
    				MCRDerivate der = MCRMetadataManager.retrieveMCRDerivate(mcrDerID);
    				String mainDoc = der.getDerivate().getInternals().getMainDoc();
    				if(mainDoc!=null && mainDoc.length()>0){
    					filepath.append("/").append(mainDoc);
    				}
    			}
    			else{
    				//display file on remaining path
    				for(int i=4;i<path.length;i++){
    					filepath.append("/").append(path[i]);
    				}
    			}
    			StringBuffer url = new StringBuffer();
    			url.append(getBaseURL()).append("file/").append(mcrID).append("/").append(mcrDerID.toString()).append(filepath); 
    			response.sendRedirect(url.toString());
    		}
    	}
    }

	//CODE under development - try to solve the "Open Large PDF file" problem
	@SuppressWarnings("unused")
	private void showDerivateFile(MCRObjectID mcrID, MCRObjectID mcrDerID, String path, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		// OLD CODE
		// the urn with information about the MCRObjectID
    	MCRFilesystemNode mainFile = null;
        if(mcrDerID!=null){
        	MCRDirectory root = MCRDirectory.getRootDirectory(mcrDerID.toString());
        	if(path!=null){
        		mainFile = root.getChildByPath(path);
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
        	
        	if(mainFile.getPath().endsWith(".pdf")){
        		openPDF(request, response, mcrID.toString(), (MCRFile)mainFile);
        		return;
        	}
        	sendFile(request, response, (MCRFile)mainFile);
        	return;
    	}
    	
    	response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        getServletContext().getRequestDispatcher("/nav?path=~mycore-error&messageKey=MCRJSPGlobalResolver.error.notfound").forward(request,response);
    }
	
	//openPDF	
	private void openPDF(HttpServletRequest request, HttpServletResponse response, String mcrid, MCRFile mcrFile) throws IOException{
		String page= request.getParameter("page");
	    String nr = request.getParameter("nr");
		
	  	StringBuffer sbURL = new StringBuffer(getBaseURL());
		sbURL.append("file/").append(mcrid).append("/").append(mcrFile.getPath());
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

	private static String accessErrorPage = MCRConfiguration.instance().getString("MCR.Access.Page.Error", "");
	 /**
     * Sends the contents of an MCRFile to the client. 
     * @see MCRFileNodeServlet for implementation details
     * 
     * 
     */
    private void sendFile(HttpServletRequest req, HttpServletResponse res, MCRFile file) throws IOException {
        LOGGER.info("Sending file " + file.getName());

        res.setContentType(file.getContentType().getMimeType());
        res.setContentLength((int) file.getSize());
        res.addHeader("Accept-Ranges", "none"); // Advice client not to attempt range requests
        
        // no transaction needed to copy long streams over slow connections
        MCRServlet.getSession(req).commitTransaction();
        OutputStream out = new BufferedOutputStream(res.getOutputStream());
        file.getContentTo(out);
        out.close();
     }
    
}
