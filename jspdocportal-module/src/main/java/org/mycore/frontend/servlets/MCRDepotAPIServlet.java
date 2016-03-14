/*
 * $RCSfile$
 * $Revision: 25918 $ $Date: 2013-01-24 13:20:01 +0100 (Do, 24 Jan 2013) $
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
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.mycore.common.HashedDirectoryStructure;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.common.config.MCRConfigurationException;
import org.mycore.common.xml.MCRXMLFunctions;

/**
 * This servlet delivers content from the depot directory
 * which contains additional files, that are not stored in MyCoRe derivates.
 * It uses the recordIdentifier to calculate the base directory 
 * of the files using a hased directory structure
 * 
 * @author Robert Stephan
 * 
 */
public class MCRDepotAPIServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Logger LOGGER = Logger.getLogger(MCRDepotAPIServlet.class);


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
   
     */
    
       
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {

    	LOGGER.debug("contextPath=" + request.getContextPath());
    	String path = request.getRequestURL().toString();

    	path = path.substring(path.indexOf("/depot/")+6);
    	while(path.startsWith("/")){
    		path = path.substring(1);
    	}
    	String recordIdentifier = StringUtils.split(path, "/",2)[0];
    	try{
    		recordIdentifier = URLDecoder.decode(URLDecoder.decode(recordIdentifier, "UTF-8"), "UTF-8");
    	}
    	catch(UnsupportedEncodingException e){
    		//won't happen - UTF-8 is always supported
    	}
    	String filePath = StringUtils.split(path, "/", 2)[1];
    	Path depotDir = null;
    	try{
    		depotDir = Paths.get(MCRConfiguration.instance().getString("MCR.depotdir"));
    	}
    	catch(MCRConfigurationException cfe){
    		LOGGER.error("Property \"MCR.depotdir\" not defined!", cfe);
    		return;
    	}
    	Path outputDir = HashedDirectoryStructure.createOutputDirectory(depotDir,  recordIdentifier);
    	Path file = outputDir.resolve(filePath);
    	
    	String mimeType = MCRXMLFunctions.getMimeType(file.getFileName().toString());
    		     
    	/* display in browser / if you want to open save as dialog use:
    	response.setHeader( "Content-Disposition",
    	         String.format("attachment; filename=\"%s\"", filename));
    	*/
    	try(OutputStream out = new BufferedOutputStream(response.getOutputStream())){
    		response.setContentType(mimeType);	
        	response.setContentLength((int) Files.size(file));
        	Files.copy(file, out);
    	}
    	catch(Exception e){
    		LOGGER.error(e);
    	}
    }
}
