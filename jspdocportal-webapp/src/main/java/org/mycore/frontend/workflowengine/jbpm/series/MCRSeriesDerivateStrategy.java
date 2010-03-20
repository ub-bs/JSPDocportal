package org.mycore.frontend.workflowengine.jbpm.series;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import org.jbpm.context.exe.ContextInstance;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRException;
import org.mycore.common.MCRUtils;
import org.mycore.datamodel.metadata.MCRDerivate;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowConstants;
import org.mycore.frontend.workflowengine.strategies.MCRDefaultDerivateStrategy;

public class MCRSeriesDerivateStrategy extends MCRDefaultDerivateStrategy {
	private static Logger logger = Logger.getLogger(MCRSeriesDerivateStrategy.class.getName());
	
	/**
	 * TODO Cleanup that mess in saving thesis derivates!!
	 * Develop a concept how to handle 1 MainFile as PDF and 1 PDF or ZIP File as attachement 1!!
	 */
	public void saveFiles(List files, String dirname, ContextInstance ctxI, String newLabel) throws MCRException {
	// a correct dissertation contains in the main derivate
	//		exactly one pdf-file and optional an attachment zip-file
	//		the pdf file will be renamed to dissertation.pdf
	//		in a derivate only one zip-file is allowed, it will be renamed
	//		to attachment.zip

	MCRDerivate der = new MCRDerivate();
	boolean bUpdateDerivate=false;
	
	try {
			der.setFromURI(new File(dirname + ".xml").toURI());
		}catch(Exception ex){
			String errMsg = "could not set derivate " + dirname + ".xml";
			logger.error(errMsg, ex);
			throw new MCRException(errMsg);
		}
		
		String derID = der.getId().getId();
		
		boolean containsPdf = false;
		boolean containsAttachement = false;
		// save the files
	
		ArrayList<String> ffname = new ArrayList<String>();
		String mainfile = "";
		int newFileCnt = 0;
		
		for (int i = 0; files !=null && i < files.size(); i++) {
			FileItem item = (FileItem) (files.get(i));
			String fname = item.getName().trim();
			Matcher mat = filenamePattern.matcher(fname);
			while(mat.find()){
				fname = mat.group(1);
			}
			fname.replace(' ', '_');
			ffname.add(fname);
			// IE Explorer Bug, the name includes the whole path
			if (fname.indexOf("/") >0)
				fname = fname.substring(fname.lastIndexOf("/")+1);
			if (fname.indexOf("\\") >0 )
				fname = fname.substring(fname.lastIndexOf("\\")+1);

			String fileextension = null;
			mat = fileextensionPattern.matcher(fname);
			while(mat.find()) {
				fileextension = mat.group(1);
			}
			fileextension = fileextension.toLowerCase();
			if(fileextension.equals("zip")) {
				if(containsAttachement) {
					String errMsg = "just one file allowed for each derivate";
					logger.error(errMsg);
					throw new MCRException(errMsg);
				}else{
					containsAttachement = true;
					//fname = "attachment.zip";
					fname=normalizeFilename(fname);
					der.setLabel(MCRConfiguration.instance().getString("MCR.Derivates.Labels.atachment", "Anhang"));
				}
			}else if(fileextension.equals("pdf")) {
				if(containsPdf) {
					if(!containsAttachement){
						containsAttachement = true;
						//fname = "attachment.zip";
						fname=normalizeFilename(fname);
						der.setLabel(MCRConfiguration.instance().getString("MCR.Derivates.Labels.atachment", "Anhang"));
					}
					else{
						String errMsg = "just one pdf-file as main file and one pdf or zip file as attachement allowed for a thesis, please delete old derivates first";
						logger.error(errMsg);
						throw new MCRException(errMsg);
					}
				}else{
					String wfPdf = (String)ctxI.getVariable(MCRWorkflowConstants.WFM_VAR_CONTAINS_PDF); 
					if(wfPdf != null && !wfPdf.equals(derID) && !wfPdf.equals("")){
						if(!containsAttachement){
							containsAttachement = true;
							containsPdf=false;
							//fname = "attachment.zip";
							fname=normalizeFilename(fname);
							der.setLabel(MCRConfiguration.instance().getString("MCR.Derivates.Labels.atachment", "Anhang"));
						}
						else{
							String errMsg = "just one pdf-file for all derivates for one thesis, please delete old derivates first";
							logger.error(errMsg);
							throw new MCRException(errMsg);
						}
					}else{
						//fname = "dissertation.pdf";
						containsPdf=true;
						fname=normalizeFilename(fname);
						mainfile = fname;
						der.setLabel(MCRConfiguration.instance().getString("MCR.Derivates.Labels.fulltext", "Volltext"));
					}
				}
			}
			else{
				fname=normalizeFilename(fname);
			}
			try{
				File fout = new File(dirname, fname);
				FileOutputStream fouts = new FileOutputStream(fout);
				InputStream fin = item.getInputStream();
				fouts = new FileOutputStream(fout);
				MCRUtils.copyStream(fin, fouts);
				fin.close();
				fouts.flush();
				fouts.close();
				logger.info("Data object stored under " + fout.getName());
				newFileCnt++;				
			}catch(Exception ex){
				String errMsg = "could not store data object " + fname;
				logger.error(errMsg, ex);
				throw new MCRException(errMsg);
			}
		}
		if (der.getDerivate().getInternals().getMainDoc().equals("#####")) {
			der.getDerivate().getInternals().setMainDoc(mainfile);
			bUpdateDerivate=true;
		}
		if ( newLabel != null && newLabel.length()>0 ) {
			der.setLabel(newLabel);
			bUpdateDerivate=true;
		}

		// update the Derivate...xml file
		try{
			if ( bUpdateDerivate ){
				byte[] outxml = MCRUtils.getByteArray(der.createXML());
				try {
					FileOutputStream out = new FileOutputStream(dirname	+ ".xml");
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
			ctxI.setVariable(MCRWorkflowConstants.WFM_VAR_CONTAINS_PDF, derID);
			if(containsAttachement){
				ctxI.setVariable("containsZIP", derID);
			}
		}
		String attachedDerivates = (String) ctxI.getVariable(MCRWorkflowConstants.WFM_VAR_ATTACHED_DERIVATES);			
		
		if ( derID.length() >0 && attachedDerivates.indexOf(derID)<0  ) {
			if ( attachedDerivates.trim().length() > 0) {
				attachedDerivates += ",";
			}
			ctxI.setVariable("attachedDerivates", attachedDerivates  + derID);
		}
		String fcnt = (String)ctxI.getVariable(MCRWorkflowConstants.WFM_VAR_FILECNT);
		try {
			int cnt = Integer.parseInt(fcnt);
			ctxI.setVariable(MCRWorkflowConstants.WFM_VAR_FILECNT, String.valueOf( cnt + newFileCnt) );	
		} catch ( NumberFormatException nonum ) {
			ctxI.setVariable(MCRWorkflowConstants.WFM_VAR_FILECNT, String.valueOf( newFileCnt) );	
		}			
		
	}
	
	public boolean deleteDerivateObject(ContextInstance ctxI, String saveDirectory, String backupDirectory, String metadataObjectID, 
			String derivateObjectID, boolean mustWorkflowVarBeUpdated) {
		try{
			if (backupDerivateObject(saveDirectory, backupDirectory, metadataObjectID, derivateObjectID, ctxI.getProcessInstance().getId())){
				if(super.deleteDerivateObject(ctxI, saveDirectory, backupDirectory, metadataObjectID, derivateObjectID, mustWorkflowVarBeUpdated) && mustWorkflowVarBeUpdated){
					String cmp = (String)ctxI.getVariable(MCRWorkflowConstants.WFM_VAR_CONTAINS_PDF); 
					if(cmp != null && cmp.equals(derivateObjectID))
						ctxI.setVariable(MCRWorkflowConstants.WFM_VAR_CONTAINS_PDF,"");
					cmp = (String)ctxI.getVariable("containsZIP");
					if(cmp != null && cmp.equals(derivateObjectID))
						ctxI.setVariable("containsZIP", "");
					return true;
				}else{
					logger.error("problems in deleting, check inconsistences in workflow process " + ctxI.getProcessInstance().getId());
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
