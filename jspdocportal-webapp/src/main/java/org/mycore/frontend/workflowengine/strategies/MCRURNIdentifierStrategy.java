package org.mycore.frontend.workflowengine.strategies;

import org.mycore.urn.services.MCRURNManager;


public class MCRURNIdentifierStrategy implements MCRIdentifierStrategy{

	
	public Object getIdentifierFromDocument(String documentID ) {
		return MCRURNManager.getURNforDocument(documentID);
	}
	

	public Object createNewIdentifier(String documentID, String workflowProcessType, String userid) {
		// TODO Auto-generated method stub
		return MCRURNManager.buildAndAssignURN(documentID, workflowProcessType);
	}
}
