package org.mycore.frontend.workflowengine.strategies;


public class MCRURNIdentifierStrategy implements MCRIdentifierStrategy{

	public Object createNewIdentifier(  String userid, String workflowProcessType) {
		return createUrnReservationForAuthor(userid, "URN for " + workflowProcessType, workflowProcessType );
	}
	
	public String getUrnFromDocument(String documentID ) {
		return org.mycore.services.urn.MCRURNManager.getURNforDocument(documentID);
	}
	
	protected String createUrnReservationForAuthor(String authorid, String comment, String workflowProcessType ){
		// String nissprefix = config.getString("MCR.nbn.nissprefix." + workflowProcessType, "diss");
		return org.mycore.services.urn.MCRURNManager.buildURN(workflowProcessType);
	}

	public void setDocumentIDToUrn(String urn, String documentID){
		org.mycore.services.urn.MCRURNManager.assignURN(urn, documentID);		
	}
}
