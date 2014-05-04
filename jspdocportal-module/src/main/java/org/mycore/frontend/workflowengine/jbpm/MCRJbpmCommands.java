/*
 * $RCSfile$
 * $Revision$ $Date$
 *
 * This file is part of ***  M y C o R e  ***
 * See http://www.mycore.de/ for details.
 *
 * This program is free software; you can use it, redistribute it
 * and / or modify it under the terms of the GNU General Public License
 * (GPL) as published by the Free Software Foundation; either version 2
 * of the License or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program, in a file called gpl.txt or license.txt.
 * If not, write to the Free Software Foundation Inc.,
 * 59 Temple Place - Suite 330, Boston, MA  02111-1307 USA
 */

package org.mycore.frontend.workflowengine.jbpm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.mycore.access.MCRAccessManager;
import org.mycore.backend.hibernate.MCRHIBConnection;
import org.mycore.common.MCRException;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.datamodel.common.MCRActiveLinkException;
import org.mycore.datamodel.common.MCRXMLMetadataManager;
import org.mycore.datamodel.metadata.MCRDerivate;
import org.mycore.datamodel.metadata.MCRMetaAccessRule;
import org.mycore.datamodel.metadata.MCRMetaElement;
import org.mycore.datamodel.metadata.MCRMetaLangText;
import org.mycore.datamodel.metadata.MCRMetaLinkID;
import org.mycore.datamodel.metadata.MCRMetadataManager;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.datamodel.metadata.MCRObjectStructure;
import org.mycore.frontend.cli.MCRAbstractCommands;
import org.mycore.frontend.cli.MCRDerivateCommands;
import org.mycore.frontend.cli.annotation.MCRCommand;
import org.mycore.frontend.cli.annotation.MCRCommandGroup;
import org.mycore.urn.services.MCRURNManager;
import org.xml.sax.SAXParseException;

/**
 * This class provides a set of commands specific to JSPDocportal
 * 
 *  
 * @author Heiko Helmbrecht
 * @author Robert Stephan
 * 
 * @version $Revision$ $Date$
 */

@MCRCommandGroup(name = "JSPDocportal Commands")
public class MCRJbpmCommands extends MCRAbstractCommands {
    /** The logger */
    private static Logger LOGGER = Logger.getLogger(MCRJbpmCommands.class.getName());
    
    /**
     * The command deletes a process instance of the workflow engine
     * 	if you've got to do this, you must restart your application server
     * 	for reinitializing your caches
     * @param strProcessID
     *        		String processId as String
     * @throws MCRException
     */
    
    @MCRCommand(syntax = "delete jbpm process {0}", help = "The command deletes a process instance of the jbpm workflow engine {0}")
    public static final void deleteProcess(String strProcessID) throws MCRException{
    	try{
    		long processID = Long.valueOf(strProcessID).longValue();
    		MCRJbpmWorkflowBase.deleteProcessInstance(processID);
    	}catch(Exception e){
    		LOGGER.error("could not delete process " + strProcessID, e);
    		throw new MCRException("Error in deleting a process from workflow engine");
    	}
    	
    	
    }

    /**
     * The command deploys a process definition to the database from a given file
     * 
     * @param resource 
     *               the filename of a class resource with the jbpm-processdefinition
     */
    @MCRCommand(syntax = "deploy jbpm processdefinition from file {0}", help = "The command deploys a process definition to the database from the file {0}")
    public static final void deployProcessDefinition(String resource) throws MCRException{
    	try{
    		MCRJbpmWorkflowBase wfb = new MCRJbpmWorkflowBase();
    		wfb.deployProcess(resource);	
    	}catch(Exception e){
    		LOGGER.error("Error in deploying a workflow process definition", e);
            throw new MCRException("Error in deploying a workflow process definition", e);
    	}
    	
    }

    /**
     * This method creates the database for the workflow engine jbpm
     * and deletes the old one
     * 
     */
    @MCRCommand(syntax = "create jbpm database schema", help = "The command DELETES the old workflow database schema and is loading a new empty schema from configuration")
    public static final void createSchema() throws MCRException{
    	try{
    		MCRJbpmWorkflowBase.createSchema();
        } catch (Exception e) {
        	LOGGER.error("Error in creating the schema for the workflow database", e);
            throw new MCRException("Error in creating the schema for the workflow database", e);
        }   
    }
    
    /**
     * Backups all objects of given type and their derivates into the following structure:
     *  - MCR_OBJECT_0001
     *    - MCR_DERIVATE_0001
     *      - file0001.pdf
     *      - file0002.pdf
     *    - MCR_DERIVATE_0002
     *      - file0003.txt
     *    - mcr_derivate_0001.xml
     *    - mcr_derivate_0002.xml
     *   - MCR_OBJECT_0002
     *     - MCR_DERIVATE_0003
     *       - file004.pdf
     *     - mcr_derivate_0003.xml
     *   - mcr_object_0001.xml
     *   - mcr_object_0002.xml
     * @param type
     *            the object type
     * @param dirname
     *            the filename to store the object 
     */
    @MCRCommand(syntax = "backup all objects of type {0} to directory {1}", help = "The command backups all objects of type {0} into the directory {1} including all derivates")
    public static final void backupAllObjects(String type, String dirname) {
        // check dirname
        File dir = new File(dirname);

        if (dir.isFile()) {
            LOGGER.error(dirname + " is not a dirctory.");
            return;
        }
        
        for (String id : MCRXMLMetadataManager.instance().listIDsOfType(type)) {
        	
             try {
                 // if object do'snt exist - no exception is catched!
                 MCRObject mcrObj = MCRMetadataManager.retrieveMCRObject(MCRObjectID.getInstance(id));                 
            	 
//               add ACL's
                 Iterator<String> it = MCRAccessManager.getPermissionsForID(id.toString()).iterator();
                 while(it.hasNext()){
                	 String s = it.next();
                     Element rule = MCRAccessManager.getAccessImpl().getRule(id.toString(), s);
                     mcrObj.getService().addRule(s, rule);
                 }
                 // build JDOM
                 Document xml = mcrObj.createXML();
                 
                 File xmlOutput = new File(dir, id.toString() + ".xml");
                 FileOutputStream out = new FileOutputStream(xmlOutput);
                 
                 new org.jdom2.output.XMLOutputter(Format.getPrettyFormat()).output(xml, out);
                 out.flush();
                 out.close();
                 
                 MCRObjectStructure mcrStructure = mcrObj.getStructure();
                 if(mcrStructure == null) return;
                 for(MCRMetaLinkID derivate: mcrStructure.getDerivates()){
                	 String derID = derivate.getXLinkHref();
                	 File subdir = new File(dirname,mcrObj.getId().toString());
                	 subdir.mkdir();
                	 MCRDerivateCommands.export(derID, subdir.getPath(), null);                	 
                 }       	

                 LOGGER.info("Object " + id.toString() + " saved to " + xmlOutput.getCanonicalPath() + ".");
                 LOGGER.info("");
             } catch (MCRException ex) {
                 return;
             } catch(FileNotFoundException ex){
            	 LOGGER.error("Could not write to file "+id, ex);
             } catch (IOException ex){
            	 LOGGER.error("Error writing file "+id, ex);
             }	
        }
    }
    
    
    /**
     * Restore all MCRObject's from a directory with the following structure:
     *  - MCR_OBJECT_0001
     *    - MCR_DERIVATE_0001
     *      - file0001.pdf
     *      - file0002.pdf
     *    - MCR_DERIVATE_0002
     *      - file0003.txt
     *    - mcr_derivate_0001.xml
     *    - mcr_derivate_0002.xml
     *   - MCR_OBJECT_0002
     *     - MCR_DERIVATE_0003
     *       - file004.pdf
     *     - mcr_derivate_0003.xml
     *   - mcr_object_0001.xml
     *   - mcr_object_0002.xml
     *     
     *  @param dirname
     *            the directory name from where to restore the objects
     * 
     */
    @MCRCommand(syntax = "restore all objects from directory {0}", help = "The command restores all objects from directory {0} including all derivates")
    public static final void restoreAllObjects(String dirname) {
        // check dirname
        File dir = new File(dirname);
        if (dir.isFile()) {
            LOGGER.error(dirname + " is not a dirctory.");
            return;
        }
        File[] files = dir.listFiles();
        Arrays.sort(files);
        for(File objectFile:files){
        	//ignore directories
        	if(objectFile.isDirectory()){continue;}
        	String id = objectFile.getName().substring(0, objectFile.getName().length()-4);
        	LOGGER.info(" ... processing object " + id);
        	try{
        		MCRObject mcrObj = new MCRObject(objectFile.toURI());
        	    mcrObj.setImportMode(true); //true = servdates are taken from xml file;
        	    MCRMetadataManager.update(mcrObj);
        	    MCRHIBConnection.instance().getSession().flush();
              	       
        	    //load derivates first:
        	    File objDir = new File(dir, id);
        	    if(objDir.exists()){
        	    for(File f: objDir.listFiles()){
        	     	if(f.isFile() && f.getName().endsWith(".xml")){
        	     		MCRObjectID derID = MCRObjectID.getInstance(f.getName().substring(0, f.getName().length()-4));
        	     		LOGGER.info(" ... processing derivate " + derID.toString());
        	     		LOGGER.info("Loading derivate "+f.getAbsolutePath()+" : File exists = "+f.exists());
        	     		if(MCRMetadataManager.exists(derID)){
        	    			MCRDerivateCommands.delete(derID.toString());
        	    		}
        	    		MCRDerivateCommands.loadFromFile(f.getAbsolutePath());
        	    		
	         	    	
	         	    	//set ACLs
	         	    	MCRDerivate mcrDer = new MCRDerivate(f.toURI());
	             	    mcrDer.setImportMode(true); //true = servdates are taken from xml file;
	             	    while(mcrDer.getService().getRulesSize()>0){
	             		   MCRMetaAccessRule rule = mcrDer.getService().getRule(0);
	             		   String permission = mcrDer.getService().getRulePermission(0);
	             		   MCRAccessManager.updateRule(derID, permission, rule.getCondition(), "");
	             		   mcrDer.getService().removeRule(0);
	             	   }
	             	   MCRHIBConnection.instance().getSession().flush(); 
        	    	}
        	    }
        	    }
        	   // MCRObjectCommands.updateFromFile(objectFile.getAbsolutePath());
        	    
        	    //set AccessRules
        	    mcrObj = new MCRObject(objectFile.toURI());
        	    mcrObj.setImportMode(true); //true = servdates are taken from xml file;
        	    MCRMetadataManager.update(mcrObj);
        	    while(mcrObj.getService().getRulesSize()>0){
        	    	MCRMetaAccessRule rule = mcrObj.getService().getRule(0);
        	    	String permission = mcrObj.getService().getRulePermission(0);
        	    	
        	    	MCRAccessManager.updateRule(id, permission, rule.getCondition(), "");
        	    	mcrObj.getService().removeRule(0);
        	    }
        	    	 
//               add ACL's
        	    Iterator<String> it = MCRAccessManager.getPermissionsForID(id.toString()).iterator();
                while(it.hasNext()){
                	 String s = it.next();
                     Element rule = MCRAccessManager.getAccessImpl().getRule(id.toString(), s);
                     mcrObj.getService().addRule(s, rule);
                 }               
        	}
        	catch(MCRActiveLinkException ale){
        		LOGGER.error("Linkage error", ale);
        	}
        	catch(SAXParseException spe){
        		LOGGER.error("SAXParseException", spe);
        	} catch (IOException e) {
        		LOGGER.error("IOException" , e);
			}
        }       
    }
    
    /**
     * Updates the URN Store by parsing all Metadata Objects
     * 
     */
    
    @MCRCommand(syntax = "repair urn store", help = "The command parses through all metadata objects and updates the urns in the URN store if necessary")
    public static final void repairURNStore() throws MCRException{
    	try{
    		 for (String mcrid : MCRXMLMetadataManager.instance().listIDs()) {
    			 // if object do'snt exist - no exception is catched!
                 MCRObject mcrObj = MCRMetadataManager.retrieveMCRObject(MCRObjectID.getInstance(mcrid));                 
                 MCRMetaElement me = mcrObj.getMetadata().getMetadataElement("urns");
                 if(me!=null){
                	 MCRMetaLangText mltUrn = (MCRMetaLangText)me.getElement(0);
                	 String urnNew = mltUrn.getText();
                	 if(MCRURNManager.hasURNAssigned(mcrid)){
                		 if(!(MCRURNManager.getURNforDocument(mcrid).equals(urnNew))){
                			 MCRURNManager.removeURNByObjectID(mcrid);
                			 MCRURNManager.assignURN(urnNew, mcrid);
                		 }
                	 }
                	 else{
                	 		MCRURNManager.assignURN(urnNew, mcrid);
                	 }
                	 
                }
    		 }
        } catch (Exception e) {
        	throw new MCRException("Error while repairing URN Store", e);
        }   
    }  
   
    @MCRCommand(syntax = "create directory {0}", help = "The command creates a directory. If MyCoRe Properties are specified as part of the path, they will be replaced.")
    public static final void createDirectory(String dirname) {
    	while(dirname.contains("${")){
    		int start = dirname.indexOf("${");
    		int end = dirname.indexOf("}", start);
    	
    		if(end>start && end < dirname.length()){
    			String prop = dirname.substring(start+2, end);
    			if(prop.length()>0){
    				String value = MCRConfiguration.instance().getString(prop, "");
    				dirname = dirname.substring(0,start) + value + dirname.substring(end+1);
    			}
    		}
    	}
    	// check dirname
    	File dir = new File(dirname);
    	dir.mkdirs();
    }
    
}