package org.mycore.frontend.workflowengine.strategies;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jbpm.context.exe.ContextInstance;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.mycore.common.MCRConfiguration;
import org.mycore.datamodel.metadata.MCRMetaClassification;
import org.mycore.datamodel.metadata.MCRMetaLangText;
import org.mycore.datamodel.metadata.MCRMetaLinkID;
import org.mycore.datamodel.metadata.MCRMetadataManager;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.cli.MCRObjectCommands;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowConstants;
import org.xml.sax.SAXParseException;

public class MCRDefaultMetadataStrategy extends MCRMetadataStrategy{
	private String documentType;
    public  static String XSI_URL = "http://www.w3.org/2001/XMLSchema-instance";
	
	public MCRDefaultMetadataStrategy(String documentType){
		this.documentType = documentType;
	}
	
	public MCRDefaultMetadataStrategy(){
		this.documentType="";
	}
	protected static Logger logger = Logger.getLogger(MCRDefaultMetadataStrategy.class.getName());
		
	public boolean createEmptyMetadataObject(boolean authorRequired, List authorIDs, List authors, 
			MCRObjectID nextFreeObjectId, 	String userid,	Map identifiers, String publicationType,
			String saveDirectory){
		
		if (authorRequired){
			if((authorIDs == null || authorIDs.size() == 0) && (authors == null || authors.size() == 0)){
				logger.warn("Could not create metadata object because of empty parameter [authorIDs]");
				return false;
			}
		}
		
		Element mycoreobject = new Element ("mycoreobject");				
		mycoreobject.addNamespaceDeclaration(org.jdom.Namespace.getNamespace("xsi", XSI_URL));
		mycoreobject.setAttribute("noNamespaceSchemaLocation", 
					"datamodel-" + nextFreeObjectId.getTypeId() +".xsd", 
					org.jdom.Namespace.getNamespace("xsi", XSI_URL));		
		
		Element structure = new Element ("structure");			
		Element metadata = new Element ("metadata");	
		Element service = new Element ("service");

		Element eCreators = new Element("creators");
		eCreators.setAttribute("class","MCRMetaLangText");
		
		if ( authorIDs != null && authorIDs.size() > 0) {				
			Element eCreatorLinks = new Element("creatorlinks");			
			eCreatorLinks.setAttribute("class","MCRMetaLinkID");
	
			for (Iterator it = authorIDs.iterator(); it.hasNext();) {
				String authorID = (String) it.next();
				if ( authorID != null ) {
					Document jAuthor =  MCRMetadataManager.retrieveMCRObject(MCRObjectID.getInstance(authorID)).createXML();
					String sAuthorName = authorID;
					if ( jAuthor != null ) {
					    Iterator it2 = jAuthor.getDescendants(new ElementFilter("fullname"));
			        	if ( it2.hasNext() )    {
			        	      Element el = (Element) it2.next();
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
					creatorlink.setReference(authorID,sAuthorName,sAuthorName);
					
					Element eCreator = creator.createXML();
					eCreators.addContent(eCreator);
					
					Element eCreatorLink = creatorlink.createXML();
					eCreatorLinks.addContent(eCreatorLink);					
				}
			}
			metadata.addContent(eCreatorLinks);
		}
		
		if ( authors != null && authors.size() > 0){
			for (Iterator it = authors.iterator(); it.hasNext();) {
				String author = (String) it.next();
				MCRMetaLangText creator = new MCRMetaLangText();
				creator.setSubTag("creator");
				creator.setLang("de");
				creator.setText(author);
				Element eCreator = creator.createXML();
				eCreators.addContent(eCreator);
			}
		}
		
		if (authorRequired)
			metadata.addContent(eCreators);
	
		if ( publicationType != null) {
			MCRMetaClassification clType = new MCRMetaClassification();
			clType.setSubTag("type");
			clType.setLang("de");
			clType.setValue(MCRConfiguration.instance().getString("MCR.ClassificationID.Type"),	publicationType);
			Element eclType = clType.createXML();
			Element eclTypes = new Element("types");
			eclTypes.setAttribute("class","MCRMetaClassification");
			//eclTypes.setAttribute("parasearch", "true");			
			eclTypes.addContent(eclType);			
			metadata.addContent(eclTypes);								
		}
		
		if(identifiers != null){
			for (Iterator it = identifiers.keySet().iterator(); it.hasNext();) {
				Integer identifierType = (Integer) it.next();
				if(identifierType.equals(MCRWorkflowConstants.KEY_IDENTIFER_TYPE_URN)){					 
					Element eUrns = createURNElement((String)identifiers.get(identifierType));
					metadata.addContent(eUrns);					
				}
			}
		}
		      
		mycoreobject.addContent(structure);
		mycoreobject.addContent(metadata);
		mycoreobject.addContent(service);
	    
		// ID Setzen
		String nextID = nextFreeObjectId.toString();
		mycoreobject.setAttribute("ID", nextID);	 
		mycoreobject.setAttribute("label", nextID);

		Document mycoreobjectdoc = new Document(mycoreobject);
		MCRObject disshab = new MCRObject();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		XMLOutputter xop = new XMLOutputter();
		try {
			xop.output(mycoreobjectdoc, baos);
			disshab.setFromXML(baos.toByteArray(), false);

		} catch (IOException e) {
			logger.error(e);
		}
		catch(SAXParseException e){
			logger.error(e);
		}
		
		try {
			FileOutputStream fos = new FileOutputStream(saveDirectory + "/" + nextID + ".xml");
			(new XMLOutputter(Format.getPrettyFormat())).output(mycoreobject,fos);
			fos.close();
		} catch ( Exception ex){
			logger.warn("Could not create disshab object " +  nextID );
			return false;
		}

   	    return true;		
	}

	public void setMetadataValid(String mcrid, boolean isValid, ContextInstance ctxI) {
		ctxI.setVariable(MCRMetadataStrategy.VALID_PREFIX + mcrid, Boolean.toString(isValid));
	}

	public boolean isMetadataValid(String mcrid, ContextInstance ctxI) {
		try{
			if(((String)ctxI.getVariable("valid-" + mcrid)).equals("true"))
				return true;
			else
				return false;
		}catch(Exception e){
			logger.error("metadata flag was not set for " + mcrid + " and process " + ctxI.getProcessInstance().getId(), e);
			return false;
		}
	}

    public final void storeMetadata(byte[] outxml, String ID, String fullname) throws Exception {
        if (outxml == null) {
            return;
        }
        try {
            FileOutputStream out = new FileOutputStream(fullname);
            out.write(outxml);
            out.flush();
        } catch (IOException ex) {
        	logger.error(ex.getMessage());
        	logger.error("Exception while store to file " + fullname);
            return;
        }
        logger.info("Object " + ID + " stored under " + fullname + ".");
    }

	public boolean removeMetadataFiles(ContextInstance ctxI, String saveDirectory, String  backupDirectory) {
		String sobjids = (String)ctxI.getVariable(MCRWorkflowConstants.WFM_VAR_METADATA_OBJECT_IDS);
		if ( sobjids == null || sobjids.length() == 0 ) {
			// no objects exists
			return true;
		}
		String[] objids = sobjids.split(",");
		for (int i = 0; i < objids.length; i++) {
			String filename = saveDirectory + "/" + objids[i] + ".xml";
			if(!backupMetadataObject(filename, backupDirectory)){
				return false;
			}
			try {
				File fi = new File(filename);
				if (fi.isFile() && fi.canWrite()) {				
					fi.delete();
					logger.debug("deleted file in workflow: " + filename);
				} else {
					if ( !fi.exists()) {
						logger.warn("nothing to doc, because file does not exist: " + filename);
					} else {
						logger.error("Cannot delete file in workflow" + filename);
						return false;
					}
				}
			} catch (Exception ex) {
				logger.error("Can't remove file " + filename);
				return false;
			}			
		}
		return true;
	}

	public void setWorkflowVariablesFromMetadata(ContextInstance ctxI, Element metadata) {
		StringBuffer sbTitle = new StringBuffer("");
		for(Iterator it = metadata.getDescendants(new ElementFilter("title")); it.hasNext();){
			Element title = (Element)it.next();
			sbTitle.append(title.getText());
		}
		if(sbTitle.length() == 0){
			ctxI.setVariable(MCRWorkflowConstants.WFM_VAR_WFOBJECT_TITLE, "Your Workflow Object");
		}else{
			ctxI.setVariable(MCRWorkflowConstants.WFM_VAR_WFOBJECT_TITLE, sbTitle.toString());
		}
	}

	public boolean commitMetadataObject(String mcrobjid, String directory) {
		try { 
	        String filename = directory + "/" + mcrobjid + ".xml";
			if (MCRMetadataManager.exists(MCRObjectID.getInstance(mcrobjid))) {
				// updates changes not the accesrules
				MCRObjectCommands.updateFromFile(filename);
			} else {
				MCRObjectCommands.loadFromFile(filename);
			}
			logger.info("The metadata object: " + filename + " is loaded.");
			return true;
		} catch (Exception ig){ 
			logger.error("Can't load File catched error: ", ig);
		}
		return false;
	}

	/**
	 * @return Returns the documentType.
	 */
	public final String getDocumentType() {
		return documentType;
	}

	/**
	 * @param documentType The documentType to set.
	 */
//	protected final void setDocumentType(String documentType) {
//		this.documentType = documentType;
//	}
//	
	public final Element createURNElement(String urn) {
		
		MCRMetaLangText turn = new MCRMetaLangText();
		turn.setSubTag("urn");
		turn.setLang("de");
		turn.setText(urn);
		Element eUrn = turn.createXML();
		eUrn.setAttribute("type", "urn_new");				
		Element eUrns = new Element("urns");
		eUrns.setAttribute("class","MCRMetaLangText");
	//	eUrns.setAttribute("textsearch", "true");
		eUrns.addContent(eUrn);
		
		return eUrns;
	}
}
