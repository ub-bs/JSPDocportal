package org.mycore.frontend.workflowengine.strategies;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.mycore.common.xml.MCRXMLHelper;
import org.mycore.frontend.cli.MCRUserCommands2;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowConstants;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowProcess;
import org.mycore.user2.MCRUserMgr;

public class MCRDefaultUserStrategy extends MCRUserStrategy{
	private String documentType;
	
	public MCRDefaultUserStrategy(){
		this.documentType = "user";
	}
	
	private static Logger logger = Logger.getLogger(MCRDefaultUserStrategy.class.getName());
	
	public void setMetadataValid(String userid, boolean isValid, MCRWorkflowProcess wfp) {
		wfp.setStringVariable(MCRMetadataStrategy.VALID_PREFIX + userid, Boolean.toString(isValid));
	}

	public boolean isMetadataValid(String userid, MCRWorkflowProcess wfp) {
		try{
			if(wfp.getStringVariable("valid-" + userid).equals("true"))
				return true;
			else
				return false;
		}catch(Exception e){
			logger.error("user-metadata  flag was not set for " + userid + " and process " + wfp.getProcessInstanceID(), e);
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

	public boolean removeMetadataFiles(MCRWorkflowProcess wfp, String saveDirectory, String  backupDirectory) {
		String[] objids = wfp.getStringVariable(MCRWorkflowConstants.WFM_VAR_METADATA_OBJECT_IDS).split(",");
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

	public void setWorkflowVariablesFromMetadata(MCRWorkflowProcess wfp, Element metadata) {
		StringBuffer sbTitle = new StringBuffer("");
		for(Iterator it = metadata.getDescendants(new ElementFilter("title")); it.hasNext();){
			Element title = (Element)it.next();
			sbTitle.append(title.getText());
		}
		if(sbTitle.length() == 0){
			wfp.setStringVariable("wfp-title", "Your Workflow Object");
		}else{
			wfp.setStringVariable("wfp-title", sbTitle.toString());
		}
	}

	public boolean checkMetadata(String userid, String directory) {  
		boolean bSuccess = true;
		String filename = directory + "/" + "user_" + userid + ".xml";
		try { 
			MCRXMLHelper.parseURI(filename, true);
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
			if ( MCRUserMgr.instance().existUser(userid) ) {
				MCRUserCommands2.updateUserFromFile(filename);
			} else {
				MCRUserCommands2.createUserFromFile(filename);
				
			}
		}catch(Exception e){
			logger.error("could not commit user");
			bSuccess = false;
		}
		if ( MCRUserMgr.instance().existUser(userid) ) {
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
