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
import org.mycore.common.MCRDefaults;
import org.mycore.datamodel.metadata.MCRMetaAddress;
import org.mycore.datamodel.metadata.MCRMetaBoolean;
import org.mycore.datamodel.metadata.MCRMetaLangText;
import org.mycore.datamodel.metadata.MCRMetaPersonName;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.workflowengine.strategies.MCRPermissionStrategy;
import org.mycore.services.fieldquery.MCRQuery;
import org.mycore.services.fieldquery.MCRQueryManager;
import org.mycore.services.fieldquery.MCRResults;
import org.mycore.user2.MCRUser;

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
		String dirname = MCRWorkflowEngineManagerFactory.getDefaultImpl().getWorkflowDirectory(type);
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
		String dirname = MCRWorkflowEngineManagerFactory.getDefaultImpl()
				.getWorkflowDirectory(type);
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
		
		MCRResults mcrResult = MCRQueryManager.search(MCRQuery.parseXML(jQuery));

		return mcrResult;
	}

	/**
	 * creates a new author from a given user and sets the given id if user ==
	 * null return an empty author object
	 * 
	 * @param user
	 * @return an author object of type MCRObject
	 * @deprecated moved to authorStrategy
	 */
	public static MCRObject createAuthorFromUser(MCRUser user, MCRObjectID id) {
		MCRObject author = new MCRObject();
		Element xmlElemAuthor = new Element("mycoreobject");
		xmlElemAuthor.addNamespaceDeclaration(org.jdom.Namespace.getNamespace(
				"xsi", MCRDefaults.XSI_URL));
		xmlElemAuthor.setAttribute("noNamespaceSchemaLocation",
				"datamodel-author.xsd", org.jdom.Namespace.getNamespace("xsi",
						MCRDefaults.XSI_URL));
		xmlElemAuthor.setAttribute("ID", id.toString());
		xmlElemAuthor.setAttribute("label", id.toString());

		Element structure = new Element("structure");
		Element metadata = new Element("metadata");
		Element service = new Element("service");

		if (user != null) {
			MCRMetaPersonName pname = new MCRMetaPersonName();
			String fullname = user.getUserContact().getFirstName() + " "
					+ user.getUserContact().getLastName();
			pname.setSubTag("name");
			pname.setLang("de");
			pname.set(user.getUserContact().getFirstName(), user
					.getUserContact().getLastName(), user.getUserContact()
					.getLastName(), fullname, "", "", user.getUserContact()
					.getSalutation());

			MCRMetaBoolean female = new MCRMetaBoolean();
			female.setSubTag("female");
			female.setLang("de");
			female.setValue("false");
			if (user.getUserContact().getSalutation().equals("Frau"))
				female.setValue("true");

			MCRMetaAddress padr = new MCRMetaAddress();
			padr.setSubTag("address");
			padr.setLang("de");

			if (user.getUserContact().getCountry().length() == 0) {
				user.getUserContact().setCountry("-");
			}
			if (user.getUserContact().getState().length() == 0) {
				user.getUserContact().setState("-");
			}
			if (user.getUserContact().getPostalCode().length() == 0) {
				user.getUserContact().setPostalCode("-");
			}
			if (user.getUserContact().getCity().length() == 0) {
				user.getUserContact().setCity("-");
			}
			if (user.getUserContact().getStreet().length() == 0) {
				user.getUserContact().setStreet("-");
			}
			if (user.getUserContact().getEmail().length() == 0) {
				user.getUserContact().setEmail("-");
			}

			padr.set(user.getUserContact().getCountry(), user.getUserContact()
					.getState(), user.getUserContact().getPostalCode(), user
					.getUserContact().getCity(), user.getUserContact()
					.getStreet(), "-");

			MCRMetaLangText userID = new MCRMetaLangText();
			userID.setSubTag("userid");
			userID.setLang("de");
			userID.setText(user.getID());

			
			Element ePname = pname.createXML();
			Element ePnames = new Element("names");
			ePnames.setAttribute("class", "MCRMetaPersonName");
			ePnames.setAttribute("textsearch", "true");
			ePnames.addContent(ePname);

			Element eFemale = female.createXML();
			Element eFemales = new Element("females");
			eFemales.setAttribute("class", "MCRMetaBoolean");
			eFemales.addContent(eFemale);

			Element ePadr = padr.createXML();
			Element ePadrs = new Element("addresses");
			ePadrs.setAttribute("class", "MCRMetaAddress");
			ePadrs.addContent(ePadr);

			Element eUserID = userID.createXML();
			Element eUserIDs = new Element("userids");
			eUserIDs.setAttribute("class", "MCRMetaLangText");
			eUserIDs.addContent(eUserID);

			MCRMetaLangText email = new MCRMetaLangText();
			email.setSubTag("email");
			email.setLang("de");
			email.setText(user.getUserContact().getEmail());
			
			Element eEmail = email.createXML();
			Element eEmails = new Element("emails");
			eEmails.setAttribute("class", "MCRMetaLangText");
			eEmails.addContent(eEmail); 
			
			metadata.addContent(ePnames);
			metadata.addContent(eFemales);
			metadata.addContent(ePadrs);
			metadata.addContent(eUserIDs);
			metadata.addContent(eEmails);
			
		} else{
			//metadata needs dummy data otherwise  author.createXML() failes
			Element ePnames = new Element("names");
			ePnames.setAttribute("class", "MCRMetaPersonName");
			ePnames.setAttribute("textsearch", "true");
						
			MCRMetaPersonName pname = new MCRMetaPersonName();
			pname.setSubTag("name");
			pname.setLang("de");
			pname.set("", "", "Neuer Autor", "", "", "", "");
			ePnames.addContent(pname.createXML());
						
			metadata.addContent(ePnames);
		}

		xmlElemAuthor.addContent(structure);
		xmlElemAuthor.addContent(metadata);
		xmlElemAuthor.addContent(service);

		Document authordoc = new Document(xmlElemAuthor);

		author.setFromJDOM(authordoc);

		// Robert: redundant?
		author.setId(id);

		return author;
	}
	
	public static void setDummyPermissions(String objid){
		for (int i = 0; i < MCRPermissionStrategy.defaultPermissionTypes.length; i++) {
			AI.addRule(objid, 
					MCRPermissionStrategy.defaultPermissionTypes[i], 
					MCRAccessManager.getTrueRule(), "");	
		}		
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
