package org.mycore.frontend.workflowengine.jbpm.person;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import org.apache.log4j.Logger;
import org.jbpm.context.exe.ContextInstance;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.mycore.common.MCRException;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowConstants;
import org.mycore.frontend.workflowengine.strategies.MCRDefaultMetadataStrategy;

public class MCRPersonMetadataStrategy extends MCRDefaultMetadataStrategy {
	private static Logger logger = Logger.getLogger(MCRPersonMetadataStrategy.class.getName());
	public MCRPersonMetadataStrategy(){
		super("person");
	}
	
	public boolean commitMetadataObject(String mcrobjid, String directory) {
		try{
			String filename = directory + "/" + mcrobjid + ".xml";
			Document d = new SAXBuilder().build(new File(filename));	
			
			XPathExpression<Element> xpe = XPathFactory.instance().compile("/mycoreobject/metadata/names/name", Filters.element());
			for(Element eName : xpe.evaluate(d)){
				
				Element eFullname = eName.getChild("fullname");
				if(eFullname==null){
					eFullname = new Element("fullname");
					eFullname.setText(createFullName(eName));
					eName.addContent(eFullname);
				}
				else{
					eFullname.setText(createFullName(eName));
				}
			}
			XMLOutputter xmlOut = new XMLOutputter(Format.getPrettyFormat());
			FileOutputStream fos = new FileOutputStream(filename); 
			OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8"); 
			xmlOut.output(d, out);
			out.close();
		}catch(Exception e){
			logger.error("Can't load File catched error: ", e);
			return false;
		}
		 
		 
//		 - <mycoreobject xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xlink="http://www.w3.org/1999/xlink" xsi:noNamespaceSchemaLocation="datamodel-author.xsd" ID="DocPortal_author_00000007" label="DocPortal_author_00000007" version="Version 1.3">
//		  <structure /> 
//		- <metadata xml:lang="de">
//		- <names class="MCRMetaPersonName" heritable="false" notinherit="false">
//		- <name xml:lang="de" inherited="0">
//		  <firstname>Friedrich</firstname> 
//		  <callname>Friedrich</callname> 
//		  <fullname>Dr. Friedrich Foxtrott</fullname> 
//		  <surname>Foxtrott</surname> 
//		  <academic>Dr.</academic> 
//		  </name>
//		  </names>		 
		
		return super.commitMetadataObject(mcrobjid, directory);
	}
	
	public void setWorkflowVariablesFromMetadata(ContextInstance ctxI, Element metadata) {
		try {
			ctxI.setVariable(MCRWorkflowConstants.WFM_VAR_WFOBJECT_TITLE, createWFOTitlefromMetadata(metadata));		
		} catch (MCRException ex) {
			logger.error("catched error", ex);
		} finally {
		}
	}

    
    
    private String createFullName(Element eName){
        StringBuffer sbFullname = new StringBuffer();
        String firstname = eName.getChildText("firstname");
        String lastname = eName.getChildText("surname");
        String academic = eName.getChildText("academic");
        String prefix = eName.getChildText("prefix");
        if(lastname!=null && !lastname.equals("")){
            sbFullname.append(lastname);
        }
        if(firstname!=null && !firstname.equals("")){
            sbFullname.append(", ");
            sbFullname.append(firstname);
        }
        
        if(prefix!=null && !lastname.equals("")){
            sbFullname.append(" ");
            sbFullname.append(prefix);
        }
     
        if(academic!=null && !academic.equals("")){
            sbFullname.append(" (");
            sbFullname.append(academic);
            sbFullname.append(")");
        }
        return sbFullname.toString();

        
    }
	private String createWFOTitlefromMetadata(Element metadata){
		Element name = metadata.getChild("names").getChild("name");
		String fullname = name.getChildTextNormalize("fullname");
		if (fullname != null) {
			return fullname;
		} else {
            return createFullName(name);
        }
	}
}
