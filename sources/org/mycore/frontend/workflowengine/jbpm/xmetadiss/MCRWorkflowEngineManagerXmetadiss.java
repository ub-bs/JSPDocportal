/**
 * $RCSfile$
 * $Revision$ $Date$
 *
 * This file is part of ** M y C o R e **
 * Visit our homepage at http://www.mycore.de/ for details.
 *
 * This program is free software; you can use it, redistribute it
 * and / or modify it under the terms of the GNU General Public License
 * (GPL) as published by the Free Software Foundation; either version 2
 * of the License or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program, normally in the file license.txt.
 * If not, write to the Free Software Foundation Inc.,
 * 59 Temple Place - Suite 330, Boston, MA  02111-1307 USA
 *
 **/

// package
package org.mycore.frontend.workflowengine.jbpm.xmetadiss;

// Imported java classes
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.mycore.services.fieldquery.MCRQueryManager;
import org.mycore.access.MCRAccessManager;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRDefaults;
import org.mycore.common.MCRException;
import org.mycore.common.MCRUtils;
import org.mycore.common.xml.MCRXMLHelper;
import org.mycore.datamodel.metadata.MCRDerivate;
import org.mycore.datamodel.metadata.MCRMetaLangText;
import org.mycore.datamodel.metadata.MCRMetaLinkID;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.cli.MCRObjectCommands;
import org.mycore.frontend.workflowengine.jbpm.MCRJbpmWorkflowBase;
import org.mycore.frontend.workflowengine.jbpm.MCRJbpmWorkflowObject;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerBaseImpl;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerInterface;
import org.mycore.services.fieldquery.MCRResults;
import org.mycore.common.JSPUtils;

/**
 * This class holds methods to manage the workflow file system of MyCoRe.
 * 
 * @author Heiko Helmbrecht, Anja Schaar
 * @version $Revision$ $Date$
 */

public class MCRWorkflowEngineManagerXmetadiss extends MCRWorkflowEngineManagerBaseImpl{
	
	
	private static Logger logger = Logger.getLogger(MCRWorkflowEngineManagerXmetadiss.class.getName());
	private static String processType = "xmetadiss" ;
	private static MCRWorkflowEngineManagerInterface singleton;
	
	protected MCRWorkflowEngineManagerXmetadiss() throws Exception {
	}

	
	/**
	 * Returns the disshab workflow manager singleton.
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static synchronized MCRWorkflowEngineManagerInterface instance() throws Exception {
		if (singleton == null)
			singleton = new MCRWorkflowEngineManagerXmetadiss();
		return singleton;
	}
	
	public long initWorkflowProcess(String initiator) throws MCRException {
		long processID = getUniqueCurrentProcessID(initiator);
		if(processID < 0){
			String errMsg = "there exists another workflow process of " + processType + " for initiator " + initiator;
			logger.warn(errMsg);
			throw new MCRException(errMsg);
		}else if (processID == 0) {
			MCRJbpmWorkflowObject wfo = new MCRJbpmWorkflowObject(processType);
			wfo.initialize(initiator);
			wfo.setStringVariableValue("fileCnt", "0");
			try{
				wfo.signal("go2processInitialized");
				wfo.signal("go2authorCreated");
				wfo.signal("go2urnCreated");
			}catch(MCRException e){
				logger.error("MCRWorkflow Error, could not initialize the workflow process", e);
				throw new MCRException("MCRWorkflow Error, could not initialize the workflow process");
			}
			return wfo.getProcessInstanceID();
		}else{
			return processID;
		}
	}
	
	public Document getListWorkflowProcess(String userid, String workflowProcessType ){
		return super.getListWorkflowProcess( userid,  workflowProcessType, "disshab");
	}
	
	public long getUniqueCurrentProcessID(String userid) {
		List li = getCurrentProcessIDs(userid, processType);
		if(li != null && li.size() > 0) {
			if(li.size() > 1) {
				StringBuffer errorSB = new StringBuffer("there are existing more than one ")
					.append(processType).append(" processIDs. Please delete old ones. Found these ")
					.append("processids: ");
				for (Iterator it = li.iterator(); it.hasNext();) {
					Long processID = (Long) it.next();
					errorSB.append("[").append(processID.longValue()).append("] ");
				}
				logger.error(errorSB.toString());
				return -1;
			}else{
				return ((Long)li.get(0)).longValue();
			}
			
		}else{
			return 0;
		}
	}		

	public String getAuthorFromUniqueWorkflow(String userid){
		MCRJbpmWorkflowObject wfo = getWorkflowObject(userid);
		if(wfo == null || !isUserValid(userid))
			return "";
	
		String authorID = wfo.getStringVariableValue("authorID");
		if(authorID != null && !authorID.equals("")){
			return authorID;
		}
		
		// im WF kein Autor vorhanden, - Direkt aus MyCore Holen	
		// - kann nachher weg - da ja dann die AuthorID immmer im WF steht, dann nur noch create zweig	
		Element query = buildQueryforAuthor(userid);
		Document jQuery = new Document(query);    	
    	MCRResults mcrResult =  MCRQueryManager.search(jQuery);
    	logger.debug("Results found hits:" + mcrResult.getNumHits());    
    	if ( mcrResult.getNumHits() > 0 ) {
    		authorID = mcrResult.getHit(0).getID();
    		return authorID;
    	} else {
    		authorID = createAuthor(userid, processType);
    		return authorID;
    	}
	}
	
	public String getURNReservation(String userid){
		MCRJbpmWorkflowObject wfo = getWorkflowObject(userid);
		if(wfo == null || !isUserValid(userid))
			return "";
		
		String urn = wfo.getStringVariableValue("reservatedURN");
		if(urn != null && !urn.equals("")){
			return urn;
		}

		// im WF keine URN vorhanden, - also in MyCoRe anlegen	
		String authorID = getAuthorFromUniqueWorkflow(userid);
		urn = createUrnReservationForAuthor(authorID, "URN for dissertation", processType);
		return urn;					
	}
		
	public String getMetadataDocumentID(String userid) throws MCRException{
		MCRJbpmWorkflowObject wfo = getWorkflowObject(userid);
		if(wfo == null || !isUserValid(userid))
			return "";

		String docID = wfo.getStringVariableValue("createdDocID");
		if(docID != null && !docID.equals("")) {
			return docID;
		}

		String authorID = getAuthorFromUniqueWorkflow(userid);
		String urn = getURNReservation(userid);
		// im WF noch keine DocID für userid vorhanden - in myCoRe kreieren	
		docID = createDisshab(authorID, userid, urn);
		if(docID != null && !docID.equals("")) {
			wfo.setStringVariableValue("createdDocID", docID);
			wfo.signal("go2disshabCreated");
		}else{
			throw new MCRException("could not create mcr-docid");
		}
		return docID;					
	}

	
	private String createDisshab(String sAuthorID, String userid, String sUrn){		
		if ( !(sAuthorID.length()>0 && sUrn.length()>0)  ){
			logger.warn("Could not create disshab object because empty parameters,  sAuthorID=" + sAuthorID + ", sUrn=" + sUrn);
			return "";
		}
		

		Element mycoreobject = new Element ("mycoreobject");				
		mycoreobject.addNamespaceDeclaration(org.jdom.Namespace.getNamespace("xsi", MCRDefaults.XSI_URL));
		mycoreobject.setAttribute("noNamespaceSchemaLocation", "datamodel-disshab.xsd", org.jdom.Namespace.getNamespace("xsi", MCRDefaults.XSI_URL));		
		
		Element structure = new Element ("structure");			
		Element metadata = new Element ("metadata");	
		Element service = new Element ("service");

		if ( sAuthorID != null ) {
			//MCRObject Author = new MCRObject();
			Document jAuthor =  new MCRObject().receiveJDOMFromDatastore(sAuthorID);
			String sAuthorName = sAuthorID;
			if ( jAuthor != null ) {
			    Iterator it = jAuthor.getDescendants(new ElementFilter("fullname"));
	        	if ( it.hasNext() )    {
	        	      Element el = (Element) it.next();
	        	      sAuthorName = el.getText();
	        	}
			}
			MCRMetaLangText creator = new MCRMetaLangText();
			creator.setSubTag("creator");
			creator.setLang("de");
			creator.setText(sAuthorName);

			MCRMetaLinkID creatorlink = new MCRMetaLinkID();
			creatorlink.setSubTag("creatorlink");
			creatorlink.setLang("de");
			creatorlink.setReference(sAuthorID,sAuthorName,sAuthorName);

			MCRMetaLangText urn = new MCRMetaLangText();
			urn.setSubTag("urn");
			urn.setLang("de");
			urn.setText(sUrn);
			
			Element eCreator = creator.createXML();
			Element eCreators = new Element("creators");
			eCreators.setAttribute("class","MCRMetaLangText");	
			eCreators.addContent(eCreator);
			
			Element eCreatorLink = creatorlink.createXML();
			Element eCreatorLinks = new Element("creatorlinks");			
			eCreatorLinks.setAttribute("class","MCRMetaLinkID");	
			eCreatorLinks.addContent(eCreatorLink);
			
			Element eUrn = urn.createXML();
			Element eUrns = new Element("urns");
			eUrns.setAttribute("class","MCRMetaLangText");
			eUrns.setAttribute("textsearch", "true");
			eUrns.setAttribute("type", "urn_new");
			eUrns.addContent(eUrn);

			metadata.addContent(eCreators);
			metadata.addContent(eCreatorLinks);
			metadata.addContent(eUrns);
		}
	      
		mycoreobject.addContent(structure);
		mycoreobject.addContent(metadata);
		mycoreobject.addContent(service);
	    
		// ID Setzen
		String nextID = getNextFreeID("disshab");
		MCRObjectID id = new MCRObjectID(nextID);
		mycoreobject.setAttribute("ID", nextID);	 
		mycoreobject.setAttribute("label", nextID);

		Document mycoreobjectdoc = new Document(mycoreobject);
		MCRObject disshab = new MCRObject();
		disshab.setFromJDOM(mycoreobjectdoc);
		try {
			String type = disshab.getId().getTypeId();
			String savedir = getWorkflowDirectory(type);
			FileOutputStream fos = new FileOutputStream(savedir + "/" + nextID + ".xml");
			(new XMLOutputter(Format.getPrettyFormat())).output(mycoreobject,fos);
			fos.close();
		} catch ( Exception ex){
			//TODO Fehlermeldung
			logger.warn("Could not create disshab object " +  nextID );
			return "";
		}
		setDefaultPermissions(id.getId(), userid );
   	    return disshab.getId().getId();		
	}

	public void setDefaultPermissions(String mcrid, String userid) {
		setDefaultPermissions(new MCRObjectID(mcrid),"xmetadiss", userid);
	}

	protected MCRJbpmWorkflowObject getWorkflowObject(String userid) {
		long curProcessID = getUniqueCurrentProcessID(userid);
		if(curProcessID == 0){
			logger.warn("no " + processType + " workflow found for user " + userid);
			return null;
		}
		return new MCRJbpmWorkflowObject(curProcessID);		
	}
	
	public void saveFiles(List files, String dirname, long pid) throws MCRException {
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
		
		MCRJbpmWorkflowObject wfo = new MCRJbpmWorkflowObject(pid);
		
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
					String wfPdf = wfo.getStringVariableValue("containsPDF"); 
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
			wfo.setStringVariableValue("containsPDF", derID);
			if(containsZip){
				wfo.setStringVariableValue("containsZIP", derID);
			}
		}
		String attachedDerivates = wfo.getStringVariableValue("attachedDerivates");
		if(attachedDerivates == null || attachedDerivates.equals("")){
			wfo.setStringVariableValue("attachedDerivates", derID);
		}else{
			wfo.setStringVariableValue("attachedDerivates", attachedDerivates + "," + derID);
		}
	}	
	
	public boolean deleteDerivateObject(String documentType, String metadataObject, String derID) {
		List lpids = MCRJbpmWorkflowBase.getCurrentProcessIDsForProcessVariable("createdDocID", metadataObject);
		long pid = 0;
		if(lpids != null && lpids.size() == 1) {
			pid = ((Long)lpids.get(0)).longValue();
		}
		MCRJbpmWorkflowObject wfo = new MCRJbpmWorkflowObject(pid);
		Set attachedDerivates = new HashSet(Arrays.asList(wfo.getStringVariableValue("attachedDerivates").split(",")));
		attachedDerivates.remove(derID);
		StringBuffer sbAttached = new StringBuffer("");
		boolean first = true;
		for (Iterator it = attachedDerivates.iterator(); it.hasNext();) {
			if(!first)
				sbAttached.append(",");
			sbAttached.append((String) it.next());
			first = false;
		}
		if (backupDerivateObject(documentType, metadataObject, derID, pid)){
			if(super.deleteDerivateObject(documentType, metadataObject, derID)){
				wfo.setStringVariableValue("attachedDerivates", sbAttached.toString());
				String cmp = wfo.getStringVariableValue("containsPDF"); 
				if(cmp != null && cmp.equals(derID))
					wfo.setStringVariableValue("containsPDF","");
				cmp = wfo.getStringVariableValue("containsZIP");
				if(cmp != null && cmp.equals(derID))
					wfo.setStringVariableValue("containsZIP", "");
				return true;
			}else{
				logger.error("problems in deleting, check inconsistences in workflow process " + pid);
				return false;
			}
		}else{
			logger.warn("could not backup derivate, so it was not deleted");
			return false;
		}
	}
	
	public boolean commitWorkflowObject(String objmcrid, String documentType) {
		boolean bSuccess = true;
		long pid = getUniqueWorkflowProcessFromCreatedDocID(objmcrid);
		MCRJbpmWorkflowObject wfo = new MCRJbpmWorkflowObject(pid);
		String dirname = getWorkflowDirectory(documentType);
		String filename = dirname + File.separator + objmcrid + ".xml";

		try { 
			if (MCRObject.existInDatastore(objmcrid)) {
				MCRObject mcr_obj = new MCRObject();
				mcr_obj.deleteFromDatastore(objmcrid);
			}
			MCRObjectCommands.loadFromFile(filename);
			logger.info("The metadata object: " + filename + " is loaded.");
		} catch (Exception ig){ 
			logger.error("Can't load File catched error: ", ig);
			bSuccess=false;
		}
		if ( (bSuccess = MCRObject.existInDatastore(objmcrid))  ) {
			List derivateIDs = Arrays.asList(wfo.getStringVariableValue("attachedDerivates").split(","));
			try{
				for (Iterator it = derivateIDs.iterator(); it.hasNext();) {
					String derivateID = (String) it.next();
					bSuccess = commitDerivateObject(derivateID, documentType);
				}
			}catch(Exception ex){
				logger.error("Can't load File catched error: ", ex);
				bSuccess=false;
			}
		}
		return bSuccess;
	}
	
	public boolean checkBooleanDecisionNode(long processid, String decisionNode) {
		if(decisionNode.equals("canDisshabBeSubmitted")){
			MCRJbpmWorkflowObject wfo = new MCRJbpmWorkflowObject(processid);
			String authorID = wfo.getStringVariableValue("authorID");
			String reservatedURN = wfo.getStringVariableValue("reservatedURN");
			String createdDocID = wfo.getStringVariableValue("createdDocID");
			String attachedDerivates = wfo.getStringVariableValue("attachedDerivates");
			if(!isEmpty(authorID) && !isEmpty(reservatedURN) && !isEmpty(createdDocID) && !isEmpty(attachedDerivates)){
				String strDocValid = wfo.getStringVariableValue("valid-" + createdDocID );
				String containsPDF = wfo.getStringVariableValue("containsPDF");
				if(strDocValid != null && containsPDF != null){
					if(strDocValid.equals("true") && containsPDF.equals("true")){
						return true;
					}
				}
			}
			return false;
		}
		return false;
	}
	
	public boolean isEmpty(String test){
		if(test == null || test.equals("")){
			return true;
		}else{
			return false;
		}
	}
}
