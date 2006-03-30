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
 * 
 * 
 */

package org.mycore.frontend.workflowengine.jbpm;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import org.mycore.common.MCRSession;
import org.mycore.common.MCRSessionMgr;
import org.mycore.frontend.editor.MCRRequestParameters;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.servlets.MCRServletJob;

/**
 * This class is the superclass of servlets which checks the MCREditorServlet
 * output XML and store the XML in a file or if an error was occured start the
 * editor again.
 * 
 * @author Heiko Helmbrecht
 * @version $Revision$ $Date$
 * 
 */
public class MCRWorkflowActions extends MCRServlet {
	private static final long serialVersionUID = 1L;
	private static Logger LOGGER = Logger.getLogger(MCRWorkflowActions.class);

	/**
     * This method overrides doGetPost of MCRServlet. <br />
     */
    public void doGetPost(MCRServletJob job) throws Exception {
    	HttpServletRequest request = job.getRequest();
    	HttpServletResponse response = job.getResponse();
            	
        MCRRequestParameters parms = new MCRRequestParameters(request);

        String pid = parms.getParameter("processid");
        
        MCRJbpmWorkflowObject wfo = new MCRJbpmWorkflowObject(Long.parseLong(pid));
        MCRWorkflowEngineManagerInterface WFI = wfo.getCurrentWorkflowManager();
        
        //jbpm_variableinstance  initiator, authorID, reservatedURN und createdDocID
        String mcrid = wfo.getStringVariableValue("createdDocID");
        String userid = wfo.getStringVariableValue("initiator");
        String workflowType = wfo.getWorkflowProcessType();
        String documentType = wfo.getDocumentType();
        
        String derivateID = parms.getParameter("derivateID");
        String nextPath = parms.getParameter("nextPath");

        if ( nextPath == null || nextPath.length() == 0)        	
        	 nextPath = "~workflow-" + documentType;
        
        String todo = parms.getParameter("todo");
       
        if ( "WFAddWorkflowObject".equals(todo) ) {
        	wfo.setWorkflowStatus(documentType + "Created");        	
        	// leeren Editor für das Object includieren
        	request.setAttribute("isNewEditorSource","false");
        	request.setAttribute("mcrid",mcrid);
        	request.setAttribute("type",documentType);
        	request.setAttribute("workflowType",workflowType);
        	request.setAttribute("step","author");
        	request.setAttribute("nextPath",nextPath);
        	request.getRequestDispatcher("/nav?path=~editor-include").forward(request, response);
        	return;
        }
        if ( "WFEditWorkflowObject".equals(todo) ) {
        	wfo.setWorkflowStatus(documentType + "Edited");
        	// befüllten Editor für das Object includieren
        	request.setAttribute("isNewEditorSource","false");
        	request.setAttribute("mcrid",mcrid);
        	request.setAttribute("type",documentType);
        	request.setAttribute("workflowType",workflowType);
        	request.setAttribute("step","author");
        	request.setAttribute("nextPath",nextPath);
        	request.getRequestDispatcher("/nav?path=~editor-include").forward(request, response);
        	return;
        	
        }
        if ( "WFCommitWorkflowObject".equals(todo) ) {
        	//Object komplett in die DB schieben
        	boolean bSuccess =false;
    		if ( ! ( 	AI.checkPermission(mcrid, "commitdb")
    	             && AI.checkPermission(derivateID,"deletedb")) ) {   			
       			wfo.setWorkflowStatus("error" + documentType + "CommitRight");
    		} else {    		
    		   	bSuccess = WFI.commitWorkflowObject(documentType, mcrid);
       			wfo.setWorkflowStatus(documentType + "Committed");
    		}
    		if (bSuccess) {
    			// Je nach WorkflowImplementation reagieren!
    			WFI.setCommitStatus(mcrid, "WFCommitWorkflowObject");
    		}
    		request.getRequestDispatcher("/nav?path=" + nextPath).forward(request, response);
        	return;
        }
        if ( "WFDeleteWorkflowObject".equals(todo) ) {
        	boolean bSuccess =false;
    		if ( ! AI.checkPermission(mcrid, "deletewf") ) {
       			wfo.setWorkflowStatus("error" + documentType + "DeleteRight");
    		} else {
    			bSuccess = WFI.deleteWorkflowObject(mcrid,documentType );
    		}
    		if (bSuccess) {
    			// gesamten Prozess löschen!!
    			MCRJbpmCommands.deleteProcess(pid);
    		}
    		request.getRequestDispatcher("/nav?path=" + nextPath).forward(request, response);
        	return;
        }
        if ( "WFAddNewDerivateToWorkflowObject".equals(todo) ) {
        	derivateID = WFI.addNewDerivateToWorkflowObject(mcrid, documentType, userid);
        	if (derivateID != null && derivateID.length()>0) {
	       		
        	}
        	todo = "WFAddNewFileToDerivate";
        	// kein requestforward !
        }
        if ( "WFEditDerivateFromWorkflowObject".equals(todo) ) {
        	//befüllten Editor für das Derivate includieren
        }
        if ( "WFAddNewFileToDerivate".equals(todo) ) {
        	//leeren upload Editor includieren
			String fuhid = new MCRWorkflowUploadHandler( mcrid, derivateID, "new", nextPath).getID();
			String base = getBaseURL() + "nav";
			Properties params = new Properties();
			params.put("path","~editor-include");
			params.put("uploadID", fuhid);
			params.put("isNewEditorSource", "true");
			params.put("mcrid2", mcrid);
			params.put("type", documentType);
			params.put("workflowType",workflowType);
			params.put("step", "author");
			params.put("mcrid", derivateID);
			params.put("target", "MCRCheckDerivateServlet");
			params.put("processid", pid);

			response.sendRedirect(response.encodeRedirectURL(buildRedirectURL(base, params)));
			return;
       	}
     
        if ( "WFRemoveFileFromDerivate".equals(todo) ) {
        	// ein File aus dem Derivate löschen
        }        
        if ( "WFRemoveDerivateFromWorkflowObject".equals(todo) ) {
            //Anschliessend muss im WF eventuell ein neuer Status gesetzt werden, denn wenn zB. kein Derivate mehr da ist
            //muss wenn es eine Dissertation ist, der Status wieder zurückgesetzt werden
        	boolean bSuccess =false;
    		if ( (  	AI.checkPermission(mcrid, "deletewf")
    	             && AI.checkPermission(derivateID,"deletewf")) ) {    			
    		   	bSuccess = WFI.deleteDerivateObject(documentType, mcrid, derivateID);
    		}
    		
           	if ( bSuccess ) {
           		//TODO
           		wfo.setWorkflowStatus(documentType + "DocumentRemoved");
           	}
            request.getRequestDispatcher("/nav?path=" + nextPath).forward(request, response);
        	return;
        }         
    }
    
	/**
	 * Builds an url that can be used to redirect the client browser to another
	 * page, including http request parameters. The request parameters will be
	 * encoded as http get request.
	 * 
	 * @param baseURL
	 *            the base url of the target webpage
	 * @param parameters
	 *            the http request parameters
	 */
	private String buildRedirectURL(String baseURL, Properties parameters) {
		StringBuffer redirectURL = new StringBuffer(baseURL);
		boolean first = true;

		for (Enumeration e = parameters.keys(); e.hasMoreElements();) {
			if (first) {
				redirectURL.append("?");
				first = false;
			} else
				redirectURL.append("&");

			String name = (String) (e.nextElement());
			String value = null;
			try {
				value = URLEncoder
						.encode(parameters.getProperty(name), "UTF-8");
			} catch (UnsupportedEncodingException ex) {
				value = parameters.getProperty(name);
			}

			redirectURL.append(name).append("=").append(value);
		}

		LOGGER.debug("Sending redirect to " + redirectURL.toString());
		return redirectURL.toString();
	}    
    


}
