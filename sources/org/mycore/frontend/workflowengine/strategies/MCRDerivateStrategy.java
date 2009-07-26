package org.mycore.frontend.workflowengine.strategies;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jbpm.context.exe.ContextInstance;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.mycore.backend.hibernate.MCRHIBConnection;
import org.mycore.common.JSPUtils;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRDerivateFileFilter;
import org.mycore.common.MCRException;
import org.mycore.common.MCRPersistenceException;
import org.mycore.common.MCRSession;
import org.mycore.common.MCRSessionMgr;
import org.mycore.common.MCRUtils;
import org.mycore.common.xml.MCRXMLHelper;
import org.mycore.datamodel.ifs.MCRDirectory;
import org.mycore.datamodel.ifs.MCRFilesystemNode;
import org.mycore.datamodel.metadata.MCRDerivate;
import org.mycore.datamodel.metadata.MCRMetaIFS;
import org.mycore.datamodel.metadata.MCRMetaLinkID;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.cli.MCRDerivateCommands;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowUtils;

public abstract class MCRDerivateStrategy {
	private static Logger logger = Logger.getLogger(MCRDefaultDerivateStrategy.class.getName());
	private static String XLINK_URL = "http://www.w3.org/1999/xlink";
	
	protected static String SEPARATOR = "/";
	private static MCRObjectID nextWorkflowDerivateID = null;
	
	// pattern for the stringpart after the last [/\]
	protected static Pattern filenamePattern = Pattern.compile("([^\\\\/]+)\\z");
	//	 pattern for the file extension
	protected static Pattern fileextensionPattern = Pattern.compile(".([^\\\\/.]+)\\z");	

	/**
	 * deletes a derivate from the workflow 
	 * @param documentType
	 * 			String like "disshab" or "document"
	 * @param metadataObjectId
	 * 			String of the objID the derivate belongs to
	 * @param derivateObjectId
	 * 			String of the derID, that will bedeleted
	 * @return
	 */
	public abstract boolean deleteDerivateObject(ContextInstance ctxI, String derivateDirectory, String backupDirectory, String metadataObjectId, String derivateObjectId, boolean mustWorkflowVarBeUpdated);
	
	
	/**
	 * deletes a file from a derivate from the workflow 
	 * @param documentType
	 * 			String like "disshab" or "document"
	 * @param metadataObjectId
	 * 			String of the objID the derivate belongs to
	 * @param derivateObjectId
	 * 			String of the derID, that will bedeleted
	 * @param filename
	 * 			String the filename with its derivatepath
	 * @return
	 */
	public abstract boolean deleteDerivateFile(ContextInstance ctxI, String derivateDirectory, String backupDirectory, String metadataObjectId, String derivateObjectId, boolean mustWorkflowVarBeUpdated, String filename);
	
	/**
	 * adds a new derivate to an workflow object
	 * @param metadataObjectId
	 * @param derivateDirectory
	 * @return
	 * TODO check, why is here a userid required???
	 */
	public String addNewDerivateToWorkflowObject(String derivateDirectory, String metadataObjectId){
		String lang = MCRSessionMgr.getCurrentSession().getCurrentLanguage();
		
		MCRObjectID IDMax = setNextFreeDerivateID();
		
		logger.debug("New derivate ID " + IDMax.getId());

		// create a new directory
		File dir = new File(derivateDirectory + SEPARATOR + IDMax.getId());
		dir.mkdir();
		logger.debug("Directory " + dir.getAbsolutePath() + " created.");

		// build the derivate XML file
		MCRDerivate der = new MCRDerivate();
		der.setId(IDMax);
		der.setLabel(MCRConfiguration.instance().getString("MCR.Derivates.Labels.default", "Dataobject from " + IDMax.getId()));
		der.setSchema("datamodel-derivate.xsd");
		MCRMetaLinkID link = new MCRMetaLinkID("linkmetas", "linkmeta", lang , 0);
		link.setReference(metadataObjectId, "", "");
		der.getDerivate().setLinkMeta(link);
		MCRMetaIFS internal = new MCRMetaIFS("internals", "internal", lang , IDMax.getId());
		internal.setMainDoc("#####");
		der.getDerivate().setInternals(internal);
		
		JSPUtils.saveDirect( der.createXML(), dir.getAbsolutePath() + ".xml");
		logger.info("Derivate " + IDMax.getId() + " stored under " + dir.getAbsolutePath() + ".xml");
		return IDMax.getId();
	}

	final public synchronized MCRObjectID setNextFreeDerivateID(){
		int maxwf=0;
		if(nextWorkflowDerivateID == null){
			List<String> allDerivateFileNames = new ArrayList<String>();
			HashMap directoryMap = MCRWorkflowDirectoryManager.getEditWorkflowDirectories();
			for (Iterator it = directoryMap.keySet().iterator(); it.hasNext();) {
				File workDir = new File((String)directoryMap.get(it.next()));
				if(workDir.isDirectory()){
					Iterator it2 = Arrays.asList(workDir.listFiles(new MCRDerivateFileFilter())).iterator(); 
					while(it2.hasNext()){
						File ff = (File) it2.next();
						if ( (ff.isFile())) {
								allDerivateFileNames.add(ff.getName());
						}
					}
				}
			}
			String base = MCRConfiguration.instance().getString("MCR.SWF.Project.ID","DocPortal")+ "_derivate";
			MCRObjectID dbIDMax = new MCRObjectID();
			dbIDMax.setNextFreeId(base);
			if(allDerivateFileNames.size() == 0){
				maxwf=0;
			}else{			
				Collections.sort(allDerivateFileNames, Collections.reverseOrder());
				String maxFilename = (String)allDerivateFileNames.get(0); 
				MCRObjectID IDinWF = new MCRObjectID(maxFilename.substring(0, maxFilename.length() - 4));
				maxwf = IDinWF.getNumberAsInteger();				
			}
			nextWorkflowDerivateID.setNextFreeId(base, maxwf);
		}
		
		
		MCRObjectID retID = new MCRObjectID(nextWorkflowDerivateID.toString());
		nextWorkflowDerivateID.setNumber(retID.getNumberAsInteger() + 1);
		return retID;
	}	
	
	/**
	 * saves a list of files in a workflow directory, 
	 * 		when the requirements of the specific workflow-type 
	 * 		cannot be fulfilled, an exception is thrown
	 * @param files
	 * @param dirname
	 * @param wfp
	 * @throws MCRException
	 * TODO a better javadoc
	 */	
	public abstract void saveFiles(List files, String dirname, ContextInstance ctxI, String newLabel) throws MCRException ;
	
	public Element getDerivateData(String derivateDirectory, String docID, String derivateID) {
		String fileName = new StringBuffer(derivateDirectory)
			.append(SEPARATOR).append(derivateID)
			.append(".xml").toString();
		Element derivate = getDerivateMetaData(fileName);
		if ( docID.equalsIgnoreCase(derivate.getAttributeValue("href"))) {
			// this is our convention
			String derivatePath = derivate.getAttributeValue("ID");
			File dir = new File(derivateDirectory, derivatePath);
			logger.debug("Derivate under " + dir.getName());
			if (dir.isDirectory()) {
				ArrayList dirlist = MCRUtils.getAllFileNames(dir);
				for (int k = 0; k < dirlist.size(); k++) {
					org.jdom.Element file = new org.jdom.Element("file");
					file.setText(derivatePath + "/" + (String) dirlist.get(k));
					File thisfile = new File(dir, (String) dirlist.get(k));
					file.setAttribute("size", String.valueOf(thisfile.length()));
					file.setAttribute("main", "false");
					if (derivate.getAttributeValue("maindoc").equals((String) dirlist.get(k))) {
						file.setAttribute("main", "true");
					}
					derivate.addContent(file);
				}
			}
		}
		return derivate;
	}

		/**
		 * returns relevant information of certain derivate for a certain document as jdom Element
		 * @param docID
		 * @param derivateID
		 * @return
		 * 	the derivate as JDOM-Element
		 * <br>
		 * output format<br>
		 * &lt;derivate id="derivateID" label="Label of Derivate" &rt;
		 *    &lt;file type="maindoc" name="filename" path="fullpath without /filename" /&gt;
		 *    &lt;file type="standard" name="filename" path="fullpath without /filename" /&gt;
		 *    &lt;file type="standard" name="filename" path="fullpath without /filename" /&gt;
		 * &lt;/derivate&rt;
		 */
		protected final Element getDerivateMetaData( String filename){
			Element derivateData = new Element("derivate");
			try {
				Element derivate = MCRXMLHelper.parseURI(new File(filename).toURI(), false).getRootElement();		
				derivateData.setAttribute("label", derivate.getAttributeValue("label") );
				derivateData.setAttribute("ID", derivate.getAttributeValue("ID") );
				
				Iterator it = derivate.getDescendants(new ElementFilter("linkmeta"));
				if ( it.hasNext() ) {
			      Element el = (Element) it.next();
			      String href = el.getAttributeValue("href",org.jdom.Namespace.getNamespace("xlink",XLINK_URL));
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
			} catch ( Exception ignore) {	;	}
			
		    return derivateData;		
		}

		/**
		 * is removing all derivates of a special workflow process
		 * @param wfp
		 * @param directory
		 */
		public abstract boolean removeDerivates(ContextInstance ctxI, String saveDirectory, String backupDirectory);
		
		
		public abstract boolean moveDerivateObject(ContextInstance ctxI, String derivateObjectID, int direction);
		
		/**
		 * is publishing a derivate to the database
		 * @param derivateid
		 * @param directory
		 * @return
		 */		
		public boolean commitDerivateObject(String derivateid, String directory) {
			String filename = directory + SEPARATOR + derivateid + ".xml";
			return loadDerivate(derivateid, filename);
		}
		
		/**
		 * is publishing the deleting in the workflowprozess - makes the delete of the derivate in the database
		 * @param derivateid
		 * @return
		 */
		public boolean deleteDeletedDerivates(String derivateid) {	
			if(MCRDerivate.existInDatastore(derivateid)){
				MCRDerivateCommands.delete(derivateid);
			}
			return true;
		}

		/**
		 * is publishing the deleting in the workflowprozess - makes the delete of single files of a derivate in the database
		 * @param filename
		 * @return
		 */
		public boolean deleteDeletedDerivateFile(String fileName) {			
			logger.debug("Delete File from Derivate: "+fileName);
			int split = fileName.indexOf(SEPARATOR);
			String derID = fileName.substring(0, split);
			MCRDirectory root = MCRDirectory.getRootDirectory(derID);
			if(root!=null){
				MCRFilesystemNode myfile = root.getChildByPath(fileName.substring(split));
				if (myfile!=null){
					try{
						myfile.delete();
					}
					catch(MCRPersistenceException pe){
						logger.error("Could not delete file: "+myfile.getAbsolutePath(), pe);
					}
				}
			}
			return true;
		}
		
		
		
		protected boolean backupDerivateObject(String saveDirectory, String backupDir,
				String metadataObjectID, String derivateObjectID, long pid) {
			
			logger.debug("backup is uncommented");
			return true;
			/***
			try{
				String derivateDirectory = saveDirectory + SEPARATOR + derivateObjectID;
				String derivateFileName = derivateDirectory + ".xml" ;
				
				File inputDir = new File(derivateDirectory);
				File inputFile = new File(derivateFileName);
				
				if ( inputFile.exist() ){
					SimpleDateFormat fmt = new SimpleDateFormat();
				    fmt.applyPattern( "yyyyMMddhhmmss" );
				    GregorianCalendar cal = new GregorianCalendar();
				    File curBackupDir = null;
				    boolean dirCreated = false;
				    while(!dirCreated) {
				    	curBackupDir = new File(backupDir + "/" + "deleted_at_" + fmt.format(cal.getTime()));
				    	if(curBackupDir.mkdir()) dirCreated = true;
				    }
				    File outputDir = new File(curBackupDir.getAbsolutePath() + SEPARATOR + inputDir.getName());
					JSPUtils.recursiveCopy(inputDir, outputDir);
					FileInputStream fin = new FileInputStream(inputFile);
					FileOutputStream fout = new FileOutputStream(new File(curBackupDir.getAbsolutePath() + SEPARATOR + inputFile.getName()));
					MCRUtils.copyStream(fin, fout);
					
					fin.close();
					fout.flush();
					fout.close();
				}

			}catch(Exception ex){
				logger.error("problems in copying", ex);
				return false;
			}
			return true;
			**/		
		}		

		private boolean loadDerivate(String derivateid, String filename) {
	        Map ruleMap = null;
	        boolean result = false;
			if (MCRDerivate.existInDatastore(derivateid)) {
		        ruleMap = MCRWorkflowUtils.getAccessRulesMap(derivateid);
				result = MCRDerivateCommands.updateFromFile(filename);
			} else {
				result = MCRDerivateCommands.loadFromFile(filename);
			}
			
			if ( ruleMap != null ) 
				MCRWorkflowUtils.setAccessRulesMap(derivateid, ruleMap);
		
			logger.debug("Commit the derivate " + filename);

			return result;
		}	
		
		/**
		 * returns a list of all derivates of given directory
		 * @param directory
		 * @return
		 */		
		public List getAllDerivateDataFromWorkflow(String directory) {
			List<Element> workfiles = new ArrayList<Element>();
			if (!directory.equals(".")) {
				File dir = new File(directory);
				String[] dirl = null;
				if (dir.isDirectory()) {
					dirl = dir.list();
				}
				if (dirl != null) {
					for (int i = 0; i < dirl.length; i++) {
						if ((dirl[i].indexOf("_derivate_") != -1) && (dirl[i].endsWith(".xml"))) {
							Element derivateData = getDerivateMetaData(directory + SEPARATOR + dirl[i]);
							workfiles.add(derivateData);						
						}
					}
				}
			}
			return workfiles;
		}	

}
