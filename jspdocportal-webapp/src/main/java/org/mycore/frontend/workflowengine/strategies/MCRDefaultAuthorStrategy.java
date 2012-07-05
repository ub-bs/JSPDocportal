package org.mycore.frontend.workflowengine.strategies;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.xml.MCRXMLFunctions;

import org.mycore.datamodel.common.MCRXMLMetadataManager;
import org.mycore.datamodel.metadata.MCRMetaAddress;
import org.mycore.datamodel.metadata.MCRMetaBoolean;
import org.mycore.datamodel.metadata.MCRMetaLangText;
import org.mycore.datamodel.metadata.MCRMetaPersonName;
import org.mycore.datamodel.metadata.MCRMetadataManager;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowUtils;
import org.mycore.services.fieldquery.MCRResults;
import org.mycore.user2.MCRUser;
import org.mycore.user2.MCRUserManager;
import org.xml.sax.SAXParseException;

public class MCRDefaultAuthorStrategy implements MCRAuthorStrategy {
	private static Logger logger = Logger.getLogger(MCRDefaultAuthorStrategy.class.getName());
	private static String XSI_URL = "http://www.w3.org/2001/XMLSchema-instance";

	public MCRObjectID createAuthor(String userid, MCRObjectID nextFreeAuthorId, boolean fromUserData, boolean inDatabase){
		MCRObject author = null;
		
		/*
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
				return MCRObjectID.getInstance(authorID);
			}
			author = createAuthorFromUser(user, nextFreeAuthorId);
		} else {
			author = createAuthorFromUser(null, nextFreeAuthorId);
		}
		*/
		author =  createAuthorFromUser(null, nextFreeAuthorId);
		
		try {
			if ( inDatabase) {
				MCRMetadataManager.create(author);
			} else {
				FileOutputStream fos = new FileOutputStream(
						MCRWorkflowDirectoryManager.getWorkflowDirectory("person")	+ "/" + author.getId().toString() + ".xml");
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
				"xsi", XSI_URL));
		xmlElemAuthor.setAttribute("noNamespaceSchemaLocation",
				"datamodel-person.xsd", org.jdom.Namespace.getNamespace("xsi",
						XSI_URL));
		xmlElemAuthor.setAttribute("ID", id.toString());
		xmlElemAuthor.setAttribute("label", id.toString());

		Element structure = new Element("structure");
		Element metadata = new Element("metadata");
		Element service = new Element("service");

		if (user != null) {
			MCRMetaPersonName pname = new MCRMetaPersonName();
			String fullname = user.getRealName();
			pname.setSubTag("name");
			pname.setLang("de");
			pname.set("", "", "", user.getRealName(), "", "", "");
				
			MCRMetaBoolean female = new MCRMetaBoolean();
			female.setSubTag("female");
			female.setLang("de");
			female.setValue("false");
			female.setValue("female".equals(user.getAttributes().get("sex")));

			MCRMetaAddress padr = new MCRMetaAddress();
			padr.setSubTag("address");
			padr.setLang("de");
			
					
//			padr.set(userContact.getCountry(), userContact
//					.getState(), userContact.getPostalCode(), user
//					.getUserContact().getCity(), userContact
//					.getStreet(), "-");
			
			  /*public final void set(String set_country, String set_state, 
			   * String set_zipcode, String set_city, String set_street, String set_number) {
			        if (set_country == null || set_state == null || set_zipcode == null || set_city == null || set_street == null || set_number == null) {
			            throw new MCRException("One parameter is null.");
			        }*/
			
			padr.setCountry("DE");
			padr.setState("-");
			padr.setZipCode(user.getAttributes().get("postalcode"));
			padr.setCity(user.getAttributes().get("city"));
			padr.setStreet(user.getAttributes().get("street"));
			padr.setNumber("-");

			MCRMetaLangText userID = new MCRMetaLangText();
			userID.setSubTag("userid");
			userID.setLang("de");
			userID.setText(user.getUserID());

			
			Element ePname = pname.createXML();
			Element ePnames = new Element("names");
			ePnames.setAttribute("class", "MCRMetaPersonName");
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
			email.setText(user.getEMailAddress());
				
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
						
			MCRMetaPersonName pname = new MCRMetaPersonName();
			pname.setSubTag("name");
			pname.setLang("de");
			pname.set("", "", "Neuer Autor", "", "", "", "");
			ePnames.addContent(pname.createXML());
						
			metadata.addContent(ePnames);

			MCRMetaAddress padr = new MCRMetaAddress();
			padr.setSubTag("address");
			padr.setLang("de");
			
			MCRConfiguration cfg = MCRConfiguration.instance();
			
		//	String instName=cfg.getString("McRCMCR.WorkflowEngine.MyInstitution.name","");
			
			padr.setCountry(cfg.getString("MCR.WorkflowEngine.MyInstitution.country",""));
			padr.setState(cfg.getString("MCR.WorkflowEngine.MyInstitution.state",""));
			padr.setZipCode(cfg.getString("MCR.WorkflowEngine.MyInstitution.zipcode",""));
			padr.setCity(cfg.getString("MCR.WorkflowEngine.MyInstitution.city",""));
			padr.setStreet(cfg.getString("MCR.WorkflowEngine.MyInstitution.street",""));
			padr.setNumber(cfg.getString("MCR.WorkflowEngine.MyInstitution.number",""));
			
			Element ePadr = padr.createXML();
			Element ePadrs = new Element("addresses");
			ePadrs.setAttribute("class", "MCRMetaAddress");
			ePadrs.addContent(ePadr);

			metadata.addContent(ePadrs);		
			
			//		metadata.addContent(new Element("females"));
	//		metadata.addContent(new Element("institutions"));
	//		metadata.addContent(new Element("addresses"));
			
		}

		xmlElemAuthor.addContent(structure);
		xmlElemAuthor.addContent(metadata);
		xmlElemAuthor.addContent(service);

		Document authordoc = new Document(xmlElemAuthor);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		XMLOutputter xop = new XMLOutputter();
		try {
			xop.output(authordoc, baos);
			author = new MCRObject(baos.toByteArray(), true);

		} catch (IOException e) {
			logger.error(e);
		}
		catch(SAXParseException e){
			logger.error(e);
		}
		

		// Robert: redundant?
		author.setId(id);
			
		XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
		Logger.getLogger(this.getClass()).info(xout.outputString(author.createXML()));
		return author;
	}		
}
