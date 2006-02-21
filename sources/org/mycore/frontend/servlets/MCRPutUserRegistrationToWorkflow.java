/**
 * $RCSfile$
 * $Revision$ $Date$
 *
 * This file is part of ** M y C o R e **
 * Visit our homepage at http://www.mycore.de/ for details.
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
 * along with this program, normally in the file license.txt.
 * If not, write to the Free Software Foundation Inc.,
 * 59 Temple Place - Suite 330, Boston, MA  02111-1307 USA
 *
 **/

package org.mycore.frontend.servlets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.servlet.ServletException;


import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import org.mycore.common.MCRConfigurationException;
import org.mycore.common.MCRMailer;
import org.mycore.common.MCRSession;
import org.mycore.common.MCRSessionMgr;
import org.mycore.common.xml.MCRXMLHelper;

import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.editor.MCREditorSubmission;
import org.mycore.frontend.editor.MCRRequestParameters;

import org.mycore.frontend.workflow.MCRWorkflowManager;
import org.mycore.user2.MCRUser;
import org.mycore.user2.MCRUserMgr;


/**
 * 
 * @author Anja Schaar
 * @version $Revision$ $Date$
 */

public class MCRPutUserRegistrationToWorkflow extends MCRCheckDataBase {
	protected static Logger LOGGER = Logger.getLogger(MCRPutUserRegistrationToWorkflow.class);
	private static final long serialVersionUID = 1L;
	private static MCRWorkflowManager WFM = null;
	private static String SLASH = "/";
	private org.jdom.Element userElement = null;
	private String savedir = "";
	private String url = "";
	
	/** Initialisation of the servlet */
	public void init() throws ServletException {
		super.init();
		try {
			savedir = CONFIG.getString("MCR.editor_user_directory" );
			WFM = MCRWorkflowManager.instance();
		} catch (Exception e) {
			LOGGER.error("WFM-Error",e);
		} 
	}
	

	/**
	 * This method overrides doGetPost of MCRServlet. <br />
	 */
	public void doGetPost(MCRServletJob job) throws Exception {
		MCRSession mcrSession = MCRSessionMgr.getCurrentSession();  

		if ( job.getRequest().getParameter("newUserID") != null ){
			// Neue ID verwenden, da erste ID schon existiert
			url = job.getRequest().getParameter("page");
			String ID = job.getRequest().getParameter("userID");
	        String fullname = savedir + SLASH + "user_" + ID + ".xml";    
			setNewUserIDforUser(job.getRequest().getParameter("newUserID"), ID, fullname, job, mcrSession.getCurrentLanguage());
		} else {       
        	getEditorSubmission(job);
		}

		if ( userElement != null ) {
			String ID = userElement.getAttributeValue("ID");	
			MCRUser testuser = MCRUserMgr.instance().retrieveUser(userElement.getAttributeValue("ID"));
			if ( testuser != null && testuser.getID().equals(ID)) { 
				// user with that ID exist in datastore
	            logger.warn("User registration - duplicate IDs");
    			url = "nav?path=~registerChangeID&userid="+ID;
	        } else {
				MCRWorkflowManager.createWorkflowDefaultRule(ID, ID);
	        }
			
	        String fullname = savedir + SLASH + "user_" + ID + ".xml";    
	        org.jdom.Document outDoc =  new org.jdom.Document (userElement);	        
	        storeUserMetadata( outDoc, job, ID, fullname, mcrSession.getCurrentLanguage());
	        
			job.getResponse().sendRedirect(job.getResponse().encodeRedirectURL(getBaseURL() + url));
		}
	}
	
	private void getEditorSubmission(MCRServletJob job) throws IOException, ServletException {
        // Read the XML data sent by the editor		
        MCREditorSubmission sub = (MCREditorSubmission) (job.getRequest().getAttribute("MCREditorSubmission"));
        org.jdom.Document jdomDoc = sub.getXML();

        // Read the request parameters
        MCRRequestParameters parms;

        if (sub == null) {
            parms = new MCRRequestParameters(job.getRequest());
        } else {
            parms = sub.getParameters();
        }
        
        String useCase = parms.getParameter("usecase");		 
		url = parms.getParameter("page");
				       
        // Determine the use case
        if ( useCase.equals("register-user")  && jdomDoc.getRootElement().getName().equals("mycoreuser")) {
            userElement = (Element) jdomDoc.getRootElement().getChild("user").clone();
        }
        
    }


	public final void storeUserMetadata(org.jdom.Document outdoc, MCRServletJob job,
			String ID, String fullname, String lang) throws Exception {
			try {
				FileOutputStream out = new FileOutputStream(fullname);
				(new XMLOutputter(Format.getPrettyFormat())).output(outdoc,out);
				out.flush();
			} catch (IOException ex) {
				logger.error(ex.getMessage());
				logger.error("Exception while store to file " + fullname);
				errorHandlerIO(job, lang);
				return;
			}
			logger.info("User " + ID + " stored under " + fullname + ".");
	}
	
	public final void setNewUserIDforUser(String newID, String userID, String fullname, 
			MCRServletJob job,String lang ) throws Exception {
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
			errorHandlerIO(job, lang);
			return;
		}					
	}
	public String getNextURL(MCRObjectID ID) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public void sendMail(MCRObjectID ID) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void sendMail(String userid) throws Exception {
		// TODO Auto-generated method stub
		ArrayList addr = new ArrayList();
        try {
        	String mailaddr = CONFIG.getString("MCR.editor_user_register_mail");
			StringTokenizer st = new StringTokenizer(mailaddr, ",");
			while (st.hasMoreTokens()) {
				addr.add(st.nextToken());
			}
			String sender = WFM.getMailSender();
			String appl = CONFIG.getString("MCR.editor_mail_application_id", "DocPortal");
			String subject = "Userregistration from " + appl;
			StringBuffer text = new StringBuffer();
			text.append("Es wurde ein neuer Nutzer mit der ID ").append(userid).append(" hat sich angemeldet!");
			
			LOGGER.info(text.toString());
			try {
				MCRMailer.send(sender, addr, subject, text.toString(),	false);
			} catch (Exception ex) {
				LOGGER.error("Can't send a mail to " + addr);
			}
        } catch (MCRConfigurationException noDir) {
			LOGGER.warn("No mail address for user to register is in the configuration.");
		}
	}


}

