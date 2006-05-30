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
package org.mycore.frontend.workflowengine.jbpm.registeruser;

// Imported java classes
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jbpm.graph.exe.ExecutionContext;
import org.jdom.Element;
import org.mycore.common.MCRException;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManager;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowProcess;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowUtils;
import org.mycore.frontend.workflowengine.strategies.MCRDefaultMetadataStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRDefaultUserStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRUserStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRWorkflowDirectoryManager;

/**
 * This class holds methods to manage the workflow file system of MyCoRe.
 * 
 * @author Heiko Helmbrecht, Anja Schaar
 * @version $Revision$ $Date$
 */

public class MCRWorkflowManagerRegisteruser extends MCRWorkflowManager{
	
	
	private static Logger logger = Logger.getLogger(MCRWorkflowManagerRegisteruser.class.getName());
	private static MCRWorkflowManager singleton;
	private MCRUserStrategy userStrategy;
	
	protected MCRWorkflowManagerRegisteruser() throws Exception {
		this.workflowProcessType = "registeruser";
		this.mainDocumentType = "user";
		this.userStrategy = new MCRDefaultUserStrategy(mainDocumentType);
		this.metadataStrategy = new MCRDefaultMetadataStrategy(mainDocumentType);
	}

	
	/**
	 * Returns the disshab workflow manager singleton.
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static synchronized MCRWorkflowManager instance() throws Exception {
		if (singleton == null)
			singleton = new MCRWorkflowManagerRegisteruser();
		return singleton;
	}
	
	
	
	public long initWorkflowProcess(String initiator, String transitionName) throws MCRException {
			
			List lpids = this.getCurrentProcessIDsForProcessType(initiator,workflowProcessType);			
			
			if(lpids.size() > 1){
				String errMsg = "there exists another workflow process of " + workflowProcessType + " for initiator " + initiator;
				logger.warn(errMsg);
				throw new MCRException(errMsg);
			}else if (lpids.size() == 0) {
				MCRWorkflowProcess wfp = createWorkflowProcess(workflowProcessType);
				
				try{
					wfp.initialize(initiator);
					wfp.save();
					wfp.setStringVariable("initiatorUserID",initiator);
					wfp.endTask("initialization", initiator, null);
					return wfp.getProcessInstanceID();
				}catch(MCRException ex){
					logger.error("MCRWorkflow Error, could not initialize the workflow process", ex);
					throw new MCRException("MCRWorkflow Error, could not initialize the workflow process");
				}finally{
					if(wfp != null)
						wfp.close();
				}			
			}else {
				return ((Long) lpids.get(0)).longValue();
			}
			
	}
	
	public String checkDecisionNode(long processid, String decisionNode, ExecutionContext executionContext) {
		String userid = getVariableValueInDecision("initiatorUserID", processid, executionContext);

		if(decisionNode.equals("canUserBeSubmitted")){
			if(checkSubmitVariables(processid)){
				return "go2userCanBeSubmitted";
			}else{
				return "go2userMustEdited";
			}
		}else if(decisionNode.equals("canUserBeRejected")){
			if ( !this.userStrategy.removeUserObject(userid, MCRWorkflowDirectoryManager.getWorkflowDirectory(this.mainDocumentType)) )
				setStringVariableInDecision("ROLLBACKERROR", Boolean.toString(true), processid,  executionContext);
			return "go2wasRejectmentSuccessful";
		}else if(decisionNode.equals("canUserBeCommitted")){
			if (! this.userStrategy.commitUserObject(userid, MCRWorkflowDirectoryManager.getWorkflowDirectory(this.mainDocumentType)) )
				setStringVariableInDecision("COMMITERROR", Boolean.toString(true), processid,  executionContext);
			return "go2wasCommitmentSuccessful";
		}
		return null;
	}

	private boolean checkSubmitVariables(long processid){
		boolean ret = false;		
		MCRWorkflowProcess wfp = getWorkflowProcess(processid);
		try{
			String group = wfp.getStringVariable("initiatorGroup");
			String email = wfp.getStringVariable("initiatorEmail");	
			ret =  !MCRWorkflowUtils.isEmpty(group);
			ret =  ret && (!MCRWorkflowUtils.isEmpty(email));			
		}catch(MCRException ex){
			logger.error("catched error", ex);
		}finally{
			if(wfp != null)
				wfp.close();
		}			
		return ret;
		
	}	
	
	public String createEmptyMetadataObject(long pid){
		return null;
	}	
		
	public boolean commitWorkflowObject(long processID){

		MCRWorkflowProcess wfp = getWorkflowProcess(processID);
		try{
			String userID = wfp.getStringVariable("initiatorUserID");
			if ( !this.userStrategy.commitUserObject(userID,MCRWorkflowDirectoryManager.getWorkflowDirectory(this.mainDocumentType))){
				throw new MCRException("error in committing a user" + userID);
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
		try{
			String userID = wfp.getStringVariable("initiatorUserID");
			userStrategy.removeUserObject(userID,MCRWorkflowDirectoryManager.getWorkflowDirectory(this.mainDocumentType));
			return true;
		}catch(MCRException ex){
			logger.error("could not delete workflow files", ex);
		}finally{
			wfp.close();
		}
		return false;
	}
	
	public void setWorkflowVariablesFromMetadata(String mcrid, Element userMetadata, long processID){		
		Map map = new HashMap();
		Element userContact = userMetadata.getChild("user.contact");
		if ( userContact != null ) {
			String salutation="", firstname="", lastname="";
			if (userContact.getChild("contact.salutation") != null)
				salutation  = userContact.getChild("contact.salutation").getText();
			if (userContact.getChild("contact.firstname") != null)
				firstname   = userContact.getChild("contact.firstname").getText();
			if (userContact.getChild("contact.lastname") != null)
				lastname    = userContact.getChild("contact.lastname").getText();
			StringBuffer bname = new StringBuffer(salutation).append(" ").append(firstname).append(" ").append(lastname);
			map.put("initiatorName", bname.toString());
			if (userContact.getChild("contact.email") != null)
				map.put("initiatorEmail", userContact.getChild("contact.email").getText());
			if (userContact.getChild("contact.institution") != null)
				map.put("initiatorInstitution", userContact.getChild("contact.institution").getText());
			if (userContact.getChild("contact.faculty") != null)
				map.put("initiatorFaculty", userContact.getChild("contact.faculty").getText());
		}
		if ( userMetadata.getChild("user.description") != null)
			map.put("initiatorIntend", userMetadata.getChild("user.description").getText());
		
		if ( userMetadata.getChild("user.groups") != null){
			List groups = userMetadata.getChild("user.groups").getChildren();
			StringBuffer sGroups = new StringBuffer("");
			for ( int i=0; i < groups.size(); i++){
				 Element eG = (Element)groups.get(i);
				 if ( !eG.getText().equalsIgnoreCase("gastgroup"))
					 sGroups.append(eG.getText()).append(" ");
			}
			map.put("initiatorGroup", sGroups.toString());
		}
		setStringVariableMap(map, processID);		
	}
	
}
