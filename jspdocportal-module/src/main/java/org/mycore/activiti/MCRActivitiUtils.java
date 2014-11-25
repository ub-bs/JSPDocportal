package org.mycore.activiti;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.mycore.access.MCRAccessManager;
import org.mycore.common.MCRException;
import org.mycore.common.MCRUtils;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.datamodel.ifs.MCRDirectory;
import org.mycore.datamodel.ifs.MCRFile;
import org.mycore.datamodel.ifs.MCRFilesystemNode;
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
		File fOut = getWorkflowDerivateFile(mcrder.getOwnerID(), mcrder.getId());
		try (FileOutputStream fos = new FileOutputStream(fOut)) {
			XMLOutputter xmlOut = new XMLOutputter(Format.getPrettyFormat());
			xmlOut.output(mcrder.createXML(), fos);
		} catch (Exception ex) {
			throw new MCRException("Could not save MCR Derivate "
					+ mcrder.getId().toString() + " into workfow directory.", ex);
		}
	}
	
	public static MCRDerivate loadMCRDerivateFromWorkflowDirectory(MCRObjectID owner, MCRObjectID mcrderid){
		try{
			File wfFile = getWorkflowDerivateFile(owner, mcrderid);
			if(wfFile.exists()){
				SAXBuilder sax = new SAXBuilder();
				return new MCRDerivate(sax.build(wfFile));
			}
		}
		catch(Exception e){
			LOGGER.error(e);
		}
		return null;
	}
	
	public static File getWorkflowDerivateFile(MCRObjectID owner, MCRObjectID mcrderid){
		File wfFile = new File(new File(MCRActivitiUtils.getWorkflowDirectory(owner), owner.toString()), mcrderid.toString() +".xml");
		if(!wfFile.getParentFile().exists()){
			wfFile.getParentFile().mkdirs();
		}
		return wfFile;
	}
	
	public static void deleteMCRDerivateFromWorkflowDirectory(MCRObjectID owner, MCRObjectID mcrderid){
		try{
			File wfDerFile = getWorkflowDerivateFile(owner, mcrderid);
			if(wfDerFile.exists()){
				wfDerFile.delete();
			}
			File wfDerDir = new File(new File(MCRActivitiUtils.getWorkflowDirectory(owner), owner.toString()), mcrderid.toString());
			deleteDirectory(wfDerDir);
		}
		catch(Exception e){
			LOGGER.error(e);
		}
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
	
	public static void deleteDirectory(File dir){
		if(dir==null || !dir.exists()){
			return;
		}
		for(File f: dir.listFiles()){
			if(f.isDirectory()){
				deleteDirectory(f);
			}
			else{
				f.delete();
			}
		}
		dir.delete();
	}
	
	/*
	public static void synchronizeDerivateContentBetweenIFSAndWorkflowDir(MCRObjectID derID, File wfDerDir){
		MCRDirectory root = MCRDirectory.getRootDirectory(derID.toString());
		if(root!=null){
			synchronizeDerivateContent(root, wfDerDir);
		}
	}
	
	private static void synchronizeDerivateContent(MCRFilesystemNode fsNode, File wfDerDir){
		if(fsNode==null){
			return;
		}
		if(fsNode instanceof MCRDirectory){
			File check = new File(wfDerDir, fsNode.getAbsolutePath());
			if(check.exists()){
				for(MCRFilesystemNode fsn: ((MCRDirectory) fsNode).getChildren()){
					synchronizeDerivateContent(fsn, wfDerDir);
				}
			}
			else{
				deleteInIFS(fsNode);
			}
		}
		if(fsNode instanceof MCRFile){
			File check = new File(wfDerDir, fsNode.getAbsolutePath());
			if(check.exists() && MCRActivitiUtils.getMD5Sum(check).equals(((MCRFile) fsNode).getMD5())){
				//files are not changed
			}
			else{
				deleteInIFS(fsNode);
			}
		}
	}
	
	*/
	public static void deleteInIFS(MCRFilesystemNode fsNode, boolean deleteSelf){
		if(fsNode == null) return;
		if(fsNode instanceof MCRDirectory){
			for(MCRFilesystemNode fsn: ((MCRDirectory) fsNode).getChildren()){
				deleteInIFS(fsn, true);
			}
		}
		if(deleteSelf){
			fsNode.delete();
		}
	}
	
	public static Map<String, Element> getAccessRulesMap(String objid) {
		Iterator<String> it = MCRAccessManager.getPermissionsForID(objid).iterator();        
        Map<String, Element> htRules = new Hashtable<String, Element>();
        while(it.hasNext()){
        	String s = it.next();
           	Element eRule = MCRAccessManager.getAccessImpl().getRule( objid, s);
           	htRules.put(s, eRule);
        }
        return htRules;
	}	

	public static void setAccessRulesMap(String objid, Map<String, Element> htRules ) {
		if ( htRules != null) {
			MCRAccessManager.getAccessImpl().removeAllRules(objid);
			for (String perm: htRules.keySet()) {
				MCRAccessManager.addRule(objid,perm,htRules.get(perm),"");
			}
		}
	}
	public static String getMD5Sum(File f){
		try{
			return MCRUtils.getMD5Sum(new FileInputStream(f));
		}
		catch(NoSuchAlgorithmException e){
			//will never happen
			return null;
		}
		catch(IOException nfe){
			return null;
		}
	} 
}
