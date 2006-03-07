package org.mycore.frontend.workflowengine.jbpm;

import java.util.List;

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
	 */
	public void initWorkflowProcess(String initiator) throws MCRException;
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
	 * a method that returns the processID for a given author
	 * for the author of a dissertation, not for the editor
	 * for an author just one active workflow for dissertation is 
	 * allowed, if there are existing older ones, they must be
	 * deleted, 
	 * 
	 * @param userid
	 *           String userid of a mycore user who wants to publish his dissertation
	 * @return
	 *           long the jbpm process id
	 */
	public long getUniqueCurrentProcessID(String userid) ;	
}
