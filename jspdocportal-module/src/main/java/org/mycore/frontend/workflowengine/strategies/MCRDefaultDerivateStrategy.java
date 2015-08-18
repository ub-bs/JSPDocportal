package org.mycore.frontend.workflowengine.strategies;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jbpm.context.exe.ContextInstance;
import org.mycore.common.JSPUtils;
import org.mycore.common.MCRException;
import org.mycore.common.MCRUtils;
import org.mycore.datamodel.metadata.MCRDerivate;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowConstants;
import org.xml.sax.SAXParseException;


public class MCRDefaultDerivateStrategy extends MCRDerivateStrategy {
	private static Logger logger = Logger.getLogger(MCRDefaultDerivateStrategy.class.getName());

	public boolean removeDerivates(ContextInstance ctxI, String saveDirectory, String backupDirectory){
		try{
			String sderids = (String)ctxI.getVariable(MCRWorkflowConstants.WFM_VAR_ATTACHED_DERIVATES);
			if (sderids == null || sderids.length() == 0) {
				// there exist no derivates
				return true;
			}
			List<?> attachedDerivates = Arrays.asList(sderids.split(","));
			for (Iterator<?> it = attachedDerivates.iterator(); it.hasNext();) {
				String derivateID = (String) it.next();
				if ( derivateID != null && derivateID.length()> 0)
					deleteDerivateObject(ctxI,saveDirectory, backupDirectory, null, derivateID, false);
			}
			ctxI.setVariable(MCRWorkflowConstants.WFM_VAR_ATTACHED_DERIVATES,"");
			return true;
		}catch(MCRException ex){
			logger.error("could not remove derivate in workflow", ex);
		}
		return false;
	}
	
	public boolean deleteDerivateFile(ContextInstance ctxI, String documentDirectory, String backupDirectory, String metadataObjectId, String derivateObjectId, boolean mustWorkflowVarBeUpdated, String filename) {
		String derivateFileName = documentDirectory + SEPARATOR + filename;
		File derFile = new File(derivateFileName);
		
		if(derFile.isFile()){
			logger.debug("deleting file " + derFile.getName());
			derFile.delete();
			String deleted = "";
			if(ctxI.hasVariable(MCRWorkflowConstants.WFM_VAR_DELETED_FILES_IN_DERIVATES)){
				deleted=(String)ctxI.getVariable(MCRWorkflowConstants.WFM_VAR_DELETED_FILES_IN_DERIVATES)+",";
			}
			ctxI.setVariable(MCRWorkflowConstants.WFM_VAR_DELETED_FILES_IN_DERIVATES, deleted+filename);
			
		}else{
			if ( !derFile.exists()) {
				logger.warn("nothing to do, because derivate file does not exist in workflow: "+derivateObjectId);					
			} else {
				logger.warn("nothing to do, because this is not a file: "+derFile.getName());
				return false;
			}
		}
		
		if(mustWorkflowVarBeUpdated){
			String fcnt = (String)ctxI.getVariable(MCRWorkflowConstants.WFM_VAR_FILECNT);
			try {
				int cnt = Integer.parseInt(fcnt);
				ctxI.setVariable(MCRWorkflowConstants.WFM_VAR_FILECNT, String.valueOf( --cnt) );	
			} catch ( NumberFormatException nonum ) {
				ctxI.setVariable(MCRWorkflowConstants.WFM_VAR_FILECNT, "0");
			}			
		}
		
		return false;
	}
	
	public boolean deleteDerivateObject(ContextInstance ctxI, String documentDirectory, String backupDirectory, String metadataObjectId, String derivateObjectId, boolean mustWorkflowVarBeUpdated){
		try{
			String derivateDirectory = documentDirectory + SEPARATOR + derivateObjectId;
			String derivateFileName = documentDirectory +  SEPARATOR + derivateObjectId + ".xml" ;

			File derDir = new File(derivateDirectory);
			File derFile = new File(derivateFileName);
			
			
			if(derDir.isDirectory()) {
				
				logger.debug("deleting directory " + derDir.getName());
				JSPUtils.recursiveDelete(derDir);
				if(mustWorkflowVarBeUpdated){
					String oldFC = (String)ctxI.getVariable(MCRWorkflowConstants.WFM_VAR_FILECNT);
					int iFC = 0;
					if ( derDir.list() != null )
						iFC = Math.abs(Integer.parseInt(oldFC) - derDir.list().length);
					ctxI.setVariable(MCRWorkflowConstants.WFM_VAR_FILECNT,String.valueOf(iFC)) ;
				}
			}else{
				if (!derDir.exists()) {
					logger.warn("nothing to do, because derivate does not exist: " + derivateObjectId);					
				} else {
					logger.warn("object not deleted, because it is no directory: "+ derDir.getName());
					return false;
				}
			}
			if(derFile.isFile()){
				logger.debug("deleting file " + derFile.getName());
				derFile.delete();
			}else{
				if ( !derFile.exists()) {
					logger.warn(derivateObjectId +  " not exist's - do nothing.");					
				} else {
					logger.warn(derFile.getName() + " is not a file, did not delete it");
					return false;
				}
			}
			if(mustWorkflowVarBeUpdated){
				String newDerivates = ((String)ctxI.getVariable(MCRWorkflowConstants.WFM_VAR_ATTACHED_DERIVATES)).replaceAll(derivateObjectId + ",*","");
				ctxI.setVariable(MCRWorkflowConstants.WFM_VAR_ATTACHED_DERIVATES, newDerivates);
				
				String deledetDerIDs = (String)ctxI.getVariable(MCRWorkflowConstants.WFM_VAR_DELETED_DERIVATES);
				deledetDerIDs += "," + derivateObjectId;
				ctxI.setVariable(MCRWorkflowConstants.WFM_VAR_DELETED_DERIVATES, deledetDerIDs);
				
			}
			
		}catch(Exception ex){
			logger.error("problems in deleting", ex);
			return false;
		}
		return true;
	}	
	
	
	public void saveFiles(List files, String dirname, ContextInstance ctxI, String newLabel, String newTitle) throws MCRException {
		logger.debug("!! You are using the saveFiles-DUMMY implementation, must be implemented in subclasses, for workflow-specific file checks");
		
		// save the files
		boolean bUpdateDerivate=false;
		List<String> ffname = new ArrayList<String>();
		String mainfile = "";
		for (int i = 0; i < files.size(); i++) {
			FileItem item = (FileItem) (files.get(i));
			String fname = item.getName().trim();
			Matcher mat = filenamePattern.matcher(fname);
			while(mat.find()){
				fname = mat.group(1);
			}
			fname.replace(' ', '_');
			// IE Explorer Bug, the name includes the whole path
			if (fname.indexOf("/") >0)
				fname = fname.substring(fname.lastIndexOf("/")+1);
			if (fname.indexOf("\\") >0 )
				fname = fname.substring(fname.lastIndexOf("\\")+1);

			ffname.add(fname);
			try{
				File fout = new File(dirname, fname);
				InputStream fin = item.getInputStream();				
				FileOutputStream fouts = new FileOutputStream(fout);
				IOUtils.copy(fin, fouts);
				fin.close();
				fouts.flush();
				fouts.close();
				logger.info("Data object stored under " + fout.getName());
			}catch(Exception ex){
				String errMsg = "could not sotre data object " + fname;
				logger.error(errMsg, ex);
				throw new MCRException(errMsg);
			}
		}
					
		MCRDerivate der = new MCRDerivate();
		try{
			der = new MCRDerivate(new File(dirname + ".xml").toURI());
		}
		catch(SAXParseException e){
			logger.error(e);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (der.getDerivate().getInternals().getMainDoc().equals("#####")) {
			if ((mainfile.length() == 0) && (ffname.size() > 0)) {
				mainfile = (String) ffname.get(0);
				der.getDerivate().getInternals().setMainDoc(mainfile);
				bUpdateDerivate=true;
			}
		}
		if ( newLabel != null && newLabel.length()>0 ) {
			der.setLabel(newLabel);
			bUpdateDerivate=true;
		}		
		try {
			if(bUpdateDerivate){
				byte[] outxml = MCRUtils.getByteArray(der.createXML());
				try {
					FileOutputStream out = new FileOutputStream(dirname	+ ".xml");
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
	
	public String normalizeFilename(String s){
		StringBuffer newName = new StringBuffer();
		for(int i=0;i<s.length();i++){
			char test = s.charAt(i);
			if(test>='a' && test<='z'){newName.append(test); continue;}
			if(test>='A' && test<='Z'){newName.append(test); continue;}
			if(test>='0' && test<='9'){newName.append(test); continue;}
			if(test=='.') {newName.append(test); continue;}
			if(test=='-') {newName.append(test); continue;}
			if(test=='_') {newName.append(test); continue;}
			
//			if(test=='ä') {newName.append("ae"); continue;}
//			if(test=='ö') {newName.append("oe"); continue;}
//			if(test=='ü') {newName.append("ue"); continue;}
//			if(test=='Ä') {newName.append("AE"); continue;}
//			if(test=='Ö') {newName.append("OE"); continue;}
//			if(test=='Ü') {newName.append("UE"); continue;}
//			if(test=='ß') {newName.append("ss"); continue;}
			if(test==' ') {newName.append("_"); continue;}
			//every other char will be ignored.
			newName.append("");
		}
		
		return newName.toString();
	}

	@Override
	public boolean moveDerivateObject(ContextInstance ctxI, String derivateObjectID, int direction) {
		String attachedDerivates = (String)ctxI.getVariable(MCRWorkflowConstants.WFM_VAR_ATTACHED_DERIVATES);
		String[] data = attachedDerivates.split(",");
		int pos = -1;
		for(int i=0;i<data.length;i++){
			if(data[i].equals(derivateObjectID)){
				pos=i;
			}
		}
		if(pos>=0 && pos+direction>=0 && pos+direction<data.length){
			String temp = data[pos];
			data[pos]=data[pos+direction];
			data[pos+direction]=temp;
			StringBuffer sb = new StringBuffer();
			for(int i=0;i<data.length;i++){
				sb.append(data[i]);
				sb.append(",");
			}
			ctxI.setVariable(MCRWorkflowConstants.WFM_VAR_ATTACHED_DERIVATES, sb.toString());
			
		}
   	    return true;	

	}
}
