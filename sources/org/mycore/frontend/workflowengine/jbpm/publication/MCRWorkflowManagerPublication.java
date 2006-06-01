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
package org.mycore.frontend.workflowengine.jbpm.publication;

// Imported java classes
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jbpm.graph.exe.ExecutionContext;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.mycore.common.JSPUtils;
import org.mycore.common.MCRException;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowConstants;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManager;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowProcess;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowUtils;
import org.mycore.frontend.workflowengine.strategies.MCRMetadataStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRWorkflowDirectoryManager;
import org.mycore.user2.MCRUser;
import org.mycore.user2.MCRUserMgr;

/**
 * This class holds methods to manage the workflow file system of MyCoRe.
 * 
 * @author Heiko Helmbrecht, Anja Schaar
 * @version $Revision$ $Date$
 */

public class MCRWorkflowManagerPublication extends MCRWorkflowManager{
	
	
	private static Logger logger = Logger.getLogger(MCRWorkflowManagerPublication.class.getName());
	private static MCRWorkflowManager singleton;
	
	protected MCRWorkflowManagerPublication() throws Exception {
		super("document", "publication");
		this.derivateStrategy = new MCRDocumentDerivateStrategy();
	}

	
	/**
	 * Returns the disshab workflow manager singleton.
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static synchronized MCRWorkflowManager instance() throws Exception {
		if (singleton == null)
			singleton = new MCRWorkflowManagerPublication();
		return singleton;
	}
	
	
	
	public long initWorkflowProcess(String initiator, String transitionName) throws MCRException {
			MCRWorkflowProcess wfp = createWorkflowProcess(workflowProcessType);
			try{
				wfp.initialize(initiator);
				wfp.save();
				MCRUser user = MCRUserMgr.instance().retrieveUser(initiator);
				String email = user.getUserContact().getEmail();
				if(email != null && !email.equals("")){
					wfp.setStringVariable(MCRWorkflowConstants.WFM_VAR_INITIATOREMAIL, email);
				}
				String salutation = user.getUserContact().getSalutation();
				if(salutation != null && !salutation.equals("")){
					wfp.setStringVariable(MCRWorkflowConstants.WFM_VAR_INITIATORSALUTATION, salutation);
				}
				wfp.setStringVariable("fileCnt", "0");
				
				wfp.endTask("initialization", initiator, transitionName);
				// wfp.signal ("go2isInitiatorsEmailAddressAvailable");
				return wfp.getProcessInstanceID();
			}catch(MCRException ex){
				logger.error("MCRWorkflow Error, could not initialize the workflow process", ex);
				throw new MCRException("MCRWorkflow Error, could not initialize the workflow process");
			}finally{
				if(wfp != null)
					wfp.close();
			}				
	}
	
	
	public long initWorkflowProcessForEditing(String initiator, String mcrid ){
		if (mcrid != null && MCRObject.existInDatastore(mcrid)) {
			// Store Object in Workflow - Filesystem
			MCRObject mob = new MCRObject();
			mob.receiveFromDatastore(mcrid);
			String type = mob.getId().getTypeId();			
			JSPUtils.saveToDirectory(mob, MCRWorkflowDirectoryManager.getWorkflowDirectory(type));
			
			long processID = initWorkflowProcess(initiator, "go2processEditInitialized");
			MCRWorkflowProcess wfp = getWorkflowProcess(processID);
			String urn = this.identifierStrategy.getUrnFromDocument(mcrid);
			
			wfp.setStringVariable(MCRWorkflowConstants.WFM_VAR_METADATA_OBJECT_IDS, mcrid);
			wfp.setStringVariable(MCRWorkflowConstants.WFM_VAR_RESERVATED_URN, urn);	
			wfp.setStringVariable(MCRWorkflowConstants.WFM_VAR_ATTACHED_DERIVATES, "");
			wfp.close();

			setWorkflowVariablesFromMetadata(mcrid, mob.createXML().getRootElement().getChild("metadata"), processID);
			setMetadataValid(mcrid, true, processID);
			return processID;

		} else {
			return -1;
		}
	}
	
	public String checkDecisionNode(long processid, String decisionNode, ExecutionContext executionContext) {
		if(decisionNode.equals("canDocumentBeSubmitted")){
			if(checkSubmitVariables(processid)){
				return "documentCanBeSubmitted";
			}else{
				return "documentCantBeSubmitted";
			}
		}else if(decisionNode.equals("canDocumentBeCommitted")){
			if(checkSubmitVariables(processid)){
				return "go2wasCommitmentSuccessful";
			}else{
				return "go2sendBackToDocumentCreated";
			}
		}
		return null;
	}

	private boolean checkSubmitVariables(long processid){
		MCRWorkflowProcess wfp = getWorkflowProcess(processid);
		try{
//			String authorID = wfp.getStringVariable(MCRWorkflowConstants.WFM_VAR_AUTHOR_IDS);
			String reservatedURN = wfp.getStringVariable(MCRWorkflowConstants.WFM_VAR_RESERVATED_URN);
			String createdDocID = wfp.getStringVariable(MCRWorkflowConstants.WFM_VAR_METADATA_OBJECT_IDS);
//			String attachedDerivates = wfp.getStringVariable(MCRWorkflowConstants.WFM_VAR_ATTACHED_DERIVATES);
			if(!MCRWorkflowUtils.isEmpty(reservatedURN) && 	!MCRWorkflowUtils.isEmpty(createdDocID)){
				String strDocValid = wfp.getStringVariable(MCRMetadataStrategy.VALID_PREFIX + createdDocID );
				if(strDocValid != null ){
					if(strDocValid.equals("true") ){
						return true;
					}
				}
			}
		}catch(MCRException ex){
			logger.error("catched error", ex);
		}finally{
			if(wfp != null)
				wfp.close();
		}			
		return false;		
	}	
	
	public String createEmptyMetadataObject(long pid){
		MCRWorkflowProcess wfp = getWorkflowProcess(pid);
		try{
			MCRObjectID nextFreeId = getNextFreeID(metadataStrategy.getDocumentType());
			String initiator = wfp.getStringVariable(MCRWorkflowConstants.WFM_VAR_INITIATOR);
			String saveDirectory = MCRWorkflowDirectoryManager.getWorkflowDirectory(mainDocumentType);
			Map identifiers = new HashMap();
			identifiers.put(MCRWorkflowConstants.KEY_IDENTIFER_TYPE_URN, wfp.getStringVariable(MCRWorkflowConstants.WFM_VAR_RESERVATED_URN));
			if(metadataStrategy.createEmptyMetadataObject(false,null,null, 
					nextFreeId, initiator, identifiers, saveDirectory) ){
						permissionStrategy.setPermissions(nextFreeId.toString(), initiator, 
								getWorkflowProcessType(), MCRWorkflowConstants.PERMISSION_MODE_DEFAULT );
						return nextFreeId.toString();
			}
		}catch(MCRException ex){
			logger.error("could not create empty metadata object", ex);
		}finally{
			wfp.close();
		}
		return null;
	}	
	
	public void saveFiles(List files, String dirname, long pid) throws MCRException {		
		MCRWorkflowProcess wfp = getWorkflowProcess(pid);
		try{
			derivateStrategy.saveFiles(files, dirname, wfp);
		}catch(MCRException ex){
			
		}finally{
			wfp.close();
		}
	}
	
	public boolean commitWorkflowObject(long processID){
		MCRWorkflowProcess wfp = getWorkflowProcess(processID);
		try{
			String documentID = wfp.getStringVariable(MCRWorkflowConstants.WFM_VAR_METADATA_OBJECT_IDS);
			String documentType = new MCRObjectID(documentID).getTypeId();
			List derivateIDs = Arrays.asList(wfp.getStringVariable(MCRWorkflowConstants.WFM_VAR_ATTACHED_DERIVATES).split(","));
			if(!metadataStrategy.commitMetadataObject(documentID, MCRWorkflowDirectoryManager.getWorkflowDirectory(documentType))){
				throw new MCRException("error in committing " + documentID);
			}
			for (Iterator it = derivateIDs.iterator(); it.hasNext();) {
				String derivateID = (String) it.next();
				if ( derivateID != null && derivateID.length() > 0 ) {
					if(!derivateStrategy.commitDerivateObject(derivateID, MCRWorkflowDirectoryManager.getWorkflowDirectory(documentType))){
						throw new MCRException("error in committing " + derivateID);
					}
				}
			}
			return true;
		}catch(MCRException ex){
			logger.error("an error occurred", ex);
		}finally{
			wfp.close();
		}		
		return false;
	}
	
	public boolean removeWorkflowFiles(long processID){
		MCRWorkflowProcess wfp = getWorkflowProcess(processID);
		String workflowDirectory = MCRWorkflowDirectoryManager.getWorkflowDirectory(mainDocumentType);
		try{
			metadataStrategy.removeMetadataFiles(wfp, workflowDirectory, deleteDir);
			derivateStrategy.removeDerivates(wfp,workflowDirectory, deleteDir);
			return true;
		}catch(MCRException ex){
			logger.error("could not delete workflow files", ex);
		}finally{
			wfp.close();
		}
		return false;
	}
	
	public void setWorkflowVariablesFromMetadata(String mcrid, Element metadata, long processID){
		MCRWorkflowProcess wfp = getWorkflowProcess(processID);
		try{
			StringBuffer sbTitle = new StringBuffer("");
			for(Iterator it = metadata.getDescendants(new ElementFilter("title")); it.hasNext();){
				Element title = (Element)it.next();
				sbTitle.append(title.getText());
			}
			wfp.setStringVariable("wfo-title", sbTitle.toString());	
		}catch(MCRException ex){
			logger.error("catched error", ex);
		}finally{
			if(wfp != null)
				wfp.close();
		}			
	}
	
}
