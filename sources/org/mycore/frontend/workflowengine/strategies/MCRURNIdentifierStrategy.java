package org.mycore.frontend.workflowengine.strategies;

import org.mycore.common.MCRConfiguration;
import org.mycore.services.nbn.MCRNBN;


public class MCRURNIdentifierStrategy implements MCRIdentifierStrategy{
	private static MCRConfiguration config = MCRConfiguration.instance();

	public Object createNewIdentifier(String documentID, String userid, String workflowProcessType) {
		return createUrnReservationForAuthor(userid, "URN for " + workflowProcessType, workflowProcessType, documentID);
	}
	
	public String getUrnFromDocument(String documentID ) {
		MCRNBN mcrurn = new MCRNBN();		
		return mcrurn.getURNByDocumentID(documentID);
	}
	
	protected String createUrnReservationForAuthor(String authorid, String comment, String workflowProcessType,String documentID ){
		String nissprefix = config.getString("MCR.nbn.nissprefix." + workflowProcessType, "diss");
		MCRNBN mcrurn = new MCRNBN(authorid, comment, nissprefix);  
		mcrurn.setDocumentId(documentID);
		return mcrurn.getURN();
	}

}
