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
package org.mycore.frontend.workflowengine.jbpm.publication;

// Imported java classes
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import org.jbpm.graph.exe.ExecutionContext;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.mycore.common.MCRDefaults;
import org.mycore.common.MCRException;
import org.mycore.common.MCRUtils;
import org.mycore.datamodel.metadata.MCRDerivate;
import org.mycore.datamodel.metadata.MCRMetaLangText;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.cli.MCRObjectCommands;
import org.mycore.frontend.workflowengine.jbpm.MCRJbpmWorkflowBase;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerBaseImpl;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerInterface;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowProcess;
import org.mycore.user2.MCRUser;
import org.mycore.user2.MCRUserMgr;

/**
 * This class holds methods to manage the workflow file system of MyCoRe.
 * 
 * @author Heiko Helmbrecht, Anja Schaar
 * @version $Revision$ $Date$
 */

public class MCRWorkflowEngineManagerPublication extends MCRWorkflowEngineManagerBaseImpl{
	
	
	private static Logger logger = Logger.getLogger(MCRWorkflowEngineManagerPublication.class.getName());
	private static String processType = "publication" ;
	private static MCRWorkflowEngineManagerInterface singleton;
	
	private static boolean multipleInstancesAllowed = false;
	
	protected MCRWorkflowEngineManagerPublication() throws Exception {
	}

	
	/**
	 * Returns the document workflow manager singleton.
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static synchronized MCRWorkflowEngineManagerInterface instance() throws Exception {
		if (singleton == null)
			singleton = new MCRWorkflowEngineManagerPublication();
		return singleton;
	}
	
	public long initWorkflowProcess(String initiator) throws MCRException {
			long  procID = 0;
			MCRWorkflowProcess wfp = createWorkflowObject(processType);
			try{
				wfp.initialize(initiator);
				wfp.save();
				MCRUser user = MCRUserMgr.instance().retrieveUser(initiator);
				String email = user.getUserContact().getEmail();
				if(email != null && !email.equals("")){
					wfp.setStringVariable(MCRJbpmWorkflowBase.varINITIATOREMAIL, email);
				}
				String salutation = user.getUserContact().getSalutation();
				if(salutation != null && !salutation.equals("")){
					wfp.setStringVariable(MCRJbpmWorkflowBase.varINITIATORSALUTATION, salutation);
				}
				wfp.setStringVariable("fileCnt", "0");
				wfp.endTask("initialization", initiator, null);
				wfp.signal("go2isInitiatorsEmailAddressAvailable");
				procID = wfp.getProcessInstanceID();
			}catch(MCRException e){
				logger.error("MCRWorkflow Error, could not initialize the workflow process", e);
				throw new MCRException("MCRWorkflow Error, could not initialize the workflow process");
			}finally{
				wfp.close();
			}
			return procID;
        /*			
		}else{
			return processID;
		}*/
	}
	
	protected boolean areMultipleInstancesAllowed(){
		return multipleInstancesAllowed;
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
	
	public String createURNReservation(String userid,long pid){
		String urn = "";
		MCRWorkflowProcess wfp = getWorkflowObject(pid);
		try{
			if(wfp == null || !isUserValid(userid))
				return "";
			
			urn = wfp.getStringVariable("reservatedURN");
			if(urn != null && !urn.equals("")){
				return urn;
			}
	
			// im WF keine URN vorhanden, - also in MyCoRe anlegen	
			// String authorID = createAuthorFromInitiator(userid);
			urn = createUrnReservationForAuthor( userid, "URN for document", processType);
		}catch(MCRException ex){
			logger.error("catched error", ex);
		}finally{
			if(wfp != null)
				wfp.close();
		}			
		return urn;					
	}
		
	public String createMetadataDocumentID(String userid, long pid) throws MCRException{
		String docID = "";
		MCRWorkflowProcess wfp = getWorkflowObject(pid);
		try{
			if(wfp == null || !isUserValid(userid))
				return "";
	
			docID = wfp.getStringVariable("createdDocID");
			if(docID != null && !docID.equals("")) {
				return docID;
			}
	
			String urn = createURNReservation(userid, pid);
			// im WF noch keine DocID f�r userid vorhanden - in myCoRe kreieren	
			docID = createDocument(userid, urn);
		}catch(MCRException ex){
			logger.error("catched error", ex);
		}finally{
			if(wfp != null)
				wfp.close();
		}			
		return docID;					
	}

	
	private String createDocument( String userid, String sUrn){		
		if ( !(sUrn.length()>0)  ){
			logger.warn("Could not create document object because empty parameters: sUrn=" + sUrn);
			return "";
		}
		Element mycoreobject = new Element ("mycoreobject");				
		mycoreobject.addNamespaceDeclaration(org.jdom.Namespace.getNamespace("xsi", MCRDefaults.XSI_URL));
		mycoreobject.setAttribute("noNamespaceSchemaLocation", "datamodel-document.xsd", org.jdom.Namespace.getNamespace("xsi", MCRDefaults.XSI_URL));		
		
		Element structure = new Element ("structure");			
		Element metadata = new Element ("metadata");	
		Element service = new Element ("service");

		if ( sUrn != null ) {
			MCRMetaLangText urn = new MCRMetaLangText();
			urn.setSubTag("urn");
			urn.setLang("de");
			urn.setText(sUrn);
			
			Element eUrn = urn.createXML();
			eUrn.setAttribute("type", "urn_new");
			
			Element eUrns = new Element("urns");
			eUrns.setAttribute("class","MCRMetaLangText");
			eUrns.setAttribute("textsearch", "true");
			eUrns.addContent(eUrn);

			metadata.addContent(eUrns);
		}
	      
		mycoreobject.addContent(structure);
		mycoreobject.addContent(metadata);
		mycoreobject.addContent(service);
	    
		// ID Setzen
		String nextID = getNextFreeID("document");
		MCRObjectID id = new MCRObjectID(nextID);
		mycoreobject.setAttribute("ID", nextID);	 
		mycoreobject.setAttribute("label", nextID);

		Document mycoreobjectdoc = new Document(mycoreobject);
		MCRObject publication = new MCRObject();
		publication.setFromJDOM(mycoreobjectdoc);
		try {
			String type = publication.getId().getTypeId();
			String savedir = getWorkflowDirectory(type);
			FileOutputStream fos = new FileOutputStream(savedir + "/" + nextID + ".xml");
			(new XMLOutputter(Format.getPrettyFormat())).output(mycoreobject,fos);
			fos.close();
		} catch ( Exception ex){
			//TODO Fehlermeldung
			logger.warn("Could not create document object " +  nextID );
			return "";
		}
		setDefaultPermissions(id.getId(), userid );
   	    return publication.getId().getId();		
	}

	public void setDefaultPermissions(String mcrid, String userid) {
		setDefaultPermissions(new MCRObjectID(mcrid),"publication", userid);
	}

	/**
	 * @deprecated
	 */
	protected MCRWorkflowProcess getWorkflowObject(String userid) {
		long curProcessID = getUniqueCurrentProcessID(userid);
		if(curProcessID == 0){
			logger.warn("no " + processType + " workflow found for user " + userid);
			return null;
		}
		return getWorkflowObject(curProcessID);		
	}
	
	public void saveFiles(List files, String dirname, long pid) throws MCRException {
		// a correct publication can containing some derivates 

		MCRDerivate der = new MCRDerivate();
		
		try {
			der.setFromURI(dirname + ".xml");
		}catch(Exception ex){
			String errMsg = "could not set derivate " + dirname + ".xml";
			logger.error(errMsg, ex);
			throw new MCRException(errMsg);
		}
		
		String derID = der.getId().getId();
		
		MCRWorkflowProcess wfp = getWorkflowObject(pid);
		try{
			// save the files	
			ArrayList ffname = new ArrayList();
			String mainfile = "";
			for (int i = 0; i < files.size(); i++) {
				FileItem item = (FileItem) (files.get(i));
				String fname = item.getName().trim();
				fname.replace(' ', '_');
				ffname.add(fname);
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
						FileOutputStream out = new FileOutputStream(dirname + ".xml");
						out.write(outxml);
						out.flush();
						out.close();
					} catch (IOException ex) {
						logger.error(ex.getMessage());
						logger.error("Exception while store to file " + dirname		+ ".xml", ex);
						throw ex;
					}
				}
			} catch (Exception e) {
				String msgErr = "Can't open file " + dirname + ".xml"; 
				logger.error(msgErr, e);
				throw new MCRException(msgErr);
			}
			String attachedDerivates = wfp.getStringVariable("attachedDerivates");
			if(attachedDerivates == null || attachedDerivates.equals("")){
				wfp.setStringVariable("attachedDerivates", derID);
			}else{
				wfp.setStringVariable("attachedDerivates", attachedDerivates + "," + derID);
			}
		}catch(MCRException ex){
			logger.error("catched error", ex);
		}finally{
			if(wfp != null)
				wfp.close();
		}			
	}	
	
	public boolean deleteDerivateObject(String documentType, String metadataObject, String derID) {
		List lpids = MCRJbpmWorkflowBase.getCurrentProcessIDsForProcessVariable("createdDocID", metadataObject);
		long pid = 0;
		if(lpids != null && lpids.size() == 1) {
			pid = ((Long)lpids.get(0)).longValue();
		}
		MCRWorkflowProcess wfp = getWorkflowObject(pid);
		try{
			HashSet attachedDerivates = new HashSet(Arrays.asList(wfp.getStringVariable("attachedDerivates").split(",")));
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
					wfp.setStringVariable("attachedDerivates", sbAttached.toString());
					return true;
				}else{
					logger.error("problems in deleting, check inconsistences in workflow process " + pid);
					return false;
				}
			}else{
				logger.warn("could not backup derivate, so it was not deleted");
				return false;
			}
		}catch(MCRException ex){
			logger.error("catched error", ex);
			return false;
		}finally{
			if(wfp != null)
				wfp.close();
		}
	}
	
	public boolean commitWorkflowObject(String objmcrid, String documentType) {
		boolean bSuccess = false;
		try{
			long pid = getUniqueWorkflowProcessFromCreatedDocID(objmcrid);
			String dirname = getWorkflowDirectory(documentType);
			String filename = dirname + File.separator + objmcrid + ".xml";
	
			MCRWorkflowProcess wfp = getWorkflowObject(pid);
			try { 
				if (MCRObject.existInDatastore(objmcrid)) {
					MCRObject mcr_obj = new MCRObject();
					mcr_obj.deleteFromDatastore(objmcrid);
				}
				MCRObjectCommands.loadFromFile(filename);
				logger.info("The metadata object: " + filename + " is loaded.");
				if ( (bSuccess = MCRObject.existInDatastore(objmcrid))  ) {
					List derivateIDs = Arrays.asList(wfp.getStringVariable("attachedDerivates").split(","));
					for (Iterator it = derivateIDs.iterator(); it.hasNext();) {
						String derivateID = (String) it.next();
						if(!(bSuccess = commitDerivateObject(derivateID, documentType))){
							break;
						}
					}
				}
			}catch(Exception ex){
				logger.error("Can't load File catched error: ", ex);
				bSuccess=false;
			}finally{
				wfp.close();
			}
		}catch(Exception e){
			logger.error("could not commit object");
			bSuccess = false;
		}
		return bSuccess;
	}
	
	public String checkDecisionNode(long processid, String decisionNode, ExecutionContext executionContext) {
		if(decisionNode.equals("canDocumentBeSubmitted")){
			if(checkSubmitVariables(processid)){
				return "documentCanBeSubmitted";
			}else{
				return "documentCantBeSubmitted";
			}
		}else if(decisionNode.equals("canDocumentBeCommitted")){
			if(checkSubmitVariables(processid)){
				return "go2wasCommitmentSuccessful";
			}else{
				return "go2sendBackToDocumentCreated";
			}
		}
		return null;
	}
	
	private boolean checkSubmitVariables(long processid){
		MCRWorkflowProcess wfp = getWorkflowObject(processid);
		try{
			String reservatedURN = wfp.getStringVariable("reservatedURN");
			String createdDocID = wfp.getStringVariable("createdDocID");
			// 	is optionally	
			// String attachedDerivates = wfp.getStringVariable("attachedDerivates");
			if(!isEmpty(reservatedURN) && !isEmpty(createdDocID) ){
				String strDocValid = wfp.getStringVariable(VALIDPREFIX + createdDocID );
				if(strDocValid.equals("true") ){
					return true;
				}
			}
			return false;
		}catch(MCRException ex){
			logger.error("catched error", ex);
			return false;
		}finally{
			if(wfp != null)
				wfp.close();
		}			
	}
	
	public void setWorkflowVariablesFromMetadata(String mcrid, Element metadata){
		long pid = getUniqueWorkflowProcessFromCreatedDocID(mcrid);
		MCRWorkflowProcess wfp = getWorkflowObject(pid);
		try{
			StringBuffer sbTitle = new StringBuffer("");
			for(Iterator it = metadata.getDescendants(new ElementFilter("title")); it.hasNext();){
				Element title = (Element)it.next();
				if(title.getAttributeValue("type").equals("original-main"))
					sbTitle.append(title.getText());
			}
			wfp.setStringVariable("wfo-title", sbTitle.toString());	
		}catch(MCRException ex){
			logger.error("catched error", ex);
		}finally{
			if(wfp != null)
				wfp.close();
		}			
	}	
	
}
