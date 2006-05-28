package org.mycore.frontend.workflowengine.strategies;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import org.mycore.common.JSPUtils;
import org.mycore.common.MCRException;
import org.mycore.common.MCRUtils;
import org.mycore.datamodel.metadata.MCRDerivate;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowConstants;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowProcess;

public class MCRDefaultDerivateStrategy extends MCRDerivateStrategy {
	private static Logger logger = Logger.getLogger(MCRDefaultDerivateStrategy.class.getName());

	public boolean removeDerivates(MCRWorkflowProcess wfp, String saveDirectory, String backupDirectory){
		try{
			List attachedDerivates = Arrays.asList(wfp.getStringVariable(MCRWorkflowConstants.WFM_VAR_ATTACHED_DERIVATES).split(","));
			for (Iterator it = attachedDerivates.iterator(); it.hasNext();) {
				String derivateID = (String) it.next();
				deleteDerivateObject(wfp,saveDirectory, backupDirectory, null, derivateID, false);
			}
			wfp.setStringVariable(MCRWorkflowConstants.WFM_VAR_ATTACHED_DERIVATES,"");
			return true;
		}catch(MCRException ex){
			logger.error("could not remove derivate", ex);
		}
		return false;
	}
	
	
	public boolean deleteDerivateObject(MCRWorkflowProcess wfp, String derivateDirectory, String backupDirectory, String metadataObjectId, String derivateObjectId, boolean mustWorkflowVarBeUpdated){
		try{
			String derivateFileName = derivateDirectory + File.separator + derivateObjectId + ".xml" ;
			
			File derDir = new File(derivateDirectory);
			File derFile = new File(derivateFileName);
			
			
			if(derDir.isDirectory()) {
				logger.debug("deleting directory " + derDir.getName());
				JSPUtils.recursiveDelete(derDir);
			}else{
				logger.warn(derDir.getName() + " is not a directory, did not delete it");
				return false;
			}
			if(derFile.isFile()){
				logger.debug("deleting file " + derFile.getName());
				derFile.delete();
			}else{
				logger.warn(derFile.getName() + " is not a file, did not delete it");
				return false;
			}
			if(mustWorkflowVarBeUpdated){
				String newDerivates = wfp.getStringVariable(MCRWorkflowConstants.WFM_VAR_ATTACHED_DERIVATES)
					.replaceAll(derivateObjectId + ",*","");
				wfp.setStringVariable(MCRWorkflowConstants.WFM_VAR_ATTACHED_DERIVATES, newDerivates);
			}
		}catch(Exception ex){
			logger.error("problems in deleting", ex);
			return false;
		}
		return true;
	}	
	
	public void saveFiles(List files, String dirname, MCRWorkflowProcess wfp) throws MCRException {
		logger.debug("enters saveFiles (dummy), must be implemented in subclasses, for workflow-specific file checks");
		// save the files
	
		ArrayList ffname = new ArrayList();
		String mainfile = "";
		for (int i = 0; i < files.size(); i++) {
			FileItem item = (FileItem) (files.get(i));
			String fname = item.getName().trim();
			Matcher mat = filenamePattern.matcher(fname);
			while(mat.find()){
				fname = mat.group(1);
			}
			fname.replace(' ', '_');
			ffname.add(fname);
			try{
				File fout = new File(dirname, fname);
				FileOutputStream fouts = new FileOutputStream(fout);
				MCRUtils.copyStream(item.getInputStream(), fouts);
				fouts.close();
				logger.info("Data object stored under " + fout.getName());
			}catch(Exception ex){
				String errMsg = "could not sotre data object " + fname;
				logger.error(errMsg, ex);
				throw new MCRException(errMsg);
			}
		}
		if ((mainfile.length() == 0) && (ffname.size() > 0)) {
			mainfile = (String) ffname.get(0);
		}
	
		// add the mainfile entry
		MCRDerivate der = new MCRDerivate();
		try {
			der.setFromURI(dirname + ".xml");
			if (der.getDerivate().getInternals().getMainDoc().equals("#####")) {
				der.getDerivate().getInternals().setMainDoc(mainfile);
				byte[] outxml = MCRUtils.getByteArray(der.createXML());
				try {
					FileOutputStream out = new FileOutputStream(dirname
							+ ".xml");
					out.write(outxml);
					out.flush();
				} catch (IOException ex) {
					logger.error(ex.getMessage());
					logger.error("Exception while store to file " + dirname
							+ ".xml", ex);
					throw ex;
				}
			}
		} catch (Exception e) {
			String msgErr = "Can't open file " + dirname + ".xml"; 
			logger.error(msgErr, e);
			throw new MCRException(msgErr);
		}
	}
}
