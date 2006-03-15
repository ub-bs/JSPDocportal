package org.mycore.frontend.workflowengine.jbpm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.jdom.output.XMLOutputter;
import org.mycore.access.MCRAccessInterface;
import org.mycore.access.MCRAccessManager;
import org.mycore.common.JSPUtils;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRDefaults;
import org.mycore.common.MCRException;
import org.mycore.common.MCRSessionMgr;
import org.mycore.common.MCRUtils;
import org.mycore.common.xml.MCRURIResolver;
import org.mycore.common.xml.MCRXMLHelper;
import org.mycore.datamodel.metadata.MCRDerivate;
import org.mycore.datamodel.metadata.MCRMetaAddress;
import org.mycore.datamodel.metadata.MCRMetaBoolean;
import org.mycore.datamodel.metadata.MCRMetaIFS;
import org.mycore.datamodel.metadata.MCRMetaLangText;
import org.mycore.datamodel.metadata.MCRMetaLinkID;
import org.mycore.datamodel.metadata.MCRMetaPersonName;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.datamodel.metadata.MCRObjectService;
import org.mycore.frontend.cli.MCRDerivateCommands;
import org.mycore.frontend.cli.MCRObjectCommands;
import org.mycore.frontend.jsp.format.MCRResultFormatter;
import org.mycore.frontend.workflow.MCREditorOutValidator;
import org.mycore.services.nbn.MCRNBN;
import org.mycore.user2.MCRUser;
import org.mycore.user2.MCRUserMgr;

public class MCRWorkflowEngineManagerBaseImpl implements MCRWorkflowEngineManagerInterface{
	
	private static Logger logger = Logger.getLogger(MCRWorkflowEngineManagerBaseImpl.class.getName());
	private static final String[] defaultPermissionTypes ;
	private static MCRWorkflowEngineManagerInterface singleton;
	protected static MCRConfiguration config = MCRConfiguration.instance();
	protected static MCRAccessInterface AI = MCRAccessManager.getAccessImpl();
	protected static HashMap editWorkflowDirectories ;
	protected static String deleteDir; 
	
	private static String sender ;
	private static String GUEST_ID ;
	
	// pattern for the stringpart after the last [/\]
	protected static Pattern filenamePattern = Pattern.compile("([^\\\\/]+)\\z");
	//	 pattern for the file extension
	protected static Pattern fileextensionPattern = Pattern.compile(".([^\\\\/.]+)\\z");
	
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
		defaultPermissionTypes = config.getString("MCR.WorkflowEngine.DefaultPermissionTypes", "read,commitdb,writedb,deletedb,deletewf").split(",");
		deleteDir = config.getString("MCR.WorkflowEngine.DeleteDirectory");
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
	
	public List  getCurrentProcessIDsForProcessType(String processType) {
		return MCRJbpmWorkflowBase.getCurrentProcessIDsForProcessType(processType);
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
		setDefaultPermissions(author.getId(), workflowProcessType, user.getID());
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
	    String base = MCRConfiguration.instance().getString("MCR.default_project_id","DocPortal")+ "_" + objtype; 	    
		String workingDirectoryPath = getWorkflowDirectory(objtype);
		
		MCRObjectID IDMax = new MCRObjectID();
		IDMax.setNextFreeId(base);
				
		File workingDirectory = new File(workingDirectoryPath);
		if (workingDirectory.isDirectory()) {
			String[] list = workingDirectory.list();
			for (int i = 0; i < list.length; i++) {
				try {
					MCRObjectID IDinWF = new MCRObjectID(list[i].substring(0, list[i].length() - 4));
					if (IDinWF.getTypeId().equals(objtype) && IDMax.getNumberAsInteger() <= IDinWF.getNumberAsInteger()) {
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
	
	public void setMetadataValidFlag(String mcrid, boolean isValid) {
		long pid = getUniqueWorkflowProcessFromCreatedDocID(mcrid);
		if(pid > 0) {
			MCRJbpmWorkflowObject wfo = new MCRJbpmWorkflowObject(pid);
			wfo.setStringVariableValue("valid-" + mcrid, Boolean.toString(isValid));
		}
	}	
	
	public boolean checkMetadataValidFlag(String mcrid) {
		long pid = getUniqueWorkflowProcessFromCreatedDocID(mcrid);
		if(pid > 0) {
			MCRJbpmWorkflowObject wfo = new MCRJbpmWorkflowObject(pid);
			try{
				if(wfo.getStringVariableValue("valid-" + mcrid).equals("true"))
					return true;
				else
					return false;
			}catch(Exception e){
				logger.error("boolean parsing of " + mcrid + " was not possible", e);
				return false;
			}
		}else{
			return false;
		}
	}	

	protected boolean backupDerivateObject(String documentType, String metadataObject, String derivateObject, long pid) {
		try{
			String derivateDirectory = getWorkflowDirectory(documentType) + File.separator + derivateObject;
			String derivateFileName = derivateDirectory + ".xml" ;
			
			File inputDir = new File(derivateDirectory);
			File inputFile = new File(derivateFileName);
			
			SimpleDateFormat fmt = new SimpleDateFormat();
		    fmt.applyPattern( "yyyyMMddhhmmss" );
		    GregorianCalendar cal = new GregorianCalendar();
			File outputDir = File.createTempFile("deleted_at_" + fmt.format(cal.getTime()),"", new File(deleteDir));
			JSPUtils.recursiveCopy(inputDir, outputDir);
			MCRUtils.copyStream(new FileInputStream(inputFile), new FileOutputStream(new File(outputDir.getAbsolutePath() + File.separator + inputFile.getName())));
		}catch(Exception ex){
			logger.error("problems in copying", ex);
			return false;
		}
		return true;		
	}		
	
	private long getUniqueWorkflowProcessFromCreatedDocID(String mcrid){
		List lpids = MCRJbpmWorkflowBase.getCurrentProcessIDsForProcessVariable("createdDocID%", mcrid);
		if(lpids == null || lpids.size() == 0){
			logger.error("there could not be found a process with this createdDocID " + mcrid);
			return 0;
		}else if (lpids.size() > 1){
			StringBuffer sb = new StringBuffer("there could be found more than one processids for createdDocID ")
				.append(mcrid).append("\n delete one of the following processes ");
			for (Iterator it = lpids.iterator(); it.hasNext();) {
				sb.append("[").append(String.valueOf(((Long) it.next()).longValue())).append("]");
			}
			logger.error(sb.toString());
			return -1;
		}else{
			return ((Long)lpids.get(0)).longValue();
		}		
	}
	
	public  Document getListWorkflowProcess(String userid, String workflowProcessType, String  documentType ){
		StringBuffer sb = null;
		
		List lpids = getCurrentProcessIDsForProcessType( workflowProcessType);  //userid, workflowProcessType);		
		
		String lang = MCRSessionMgr.getCurrentSession().getCurrentLanguage();
        Element mcr_result = new Element("mcr_result");
		
		org.jdom.Element root = new org.jdom.Element("mcr_workflow");
		root.addNamespaceDeclaration(org.jdom.Namespace.getNamespace("xsi",	MCRDefaults.XSI_URL));
		root.setAttribute("type", documentType);
		root.setAttribute("step", "editor");
			
		MCRResultFormatter formatter = (MCRResultFormatter) MCRConfiguration.instance().getSingleInstanceOf("MCR.ResultFormatter_class_name","org.mycore.frontend.jsp.format.MCRResultFormatter");
		String resultlistResource = new StringBuffer("resource:resultlist-").append(documentType).append(".xml").toString();
		Element resultlistElement = MCRURIResolver.instance().resolve(resultlistResource);
		
		ArrayList derivateDataArray = getAllDerivateDataFromWorkflow(documentType);
		
		String dirname = getWorkflowDirectory(documentType);
		
		for (Iterator iter = lpids.iterator(); iter.hasNext();) {
			Long  pid  = (Long) iter.next();
			MCRJbpmWorkflowObject wfo = new MCRJbpmWorkflowObject(pid.longValue());
			String docID = wfo.getStringVariableValue("createdDocID");
			
			if ( ! AI.checkPermission(docID, "writedb") )
				continue;
			
		    try {
			    String wfile = docID + ".xml";
			    sb = (new StringBuffer(dirname)).append(File.separator).append(	wfile);
			    Document workflow_in = MCRXMLHelper.parseURI(sb.toString(), false);
			    logger.debug("Workflow file "+wfile+" was readed.");
			    
			    Element containerHit = new Element ("all-metavalues");
	        	mcr_result.setAttribute("filename", sb.toString().replaceAll("\\\\","/"));
	        	mcr_result.setAttribute("processid", pid.toString()); 
	        	mcr_result.setAttribute("ID", docID);
	        	
		        try {
			        containerHit = formatter.processDocDetails(workflow_in,resultlistElement,lang,"", documentType);
		        } catch (Exception formattingEx ){
		        	//ignore this, maybee the document is not valid
		        	; 
		        } finally {
		        	mcr_result.addContent(containerHit);
		        }
		        root.addContent(mcr_result);
		        
			} catch (Exception ex) {
			    logger.warn("Can't parse workflow file for Document " + docID);
				continue;
			}	
				
			for (int j = 0; j < derivateDataArray.size(); j++) {
			  try {				   			        
					Element derivate =  (Element) derivateDataArray.get(j);
					if ( docID.equalsIgnoreCase(derivate.getAttributeValue("href"))) {
						String derivatePath = derivate.getAttributeValue("ID");
						File dir = new File(dirname, derivatePath);
						logger.debug("Derivate under " + dir.getName());
						
						if (dir.isDirectory()) {
							ArrayList dirlist = MCRUtils.getAllFileNames(dir);
							for (int k = 0; k < dirlist.size(); k++) {
								org.jdom.Element file = new org.jdom.Element("file");
								file.setText(derivatePath + "/" + derivatePath + "/" + (String) dirlist.get(k));
								File thisfile = new File(dir, (String) dirlist.get(k));
								file.setAttribute("size", String.valueOf(thisfile.length()));
								file.setAttribute("main", "false");
								if (derivate.getAttributeValue("maindoc").equals((String) dirlist.get(k))) {
									file.setAttribute("main", "true");
								}
								derivate.addContent(file);
								derivateDataArray.remove(j);
								j--;
							}
							mcr_result.addContent(derivate);
						}
					}
				  } catch (Exception ex) {
						logger.error("Error while read derivates for XML workflow file " + docID, ex);
				  }
		    }
		}		
		return new org.jdom.Document(root);
	}
	
	protected final ArrayList getAllDerivateDataFromWorkflow(String documentType) {
			String dirname = getWorkflowDirectory(documentType);
			ArrayList workfiles = new ArrayList();
			if (!dirname.equals(".")) {
				File dir = new File(dirname);
				String[] dirl = null;
				if (dir.isDirectory()) {
					dirl = dir.list();
				}
				if (dirl != null) {
					for (int i = 0; i < dirl.length; i++) {
						if ((dirl[i].indexOf("_derivate_") != -1) && (dirl[i].endsWith(".xml"))) {
							Element derivateData = getDerivateMetaData(dirname + File.separator + dirl[i]);
							workfiles.add(derivateData);						
						}
					}
				}
			}
			return workfiles;
	}
	
	protected final Element getDerivateMetaData( String filename){
		Element derivateData = new Element("derivate");
		Element derivate = MCRXMLHelper.parseURI(filename, false).getRootElement();		
		derivateData.setAttribute("label", derivate.getAttributeValue("label") );
		derivateData.setAttribute("ID", derivate.getAttributeValue("ID") );
		
		Iterator it = derivate.getDescendants(new ElementFilter("linkmeta"));
		if ( it.hasNext() ) {
	      Element el = (Element) it.next();
	      String href = el.getAttributeValue("href",org.jdom.Namespace.getNamespace("xlink",MCRDefaults.XLINK_URL));
	      if ( href==null)  	href = "";      
          derivateData.setAttribute("href", href);
	    } 
		
		it = derivate.getDescendants(new ElementFilter("internal"));		
	    if ( it.hasNext() )	    {
	      Element el = (Element) it.next();
	      String maindoc = el.getAttributeValue("maindoc");
	      if ( maindoc==null)  	maindoc = "####";
	      derivateData.setAttribute("maindoc", maindoc );          
	    }
	    return derivateData;		
	}
	
	
	public void setDummyPermissions(String objid){
		for (int i = 0; i < defaultPermissionTypes.length; i++) {
			AI.addRule(objid, defaultPermissionTypes[i], MCRAccessManager.getTrueRule(), "");	
		}		
	}

	/**
	 * The method create a new MCRDerivate and store them to the directory of
	 * the workflow that correspons with the type of the given object
	 * MCRObjectID with the name of itseslf. Also it creates a new directory with
	 * the same new name. This new derivate ID was returned.
	 * 
	 * @param objmcrid        the MCRObjectID of the related object
	 * @return the MCRObjectID of the derivate
	 */
	public String addNewDerivateToWorkflowObject(String objmcrid, String documentType, String userid){
		String lang = MCRSessionMgr.getCurrentSession().getCurrentLanguage();
	    String base = MCRConfiguration.instance().getString("MCR.default_project_id","DocPortal")+ "_derivate"; 	    		
		MCRObjectID IDMax = new MCRObjectID();
		IDMax.setNextFreeId(base);
		
		String dirname = getWorkflowDirectory(documentType);
		ArrayList DerivateList = getAllDerivateDataFromWorkflow( documentType);		
		for (int i = 0  ; i < DerivateList.size(); i++) {
			Element derivate = (Element) DerivateList.get(i);
			try { 
				
				MCRObjectID IDinWF = new MCRObjectID(derivate.getAttributeValue("ID"));
				if (IDMax.getNumberAsInteger() <= IDinWF.getNumberAsInteger()) {
					IDinWF.setNumber(IDinWF.getNumberAsInteger() + 1);
					IDMax = IDinWF;
				}				
			} catch (Exception ignored){ ; } 
		}
		
		logger.debug("New derivate ID " + IDMax.getId());

		// create a new directory
		File dir = new File(dirname + File.separator + IDMax.getId());
		dir.mkdir();
		logger.debug("Directory " + dir.getAbsolutePath() + " created.");

		// build the derivate XML file
		MCRDerivate der = new MCRDerivate();
		der.setId(IDMax);
		der.setLabel("Dataobject from " + IDMax.getId());
		der.setSchema("datamodel-derivate.xsd");
		MCRMetaLinkID link = new MCRMetaLinkID("linkmetas", "linkmeta", lang , 0);
		link.setReference(objmcrid, "", "");
		der.getDerivate().setLinkMeta(link);
		MCRMetaIFS internal = new MCRMetaIFS("internals", "internal", lang , IDMax.getId());
		internal.setMainDoc("#####");
		der.getDerivate().setInternals(internal);
		
		JSPUtils.saveDirect( der.createXML(), dir.getAbsolutePath() + ".xml");
		logger.info("Derivate " + IDMax.getId() + " stored under " + dir.getAbsolutePath() + ".xml");
		setDefaultPermissions(IDMax.getId(), userid);
		return IDMax.getId();		
	}

	public boolean commitWorkflowObject(String objmcrid, String documentType) {
		boolean bSuccess = true;
		String dirname = getWorkflowDirectory(documentType);
		String filename = dirname + File.separator + objmcrid + ".xml";

		try { 
			if (MCRObject.existInDatastore(objmcrid)) {
				MCRObject mcr_obj = new MCRObject();
				mcr_obj.deleteFromDatastore(objmcrid);
			}
			MCRObjectCommands.loadFromFile(filename);
			logger.info("The metadata object: " + filename + " is loaded.");
		} catch (Exception ig){ 
			logger.error("Can't load File catched error: ", ig);
			bSuccess=false;
		}
		if ( (bSuccess = MCRObject.existInDatastore(objmcrid))  ) {
			ArrayList DerivateList = getAllDerivateDataFromWorkflow( documentType);		
			for (int i = 0  ; i < DerivateList.size(); i++) {
				Element derivate = (Element) DerivateList.get(i);
				try { 				
					String href = derivate.getAttributeValue("href");
					String derivateID = derivate.getAttributeValue("ID");
					if ( objmcrid.equalsIgnoreCase(href)) {
						bSuccess = commitDerivateObject(derivateID, documentType);
					}
				} catch (Exception ig){ 
					logger.error("Can't load File catched error: ", ig);
					bSuccess=false;
				}				
			}
		}
		return bSuccess;
	}
	
	
	public boolean commitDerivateObject(String derivateid, String documentType) {
		String dirname = getWorkflowDirectory(documentType);
		String filename = dirname + File.separator + derivateid + ".xml";
		return loadDerivate(derivateid, filename);
	}

	private boolean loadDerivate(String derivateid, String filename) {
		if (MCRDerivate.existInDatastore(derivateid)) {
			MCRDerivateCommands.updateFromFile(filename);
		} else {
			MCRDerivateCommands.loadFromFile(filename);
		}
		if (!MCRDerivate.existInDatastore(derivateid)) {
			return false;
		}
		logger.debug("Commit the derivate " + filename);
		return true;
	}
	
	public final boolean deleteWorkflowObject(String objmcrid, String documentType) {
		boolean bSuccess = true;
		String dirname = getWorkflowDirectory(documentType);
		String filename = dirname + File.separator + objmcrid + ".xml";

		try {
			File fi = new File(filename);
			if (fi.isFile() && fi.canWrite()) {				
				fi.delete();
				logger.debug("File " + filename + " removed.");
			} else {
				logger.error("Can't remove file " + filename);
				bSuccess = false;
			}
		} catch (Exception ex) {
			logger.error("Can't remove file " + filename);
				bSuccess = false;
		}
		
		if ( bSuccess ) {
			ArrayList DerivateList = getAllDerivateDataFromWorkflow( documentType);		
			for (int i = 0  ; i < DerivateList.size(); i++) {
				Element derivate = (Element) DerivateList.get(i);
				try { 				
					String href = derivate.getAttributeValue("href");
					String derivateID = derivate.getAttributeValue("ID");
					if ( objmcrid.equalsIgnoreCase(href)) {
						bSuccess = deleteDerivateObject(documentType, objmcrid, derivateID );
					}
				} catch (Exception ig){ 
					logger.error("Can't load File catched error: ", ig);
					bSuccess=false;
				}				
			}
		} 
		return bSuccess;
	 }
	
	public static void setDefaultPermissions(MCRObjectID objID, String workflowProcessType, String userID){
		
		for (int i = 0; i < defaultPermissionTypes.length; i++) {
			String propName = new StringBuffer("MCR.WorkflowEngine.defaultACL.")
				.append(objID.getTypeId()).append(".").append(defaultPermissionTypes[i]).append(".")
				.append(workflowProcessType).toString();
			String strRule = config.getString(propName,"<condition format=\"xml\"><boolean operator=\"false\" /></condition>");
			strRule = strRule.replaceAll("\\$\\{user\\}",userID);
			Element rule = (Element)MCRXMLHelper.parseXML(strRule).getRootElement().detach();
			AI.addRule(objID.getId(), defaultPermissionTypes[i], rule, "");
		}
	}	

	/*
	 *    DUMMY IMPLEMENTATION, MUST BE EXTENDED BY REAL WORKFLOW-IMPLEMENTATIONS
	 *    IF NEEDED THERE
	 */
	
	public void setDefaultPermissions(String mcrid, String userid){
		setDummyPermissions(mcrid);
	}

	public boolean deleteDerivateObject(String documentType, String metadataObject, String derivateObject) {
		try{
			String derivateDirectory = getWorkflowDirectory(documentType) + File.separator + derivateObject;
			String derivateFileName = derivateDirectory + ".xml" ;
			
			File derDir = new File(derivateDirectory);
			File derFile = new File(derivateFileName);
			
			
			if(derDir.isDirectory()) {
				logger.debug("deleting " + derDir.getName());
				JSPUtils.recursiveDelete(derDir);
			}else{
				logger.warn(derDir.getName() + " is not a directory, did not delete it");
				return false;
			}
			if(derFile.isFile()){
				logger.debug("deleting " + derFile.getName());
				derFile.delete();
			}else{
				logger.warn(derFile.getName() + " is not a file, did not delete it");
				return false;
			}
		}catch(Exception ex){
			logger.error("problems in deleting", ex);
			return false;
		}
		return true;
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
	
	public Document getListWorkflowProcess(String userid, String workflowProcessType) {
		logger.warn("no document type is initialized, must be implemented in subclass, if needed");
		return null;
	}
	
	public long initWorkflowProcess(String initiator) throws MCRException {
		logger.warn("no workflow process is initialized, must be implemented in subclass, if needed");
		return -1;
	}
	
	public String getCurrentWorkflowMessage(String role, long  pid){
		String status = MCRJbpmWorkflowBase.getWorkflowStatus(pid);
		// getMessage from Status and role (author|editor)
		String message = status;
		
		return message;
	}	
	
	public void setCommitStatus( String mcrid, String lastAction){
		// leer	
	}

	public void setDerivateVariables(long pid) {
		logger.debug("enters setDerivateVariables (dummy), must be implemented in subclasses, if needes");
	}
	
	public void saveFiles(List files, String dirname, long pid) throws MCRException {
		logger.debug("enters saveFiles (dummy), must be implemented in subclasses, for workflow-specific file checks");
		// save the files
	
		ArrayList ffname = new ArrayList();
		String mainfile = "";
		for (int i = 0; i < files.size(); i++) {
			FileItem item = (FileItem) (files.get(i));
			String fname = item.getName().trim();
			Matcher mat = filenamePattern.matcher(fname);
			while(mat.find()){
				fname = mat.group(1);
			}
			fname.replace(' ', '_');
			ffname.add(fname);
			try{
				File fout = new File(dirname, fname);
				FileOutputStream fouts = new FileOutputStream(fout);
				MCRUtils.copyStream(item.getInputStream(), fouts);
				fouts.close();
				logger.info("Data object stored under " + fout.getName());
			}catch(Exception ex){
				String errMsg = "could not sotre data object " + fname;
				logger.error(errMsg, ex);
				throw new MCRException(errMsg);
			}
		}
		if ((mainfile.length() == 0) && (ffname.size() > 0)) {
			mainfile = (String) ffname.get(0);
		}
	
		// add the mainfile entry
		MCRDerivate der = new MCRDerivate();
		try {
			der.setFromURI(dirname + ".xml");
			if (der.getDerivate().getInternals().getMainDoc().equals("#####")) {
				der.getDerivate().getInternals().setMainDoc(mainfile);
				byte[] outxml = MCRUtils.getByteArray(der.createXML());
				try {
					FileOutputStream out = new FileOutputStream(dirname
							+ ".xml");
					out.write(outxml);
					out.flush();
				} catch (IOException ex) {
					logger.error(ex.getMessage());
					logger.error("Exception while store to file " + dirname
							+ ".xml", ex);
					throw ex;
				}
			}
		} catch (Exception e) {
			String msgErr = "Can't open file " + dirname + ".xml"; 
			logger.error(msgErr, e);
			throw new MCRException(msgErr);
		}
	}
	
}
