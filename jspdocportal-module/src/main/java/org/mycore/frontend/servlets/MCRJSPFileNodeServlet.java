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

import org.apache.commons.lang.StringUtils;
import org.mycore.datamodel.ifs.MCRFileNodeServlet;

/**
 * This Servlet overides only the output methods of MCRFileNodeServlet for JSPDocportal use 
 * @author Robert Stephan
 * 
 *  
 *  */
public class MCRJSPFileNodeServlet extends MCRFileNodeServlet {
    private static final long serialVersionUID = 1L;
  
    @Override
    protected String getPath(String pathInfo) {
    	int pos = pathInfo.indexOf("/"+getOwnerID(pathInfo));
    	if(pos>=0){
    		return super.getPath(pathInfo.substring(pos));
    	}
    	else{
    		return super.getPath(pathInfo);
    	}
    			
    }
    
    @Override
    protected String getOwnerID(String pathInfo) {
        String[] path = StringUtils.split(pathInfo, "/");
        //apache commons StringUtils.split ignores leading and trailing separators and removes empty parts
        
      //path begins with object id followed by derivate id 
        if(path.length>1 && path[1].contains("_derivate_")){
                return path[1];
        }
        
        if(path.length>0){
            return path[0];
        }

        return "";
    }
}