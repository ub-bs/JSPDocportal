package org.mycore.frontend.workflowengine.strategies;

public interface MCRIdentifierStrategy {
	/**
	 * returns a new created Identifier for a given documentID and a given user
	 *    
	 * @param documentID
	 *    String a MyCoRe document ID
	 * @param userid 
	 * 	  String of a MyCoRe user
	 * @return
	 */	
	public Object createNewIdentifier(String documentID, String userid, String workflowProcessType);
	
	public String getUrnFromDocument(String documentID ); 
}
