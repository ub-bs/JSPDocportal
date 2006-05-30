package org.mycore.frontend.workflowengine.strategies;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.mycore.common.MCRDefaults;
import org.mycore.datamodel.metadata.MCRMetaLangText;
import org.mycore.datamodel.metadata.MCRMetaLinkID;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.cli.MCRObjectCommands;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowConstants;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowProcess;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowUtils;

public class MCRDefaultMetadataStrategy extends MCRMetadataStrategy{
	private String documentType;
	
	public MCRDefaultMetadataStrategy(String documentType){
		this.documentType = documentType;
	}
	
	private static Logger logger = Logger.getLogger(MCRDefaultMetadataStrategy.class.getName());
	
	public boolean createEmptyMetadataObject(boolean authorRequired, List authorIDs, List authors, MCRObjectID nextFreeObjectId, String userid, Map identifiers, String saveDirectory){
		
		if (authorRequired){
			if((authorIDs == null || authorIDs.size() == 0) && (authors == null || authors.size() == 0)){
				logger.warn("Could not create metadata object because of empty parameter [authorIDs]");
				return false;
			}
		}
		
		Element mycoreobject = new Element ("mycoreobject");				
		mycoreobject.addNamespaceDeclaration(org.jdom.Namespace.getNamespace("xsi", MCRDefaults.XSI_URL));
		mycoreobject.setAttribute("noNamespaceSchemaLocation", "datamodel-disshab.xsd", org.jdom.Namespace.getNamespace("xsi", MCRDefaults.XSI_URL));		
		
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
					Document jAuthor =  new MCRObject().receiveJDOMFromDatastore(authorID);
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
	
		if(identifiers != null){
			for (Iterator it = identifiers.keySet().iterator(); it.hasNext();) {
				Integer identifierType = (Integer) it.next();
				if(identifierType.equals(MCRWorkflowConstants.KEY_IDENTIFER_TYPE_URN)){
					MCRMetaLangText urn = new MCRMetaLangText();
					urn.setSubTag("urn");
					urn.setLang("de");
					urn.setText((String)identifiers.get(identifierType));
					Element eUrn = urn.createXML();
					eUrn.setAttribute("type", "urn_new");
						
					Element eUrns = new Element("urns");
					eUrns.setAttribute("class","MCRMetaLangText");
					eUrns.setAttribute("textsearch", "true");
					eUrns.addContent(eUrn);

					metadata.addContent(eUrns);					
				}
			}
		}
	      
		mycoreobject.addContent(structure);
		mycoreobject.addContent(metadata);
		mycoreobject.addContent(service);
	    
		// ID Setzen
		String nextID = nextFreeObjectId.getId();
		mycoreobject.setAttribute("ID", nextID);	 
		mycoreobject.setAttribute("label", nextID);

		Document mycoreobjectdoc = new Document(mycoreobject);
		MCRObject disshab = new MCRObject();
		disshab.setFromJDOM(mycoreobjectdoc);
		try {
			FileOutputStream fos = new FileOutputStream(saveDirectory + "/" + nextID + ".xml");
			(new XMLOutputter(Format.getPrettyFormat())).output(mycoreobject,fos);
			fos.close();
		} catch ( Exception ex){
			logger.warn("Could not create disshab object " +  nextID );
			return false;
		}
		//TODO permission and save im workflow
		//setDefaultPermissions(id.getId(), userid );
   	    return true;		
	}

	public void setMetadataValid(String mcrid, boolean isValid, MCRWorkflowProcess wfp) {
		wfp.setStringVariable(MCRMetadataStrategy.VALID_PREFIX + mcrid, Boolean.toString(isValid));
	}

	public boolean isMetadataValid(String mcrid, MCRWorkflowProcess wfp) {
		try{
			if(wfp.getStringVariable("valid-" + mcrid).equals("true"))
				return true;
			else
				return false;
		}catch(Exception e){
			logger.error("metadata flag was not set for " + mcrid + " and process " + wfp.getProcessInstanceID(), e);
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

	public boolean removeMetadataFiles(MCRWorkflowProcess wfp, String saveDirectory, String  backupDirectory) {
		String sobjids = wfp.getStringVariable(MCRWorkflowConstants.WFM_VAR_METADATA_OBJECT_IDS);
		if ( sobjids == null || sobjids.length() == 0 ) {
			// no objects exists
			return true;
		}
		String[] objids = sobjids.split(",");
		for (int i = 0; i < objids.length; i++) {
			String filename = saveDirectory + File.separator + objids[i] + ".xml";
			if(!backupMetadataObject(filename, backupDirectory)){
				logger.error("could not backup file " + filename);
				return false;
			}
			try {
				File fi = new File(filename);
				if (fi.isFile() && fi.canWrite()) {				
					fi.delete();
					logger.debug("File " + filename + " removed.");
				} else {
					logger.error("Can't remove file " + filename);
					return false;
				}
			} catch (Exception ex) {
				logger.error("Can't remove file " + filename);
				return false;
			}			
		}
		return true;
	}

	public void setWorkflowVariablesFromMetadata(MCRWorkflowProcess wfp, Element metadata) {
		StringBuffer sbTitle = new StringBuffer("");
		for(Iterator it = metadata.getDescendants(new ElementFilter("title")); it.hasNext();){
			Element title = (Element)it.next();
			sbTitle.append(title.getText());
		}
		if(sbTitle.length() == 0){
			wfp.setStringVariable("wfp-title", "Your Workflow Object");
		}else{
			wfp.setStringVariable("wfp-title", sbTitle.toString());
		}
	}

	public boolean commitMetadataObject(String mcrobjid, String directory) {
		try { 
	        Map ruleMap = null;
	        String filename = directory + File.separator + mcrobjid + ".xml";
			if (MCRObject.existInDatastore(mcrobjid)) {
		        ruleMap = MCRWorkflowUtils.getAccessRulesMap(mcrobjid);
				MCRObject mcr_obj = new MCRObject();
				mcr_obj.deleteFromDatastore(mcrobjid);
			}
			MCRObjectCommands.loadFromFile(filename);		
			if(ruleMap != null)
				MCRWorkflowUtils.setAccessRulesMap(mcrobjid, ruleMap);
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
	protected final void setDocumentType(String documentType) {
		this.documentType = documentType;
	}
	
	
}
