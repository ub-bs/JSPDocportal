package org.mycore.frontend.workflowengine.strategies;


public class MCRDefaultIdentifierStrategy implements MCRIdentifierStrategy{

	public Object createNewIdentifier(String documentID, String userid, String workflowProcessType) {

		return documentID;
	}
	
	public String getUrnFromDocument(String documentID) {
		// TODO Auto-generated method stub
		return "";
	}
}
