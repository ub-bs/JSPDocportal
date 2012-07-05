package org.mycore.frontend.workflowengine.strategies;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jbpm.context.exe.ContextInstance;
import org.jdom.Element;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.xml.MCRXMLHelper;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowConstants;
import org.mycore.frontend.jsp.user.MCRExternalUserLogin;
import org.mycore.user2.MCRUserCommands;
import org.mycore.user2.MCRUserManager;


public class MCRDefaultUserStrategy extends MCRUserStrategy{
	private String documentType;
	
	public MCRDefaultUserStrategy(){
		this.documentType = "user";
	}
	
	private static Logger logger = Logger.getLogger(MCRDefaultUserStrategy.class.getName());
	
	public void setMetadataValid(String userid, boolean isValid, ContextInstance ctxI) {
		ctxI.setVariable(MCRMetadataStrategy.VALID_PREFIX + userid, Boolean.toString(isValid));
	}

	public boolean isMetadataValid(String userid, ContextInstance ctxI) {
		try{
			if(ctxI.getVariable("valid-" + userid).equals("true"))
				return true;
			else
				return false;
		}catch(Exception e){
			logger.error("user-metadata  flag was not set for " + userid + " and process " + ctxI.getProcessInstance().getId(), e);
			return false;
		}
	}

    public final void storeMetadata(byte[] outxml, String ID, String fullname) throws Exception {
        if (outxml == null) {
            return;
        }
        try {
            FileOutputStream out = new FileOutputStream(fullname);
            out.write(outxml);
            out.flush();
        } catch (IOException ex) {
        	logger.error(ex.getMessage());
        	logger.error("Exception while store to file " + fullname);
            return;
        }
        logger.info("Object " + ID + " stored under " + fullname + ".");
    }

	public boolean removeMetadataFiles(ContextInstance ctxI, String saveDirectory, String  backupDirectory) {
		String[] objids = ((String)ctxI.getVariable(MCRWorkflowConstants.WFM_VAR_METADATA_OBJECT_IDS)).split(",");
		for (int i = 0; i < objids.length; i++) {
			String filename = saveDirectory + "/" + objids[i] + ".xml";
			if(!backupMetadataObject(filename, backupDirectory)){
				logger.error("could not backup file " + filename);
				return false;
			}
			try {
				File fi = new File(filename);
				if (fi.isFile() && fi.canWrite()) {				
					fi.delete();
					logger.debug("File " + filename + " removed.");
				} else {
					logger.error("Can't remove file " + filename);
					return false;
				}
			} catch (Exception ex) {
				logger.error("Can't remove file " + filename);
				return false;
			}			
		}
		return true;
	}

	public void setWorkflowVariablesFromMetadata(ContextInstance ctxI, Element userMetadata) {
		Map<String, Object> map = new HashMap<String, Object>();
		Element userContact = userMetadata.getChild("user.contact");
		if ( userContact != null ) {
			String salutation="", firstname="", lastname="";
			if (userContact.getChild("contact.salutation") != null)
				salutation  = userContact.getChild("contact.salutation").getText();
			if (userContact.getChild("contact.firstname") != null)
				firstname   = userContact.getChild("contact.firstname").getText();
			if (userContact.getChild("contact.lastname") != null)
				lastname    = userContact.getChild("contact.lastname").getText();
			StringBuffer bname = new StringBuffer(salutation).append(" ").append(firstname).append(" ").append(lastname);
			map.put("initiatorName", bname.toString());
			if (userContact.getChild("contact.email") != null)
				map.put("initiatorEmail", userContact.getChild("contact.email").getText());
			if (userContact.getChild("contact.institution") != null)
				map.put("initiatorInstitution", userContact.getChild("contact.institution").getText());
			if (userContact.getChild("contact.faculty") != null)
				map.put("initiatorFaculty", userContact.getChild("contact.faculty").getText());
		}
		if ( userMetadata.getChild("user.description") != null)
			map.put("initiatorIntend", userMetadata.getChild("user.description").getText());
		if ( userMetadata.getChild("user.password") != null)
			map.put("initiatorPwd", "xxxxxxx");
		if ( userMetadata.getChild("user.groups") != null){
			List groups = userMetadata.getChild("user.groups").getChildren();
			StringBuffer sGroups = new StringBuffer("");
			for ( int i=0; i < groups.size(); i++){
				 Element eG = (Element)groups.get(i);
				 if ( !eG.getText().equalsIgnoreCase("gastgroup"))
					 sGroups.append(eG.getText()).append(" ");
			}
			map.put("initiatorGroup", sGroups.toString());
		}
		
		//check if the UserID exists in an external User Management System
		String classNameExtUserLogin = MCRConfiguration.instance().getString("MCR.Application.ExternalUserLogin.Class", "").trim();
		if(classNameExtUserLogin.length()>1){
			String checkExtern =null;
			if(userMetadata.getAttributeValue("ID")!=null){
				MCRExternalUserLogin extLogin= null;
		        if(classNameExtUserLogin.length()>0){
		        	try{
		        		Class c = Class.forName(classNameExtUserLogin);
		        		extLogin = (MCRExternalUserLogin)c.newInstance();
		        		checkExtern = extLogin.checkUserID(userMetadata.getAttributeValue("ID"));
		        		map.put("externalValidation", checkExtern);
		        		
		        	}       	
		        	catch(Exception e){
		        
		        	}
		        }
			}
		}
		
		//setStringVariableMap(map, processID);
		for (Iterator it = map.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			String value = (String)map.get(key);
			if(value == null)
				value = "";
			ctxI.setVariable(key, (String)map.get(key));
		}		
	}

	public boolean checkMetadata(String userid, String directory) {  
		boolean bSuccess = true;
		String filename = directory + "/" + "user_" + userid + ".xml";
		try { 
			MCRXMLHelper.parseURI(new File(filename).toURI(), false);
		}catch(Exception e){
			logger.error("Check Metadata of user" +  filename);
			logger.error( e.getMessage());
			bSuccess = false;
		}
		return bSuccess;
	}
	
	public boolean commitUserObject(String userid, String directory) {
		boolean bSuccess = true;
		String filename = directory + "/" + "user_" + userid + ".xml";
		try { 
			if ( MCRUserManager.exists(userid) ) {
				MCRUserCommands.updateUserFromFile(filename);
			} else {
				MCRUserCommands.createUserFromFile(filename);
				
			}
			MCRUserCommands.enableUser(userid);
		}catch(Exception e){
			logger.error("could not commit user");
			bSuccess = false;
		}
		if ( MCRUserManager.exists(userid) ) {
			logger.info("The user object: " + filename + " is loaded.");
			bSuccess=true;
		}
		return bSuccess;			
	}

	public boolean removeUserObject(String userid,String directory) {
		boolean bSuccess = true;
		try{
			String filename = directory + "/" + "user_" + userid + ".xml";	
			try {
				File ff = new File (filename);
				if ( ff.exists()) 
					bSuccess = ff.delete();
			} catch (Exception ig){ 
				logger.error("Can't delete File catched error: ", ig);
				bSuccess=false;
			}
			logger.info("deleting user object: " + filename + " success=" + bSuccess);			
		}catch(Exception e){
			logger.error("could not rollback user");
			bSuccess = false;
		}
		return bSuccess;
	}
	
	/**
	 * @return Returns the documentType.
	 */
	public final String getDocumentType() {
		return documentType;
	}

	/**
	 * @param documentType The documentType to set.
	 */
	protected final void setDocumentType(String documentType) {
		this.documentType = documentType;
	}
	
	
}
