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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.mycore.common.MCRConfiguration;

/**
 * This Servlet overides only the output methods of mcrfilenodservlet for jsp docportal use 
 * @author Anja Schaar
 * 
 *  
 *  */
public class MCRWFFileNodeServlet extends  MCRServlet{
    private static final long serialVersionUID = 1L; 
	private static Logger LOGGER = Logger.getLogger(MCRWFFileNodeServlet.class);
   
    /**
     * The initalization of the servlet.
     * 
     * @see javax.servlet.GenericServlet#init()
     */
    public void init() throws ServletException {
        super.init();
        CONFIG = MCRConfiguration.instance();
    }
    
    public void doGetPost(MCRServletJob job) throws ServletException, Exception {
        // the urn with information about the MCRObjectID
    	HttpServletRequest request = job.getRequest();
    	HttpServletResponse response = job.getResponse();
    	LOGGER.debug("servletPath=" + request.getServletPath());
        String uri = request.getPathInfo();
        String filename = null;
        String type = null;
        String derivateID = null;
        
        if (uri != null) {
            LOGGER.debug(" Path = " + uri);
            String path[] = uri.split("/");
            derivateID = path[1];
            filename = path[2];
            type = getProperty(job.getRequest(), "type");
        }
        
        if (filename == null || type == null) {
        	getServletContext().getRequestDispatcher("/nav?path=~mycore-error&messageKey=IdNotGiven").forward(request,response);
        }
        String basedir = CONFIG.getString("MCR.WorkflowEngine.EditDirectory." + type);
        File file = new File ( basedir + "/" + derivateID + "/" + filename );
        if ( file.exists() && file.canRead()) {
        	// 	 Set the headers.
        	if ( filename.endsWith("pdf"))
        		response.setContentType("application/pdf");
        	else if ( filename.endsWith("jpg") )
        		response.setContentType("image/jpeg");
        	else if ( filename.endsWith("gif"))
        		response.setContentType("image/gif");
        	else if (filename.endsWith("png")  )
        		response.setContentType("image/png");
        	else 
        		response.setContentType("application/x-download");
        	response.setHeader("Content-Disposition", "attachment; filename=" + filename);

        	//        	 Send the file.
        	OutputStream out = response.getOutputStream(  );
        	returnFile(file, out); 
        }        
        
    }
    
    public static void returnFile(File file, OutputStream out)    throws FileNotFoundException, IOException {
		InputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(file));
			byte[  ] buf = new byte[4 * 1024];  // 4K buffer
			int bytesRead;
			while ((bytesRead = in.read(buf)) != -1) {
				out.write(buf, 0, bytesRead);
			}
		}
		finally {
			if (in != null) in.close(  );
		}
	}
}