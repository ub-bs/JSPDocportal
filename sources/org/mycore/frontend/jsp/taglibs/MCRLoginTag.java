package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.DOMOutputter;
import org.mycore.common.JSPUtils;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRException;
import org.mycore.common.MCRSessionMgr;
import org.mycore.user2.MCRUser;
import org.mycore.user2.MCRUserMgr;


public class MCRLoginTag extends SimpleTagSupport
{
	private String var;
	private String uid;
	private String pwd;
	private static Logger logger = Logger.getLogger(MCRInitWorkflowProcessTag.class);
	
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
        boolean loginOk = false;
        String status = "user.login";
        String username = MCRSessionMgr.getCurrentSession().getCurrentUserName();
        if ( username == null ) username = MCRSessionMgr.getCurrentSession().getCurrentUserID();
        	
        
		PageContext pageContext = (PageContext) getJspContext();		
	    Element loginresult = new Element("loginresult"); 
        if (uid != null)
            uid = (uid.trim().length() == 0) ? null : uid.trim();
        if (pwd != null)
            pwd = (pwd.trim().length() == 0) ? null : pwd.trim();

        loginresult.setAttribute("loginOK", Boolean.toString(loginOk));
        loginresult.setAttribute("status",  status);
        loginresult.setAttribute("username",  username);

        if( !(uid==null || pwd == null) ) {
	        logger.debug("Trying to log in user "+uid);	
	        try {
	            loginOk = ((uid != null) && (pwd != null) && MCRUserMgr.instance().login(uid, pwd));
	            if (loginOk) {
		        	MCRSessionMgr.getCurrentSession().setCurrentUserID(uid);
		        	username = MCRUserMgr.instance().retrieveUser(uid).getName();
	                status = "user.welcome";
		            logger.info("user " + uid + " logged in ");            			
	            } else {
		            if (uid != null) {
		            	status = "user.invalid_password";
		            }
	            }
	        } catch (MCRException e) {
	            if (e.getMessage().equals("user can't be found in the database")) {
	                status = "user.unknown";
	            } else if (e.getMessage().equals("Login denied. User is disabled.")) {
	                status = "user.disabled";
	            } else {
	                status = "user.unkwnown_error";
	                logger.debug("user.unkwnown_error" + e.getMessage());
	            }
	        }	        
            logger.info( status );
	        loginresult.setAttribute("loginOK", Boolean.toString(loginOk));
            loginresult.setAttribute("status",  status);
            loginresult.setAttribute("username",  username);
        }
        
		org.jdom.Document lgresult = new org.jdom.Document(loginresult);
		org.w3c.dom.Document domDoc = null;
		try {
			domDoc = new DOMOutputter().output(lgresult);
		} catch (JDOMException e) {
			Logger.getLogger(MCRSetResultListTag.class).error("Domoutput failed: ", e);
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
		
		return;
	}	
   
}