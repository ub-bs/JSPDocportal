/*
 * Created on 12.04.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.mycore.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectStructure;
import org.mycore.frontend.cli.MCRDerivateCommands;

/**
 * @author mycore
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class JSPUtils {

	protected static Logger logger=Logger.getLogger(JSPUtils.class);
	private static int uniqueNumber ;
	//private static Document allAuthorsQuery;
	
	// "dd.MM.YYYY" 
	private static Pattern germanDatePattern = Pattern.compile("(\\d{1,2})\\.(\\d{1,2})\\.(\\d{4})");
	private static Pattern englishDatePattern1 = Pattern.compile("(\\d{4})-(\\d{1,2})-(\\d{1,2})");
	private static Pattern englishDatePattern2 = Pattern.compile("(\\d{4})/(\\d{1,2})/(\\d{1,2})");
	private static GregorianCalendar cal = new GregorianCalendar();
	

	public static void initialize() {
		uniqueNumber = 0;
	//	allAuthorsQuery = createAllAuthorsQuery("DocPortal_author");
	}
	
	public static void deinitialize() {
		uniqueNumber = 0;
	//	allAuthorsQuery = null;
	}	
	
	/*
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
	
	private static Document createAllAuthorsQuery(String prefix) {
		
        Element query = new Element("query");
        query.setAttribute("maxResults","1000");
        
        Element conditions = new Element("conditions");
        conditions.setAttribute("format","xml");
        
        Element or = new Element("boolean");
        or.setAttribute("operator", "OR");
        
        Element personCondition = new Element("condition");
        personCondition.setAttribute("field","id");
        personCondition.setAttribute("value",prefix + "*");
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
   	   String searchTypes = MCRConfiguration.instance().getString("MCR.type_alldocs", "alldocs");
 	   return (searchTypes.indexOf(docType) >= 0);
     }    
     public static boolean isInstitution(String docType) {
        if (docType.equals("institution"))
           return true;
        else
           return false;
     }*/ 
     
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
   
     /*
     public static Document getAllAuthorsQuery() {
    	 if (allAuthorsQuery == null) {
    		 initialize();
    	 }
    	 return allAuthorsQuery ;
     }*/
     
	public static String convertToISO8601String(String date) {
		int length = date.length();
		String isoString;
		if (length > 10) return date;
		else if(length >= 8){
			isoString = convertToISO8601String(germanDatePattern.matcher(date), 3, 2, 1);
			if (!isoString.equals("")) return isoString;
			isoString = convertToISO8601String(englishDatePattern1.matcher(date), 1, 2, 3);
			if (!isoString.equals("")) return isoString;
			isoString = convertToISO8601String(englishDatePattern2.matcher(date), 1, 2, 3);
			if (!isoString.equals("")) return isoString;
		}else if (length == 4) {
			cal.set(Integer.parseInt(date),0,1);
			isoString = new StringBuffer(date).append("-").append("01").append("-").append("01")
				.append("T12:00:00.00Z").toString();
			return isoString;
		} 
		return date;
	} 

	private static String convertToISO8601String(Matcher m, int yeargroup, int monthgroup, int daygroup){
		StringBuffer sb = new StringBuffer("");
		while(m.find()) {
			int year = Integer.parseInt(m.group(yeargroup));
			int month = Integer.parseInt(m.group(monthgroup)) ;
			int day = Integer.parseInt(m.group(daygroup));
			String date = new StringBuffer(String.valueOf(year))
				.append("-")
				.append(fillToConstantLength(String.valueOf(month),"0",2))
				.append("-")
				.append(fillToConstantLength(String.valueOf(day),"0",2)).toString();
			sb.append(date);
		}
		return sb.toString();
	}
	
    public static String getISO8601TimeOffset(Calendar inputCal) {
    	int timeOffset_h = (((inputCal.get(Calendar.ZONE_OFFSET) + inputCal.get(Calendar.DST_OFFSET)) / 1000 ) / 60) / 60 ;
		StringBuffer sb = new StringBuffer("");
		if (timeOffset_h >= 0) sb.append("+");
		else sb.append("-");
		sb.append(fillToConstantLength(String.valueOf(timeOffset_h),"0",2));
		sb.append(":00");
		return sb.toString();
	}	
     
    //  static method to save any Document Object to an give directory - uses to put idt into
	// the workflow directory or to save it before deleteing - look to MCRStartEditorServlet - sdelobj
	public static String saveToDirectory(MCRObject mob, String savedir){
		MCRObjectStructure structure = mob.getStructure();
		String mcrid = mob.getId().getId();
		int derSize = structure.getDerivateSize();
		String atachedDerivates = "";

		for(int i = 0; i < derSize; i++) {
			String derivateID = structure.getDerivate(i).getXLinkHref();
	        String derDir  = savedir ;
	        if ( derivateID != null && MCRObject.existInDatastore(derivateID) ) {
				atachedDerivates += derivateID + ",";
		        MCRDerivateCommands.show(derivateID, derDir);
	        }				
		}
		for(int i = 0; i < derSize; i++) {
			structure.removeDerivate(0);
		}	
		
		saveDirect( mob.createXML(), savedir + "/" + mcrid + ".xml");
		return atachedDerivates;
	}
	 
	public static void saveDirect(Document jdom, String filename){
		FileOutputStream fos =null;	
		try {
			fos = new FileOutputStream(filename);
			(new XMLOutputter(Format.getPrettyFormat())).output(jdom,fos);
			fos.close();
		} catch (Exception ex){
			logger.error("Cant save Object" + filename);
		} finally{
			if ( fos != null ){
				try {		fos.close(); }			
				catch ( IOException io ) {; // cant clos the fos 
				}
			}
		}
	}
	
	/**
	* deletes recursively a directory 
	* @param path
	* 	java.io.File the directory to be deleted recursively
	*/    
	public static void recursiveDelete( File path ) throws MCRException{
		File files[] = path.listFiles();
		for ( int i = 0; i < files.length; i++ ){
			if ( files[i].isDirectory() )
				recursiveDelete( files[i] );
	    	files[i].delete();
		}
		path.delete();
	}
	
	/**
	* deletes recursively a directory 
	* @param path
	* 	java.io.File the directory to be deleted recursively
	*/    
	public static void recursiveCopy( File input, File output ) throws Exception{
		File files[] = input.listFiles();
		
		if ( files == null)		return;
		
		if(!output.mkdir()) throw new MCRException("could not create dir " + output.getAbsolutePath()); 
		for ( int i = 0;  i < files.length; i++ ){
			if ( files[i].isDirectory() ){
				File output2 = new File(output.getAbsolutePath() + "/" + files[i].getName());
				recursiveCopy( files[i], output2 );
			}else{
				File output2 = new File(output.getAbsolutePath() + "/" + files[i].getName());
				FileInputStream fis = new FileInputStream(files[i]);
				FileOutputStream fos = new FileOutputStream(output2); 
				MCRUtils.copyStream(fis, fos);
				fis.close();
				fos.close();
			}
		}
	}	
	
     public static void main(String[] args) {
    	 initialize();
    	 //just for testing
    	 System.out.println(convertToISO8601String("2003"));
    	 System.out.println(convertToISO8601String("2003-3-28"));
    	 System.out.println(convertToISO8601String("2003/03/30"));
    	 System.out.println(convertToISO8601String("12.5.2003"));
     }
   
}	
