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

import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.editor.MCREditorSubmission;
import org.mycore.frontend.editor.MCRRequestParameters;

import org.mycore.frontend.workflow.MCRWorkflowManager;


/**
 * 
 * @author Anja Schaar
 * @version $Revision$ $Date$
 */

public class MCRPutUserRegistrationToWorkflow extends MCRCheckBase {
	protected static Logger LOGGER = Logger.getLogger(MCRPutUserRegistrationToWorkflow.class);
	private static final long serialVersionUID = 1L;
	private static MCRWorkflowManager WFM = null;
	private static String SLASH = File.separator;
	

	/** Initialisation of the servlet */
	public void init() throws ServletException {
		super.init();
		try {
			WFM = MCRWorkflowManager.instance();
		} catch (Exception e) {
			LOGGER.error("WFM-Error",e);
		} 
	}
	

	/**
	 * This method overrides doGetPost of MCRServlet. <br />
	 */
	public void doGetPost(MCRServletJob job) throws Exception {	
		getEditorSubmission(job);		
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
		String url = parms.getParameter("page");
		String savedir = ""; 
        org.jdom.Element userElement = null;
        
        // Determine the use case
        if ( useCase.equals("register-user")  && jdomDoc.getRootElement().getName().equals("mycoreuser")) {
            try {
    			savedir = CONFIG.getString("MCR.editor_user_directory" );
            } catch (MCRConfigurationException noDir) {
            	String errorparms = "?messageKey=SWF.User.ErrorConfigurationKey&message=SWF.User.ErrorConfigurationMessage";
    			job.getResponse().sendRedirect(job.getResponse().encodeRedirectURL(getBaseURL() + "mycore-error.jsp" + errorparms));            	
            }
            userElement = jdomDoc.getRootElement().getChild("user");
        }
		if ( saveToDirectory(userElement, savedir)) {
			job.getResponse().sendRedirect(job.getResponse().encodeRedirectURL(getBaseURL() + url));
        } else {
            // TODO: error message
        	String errorparms = "?messageKey=SWF.User.ErrorKey&message=SWF.User.ErrorMessage";
			job.getResponse().sendRedirect(job.getResponse().encodeRedirectURL(getBaseURL() + "mycore-error.jsp" + errorparms));            	
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
	
	// static method to save any Document Object to an give directory - uses to put it into
	// the workflow directory 
	public  boolean	saveToDirectory(Element userElement , String savedir){	
		FileOutputStream fos =null;
		boolean bret = true;
		if ( userElement != null ) {
			String userid = userElement.getAttributeValue("ID");
			try {
				fos = new FileOutputStream(savedir + "/user_" + userid + ".xml");
				(new XMLOutputter(Format.getPrettyFormat())).output(userElement,fos);
				fos.close();
				sendMail(userid);
				logger.info("Object: /user_"+ userid +".xml  saved to directory " + savedir);
			} catch (Exception ex){
				logger.debug(ex);
				logger.info("Cant save Object: /user_"+ userid +".xml  to directory " + savedir);
			    bret = false;
			} finally{
				if ( fos != null ){
					try {		fos.close(); }			
					catch ( IOException io ) {; 
					    // can't close the fos
					}
				}
			}
		}
		return bret;
	 }

}

