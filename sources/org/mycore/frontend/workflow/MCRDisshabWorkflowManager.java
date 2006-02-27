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

// package
package org.mycore.frontend.workflow;

// Imported java classes
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.mycore.services.fieldquery.MCRQueryManager;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRDefaults;
import org.mycore.common.MCRSession;
import org.mycore.common.MCRSessionMgr;
import org.mycore.datamodel.metadata.MCRMetaAddress;
import org.mycore.datamodel.metadata.MCRMetaBoolean;
import org.mycore.datamodel.metadata.MCRMetaLangText;
import org.mycore.datamodel.metadata.MCRMetaPersonName;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.services.fieldquery.MCRResults;
import org.mycore.services.nbn.MCRNBN;
import org.mycore.services.nbn.MCRNBNManager;
import org.mycore.user2.MCRUser;
import org.mycore.user2.MCRUserMgr;

/**
 * This class holds methods to manage the workflow file system of MyCoRe.
 * 
 * @author Heiko Helmbrecht, Anja Schaar
 * @version $Revision$ $Date$
 */

public class MCRDisshabWorkflowManager {
	
	protected static MCRDisshabWorkflowManager singleton;
	private static MCRConfiguration config = null;
	private static Logger logger = Logger.getLogger(MCRDisshabWorkflowManager.class.getName());
	private static String sender = "";
	private static MCRNBNManager nbnmgr=null;
	
	private Hashtable ht = null;
	private Hashtable mt = null;
	private XMLOutputter out = new XMLOutputter(org.jdom.output.Format.getPrettyFormat());

	/**
	 * Returns the disshab workflow manager singleton.
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static synchronized MCRDisshabWorkflowManager instance() throws Exception {
		if (singleton == null)
			singleton = new MCRDisshabWorkflowManager();
		return singleton;
	}

	/**
	 * The constructor of this class.
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	protected MCRDisshabWorkflowManager() throws Exception {
		config = MCRConfiguration.instance();
		sender = config.getString("MCR.editor_mail_sender",	"mcradmin@localhost");
		nbnmgr = (MCRNBNManager) config.getInstanceOf("MCR.NBN.ManagerImplementation");
		// int tables
		ht = new Hashtable();
		mt = new Hashtable();
	}


	/**
	 * The method return the information mail address for a given MCRObjectID
	 * type.
	 * 
	 * @param type
	 *            the MCRObjectID type
	 * @param todo
	 *            the todo action String from the workflow.
	 * @return the List of the information mail addresses
	 */
	public List getMailAddress(String type, String todo) {
		if ((type == null) || ((type = type.trim()).length() == 0)
				||
			(todo == null) || ((todo = todo.trim()).length() == 0)				
				) {
			return new ArrayList();
		}

		if (mt.containsKey(type + "_" + todo)) {
			return (List) mt.get(type + "_" + todo);
		}
		String mailaddr = config.getString("MCR.editor_" + type + "_" + todo + "_mail", "");
		ArrayList li = new ArrayList();
		if ((mailaddr == null) || ((mailaddr = mailaddr.trim()).length() == 0)) {
			mt.put(type, li);
			logger.warn("No mail address for " + type + "_" + todo	+ " is in the configuration.");
			return li;
		}
		StringTokenizer st = new StringTokenizer(mailaddr, ",");
		while (st.hasMoreTokens()) {
			li.add(st.nextToken());
		}
		mt.put(type, li);
		return li;
	}

	/**
	 * The method return the mail sender adress form the configuration.
	 * @return the mail sender adress
	 */
	public String getMailSender() {
		return sender;
	}

	public String getDisshabAuthor(String userid){
		Element query = buildQueryforAuthor(userid);
		Document jQuery = new Document(query);    	
    	MCRResults mcrResult =  MCRQueryManager.search(jQuery);
    	logger.debug("Results found hits:" + mcrResult.getNumHits());    
    	if ( mcrResult.getNumHits() > 0 ) {    			
    		return mcrResult.getHit(0).getID();
    	}
		return "";
	 }
	
	public String getURNReservationForAuthor(String authorid){
		Set urnset = new HashSet();
		String nissAusWorkflow = "123456788";
		urnset = nbnmgr.listReservedURNs();
		String urn = ""; 
		if ( urnset.contains(nissAusWorkflow) ){
			String prefix = config.getString("MCR.NBN.NamespacePrefix");
			urn =  prefix+"-"+nissAusWorkflow;
		}else {
			urn = createUrnReservationForAuthor(authorid);		    
		}
		return urn;			
	}
		
	public String createUrnReservationForAuthor(String authorid){
		MCRNBN mcrurn = new MCRNBN(authorid,"URN for Dissertation");
		nbnmgr.reserveURN(mcrurn);
		return mcrurn.getURN();
	}
	
	
	public String createAuthorforDisshab(String userid){
		MCRUser user = null;

		try {
			user = MCRUserMgr.instance().retrieveUser(userid);
		} catch (Exception noUser) {
			//TODO Fehlermeldung
			logger.warn("user dos'nt exist userid=" + userid);
			return "";			
		}
		
		MCRObject author = new MCRObject();
		MCRMetaPersonName pname = new MCRMetaPersonName();
		String fullname = user.getUserContact().getFirstName() + " " + user.getUserContact().getLastName();
		pname.setSubTag("name");
		pname.setLang("de");
		pname.set(user.getUserContact().getFirstName(),
				  user.getUserContact().getLastName(), 
				  user.getUserContact().getLastName(), fullname, "", "", user.getUserContact().getSalutation());
			
		MCRMetaBoolean female = new MCRMetaBoolean();
		female.setSubTag("female");
		female.setLang("de");
		female.setValue("false");
		if ( user.getUserContact().getSalutation().equals("Frau")) 
				female.setValue("true");
		
		MCRMetaAddress padr = new MCRMetaAddress();
		padr.setSubTag("address");
		padr.setLang("de");
		
		if ( user.getUserContact().getCountry().length()==0){
			user.getUserContact().setCountry("-");
		}
		if ( user.getUserContact().getState().length()==0){
			user.getUserContact().setState("-");
		}
		if ( user.getUserContact().getPostalCode().length()==0){
			user.getUserContact().setPostalCode("-");
		}
		if ( user.getUserContact().getCity().length()==0){
			user.getUserContact().setCity("-");
		}
		if ( user.getUserContact().getStreet().length()==0){
			user.getUserContact().setStreet("-");
		}
		
		padr.set(user.getUserContact().getCountry(), user.getUserContact().getState(),
				user.getUserContact().getPostalCode(),user.getUserContact().getCity(),
				user.getUserContact().getStreet(), "-");

		MCRMetaLangText userID = new MCRMetaLangText();
		userID.setSubTag("userid");
		userID.setLang("de");
		userID.setText(user.getID());
		
		
		Element ePname = pname.createXML();
		Element ePnames = new Element("names");
		ePnames.setAttribute("class","MCRMetaPersonName");
		ePnames.addContent(ePname);

		
		Element eFemale = female.createXML();
		Element eFemales = new Element("females");
		eFemales.setAttribute("class","MCRMetaBoolean");	
		eFemales.addContent(eFemale);
		
		Element ePadr = padr.createXML();
		Element ePadrs = new Element("addresses");
		ePadrs.setAttribute("class","MCRMetaAddress");
		ePadrs.addContent(ePadr);
		
		Element eUserID = userID.createXML();
		Element eUserIDs = new Element("userids");
		eUserIDs.setAttribute("class","MCRMetaLangText");	
		eUserIDs.addContent(eUserID);
		
		
		Element mycoreauthor = new Element ("mycoreobject");
		MCRObjectID ID = new MCRObjectID();
 	    String base = config.getString("MCR.default_project_id","DocPortal")+"_author";
		ID.setNextFreeId(base);
		mycoreauthor.addNamespaceDeclaration(org.jdom.Namespace.getNamespace("xsi", MCRDefaults.XSI_URL));
		mycoreauthor.setAttribute("noNamespaceSchemaLocation", "datamodel-author.xsd", org.jdom.Namespace.getNamespace("xsi", MCRDefaults.XSI_URL));
		mycoreauthor.setAttribute("ID", ID.toString());	 
		mycoreauthor.setAttribute("label", ID.toString());
		
		Element structure = new Element ("structure");			
		Element metadata = new Element ("metadata");	
		Element service = new Element ("service");
		
	    metadata.addContent(ePnames);
	    metadata.addContent(eFemales);
	    metadata.addContent(ePadrs);
	    metadata.addContent(eUserIDs);
	    
	    mycoreauthor.addContent(structure);
	    mycoreauthor.addContent(metadata);
	    mycoreauthor.addContent(service);
	    
		Document authordoc = new Document(mycoreauthor);
		author.setFromJDOM(authordoc);
		try {
				author.createInDatastore();
		} catch ( Exception ex){
			//TODO Fehlermeldung
			logger.warn("Could not Create authors object from the user object " + user.getID());
			return "";
		}
		
   	    return author.getId().getId();
	}
	
	private Element  buildQueryforAuthor(String userid) {
	    	
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
	   		condition.setAttribute("field", "userid");
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
	    	
	    	logger.debug("generated query: \n" + out.outputString(query));
	    	return query;
	    	
	    }
	     

}
