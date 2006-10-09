package org.mycore.user2;

import org.mycore.user2.MCRUser;

/**
 * Interface that should be implemented if you want to use login data from
 * an external User Management System (e.g. a database or an active directory)
 * @author Robert Stephan
 * 
 *
 */
public interface MCRExternalUserLogin {
	/**
	 * check user against external user management system
	 * @param userID - the external userid
	 * @param password - the external password
	 * @return true, if the user could be successfully logged in.
	 */
	public boolean loginUser(String userID, String password);
	/**
	 * @param userID - the external userid 
	 * @param password - the external password
	 * @return  the MyCoRe-userid that is mapped to the external userid
	 */
	public String retrieveMyCoReUserID(String userID, String password);
	/**
	 * @param userID - the external userid 
	 * @param password - the external password
	 * @return  the MyCoRe-password that is mapped to the external userid
	 */
	public String retrieveMyCoRePassword(String userID, String password);
	
	/**
	 * updates the MyCoreUser Data with data from the external
	 * user management system
	 * @param userID - the external userid
	 * @param mcrUser - the MyCoRe userobject
	 */
	public void updateUserData(String userID, MCRUser mcrUser);	
	/**
	 * @param userID - the external userid
	 * @return a text that gives information about the external usermessage
	 * (e.g. exists, is enabled, ... )
	 */
	public String checkUserID(String userID);
}
