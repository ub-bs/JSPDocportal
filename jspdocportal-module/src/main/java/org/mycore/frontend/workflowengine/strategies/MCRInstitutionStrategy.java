package org.mycore.frontend.workflowengine.strategies;

import org.mycore.datamodel.metadata.MCRObjectID;

public interface MCRInstitutionStrategy {


	/**
	 * creates a new institution object
	 * @param nextFreeInstitutionID - the next free id
	 * @param inDatabasee - true = database / falsch = workflow
	 * @return the objectID of the newly created object
	 */
	public MCRObjectID createInstitution(MCRObjectID nextFreeInstitutionId, boolean inDatabase);
	
	
}
