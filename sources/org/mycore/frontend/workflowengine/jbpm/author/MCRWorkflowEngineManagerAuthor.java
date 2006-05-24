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

// package
package org.mycore.frontend.workflowengine.jbpm.author;

import java.io.FileOutputStream;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.jbpm.graph.exe.ExecutionContext;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.mycore.access.MCRAccessManager;
import org.mycore.common.MCRException;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.workflowengine.jbpm.MCRJbpmWorkflowBase;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerBaseImpl;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerInterface;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowProcess;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowUtils;
import org.mycore.services.fieldquery.MCRResults;
import org.mycore.user2.MCRUser;
import org.mycore.user2.MCRUserMgr;

/**
 * This class holds methods to manage the workflow file system of MyCoRe.
 * 
 * @author Heiko Helmbrecht, Anja Schaar
 * @version $Revision$ $Date$
 */

public class MCRWorkflowEngineManagerAuthor extends
		MCRWorkflowEngineManagerBaseImpl {
	public static final String varAUTHORID = "authorID";
	
	
	private static Logger logger = Logger
			.getLogger(MCRWorkflowEngineManagerAuthor.class.getName());
	private static String processType = "author";
	private static MCRWorkflowEngineManagerInterface singleton;
	private static boolean multipleInstancesAllowed = true;
	protected MCRWorkflowEngineManagerAuthor() throws Exception {

	}

	/**
	 * Returns the disshab workflow manager singleton.
	 * 
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static synchronized MCRWorkflowEngineManagerInterface instance()
			throws Exception {
		if (singleton == null)
			singleton = new MCRWorkflowEngineManagerAuthor();
		return singleton;
	}

	public long initWorkflowProcess(String initiator) throws MCRException {
		long processID = 0;
		MCRWorkflowProcess wfp = createWorkflowObject(processType);
		try {
			wfp.initialize(initiator);
			wfp.save();
			MCRUser user = MCRUserMgr.instance().retrieveUser(initiator);

			String email = user.getUserContact().getEmail();
			if (email != null && !email.equals("")) {
				wfp.setStringVariable(MCRJbpmWorkflowBase.varINITIATOREMAIL,
						email);
			}
			String salutation = user.getUserContact().getSalutation();
			if (salutation != null && !salutation.equals("")) {
				wfp.setStringVariable(
						MCRJbpmWorkflowBase.varINITIATORSALUTATION, salutation);
			}
			wfp.endTask("initialization", initiator,
					"go2isInitiatorsEmailAdressAvailable");
			processID = wfp.getProcessInstanceID();
		} catch (MCRException e) {
			logger
					.error(
							"MCRWorkflow Error, could not initialize the workflow process",
							e);
			throw new MCRException(
					"MCRWorkflow Error, could not initialize the workflow process");
		} finally {
			if (wfp != null)
				wfp.close();
		}

		return processID;
	}

	protected boolean areMultipleInstancesAllowed() {
		return multipleInstancesAllowed;
	}

	public String createNewAuthor(String userid, long pid) {
		String authorID = "";
		MCRWorkflowProcess wfp = getWorkflowObject(pid);
		try {
			if (wfp == null || !isUserValid(userid))
				return "";

			authorID = wfp.getStringVariable("authorID");
		} catch (MCRException ex) {
			logger.error("catched error", ex);
		} finally {
			if (wfp != null)
				wfp.close();
		}
		if (authorID != null && !authorID.equals("")) {
			return authorID;
		}

		authorID = createNewAuthor(userid, processType, false);
		return authorID;
	}

	public String createAuthorFromInitiator(String userid, long pid) {
		String authorID = "";
		MCRWorkflowProcess wfp = getWorkflowObject(pid);
		try {
			if (wfp == null || !isUserValid(userid))
				return "";

			authorID = wfp.getStringVariable("authorID");
		} catch (MCRException ex) {
			logger.error("catched error", ex);
		} finally {
			if (wfp != null)
				wfp.close();
		}
		if (authorID != null && !authorID.equals("")) {
			return authorID;
		}

		// im WF kein Autor vorhanden, - Direkt aus MyCore Holen
		// - kann nachher weg - da ja dann die AuthorID immmer im WF steht, dann
		// nur noch create zweig

		MCRResults mcrResult = MCRWorkflowUtils
				.queryMCRForAuthorByUserid(userid);
		logger.debug("Results found hits:" + mcrResult.getNumHits());
		if (mcrResult.getNumHits() > 0) {
			authorID = mcrResult.getHit(0).getID();
			return authorID;
		} else {
			authorID = createNewAuthor(userid, processType, true);
			return authorID;
		}
	}

	protected String createNewAuthor(String userid, String workflowProcessType,
			boolean isFillInUserData) {
		MCRUser user = null;

		try {
			user = MCRUserMgr.instance().retrieveUser(userid);
		} catch (Exception noUser) {
			// TODO Fehlermeldung
			logger.warn("user dos'nt exist userid=" + userid);
			return "";
		}

		String nextID = getNextFreeID("author");
		MCRObjectID id = new MCRObjectID(nextID);

		MCRObject author = null;

		if (isFillInUserData) {
			author = MCRWorkflowUtils.createAuthorFromUser(user, id);
		} else {
			author = MCRWorkflowUtils.createAuthorFromUser(null, id);
		}

		boolean result = saveMCRObjectFile(author, author.createXML()
				.getRootElement());
		if (!result) {
			return null;
		}

		// setDefaultPermissions(author.getId(), workflowProcessType,
		// user.getID());
		setDefaultPermissions(author.getId().getId(), user.getID());
		return author.getId().getId();
	}

	protected boolean saveMCRObjectFile(MCRObject object, Element xmlObject) {
		try {
			String type = object.getId().getTypeId();
			String savedir = getWorkflowDirectory(type);
			FileOutputStream fos = new FileOutputStream(savedir + "/"
					+ object.getId().getId() + ".xml");
			(new XMLOutputter(Format.getPrettyFormat())).output(xmlObject, fos);
			fos.close();
		} catch (Exception ex) {
			// TODO Fehlermeldung
			logger.warn("Could not create mycore object "
					+ object.getId().getId());
			logger.error(ex);
			return false;
		}
		return true;
	}

	public void setDefaultPermissions(String mcrid, String userid) {
		setDefaultPermissions(new MCRObjectID(mcrid), "author", userid);
	}

	public boolean commitWorkflowObject(String objmcrid, String documentType) {
		boolean bSuccess = super.commitWorkflowObject(objmcrid, documentType);
		// make it readable for all users
		if (bSuccess) {
			MCRAccessManager.getAccessImpl().addRule(objmcrid, "read",
					MCRAccessManager.getTrueRule(), "");
		}
		return bSuccess;
	}

	public String checkDecisionNode(long processid, String decisionNode,
			ExecutionContext executionContext) {
		MCRWorkflowProcess wfp = getWorkflowObject(processid);
		if (decisionNode.equals("canAuthorBeSubmitted")) {
			if (checkSubmitVariables(processid)) {
				return "authorCanBeSubmitted";
			} else {
				return "authorCantBeSubmitted";
			}
		}

		if (decisionNode.equals("canAuthorBeCommitted")) {
			if (checkSubmitVariables(processid)) {
				return "authorCanBeCommitted";
			} else {
				return "authorCantBeCommitted";
			}
		}
		
		if (decisionNode.equals("doesAuthorForUserExist")) {
			String userid = wfp.getStringVariable(MCRJbpmWorkflowBase.varINITIATOR);
			MCRResults mcrResult = MCRWorkflowUtils.queryMCRForAuthorByUserid(userid);
			logger.debug("Results found hits:" + mcrResult.getNumHits());
			if (mcrResult.getNumHits() > 0) {
				String authorID = mcrResult.getHit(0).getID();
				executionContext.setVariable(MCRWorkflowEngineManagerAuthor.varAUTHORID, authorID);			
//alternativ:
//				wfp.setStringVariable(MCRWorkflowEngineManagerAuthor.varAUTHORID, authorID);
//				wfp.close();
				return "authorForUserExists_yes";
			} else {
				return "authorForUserExists_no";
			}
		}
		return null;
	}

	private boolean checkSubmitVariables(long processid) {
		boolean ret = false;
		MCRWorkflowProcess wfp = getWorkflowObject(processid);
		try {
			String createdDocID = wfp.getStringVariable(varAUTHORID);
			if(createdDocID==null)createdDocID = wfp.getStringVariable("createdDocID");
				
			if (!isEmpty(createdDocID)) {
				String strDocValid = wfp.getStringVariable(VALIDPREFIX
						+ createdDocID);
				if (strDocValid != null && strDocValid.equals("true")) {
					ret = true;
				}
			}
		} catch (MCRException ex) {
			logger.error("catched error", ex);
		} finally {
			if (wfp != null)
				wfp.close();
		}
		return ret;
	}

	public void setWorkflowVariablesFromMetadata(String mcrid, Element metadata) {
		long pid = getUniqueWorkflowProcessFromCreatedDocID(mcrid);
		MCRWorkflowProcess wfp = getWorkflowObject(pid);
		try {
			Element name = metadata.getChild("names").getChild("name");
			StringBuffer sbTitle = new StringBuffer("");
			String first = name.getChildTextNormalize("firstname");
			String last = name.getChildTextNormalize("surname");
			String academic = name.getChildTextNormalize("academic");
			String prefix = name.getChildTextNormalize("prefix");
			String fullname=name.getChildTextNormalize("fullname");
			if(fullname!=null){
				sbTitle.append(fullname);
			}
			else{
				if(academic!=null){ sbTitle.append(academic); sbTitle.append(" ");}
				if(first!=null ){ sbTitle.append(first); sbTitle.append(" ");}
				if(prefix!=null){ sbTitle.append(prefix); sbTitle.append(" ");}
				if(last!=null ){ sbTitle.append(last); sbTitle.append("");}
			}
			
		
			wfp.setStringVariable("wfo-title", sbTitle.toString());
		} catch (MCRException ex) {
			logger.error("catched error", ex);
		} finally {
			if (wfp != null)
				wfp.close();
		}
	}
}
