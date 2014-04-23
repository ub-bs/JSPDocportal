/**
 * $RCSfile$
 * $Revision$ $Date$
 *
 * This file is part of ** M y C o R e **
 * Visit our homepage at http://www.mycore.de/ for details.
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
 * along with this program, normally in the file license.txt.
 * If not, write to the Free Software Foundation Inc.,
 * 59 Temple Place - Suite 330, Boston, MA  02111-1307 USA
 *
 **/

package org.mycore.frontend.workflowengine.jbpm;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jbpm.bytes.ByteArray;
import org.jbpm.context.exe.ContextInstance;
import org.jdom2.Element;
import org.mycore.access.MCRAccessManager;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.common.MCRException;
import org.mycore.frontend.workflowengine.strategies.MCRWorkflowDirectoryManager;

/**
 * This class holds useful methods for the workflow.
 * 
 * @author Heiko Helmbrecht, Anja Schaar, Robert Stephan
 * @version $Revision$ $Date$
 * 
 */
public class MCRWorkflowUtils {
	private static Logger logger = Logger
			.getLogger(MCRWorkflowUtils.class.getName());

	protected static MCRConfiguration config = MCRConfiguration.instance();

	
	/**
	 * The method return a ArrayList of file names from objects of a special
	 * type in the workflow
	 * 
	 * @param type
	 *            the MCRObjectID type attribute
	 * @return an ArrayList of file names
	 */
	public final static ArrayList getAllObjectFileNames(String type) {
		String dirname = MCRWorkflowDirectoryManager.getWorkflowDirectory(type);
		ArrayList<String> workfiles = new ArrayList<String>();
		if (!dirname.equals(".")) {
			File dir = new File(dirname);
			String[] dirl = null;
			if (dir.isDirectory()) {
				dirl = dir.list();
			}
			if (dirl != null) {
				for (int i = 0; i < dirl.length; i++) {
					if ((dirl[i].indexOf(type) != -1)
							&& (dirl[i].endsWith(".xml"))) {
						workfiles.add(dirl[i]);
					}
				}
			}
			java.util.Collections.sort(workfiles);
		}
		return workfiles;
	}

	/**
	 * The method return a ArrayList of file names from derivates from objects
	 * of a special type in the workflow
	 * 
	 * @param type
	 *            the MCRObjectID type attribute
	 * @return an ArrayList of file names
	 */
	public final static ArrayList getAllDerivateFileNames(String type) {
		String dirname = MCRWorkflowDirectoryManager.getWorkflowDirectory(type);
		ArrayList<String> workfiles = new ArrayList<String>();
		if (!dirname.equals(".")) {
			File dir = new File(dirname);
			String[] dirl = null;
			if (dir.isDirectory()) {
				dirl = dir.list();
			}
			if (dirl != null) {
				for (int i = 0; i < dirl.length; i++) {
					if ((dirl[i].indexOf("_derivate_") != -1)
							&& (dirl[i].endsWith(".xml"))) {
						workfiles.add(dirl[i]);
					}
				}
			}
			java.util.Collections.sort(workfiles);
		}
		return workfiles;
	}

	
	
	public static Map getAccessRulesMap(String objid) {
		Iterator<String> it = MCRAccessManager.getPermissionsForID(objid).iterator();        
        Map<String, Element> htRules = new Hashtable<String, Element>();
        while(it.hasNext()){
        	String s = it.next();
           	Element eRule = MCRAccessManager.getAccessImpl().getRule( objid, s);
           	htRules.put(s, eRule);
        }
        return htRules;
	}	

	public static void setAccessRulesMap(String objid, Map htRules ) {
		if ( htRules == null || htRules.isEmpty()) {
			logger.warn("Can't reset AccessRules, they are empty");
			return;
		}
		MCRAccessManager.getAccessImpl().removeAllRules(objid);
		for (Iterator it = htRules.keySet().iterator(); it.hasNext();) {
			String perm = (String) it.next();
			Element eRule = (Element)htRules.get(perm);
			MCRAccessManager.addRule(objid,perm,eRule,"");
		}
	}	
	/**
	 * checks if a string is null or empty
	 * @param test
	 * @return true, if a string is empty or null
	 */
    public static boolean isEmpty(String test){
		if(test == null || test.equals("")){
			return true;
		}else{
			return false;
		}
	}
    
	/**
	 * returns a large String variable from Workflow;
	 * JBPM allowes only 255 Characters (definition of the MySQL table) 
	 * @param varName -the veriable name
	 * @param processID - the processID
	 * @return
	 */
	public static String getLargeStringVariableFromWorkflow(String varName, ContextInstance ctxI){
		try{
			ByteArray ba = (ByteArray)ctxI.getVariable(varName);
			if(ba==null){return null;}
			byte[] b = ba.getBytes();
			String s = new String(b, "UTF-8");
			return s;			
		}
		catch(Exception e){
			logger.error("could not deserialize workflow variable [" + varName + "] for process [" + ctxI.getProcessInstance().getId()+ "]",e);
			return null;
		}finally{
		}
	}
	
	/**
	 * set a larger String as workflow variable
	 * JBPM allowes only 255 Characters (definition of the MySQL table)
	 * so we simply wrap the String into an object
	 * @param varName - the name of the variable
	 * @param varValue - the value of the variable
	 * @param processID - the ProcessID
	 */
	public static void setLargeStringVariableInWorkflow(String varName, String varValue, ContextInstance ctxI){
		try{
			ctxI.setVariable(varName, new ByteArray(varValue.getBytes("UTF-8")));
		}
		catch(UnsupportedEncodingException use){
			//do nothing
		
		}catch(MCRException e){
			logger.error("could not get workflow variable [" + varName + "] for process [" + ctxI.getProcessInstance().getId() + "]",e);
		}finally{
			//jbpmContext.close();
		}			
	}
	
}
