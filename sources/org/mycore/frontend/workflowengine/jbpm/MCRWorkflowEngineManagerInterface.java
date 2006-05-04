package org.mycore.frontend.workflowengine.jbpm;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jbpm.graph.exe.ExecutionContext;
import org.jdom.Element;
import org.mycore.common.MCRException;

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
	public String createAuthorFromInitiator(String userid);
	
	/**
	 * creates or fetchs a new urn and 
	 * 		sets new workflow status and workflow variables 
	 * @param userid
	 * @return
	 * 		String urn
	 */
	public String createURNReservation(String userid);
	
	/**
	 * creates or fetchs a metadata document and 
	 * 		sets new workflow status and workflow variables 
	 * @param userid
	 * @return
	 * 		String documentID
	 */
	public String createMetadataDocumentID(String userid, long pid);
	
	/**
	 * returns the current workflow status of a workflow process with the given 
	 * processID
	 * @param processID
	 *        long ID of a workflow-process
	 * @return
	 */
	public String getStatus(long processID);
	
	
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
	 * @deprecated           
	 */
	public long getUniqueCurrentProcessID(String userid);
	
	/**
	 * deletes a derivate from the workflow 
	 * @param documentType
	 * 			String like "disshab" or "document"
	 * @param metadataObject
	 * 			String of the objID the derivate belongs to
	 * @param derivateObject
	 * 			String of the derID, that will bedeleted
	 * @return
	 */
	public boolean deleteDerivateObject(String documentType, String metadataObject, String derivateObject);
	
	/**
	 * adds a new derivate to an workflow object
	 * @param objmcrid
	 * @param documentType
	 * @param userid
	 * @return
	 * TODO check, why is here a userid required???
	 */
	public String addNewDerivateToWorkflowObject(String objmcrid, String documentType, String userid);
	
	/**
	 * commits a workflow object to the database(server)
	 * @param objmcrid
	 * @param documentType
	 * @return true|false
	 */
	public boolean commitWorkflowObject(String objmcrid, String documentType);
	
	/**
	 * delete a whole object from the workflow
	 * @param objmcrid
	 * @param documentType
	 * @return true|false
	 */
	public boolean deleteWorkflowObject(String objmcrid, String documentType);
	
	/**
	 * sets default permissions for a given mcrobj 
	 * and a given user in the specific workflow
	 * 
	 * @param mcrid
	 * @param userid
	 */
	public void setDefaultPermissions(String mcrid, String userid);
	
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
	public void setMetadataValidFlag(String mcrid, boolean isValid);
	
	/**
	 * returns the boolean value of the valid-Flag that was set
	 * via <code>setMetadataValidFlag</code>
	 * @param mcrid
	 * @return true|false
	 */
	public boolean checkMetadataValidFlag(String mcrid);
	
	/**
	 * saves a list of files in a workflow directory, 
	 * 		when the requirements of the specific workflow-type 
	 * 		cannot be fulfilled, an exception is thrown
	 * @param files
	 * @param dirname
	 * @param pid
	 * @throws MCRException
	 */
	public void saveFiles(List files, String dirname, long pid) throws MCRException;
	
	/**
	 * returns the transition that is delivered from a jbpm decision node,
	 * 	if the node would be reached now, must be implemented in each workflow
	 * 	for all decision nodes
	 * @param processid
	 * @param decision
	 * @return 
	 * 		String name of the resulting transition
	 */
	public String checkDecisionNode(long processid, String decision, ExecutionContext executionContext);
	
	/**
	 * returns a list of the mycore task beans that bundle all essential 
	 * 		information of a workflow process instance
	 * @param userid
	 * @param mode
	 * @param workflowProcessTypes
	 * @return
	 * 		a list of java beans
	 */
	public List getTasks(String userid, String mode, List workflowProcessTypes);
	
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
	
	/**
	 * sets some workflow variables with any information from a documents metadata
	 * 		can be used in every workflow type completely different according to the needs
	 * @param mcrid
	 * @param metadata
	 */
	public void setWorkflowVariablesFromMetadata(String mcrid, Element metadata);
	
	/**
	 * is  ending a task and is checking if a user with sufficient rights tries to end a task
	 * @param processid
	 * @param taskName
	 * @param transitionName
	 * @return true|false
	 */
	public boolean endTask(long processid, String taskName, String transitionName);
	
	
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
	 
	 /**
	  * sets a given map of string variables for a given workflow process instance
	  * @param map
	  * @param processID
	  */
	 abstract void setStringVariables(Map map, long processID);
	
	 /**
	  * sets a variable to a given string value for a given workflow process instance
	  * @param variable
	  * @param value
	  * @param processID
	  */
	 abstract void setStringVariable(String variable, String value, long processID);
	
	 /**
	  * returns the value of a variable in a given workflow process instance
	  * @param variable
	  * @param processID
	  * @return
	  */
	 abstract String getStringVariable(String variable, long processID);
	
	 /**
	  * deletes a given workflow process instance with all tasks and variables
	  * @param processID
	  */
	 abstract void deleteWorkflowProcessInstance(long processID);
	
	 abstract void deleteWorkflowVariables(Set set, long processID);
	
}
