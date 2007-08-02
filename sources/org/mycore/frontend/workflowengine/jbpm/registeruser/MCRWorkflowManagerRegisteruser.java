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
import java.util.List;

import org.apache.log4j.Logger;
import org.jbpm.context.exe.ContextInstance;
import org.jdom.Element;
import org.mycore.common.MCRException;
import org.mycore.frontend.cli.MCRUserCommands;
import org.mycore.frontend.workflowengine.guice.MCRRegisteruserWorkflowModule;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManager;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowProcess;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowProcessManager;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowUtils;
import org.mycore.frontend.workflowengine.strategies.MCRUserStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRWorkflowDirectoryManager;

import com.google.inject.Guice;
import com.google.inject.Inject;

/**
 * This class holds methods to manage the workflow file system of MyCoRe.
 * 
 * @author Heiko Helmbrecht, Anja Schaar
 * @version $Revision$ $Date$
 */

public class MCRWorkflowManagerRegisteruser extends MCRWorkflowManager{
	
	
	private static Logger logger = Logger.getLogger(MCRWorkflowManagerRegisteruser.class.getName());
	private static MCRWorkflowManager singleton;
	
	@Inject protected MCRUserStrategy userStrategy;
	protected MCRWorkflowManagerRegisteruser() throws Exception {
		super("user", "registeruser");
		
	}

	/**
	 * Returns the registeruser workflow manager singleton.
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static synchronized MCRWorkflowManager instance() throws Exception {
		if (singleton == null)
			singleton = Guice.createInjector(new MCRRegisteruserWorkflowModule()).getInstance(MCRWorkflowManager.class);
		return singleton;
	}
	
	
	public  void setWorkflowVariablesFromMetadata(ContextInstance ctxI, Element metadata){		
		userStrategy.setWorkflowVariablesFromMetadata( ctxI, metadata);
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
	/**
	 * TODO look into MCRWorkflowManagerAuthor for further implementation details
	 */
	public long initWorkflowProcessForEditing(String initiator, String mcrid){		
		return initWorkflowProcess(initiator, "");
	}
	
	public String checkDecisionNode(String decisionNode, ContextInstance ctxI) {
		String userid = (String) ctxI.getVariable("initiatorUserID");

		if(decisionNode.equals("canUserBeSubmitted")){
			if(checkSubmitVariables(ctxI) && 
				this.userStrategy.checkMetadata(userid,MCRWorkflowDirectoryManager.getWorkflowDirectory(this.mainDocumentType))){				
				return "go2userCanBeSubmitted";
			}else{
				return "go2userMustEdited";
			}
		}else if(decisionNode.equals("canUserBeRejected")){
			if ( !this.userStrategy.removeUserObject(userid, MCRWorkflowDirectoryManager.getWorkflowDirectory(this.mainDocumentType)) )
				ctxI.setVariable("ROLLBACKERROR", Boolean.toString(true));
			return "go2wasRejectmentSuccessful";
		}else if(decisionNode.equals("canUserBeCommitted")){
			if (! this.userStrategy.commitUserObject(userid, MCRWorkflowDirectoryManager.getWorkflowDirectory(this.mainDocumentType)) )
				ctxI.setVariable("COMMITERROR", Boolean.toString(true));
			return "go2wasCommitmentSuccessful";
		}
		return null;
	}

	private boolean checkSubmitVariables(ContextInstance ctxI){
		boolean ret = false;		
		try{
			String group = (String) ctxI.getVariable("initiatorGroup");
			String email = (String) ctxI.getVariable("initiatorEmail");
			String pwd	 = (String) ctxI.getVariable("initiatorPwd");	
			ret =  !MCRWorkflowUtils.isEmpty(group);
			ret =  ret && (!MCRWorkflowUtils.isEmpty(email));			
			ret =  ret && (!MCRWorkflowUtils.isEmpty(pwd));
		}catch(MCRException ex){
			logger.error("catched error", ex);
		}finally{

		}			
		return ret;
		
	}	
	
	public String createEmptyMetadataObject(ContextInstance ctxI){
		return null;
	}	
		
	public boolean commitWorkflowObject(ContextInstance ctxI){
		try{
			String userID = (String) ctxI.getVariable("initiatorUserID");
			if ( !this.userStrategy.commitUserObject(userID,MCRWorkflowDirectoryManager.getWorkflowDirectory(this.mainDocumentType))){
				throw new MCRException("error in committing a user" + userID);
			}
			return true;
		}catch(MCRException ex){
			logger.error("an error occurred", ex);
			ctxI.setVariable("varnameERROR", ex.getMessage());									
		}finally{

		}		
		return false;
	}
	
	public boolean removeWorkflowFiles(ContextInstance ctxI){		
		try{
			String userID = (String) ctxI.getVariable("initiatorUserID");
			userStrategy.removeUserObject(userID,MCRWorkflowDirectoryManager.getWorkflowDirectory(this.mainDocumentType));
			return true;
		}catch(MCRException ex){
			logger.error("could not delete workflow files", ex);
			ctxI.setVariable("varnameERROR", ex.getMessage());						
		}finally{
		
		}
		return false;
	}
	
	public boolean removeDatabaseAndWorkflowObject(long processID) {
    	boolean bSuccess =false;
		MCRWorkflowProcess wfp = MCRWorkflowProcessManager.getInstance().getWorkflowProcess(processID);
		try{
			String userID = wfp.getStringVariable("initiatorUserID");
			MCRUserCommands.deleteUser(userID);
			bSuccess = this.removeWorkflowFiles(wfp.getContextInstance());
		}catch(Exception ex){
			logger.error("could not delete workflow files", ex);
			bSuccess =false;
			wfp.setStringVariable("ROLLBACKERROR", Boolean.toString(bSuccess));
			wfp.setStringVariable("varnameERROR", ex.getMessage());						
		}finally{
			wfp.close();
		}
		return bSuccess;
	}
	
		
}
