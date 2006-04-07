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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.output.DOMOutputter;

import org.mycore.common.MCRDefaults;
import org.mycore.common.MCRSession;
import org.mycore.common.MCRSessionMgr;
import org.mycore.common.MCRUtils;
import org.mycore.common.xml.MCRXMLHelper;

import org.mycore.frontend.editor.MCREditorSubmission;
import org.mycore.frontend.editor.MCRRequestParameters;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.servlets.MCRServletJob;

import org.mycore.user2.MCRUserMgr;

/**
 * This class is the superclass of servlets which checks the MCREditorServlet
 * output XML and store the XML in a file or if an error was occured start the
 * editor again.
 * 
 * @author Heiko Helmbrecht
 * @version $Revision$ $Date$
 */
public class MCRRegisterUserWorkflowServlet extends MCRServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(MCRRegisterUserWorkflowServlet.class);
	private String pid;
	private String nextPath;
	private String workflowType;
	private String newUserID;
	private String ID ;
	private String lang;
	private MCRWorkflowEngineManagerInterface WFI;
	private String documentType ="user";
	
    /**
     * This method overrides doGetPost of MCRServlet. <br />
     */
    public void doGetPost(MCRServletJob job) throws Exception {
		MCRSession mcrSession = MCRSessionMgr.getCurrentSession();  
    	HttpServletRequest  request = job.getRequest();
    	HttpServletResponse response = job.getResponse();
       

    	// read the XML data
        MCREditorSubmission sub = (MCREditorSubmission) (request.getAttribute("MCREditorSubmission"));
        
       
        // read the parameter
        MCRRequestParameters parms;

        if (sub == null) {
            parms = new MCRRequestParameters(request);
        } else {
            parms = sub.getParameters();
        }
        
    	pid = parms.getParameter("processid");
		nextPath = parms.getParameter("nextPath");
		workflowType = parms.getParameter("workflowType");
		newUserID = parms.getParameter("newUserID");
		ID = parms.getParameter("userID");
		lang = mcrSession.getCurrentLanguage();
        String todo = parms.getParameter("todo");


        WFI = MCRWorkflowEngineManagerFactory.getImpl(workflowType);
		
		if ( pid == null ) {
			initializeUserRegistration(sub);
	       	request.getRequestDispatcher("/nav?path=" + nextPath).forward(request, response);
	       	return;
		} else {
	        MCRJbpmWorkflowObject wfo = new MCRJbpmWorkflowObject(Long.parseLong(pid));
    		if ( ! AI.checkPermission("administrate-user") ) {
       			wfo.setWorkflowStatus("error" + workflowType + "Right");
    		} else {    		
    			if ( "WFModifyWorkflowUser".equals(todo) ) {
    				// nochmals editieren
    				/**
    				  <mcr:includeEditor 
    		          isNewEditorSource="false" 
    		          mcrid="${userID}" type="user" workflowType="registeruser"
    		          step=""  target="MCRCheckUserRegistrationServlet" nextPath="~workflow-registeruser" 
    		          editorPath="editor/workflow/editor-modifyuser.xml" />
    		          ***/   
    	        	// befüllten Editor für das Object includieren
    				// aus dem wfo die Daten für die ID ... holen 
    				ID = wfo.getStringVariable("initiatorUserID");
    				
    	        	request.setAttribute("isNewEditorSource","false");
    	        	request.setAttribute("mcrid",ID);
    	        	request.setAttribute("type",documentType);
    	        	request.setAttribute("workflowType",workflowType);
    	        	request.setAttribute("step","");
    	        	request.setAttribute("nextPath",nextPath);
    	        	request.setAttribute("editorPath","editor/workflow/editor-modifyuser.xml");
    	        	request.getRequestDispatcher("/nav?path=~editor-include").forward(request, response);
    	        	return;    				
    			}
    		}
		}
     }
          
    private void initializeUserRegistration(MCREditorSubmission sub) throws Exception { 
        Element root = new Element("mycoreuser");        
 		root.setAttribute("noNamespaceSchemaLocation", "MCRUser.xsd", org.jdom.Namespace.getNamespace("xsi", MCRDefaults.XSI_URL));
        Element userElement = new Element("user");
        org.jdom.Document   indoc = sub.getXML();
		
        if ( newUserID != null ) {
			// Neue ID verwenden, da erste ID schon existiert
    		StringBuffer storePath = new StringBuffer(WFI.getWorkflowDirectory("user"))
    			.append(File.separator).append("user_")     	
    			.append(ID).append(".xml");
        	userElement = (Element) setNewUserIDforUser(newUserID, ID, storePath.toString(), lang);
		} else {
            userElement = (Element) indoc.getRootElement().getChild("user").clone();
		}
        
        if ( userElement != null ) {
			ID = userElement.getAttributeValue("ID");
			      	
	       	StringBuffer storePath = new StringBuffer(WFI.getWorkflowDirectory("user"))
				.append(File.separator).append("user_")
				.append(ID).append(".xml");
	       	
	       	root.addContent(userElement);
			org.jdom.Document outDoc =  new org.jdom.Document (root);	        
			WFI.storeMetadata(MCRUtils.getByteArray(outDoc), ID, storePath.toString());
				
			if ( MCRUserMgr.instance().existUser(userElement.getAttributeValue("ID")) ) {
				// we have another user with that ID 
		        logger.warn("User registration - duplicate IDs");
		        nextPath = "~registerChooseIDwhenDuplicate&userID="+ID;
			} else {
				//erst wenn alles OK ist wird der WFI initiiert mit der UserID, die unique ist.
				//we have registeruser prozess - with that id
				long pid = WFI.getUniqueCurrentProcessID(ID);
				if ( pid ==0) {
					pid = WFI.initWorkflowProcess(ID);				
					MCRSessionMgr.getCurrentSession().put("registereduser", new DOMOutputter().output( outDoc ));
			        nextPath = "~registeredUser";		
			    }
				// for initiator and editor 
				WFI.setWorkflowVariablesFromMetadata(String.valueOf(pid),userElement);
		       	
		    }		        
        }		
	}
	  
    public final Element setNewUserIDforUser(String newID, String userID, String fullname, String lang ) throws Exception {
    	 org.jdom.Element userElement = null;
         try {
             org.jdom.Document doc = MCRXMLHelper.parseURI(fullname, false);
             userElement = (Element) doc.getRootElement().getChild("user").clone();
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
