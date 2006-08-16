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
package org.mycore.frontend.workflowengine.jbpm.institution;

// Imported java classes
import org.apache.log4j.Logger;
import org.jbpm.graph.exe.ExecutionContext;
import org.jdom.Element;
import org.mycore.common.JSPUtils;
import org.mycore.common.MCRException;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowAccessRuleEditorUtils;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowConstants;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManager;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowProcess;
import org.mycore.frontend.workflowengine.strategies.MCRMetadataStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRWorkflowDirectoryManager;
import org.mycore.user2.MCRUser;
import org.mycore.user2.MCRUserMgr;

/**
 * This class holds methods to manage the workflow file system of MyCoRe.
 * 
 * @author Robert Stephan
 * @version $Revision$ $Date$
 */

public class MCRWorkflowManagerInstitution extends MCRWorkflowManager {

	private static Logger logger = Logger
			.getLogger(MCRWorkflowManagerInstitution.class.getName());

	private static MCRWorkflowManager singleton;

	protected MCRWorkflowManagerInstitution() throws Exception {
		super("institution", "institution");
	}

	/**
	 * Returns the disshab workflow manager singleton.
	 * 
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static synchronized MCRWorkflowManager instance() throws Exception {
		if (singleton == null)
			singleton = new MCRWorkflowManagerInstitution();
		return singleton;
	}

	public long initWorkflowProcess(String initiator, String transitionName)
			throws MCRException {
		MCRWorkflowProcess wfp = createWorkflowProcess(workflowProcessType);
		try {
			wfp.initialize(initiator);
			wfp.save();
			MCRUser user = MCRUserMgr.instance().retrieveUser(initiator);
			String email = user.getUserContact().getEmail();
			if (email != null && !email.equals("")) {
				wfp.setStringVariable(
						MCRWorkflowConstants.WFM_VAR_INITIATOREMAIL, email);
			}
			
			wfp.endTask("taskInitialization", initiator, transitionName);
			return wfp.getProcessInstanceID();
		} catch (MCRException ex) {
			logger.error("MCRWorkflow Error, could not initialize the workflow process", ex);
			throw new MCRException("MCRWorkflow Error, could not initialize the workflow process");
		} finally {
			if (wfp != null)
				wfp.close();
		}
	}

	public String checkDecisionNode(long processid, String decisionNode,
			ExecutionContext executionContext) {
		if (decisionNode.equals("canInstitutionBeSubmitted")) {
			if (checkSubmitVariables(processid)) {
				return "canInstitutionBeSubmitted_yes";
			} else {
				return "canInstitutionBeSubmitted_no";
			}
		}

		if (decisionNode.equals("canInstitutionBeCommitted")) {
			if (checkSubmitVariables(processid)) {
				return "canInstitutionBeCommitted_yes";
			} else {
				return "canInstitutionBeCommitted_no";
			}
		}

		if (decisionNode.equals("canChangesBeCommitted")) {
			if (checkSubmitVariables(processid)) {
				return "canChangesBeCommitted_yes";
			} else {
				return "canChangesBeCommited_no";
			}
		}
		return null;
	}

	private boolean checkSubmitVariables(long processid) {
		boolean ret = false;
		MCRWorkflowProcess wfp = getWorkflowProcess(processid);
		try {
			String createdDocID = wfp
					.getStringVariable(MCRWorkflowConstants.WFM_VAR_METADATA_OBJECT_IDS);
			String strDocValid = wfp
					.getStringVariable(MCRMetadataStrategy.VALID_PREFIX
							+ createdDocID);
			if (strDocValid != null) {
				if (strDocValid.equals("true")) {
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

	public String createEmptyMetadataObject(long pid) {
		logger.warn("this is an empty method for workflowtype institution");
		return null;
	}

public String createNewInstitution(String userid, long processID) {
	MCRWorkflowProcess wfp = getWorkflowProcess(processID);	
	try {
			MCRObjectID institution = this.getNextFreeID(this.mainDocumentType);
			institution = institutionStrategy.createInstitution(institution, false);
			setDefaultPermissions(institution.getId(), userid, wfp.getContextInstance());
		
			return institution.getId();
		} catch (MCRException ex) {
			logger.error("an error occurred", ex);
		} finally {
			if (wfp != null)
				wfp.close();
		}
		return null;
	}

	public boolean commitWorkflowObject(long processID) {
		MCRWorkflowProcess wfp = getWorkflowProcess(processID);
		try {
			String documentID = wfp
					.getStringVariable(MCRWorkflowConstants.WFM_VAR_METADATA_OBJECT_IDS);
			String documentType = new MCRObjectID(documentID).getTypeId();
			if (!metadataStrategy.commitMetadataObject(documentID,
					MCRWorkflowDirectoryManager
							.getWorkflowDirectory(documentType))) {
				throw new MCRException("error in committing " + documentID);
			}
			permissionStrategy.setPermissions(documentID, null,	workflowProcessType,wfp.getContextInstance(), MCRWorkflowConstants.PERMISSION_MODE_PUBLISH);
			return true;
		} catch (MCRException ex) {
			logger.error("an error occurred", ex);
			wfp.setStringVariable("varnameERROR", ex.getMessage());						
		} finally {
			wfp.close();
		}
		return false;
	}

	public boolean removeWorkflowFiles(long processID) {
		MCRWorkflowProcess wfp = getWorkflowProcess(processID);
		String workflowDirectory = MCRWorkflowDirectoryManager.getWorkflowDirectory(mainDocumentType);
		try {
			metadataStrategy.removeMetadataFiles(wfp, workflowDirectory,deleteDir);
			return true;
		} catch (MCRException ex) {
			logger.error("could not delete workflow files", ex);
			wfp.setStringVariable("varnameERROR", ex.getMessage());						
		} finally {
			wfp.close();
		}
		return false;
	}

	public long initWorkflowProcessForEditing(String initiator, String mcrid ) throws MCRException {
		if (mcrid != null && MCRObject.existInDatastore(mcrid)) {
			// Store Object in Workflow - Filesystem
			MCRObject mob = new MCRObject();
			mob.receiveFromDatastore(mcrid);
			String type = mob.getId().getTypeId();
			JSPUtils.saveToDirectory(mob, MCRWorkflowDirectoryManager.getWorkflowDirectory(type));
			long processID = initWorkflowProcess(initiator,  "go2DisplayInstitutionData");
			MCRWorkflowProcess wfp = getWorkflowProcess(processID);
			wfp.setStringVariable(MCRWorkflowConstants.WFM_VAR_METADATA_OBJECT_IDS, mcrid);
			wfp.close();
			MCRWorkflowAccessRuleEditorUtils.setWorkflowVariablesForAccessRuleEditor(mcrid, processID);
			setWorkflowVariablesFromMetadata(mcrid, mob.createXML().getRootElement().getChild("metadata"), processID);
			setMetadataValid(mcrid, true, processID);
			return processID;

		} else {
			return -1;
		}
	}
	
	public void setWorkflowVariablesFromMetadata(String mcrid,
			Element metadata, long processID) {
		MCRWorkflowProcess wfp = getWorkflowProcess(processID);
		try {
			wfp.setStringVariable(MCRWorkflowConstants.WFM_VAR_WFOBJECT_TITLE, createWFOTitlefromMetadata(metadata));		
		} catch (MCRException ex) {
			logger.error("catched error", ex);
		} finally {
			if (wfp != null)
				wfp.close();
		}
	}
	private String createWFOTitlefromMetadata(Element metadata){
		Element name = metadata.getChild("names").getChild("name");
		
		return name.getChildText("fullname");
	}
}
