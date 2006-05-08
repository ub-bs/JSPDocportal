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
package org.mycore.frontend.workflowengine.jbpm.registerauthor;

// Imported java classes
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;

import org.jbpm.graph.exe.ExecutionContext;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.mycore.services.fieldquery.MCRQueryManager;
import org.mycore.common.MCRDefaults;
import org.mycore.common.MCRException;
import org.mycore.common.MCRUtils;
import org.mycore.common.xml.MCRXMLHelper;
import org.mycore.datamodel.metadata.MCRDerivate;
import org.mycore.datamodel.metadata.MCRMetaAddress;
import org.mycore.datamodel.metadata.MCRMetaBoolean;
import org.mycore.datamodel.metadata.MCRMetaLangText;
import org.mycore.datamodel.metadata.MCRMetaLinkID;
import org.mycore.datamodel.metadata.MCRMetaPersonName;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.cli.MCRObjectCommands;
import org.mycore.frontend.workflowengine.jbpm.MCRJbpmWorkflowBase;
import org.mycore.frontend.workflowengine.jbpm.MCRJbpmWorkflowObject;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerBaseImpl;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerInterface;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowProcess;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowUtils;
import org.mycore.services.fieldquery.MCRResults;
import org.mycore.user2.MCRUser;
import org.mycore.user2.MCRUserMgr;
import org.mycore.common.JSPUtils;

/**
 * This class holds methods to manage the workflow file system of MyCoRe.
 * 
 * @author Heiko Helmbrecht, Anja Schaar
 * @version $Revision$ $Date$
 */

public class MCRWorkflowEngineManagerRegisterauthor extends MCRWorkflowEngineManagerBaseImpl{
	
	
	private static Logger logger = Logger.getLogger(MCRWorkflowEngineManagerRegisterauthor.class.getName());
	private static String processType = "registerauthor" ;
	private static MCRWorkflowEngineManagerInterface singleton;
	
	private static boolean multipleInstancesAllowed = true;
	
	protected MCRWorkflowEngineManagerRegisterauthor() throws Exception {
	}

	
	/**
	 * Returns the disshab workflow manager singleton.
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static synchronized MCRWorkflowEngineManagerInterface instance() throws Exception {
		if (singleton == null)
			singleton = new MCRWorkflowEngineManagerRegisterauthor();
		return singleton;
	}
	
	public long initWorkflowProcess(String initiator) throws MCRException {
	/*	long processID = getUniqueCurrentProcessID(initiator);
		if(processID < 0){
			String errMsg = "there exists another workflow process of " + processType + " for initiator " + initiator;
			logger.warn(errMsg);
			throw new MCRException(errMsg);
		}else if (processID == 0) { */
		MCRWorkflowProcess wfo = null;
			try{
				wfo = createWorkflowObject(processType);
				wfo.initialize(initiator);
				MCRUser user = MCRUserMgr.instance().retrieveUser(initiator);
			
				String email = user.getUserContact().getEmail();
				if(email != null && !email.equals("")){
					wfo.setStringVariable(MCRJbpmWorkflowBase.varINITIATOREMAIL, email);
				}
				String salutation = user.getUserContact().getSalutation();
				if(salutation != null && !salutation.equals("")){
					wfo.setStringVariable(MCRJbpmWorkflowBase.varINITIATORSALUTATION, salutation);
				}
				//wfo.setStringVariable("fileCnt", "0");
				//gibt es hier nicht - ist das nicht doppelgemoppelt
				//steht bei uns im start state
				
				wfo.endTask("initialization", initiator, "go2isInitiatorsEmailAdressAvailable");
			//	wfo.signal("go2isInitiatorsEmailAdressAvailable");		
			}catch(MCRException e){
				logger.error("MCRWorkflow Error, could not initialize the workflow process", e);
				throw new MCRException("MCRWorkflow Error, could not initialize the workflow process");
			}finally {
				wfo.close();
			}
			
			return wfo.getProcessInstanceID();
	/*	}else{
			return processID;
		}
	*/
	}
	
	protected boolean areMultipleInstancesAllowed(){
		return multipleInstancesAllowed;
	}	
	
	public String createNewAuthor(String userid, long pid){
		MCRWorkflowProcess wfo = getWorkflowObject(pid);
		if(wfo == null || !isUserValid(userid))
			return "";
	
		String authorID = wfo.getStringVariable("authorID");
		if(authorID != null && !authorID.equals("")){
			return authorID;
		}
		
   		authorID = createNewAuthor(userid, processType, false);
   		return authorID;
	}	
	
	public String createAuthorFromInitiator(String userid, long pid){
		MCRWorkflowProcess wfo = getWorkflowObject(pid);
		if(wfo == null || !isUserValid(userid))
			return "";
	
		String authorID = wfo.getStringVariable("authorID");
		if(authorID != null && !authorID.equals("")){
			return authorID;
		}

		// im WF kein Autor vorhanden, - Direkt aus MyCore Holen	
		// - kann nachher weg - da ja dann die AuthorID immmer im WF steht, dann nur noch create zweig	
	
    	MCRResults mcrResult =  MCRWorkflowUtils.queryMCRForAuthorByUserid(userid);
    	logger.debug("Results found hits:" + mcrResult.getNumHits());    
    	if ( mcrResult.getNumHits() > 0 ) {
    		authorID = mcrResult.getHit(0).getID();
    		return authorID;
    	} else {
    		authorID = createNewAuthor(userid, processType, true);
    		return authorID;
    	}
	}
	
	protected String createNewAuthor(String userid, String workflowProcessType, boolean isFillInUserData){
		MCRUser user = null;
			
		try {
			user = MCRUserMgr.instance().retrieveUser(userid);
		} catch (Exception noUser) {
			//TODO Fehlermeldung
			logger.warn("user dos'nt exist userid=" + userid);
			return "";			
		}
		
		String nextID = getNextFreeID("author");
		MCRObjectID id = new MCRObjectID(nextID);
	
		MCRObject author = null;
		
		if(isFillInUserData) {
			author=MCRWorkflowUtils.createAuthorFromUser(user, id);
		}
		else{
			author=MCRWorkflowUtils.createAuthorFromUser(null, id);
		}
		
		boolean result = saveMCRObjectFile(author, author.createXML().getRootElement());
	    if(!result){
	    	return null;
	    }
	    
		setDefaultPermissions(author.getId(), workflowProcessType, user.getID());
   	    return author.getId().getId();	
   	 }
	
		
	protected boolean saveMCRObjectFile(MCRObject object, Element xmlObject){
	try {
		String type = object.getId().getTypeId();
		String savedir = getWorkflowDirectory(type);
		FileOutputStream fos = new FileOutputStream(savedir + "/" + object.getId().getId() + ".xml");
		(new XMLOutputter(Format.getPrettyFormat())).output(xmlObject,fos);
		fos.close();
	} catch ( Exception ex){
		//TODO Fehlermeldung
		logger.warn("Could not create mycore object " +  object.getId().getId() );
		logger.error(ex);
		return false;
	}
	return true;
}

	
	public void setDefaultPermissions(String mcrid, String userid) {
		setDefaultPermissions(new MCRObjectID(mcrid),"registerauthor", userid);
	}

//	protected MCRJbpmWorkflowObject getWorkflowObject(String userid) {
//		long curProcessID = getUniqueCurrentProcessID(userid);
//		if(curProcessID == 0){
//			logger.warn("no " + processType + " workflow found for user " + userid);
//			return null;
//		}
//		return getWorkflowObject(curProcessID);		
//	}
	
	public boolean commitWorkflowObject(String objmcrid, String documentType) {
		boolean bSuccess = true;
		try{
//			long pid = getUniqueWorkflowProcessFromCreatedDocID(objmcrid);
//			MCRJbpmWorkflowObject wfo = getWorkflowObject(pid);
			String dirname = getWorkflowDirectory(documentType);
			String filename = dirname + File.separator + objmcrid + ".xml";
	
			try { 
				if (MCRObject.existInDatastore(objmcrid)) {
					MCRObject mcr_obj = new MCRObject();
					mcr_obj.deleteFromDatastore(objmcrid);
				}
				MCRObjectCommands.loadFromFile(filename);
				logger.info("The metadata object: " + filename + " is loaded.");
				bSuccess = MCRObject.existInDatastore(objmcrid);
			} catch (Exception ig){ 
				logger.error("Can't load File catched error: ", ig);
				bSuccess=false;
			}
		}catch(Exception e){
			logger.error("could not commit object");
			bSuccess = false;
		}
		return bSuccess;
	}
	
	public String checkDecisionNode(long processid, String decisionNode, ExecutionContext executionContext) {
		if(decisionNode.equals("canAuthorBeSubmitted")){
			if(checkSubmitVariables(processid)){
				return "authorCanBeSubmitted";
			}else{
				return "authorCantBeSubmitted";
			}
		}
		
		if(decisionNode.equals("canAuthorBeCommitted")){
			if(checkSubmitVariables(processid)){
					return "authorCanBeCommitted";
			}
			else{
					return "authorCantBeCommitted";
				}
		}
		return null;
	}
	
	private boolean checkSubmitVariables(long processid){
		MCRWorkflowProcess wfo = getWorkflowObject(processid);
		String createdDocID = wfo.getStringVariable("createdDocID");
		if(!isEmpty(createdDocID)){
			String strDocValid = wfo.getStringVariable(VALIDPREFIX + createdDocID );
			if(strDocValid != null && strDocValid.equals("true")){
				return true;
			}
		}
		return false;
	}

	public void setWorkflowVariablesFromMetadata(String mcrid, Element metadata){
		long pid = getUniqueWorkflowProcessFromCreatedDocID(mcrid);
		MCRWorkflowProcess wfo = getWorkflowObject(pid);
		StringBuffer sbTitle = new StringBuffer("");
		for(Iterator it = metadata.getDescendants(new ElementFilter("title")); it.hasNext();){
			Element title = (Element)it.next();
			if(title.getAttributeValue("type").equals("original-main"))
				sbTitle.append(title.getText());
		}
		wfo.setStringVariable("wfo-title", sbTitle.toString());	
	}	
	
}
