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

package org.mycore.frontend.workflowengine.jbpm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRConfigurationException;
import org.mycore.common.MCRException;
import org.mycore.common.MCRSession;
import org.mycore.common.MCRSessionMgr;
import org.mycore.common.MCRUtils;
import org.mycore.common.xml.MCRXMLHelper;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.editor.MCREditorSubmission;
import org.mycore.frontend.editor.MCRRequestParameters;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.servlets.MCRServletJob;
import org.mycore.frontend.workflow.MCREditorOutValidator;
import org.mycore.frontend.workflowengine.jbpm.xmetadiss.MCRCreateDisshabAction;
import org.mycore.user2.MCRUserMgr;

/**
 * This class is the superclass of servlets which checks the MCREditorServlet
 * output XML and store the XML in a file or if an error was occured start the
 * editor again.
 * 
 * @author Heiko Helmbrecht
 * @version $Revision$ $Date$
 */
public class MCRCheckUserRegistrationServlet extends MCRServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(MCRCheckUserRegistrationServlet.class);
	
	
    /**
     * This method overrides doGetPost of MCRServlet. <br />
     */
     public void doGetPost(MCRServletJob job) throws Exception {
		MCRSession mcrSession = MCRSessionMgr.getCurrentSession();  
    	HttpServletRequest  request = job.getRequest();
    	HttpServletResponse response = job.getResponse();
    	// read the XML data
        MCREditorSubmission sub = (MCREditorSubmission) (request.getAttribute("MCREditorSubmission"));
        org.jdom.Document   indoc = sub.getXML();
        Element userElement = new Element("user");
        
        // read the parameter
        MCRRequestParameters parms;

        if (sub == null) {
            parms = new MCRRequestParameters(request);
        } else {
            parms = sub.getParameters();
        }
        
		String nextPath = parms.getParameter("nextPath");
		String workflowType = parms.getParameter("workflowType");
		MCRWorkflowEngineManagerInterface WFI = MCRWorkflowEngineManagerFactory.getImpl(workflowType);

		String newUserID = parms.getParameter("newUserID");
		String ID = parms.getParameter("userID");
		
        if ( newUserID != null ) {
			// Neue ID verwenden, da erste ID schon existiert
    		StringBuffer storePath = new StringBuffer(WFI.getWorkflowDirectory("user"))
    			.append(File.separator).append("user_")     	
    			.append(ID).append(".xml");
        	userElement = (Element) setNewUserIDforUser(newUserID, ID, storePath.toString(),  mcrSession.getCurrentLanguage());
		} else {
            userElement = (Element) indoc.getRootElement().getChild("user").clone();
		}
        
        if ( userElement != null ) {
			ID = userElement.getAttributeValue("ID");
			
            WFI.setMetadataValidFlag(ID, false);			
	       	WFI.setWorkflowVariablesFromMetadata(ID, userElement);
	       	
	       	StringBuffer storePath = new StringBuffer(WFI.getWorkflowDirectory("user"))
				.append(File.separator).append("user_")
				.append(ID).append(".xml");
			org.jdom.Document outDoc =  new org.jdom.Document (userElement);	        
			WFI.storeMetadata(MCRUtils.getByteArray(outDoc), ID, storePath.toString());
				
			try {
				MCRUserMgr.instance().retrieveUser(userElement.getAttributeValue("ID"));
				// try == error - we have another user with that ID 
		        logger.warn("User registration - duplicate IDs");
		        nextPath = "~registerChooseIDwhenDuplicate&userid="+ID;
			} catch ( MCRException notExist) {
				//catch == ok - new user is unknown and unique
		        nextPath = "~registeredUser";					
		       	WFI.setMetadataValidFlag(ID, true);
		    }		        
        }
       	request.getRequestDispatcher("/nav?path=" + nextPath).forward(request, response);
		
	}
	
     
    public final Element setNewUserIDforUser(String newID, String userID, String fullname, String lang ) throws Exception {
    	 org.jdom.Element userElement = null;
         try {
             org.jdom.Document doc = MCRXMLHelper.parseURI(fullname, false);
             userElement = (Element) doc.getRootElement().clone();
             userElement.setAttribute("ID",newID);
             // delete OldFile
             try {
     			File fi = new File(fullname);
     			if (fi.isFile() && fi.canWrite()) {				
     				fi.delete();     				
     				logger.debug("File " + fullname + " removed.");
     			} else {
     				logger.error("Can't remove file " + fullname);
     			}
     		} catch (Exception ex) {
     			logger.error("Can't remove file " + fullname);
     		}
         } catch (Exception ex) {
 			logger.error(ex.getMessage());
 			logger.error("Exception while loading the file " + fullname);						
 		}
        return userElement;

 	}    

}
