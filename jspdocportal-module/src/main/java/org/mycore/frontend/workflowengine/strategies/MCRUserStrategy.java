package org.mycore.frontend.workflowengine.strategies;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jbpm.context.exe.ContextInstance;
import org.jdom2.Element;


public abstract class MCRUserStrategy {
	private static Logger logger = Logger.getLogger(MCRUserStrategy.class.getName());
	
	public static final String VALID_PREFIX = "isValid-";
	public static final String VARIABLE_PREFIX = "createdDocID";

	
	/**
	 * returns the documentType for a given metadata strategy
	 * 
	 * @return
	 */
	public abstract String getDocumentType();
	
	/**
     * The method stores the data in a working directory dependent of the
     * type.
     * 
     * @param outxml
     *            the prepared JDOM object
     * @param ID
     *            MCRObject ID of the MCRObject/MCRDerivate/MCRUser
     * @param fullname
     *            the file name where the JDOM was stored.
     */	
	 public abstract void storeMetadata(byte[] outxml,  String ID, String fullname)  throws Exception;	
	
	/**
	 * sets a workflow-process-variable with the name
	 * 	valid-{mcrid} to boolean isValid
	 * 
	 * @param mcrid
	 * @param isValid
	 */
	public abstract void setMetadataValid(String mcrid, boolean isValid, ContextInstance ctxI);
	
	/**
	 * returns the boolean value of the valid-Flag that was set
	 * via <code>setMetadataValidFlag</code>
	 * @param mcrid
	 * @return true|false
	 */
	public abstract boolean isMetadataValid(String mcrid, ContextInstance ctxI);
	
	/**
	 * sets some workflow variables with any information from a documents metadata
	 * 		can be used in every workflow type completely different according to the needs
	 * @param mcrid
	 * @param metadata
	 */
	public abstract void setWorkflowVariablesFromMetadata(ContextInstance ctxI, Element metadata);
	
	/**
	 * is publishing a user metadata object to the database
	 * @param mcrobjid
	 * @param directory
	 * @return
	 */
	public abstract boolean commitUserObject(String mcrobjid, String directory);
	
	/**
	 * is removing a user metadata object from the filesystem
	 * @param mcrobjid
	 * @param directory
	 * @return
	 */
	public abstract boolean removeUserObject(String mcrobjid, String directory);
	
	protected boolean backupMetadataObject(String inputFile, String backupDirectory){
		try{
			File fInputFile = new File(inputFile);
			File fBackupDirectory = new File(backupDirectory);

			FileInputStream fin = new FileInputStream(fInputFile);
			FileOutputStream fout = new FileOutputStream(new File(fBackupDirectory.getAbsolutePath() + "/" + fInputFile.getName()));
			IOUtils.copy(fin, fout);			
			fin.close();
			fout.flush();
			fout.close();
		}catch (Exception ex) {
			logger.error("could not backup Metadata Object", ex);
			return false;
		}
		return true;
		
	}

	public abstract boolean checkMetadata(String userid, String workflowDirectory) ;
}
