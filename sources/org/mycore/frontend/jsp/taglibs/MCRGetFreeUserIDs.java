package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.DOMOutputter;
import org.mycore.access.MCRAccessInterface;
import org.mycore.access.MCRAccessManager;
import org.mycore.common.JSPUtils;
import org.mycore.common.MCRConfiguration;
import org.mycore.datamodel.metadata.MCRXMLTableManager;
import org.mycore.user2.MCRUser;
import org.mycore.user2.MCRUserMgr;


public class MCRGetFreeUserIDs extends SimpleTagSupport
{
	private String var;
	private String userid;
	private int count = 0;
	
	private static MCRUserMgr mgr = MCRUserMgr.instance();
	private static String randomChar = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz";

	public void setVar(String inputVar) {
		var = inputVar;
		return;
	}
	public void setCount(int inputCount){
		count = inputCount;
	}
	public void setUserid(String inputUserid){
		userid = inputUserid;
	}	

	public void doTag() throws JspException, IOException {		
		PageContext pageContext = (PageContext) getJspContext();		
	    Element newUserID = new Element("freeUserID"); 
		if(userid == null || userid.equals("") ) {
			count = 0; 									
		}
		for(int childcnt=0, forcnt=0; childcnt < count; forcnt++ ) {
			String testID = getRandomID(childcnt, forcnt);
			MCRUser utest = null;
			try {
				utest = mgr.retrieveUser(testID);
			} catch ( Exception all ){
				utest = null;
			}
			if ( utest.getID().length() == 0 ){
				childcnt++;
				Element child = new Element("userid");
				child.setAttribute("ID", testID);
				newUserID.addContent(child);				
			}			
		}
		
		org.jdom.Document newIDs = new org.jdom.Document(newUserID);
		org.w3c.dom.Document domDoc = null;
		try {
			domDoc = new DOMOutputter().output(newIDs);
		} catch (JDOMException e) {
			Logger.getLogger(MCRSetResultListTag.class).error("Domoutput failed: ", e);
		}
		if(pageContext.getAttribute("debug") != null && pageContext.getAttribute("debug").equals("true")) {
			JspWriter out = pageContext.getOut();
			StringBuffer debugSB = new StringBuffer("<textarea cols=\"80\" rows=\"30\">")
				.append("found this IDs:\r\n")
				.append(JSPUtils.getPrettyString(newIDs))
				.append("--------------------\r\nfor the ID\r\n")
				.append("</textarea>");
			out.println(debugSB.toString());
		}
		pageContext.setAttribute(var, domDoc);
		
		return;
	}	
   
	private String getRandomID(int len, int trys){
		String testID = userid;
		if ( len==0) len = 1;
		if ( trys > 3*randomChar.length())	 len++;
		for (int i = 0; i < len; i++) {
			double pos = Math.random();			
			long ri = Math.round(pos * 1000000) % randomChar.length();
			Integer intObj = new Integer(Long.toString(ri));
			int index = intObj.intValue();
			testID += randomChar.substring(index,index+1);			
		}
		return testID;
	}


}