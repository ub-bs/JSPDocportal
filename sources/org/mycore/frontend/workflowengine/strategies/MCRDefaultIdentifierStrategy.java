package org.mycore.frontend.workflowengine.strategies;


public class MCRDefaultIdentifierStrategy implements MCRIdentifierStrategy{

	public Object createNewIdentifier(String documentID, String userid, String workflowProcessType) {

		return documentID;
	}
	
	
}
