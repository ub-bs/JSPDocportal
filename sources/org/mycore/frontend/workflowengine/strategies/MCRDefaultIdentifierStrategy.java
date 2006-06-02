package org.mycore.frontend.workflowengine.strategies;


public class MCRDefaultIdentifierStrategy implements MCRIdentifierStrategy{

	public Object createNewIdentifier( String userid, String workflowProcessType) {
		return "";
	}
	
	public String getUrnFromDocument(String documentID) {
		// TODO Auto-generated method stub
		return "";
	}
	
	public void setDocumentIDToUrn(String urn, String documentID) {
		// TODO Auto-generated method stub
		return ;
	}
}
