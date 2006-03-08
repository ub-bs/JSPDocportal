package org.mycore.frontend.workflowengine.jbpm;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.mycore.access.MCRAccessInterface;
import org.mycore.access.MCRAccessManager;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRDefaults;
import org.mycore.common.MCRException;
import org.mycore.common.xml.MCRXMLHelper;
import org.mycore.datamodel.metadata.MCRMetaAddress;
import org.mycore.datamodel.metadata.MCRMetaBoolean;
import org.mycore.datamodel.metadata.MCRMetaLangText;
import org.mycore.datamodel.metadata.MCRMetaPersonName;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.workflow.MCREditorOutValidator;
import org.mycore.services.nbn.MCRNBN;
import org.mycore.user2.MCRUser;
import org.mycore.user2.MCRUserMgr;

public class MCRWorkflowEngineManagerBaseImpl implements MCRWorkflowEngineManagerInterface{
	private static Logger logger = Logger.getLogger(MCRWorkflowEngineManagerBaseImpl.class.getName());
	private static MCRWorkflowEngineManagerInterface singleton;
	protected static MCRConfiguration config = MCRConfiguration.instance();
	protected static MCRAccessInterface AI = MCRAccessManager.getAccessImpl();
	protected static HashMap editWorkflowDirectories ;
	
	private static String sender ;
	private static String GUEST_ID ;
	
	static{
		sender = config.getString("MCR.editor_mail_sender",	"mcradmin@localhost");
		GUEST_ID = config.getString("MCR.users_guestuser_username","gast");
		editWorkflowDirectories = new HashMap();
		Properties props = config.getProperties("MCR.WorkflowEngine.EditDirectory.");
		for(Enumeration e = props.keys(); e.hasMoreElements();){
			String propKey = (String)e.nextElement();
			String hashKey = propKey.substring("MCR.WorkflowEngine.EditDirectory.".length());
			editWorkflowDirectories.put(hashKey,props.getProperty(propKey));
		}
	}
    	
	private Hashtable mt = null;
	private XMLOutputter out = new XMLOutputter(org.jdom.output.Format.getPrettyFormat());	
	
	/**
	 * Returns the workflow manager singleton.
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static synchronized MCRWorkflowEngineManagerInterface instance() throws Exception {
		if (singleton == null)
			singleton = new MCRWorkflowEngineManagerBaseImpl();
		return singleton;
	}	

	/**
	 * The constructor of this class.
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	protected MCRWorkflowEngineManagerBaseImpl() throws Exception {
		mt = new Hashtable();		
	}	
	

	public List getCurrentProcessIDs(String userid) {
		return MCRJbpmWorkflowBase.getCurrentProcessIDs(userid);
	}
	
	public List getCurrentProcessIDs(String userid, String workflowProcessType) {
		return MCRJbpmWorkflowBase.getCurrentProcessIDs(userid, workflowProcessType);
	}	
	
	public String getWorkflowDirectory(String documentType){
		return (String)editWorkflowDirectories.get(documentType);
	}
	
	public String getStatus(String userid){
		long processID = getUniqueCurrentProcessID(userid);
		if(processID > 0)
			return getStatus(processID);
		else
			return "";
	}
	
	public String getStatus(long processID) {
		return MCRJbpmWorkflowBase.getWorkflowStatus(processID);
	} 	
	
	/**
	 * The method return the mail sender adress form the configuration.
	 * @return the mail sender adress
	 */
	protected String getMailSender() {
		return sender;
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
	protected List getMailAddress(String type, String todo) {
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
	
	
	protected boolean isUserValid(String userid){
		boolean isValid= !GUEST_ID.equals(userid);				
		try {
			MCRUser user = MCRUserMgr.instance().retrieveUser(userid);
			isValid &= user.isEnabled();
			isValid &= user.isValid();
		} catch (Exception noUser) {
			//TODO Fehlermeldung
			logger.warn("user dos'nt exist userid=" + userid);
			isValid &= false;			
		}
		return isValid;
	}
	
	protected String createUrnReservationForAuthor(String authorid, String comment, String workflowProcessType){
		String nissprefix = config.getString("MCR.nbn.nissprefix." + workflowProcessType, "diss");
		//MCRNBN mcrurn = new MCRNBN(authorid, comment);
		MCRNBN mcrurn = new MCRNBN(authorid, comment, nissprefix);  
		return mcrurn.getURN();
	}


	protected Element  buildQueryforAuthor(String userid) {
    	
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
	
	protected String createAuthor(String userid, String workflowProcessType){
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
		ePnames.setAttribute("textsearch","true");		
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
		setDefaultACL(author.getId(), workflowProcessType, user.getID());
   	    return author.getId().getId();
	}
	
	/**
	 * returns the next free document id for a given document tpye
	 * ! if documents are saved in another workflow engines than this one
	 * ! you can't use this function to create the next free id
	 * @param objtype
	 *      String of documentType for which a ID is required
	 * @return
	 */
	public String getNextFreeID(String objtype) {
		String defaproject 	= MCRConfiguration.instance().getString("MCR.default_project_id",	"MCR");
		String myproject 	= MCRConfiguration.instance().getString("MCR." + objtype + "_project_id", "MCR");
		if (myproject.equals("MCR")) {
			myproject = defaproject;
		}
		myproject = myproject + "_" + objtype;		
		String workingDirectoryPath = getWorkflowDirectory(objtype);
		
		MCRObjectID IDMax = new MCRObjectID();
		IDMax.setNextFreeId(myproject);
		
		File workingDirectory = new File(workingDirectoryPath);
		if (workingDirectory.isDirectory()) {
			String[] list = workingDirectory.list();
			for (int i = 0; i < list.length; i++) {
				try {
					MCRObjectID IDinWF = new MCRObjectID(list[i].substring(0, list[i].length() - 4));
					if (IDMax.getNumberAsInteger() <= IDinWF.getNumberAsInteger()) {
						IDinWF.setNumber(IDinWF.getNumberAsInteger() + 1);
						IDMax = IDinWF;
					}
				} catch (Exception e) {
					;   //other files can be ignored
				}
			}
		}		
		logger.debug("New ID is" + IDMax.getId());
		return IDMax.getId();
	}	
	
	
	/*
	 *    DUMMY IMPLEMENTATION, MUST BE EXTENDED BY REAL WORKFLOW-IMPLEMENTATIONS
	 *    IF NEEDED THERE
	 */
	
	public static void setDefaultACL(MCRObjectID objID, String workflowProcessType, String userID){
		String[] permissions = {"read","commitdb","writedb","deletedb","deletewf"};
		for (int i = 0; i < permissions.length; i++) {
			String propName = new StringBuffer("MCR.WorkflowEngine.defaultACL.")
				.append(objID.getTypeId()).append(".").append(permissions[i]).append(".")
				.append(workflowProcessType).toString();
			String strRule = config.getString(propName,"<condition format=\"xml\"><boolean operator=\"false\" /></condition>");
			strRule = strRule.replaceAll("\\$\\{user\\}",userID);
			Element rule = (Element)MCRXMLHelper.parseXML(strRule).getRootElement().detach();
			AI.addRule(objID.getId(), permissions[i], rule, "");
		}
	}	

	public String getAuthorFromUniqueWorkflow(String userid){
		return "";
	}
	
	public String getURNReservation(String userid){
		return "";					
	}
		
	public String getMetadataDocumentID(String userid){
		return "";
	}	
	
	public long getUniqueCurrentProcessID(String userid) {
		return 0;
	}

	public void initWorkflowProcess(String initiator) throws MCRException {
		logger.warn("no workflow process is initialized, must be implemented in subclass, if needed");
	}
	
}
