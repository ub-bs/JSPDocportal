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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.mycore.access.MCRAccessInterface;
import org.mycore.access.MCRAccessManager;
import org.mycore.common.MCRConfiguration;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.workflowengine.strategies.MCRWorkflowDirectoryManager;
import org.mycore.services.fieldquery.MCRQuery;
import org.mycore.services.fieldquery.MCRResults;
import org.mycore.services.fieldquery.MCRSearcher;
import org.mycore.services.fieldquery.MCRSearcherFactory;

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
	private static MCRAccessInterface AI = MCRAccessManager.getAccessImpl();

	protected static MCRConfiguration config = MCRConfiguration.instance();

	public final static MCRObjectID retrieveUsersAuthorId(String userid){
		
    	MCRResults mcrResult =  MCRWorkflowUtils.queryMCRForAuthorByUserid(userid);
    	logger.debug("Results found hits:" + mcrResult.getNumHits());    
    	if ( mcrResult.getNumHits() > 0 ) {
    		String authorID = mcrResult.getHit(0).getID();
    		return new MCRObjectID(authorID);
    	}
    	return null;
	}
	
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
		ArrayList workfiles = new ArrayList();
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
		ArrayList workfiles = new ArrayList();
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

	/**
	 * returns all authors (hopefully just one ;-) which are associated with the
	 * given user id
	 * 
	 * @param userid
	 * @return the query result as MCRResult
	 */
	public static MCRResults queryMCRForAuthorByUserid(String userid) {
		Element query = new Element("query");
		query.setAttribute("maxResults", "1");
		Element conditions = new Element("conditions");
		conditions.setAttribute("format", "xml");
		query.addContent(conditions);
		Element op = new Element("boolean");
		op.setAttribute("operator", "AND");
		conditions.addContent(op);

		Element condition = new Element("condition");
		condition.setAttribute("operator", "=");
		condition.setAttribute("field", "userID");
		condition.setAttribute("value", userid);
		op.addContent(condition);

		condition = new Element("condition");
		condition.setAttribute("operator", "=");
		condition.setAttribute("field", "objectType");
		condition.setAttribute("value", "author");
		op.addContent(condition);

		Element hosts = new Element("hosts");
		query.addContent(hosts);
		Element host = new Element("host");
		hosts.addContent(host);
		host.setAttribute("field", "local");

		XMLOutputter out = new XMLOutputter(org.jdom.output.Format
				.getPrettyFormat());
		logger.debug("generated query: \n" + out.outputString(query));
		Document jQuery = new Document(query);
		
//		BUGFIX by Robert:
//		The original lines of code cannot be executed properly. The method fails to search data for ??? remote query ???
//      Look  into method: search(MCRQuery query, boolean comesFromRemoteHost) in MCRQueryManager
//		line:  MCRQueryClient.search(query, results) does not come back properly;
//		The problem only occurs when the "local" result ist empty.
//		
//
//Original:		
//		MCRResults mcrResult = MCRQueryManager.search(MCRQuery.parseXML(jQuery));
//		
//
//Workaround:		
//	     simplification based on org.myocre.services.fieldquery.MCRQueryManager
//		 only considering local results
//	     - line 154: Method buildResults(...)
			MCRQuery mcrQuery = MCRQuery.parseXML(jQuery);
	        String index="metadata";
	        MCRSearcher searcher = MCRSearcherFactory.getSearcherForIndex(index);
            MCRResults mcrResult = searcher.search(mcrQuery.getCondition(), mcrQuery.getMaxResults(), mcrQuery.getSortBy(), false);
//Workaround - end

		return mcrResult;
	}

	
	public static Map getAccessRulesMap(String objid) {
		List liPerms = AI.getPermissionsForID(objid);        
        Map htRules = new Hashtable();
        for (int  i = 0; i< liPerms.size(); i++) {
        	Element eRule = AI.getRule( objid,(String)liPerms.get(i));
        	htRules.put((String)liPerms.get(i),eRule);
        }
        return htRules;
	}	

	public static void setAccessRulesMap(String objid, Map htRules ) {
		if ( htRules == null || htRules.isEmpty()) {
			logger.warn("Can't reset AccessRules, they are empty");
			return;
		}
		AI.removeAllRules(objid);
		for (Iterator it = htRules.keySet().iterator(); it.hasNext();) {
			String perm = (String) it.next();
			Element eRule = (Element)htRules.get(perm);
			AI.addRule(objid,perm,eRule,"");
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
}
