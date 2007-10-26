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
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.mycore.access.MCRAccessInterface;
import org.mycore.access.MCRAccessManager;
import org.mycore.common.MCRException;
import org.mycore.common.xml.MCRXMLHelper;
import org.mycore.datamodel.common.MCRXMLTableManager;
import org.mycore.datamodel.metadata.MCRDerivate;
import org.mycore.datamodel.metadata.MCRMetaLinkID;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.datamodel.metadata.MCRObjectStructure;
import org.mycore.frontend.cli.MCRAbstractCommands;
import org.mycore.frontend.cli.MCRCommand;
import org.mycore.frontend.cli.MCRDerivateCommands;
import org.mycore.frontend.cli.MCRObjectCommands;

/**
 * This class provides a set of commands for the org.mycore.access management
 * which can be used by the command line interface.
 * 
 * @author Heiko Helmbrecht
 * @version $Revision$ $Date$
 */
public class MCRJbpmCommands extends MCRAbstractCommands {
    /** The ACL interface */
    private static final MCRAccessInterface ACCESS_IMPL = MCRAccessManager.getAccessImpl();
    
	/** The logger */
    private static Logger LOGGER = Logger.getLogger(MCRJbpmCommands.class.getName());

    /**
     * The constructor.
     */
    public MCRJbpmCommands() {
        super();

        MCRCommand com = null;

        com = new MCRCommand("create jbpm database schema", "org.mycore.frontend.workflowengine.jbpm.MCRJbpmCommands.createSchema", "The command DELETES the old workflow database schema and is loading a new empty schema from configuration");
        command.add(com);
        
        com = new MCRCommand("deploy jbpm processdefinition from file {0}", "org.mycore.frontend.workflowengine.jbpm.MCRJbpmCommands.deployProcessDefinition String", "The command deploys a process definition to the database from the file {0}");
        command.add(com);
        
        com = new MCRCommand("delete jbpm process {0}", "org.mycore.frontend.workflowengine.jbpm.MCRJbpmCommands.deleteProcess String", "The command deletes a processinstance of the jbpm workflow engine {0}");
        command.add(com);     
        
        com = new MCRCommand("backup all objects of type {0} to directory {1}", "org.mycore.frontend.workflowengine.jbpm.MCRJbpmCommands.backupAllObjects String String", "The command backups all objects of type {0} into the directory {1} including all derivates");
        command.add(com);
        
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
     * Save all MCRObject's to files named <em>MCRObjectID</em> .xml in a
     * <em>dirname</em>directory for the data type <em>type</em>. The
     * method use the converter stylesheet mcr_<em>style</em>_object.xsl.
     * 
     * @param fromID
     *            the ID of the MCRObject from be save.
     * @param toID
     *            the ID of the MCRObject to be save.
     * @param dirname
     *            the filename to store the object
     * @param style
     *            the type of the stylesheet
     */
    public static final void backupAllObjects(String type, String dirname) {
        // check dirname
        File dir = new File(dirname);

        if (dir.isFile()) {
            LOGGER.error(dirname + " is not a dirctory.");
            return;
        }
        
        MCRXMLTableManager tm = MCRXMLTableManager.instance();
        for (String id : tm.retrieveAllIDs(type)) {
        	
             try {
                 // if object do'snt exist - no exception is catched!
                 MCRObject mcrObj = new MCRObject();
                 mcrObj.receiveFromDatastore(id);
                 
            	 
//               add ACL's
                 List l = ACCESS_IMPL.getPermissionsForID(id.toString());
                 for (int i = 0; i < l.size(); i++) {
                     Element rule = ACCESS_IMPL.getRule(id.toString(), (String) l.get(i));
                     mcrObj.getService().addRule((String) l.get(i), rule);
                 }
                 // build JDOM
                 Document xml = mcrObj.createXML();
                 
                 File xmlOutput = new File(dir, id.toString() + ".xml");
                 FileOutputStream out = new FileOutputStream(xmlOutput);
                 
                 new org.jdom.output.XMLOutputter(Format.getPrettyFormat()).output(xml, out);
                 out.flush();
                 out.close();
                 
                 MCRObjectStructure mcrStructure = mcrObj.getStructure();
                 if(mcrStructure == null) return;
                 for(int i=0; i<mcrStructure.getDerivateSize();i++){
                	 MCRMetaLinkID derivate = mcrStructure.getDerivate(i);
                	 String derID = derivate.getXLinkHref();
                	 File subdir = new File(dirname,mcrObj.getId().getId());
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
}
