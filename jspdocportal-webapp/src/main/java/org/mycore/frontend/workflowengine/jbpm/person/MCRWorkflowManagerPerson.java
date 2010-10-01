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
package org.mycore.frontend.workflowengine.jbpm.person;

// Imported java classes
import java.text.MessageFormat;
import java.util.PropertyResourceBundle;

import org.apache.log4j.Logger;
import org.jbpm.context.exe.ContextInstance;
import org.mycore.common.JSPUtils;
import org.mycore.common.MCRException;
import org.mycore.datamodel.metadata.MCRMetadataManager;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.workflowengine.guice.MCRAuthorWorkflowModule;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowAccessRuleEditorUtils;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowConstants;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManager;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowProcess;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowProcessManager;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowUtils;
import org.mycore.frontend.workflowengine.strategies.MCRAuthorStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRMetadataStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRWorkflowDirectoryManager;
import org.mycore.services.fieldquery.MCRFieldDef;
import org.mycore.services.fieldquery.MCRQuery;
import org.mycore.services.fieldquery.MCRQueryCondition;
import org.mycore.services.fieldquery.MCRQueryManager;
import org.mycore.services.fieldquery.MCRResults;
import org.mycore.user.MCRUser;
import org.mycore.user.MCRUserMgr;

import com.google.inject.Guice;
import com.google.inject.Inject;

/**
 * This class holds methods to manage the workflow file system of MyCoRe.
 * 
 * @author Heiko Helmbrecht, Anja Schaar
 * @version $Revision$ $Date$
 */

public class MCRWorkflowManagerPerson extends MCRWorkflowManager {
	
	private static Logger logger = Logger
			.getLogger(MCRWorkflowManagerPerson.class.getName());

	private static MCRWorkflowManager singleton;

	@Inject MCRAuthorStrategy authorStrategy;
	
	
	protected MCRWorkflowManagerPerson() throws Exception {
		super("person", "person");
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
			singleton = Guice.createInjector(new MCRAuthorWorkflowModule()).getInstance(MCRWorkflowManager.class);
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
			String salutation = user.getUserContact().getSalutation();
			if (salutation != null && !salutation.equals("")) {
				wfp.setStringVariable(
						MCRWorkflowConstants.WFM_VAR_INITIATORSALUTATION,
						salutation);
			}
			wfp.endTask("initialization", initiator, transitionName);
			return wfp.getProcessInstanceID();
		} catch (MCRException ex) {
			logger.error("MCRWorkflow Error, could not initialize the workflow process",ex);
			throw new MCRException(	"MCRWorkflow Error, could not initialize the workflow process");
		} finally {
			if (wfp != null)
				wfp.close();
		}
	}

	public String checkDecisionNode(String decisionNode, ContextInstance ctxI) {
		if (decisionNode.equals("canPersonBeSubmitted")) { 
			boolean canDo = checkSubmitVariables(ctxI);
            String existsMessage = doesAuthorWithSameNameExist((String)ctxI.getVariable(MCRWorkflowConstants.WFM_VAR_WFOBJECT_TITLE),
                                                               (String)ctxI.getVariable(MCRWorkflowConstants.WFM_VAR_METADATA_OBJECT_IDS));
            if(existsMessage!=null){
                ctxI.setVariable(MCRWorkflowConstants.WFM_VAR_HINT, existsMessage);
            }
            else{
                ctxI.deleteVariable(MCRWorkflowConstants.WFM_VAR_HINT);
            }
            if (canDo) {
				return "personCanBeSubmitted";
			} else {
				return "personCantBeSubmitted";
			}
		}

		if (decisionNode.equals("canPersonBeCommitted")) {
			if (checkSubmitVariables(ctxI)) {
				return "personCanBeCommitted";
			} else {
				return "personCantBeCommitted";
			}
		}

		if (decisionNode.equals("doesPersonForUserExist")) {
			
			String userid = (String) ctxI.getVariable(MCRWorkflowConstants.WFM_VAR_INITIATOR);
			MCRResults mcrResult = MCRWorkflowUtils.queryMCRForAuthorByUserid(userid);
			logger.debug("Results found hits:" + mcrResult.getNumHits());
			if (mcrResult.getNumHits() > 0) {
				String createdDocID = mcrResult.getHit(0).getID();
				ctxI.setVariable(
				   MCRWorkflowConstants.WFM_VAR_METADATA_OBJECT_IDS,
				   createdDocID);
				// cannot be used in decision handlers - persistence problems with jbpm
				// wfp.setStringVariable(MCRWorkflowConstants.WFM_VAR_METADATA_OBJECT_IDS, createdDocID);
							
				MCRObject mob = MCRMetadataManager.retrieveMCRObject(MCRObjectID.getInstance(createdDocID));
				setWorkflowVariablesFromMetadata(ctxI, mob.createXML().getRootElement().getChild("metadata"));
				// cannot be used in decision handlers - persistence problems with jbpm
				// setWorkflowVariablesFromMetadata(createdDocID, mob.createXML()
				//	.getRootElement().getChild("metadata"), processid);

				return "personForUserExists_yes";
			} else {
				return "personForUserExists_no";
			}
		}

		if (decisionNode.equals("canChangesBeCommitted")) {
			if (checkSubmitVariables(ctxI)) {
				return "changesCanBeCommitted";
			} else {
				return "changesCantBeCommitted";
			}
		}
		return null;
	}

	private boolean checkSubmitVariables(ContextInstance ctxI) {
		boolean ret = false;
		try {
			String createdDocID = (String) ctxI.getVariable(MCRWorkflowConstants.WFM_VAR_METADATA_OBJECT_IDS);
			if (createdDocID == null)
				createdDocID = (String) ctxI.getVariable(MCRWorkflowConstants.WFM_VAR_METADATA_OBJECT_IDS);
			String strDocValid = (String) ctxI.getVariable(MCRMetadataStrategy.VALID_PREFIX
							+ createdDocID);
			if (strDocValid != null) {
				if (strDocValid.equals("true")) {
					ret = true;
				}
			}
		} catch (MCRException ex) {
			logger.error("catched error", ex);
		}
		return ret;
	}

	public String createEmptyMetadataObject(ContextInstance ctxI) {
		logger.warn("this is an empty method for workflowtype author");
		return null;
	}

public String createNewAuthor(String userid, ContextInstance ctxI,
			boolean isFillInUserData) {
		
	try {
			MCRObjectID author = this.getNextFreeID(this.mainDocumentType);
			author = authorStrategy.createAuthor(userid, author,
					isFillInUserData, false);
	//		setStringVariable(MCRWorkflowConstants.WFM_VAR_METADATA_OBJECT_IDS, author.getId(), processID);
			setDefaultPermissions(author.toString(), userid, ctxI);
			return author.toString();
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
					MCRWorkflowDirectoryManager.getWorkflowDirectory(documentType))) {
				throw new MCRException("error in committing " + documentID);
			}
			permissionStrategy.setPermissions(documentID, null,	workflowProcessType,ctxI, MCRWorkflowConstants.PERMISSION_MODE_PUBLISH);
			return true;
		} catch (MCRException ex) {
			logger.error("an error occurred", ex);
			ctxI.setVariable("varnameERROR", ex.getMessage());			
		} finally {
			
		}
		return false;
	}

	public boolean removeWorkflowFiles(ContextInstance ctxI) {
		//since author database objects are created when the user submits the author, that he can move on
		//we must try to delete this database object when the workflow process is killed
		//
		if(ctxI.hasVariable(MCRWorkflowConstants.WFM_VAR_BOOL_TEMPORARY_IN_DATABASE)
             &&((Boolean)ctxI.getVariable(MCRWorkflowConstants.WFM_VAR_BOOL_TEMPORARY_IN_DATABASE)).booleanValue()){
			String createdDocID = (String) ctxI.getVariable(MCRWorkflowConstants.WFM_VAR_METADATA_OBJECT_IDS);
			try{
				MCRMetadataManager.deleteMCRObject(MCRObjectID.getInstance(createdDocID));
			}
			 catch (Exception ex) {
					logger.error("could not delete workflow files", ex);
					ctxI.setVariable("varnameERROR", ex.getMessage());
			 }
		}		
		
		String workflowDirectory = MCRWorkflowDirectoryManager
				.getWorkflowDirectory(mainDocumentType);
		try {
			metadataStrategy.removeMetadataFiles(ctxI, workflowDirectory, deleteDir);
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
			long processID = initWorkflowProcess(initiator,  "go2DisplayPersonData");
			MCRWorkflowProcess wfp = MCRWorkflowProcessManager.getInstance().getWorkflowProcess(processID);
			try{
			wfp.setStringVariable(MCRWorkflowConstants.WFM_VAR_METADATA_OBJECT_IDS, mcrid);
			
			MCRWorkflowAccessRuleEditorUtils.setWorkflowVariablesForAccessRuleEditor(mcrid, wfp.getContextInstance());
			setWorkflowVariablesFromMetadata(wfp.getContextInstance(), mob.createXML().getRootElement().getChild("metadata"));
			setMetadataValid(mcrid, true, wfp.getContextInstance());
			return processID;
			}
			catch(Exception e){
				logger.error("catched exception: ",e);
				return -1;
			}
			finally{
				wfp.close();
				
			}

		} else {
			return -1;
		}
	}
    
      /**
     * @param name - the fullname of the author
     * @return The message that should be displayed or
     *          <i>null</i> if there is no author.
     */
    private String doesAuthorWithSameNameExist(String name, String mcrid){
        //query="fullname like \""+name+"\"";
        if (name==null) return null;
        MCRFieldDef field = MCRFieldDef.getDef("fullname");
        MCRQuery query = new MCRQuery(new MCRQueryCondition(field, "=", name));
        MCRResults mcrResult = MCRQueryManager.search(query); 
        if(mcrResult.getNumHits()>0){
            String hitid = mcrResult.getHit(0).getID();
            if(hitid.equals(mcrid)) return null;
            String tt = PropertyResourceBundle.getBundle("messages").getString("WF.author.PersonAllreadyExists");
            return MessageFormat.format( tt, new Object[]{hitid});
        }        
        return null;
    }
}
