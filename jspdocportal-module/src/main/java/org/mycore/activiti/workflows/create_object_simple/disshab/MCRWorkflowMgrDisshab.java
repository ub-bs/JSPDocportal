package org.mycore.activiti.workflows.create_object_simple.disshab;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.mycore.activiti.MCRActivitiUtils;
import org.mycore.activiti.workflows.create_object_simple.MCRAbstractWorkflowMgr;
import org.mycore.activiti.workflows.create_object_simple.MCRWorkflowMgr;
import org.mycore.common.MCRException;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.datamodel.metadata.MCRObjectMetadata;
import org.xml.sax.SAXParseException;

public class MCRWorkflowMgrDisshab extends MCRAbstractWorkflowMgr implements MCRWorkflowMgr{
	private static final String DEFAULT_METADATA_XML =""
	+"<metadata>"
    +"  <def.modsContainer class='MCRMetaXML'>"
	+"    <modsContainer inherited='0'>"
    +"      <mods:mods xmlns:mods='http://www.loc.gov/mods/v3' version='3.6'>"
    +"        <mods:titleInfo xml:lang='de' usage='primary'>"
    +"           <mods:title>Neue Dissertation / Habilitation</mods:title>"
    +"        </mods:titleInfo>"
    +"      </mods:mods>"
    +"    </modsContainer>"
    +"</def.modsContainer>"
    + "</metadata>";

	
	@Override
	public MCRObjectMetadata getDefaultMetadata() {
		SAXBuilder sax = new SAXBuilder();
		try{
		  Document doc = sax.build(new StringReader(DEFAULT_METADATA_XML));
		  MCRObjectMetadata mcrOMD = new MCRObjectMetadata();
		  mcrOMD.setFromDOM(doc.getRootElement());
		  return mcrOMD;
		}
		catch(Exception e){
			throw new MCRException("Could not create default metadata", e);
		}
	}

	
	/**
	 * 
	 * @param mcrObjID
	 * @return null if correct, errormessage otherwise
	 */
	@Override
	public String validate(MCRObjectID mcrObjID){
		File wfFile = new File(MCRActivitiUtils.getWorkflowDirectory(mcrObjID), mcrObjID.toString() + ".xml");
		try {
			@SuppressWarnings("unused")
            MCRObject mcrWFObj = new MCRObject(wfFile.toURI());
		}
		catch(SAXParseException e){
			return "XML Error: "+e.getMessage();
		}
		catch(IOException e){
			return "I/O-Error: "+e.getMessage();
		}
		return null;
		
	}
	
}
