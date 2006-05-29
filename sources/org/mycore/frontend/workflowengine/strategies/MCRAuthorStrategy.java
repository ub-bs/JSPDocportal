package org.mycore.frontend.workflowengine.strategies;

import org.mycore.datamodel.metadata.MCRObjectID;

public interface MCRAuthorStrategy {


	/**
	 * returns a list of author-ids
	 * @param userid
	 * @return
	 */
	public MCRObjectID createAuthor(String userid, MCRObjectID nextFreeAuthorId, boolean fromUserData, boolean inDatabase);
	
	
}
