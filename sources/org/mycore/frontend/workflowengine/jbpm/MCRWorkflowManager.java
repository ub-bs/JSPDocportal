package org.mycore.frontend.workflowengine.jbpm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.exe.ExecutionContext;
import org.jdom.Element;
import org.mycore.access.MCRAccessInterface;
import org.mycore.access.MCRAccessManager;
import org.mycore.common.JSPUtils;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRException;
import org.mycore.common.MCRUtils;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.workflowengine.strategies.MCRAuthorStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRDerivateStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRWorkflowDirectoryManager;
import org.mycore.frontend.workflowengine.strategies.MCRIdentifierStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRMetadataStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRPermissionStrategy;
import org.mycore.user2.MCRUser;
import org.mycore.user2.MCRUserMgr;

public abstract class MCRWorkflowManager {
	private static Logger logger;
	private static String GUEST_ID ;

	
	protected static String deleteDir ;
	
	static{
		MCRConfiguration config = MCRConfiguration.instance();
		GUEST_ID = config.getString("MCR.users_guestuser_username","gast");
		deleteDir = config.getString("MCR.WorkflowEngine.DeleteDirectory");
		logger = Logger.getLogger(MCRWorkflowManager.class.getName());
	}

	
	protected MCRAccessInterface AI = MCRAccessManager.getAccessImpl();
	
	protected String workflowProcessType;
	protected String mainDocumentType;
	
	protected MCRIdentifierStrategy identifierStrategy;
	protected MCRAuthorStrategy authorStrategy;
	protected MCRMetadataStrategy metadataStrategy;
	protected MCRDerivateStrategy derivateStrategy;
	protected MCRPermissionStrategy permissionStrategy;
	
	
	
	/**
	 * initializes and starts a new workflow process instance
	 * @param initiator
	 * @param transitionName via the transitionname, 
	 * 	you can define different start scenarios, 
	 * 	the transitionname defines the next node after initialization
	 * 
	 * @return 
	 *     the workflow processid as long
	 */
	public abstract long initWorkflowProcess(String initiator, String transitionName) throws MCRException;	
	
	/**
	 * returns the transition that is delivered from a jbpm decision node,
	 * 	if the node would be reached now, must be implemented in each workflow
	 * 	for all decision nodes
	 * @param processid
	 * @param decision
	 * @return 
	 * 		String name of the resulting transition
	 */
	public abstract String checkDecisionNode(long processid, String decision, ExecutionContext executionContext);
	
	/**
	 * sets some workflow variables with any information from a documents metadata
	 * 		can be used in every workflow type completely different according to the needs
	 * @param mcrid
	 * @param metadata
	 */
	public abstract void setWorkflowVariablesFromMetadata(String mcrid, Element metadata, long processID);	
		
	/**
	 * returns an empty metadata object and returns the mcrobjectid of this object
	 * @param pid
	 * @return
	 */
	public abstract String createEmptyMetadataObject(long pid);
	
	
	/**
	 * commits metadata and derivates to the database
	 * @param processID
	 */
	public abstract boolean commitWorkflowObject(long processID);
	
	public abstract boolean removeWorkflowFiles(long processID);
	
	final protected void setDefaultPermissions(String objid, String userid){
		permissionStrategy.setPermissions(objid, userid, workflowProcessType, MCRWorkflowConstants.PERMISSION_MODE_DEFAULT);
	}
	
	/**
	 * adds a derivate to the workflow and returns the new derivate id
	 * @param processid
	 * @param metadataObjectId
	 * @return
	 */
	final protected String addDerivate(long processid, String metadataObjectId){
		return derivateStrategy.addNewDerivateToWorkflowObject(MCRWorkflowDirectoryManager.getWorkflowDirectory(mainDocumentType),
				metadataObjectId);
	}
	
	final protected boolean removeDerivate(long processID, String metadataObjectID, String derivateObjectID){
		MCRWorkflowProcess wfp = getWorkflowProcess(processID);
		try{
			return derivateStrategy.deleteDerivateObject(wfp, MCRWorkflowDirectoryManager.getWorkflowDirectory(mainDocumentType),
					deleteDir, metadataObjectID, derivateObjectID, true);
		}catch(MCRException ex){
			logger.error("could not remove derivate");
		}finally{
			wfp.close();
		}
		return false;
	}

	/**
	 * creates and returns a new workflow process of a given type
	 * 	use this function just in initWorkflowProcess
	 * @param workflowProcessType
	 * @return
	 */
	final protected MCRWorkflowProcess createWorkflowProcess(String workflowProcessType){
		MCRWorkflowProcess wfp = new MCRWorkflowProcess(workflowProcessType);
		return wfp;
	}	

	/**
	 * checks file-types and saves all uploaded files in the workflow directory
	 * @param files
	 * @param dirname
	 * @param pid
	 */
	final public void saveUploadedFiles(List files, String dirname, long processID) {
		MCRWorkflowProcess wfp = getWorkflowProcess(processID);
		try{
			derivateStrategy.saveFiles(files, dirname, wfp);
		}catch(MCRException ex){
			logger.error("could not save uploaded files");
		}finally{
			wfp.close();
		}
	}	
	
	/**
	 * In Decision Nodes the value of a workflow variable should fetched this way
	 * @param varname
	 * @param processid
	 * @param decisionNode
	 * @param executionContext
	 * @return
	 */
	final protected String getVariableValueInDecision(String varname, long processid,  ExecutionContext executionContext){
		String ret = "";
		try{
			if(executionContext == null){
				MCRWorkflowProcess wfp = getWorkflowProcess(processid);
				try{
					ret = wfp.getStringVariable(varname);
				}catch(MCRException ex){
					logger.error("catched error", ex);
				}finally{
					if(wfp != null)
						wfp.close();
				}					
			}else{
				ret = (String)executionContext.getVariable(varname);
			}
		}catch(Exception e){
			logger.error("could not get variable value", e);
		}
		return ret;
	}	
	
	/**
	 * returns a list of the mycore task beans that bundle all essential 
	 * 		information of a workflow process instance
	 * @param userid
	 * @param mode
	 * @param workflowProcessTypes
	 * @return
	 * 		a list of java beans
	 */
	final public static List getTasks(String userid, String mode, List workflowProcessTypes){
		List ret = new ArrayList();
		if(mode == null) mode = "";
		if(mode.equals("activeTasks")){
			ret.addAll(MCRJbpmWorkflowBase.getTasks(userid, workflowProcessTypes));
		}else if (mode.equals("initiatedProcesses")){
			ret.addAll(MCRJbpmWorkflowBase.getProcessesByInitiator(userid, workflowProcessTypes));
		}else{
			ret.addAll(MCRJbpmWorkflowBase.getTasks(userid, workflowProcessTypes));
			ret.addAll(MCRJbpmWorkflowBase.getProcessesByInitiator(userid, workflowProcessTypes));
		}
		return ret;		
	}
	
	/**
	 * is  ending a task and is checking if a user with sufficient rights tries to end a task
	 * @param processid
	 * @param taskName
	 * @param transitionName
	 * @return true|false
	 */
	final public boolean endTask(long processID, String taskName, String transitionName){
		MCRUser user = MCRUserMgr.instance().getCurrentUser();
		MCRWorkflowProcess wfp = getWorkflowProcess(processID);
		try{
			return wfp.endTask(taskName, user.getID(), transitionName);
		}catch(MCRException ex){
			logger.error("could not end task",ex);
		}finally{
			wfp.close();
		}
		return false;
	}
	

	/**
	 * returns a list of all processIDs of a given user and a given workflowType
	 * @param userid
	 *  		String userID
	 *  @param workflowProcessType
	 *          String a special workflowType
	 * @return
	 * 		a List of java.lang.Long-Objects that represent the processIDs
	 */	
	final public List getCurrentProcessIDsForProcessType(String userid, String workflowProcessType){
		return MCRJbpmWorkflowBase.getCurrentProcessIDs(userid, workflowProcessType);
	}

	/**
	 * returns a list of all processIDs of a given workflowType
	 *  @param workflowProcessType
	 *          String a special workflowType
	 * @return
	 * 		a List of java.lang.Long-Objects that represent the processIDs
	 */	
	final public List getCurrentProcessIDsForProcessType(String workflowProcessType){
		return MCRJbpmWorkflowBase.getCurrentProcessIDsForProcessType(workflowProcessType);
	}	
	
	public void deleteWorkflowProcessInstance(long processID) {
		removeWorkflowFiles(processID);
		try{
	    	MCRJbpmWorkflowBase.deleteProcessInstance(processID);
		}catch(Exception e){
			String errMsg = "could not delete process [" + processID + "]"; 
			logger.error(errMsg);
			throw new MCRException(errMsg);
		}
	}	
	
	 /**
	  * returns the value of a variable in a given workflow process instance
	  * 	for use in jsp tags or in servlets
	  * @param variable
	  * @param processID
	  * @return
	  */
	 final public String getStringVariable(String variable, long processID){
			MCRWorkflowProcess wfp = getWorkflowProcess(processID);
			String ret = "";
			try{
				ret = wfp.getStringVariable(variable);
			}catch(MCRException ex){
				logger.error("catched error", ex);
			}finally{
				if(wfp != null)
					wfp.close();
			}		
			return ret;		 
	 }

	 /**
	  * returns the value of a variable in a given workflow process instance
	  * 	for use in jsp tags or in servlets
	  * @param processID
	  * @return
	  */		 
	 public Map getStringVariableMap(long processID)	{
		 Map map = null;
		 MCRWorkflowProcess wfp = getWorkflowProcess(processID);
		 try{
			 map = wfp.getStringVariableMap();
		 }catch(MCRException ex){
				logger.error("catched error", ex);
		}finally{
			if(wfp != null)
				wfp.close();
		}
		return map;
	 }
	 
	 /**
	  * sets the value of a workflow variable in a given workflow process instance
	  * 	for use in jsp tags or in servlets
	  * @param variable
	  * @param processID
	  * @return
	  */
	 final public void setStringVariable(String variable, String value, long processID) throws MCRException{
			MCRWorkflowProcess wfp = getWorkflowProcess(processID);
			try{
				wfp.setStringVariable(variable, value);
			}catch(MCRException ex){
				String errMsg = "could not set workflow variable";
				logger.error(errMsg, ex);
				throw new MCRException(errMsg);
			}finally{
				if(wfp != null)
					wfp.close();
			}		
	 }
	 
	 /**
	  * sets a map of workflow variable in a given workflow process instance
	  * 	for use in jsp tags or in servlets
	  * @param map	a map of key/value paires for workflow variables
	  * @param processID
	  * @return
	  */
	 final public void setStringVariableMap(Map map, long processID) throws MCRException{
			MCRWorkflowProcess wfp = getWorkflowProcess(processID);
			try{
				wfp.setStringVariables(map);
			}catch(MCRException ex){
				String errMsg = "could not set workflow variable";
				logger.error(errMsg, ex);
				throw new MCRException(errMsg);
			}finally{
				if(wfp != null)
					wfp.close();
			}		
	 }		 

 	final protected void setStringVariableInDecision(String varname, String value, long processid,  ExecutionContext executionContext){
	 try{
		if(executionContext == null){
			MCRWorkflowProcess wfp = new MCRWorkflowProcess(processid);
			try{
				wfp.setStringVariable(varname, value);
			}catch(MCRException ex){
				logger.error("catched error", ex);
			}finally{
				if(wfp != null)
					wfp.close();
			}					
		}else{
			executionContext.setVariable(varname, value);
		}
	 }catch(Exception e){
			logger.error("could not set variable: " + varname + "=" + value, e);
	 }
	}	
	 

	final protected MCRWorkflowProcess getWorkflowProcess(long processID){
		MCRWorkflowProcess wfp = new MCRWorkflowProcess(processID);
		return wfp;
	}
	
	/**
	 * returns the current workflow status of a workflow process with the given 
	 * processID
	 * @param processID
	 *        long ID of a workflow-process
	 * @return
	 */
	final public String getStatus(long processID){
		String nodeName = "";
		MCRWorkflowProcess wfp = getWorkflowProcess(processID);
		try{
			Node curNode = wfp.getProcessInstance().getRootToken().getNode();
			if(curNode != null) {
				nodeName = (curNode.getName() == null)? "noname" : curNode.getName();
			}		
		}catch(MCRException ex){
			logger.error("could not get workflow status for processid [" + processID + "]", ex);
		}finally{
			wfp.close();
		}
		return nodeName;
	}
	
	/**
	 * returns the next free document id for a given document tpye
	 * ! if documents are saved in another workflow engines than this one
	 * ! you can't use this function to create the next free id
	 * @param objtype
	 *      String of documentType for which a ID is required
	 * @return
	 * TODO for user
	 */
	final public MCRObjectID getNextFreeID(String objtype) {
	    String base = MCRConfiguration.instance().getString("MCR.default_project_id","DocPortal")+ "_" + objtype; 	    
		String workingDirectoryPath = MCRWorkflowDirectoryManager.getWorkflowDirectory(objtype);
		
		MCRObjectID IDMax = new MCRObjectID();
		IDMax.setNextFreeId(base);
				
		File workingDirectory = new File(workingDirectoryPath);
		if (workingDirectory.isDirectory()) {
			String[] list = workingDirectory.list();
			for (int i = 0; i < list.length; i++) {
				try {
					MCRObjectID IDinWF = new MCRObjectID(list[i].substring(0, list[i].length() - 4));
					if (IDinWF.getTypeId().equals(objtype) && IDMax.getNumberAsInteger() <= IDinWF.getNumberAsInteger()) {
						IDinWF.setNumber(IDinWF.getNumberAsInteger() + 1);
						IDMax = IDinWF;
					}
				} catch (Exception e) {
					;   //other files can be ignored
				}
			}
		}		
		logger.debug("New ID is" + IDMax.getId());
		return IDMax;
	}
	
	protected boolean backupDerivateObject(String documentType, String metadataObject, String derivateObject, long pid) {
		try{
			String derivateDirectory = MCRWorkflowDirectoryManager.getWorkflowDirectory(documentType) + File.separator + derivateObject;
			String derivateFileName = derivateDirectory + ".xml" ;
			
			File inputDir = new File(derivateDirectory);
			File inputFile = new File(derivateFileName);
			
			SimpleDateFormat fmt = new SimpleDateFormat();
		    fmt.applyPattern( "yyyyMMddhhmmss" );
		    GregorianCalendar cal = new GregorianCalendar();
		    File curBackupDir = null;
		    boolean dirCreated = false;
		    while(!dirCreated) {
		    	curBackupDir = new File(deleteDir + "/" + "deleted_at_" + fmt.format(cal.getTime()));
		    	if(curBackupDir.mkdir()) dirCreated = true;
		    }
		    File outputDir = new File(curBackupDir.getAbsolutePath() + File.separator + inputDir.getName());
			JSPUtils.recursiveCopy(inputDir, outputDir);
			MCRUtils.copyStream(new FileInputStream(inputFile), new FileOutputStream(new File(curBackupDir.getAbsolutePath() + File.separator + inputFile.getName())));
		}catch(Exception ex){
			logger.error("problems in copying", ex);
			return false;
		}
		return true;		
	}			

	final public static boolean isUserValid(String userid){
		boolean isValid= !GUEST_ID.equals(userid);				
		try {
			MCRUser user = MCRUserMgr.instance().retrieveUser(userid);
			isValid &= user.isEnabled();
			isValid &= user.isValid();
		} catch (Exception noUser) {
			//TODO Fehlermeldung
			logger.warn("user dos'nt exist userid=" + userid);
			isValid &= false;			
		}
		return isValid;
	}
	
	
	/**
	 * @return Returns the authorStrategy.
	 */
	public final MCRAuthorStrategy getAuthorStrategy() {
		return authorStrategy;
	}


	/**
	 * @param authorStrategy The authorStrategy to set.
	 */
	protected final void setAuthorStrategy(MCRAuthorStrategy authorStrategy) {
		this.authorStrategy = authorStrategy;
	}

	/**
	 * @return Returns the identifierStrategy.
	 */
	public final MCRIdentifierStrategy getIdentifierStrategy() {
		return identifierStrategy;
	}


	/**
	 * @param identifierStrategy The identifierStrategy to set.
	 */
	protected final void setIdentifierStrategy(
			MCRIdentifierStrategy identifierStrategy) {
		this.identifierStrategy = identifierStrategy;
	}


	/**
	 * @return Returns the metadataStrategy.
	 */
	public final MCRMetadataStrategy getMetadataStrategy() {
		return metadataStrategy;
	}


	/**
	 * @param metadataStrategy The metadataStrategy to set.
	 */
	protected final void setMetadataStrategy(MCRMetadataStrategy metadataStrategy) {
		this.metadataStrategy = metadataStrategy;
	}


	/**
	 * @return Returns the workflowProcessType.
	 */
	public final String getWorkflowProcessType() {
		return workflowProcessType;
	}


	/**
	 * @param workflowProcessType The workflowProcessType to set.
	 */
	protected final void setWorkflowProcessType(String workflowProcessType) {
		this.workflowProcessType = workflowProcessType;
	}

	/**
	 * @return Returns the derivateStrategy.
	 */
	protected final MCRDerivateStrategy getDerivateStrategy() {
		return derivateStrategy;
	}

	/**
	 * @param derivateStrategy The derivateStrategy to set.
	 */
	protected final void setDerivateStrategy(MCRDerivateStrategy derivateStrategy) {
		this.derivateStrategy = derivateStrategy;
	}

	/**
	 * @return Returns the permissionStrategy.
	 */
	protected final MCRPermissionStrategy getPermissionStrategy() {
		return permissionStrategy;
	}

	/**
	 * @param permissionStrategy The permissionStrategy to set.
	 */
	protected final void setPermissionStrategy(
			MCRPermissionStrategy permissionStrategy) {
		this.permissionStrategy = permissionStrategy;
	}

	/**
	 * @return Returns the mainDocumentType.
	 */
	protected final String getMainDocumentType() {
		return mainDocumentType;
	}

	/**
	 * @param mainDocumentType The mainDocumentType to set.
	 */
	protected final void setMainDocumentType(String mainDocumentType) {
		this.mainDocumentType = mainDocumentType;
	}

	final public void setMetadataValid(String mcrid, boolean valid, long processID) {
		MCRWorkflowProcess wfp = getWorkflowProcess(processID);
		try{
			metadataStrategy.setMetadataValid(mcrid, valid, wfp);
		}catch(MCRException ex){
			logger.error("could not set metadata valid flag", ex);
		}finally{
			wfp.close();
		}
	}
	
	/**
     * The method stores the data in a working directory dependent of the
     * type.
     * 
     * @param outxml
     *            the prepared JDOM object
     * @param ID
     *            MCRObject ID of the MCRObject/MCRDerivate/MCRUser
     * @param fullname
     *            the file name where the JDOM was stored.
     */		
	final public void storeMetadata(byte[] outxml,  String ID, String fullname)  throws Exception {
		metadataStrategy.storeMetadata(outxml, ID, fullname);
	}
	
	
	
	

// hoffentlich entsorgbar	
//	 /**
//	  * sets a given map of string variables for a given workflow process instance
//	  * @param map
//	  * @param processID
//	  */
//	 abstract void setStringVariables(Map map, long processID);
//	
//	 /**
//	  * sets a variable to a given string value for a given workflow process instance
//	  * @param variable
//	  * @param value
//	  * @param processID
//	  */
//	 abstract void setStringVariable(String variable, String value, long processID);

	
}
