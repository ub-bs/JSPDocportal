package org.mycore.frontend.workflowengine.strategies;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.mycore.common.MCRDefaults;
import org.mycore.datamodel.metadata.MCRMetaAddress;
import org.mycore.datamodel.metadata.MCRMetaBoolean;
import org.mycore.datamodel.metadata.MCRMetaLangText;
import org.mycore.datamodel.metadata.MCRMetaPersonName;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowUtils;
import org.mycore.services.fieldquery.MCRResults;
import org.mycore.user2.MCRUser;
import org.mycore.user2.MCRUserContact;
import org.mycore.user2.MCRUserMgr;

public class MCRDefaultAuthorStrategy implements MCRAuthorStrategy {
	private static Logger logger = Logger.getLogger(MCRDefaultAuthorStrategy.class.getName());


	public MCRObjectID createAuthor(String userid, MCRObjectID nextFreeAuthorId){
		
		MCRUser user = null;
		try {
			user = MCRUserMgr.instance().retrieveUser(userid);
		} catch (Exception noUser) {
			//TODO Fehlermeldung
			logger.warn("user does'nt exist userid=" + userid);
			return null;			
		}	
		MCRResults mcrResult =  MCRWorkflowUtils.queryMCRForAuthorByUserid(userid);
		logger.debug("Results found hits:" + mcrResult.getNumHits());    
		if ( mcrResult.getNumHits() > 0 ) {
			String authorID = mcrResult.getHit(0).getID();
			return new MCRObjectID(authorID);
		}
		
		MCRObject author = createAuthorFromUser(user, nextFreeAuthorId);
		try {
			author.createInDatastore();
		} catch ( Exception ex){
			//TODO Fehlermeldung
			logger.warn("Could not Create authors object from the user object " + user.getID());
			return null;
		}
   	    return author.getId();		
	}
	
	private MCRObject createAuthorFromUser(MCRUser user, MCRObjectID id) {
		MCRObject author = new MCRObject();
		Element xmlElemAuthor = new Element("mycoreobject");
		xmlElemAuthor.addNamespaceDeclaration(org.jdom.Namespace.getNamespace(
				"xsi", MCRDefaults.XSI_URL));
		xmlElemAuthor.setAttribute("noNamespaceSchemaLocation",
				"datamodel-author.xsd", org.jdom.Namespace.getNamespace("xsi",
						MCRDefaults.XSI_URL));
		xmlElemAuthor.setAttribute("ID", id.toString());
		xmlElemAuthor.setAttribute("label", id.toString());

		Element structure = new Element("structure");
		Element metadata = new Element("metadata");
		Element service = new Element("service");

		if (user != null) {
			MCRMetaPersonName pname = new MCRMetaPersonName();
			String fullname = user.getUserContact().getFirstName() + " "
					+ user.getUserContact().getLastName();
			pname.setSubTag("name");
			pname.setLang("de");
			pname.set(user.getUserContact().getFirstName(), user
					.getUserContact().getLastName(), user.getUserContact()
					.getLastName(), fullname, "", "", user.getUserContact()
					.getSalutation());

			MCRMetaBoolean female = new MCRMetaBoolean();
			female.setSubTag("female");
			female.setLang("de");
			female.setValue("false");
			if (user.getUserContact().getSalutation().equals("Frau"))
				female.setValue("true");

			MCRMetaAddress padr = new MCRMetaAddress();
			padr.setSubTag("address");
			padr.setLang("de");
			
			MCRUserContact userContact = user.getUserContact();

			if (userContact.getCountry().length() == 0) {
				userContact.setCountry("-");
			}
			if (userContact.getState().length() == 0) {
				userContact.setState("-");
			}
			if (userContact.getPostalCode().length() == 0) {
				userContact.setPostalCode("-");
			}
			if (userContact.getCity().length() == 0) {
				userContact.setCity("-");
			}
			if (userContact.getStreet().length() == 0) {
				userContact.setStreet("-");
			}
			if (userContact.getEmail().length() == 0) {
				userContact.setEmail("-");
			}

			padr.set(userContact.getCountry(), userContact
					.getState(), userContact.getPostalCode(), user
					.getUserContact().getCity(), userContact
					.getStreet(), "-");

			MCRMetaLangText userID = new MCRMetaLangText();
			userID.setSubTag("userid");
			userID.setLang("de");
			userID.setText(user.getID());

			
			Element ePname = pname.createXML();
			Element ePnames = new Element("names");
			ePnames.setAttribute("class", "MCRMetaPersonName");
			ePnames.setAttribute("textsearch", "true");
			ePnames.addContent(ePname);

			Element eFemale = female.createXML();
			Element eFemales = new Element("females");
			eFemales.setAttribute("class", "MCRMetaBoolean");
			eFemales.addContent(eFemale);

			Element ePadr = padr.createXML();
			Element ePadrs = new Element("addresses");
			ePadrs.setAttribute("class", "MCRMetaAddress");
			ePadrs.addContent(ePadr);

			Element eUserID = userID.createXML();
			Element eUserIDs = new Element("userids");
			eUserIDs.setAttribute("class", "MCRMetaLangText");
			eUserIDs.addContent(eUserID);
			
			MCRMetaLangText email = new MCRMetaLangText();
			email.setSubTag("email");
			email.setLang("de");
			email.setText(userContact.getEmail());
				
			Element eEmail = email.createXML();
			Element eEmails = new Element("emails");
			eEmails.setAttribute("class", "MCRMetaLangText");
			eEmails.addContent(eEmail); 
			
			metadata.addContent(ePnames);
			metadata.addContent(eFemales);
			metadata.addContent(ePadrs);
			metadata.addContent(eUserIDs);
			metadata.addContent(eEmails);
			
		} else{
			//metadata needs dummy data otherwise  author.createXML() failes
			Element ePnames = new Element("names");
			ePnames.setAttribute("class", "MCRMetaPersonName");
			ePnames.setAttribute("textsearch", "true");
						
			MCRMetaPersonName pname = new MCRMetaPersonName();
			pname.setSubTag("name");
			pname.setLang("de");
			pname.set("", "", "Neuer Autor", "", "", "", "");
			ePnames.addContent(pname.createXML());
						
			metadata.addContent(ePnames);
		}

		xmlElemAuthor.addContent(structure);
		xmlElemAuthor.addContent(metadata);
		xmlElemAuthor.addContent(service);

		Document authordoc = new Document(xmlElemAuthor);

		author.setFromJDOM(authordoc);

		// Robert: redundant?
		author.setId(id);

		return author;
	}		
}
