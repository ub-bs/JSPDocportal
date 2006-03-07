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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import org.mycore.access.MCRAccessInterface;
import org.mycore.access.MCRAccessManager;
import org.mycore.common.JSPUtils;
import org.mycore.common.MCRException;
import org.mycore.common.MCRMailer;
import org.mycore.common.MCRSession;
import org.mycore.common.MCRSessionMgr;
import org.mycore.common.MCRUtils;
import org.mycore.datamodel.metadata.MCRDerivate;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.fileupload.*;
import org.mycore.frontend.workflow.MCRWorkflowManager;


/**
 * The servlet start the MyCoRe editor session or other workflow actions with
 * some parameters from a HTML form. The parameters are: <br />
 * <li>type - the MCRObjectID type like schrift, text ...</li>
 * <br />
 * <li>step - the name of the step like author, editor ...</li>
 * <br />
 * <li>layout - the name of the layout like firststep, secondstep ...</li>
 * <br />
 * <li>todo - the mode of the editor start like new or edit or change or delete
 * </li>
 * <li>page - alternative page for reditrect after operation failes
 * </li>
 * <br />
 * <li>mcrid - the MCRObjectID of the data 
 * </li>
 * <br />
 * <li>mcrid - the MCRObjectID of the data for example the Derivate
 * </li>
 * <br />
 * 
 * @author Heiko Helmbrecht, Anja Schaar
 * @version $Revision$ $Date$
 */

public class MCRStartEditorServlet extends MCRServlet {
	private static final long serialVersionUID = 1L;
	private static Logger LOGGER = Logger.getLogger(MCRStartEditorServlet.class);
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
	 * The <b>todo </b> value corresponds with <b>tf_mcrid </b> or <b>se_mcrid
	 * </b> and with the type of the data model for the privileges that the user
	 * need. for some actions you need a third value of re_mcrid for relations
	 * (object - derivate). <br />
	 * 
	 * The table shows the possible todo's in the server: <br />
	 * <table>
	 * <tr>
	 * <th>TODO</th>
	 * <th>MCRObjectID from</th>
	 * <th>used privileg</th>
	 * <th>description</th>
	 * </tr>
	 * <tr>
	 * <td>sdelobj</td>
	 * <td>mcrid</td>
	 * <td>delete-type</td>
	 * <td>delete an object from the server</tr>
	 * <tr>
	 * 
	 * The table shows the possible todo's in the workflow: <br />
	 * <table>
	 * <tr>
	 * <th>TODO</th>
	 * <th>MCRObjectID from</th>
	 * <th>used privileg</th>
	 * <th>description</th>
	 * </tr>
	 * <tr>
	 * <td>wnewobj</td>
	 * <td></td>
	 * <td>create-type</td>
	 * <td>add a new object to the workflow</td>
	 * </tr>
	 * <tr>
	 * <td>wnewder</td>
	 * <td>mcrid</td>
	 * <td>create-type</td>
	 * <td>add a new derivate to the workflow</td>
	 * </tr>
	 * <tr>
	 * <td>waddfile</td>
	 * <td>mcrid <br /> mcrid2</td>
	 * <td>create-type</td>
	 * <td>add a new file to a derivate in the workflow</td>
	 * </tr>
	 * <tr>
	 * <td>weditobj</td>
	 * <td>mcrid</td>
	 * <td>modify-type</td>
	 * <td>edit an object in the workflow</td>
	 * </tr>
	 * <tr>
	 * <td>weditder</td>
	 * <td>mcrid (Der) <br /> mcrid2 (Obj)</td>
	 * <td>modify-type</td>
	 * <td>edit an derivate in the workflow</td>
	 * </tr>
	 * <tr>
	 * <td>wcommit</td>
	 * <td>mcrid</td>
	 * <td>commit-type</td>
	 * <td>commit a document to the server</td>
	 * </tr>
	 * <tr>
	 * <td>wdelobj</td>
	 * <td>mcrid</td>
	 * <td>delete-type</td>
	 * <td>delete an object from the workflow</td>
	 * </tr>
	 * <tr>
	 * <td>wdelder</td>
	 * <td>mcrid <br /> mcrid2</td>
	 * <td>delete-type</td>
	 * <td>delete a derivate from the workflow</td>
	 * </tr>
	 * </table> <br />
	 * <li>If the privileg is not correct it calls	 the  <em> error page </em>.</li>
	 * <br />
	 * <li>If the MCRObjectID is not correct it calls
	 * <em>editor_error_mcrid.xml</em>.</li>
	 * <br />
	 * <li>If a store error is occured it calls <em>editor_error_store.xml</em>.
	 * </li>
	 * <br />
	 * <li>If <b>CANCEL </b> was pressed it calls <em>editor_cancel.xml</em>.
	 * </li>
	 * <br />
	 * <li>If the privileg is correct it starts the file editor_form_
	 * <em>step-type</em> .xml.</li>
	 * <br />
	 */
	public void doGetPost(MCRServletJob job) throws Exception {
		
		HttpServletRequest request = job.getRequest();
		HttpServletResponse response = job.getResponse();
		
		MCRSession mcrSession = null;
		
		String sessionID = request.getParameter("HttpSessionID");
		if ( sessionID != null ) {			
			LOGGER.debug("submitted sessionID = " + sessionID);
			mcrSession = MCRSession.getSession(sessionID);
			MCRSessionMgr.setCurrentSession(mcrSession);
		}
		if ( mcrSession == null ) {
			mcrSession = MCRSessionMgr.getCurrentSession();
		}

		
		// get the current user
		String userid = mcrSession.getCurrentUserID();
		LOGGER.debug("current user for editor actions = " + userid);
		
		// get the current language
		String mylang = mcrSession.getCurrentLanguage();
		LOGGER.info("LANG = " + mylang);

		// read the parameter
		String mystep = getProperty(request, "step");
		if (mystep == null) {
			mystep = "";
		}
		LOGGER.info("STEP = " + mystep);

		String mytype = getProperty(request, "type");
		if (mytype == null) {
			mytype = CONFIG.getString("MCR.default_project_type", "document");
		}
		LOGGER.info("TYPE = " + mytype);
		
		// get the layout
		String mylayout = getProperty(request, "layout");
		if (mylayout == null) {
			mylayout = "";
		}
		LOGGER.info("LAYOUT = " + mylayout);
		// get the editor-page
		String mypage = getProperty(request, "page");
		if (mypage == null) {
			mypage = "";
		}
		LOGGER.info("PAGE for redirect afeter Operation= " + mypage);
		
		
		// get what is to do
		String mytodo = getProperty(request, "todo");
		if ((mytodo == null) || ((mytodo = mytodo.trim()).length() == 0)) {
			mytodo = "wrongtodo";
		}
		if (   !mytodo.equals("wnewobj") 	&& !mytodo.equals("wnewder")
			&& !mytodo.equals("waddfile") 	&& !mytodo.equals("wdelfile")
			&& !mytodo.equals("weditobj") 	&& !mytodo.equals("weditder")
			&& !mytodo.equals("wdelobj") 	&& !mytodo.equals("wdelder")
			&& !mytodo.equals("wsetfile") 	&& !mytodo.equals("wsetlabel")
			&& !mytodo.equals("wcommit") 	&& !mytodo.equals("sdelobj") )	{
			mytodo = "wrongtodo";
		}
		LOGGER.info("TODO = " + mytodo);
		
		// get the MCRObjectID from mcrid parameter
		String mymcrid = getProperty(request, "mcrid");
		
		try {
			// test id
			new MCRObjectID(mymcrid);
		} catch (Exception e) {
			mymcrid = "";
		}
		if ((mymcrid == null)	|| ((mymcrid.trim()).length()==0 )) {
			String defaproject 	= CONFIG.getString("MCR.default_project_id",	"MCR");
			String myproject 	= CONFIG.getString("MCR." + mytype + "_project_id", "MCR");
			if (myproject.equals("MCR")) {
				myproject = defaproject;
			}
			myproject = myproject + "_" + mytype;
			MCRObjectID mcridnext = new MCRObjectID();
			mcridnext.setNextFreeId(myproject);
			String workdir = CONFIG.getString("MCR.editor_" + mytype+ "_directory", "/");
			File workf = new File(workdir);
			if (workf.isDirectory()) {
				String[] list = workf.list();
				for (int i = 0; i < list.length; i++) {
					if (!list[i].startsWith(myproject))
						continue;
					try {
						MCRObjectID mcriddir = new MCRObjectID(list[i]
								.substring(0, list[i].length() - 4));
						if (mcridnext.getNumberAsInteger() <= mcriddir
								.getNumberAsInteger()) {
							mcriddir.setNumber(mcriddir.getNumberAsInteger() + 1);
							mcridnext = mcriddir;
						}
					} catch (Exception e) {
					}
				}
			}
			mymcrid = mcridnext.getId();
		}
		LOGGER.info("MCRID= " + mymcrid);
		
		// get the MCRObjectID from the second Object (derivate) 
		String mymcrid2 = getProperty(request, "mcrid2");
		if (mymcrid2 == null) {
			mymcrid2 = "";
		} else {
			try {
				// test id
				new MCRObjectID(mymcrid2);
			} catch (Exception e) {
				mymcrid2 = "";
			}
		}
		LOGGER.info("second MCRID = " + mymcrid2);
	
		// appending parameter
		String extparm = getProperty(request, "extparm");
		LOGGER.info( "EXTPARM = " + extparm);
		
		LOGGER.debug("Base URL : " + getBaseURL());

		// set the pages and language 
		String pagedir = CONFIG.getString("MCR.editor_workflow_page_dir", "");
		StringBuffer sb = new StringBuffer();
		sb.append(pagedir).append("editor_form_").append(mystep).append('-').append(mytype);
		if (mylayout.length() != 0) {
			sb.append('-').append(mylayout);
		}
		sb.append(".xml");
		
		String myfile = (mypage == "") ? sb.toString() : mypage;
		String cancelpage 		= "nav?path=~workflow-" + mytype;
		String deletepage 		= pagedir	+ CONFIG.getString("MCR.editor_page_delete","editor_delete.xml");
		String privilegeerrorpage 	= "mycore-error.jsp?messageKey=SWF.PrivilegesError&lang=" + mylang;
		String usererrorpage 	= "nav?path=~mycore-error&messageKey=SWF.PrivilegesError&lang=" + mylang;
		String mcriderrorpage 	= pagedir	+ CONFIG.getString("MCR.editor_page_error_mcrid","editor_error_mcrid.xml");
		String storeerrorpage 	= pagedir	+ CONFIG.getString("MCR.editor_page_error_store","editor_error_store.xml");
		String deleteerrorpage 	= pagedir   + CONFIG.getString("MCR.editor_page_error_delete","editor_error_delete.xml");

		String guestUserID = CONFIG.getString("MCR.users_guestuser_username", "gast");
		if (userid.equals(guestUserID)) {
			response.sendRedirect(response.encodeRedirectURL(getBaseURL() + privilegeerrorpage));
			return;
		}		
		
		
		sb = new StringBuffer("");
		sb.append("nav?path=~workflow-").append(mytype);		
		String workflowpage = sb.toString();
		
		if (   mytodo.equals("weditder") 	|| mytodo.equals("wdelder") ) {
			//only in this caase we nee the second mcrid for the derivates
			if ( mymcrid2 == null || mymcrid2.trim().length() == 0 )
						response.sendRedirect(response.encodeRedirectURL(getBaseURL() + mcriderrorpage));
		}					
		

		// action WNEWOBJ - create a new object
		if (mytodo.equals("wnewobj")) {
			if (!AI.checkPermission("create-" + mytype)) {
				response.sendRedirect(getBaseURL() + usererrorpage);
				return;
			}			
			String base = getBaseURL() + myfile;
			Properties params = new Properties();
			params.put("XSL.editor.source.new", "true");
			params.put("XSL.editor.cancel.url", getBaseURL() + cancelpage);
			params.put("mcrid", mymcrid);
			params.put("type",  mytype);
			params.put("step",  mystep);
			response.sendRedirect(response.encodeRedirectURL(buildRedirectURL(base, params)));
			return;
		}

		// action WNEWDER - create a new derivate and put the derivateid to mcrid2
		if (mytodo.equals("wnewder")) {
			mymcrid2 = WFM.createDerivate(mymcrid, false);
			mytodo = "waddfile";
		}

		// action WADDFILE - create a new file in the derivate
		if (mytodo.equals("waddfile")) {
			if (!AI.checkPermission(mymcrid, "writedb")) {
				response.sendRedirect(getBaseURL() + usererrorpage);
				return;
			}
			sb = new StringBuffer(pagedir);
            sb.append("editor_").append(mytype).append("_editor.xml");
         
			String fuhid = new MCRUploadHandlerMyCoRe( mymcrid, mymcrid2, "new", getBaseURL() + sb.toString() ).getID();
			String base = getBaseURL() + "nav";
			Properties params = new Properties();
			params.put("path","~workflow-fileupload");
			params.put("XSL.UploadID", fuhid);
			params.put("XSL.editor.source.new", "true");
			params.put("XSL.editor.cancel.url", getBaseURL() + cancelpage);
			params.put("mcrid", mymcrid2);
			params.put("type", mytype);
			params.put("step", mystep);
			params.put("remcrid", mymcrid);

			response.sendRedirect(response.encodeRedirectURL(buildRedirectURL(base, params)));
			return;
		}

		// action WEDITOBJ - change the object in the workflow	
		if (mytodo.equals("weditobj")) {
			if (!AI.checkPermission(mymcrid, "writedb")) {
				response.sendRedirect(response.encodeRedirectURL(getBaseURL() + usererrorpage));
				return;
			}
			sb = new StringBuffer();
			sb.append("file://").append(CONFIG.getString("MCR.editor_" + mytype + "_directory")).append('/').append(mymcrid).append(".xml");
			String base = getBaseURL() + myfile;
			Properties params = new Properties();
			params.put("XSL.editor.source.url", sb.toString());
			params.put("XSL.editor.cancel.url", getBaseURL() + cancelpage);
			params.put("mcrid", mymcrid);
			params.put("type", mytype);
			params.put("step", mystep);
			response.sendRedirect(response.encodeRedirectURL(buildRedirectURL(base, params)));
			return;
		}

		// action WDELOBJ - delete an object from the workflow
		if (mytodo.equals("wdelobj")) {
			if (!AI.checkPermission(mymcrid, "deletewf") && !AI.checkPermission(mymcrid, "deletedb")) {
				response.sendRedirect(response.encodeRedirectURL(getBaseURL() + usererrorpage));
				return;
			}
			WFM.deleteMetadataObject(mytype, mymcrid);
			List addr = WFM.getMailAddress(mytype, "wdelobj");
			StringBuffer text = new StringBuffer();
			text.append("Es wurde ein Objekt vom Typ ").append(mytype).append(" mit der ID ").append(mymcrid).append(" aus dem Workflow gelöscht.");
			mailToAddr(addr, text.toString());
			response.sendRedirect(response.encodeRedirectURL(getBaseURL() + workflowpage));
			return;
		}

		// action WDELDER - delete a derivate from the workflow
		if (mytodo.equals("wdelder")) {
			if (!AI.checkPermission(mymcrid, "deletewf") && !AI.checkPermission(mymcrid, "deletedb") && !AI.checkPermission(mymcrid2, "deletewf")) {
				response.sendRedirect(	response.encodeRedirectURL(	getBaseURL() + usererrorpage));
				return;
			}
			WFM.deleteDerivateObject(mytype, mymcrid2);		
			List addr = WFM.getMailAddress(mytype, "wdelder");
			StringBuffer text = new StringBuffer();
			text.append("Es wurde ein Derivat mit der ID ").append(mymcrid2).append(" aus dem Workflow gelöscht.");
			mailToAddr(addr, text.toString());
			response.sendRedirect(response.encodeRedirectURL(getBaseURL() + workflowpage));
			return;
		}

		// action WCOMMIT - commit a object from the workflow to the server
		if (mytodo.equals("wcommit")) {
			if (!AI.checkPermission(mymcrid, "commitdb")) {		
				response.sendRedirect(response.encodeRedirectURL(getBaseURL() + usererrorpage));
				return;
			}
			try {
				if (WFM.commitMetadataObject(mytype, mymcrid)) {
					WFM.deleteMetadataObject(mytype, mymcrid);
					List addr = WFM.getMailAddress(mytype, "wcommit");
					StringBuffer text = new StringBuffer();
					text.append("Es wurde ein Objekt vom Typ ").append(mytype).append(" mit der ID ").append(mymcrid).append(" aus dem Workflow in das System geladen.");
					mailToAddr(addr, text.toString());
					if ( mypage != null) {
						myfile = mypage;
					} else {
						sb = new StringBuffer("nav?path=~docdetail&id=").append(mymcrid);
						myfile = sb.toString();
					}					
				} else {
					myfile = storeerrorpage;
				}
			} catch (MCRException e) {
				myfile = storeerrorpage;
			}
			response.sendRedirect(	response.encodeRedirectURL(getBaseURL() + myfile));
			return;
		}

		// action WEDITDER in the database
		if (mytodo.equals("weditder")) {
			if (!AI.checkPermission(mymcrid, "writedb")) {
				response.sendRedirect(response.encodeRedirectURL(getBaseURL() + usererrorpage));
				return;
			}
			sb = new StringBuffer();
			sb.append(WFM.getDirectoryPath(mytype)).append(SLASH).append(mymcrid2).append(".xml");
			MCRDerivate der = new MCRDerivate();
			der.setFromURI(sb.toString());
			org.jdom.Element textfield = new org.jdom.Element("textfield");
			org.jdom.Element defa = new org.jdom.Element("default");
			defa.setText(der.getLabel());
			textfield.addContent(defa);
			MCRSessionMgr.getCurrentSession().put("weditder", textfield);
			sb = new StringBuffer();
			sb.append(getBaseURL()).append(pagedir).append("editor_form_editor-derivate.xml");
			Properties params = new Properties();
			params.put("XSL.editor.source.new", "true");
			params.put("XSL.editor.cancel.url", getBaseURL() + cancelpage);
			params.put("mcrid",  mymcrid);
			params.put("mcrid2", mymcrid2);
			params.put("type", mytype);
			params.put("step", mystep);
			response.sendRedirect(response.encodeRedirectURL(buildRedirectURL(sb.toString(), params)));
			return;
		}

		// action WSETLABEL in the database
		if (mytodo.equals("wsetlabel")) {
			extparm = "####label####" + extparm;
			mytodo = "wsetfile";
		}

		// action WSETFILE in the database
		if (mytodo.equals("wsetfile")) {
			if (!AI.checkPermission(mymcrid, "writedb")) {
				response.sendRedirect(	response.encodeRedirectURL(	getBaseURL() + usererrorpage));
				return;
			}
			sb = new StringBuffer();
			sb.append(WFM.getDirectoryPath(mytype)).append(SLASH).append(mymcrid2).append(".xml");
			MCRDerivate der = new MCRDerivate();
			der.setFromURI(sb.toString());
			if (extparm.startsWith("####main####")) {
				der.getDerivate().getInternals().setMainDoc(
						extparm.substring(mymcrid2.length() + 1 + 12, extparm.length()));
			}
			if (extparm.startsWith("####label####")) {
				der.setLabel(extparm.substring(13, extparm.length()));
			}
			byte[] outxml = MCRUtils.getByteArray(der.createXML());
			try {
				FileOutputStream out = new FileOutputStream(sb.toString());
				out.write(outxml);
				out.flush();
			} catch (IOException ex) {
				LOGGER.error("Exception while store to file " + sb.toString());
			}
			response.sendRedirect(response.encodeRedirectURL(getBaseURL() + workflowpage));
			return;
		}

		// action WDELFILE in the database
		if (mytodo.equals("wdelfile")) {
			if (!AI.checkPermission(mymcrid, "deletewf")) {
				response.sendRedirect(response.encodeRedirectURL(getBaseURL() + usererrorpage));
				return;
			}
			int all = 0;
			int i = extparm.indexOf("####nrall####");
			int j = 0;
			if (i != -1) {
				j = extparm.indexOf("####", i + 13);
				all = Integer.parseInt(extparm.substring(i + 13, j));
			}
			if (all > 1) {
				String derpath = WFM.getDirectoryPath(mytype);
				i = extparm.indexOf("####filename####");
				if (i != -1) {
					String filename = extparm.substring(i + 16, extparm.length());
					try {
						File fi = new File(derpath, filename);
						fi.delete();
					} catch (Exception ex) {
						LOGGER.warn("Can't remove file " + filename);
					}
				}
			}

			response.sendRedirect(response.encodeRedirectURL(getBaseURL() + workflowpage));
			return;
		}

		// action SDELOBJ from the database
		if (mytodo.equals("sdelobj")) {
			if (!AI.checkPermission(mymcrid, "deletedb")) {
				response.sendRedirect(	response.encodeRedirectURL(	getBaseURL() + usererrorpage));
				return;
			}
			saveDocumentToDeletedFolder(mymcrid);
			MCRObject obj = new MCRObject();
			try {
				obj.deleteFromDatastore(mymcrid);
				if ( mypage.length() > 0)	
					myfile = mypage;
				else	
					myfile = deletepage;
			} catch (Exception e) {
				myfile = deleteerrorpage;
			}
			List addr = WFM.getMailAddress(mytype, "sdelobj");
			StringBuffer text = new StringBuffer();
			text.append("Es wurde ein Objekt vom Typ ").append(mytype).append(" mit der ID ").append(mymcrid).append(" aus dem Server gelöscht.");
			mailToAddr(addr, text.toString());
			response.sendRedirect(response.encodeRedirectURL(getBaseURL() + myfile));
			return;
		}


		response.sendRedirect(	response.encodeRedirectURL(getBaseURL()));
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
	
	private void mailToAddr(List addr, String text) {
		if (addr.size() != 0) {
			String sender = WFM.getMailSender();
			String appl = CONFIG.getString("MCR.editor_mail_application_id", "DocPortal");
			String subject = "Automaticaly message from " + appl;
			LOGGER.info(text.toString());
			try {
				MCRMailer.send(sender, addr, subject, text.toString(),	false);
			} catch (Exception ex) {
				LOGGER.error("Can't send a mail to " + addr);
			}
		}
	}
	
	private void saveDocumentToDeletedFolder(String mymcrid){
		MCRObject mob = new MCRObject();
		mob.receiveFromDatastore(mymcrid);
		String savedir = CONFIG.getString("MCR.editor_deleted_directory");
		if ( savedir != null ) {
			JSPUtils.saveToDirectory(mob, savedir);
		} else {
			LOGGER.info(" MCR.editor_deleted_directory - ist in mycore.properties nicht gesetzt");
			LOGGER.info(" Es wurde keine Sicherheitskopie des gelöschten Objects angelegt!!");
		}
	}	

}
