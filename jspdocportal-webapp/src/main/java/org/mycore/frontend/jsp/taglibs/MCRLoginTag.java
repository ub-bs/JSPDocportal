package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.output.DOMOutputter;
import org.mycore.common.JSPUtils;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRException;
import org.mycore.common.MCRSession;
import org.mycore.common.MCRSessionMgr;
import org.mycore.common.MCRSystemUserInformation;
import org.mycore.frontend.jsp.user.MCRExternalUserLogin;
import org.mycore.user2.MCRRole;
import org.mycore.user2.MCRRoleManager;
import org.mycore.user2.MCRUser;
import org.mycore.user2.MCRUserManager;



public class MCRLoginTag extends SimpleTagSupport
{
    private String var;
	private String uid;
	private String pwd;
	private static Logger logger = Logger.getLogger(MCRLoginTag.class);
	private static String classNameExtUserLogin = MCRConfiguration.instance().getString("MCR.Application.ExternalUserLogin.Class", "").trim();
	
	public void setVar(String inputVar) {
		var = inputVar;
		return;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	
	public void setUid(String uid) {
		this.uid = uid;
	}

	public void doTag() throws JspException, IOException {		
        boolean extLoginOk = false;
        boolean mcrLoginOK = false;
        String mcrUID = "";
        String mcrPWD = "";
       
        MCRSession mcrSession = MCRSessionMgr.getCurrentSession();
		
        String oldID = mcrSession.getUserInformation().getUserID();
		String oldUsername =  mcrSession.getUserInformation().getUserID();
		

		if (uid != null)
            uid = (uid.trim().length() == 0) ? null : uid.trim();
        if (pwd != null)
            pwd = (pwd.trim().length() == 0) ? null : pwd.trim();
        
        MCRUser mcrUser = null;
        Element loginresult = new Element("loginresult");
       
        if(uid==null || pwd == null ){
            logger.debug("ID or Password cannot be empty");
            loginresult.setAttribute("loginOK", "false");
            loginresult.setAttribute("status",  "user.login");
            loginresult.setAttribute("username",  oldUsername);
            setTagResult(loginresult);
            return;
        }
        
        logger.debug("Trying to log in user "+uid);
        if(oldID.equals(uid)){
        	logger.debug("User "+oldUsername+" with ID "+uid+" is allready logged in");
            loginresult.setAttribute("loginOK", "false");
            loginresult.setAttribute("status",  "user.exists");
            loginresult.setAttribute("username",  oldUsername);
            setTagResult(loginresult);
        	return;
        }
        
        MCRExternalUserLogin extLogin= null;
        if(classNameExtUserLogin.length()>0){
        	try{
        		@SuppressWarnings("unchecked")
				Class<MCRExternalUserLogin> c = (Class<MCRExternalUserLogin>)Class.forName(classNameExtUserLogin);
        		extLogin = (MCRExternalUserLogin)c.newInstance();		
        	}       	
        	catch(Exception e){
        		//ExceptionClassNotFoundException, IllegalAccessException, InstantiationException
        		//do nothing
        		logger.debug("Could not load MCRExternalUserLogin: " + classNameExtUserLogin, e);
        	}
        }
        
        if (extLogin!=null) {
           // check userID und PW against external user management system
        	extLoginOk =extLogin.loginUser(uid, pwd);
        }
       	if(extLoginOk){
   		    mcrUID = extLogin.retrieveMyCoReUserID(uid, pwd);
  		    mcrPWD = extLogin.retrieveMyCoRePassword(uid, pwd);
   		   	if (  MCRUserManager.exists(mcrUID) ) { 
   		  	       	mcrLoginOK = loginInMyCore(mcrUID, mcrPWD, loginresult);  			        		
   		    } 
       	} else {
	       logger.info("No External User Login - check for MyCoRe User");
	       mcrUID = uid;
	       mcrPWD = pwd;
	       mcrLoginOK=loginInMyCore(mcrUID, mcrPWD, loginresult);
       	}
		if(mcrLoginOK){
			mcrUser = MCRUserManager.getUser(mcrUID);
			if(loginresult.getAttribute("loginOK")!=null){
			    mcrLoginOK =  loginresult.getAttribute("loginOK").getValue().equals(Boolean.toString(Boolean.TRUE));
		
			}
		}	
		//interprete the results
		if(extLoginOk && mcrLoginOK){
			//the user exists in external system and MyCoRe -> everything is OK
	     	mcrUser = MCRUserManager.getUser(mcrUID);
	       	 
	     	mcrSession.setUserInformation(MCRSystemUserInformation.getSuperUserInstance()); //"root;"
	       	extLogin.updateUserData(uid, "", mcrUser);
	     	mcrSession.setUserInformation(mcrUser);
	       	
		    loginresult.setAttribute("loginOK", "true");
		    setNameIntoLoginResult(uid, loginresult);
		}
		
		if(!extLoginOk && mcrLoginOK){
			//the user could not be validated against external system
			//but he could be validated against MyCoRe Loging 
			//-> use MyCoRe
	     	mcrUser = MCRUserManager.getUser(mcrUID);
	     	mcrSession.setUserInformation(mcrUser);
		    loginresult.setAttribute("loginOK", "true");
		}
		if(extLoginOk && !mcrLoginOK){
			//the user is regcognized as member of the institution
			//-> login as member (special account with extended read rights)
			mcrUID=MCRConfiguration.instance().getString("MCR.Application.ExternalUserLogin.DefaultUser.uid", "gast").trim();
			mcrPWD=MCRConfiguration.instance().getString("MCR.Application.ExternalUserLogin.DefaultUser.pwd", "gast").trim();
			String oldStatus = loginresult.getAttributeValue("status");
			loginInMyCore(mcrUID, mcrPWD, loginresult);
			if((oldStatus!=null) && oldStatus.equals("user.disabled")){
				loginresult.setAttribute("status", "user.disabled_member");	
			}
			else{
				loginresult.setAttribute("status", "user.member");
			}
        	loginresult.setAttribute("loginOK", "true");	
			
			
		}
		if(!extLoginOk && !mcrLoginOK){
			//the user is not allowed
        	loginresult.setAttribute("status", "user.unknown");
        	loginresult.setAttribute("loginOK", "false");	
		}
		
		setTagResult(loginresult);
	}	
		
	

    
        
        
	private void setTagResult(Element loginresult) throws IOException{
		PageContext pageContext = (PageContext) getJspContext();
		org.jdom2.Document lgresult = new org.jdom2.Document(loginresult);
		org.w3c.dom.Document domDoc = null;
		try {
			domDoc = new DOMOutputter().output(lgresult);
		} catch (JDOMException e) {
			Logger.getLogger(MCRLoginTag.class).error("Domoutput failed: ", e);
		}
		if(pageContext.getAttribute("debug") != null && pageContext.getAttribute("debug").equals("true")) {
			JspWriter out = pageContext.getOut();
			StringBuffer debugSB = new StringBuffer("<textarea cols=\"80\" rows=\"30\">")
				.append("found this UserInfos:\r\n")
				.append(JSPUtils.getPrettyString(lgresult))
				.append("--------------------\r\nfor the ID\r\n")
				.append("</textarea>");
			out.println(debugSB.toString());
		}
		pageContext.setAttribute(var, domDoc);	
	}	

	private boolean loginInMyCore(String uid, String pwd, Element loginresult){
		 boolean loginOk=false;
		try {
            MCRUser mcrUser = MCRUserManager.login(uid, pwd);
            if (mcrUser!=null) {
                loginOk=true;
               	MCRSessionMgr.getCurrentSession().setUserInformation(mcrUser);
	        	setNameIntoLoginResult(uid, loginresult);
	            
	        	String[] allGroupIDS = mcrUser.getSystemRoleIDs().toArray(new String[]{});
	        	Element eGroups = new Element ("groups");
	        	for ( int i=0; i<allGroupIDS.length; i++ ) {		        		
	        		Element eGroup = new Element ("group");
	        		MCRRole mcrgroup = MCRRoleManager.getRole(allGroupIDS[i]);		        				        		
	        		eGroup.setAttribute("gid", allGroupIDS[i]);
	        		eGroup.setAttribute("description", mcrgroup.getName());
	        		eGroups.addContent(eGroup);
            	}
	        	loginresult.setAttribute("status", "user.welcome");
	            logger.info("user " + uid + " logged in ");
	            loginresult.addContent(eGroups);
            } else {
	            if (uid != null) {
	            	loginresult.setAttribute("status", "user.invalid_password");
	            }
            }
        } catch (MCRException e) {
        	loginOk=false;
            if (e.getMessage().equals("user can't be found in the database")) {
            	loginresult.setAttribute("status", "user.unknown");
            } else if (e.getMessage().equals("Login denied. User is disabled.")) {
            	loginresult.setAttribute("status",  "user.disabled");
            } else {
            	loginresult.setAttribute("status", "user.unkwnown_error");
                logger.debug("user.unkwnown_error" + e.getMessage());
            }
        }	        
        loginresult.setAttribute("loginOK", Boolean.toString(loginOk));
        
        
        logger.info( loginresult.getAttributeValue("status"));
        return loginOk;
	}
	
	private void setNameIntoLoginResult(String uid, Element loginresult){
    	loginresult.setAttribute("username", uid);    			
    	StringBuffer name=new StringBuffer();
    	ResourceBundle messages = PropertyResourceBundle.getBundle("messages", new Locale(MCRSessionMgr.getCurrentSession().getCurrentLanguage()));
    	MCRUser mcrUser = MCRUserManager.getUser(uid);
    	if("female".equals(mcrUser.getAttributes().get("sex"))){
    		//Frau
    		name.append(messages.getString("Editor.Person.gender.female"));
    	}
    	else{
    		//Herr
    		name.append(messages.getString("Editor.Person.gender.male"));
    	}
    	name.append(" ");
    	name.append(mcrUser.getRealName());
        loginresult.setAttribute("name", name.toString());
	}
}