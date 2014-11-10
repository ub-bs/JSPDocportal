package org.mycore.activiti;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.mycore.common.MCRException;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.datamodel.metadata.MCRDerivate;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;

/**
 * provides some static utility methods
 * 
 * @author Robert Stephan
 * 
 */
public class MCRActivitiUtils {
	private static Logger LOGGER = Logger.getLogger(MCRActivitiUtils.class);
	/**
	 * saves a given MCR object into the workflow directory
	 * @param MCRObject
	 */
	public static void saveMCRObjectToWorkflowDirectory(MCRObject mcrObj) {
		File dir = getWorkflowDirectory(mcrObj.getId());
		File fOut = new File(dir, mcrObj.getId().toString() + ".xml");
		try (FileOutputStream fos = new FileOutputStream(fOut)) {
			XMLOutputter xmlOut = new XMLOutputter(Format.getPrettyFormat());
			xmlOut.output(mcrObj.createXML(), fos);
		} catch (Exception ex) {
			throw new MCRException("Cant save MCR Object "
					+ mcrObj.getId().toString() + " into workfow directory "
					+ dir.getName());
		}
	}
	
	public static MCRObject loadMCRObjectFromWorkflowDirectory(MCRObjectID mcrobjid){
		try{
			File wfFile = new File(MCRActivitiUtils.getWorkflowDirectory(mcrobjid), mcrobjid+".xml");
			if(wfFile.exists()){
				SAXBuilder sax = new SAXBuilder();
				return new MCRObject(sax.build(wfFile));
			}
		}
		catch(Exception e){
			LOGGER.error(e);;
		}
		return null;
	}
	
	/**
	 * saves a given MCR object into the workflow directory
	 * @param MCRObject
	 */
	public static void saveMCRDerivateToWorkflowDirectory(MCRDerivate mcrder) {
		File dir = new File(getWorkflowDirectory(mcrder.getOwnerID()), mcrder.getOwnerID().toString());
		dir.mkdir();
		File fOut = new File(dir, mcrder.getId().toString() + ".xml");
		try (FileOutputStream fos = new FileOutputStream(fOut)) {
			XMLOutputter xmlOut = new XMLOutputter(Format.getPrettyFormat());
			xmlOut.output(mcrder.createXML(), fos);
		} catch (Exception ex) {
			throw new MCRException("Cant save MCR Derivate "
					+ mcrder.getId().toString() + " into workfow directory "
					+ dir.getName(), ex);
		}
	}
	
	public static MCRObject loadMCRDerivateFromWorkflowDirectory(MCRObjectID owner, MCRObjectID mcrderid){
		try{
			File wfFile = new File(new File(MCRActivitiUtils.getWorkflowDirectory(owner), owner.toString()), mcrderid.toString() +".xml");
			if(wfFile.exists()){
				SAXBuilder sax = new SAXBuilder();
				return new MCRObject(sax.build(wfFile));
			}
		}
		catch(Exception e){
			LOGGER.error(e);;
		}
		return null;
	}

	public static File getWorkflowDirectory(MCRObjectID mcrObjID) {
		String s = MCRConfiguration.instance().getString("MCR.Workflow.WorkflowDirectory");
		File f = new File(s);
		f = new File(f, mcrObjID.getTypeId());
		if (!f.exists()) {
			f.mkdirs();
		}
		return f;
	}

}
