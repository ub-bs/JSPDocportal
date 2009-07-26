package org.mycore.frontend.workflowengine.jbpm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.def.Node;
import org.jdom.Element;
import org.mycore.access.MCRAccessInterface;
import org.mycore.access.MCRAccessManager;
import org.mycore.common.JSPUtils;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRException;
import org.mycore.common.MCRUtils;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.workflowengine.strategies.MCRDerivateStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRMetadataStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRPermissionStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRWorkflowDirectoryManager;
import org.mycore.user.MCRUser;
import org.mycore.user.MCRUserMgr;

import com.google.inject.Inject;

public abstract class MCRWorkflowManager {
	private static Logger logger;
	private static String GUEST_ID ;
	protected static String deleteDir ;
	
	static{
		MCRConfiguration config = MCRConfiguration.instance();
		GUEST_ID = config.getString("MCR.Users.Guestuser.UserName","gast");
		deleteDir = config.getString("MCR.WorkflowEngine.DeleteDirectory");
		logger = Logger.getLogger(MCRWorkflowManager.class.getName());
		
	}


	@Inject protected MCRMetadataStrategy 	metadataStrategy;
	@Inject protected MCRDerivateStrategy 	derivateStrategy;
	@Inject public MCRPermissionStrategy    permissionStrategy;
	
	protected MCRAccessInterface AI = MCRAccessManager.getAccessImpl();
	
	protected String workflowProcessType;
	protected String mainDocumentType;
	protected String documentTypes; //','-Separated String
	

	
	

	protected MCRWorkflowManager( String mainDocumentType, String workflowProcessType) throws Exception {
		this.workflowProcessType 	= workflowProcessType;
		this.mainDocumentType 		= mainDocumentType;
		this.documentTypes 			= mainDocumentType;
//		this.metadataStrategy 		= new MCRDefaultMetadataStrategy(mainDocumentType);
//		this.authorStrategy 		= new MCRDefaultAuthorStrategy();
//		this.identifierStrategy 	= new MCRURNIdentifierStrategy();
//		this.derivateStrategy   	= new MCRDefaultDerivateStrategy();
//		this.permissionStrategy 	= new MCRDefaultPermissionStrategy();
//		this.userStrategy 			= new MCRDefaultUserStrategy();
//		this.institutionStrategy    = new MCRDefaultInstitutionStrategy();
	}
	
	protected MCRWorkflowManager( String mainDocumentType, String documentTypes, String workflowProcessType) throws Exception {
		if(!documentTypes.contains(mainDocumentType)){
			documentTypes+=","+mainDocumentType;
		}
			this.workflowProcessType 	= workflowProcessType;
			this.mainDocumentType 		= mainDocumentType;
			this.documentTypes 			= documentTypes;
	}
	
	
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
	 * initialize and starts a new workflow process instance for editing an object
	 * @param initiator the MCRUser that initiates the process
	 * @param mcrid the MCRObjectID as String
	 * @param transitionName the transition the workflow process should take after initialization
	 * @return the workflow processid as long
	 * @throws MCRException
	 */
	public abstract long initWorkflowProcessForEditing(String initiator, String mcrid ) throws MCRException;
	
	
	/**
	 * returns the transition that is delivered from a jbpm decision node,
	 * 	if the node would be reached now, must be implemented in each workflow
	 * 	for all decision nodes
	 * @param processid
	 * @param decision
	 * @return 
	 * 		String name of the resulting transition 
	 */
	public abstract String checkDecisionNode(String decision, ContextInstance ctxI);
	
	/**
	 * sets some workflow variables with any information from a documents metadata
	 * 		can be used in every workflow type completely different according to the needs
	 * @param mcrid
	 * @param metadata
	 */
	public  void setWorkflowVariablesFromMetadata(ContextInstance ctxI, Element metadata){
		metadataStrategy.setWorkflowVariablesFromMetadata(ctxI, metadata);
	}
		
	/**
	 * returns an empty metadata object and returns the mcrobjectid of this object
	 * @param pid
	 * @return
	 */
	public abstract String createEmptyMetadataObject(ContextInstance ctxI);
	
	
	/**
	 * commits metadata and derivates to the database
	 * @param processID
	 */
	public abstract boolean commitWorkflowObject(ContextInstance ctxI);
	
	public abstract boolean removeWorkflowFiles(ContextInstance ctxI);
	
	public boolean removeDatabaseObjects(ContextInstance ctxI) {
		boolean bSuccess =false;
		MCRObject mycore_obj = new MCRObject();
		
		try{
			String documentID = (String) ctxI.getVariable(MCRWorkflowConstants.WFM_VAR_METADATA_OBJECT_IDS);
			if ( MCRObject.existInDatastore(documentID)) {
				mycore_obj.deleteFromDatastore(documentID);
				AI.removeAllRules(documentID);
				logger.info(mycore_obj.getId().getId() + " deleted.");
			}
			bSuccess=true;
			//wfp.setStringVariable("varnameERROR", '');						
		} catch ( Exception all ) {
            logger.error(all.getMessage());
			bSuccess =false;
			ctxI.setVariable("varnameERROR", all.getMessage());						
		}finally{
		
		}
		return bSuccess;
	}
	
	final protected void setDefaultPermissions(String objid, String userid, ContextInstance ctxI){
		permissionStrategy.setPermissions(objid, userid, workflowProcessType, ctxI, MCRWorkflowConstants.PERMISSION_MODE_DEFAULT);
	}
	
	/**
	 * adds a derivate to the workflow and returns the new derivate id
	 * @param processid
	 * @param metadataObjectId
	 * @return
	 */
	final protected String addDerivate(ContextInstance ctxI, String metadataObjectId){
		return derivateStrategy.addNewDerivateToWorkflowObject(MCRWorkflowDirectoryManager.getWorkflowDirectory(mainDocumentType),
				metadataObjectId);
	}
	
	final protected boolean removeDerivate(ContextInstance ctxI, String metadataObjectID, String derivateObjectID){
		try{
			return derivateStrategy.deleteDerivateObject(ctxI, MCRWorkflowDirectoryManager.getWorkflowDirectory(mainDocumentType),
					deleteDir, metadataObjectID, derivateObjectID, true);
		}catch(MCRException ex){
			logger.error("could not remove derivate");
		}finally{
		
		}
		return false;
	}

	final protected boolean removeFileFromDerivate(ContextInstance ctxI, String metadataObjectID, String derivateObjectID, String filename){
		try{
			return derivateStrategy.deleteDerivateFile(ctxI, MCRWorkflowDirectoryManager.getWorkflowDirectory(mainDocumentType),
					deleteDir, metadataObjectID, derivateObjectID, true, filename);
		}catch(MCRException ex){
			logger.error("could not remove file from derivate");
		}finally{
		
		}
		return false;
	}
	
	final protected boolean moveDerivate(ContextInstance ctxI, String metadataObjectID, String derivateObjectID, int direction){
		try{
			return derivateStrategy.moveDerivateObject(ctxI,derivateObjectID, direction);
		}catch(MCRException ex){
			logger.error("could not move derivate");
		}finally{
		
		}
		return false;
	}
	
	/**
	 * creates and returns a new workflow process of a given type
	 * 	use this function <b>just</b> in initWorkflowProcess
	 * @param workflowProcessType
	 * @return
	 */
	final protected MCRWorkflowProcess createWorkflowProcess(String workflowProcessType){
		return MCRWorkflowProcessManager.getInstance().createWorkflowProcess(workflowProcessType);
		
		//wfp = new MCRWorkflowProcess(workflowProcessType);
		//workflowprocesses.put(new Long(wfp.getProcessInstanceID()), wfp);
		//return wfp;
	}	

	/**
	 * checks file-types and saves all uploaded files in the workflow directory
	 * @param files
	 * @param dirname
	 * @param pid
	 */
	final public void saveUploadedFiles(List files, String dirname, ContextInstance ctxI, String newLabel) {
		try{
			derivateStrategy.saveFiles(files, dirname, ctxI, newLabel);
		}catch(MCRException ex){
			logger.error("could not save uploaded files");
		}finally{
		
		}
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
	@SuppressWarnings("unchecked")
	final public static List getTasks(String userid, String mode, List workflowProcessTypes){
		List ret = new ArrayList<Object>();
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
		MCRWorkflowProcess wfp = MCRWorkflowProcessManager.getInstance().getWorkflowProcess(processID);
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
	final public List<Long> getCurrentProcessIDsForProcessType(String userid, String workflowProcessType){
		return MCRJbpmWorkflowBase.getCurrentProcessIDs(userid, workflowProcessType);
	}
	
	

	/**
	 * returns a list of all processIDs of a given variablename and its value
	 *  @param workflowvariable
	 *  @param value of it 
	 *          
	 * @return
	 * 		a List of java.lang.Long-Objects that represent the processIDs
	 */	
	final public List getCurrentProcessIDsForVariable(String varname, String value){
		return MCRJbpmWorkflowBase.getCurrentProcessIDsForProcessVariable(varname,value);
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
	
	public void deleteWorkflowProcessInstance(long pid) {
		MCRWorkflowProcess wfp = MCRWorkflowProcessManager.getInstance().getWorkflowProcess(pid);
		try{
			
			removeWorkflowFiles(wfp.getContextInstance());
			wfp.close();
			MCRJbpmWorkflowBase.deleteProcessInstance(pid);
		}catch(Exception e){
			String errMsg = "could not delete process [" + pid + "]"; 
			logger.error(errMsg);
			throw new MCRException(errMsg);
		}
		finally{
			if(!wfp.wasClosed()){
				wfp.close();
			}
		}
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
		MCRWorkflowProcess wfp = MCRWorkflowProcessManager.getInstance().getWorkflowProcess(processID);
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
	final public synchronized MCRObjectID getNextFreeID(String objtype) {
	    String base = MCRConfiguration.instance().getString("MCR.SWF.Project.ID","DocPortal")+ "_" + objtype; 	    
		String workingDirectoryPath = MCRWorkflowDirectoryManager.getWorkflowDirectory(objtype);
		int maxwf =0;
				
		File workingDirectory = new File(workingDirectoryPath);
		if (workingDirectory.isDirectory()) {
			for (String filename: workingDirectory.list()) {
				try {
					MCRObjectID IDinWF = new MCRObjectID(filename.substring(0, filename.length() - 4));
					if (IDinWF.getTypeId().equals(objtype) && maxwf < IDinWF.getNumberAsInteger()) {
						maxwf = IDinWF.getNumberAsInteger();
					}
				} catch (Exception e) {
					  //other files can be ignored
				}
			}
		}
		MCRObjectID result = new MCRObjectID();
		result.setNextFreeId(base,maxwf);
	
		logger.debug("New ID is: " + result.getId());
		return result;
	}
	
	protected boolean backupDerivateObject(String documentType, String metadataObject, String derivateObject, long pid) {
		try{
			String derivateDirectory = MCRWorkflowDirectoryManager.getWorkflowDirectory(documentType) + "/" + derivateObject;
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
		    File outputDir = new File(curBackupDir.getAbsolutePath() + "/" + inputDir.getName());
			JSPUtils.recursiveCopy(inputDir, outputDir);
			FileInputStream fin = new FileInputStream(inputFile);
			FileOutputStream fout = new FileOutputStream(new File(curBackupDir.getAbsolutePath() + "/" + inputFile.getName()));
			MCRUtils.copyStream(fin, fout);
			fin.close();
			fout.flush();
			fout.close();
			
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
	
	
//	/**
//	 * @return Returns the authorStrategy.
//	 */
//	public final MCRAuthorStrategy getAuthorStrategy() {
//		return authorStrategy;
//	}
//
//
//	/**
//	 * @param authorStrategy The authorStrategy to set.
//	 */
//	protected final void setAuthorStrategy(MCRAuthorStrategy authorStrategy) {
//		this.authorStrategy = authorStrategy;
//	}
//
//	/**
//	 * @return Returns the identifierStrategy.
//	 */
//	public final MCRIdentifierStrategy getIdentifierStrategy() {
//		return identifierStrategy;
//	}
//
//
//	/**
//	 * @param identifierStrategy The identifierStrategy to set.
//	 */
//	protected final void setIdentifierStrategy(
//			MCRIdentifierStrategy identifierStrategy) {
//		this.identifierStrategy = identifierStrategy;
//	}
//
//
//	/**
//	 * @return Returns the metadataStrategy.
//	 */
//	public final MCRMetadataStrategy getMetadataStrategy() {
//		return metadataStrategy;
//	}
//
//
//	/**
//	 * @param metadataStrategy The metadataStrategy to set.
//	 */
//	protected final void setMetadataStrategy(MCRMetadataStrategy metadataStrategy) {
//		this.metadataStrategy = metadataStrategy;
//	}


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

//	/**
//	 * @return Returns the derivateStrategy.
//	 */
//	protected final MCRDerivateStrategy getDerivateStrategy() {
//		return derivateStrategy;
//	}
//
//	/**
//	 * @param derivateStrategy The derivateStrategy to set.
//	 */
//	protected final void setDerivateStrategy(MCRDerivateStrategy derivateStrategy) {
//		this.derivateStrategy = derivateStrategy;
//	}
//
//	/**
//	 * @return Returns the permissionStrategy.
//	 */
//	public final MCRPermissionStrategy getPermissionStrategy() {
//		return permissionStrategy;
//	}
//
//	/**
//	 * @param permissionStrategy The permissionStrategy to set.
//	 */
//	protected final void setPermissionStrategy(
//			MCRPermissionStrategy permissionStrategy) {
//		this.permissionStrategy = permissionStrategy;
//	}

	/**
	 * @return Returns the mainDocumentType.
	 */
	public final String getMainDocumentType() {
		return mainDocumentType;
	}

	public final String getDocumentTypes(){
		return documentTypes;
	}
	/**
	 * @param mainDocumentType The mainDocumentType to set.
	 */
	protected final void setMainDocumentType(String mainDocumentType) {
		this.mainDocumentType = mainDocumentType;
	}

	public final void setMetadataValid(String mcrid, boolean valid, ContextInstance ctxI) {
		metadataStrategy.setMetadataValid(mcrid, valid, ctxI);
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
	
	/**
	 * This method can be used to create a new URN
	 * The default implementation does nothing.
	 * Subclasses may overwrite.
	 * @return boolean: true if the creation was successful
	 */
	public boolean createURN(ContextInstance ctxI){
		//does noting
		return true;
	}
	
}
