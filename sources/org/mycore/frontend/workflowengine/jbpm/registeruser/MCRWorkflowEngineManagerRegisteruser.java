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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.mycore.common.MCRException;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.frontend.cli.MCRObjectCommands;
import org.mycore.frontend.cli.MCRUserCommands2;
import org.mycore.frontend.workflowengine.jbpm.MCRJbpmWorkflowBase;

import org.mycore.frontend.workflowengine.jbpm.MCRJbpmWorkflowObject;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerBaseImpl;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerInterface;
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
			MCRJbpmWorkflowObject wfo = createWorkflowObject(processType);
			wfo.initialize(initiator);
			wfo.setStringVariable("userID",initiator);
			wfo.endTask("initialization", initiator, null);
			return wfo.getProcessInstanceID();
		}else{
			return processID;
		}
	}
	
	public void setWorkflowVariablesFromMetadata(String pID, Element userMetadata){
		Map map = new HashMap();
		long pid = Long.parseLong(pID);
		String salutation = userMetadata.getChild("user.contact").getChild("contact.salutation").getText();
		String firstname   = userMetadata.getChild("user.contact").getChild("contact.firstname").getText();
		String lastname    = userMetadata.getChild("user.contact").getChild("contact.lastname").getText();
		StringBuffer bname = new StringBuffer(salutation).append(" ").append(firstname).append(" ").append(lastname);
		map.put("email", userMetadata.getChild("user.contact").getChild("contact.email").getText());
		map.put("name", bname);
		map.put("institution", userMetadata.getChild("user.contact").getChild("contact.institution").getText());
		map.put("faculty", userMetadata.getChild("user.contact").getChild("contact.faculty").getText());		
		map.put("description", userMetadata.getChild("user.description").getText());
		
		setStringVariables(map, pid);
	}	
	
	protected MCRJbpmWorkflowObject getWorkflowObject(String userid) {
		long curProcessID = getUniqueCurrentProcessID(userid);
		if(curProcessID == 0){
			logger.warn("no " + processType + " workflow found for user " + userid);
			return null;
		}
		return getWorkflowObject(curProcessID);		
	}
	
	public void setUserIDFromWorkflow(String initiator, String userID){
		MCRJbpmWorkflowObject wfo = getWorkflowObject(initiator);
		wfo.setStringVariable("userID", userID);
	}		

	public String getUserIDFromWorkflow(String initiator){
		MCRJbpmWorkflowObject wfo = getWorkflowObject(initiator);
		String userID = wfo.getStringVariable("userID");
		if(wfo != null && userID != null && !userID.equals("")){
			return userID;
		}
		return "";
	}		
	
	public void setUserIDValidFlag(String userID, boolean isValid) {
		List lpids = MCRJbpmWorkflowBase.getCurrentProcessIDsForProcessVariable("userID%", userID);
		long pid =0;
		if(lpids == null || lpids.size() == 0){
			logger.error("setUserIDValidFlag: there could not be found a process with this createdDocID " + userID);
		}else{
			pid = ((Long)lpids.get(0)).longValue();
		}				
		if(pid > 0) {
			MCRJbpmWorkflowObject wfo = getWorkflowObject(pid);
			wfo.setStringVariable(VALIDPREFIX + userID, Boolean.toString(isValid));
		}
	}	

	public boolean commitWorkflowObject(String userid, String documentType) {
		boolean bSuccess = false;
		try{
			long pid = getUniqueCurrentProcessID(userid);
		
			MCRJbpmWorkflowObject wfo = getWorkflowObject(pid);
			String dirname = getWorkflowDirectory(documentType);
			String filename = dirname + File.separator + "user_" + userid + ".xml";
	
			try { 
				MCRUser u = new MCRUser();
		
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
}
