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

package org.mycore.frontend.workflowengine.jbpm.registeruser;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.DOMOutputter;
import org.jdom.xpath.XPath;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRSession;
import org.mycore.common.MCRSessionMgr;
import org.mycore.common.MCRUtils;
import org.mycore.common.xml.MCRXMLHelper;
import org.mycore.frontend.editor.MCREditorSubmission;
import org.mycore.frontend.editor.MCRRequestParameters;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.servlets.MCRServletJob;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManager;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManagerFactory;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowProcess;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowProcessManager;
import org.mycore.frontend.workflowengine.strategies.MCRWorkflowDirectoryManager;
import org.mycore.user.MCRExternalUserLogin;
import org.mycore.user.MCRUserMgr;


/**
 * This class takes the input from registeruser 
 * output XML and store the XML in a file or if an error was occured start the
 * editor again.
 * 
 * @author Heiko Helmbrecht; Anja Schaar
 * @version $Revision$ $Date$
 */
public class MCRRegisterUserWorkflowServlet extends MCRServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(MCRRegisterUserWorkflowServlet.class);
	private static String XSI_URL = "http://www.w3.org/2001/XMLSchema-instance";
	private String pid;
	private String nextPath;
	private String newUserID;
	private String ID ;
	private String lang;
	private static MCRWorkflowManager WFM = MCRWorkflowManagerFactory.getImpl("registeruser");
	private static String classNameExtUserLogin = MCRConfiguration.instance().getString("MCR.Application.ExternalUserLogin.Class", "").trim();

	private String documentType ="user";
	private String workflowType="registeruser";
	
    /**
     * This method overrides doGetPost of MCRServlet. <br />
     */
    public void doGetPost(MCRServletJob job) throws Exception {
		MCRSession mcrSession = MCRSessionMgr.getCurrentSession();  
    	HttpServletRequest  request = job.getRequest();
    	HttpServletResponse response = job.getResponse();
       

    	// read the XML data
        MCREditorSubmission sub = (MCREditorSubmission) (request.getAttribute("MCREditorSubmission"));
        
       
        // read the parameter
        MCRRequestParameters parms;

        if (sub == null) {
            parms = new MCRRequestParameters(request);
        } else {
            parms = sub.getParameters();
        }
        
    	pid = parms.getParameter("processid");
		nextPath = parms.getParameter("nextPath");
		newUserID = parms.getParameter("newUserID");
		ID = parms.getParameter("userID");
		lang = mcrSession.getCurrentLanguage();
        String todo = parms.getParameter("todo");
		
   		if (  AI.checkPermission("administrate-user") &&  "WFModifyWorkflowUser".equals(todo) ) {
			// nochmals editieren
			/**
			  <mcr:includeEditor 
	          isNewEditorSource="false" processid="${pid}"
	          mcrid="${userID}" type="user" workflowType="registeruser"
	          step=""  target="MCRCheckUserRegistrationServlet" nextPath="~workflow-registeruser" 
	          editorPath="editor/workflow/editor-modifyuser.xml" />
	          ***/   
        	// befüllten Editor für das Object includieren
			// aus dem wfo die Daten für die ID ... holen 
			MCRWorkflowProcess wfp = MCRWorkflowProcessManager.getInstance().getWorkflowProcess(Long.parseLong(pid));
   			try{
			ID = wfp.getStringVariable("initiatorUserID");
			
        	request.setAttribute("isNewEditorSource","false");
        	request.setAttribute("mcrid","user_"+ID);
        	request.setAttribute("processid",pid);
        	request.setAttribute("type",documentType);
        	request.setAttribute("target","MCRRegisterUserWorkflowServlet");
        	request.setAttribute("workflowType",workflowType);
        	request.setAttribute("step","");
        	request.setAttribute("nextPath",nextPath);
        	request.setAttribute("editorPath","editor/workflow/editor-modifyuser.xml");
        	request.getRequestDispatcher("/nav?path=~editor-include").forward(request, response);
   			}
   			catch(Exception e){
   				logger.error("caught exception: ",e);
   			}
   			finally{
   				wfp.close();
   			}
        	return;
	        	
		} else {
			// im Editor/Administrator modus
			assignUserRegistration(sub);
	       	request.getRequestDispatcher("/nav?path=" + nextPath).forward(request, response);
	       	return;    				
		}
    }

    
    private void assignUserRegistration(MCREditorSubmission sub) throws Exception { 
        Element root = new Element("mycoreuser");        
 		root.setAttribute("noNamespaceSchemaLocation", "MCRUser.xsd", org.jdom.Namespace.getNamespace("xsi", XSI_URL));
        Element userElement = new Element("user");
		
        if ( newUserID != null ) {
			if(newUserID.equals("")){
				userElement=null;
			}else{
				// 	Neue ID verwenden, da erste ID schon existiert
				StringBuffer storePath = new StringBuffer(MCRWorkflowDirectoryManager.getWorkflowDirectory(this.documentType))
    				.append("/").append("user_")     	
    				.append(ID).append(".xml");
				userElement = (Element) setNewUserIDforUser(newUserID, ID, storePath.toString(), lang);
			}
		} else {
	        org.jdom.Document   indoc = sub.getXML();
            userElement = (Element) indoc.getRootElement().getChild("user").clone();
            /**
            // remove the n- Entry and put it into one descrition...
            // map the user.groups to the selected description entries
            List descriptions = userElement.getChildren("user.description");
            Element groups = new Element("user.groups");
            String descr = "";
            for(int i=0;  i < descriptions.size(); i++) {
            	String groupname = ((Element)descriptions.get(i)).getText();
            	if ( MCRUserMgr.instance().existGroup(groupname)) {
            		// gruppe gewählt
            		Element group = new Element("groups.groupID");
            		group.setText(groupname);
            		groups.addContent(group);
            	} else {
            		// beschreibungstext
            		descr += groupname + ", ";
            	}
            }
           	userElement.removeChildren("user.description");
           	Element eDescr = new Element ("user.description");
           	eDescr.setText(descr);
        	userElement.addContent(1, eDescr);

        	if ( groups.getChildren() != null) {
        		userElement.addContent(groups);
        	}
        	**/
		}
        
        if ( userElement != null ) {
			ID = userElement.getAttributeValue("ID");

			String sUnAcceptedType = checkUserType(ID );
        	if ( sUnAcceptedType  != null ) {
	        	nextPath = "~mycore-error&messageKey=WF.registerUser.ErrorKey&message="+sUnAcceptedType;	        
			} else {			
				//change default password "dummy" to password from properties file
				Element ePWD = userElement.getChild("user.password");
				if(ePWD.getText().equals("dummy")){
					String pwd = MCRConfiguration.instance().getString("MCR.Application.user_initialpasswd","dummy");
					ePWD.setText(pwd);
					logger.debug("New Password: default password from properties File");	
				}
	
				int numID = MCRUserMgr.instance().getMaxUserNumID();
				userElement.setAttribute("numID", String.valueOf(numID +1)) ;
			
				addImplicitGroupIDs(userElement);
			
		       	StringBuffer storePath = new StringBuffer(MCRWorkflowDirectoryManager.getWorkflowDirectory(this.documentType))
					.append("/").append("user_")
					.append(ID).append(".xml");
		       	
		       	root.addContent(userElement);
				org.jdom.Document outDoc =  new org.jdom.Document (root);	        
				WFM.storeMetadata(MCRUtils.getByteArray(outDoc), ID, storePath.toString());
					
				if ( MCRUserMgr.instance().existUser(ID) ) {
					// we have another user with that ID 
			        logger.warn("User registration - duplicate IDs");
			        if(MCRConfiguration.instance().getString("MCR.Application.ExternalUserLogin.Class").length()>1){
			        	//we use an external user manager ..-> finish workflowprocess and display error
			        	List lpids  = WFM.getCurrentProcessIDsForProcessType(ID,workflowType);	
						if ( !lpids.isEmpty()){
							long lpid = ((Long)lpids.get(0)).longValue();
							WFM.deleteWorkflowProcessInstance(lpid);
					    }						
						// choose another id or break this prozess, 
						// for initiator and editor 						
			        	nextPath = "~breakIfDuplicateUserID&userID="+ID;
			        }
			        else{
			        	//we use MyCoRe as user manager
			        	//user should select an ID
			        	nextPath = "~chooseIDwhenDuplicate&userID="+ID;
			        	
			        }
			        
				} else {
					//erst wenn alles OK ist wird der WFI initiiert mit der UserID, die unique ist.
					//we have registeruser prozess - with that id
					//long lpid = WFI.getUniqueCurrentProcessID(ID);
					long lpid = 0;
					List<Long> lpids  = WFM.getCurrentProcessIDsForProcessType(ID,workflowType);	
					if ( lpids.isEmpty()){
						lpid = WFM.initWorkflowProcess(ID, null);				
				        nextPath = "~registered";
				        lpids.add(new Long(lpid));
				    }
					lpid = ((Long)lpids.get(0)).longValue();
					// for initiator and editor 
					MCRWorkflowProcess wfp = MCRWorkflowProcessManager.getInstance().getWorkflowProcess(lpid);
					try{
						WFM.setWorkflowVariablesFromMetadata(wfp.getContextInstance(), userElement);
					}
					catch(Exception e){
						logger.error("caught exception ",e);
					}
					finally{
						wfp.close();
					}
					MCRSessionMgr.getCurrentSession().put("registereduser", new DOMOutputter().output( outDoc ));
					for ( int i=1; i< lpids.size(); i++) {
				        logger.warn("User registration - duplicate Process ID's" + lpids.get(i).toString());
					}
			       	
			    }
			}
        }		
	}
	  
    public final Element setNewUserIDforUser(String newID, String userID, String fullname, String lang ) throws Exception {
    	 org.jdom.Element userElement = null;
         try {
             org.jdom.Document doc = MCRXMLHelper.parseURI(fullname, false);
             userElement = (Element) doc.getRootElement().getChild("user").clone();
             userElement.setAttribute("ID",newID);
             // delete OldFile
             try {
     			File fi = new File(fullname);
     			if (fi.isFile() && fi.canWrite()) {				
     				fi.delete();     				
     				logger.debug("File " + fullname + " removed.");
     			} else {
     				logger.error("Can't remove file " + fullname);
     			}
     		} catch (Exception ex) {
     			logger.error("Can't remove file " + fullname);
     		}
         } catch (Exception ex) {
 			logger.error(ex.getMessage());
 			logger.error("Exception while loading the file " + fullname);						
 		}
        return userElement;

 	}    

     private String checkUserType(String ID) {
    	 MCRExternalUserLogin extLogin= null;
         if(classNameExtUserLogin.length()>0){
         	try{
         		Class c = Class.forName(classNameExtUserLogin);
         		extLogin = (MCRExternalUserLogin)c.newInstance();		
         	}       	
         	catch(Exception e){
         		//ExceptionClassNotFoundException, IllegalAccessException, InstantiationException
         		//do nothing
         	}
         }
         if (extLogin!=null) {
             // check userType from the external user system
          	return  extLogin.checkUserType(ID);
         }
         return null;

     }
     
     private void addImplicitGroupIDs(Element user){
    	 //<groups.groupID>createauthor</groups.groupID>
    		Properties props = MCRConfiguration.instance().getProperties("MCR.Users.Implicitgroups.");
			List<String> gl = new ArrayList<String>();
			try{
				String path = "user.groups/groups.groupID";
				Iterator itElems = XPath.selectNodes(user, path).iterator();
				while(itElems.hasNext()){
					Element e = (Element)itElems.next();
					gl.add(e.getTextNormalize());
				}
			}
			catch(JDOMException jde){
				//do nothing
			}
    		List<String> newGIDs = new ArrayList<String>();			
    		for(String id:gl){
					if(props.containsKey("MCR.Users.Implicitgroups."+id)){
					String value = props.getProperty("MCR.Users.Implicitgroups."+id);
					String[] gids = value.split(",");
					for(int i=0;i<gids.length;i++){
						if(!gl.contains(gids[i])){
							newGIDs.add(gids[i]);
						}
					}								
				}
			}
			Element groups = user.getChild("user.groups");
			for(String s:newGIDs){
				Element el = new Element("groups.groupID");
				el.setText(s);
				groups.addContent(el);
			}
     }
}
