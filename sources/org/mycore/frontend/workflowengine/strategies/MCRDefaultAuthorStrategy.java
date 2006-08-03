package org.mycore.frontend.workflowengine.strategies;

import java.io.FileOutputStream;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
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


	public MCRObjectID createAuthor(String userid, MCRObjectID nextFreeAuthorId, boolean fromUserData, boolean inDatabase){
		MCRObject author = null;
		
		if (fromUserData){
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
				//workflow control should avoid getting here
				String authorID = mcrResult.getHit(0).getID();
				return new MCRObjectID(authorID);
			}
			author = createAuthorFromUser(user, nextFreeAuthorId);
		} else {
			author = createAuthorFromUser(null, nextFreeAuthorId);
		}
		
		try {
			if ( inDatabase) {
				author.createInDatastore();
			} else {
				FileOutputStream fos = new FileOutputStream(
						MCRWorkflowDirectoryManager.getWorkflowDirectory("author")	+ "/" + author.getId().getId() + ".xml");
						(new XMLOutputter(Format.getPrettyFormat())).output(author.createXML(),fos);
				fos.close();
			}
		} catch ( Exception ex){
			//TODO Fehlermeldung
			logger.warn("Could not Create authors object for the user:  " + userid, ex);
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
			StringBuffer sbFullname=new StringBuffer();
			sbFullname.append(user.getUserContact().getLastName());
			if(user.getUserContact().getFirstName().length()>0){
				sbFullname.append(", ");
				sbFullname.append(user.getUserContact().getFirstName());
			}
			String fullname = sbFullname.toString();
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
			if (user.getUserContact().getSalutation().equalsIgnoreCase("Frau"))
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
			metadata.addContent(eEmails);
			metadata.addContent(eUserIDs);
			
//			<xsd:sequence>
//            <xsd:element maxOccurs="1" minOccurs="1" name="names" type="namesType"/>
//            <xsd:element maxOccurs="1" minOccurs="0" name="females" type="femalesType"/>
//            <xsd:element maxOccurs="1" minOccurs="1" name="institutions" type="institutionsType"/>
//            <xsd:element maxOccurs="1" minOccurs="0" name="addresses" type="addressesType"/>
//            <xsd:element maxOccurs="1" minOccurs="0" name="phones" type="phonesType"/>
//            <xsd:element maxOccurs="1" minOccurs="0" name="dates" type="datesType"/>
//            <xsd:element maxOccurs="1" minOccurs="0" name="professions" type="professionsType"/>
//            <xsd:element maxOccurs="1" minOccurs="0" name="profclasses" type="profclassesType"/>
//            <xsd:element maxOccurs="1" minOccurs="0" name="nationals" type="nationalsType"/>
//            <xsd:element maxOccurs="1" minOccurs="0" name="urls" type="urlsType"/>
//            <xsd:element maxOccurs="1" minOccurs="0" name="emails" type="emailsType"/>
//            <xsd:element maxOccurs="1" minOccurs="0" name="references" type="referencesType"/>
//            <xsd:element maxOccurs="1" minOccurs="0" name="notes" type="notesType"/>
//            <xsd:element maxOccurs="1" minOccurs="0" name="publications" type="publicationsType"/>
//            <xsd:element maxOccurs="1" minOccurs="0" name="userids" type="useridsType"/>
//			<xsd/:sequence>
			
			
			
			
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
	//		metadata.addContent(new Element("females"));
	//		metadata.addContent(new Element("institutions"));
	//		metadata.addContent(new Element("addresses"));
			
		}

		xmlElemAuthor.addContent(structure);
		xmlElemAuthor.addContent(metadata);
		xmlElemAuthor.addContent(service);

		Document authordoc = new Document(xmlElemAuthor);

		author.setFromJDOM(authordoc);

		// Robert: redundant?
		author.setId(id);
			
		XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
		Logger.getLogger(this.getClass()).info(xout.outputString(author.createXML()));
		return author;
	}		
}
