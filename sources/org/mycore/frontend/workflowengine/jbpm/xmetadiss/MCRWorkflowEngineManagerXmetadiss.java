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
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.mycore.services.fieldquery.MCRQueryManager;
import org.mycore.access.MCRAccessManager;
import org.mycore.common.MCRDefaults;
import org.mycore.common.MCRException;
import org.mycore.common.xml.MCRXMLHelper;
import org.mycore.datamodel.metadata.MCRMetaLangText;
import org.mycore.datamodel.metadata.MCRMetaLinkID;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
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
		super();
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

	public void initWorkflowProcess(String initiator) throws MCRException {
		long processID = getUniqueCurrentProcessID(initiator);
		if(processID != 0){
			String errMsg = "there exists another workflow process of " + processType + " for initiator " + initiator;
			logger.warn(errMsg);
			throw new MCRException(errMsg);
		}
		MCRJbpmWorkflowObject wfo = new MCRJbpmWorkflowObject(processType);
		wfo.setInitiator(initiator);
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
				return 0;
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
    		wfo.setStringVariableValue("authorID", authorID);
    		wfo.setWorkflowStatus("existingAuthor");
    		return authorID;
    	} else {
    		authorID = createAuthor(userid, processType);
    		wfo.setStringVariableValue("authorID", authorID);
    		wfo.setWorkflowStatus("existingAuthor");
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
		wfo.setStringVariableValue("reservatedURN", urn);
		wfo.setWorkflowStatus("urnCreated");
		return urn;					
	}
		
	public String getMetadataDocumentID(String userid){
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
		docID = createDisshab(authorID, urn);
		wfo.setStringVariableValue("createdDocID", docID);
		wfo.setWorkflowStatus("disshabCreated");
		return docID;					
	}

	
	private String createDisshab(String sAuthorID, String sUrn){		
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
			creatorlink.setReference(sAuthorID,sAuthorID,sAuthorName);

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
			eUrns.addContent(eUrn);

			metadata.addContent(eCreators);
			metadata.addContent(eCreatorLinks);
			metadata.addContent(eUrns);
		}
	      
		mycoreobject.addContent(structure);
		mycoreobject.addContent(metadata);
		mycoreobject.addContent(service);
	    
		// ID Setzen
		String nextID = JSPUtils.getNextFreeID("disshab");
		mycoreobject.setAttribute("ID", nextID);	 
		mycoreobject.setAttribute("label", nextID);

		Document mycoreobjectdoc = new Document(mycoreobject);
		MCRObject disshab = new MCRObject();
		disshab.setFromJDOM(mycoreobjectdoc);
		try {
			String type = disshab.getId().getTypeId();
			String savedir = config.getString("MCR.editor_" + type + "_directory");
			JSPUtils.saveToDirectory(disshab, savedir);		
		} catch ( Exception ex){
			//TODO Fehlermeldung
			logger.warn("Could not create disshab object " +  nextID );
			return "";
		}	
   	    return disshab.getId().getId();		
	}


	protected MCRJbpmWorkflowObject getWorkflowObject(String userid) {
		long curProcessID = getUniqueCurrentProcessID(userid);
		if(curProcessID == 0){
			logger.warn("no " + processType + " workflow found for user " + userid);
			return null;
		}
			
		return new MCRJbpmWorkflowObject(curProcessID);		
	}
	     

}
