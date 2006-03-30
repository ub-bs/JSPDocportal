package org.mycore.frontend.workflowengine.jbpm;

import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.mycore.common.MCRException;
import org.mycore.frontend.servlets.MCRServletJob;

public interface MCRWorkflowEngineManagerInterface {

	/**
	 * returns the directory, where the workflow-data of a given
	 *    documentType are saved
	 *    
	 * @param documentType
	 *    String a MyCoRe document type like author, document, institution
	 * @return
	 */
	public String getWorkflowDirectory(String documentType);
	/**
	 * initializes and starts a new workflow process instance
	 * @param initiator
	 * @return 
	 *     the workflow processid as long
	 */
	public long initWorkflowProcess(String initiator) throws MCRException;
	/**
	 * creates or fetchs the author joined to a userid and 
	 * 		sets new workflow status and workflow variables 
	 * @param userid
	 * @return
	 * 		String authorID
	 */	
	public String getAuthorFromUniqueWorkflow(String userid);
	
	/**
	 * creates or fetchs a new urn and 
	 * 		sets new workflow status and workflow variables 
	 * @param userid
	 * @return
	 * 		String urn
	 */
	public String getURNReservation(String userid);
	
	/**
	 * creates or fetchs a metadata document and 
	 * 		sets new workflow status and workflow variables 
	 * @param userid
	 * @return
	 * 		String documentID
	 */
	public String getMetadataDocumentID(String userid);
	
	/**
	 * returns the current workflow status of a given workflow-process
	 * don't call this, if there are multiple instances allowed for one user
	 * @param userid
	 *        String the user-id
	 * @return
	 *        String the workflow-status-name
	 */
	public String getStatus(String userid);
	
	/**
	 * returns the current workflow status of a workflow process with the given 
	 * processID
	 * @param processID
	 *        long ID of a workflow-process
	 * @return
	 */
	public String getStatus(long processID);
	
	/**
	 * returns a list of all processIDs of a given user
	 * @param userid
	 *  		String userID
	 * @return
	 * 		a List of java.lang.Long-Objects that represent the processIDs
	 * @deprecated
	 */
	public List getCurrentProcessIDs(String userid) ;
	
	/**
	 * returns a list of all processIDs of a given user and a given workflowType
	 * @param userid
	 *  		String userID
	 *  @param workflowProcessType
	 *          String a special workflowType
	 * @return
	 * 		a List of java.lang.Long-Objects that represent the processIDs
	 */	
	public List getCurrentProcessIDs(String userid, String workflowProcessType);
	
	/**
	 * a method that returns the processID for a given userid
	 * for the initiator of a workflow process, not for the editor
	 * 
	 *  
	 * 
	 * @param userid
	 *           String userid of a mycore user who wants to publish his dissertation
	 * @return
	 *           long the jbpm process id if there exists exactly one process id
	 *           0 if there exists no processInstance for given author
	 *           -1 if there are existing more than one processInstances
	 */
	public long getUniqueCurrentProcessID(String userid);
	
	/**
	 * a method that returns a jdom document for a given user
	 * and the processtype, containing the list of all editable documents 
	 * which are in the active  workflow
	 * 
	 * @param userid
	 *           String userid of a mycore user 
	 * @param workflowProcessType
	 *           String the workflow Process Type 
	 * @return
	 *           Document jdom a list of all documents plus there derivate id's
	 */
	abstract Document getListWorkflowProcess(String userid, String workflowProcessType);

	public boolean deleteDerivateObject(String documentType, String metadataObject, String derivateObject);
	
	public String addNewDerivateToWorkflowObject(String objmcrid, String documentType, String userid);
	
	public boolean commitWorkflowObject(String objmcrid, String documentType);
	
	public boolean deleteWorkflowObject(String objmcrid, String documentType);
	
	public void setCommitStatus(String mcrid, String lastAction);
	
	abstract void setDefaultPermissions(String mcrid, String userid);
	
	/**
	 * sets a workflow-process-variable with the name
	 * 	valid-{mcrid} to boolean isValid
	 * 
	 * can only be used in workflow-processes with variables
	 *  createdDocID, createdDocID1, createdDocID2, 
	 *  	that contain the requested mcrid
	 *  
	 * @param mcrid
	 * @param isValid
	 */
	abstract void setMetadataValidFlag(String mcrid, boolean isValid);
	
	abstract boolean checkMetadataValidFlag(String mcrid);
	
	abstract void saveFiles(List files, String dirname, long pid) throws MCRException;
	
	abstract String checkDecisionNode(long processid, String decision);
	
	abstract List getTasks(String userid, String mode, List workflowProcessTypes);
	
	/**
	 * returns relevant information of certain derivate for a certain document as jdom Element
	 * @param docID
	 * @param derivateID
	 * @return
	 * 	the derivate as JDOM-Element
	 * <br>
	 * output format<br>
	 * &lt;derivate id="derivateID" label="Label of Derivate" &rt;
	 *    &lt;file type="maindoc" name="filename" path="fullpath without /filename" /&gt;
	 *    &lt;file type="standard" name="filename" path="fullpath without /filename" /&gt;
	 *    &lt;file type="standard" name="filename" path="fullpath without /filename" /&gt;
	 * &lt;/derivate&rt;
	 */
	abstract Element getDerivateData(String docID, String derivateID);
	
	abstract void setWorkflowVariablesFromMetadata(String mcrid, Element metadata);
	
	abstract boolean endTask(long processid, String taskName, String transitionName);
	
	
	/**
     * The method stores the data in a working directory dependenced of the
     * type.
     * 
     * @param outxml
     *            the prepared JDOM object
     * @param ID
     *            MCRObject ID of the MCRObject/MCRDerivate/MCRUser
     * @param fullname
     *            the file name where the JDOM was stored.
     */	
	 public void storeMetadata(byte[] outxml,  String ID, String fullname)  throws Exception;
	 
	 public String getUserIDFromWorkflow(String initiator);
	 
	/**
	 * sets a workflow-process-variable with the name
	 * 	valid-{mcrid} to boolean isValid
	 * 
	 * can only be used in workflow-processes with variables
	 *  userID%  	that contain the requested userID's
	 *  
	 * @param userID
	 * @param isValid
	 */
	abstract void setUserIDValidFlag(String userID, boolean isValid);
	
}
