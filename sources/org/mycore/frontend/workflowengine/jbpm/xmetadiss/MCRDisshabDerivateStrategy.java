package org.mycore.frontend.workflowengine.jbpm.xmetadiss;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import org.mycore.common.MCRException;
import org.mycore.common.MCRUtils;
import org.mycore.datamodel.metadata.MCRDerivate;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowProcess;
import org.mycore.frontend.workflowengine.strategies.MCRDefaultDerivateStrategy;

public class MCRDisshabDerivateStrategy extends MCRDefaultDerivateStrategy {
	private static Logger logger = Logger.getLogger(MCRDisshabDerivateStrategy.class.getName());
	
	public void saveFiles(List files, String dirname, MCRWorkflowProcess wfp) throws MCRException {
	// a correct dissertation contains in the main derivate
	//		exactly one pdf-file and optional an attachment zip-file
	//		the pdf file will be renamed to dissertation.pdf
	//		in a derivate only one zip-file is allowed, it will be renamed
	//		to attachment.zip

	MCRDerivate der = new MCRDerivate();
	
	try {
			der.setFromURI(dirname + ".xml");
		}catch(Exception ex){
			String errMsg = "could not set derivate " + dirname + ".xml";
			logger.error(errMsg, ex);
			throw new MCRException(errMsg);
		}
		
		String derID = der.getId().getId();
		
		boolean containsPdf = false;
		boolean containsZip = false;
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
			String fileextension = null;
			mat = fileextensionPattern.matcher(fname);
			while(mat.find()) {
				fileextension = mat.group(1);
			}
			fileextension = fileextension.toLowerCase();
			if(fileextension.equals("zip")) {
				if(containsZip) {
					String errMsg = "just one zip-file allowed for each derivate";
					logger.error(errMsg);
					throw new MCRException(errMsg);
				}else{
					containsZip = true;
					fname = "attachment.zip";
				}
			}else if(fileextension.equals("pdf")) {
				if(containsPdf) {
					String errMsg = "just one pdf-file allowed for one derivate, other files must be put in a zip file";
					logger.error(errMsg);
					throw new MCRException(errMsg);
				}else{
					containsPdf = true;
					String wfPdf = wfp.getStringVariable("containsPDF"); 
					if(wfPdf != null && !wfPdf.equals(derID) && !wfPdf.equals("")){
						String errMsg = "just one pdf-file for all derivates for one dissertation, please delete old derivates first";
						logger.error(errMsg);
						throw new MCRException(errMsg);
					}else{
						fname = "dissertation.pdf";
						mainfile = fname;
					}
				}
			}
			try{
				File fout = new File(dirname, fname);
				FileOutputStream fouts = new FileOutputStream(fout);
				MCRUtils.copyStream(item.getInputStream(), fouts);
				fouts.close();
				logger.info("Data object stored under " + fout.getName());
			}catch(Exception ex){
				String errMsg = "could not store data object " + fname;
				logger.error(errMsg, ex);
				throw new MCRException(errMsg);
			}
		}
		if ((mainfile.length() == 0) && (ffname.size() > 0)) {
			mainfile = (String) ffname.get(0);
		}
	
		// add the mainfile entry
		try{
			if (der.getDerivate().getInternals().getMainDoc().equals("#####")) {
				der.getDerivate().getInternals().setMainDoc(mainfile);
				byte[] outxml = MCRUtils.getByteArray(der.createXML());
				try {
					FileOutputStream out = new FileOutputStream(dirname
							+ ".xml");
					out.write(outxml);
					out.flush();
					out.close();
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
		if(containsPdf){
			wfp.setStringVariable("containsPDF", derID);
			if(containsZip){
				wfp.setStringVariable("containsZIP", derID);
			}
		}
		String attachedDerivates = wfp.getStringVariable("attachedDerivates");
		if(attachedDerivates == null || attachedDerivates.equals("")){
			wfp.setStringVariable("attachedDerivates", derID);
		}else{
			wfp.setStringVariable("attachedDerivates", attachedDerivates + "," + derID);
		}
	}
	
	public boolean deleteDerivateObject(MCRWorkflowProcess wfp, String saveDirectory, String backupDirectory, String metadataObjectID, 
			String derivateObjectID, boolean mustWorkflowVarBeUpdated) {

//			HashSet attachedDerivates = new HashSet(Arrays.asList(wfp.getStringVariable("attachedDerivates").split(",")));
//			attachedDerivates.remove(derivateObjectID);
		try{
			if (backupDerivateObject(saveDirectory, backupDirectory, metadataObjectID, derivateObjectID, wfp.getProcessInstanceID())){
				if(super.deleteDerivateObject(wfp, saveDirectory, backupDirectory, metadataObjectID, derivateObjectID, mustWorkflowVarBeUpdated) && mustWorkflowVarBeUpdated){
					String cmp = wfp.getStringVariable("containsPDF"); 
					if(cmp != null && cmp.equals(derivateObjectID))
						wfp.setStringVariable("containsPDF","");
					cmp = wfp.getStringVariable("containsZIP");
					if(cmp != null && cmp.equals(derivateObjectID))
						wfp.setStringVariable("containsZIP", "");
					return true;
				}else{
					logger.error("problems in deleting, check inconsistences in workflow process " + wfp.getProcessInstanceID());
					return false;
				}
			}else{
				logger.warn("could not backup derivate, so it was not deleted");
				return false;
			}
		}catch(MCRException ex){
			logger.error("catched error", ex);
			return false;
		}			
	}	
}
