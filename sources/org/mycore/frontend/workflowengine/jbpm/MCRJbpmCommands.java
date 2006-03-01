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

import org.apache.log4j.Logger;
import org.mycore.common.MCRException;
import org.mycore.frontend.cli.MCRAbstractCommands;
import org.mycore.frontend.cli.MCRCommand;

/**
 * This class provides a set of commands for the org.mycore.access management
 * which can be used by the command line interface.
 * 
 * @author Heiko Helmbrecht
 * @version $Revision$ $Date$
 */
public class MCRJbpmCommands extends MCRAbstractCommands {
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
    		MCRJbpmWorkflowBase wfb = new MCRJbpmWorkflowBase();
    		wfb.createSchema();
        } catch (Exception e) {
        	LOGGER.error("Error in creating the schema for the workflow database", e);
            throw new MCRException("Error in creating the schema for the workflow database", e);
        }   
    }
}
