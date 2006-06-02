package org.mycore.frontend.workflowengine.strategies;

import org.mycore.common.MCRConfiguration;
import org.mycore.services.nbn.MCRNBN;


public class MCRURNIdentifierStrategy implements MCRIdentifierStrategy{
	private static MCRConfiguration config = MCRConfiguration.instance();

	public Object createNewIdentifier(  String userid, String workflowProcessType) {
		return createUrnReservationForAuthor(userid, "URN for " + workflowProcessType, workflowProcessType );
	}
	
	public String getUrnFromDocument(String documentID ) {
		MCRNBN mcrurn = new MCRNBN();		
		return mcrurn.getURNByDocumentID(documentID);
	}
	
	protected String createUrnReservationForAuthor(String authorid, String comment, String workflowProcessType ){
		String nissprefix = config.getString("MCR.nbn.nissprefix." + workflowProcessType, "diss");
		MCRNBN mcrurn = new MCRNBN(authorid, comment, nissprefix);  
		return mcrurn.getURN();
	}

	public void setDocumentIDToUrn(String urn, String documentID){
		MCRNBN mcrurn = new MCRNBN(urn);
		mcrurn.setDocumentId(documentID);
		
	}
}
