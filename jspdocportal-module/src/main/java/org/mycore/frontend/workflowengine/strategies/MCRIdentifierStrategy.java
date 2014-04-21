package org.mycore.frontend.workflowengine.strategies;

public interface MCRIdentifierStrategy {
	
	public Object getIdentifierFromDocument(String documentID ); 
	
		
	/**
	 * 	returns a new created Identifier for a given documentID,
	 *  given workflowType and a given user
	 *    
	 * @param documentID
	 *    String a MyCoRe document ID
	 * @param workflowProcessType
	 *    String the type of workflow   
	 * @param userid 
	 * 	  String of a MyCoRe user
	 * @return
	 *   the identifier object
	 */	
	public Object createNewIdentifier(String documentID, String workflowProcessType, String userid);
	
}
