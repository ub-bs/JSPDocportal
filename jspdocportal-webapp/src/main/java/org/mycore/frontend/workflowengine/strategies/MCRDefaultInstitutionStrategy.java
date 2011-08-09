package org.mycore.frontend.workflowengine.strategies;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import org.mycore.datamodel.metadata.MCRMetaInstitutionName;
import org.mycore.datamodel.metadata.MCRMetadataManager;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.xml.sax.SAXParseException;

public class MCRDefaultInstitutionStrategy implements MCRInstitutionStrategy {
	private static Logger logger = Logger.getLogger(MCRDefaultInstitutionStrategy.class.getName());
	private static String XSI_URL = "http://www.w3.org/2001/XMLSchema-instance";

	public MCRObjectID createInstitution(MCRObjectID nextFreeInstitutionId, boolean inDatabase){
		MCRObject institution = null;
		institution = createInstitutionObject(nextFreeInstitutionId);
		try {
			if ( inDatabase) {
				MCRMetadataManager.create(institution);
			} else {
				FileOutputStream fos = new FileOutputStream(
						MCRWorkflowDirectoryManager.getWorkflowDirectory("institution")	+ "/" + institution.getId().toString() + ".xml");
						(new XMLOutputter(Format.getPrettyFormat())).output(institution.createXML(),fos);
				fos.close();
			}
		} catch ( Exception ex){
			//TODO Fehlermeldung
			logger.warn("Could not Create institution object:  " + nextFreeInstitutionId.toString(), ex);
			return null;
		}
   	    return institution.getId();		
	}
	
	private MCRObject createInstitutionObject(MCRObjectID id) {
		MCRObject institution = new MCRObject();
		Element xmlElemInstitution = new Element("mycoreobject");
		xmlElemInstitution.addNamespaceDeclaration(org.jdom.Namespace.getNamespace(
				"xsi", XSI_URL));
		xmlElemInstitution.setAttribute("noNamespaceSchemaLocation",
				"datamodel-author.xsd", org.jdom.Namespace.getNamespace("xsi",
						XSI_URL));
		xmlElemInstitution.setAttribute("ID", id.toString());
		xmlElemInstitution.setAttribute("label", id.toString());

		Element structure = new Element("structure");
		Element metadata = new Element("metadata");
		Element service = new Element("service");

		//metadata needs dummy data otherwise  author.createXML() failes
		
		Element eInames = new Element("names");
		eInames.setAttribute("class", "MCRMetaInstitutionName");
		eInames.setAttribute("textsearch", "true");
		MCRMetaInstitutionName iname = new MCRMetaInstitutionName();
		iname.setSubTag("name");
		iname.setLang("de");
		iname.set("Neue Institution", "", "");
		eInames.addContent(iname.createXML());
						
		metadata.addContent(eInames);
		xmlElemInstitution.addContent(structure);
		xmlElemInstitution.addContent(metadata);
		xmlElemInstitution.addContent(service);

		Document institutiondoc = new Document(xmlElemInstitution);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		XMLOutputter xop = new XMLOutputter();
		try {
			xop.output(institutiondoc, baos);
			institution = new MCRObject(baos.toByteArray(), false);

		} catch (IOException e) {
			logger.error(e);
		}
		catch(SAXParseException e){
			logger.error(e);
		}
		institution.setId(id);
			
		XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
		Logger.getLogger(this.getClass()).info(xout.outputString(institution.createXML()));
		return institution;
	}		
}
