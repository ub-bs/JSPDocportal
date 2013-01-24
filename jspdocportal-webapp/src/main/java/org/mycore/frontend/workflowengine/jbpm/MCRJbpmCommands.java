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
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.mycore.access.MCRAccessManager;
import org.mycore.common.MCRException;
import org.mycore.datamodel.common.MCRActiveLinkException;
import org.mycore.datamodel.common.MCRXMLMetadataManager;
import org.mycore.datamodel.metadata.MCRDerivate;
import org.mycore.datamodel.metadata.MCRMetaAccessRule;
import org.mycore.datamodel.metadata.MCRMetaLinkID;
import org.mycore.datamodel.metadata.MCRMetadataManager;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.datamodel.metadata.MCRObjectStructure;
import org.mycore.frontend.cli.MCRAbstractCommands;
import org.mycore.frontend.cli.MCRCommand;
import org.mycore.frontend.cli.MCRDerivateCommands;
import org.xml.sax.SAXParseException;

/**
 * This class provides a set of commands for the org.mycore.access management
 * which can be used by the command line interface.
 * 
 * @author Heiko Helmbrecht
 * @version $Revision$ $Date$
 */
public class MCRJbpmCommands extends MCRAbstractCommands {
    /** The ACL interface */
        
	/** The logger */
    private static Logger LOGGER = Logger.getLogger(MCRJbpmCommands.class.getName());

    /**
     * The constructor.
     */
    public MCRJbpmCommands() {
        super();

        MCRCommand com = null;

        com = new MCRCommand("create jbpm database schema", "org.mycore.frontend.workflowengine.jbpm.MCRJbpmCommands.createSchema", "The command DELETES the old workflow database schema and is loading a new empty schema from configuration");
        addCommand(com);
        
        com = new MCRCommand("deploy jbpm processdefinition from file {0}", "org.mycore.frontend.workflowengine.jbpm.MCRJbpmCommands.deployProcessDefinition String", "The command deploys a process definition to the database from the file {0}");
        addCommand(com);
        
        com = new MCRCommand("delete jbpm process {0}", "org.mycore.frontend.workflowengine.jbpm.MCRJbpmCommands.deleteProcess String", "The command deletes a processinstance of the jbpm workflow engine {0}");
        addCommand(com);     
        
        com = new MCRCommand("backup all objects of type {0} to directory {1}", "org.mycore.frontend.workflowengine.jbpm.MCRJbpmCommands.backupAllObjects String String", "The command backups all objects of type {0} into the directory {1} including all derivates");
        addCommand(com);
        
        com = new MCRCommand("restore all objects from directory {0}", "org.mycore.frontend.workflowengine.jbpm.MCRJbpmCommands.restoreAllObjects String", "The command restores all objects from directory {0} including all derivates");
        addCommand(com);
        
    }

    /**
     * The command deletes a process instance of the workflow engine
     * 	if you've got to do this, you must restart your application server
     * 	for reinitializing your caches
     * @param strProcessID
     *        		String processId as String
     * @throws MCRException
     */
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
    public static final void restoreAllObjects(String dirname) {
        // check dirname
        File dir = new File(dirname);
        if (dir.isFile()) {
            LOGGER.error(dirname + " is not a dirctory.");
            return;
        }
        for(File objectFile:dir.listFiles()){
        	//ignore directories
        	if(objectFile.isDirectory()){continue;}
        	String id = objectFile.getName().substring(0, objectFile.getName().length()-4);
        	try{
        		MCRObject mcrObj = new MCRObject(objectFile.toURI());
        	    mcrObj.setImportMode(true); //true = servdates are taken from xml file;
        	    MCRMetadataManager.update(mcrObj);
              	       
        	    //load derivates first:
        	    File objDir = new File(dir, id);
        	    if(objDir.exists()){
        	    for(File f: objDir.listFiles()){
        	     	if(f.isFile() && f.getName().endsWith(".xml")){
        	     		MCRObjectID derID = MCRObjectID.getInstance(f.getName().substring(0, f.getName().length()-4));
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
}
