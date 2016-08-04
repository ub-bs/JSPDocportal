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

package org.mycore.frontend.jsp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.mycore.access.MCRAccessException;
import org.mycore.access.MCRAccessManager;
import org.mycore.common.MCRException;
import org.mycore.common.MCRPersistenceException;
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
import org.mycore.frontend.workflowengine.jbpm.MCRJbpmWorkflowBase;
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
public class MCRJSPDocportalCommands extends MCRAbstractCommands {
    /** The logger */
    private static Logger LOGGER = Logger.getLogger(MCRJSPDocportalCommands.class.getName());

    /**
     * The command deletes a process instance of the workflow engine
     * 	if you've got to do this, you must restart your application server
     * 	for reinitializing your caches
     * @param strProcessID
     *        		String processId as String
     * @throws MCRException
     */

    @MCRCommand(syntax = "delete jbpm process {0}", help = "The command deletes a process instance of the jbpm workflow engine {0}")
    public static final void deleteProcess(String strProcessID) throws MCRException {
        try {
            long processID = Long.valueOf(strProcessID).longValue();
            MCRJbpmWorkflowBase.deleteProcessInstance(processID);
        } catch (Exception e) {
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
    public static final void deployProcessDefinition(String resource) throws MCRException {
        try {
            MCRJbpmWorkflowBase.deployProcess(resource);
        } catch (Exception e) {
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
    public static final void createSchema() throws MCRException {
        try {
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
    public static final List<String> backupAllObjects(String type, String dirname) {
        // check dirname
        List<String> commandList = new ArrayList<String>();
        File dir = new File(dirname);
        if (dir.isDirectory()) {
            for (String id : MCRXMLMetadataManager.instance().listIDsOfType(type)) {
                commandList.add("backup object " + id + " to directory " + dirname);
            }
        } else {
            LOGGER.error(dirname + " is not a dirctory.");
        }
        return commandList;
    }

    @MCRCommand(syntax = "backup object {0} to directory {1}", help = "The command backups a single object {0} into the directory {1} including all derivates")
    public static final void backupObject(String id, String dirname) {
        File dir = new File(dirname);
        if (dir.isFile()) {
            LOGGER.error(dirname + " is not a dirctory.");
            return;
        }
        // check dirname
        try {
            // if object do'snt exist - no exception is catched!
            MCRObject mcrObj = MCRMetadataManager.retrieveMCRObject(MCRObjectID.getInstance(id));

            //               add ACL's
            Iterator<String> it = MCRAccessManager.getPermissionsForID(id.toString()).iterator();
            while (it.hasNext()) {
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
            if (mcrStructure == null)
                return;
            for (MCRMetaLinkID derivate : mcrStructure.getDerivates()) {
                String derID = derivate.getXLinkHref();
                File subdir = new File(dirname, mcrObj.getId().toString());
                subdir.mkdir();
                MCRDerivateCommands.export(derID, subdir.getPath(), null);
            }

            LOGGER.info("Object " + id.toString() + " saved to " + xmlOutput.getCanonicalPath() + ".");
            LOGGER.info("");
        } catch (MCRException ex) {
            return;
        } catch (FileNotFoundException ex) {
            LOGGER.error("Could not write to file " + id, ex);
        } catch (IOException ex) {
            LOGGER.error("Error writing file " + id, ex);
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
    public static final List<String> restoreAllObjects(String dirname) {
        // check dirname
        ArrayList<String> commandList = new ArrayList<String>();
        File dir = new File(dirname);
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            Arrays.sort(files);
            for (File objectFile : files) {
                if (objectFile.isDirectory()) {
                    continue;
                }
                commandList.add("restore object from file " + objectFile.getAbsolutePath());

            }
        } else {
            LOGGER.error(dirname + " is not a dirctory.");
        }
        return commandList;
    }

    @MCRCommand(syntax = "restore object from file {0}", help = "The command restores a single object {0} including all derivates")
    public static final void restoreObject(String fileName) {
        //ignore directories
        File objectFile = new File(fileName);
        if (!objectFile.exists() && !objectFile.isFile()) {
            LOGGER.error(fileName + "is not a file.");
            return;
        }

        String id = objectFile.getName().substring(0, objectFile.getName().length() - 4);
        LOGGER.info(" ... processing object " + id);
        try {
            MCRObject mcrObj = new MCRObject(objectFile.toURI());
            mcrObj.setImportMode(true); //true = servdates are taken from xml file;
            //clone derivateIDs
            List<MCRMetaLinkID> derivateIDs = new ArrayList<MCRMetaLinkID>(mcrObj.getStructure().getDerivates());
            mcrObj.getStructure().getDerivates().clear();
            MCRMetadataManager.update(mcrObj);

            //load derivates in the order specified in MCRObject
            File objDir = new File(objectFile.getParentFile(), id);
            if (objDir.exists()) {
                for (MCRMetaLinkID derLinkID : derivateIDs) {
                    MCRObjectID derID = derLinkID.getXLinkHrefID();
                    LOGGER.info(" ... processing derivate " + derID.toString());
                    if (MCRMetadataManager.exists(derID)) {
                        try{
                        MCRDerivateCommands.delete(derID.toString());
                        }
                        catch(MCRPersistenceException mpe){
                            LOGGER.error("Could not delete derivate "+ derID.toString(), mpe);
                        }
                    }
                    File f = new File(objDir, derID.toString() + ".xml");
                    LOGGER.info("Loading derivate " + f.getAbsolutePath() + " : file exists?: " + f.exists());

                    MCRDerivate mcrDer = new MCRDerivate(f.toURI());
                    mcrDer.setImportMode(true); //true = servdates are taken from xml file;

                    // override creation dates with the information from the xml file
                    Date dateCreated = mcrDer.getService().getDate("createdate");
                    Path p = objDir.toPath().resolve(derID.toString());
                    LOGGER.info(p);
                    Files.walkFileTree(p, new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs)
                            throws IOException {
                            if (attrs.creationTime().toMillis() > dateCreated.getTime()) {
                                BasicFileAttributeView basicView = Files.getFileAttributeView(dir,
                                    BasicFileAttributeView.class);
                                basicView.setTimes(null, null, FileTime.fromMillis(dateCreated.getTime()));
                            }
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs)
                            throws IOException {
                            LOGGER.info("Update create date of file: " + file.toString() + ":" + attrs.creationTime());
                            if (attrs.creationTime().toMillis() > dateCreated.getTime()) {
                                BasicFileAttributeView basicView = Files.getFileAttributeView(file,
                                    BasicFileAttributeView.class);
                                basicView.setTimes(null, null, FileTime.fromMillis(dateCreated.getTime()));
                            }
                            BasicFileAttributeView basicView = Files.getFileAttributeView(file,
                                BasicFileAttributeView.class);
                            LOGGER.info("   -------------> " + basicView.readAttributes().creationTime());
                            return FileVisitResult.CONTINUE;
                        }
                    });

                    if(MCRMetadataManager.exists(derID)){
                        MCRDerivateCommands.updateFromFile(f.getAbsolutePath());
                    }
                    else{
                        MCRDerivateCommands.loadFromFile(f.getAbsolutePath());
                    }
                    

                    //set ACLs
                    while (mcrDer.getService().getRulesSize() > 0) {
                        MCRMetaAccessRule rule = mcrDer.getService().getRule(0);
                        String permission = mcrDer.getService().getRulePermission(0);
                        MCRAccessManager.updateRule(derID, permission, rule.getCondition(), "");
                        mcrDer.getService().removeRule(0);
                    }
                }
            }

            //set AccessRules
            while (mcrObj.getService().getRulesSize() > 0) {
                MCRMetaAccessRule rule = mcrObj.getService().getRule(0);
                String permission = mcrObj.getService().getRulePermission(0);

                MCRAccessManager.updateRule(id, permission, rule.getCondition(), "");
                mcrObj.getService().removeRule(0);
            }
        } catch (MCRActiveLinkException | MCRAccessException | SAXParseException | IOException e) {
            LOGGER.error(e);
        }
    }

    /**
     * Updates the URN Store by parsing all Metadata Objects
     * 
     */


    @MCRCommand(syntax = "repair urn store", help = "The command parses through all metadata objects and updates the urns in the URN store if necessary")
    public static final void repairURNStore() throws MCRException {
        try {
            for (String mcrid : MCRXMLMetadataManager.instance().listIDs()) {
                // if object do'snt exist - no exception is catched!
                MCRObject mcrObj = MCRMetadataManager.retrieveMCRObject(MCRObjectID.getInstance(mcrid));
                MCRMetaElement me = mcrObj.getMetadata().getMetadataElement("urns");
                if (me != null) {
                    MCRMetaLangText mltUrn = (MCRMetaLangText) me.getElement(0);
                    String urnNew = mltUrn.getText();
                    if (MCRURNManager.hasURNAssigned(mcrid)) {
                        if (!(MCRURNManager.getURNforDocument(mcrid).equals(urnNew))) {
                            MCRURNManager.removeURNByObjectID(mcrid);
                            MCRURNManager.assignURN(urnNew, mcrid);
                        }
                    } else {
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
        while (dirname.contains("${")) {
            int start = dirname.indexOf("${");
            int end = dirname.indexOf("}", start);

            if (end > start && end < dirname.length()) {
                String prop = dirname.substring(start + 2, end);
                if (prop.length() > 0) {
                    String value = MCRConfiguration.instance().getString(prop, "");
                    dirname = dirname.substring(0, start) + value + dirname.substring(end + 1);
                }
            }
        }
        // check dirname
        File dir = new File(dirname);
        dir.mkdirs();
    }

}
