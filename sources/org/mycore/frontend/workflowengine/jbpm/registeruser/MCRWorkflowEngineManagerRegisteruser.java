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


import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jbpm.graph.exe.ExecutionContext;
import org.jdom.Element;
import org.mycore.common.MCRException;
import org.mycore.frontend.cli.MCRUserCommands2;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerBaseImpl;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerInterface;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowProcess;
import org.mycore.user2.MCRUser;
import org.mycore.user2.MCRUserMgr;

/**
 * This class holds methods to manage the workflow file system of MyCoRe.
 * 
 * @author Heiko Helmbrecht, Anja Schaar
 * @version $Revision$ $Date$
 */

public class MCRWorkflowEngineManagerRegisteruser extends MCRWorkflowEngineManagerBaseImpl{
	
	
	private static Logger logger = Logger.getLogger(MCRWorkflowEngineManagerRegisteruser.class.getName());
	private static String processType = "registeruser" ;
	private static MCRWorkflowEngineManagerInterface singleton;
	private static String documentType = "user";
	protected MCRWorkflowEngineManagerRegisteruser() throws Exception {	}

	
	/**
	 * Returns the disshab workflow manager singleton.
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static synchronized MCRWorkflowEngineManagerInterface instance() throws Exception {
		if (singleton == null)
			singleton = new MCRWorkflowEngineManagerRegisteruser();
		return singleton;
	}
	
	public long initWorkflowProcess(String initiator) throws MCRException {
		long processID = getUniqueCurrentProcessID(initiator);
		if(processID < 0){
			String errMsg = "there exists another workflow process of " + processType + " for initiator " + initiator;
			logger.warn(errMsg);
			throw new MCRException(errMsg);
		}else if (processID == 0) {
			MCRWorkflowProcess wfp = createWorkflowObject(processType);
			try{
				wfp.initialize(initiator);
				wfp.save();
				wfp.setStringVariable("initiatorUserID",initiator);
				wfp.endTask("initialization", initiator, null);
				return wfp.getProcessInstanceID();
			}catch(MCRException ex){
				logger.error("catched error", ex);
				return -1;
			}finally{
				if(wfp != null)
					wfp.close();
			}				
		}else{
			return processID;
		}
	}
	
	public void setWorkflowVariablesFromMetadata(String pID, Element userMetadata){
		Map map = new HashMap();
		long pid = Long.parseLong(pID);
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
		setStringVariables(map, pid);
	}	
	
	protected MCRWorkflowProcess getWorkflowObject(String userid) {
		long curProcessID = getUniqueCurrentProcessID(userid);
		if(curProcessID == 0){
			logger.warn("no " + processType + " workflow found for user " + userid);
			return null;
		}
		return getWorkflowObject(curProcessID);		
	}
	
	public String checkDecisionNode(long processid, String decisionNode, ExecutionContext executionContext) {
		String userid = getVariableValueInDecision("initiatorUserID", processid, decisionNode, executionContext);

		if(decisionNode.equals("canUserBeSubmitted")){
			if(checkSubmitVariables(processid)){
				return "go2userCanBeSubmitted";
			}else{
				return "go2userMustEdited";
			}
		}else if(decisionNode.equals("canUserBeRejected")){
			if (! this.rollbackWorkflowObject(userid))
				setStringVariableInDecision("ROLLBACKERROR", Boolean.toString(true), processid, decisionNode, executionContext);
			return "go2wasRejectmentSuccessful";
		}else if(decisionNode.equals("canUserBeCommitted")){
			if (! this.commitWorkflowObject(userid, "user")) 
				setStringVariableInDecision("COMMITERROR", Boolean.toString(true), processid, decisionNode, executionContext);
			return "go2wasCommitmentSuccessful";
		}
		return null;
	}
	
	private boolean checkSubmitVariables(long processid){
		boolean ret = false;
		MCRWorkflowProcess wfp = getWorkflowObject(processid);
		try{
			String group = wfp.getStringVariable("initiatorGroup");
			String email = wfp.getStringVariable("initiatorEmail");				

			ret =  (!isEmpty(group+email));
		}catch(MCRException ex){
			logger.error("catched error", ex);
		}finally{
			if(wfp != null)
				wfp.close();
		}
		return ret;
	}
	
	
	public boolean commitWorkflowObject(String userid, String dummypar) {
		boolean bSuccess = false;
		try{
			String dirname = getWorkflowDirectory(documentType);
			String filename = dirname + File.separator + "user_" + userid + ".xml";
	
			try { 
				if ( MCRUserMgr.instance().existUser(userid) ) {
					MCRUserCommands2.updateUserFromFile(filename);
				} else {
					MCRUserCommands2.createUserFromFile(filename);
				}
				logger.info("The user object: " + filename + " is loaded.");
			} catch (Exception ig){ 
				logger.error("Can't load File catched error: ", ig);
				bSuccess=false;
			}
		}catch(Exception e){
			logger.error("could not commit user");
			bSuccess = false;
		}
		return bSuccess;
	}
	
	public boolean rollbackWorkflowObject(String userid) {
		boolean bSuccess = false;
		try{
			String dirname = getWorkflowDirectory(documentType);
			String filename = dirname + File.separator + "user_" + userid + ".xml";	
			try {
				File ff = new File (filename);
				if ( ff.exists()) 
					bSuccess = ff.delete();
			} catch (Exception ig){ 
				logger.error("Can't load File catched error: ", ig);
				bSuccess=false;
			}
			logger.info("loading user object: " + filename + " success=" + bSuccess);			
		}catch(Exception e){
			logger.error("could not commit user");
			bSuccess = false;
		}
		return bSuccess;
	}
	
	public boolean endTask(long processid, String taskName, String transitionName){
		boolean ret = false;
		MCRWorkflowProcess wfp = getWorkflowObject(processid);
		try{
			MCRUser user = MCRUserMgr.instance().getCurrentUser();
			ret = wfp.endTask(taskName, user.getID(), transitionName);
		}catch(MCRException ex){
			logger.error("catched error", ex);
		}finally{
			if(wfp != null)
				wfp.close();
		}
		return ret;
	}
	
	public long getUniqueCurrentProcessID(String userid) {
		List li = getCurrentProcessIDs(userid, processType);
		if(li != null && li.size() > 0) {
			if(li.size() > 1) {
				StringBuffer errorSB = new StringBuffer("there are existing more than one ")
					.append(processType).append(" processIDs. Please delete old ones. Found these ")
					.append("processids: ");
				for (Iterator it = li.iterator(); it.hasNext();) {
					Long processID = (Long) it.next();
					errorSB.append("[").append(processID.longValue()).append("] ");
				}
				logger.error(errorSB.toString());
				return -1;
			}else{
				return ((Long)li.get(0)).longValue();
			}
			
		}else{
			return 0;
		}
	}		

}
