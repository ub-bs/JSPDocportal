package org.mycore.frontend.workflowengine.strategies;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

public class MCRAuthorMetadataStrategy extends MCRDefaultMetadataStrategy {
	private static Logger logger = Logger.getLogger(MCRAuthorMetadataStrategy.class.getName());
	public MCRAuthorMetadataStrategy(String documentType){
		super(documentType);
	}
	
	public boolean commitMetadataObject(String mcrobjid, String directory) {
		try{
			String filename = directory + "/" + mcrobjid + ".xml";
			Document d = new SAXBuilder().build(new File(filename)); 
			XPath xName = XPath.newInstance("/mycoreobject/metadata/names/name");
			List resultList = xName.selectNodes(d);
			for(int i=0;i<resultList.size();i++){
				Element eName = (Element)resultList.get(i);
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
				Element eFullname = eName.getChild("fullname");
				if(eFullname==null){
					eFullname = new Element("fullname");
					eFullname.setText(sbFullname.toString());
					eName.addContent(eFullname);
				}
				else{
					eFullname.setText(sbFullname.toString());
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
//		- <names class="MCRMetaPersonName" heritable="false" notinherit="false" parasearch="true" textsearch="true">
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
}
