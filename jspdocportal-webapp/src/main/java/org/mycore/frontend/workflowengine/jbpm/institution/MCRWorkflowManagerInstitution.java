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
import org.jbpm.context.exe.ContextInstance;
import org.mycore.common.JSPUtils;
import org.mycore.common.MCRException;
import org.mycore.datamodel.metadata.MCRMetadataManager;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.workflowengine.guice.MCRInstitutionWorkflowModule;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowAccessRuleEditorUtils;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowConstants;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManager;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowProcess;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowProcessManager;
import org.mycore.frontend.workflowengine.strategies.MCRInstitutionStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRMetadataStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRWorkflowDirectoryManager;
import org.mycore.user2.MCRUser;
import org.mycore.user2.MCRUserManager;

import com.google.inject.Guice;
import com.google.inject.Inject;

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

	@Inject protected MCRMetadataStrategy metadataStrategy;
	@Inject protected MCRInstitutionStrategy institutionStrategy;

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
			singleton = Guice.createInjector(new MCRInstitutionWorkflowModule()).getInstance(MCRWorkflowManager.class);
		return singleton;
	}

	public long initWorkflowProcess(String initiator, String transitionName)
			throws MCRException {
		MCRWorkflowProcess wfp = createWorkflowProcess(workflowProcessType);
		try {
			wfp.initialize(initiator);
			wfp.save();
			MCRUser user = MCRUserManager.getUser(initiator);
			String email = user.getEMailAddress();
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

	public String checkDecisionNode(String decisionNode, ContextInstance ctxI) {
		if (decisionNode.equals("canInstitutionBeSubmitted")) {
			if (checkSubmitVariables(ctxI)) {
				return "canInstitutionBeSubmitted_yes";
			} else {
				return "canInstitutionBeSubmitted_no";
			}
		}

		if (decisionNode.equals("canInstitutionBeCommitted")) {
			if (checkSubmitVariables(ctxI)) {
				return "canInstitutionBeCommitted_yes";
			} else {
				return "canInstitutionBeCommitted_no";
			}
		}

		if (decisionNode.equals("canChangesBeCommitted")) {
			if (checkSubmitVariables(ctxI)) {
				return "canChangesBeCommitted_yes";
			} else {
				return "canChangesBeCommited_no";
			}
		}
		return null;
	}

	private boolean checkSubmitVariables(ContextInstance ctxI) {
		boolean ret = false;
		try {
			String createdDocID = (String) ctxI.getVariable(MCRWorkflowConstants.WFM_VAR_METADATA_OBJECT_IDS);
			String strDocValid = (String) ctxI.getVariable(MCRMetadataStrategy.VALID_PREFIX
							+ createdDocID);
			if (strDocValid != null) {
				if (strDocValid.equals("true")) {
					ret = true;
				}
			}
		} catch (MCRException ex) {
			logger.error("catched error", ex);
		} finally {
		
		}
		return ret;
	}

	public String createEmptyMetadataObject(ContextInstance ctxI) {
		logger.warn("this is an empty method for workflowtype institution");
		return null;
	}

public String createNewInstitution(String userid, ContextInstance ctxI) {
	try {
			MCRObjectID institution = this.getNextFreeID(this.mainDocumentType);
			institution = institutionStrategy.createInstitution(institution, false);
			setDefaultPermissions(institution.toString(), userid, ctxI);
		
			return institution.toString();
		} catch (MCRException ex) {
			logger.error("an error occurred", ex);
		} finally {
		}
		return null;
	}

	public boolean commitWorkflowObject(ContextInstance ctxI) {
		
		try {
			String documentID = (String) ctxI.getVariable(MCRWorkflowConstants.WFM_VAR_METADATA_OBJECT_IDS);
			String documentType = MCRObjectID.getInstance(documentID).getTypeId();
			if (!metadataStrategy.commitMetadataObject(documentID,
					MCRWorkflowDirectoryManager
							.getWorkflowDirectory(documentType))) {
				throw new MCRException("error in committing " + documentID);
			}
			permissionStrategy.setPermissions(documentID, null,	workflowProcessType, ctxI, MCRWorkflowConstants.PERMISSION_MODE_PUBLISH);
			return true;
		} catch (MCRException ex) {
			logger.error("an error occurred", ex);
			ctxI.setVariable("varnameERROR", ex.getMessage());						
		} finally {
		
		}
		return false;
	}

	public boolean removeWorkflowFiles(ContextInstance ctxI) {
		
		String workflowDirectory = MCRWorkflowDirectoryManager.getWorkflowDirectory(mainDocumentType);
		try {
			metadataStrategy.removeMetadataFiles(ctxI, workflowDirectory,deleteDir);
			return true;
		} catch (MCRException ex) {
			logger.error("could not delete workflow files", ex);
			ctxI.setVariable("varnameERROR", ex.getMessage());						
		} finally {
			
		}
		return false;
	}

	public long initWorkflowProcessForEditing(String initiator, String mcrid ) throws MCRException {
		if (mcrid != null && MCRMetadataManager.exists(MCRObjectID.getInstance(mcrid))) {
			// Store Object in Workflow - Filesystem
			MCRObject mob = MCRMetadataManager.retrieveMCRObject(MCRObjectID.getInstance(mcrid));
			String type = mob.getId().getTypeId();
			JSPUtils.saveToDirectory(mob, MCRWorkflowDirectoryManager.getWorkflowDirectory(type));
			long processID = initWorkflowProcess(initiator,  "go2DisplayInstitutionData");
			MCRWorkflowProcess wfp = MCRWorkflowProcessManager.getInstance().getWorkflowProcess(processID);
			wfp.setStringVariable(MCRWorkflowConstants.WFM_VAR_METADATA_OBJECT_IDS, mcrid);
			MCRWorkflowAccessRuleEditorUtils.setWorkflowVariablesForAccessRuleEditor(mcrid, wfp.getContextInstance());
			setWorkflowVariablesFromMetadata(wfp.getContextInstance(), mob.createXML().getRootElement().getChild("metadata"));
		
			setMetadataValid(mcrid, true, wfp.getContextInstance());
			wfp.close();
			return processID;

		} else {
			return -1;
		}
	}
}
