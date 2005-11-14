/*
 * Created on 12.04.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.mycore.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.regex.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.mycore.access.MCRAccessManager;
import org.mycore.access.MCRAccessStore;
import org.mycore.access.MCRIPAddress;
import org.mycore.datamodel.classifications.MCRClassification;
import org.mycore.user.MCRUser;

/**
 * @author mycore
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class JSPUtils {

	protected static Logger logger=Logger.getLogger(JSPUtils.class);
	private static int uniqueNumber ;
	private static Document allAuthorsQuery;

	public static void initialize() {
		uniqueNumber = 0;
		allAuthorsQuery = createAllAuthorsQuery();
	}
	
	public static void deinitialize() {
		uniqueNumber = 0;
		allAuthorsQuery = null;
	}	
	
	public static Map getSessionParameterMap(HttpServletRequest request) {
	    Map map = request.getParameterMap();
	    HttpSession session = request.getSession();
	    //MCRSession session = MCRSessionMgr.getCurrentSession();
	    String sessionID = session.getId();
	    if (sessionID == null) {
	        Logger.getLogger("workflow-editor.jsp").error("session is null");
	    }
	    String jSessionID = MCRConfiguration.instance().getString("MCR.session.param", ";jsessionid=");
	    if ((session != null) && !request.isRequestedSessionIdFromCookie()) {
	        String[] values = {jSessionID + session.getId()};
	        map.put("HttpSession", values);
	    }
	    if (session != null) {
	        String[] values = {jSessionID + session.getId()};
	        map.put("JSessionID", values);
	    }  
	    if(map.get("lang") == null) {
	    	String lang = (String)request.getAttribute("lang");
	    	if (lang == null) lang = "de";
	        String[] values = {lang};
	        map.put("lang",values);
	    }
	    return map;
	}
	
	private static Document createAllAuthorsQuery() {
		
        Element query = new Element("query");
        query.setAttribute("maxResults","1000");
        
        Element conditions = new Element("conditions");
        conditions.setAttribute("format","xml");
        
        Element or = new Element("or");
        
        Element personCondition = new Element("condition");
        personCondition.setAttribute("field","id");
        personCondition.setAttribute("value","_author_");
        personCondition.setAttribute("operator","like");
        
        Element hosts = new Element("hosts");
        
        Element host = new Element("host");
        host.setAttribute("field","local");
        
        Element types = new Element("types");
        
        Element type = new Element("type");
        type.setAttribute("field","allpers");
        
        Element sortby = new Element("sortby");
        
        Element sortfield = new Element("field");
        sortfield.setAttribute("field","surname");
        sortfield.setAttribute("order","ascending");

        sortby.addContent(sortfield);
        
        types.addContent(type);
        hosts.addContent(host);
        
        or.addContent(personCondition);
        conditions.addContent(or);
        query.addContent(conditions);
        query.addContent(hosts);
        query.addContent(types);
        query.addContent(sortby); 		
		
		return new Document(query);
	}
	
    public static boolean isAuthor(String docType) {
        if (docType.equals("author"))
           return true;
        else
           return false;
     }
     public static boolean isDocument(String docType) {
        if (docType.equals("document"))
           return true;
        else
           return false;
     }    
     public static boolean isInstitution(String docType) {
        if (docType.equals("institution"))
           return true;
        else
           return false;
     } 
     public static String fillToConstantLength(String value,String fillsign,int length) {
    	 int valueLength = value.length();
    	 if (valueLength >= length) return value;
    	 StringBuffer ret = new StringBuffer("");
    	 for (int i = 0; i < length - valueLength; i++) {
			ret.append(fillsign);
    	 }
    	 ret.append(value);
    	 return ret.toString();
     }
     
     public static synchronized int getNextUniqueNumber() {
    	 return ++uniqueNumber;
	 }
     
     public static String getPrettyString(Element el) {
    	 XMLOutputter output = new XMLOutputter(Format.getPrettyFormat());
    	 return output.outputString(el);
     }

     public static String getPrettyString(Document doc) {
    	 XMLOutputter output = new XMLOutputter(Format.getPrettyFormat());
    	 return output.outputString(doc);
     }  
     
     public static Document getAllAuthorsQuery() {
    	 if (allAuthorsQuery == null) {
    		 initialize();
    	 }
    	 return allAuthorsQuery ;
     }
     
     public static void main(String[] args) {
    	 initialize();
System.out.println(new StringBuffer("").append("heiko").append("&lang=").append("de").toString());
     }
   
}	
