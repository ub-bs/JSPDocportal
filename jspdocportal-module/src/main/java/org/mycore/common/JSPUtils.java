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
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.mycore.datamodel.metadata.MCRMetaLinkID;
import org.mycore.datamodel.metadata.MCRMetadataManager;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.cli.MCRDerivateCommands;

/**
 * @author mycore
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class JSPUtils {

	protected static Logger logger=Logger.getLogger(JSPUtils.class);
	
	// "dd.MM.YYYY" 
	private static Pattern germanDatePattern = Pattern.compile("(\\d{1,2})\\.(\\d{1,2})\\.(\\d{4})");
	private static Pattern englishDatePattern1 = Pattern.compile("(\\d{4})-(\\d{1,2})-(\\d{1,2})");
	private static Pattern englishDatePattern2 = Pattern.compile("(\\d{4})/(\\d{1,2})/(\\d{1,2})");
	private static GregorianCalendar cal = new GregorianCalendar(Locale.GERMANY);
	
     
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
		String atachedDerivates = "";
		String mcrid = mob.getId().toString();
		
		for(MCRMetaLinkID metalinkID: mob.getStructure().getDerivates()) {
			MCRObjectID derID = metalinkID.getXLinkHrefID();
			if ( derID != null && MCRMetadataManager.exists(derID)) {
				atachedDerivates += derID.toString() + ",";
		        MCRDerivateCommands.show(derID.toString(), savedir);
	        }				
		}
		mob.getStructure().getDerivates().clear();
		
		saveDirect( mob.createXML(), savedir + "/" + mcrid + ".xml");
		return atachedDerivates;
	}
	 
	public static void saveDirect(Document jdom, String filename){
		FileOutputStream fos =null;	
		try {
			fos = new FileOutputStream(filename);
			XMLOutputter xmlOut = new XMLOutputter(Format.getPrettyFormat()); 
			xmlOut.output(jdom,fos);
			fos.close();
		} catch (Exception ex){
			logger.error("Cant save Object" + filename, ex);
		} finally{
			if ( fos != null ){
				try {
					fos.close(); 
				}			
				catch ( IOException ioe ) {
					logger.error(ioe); 
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
		for(File f: path.listFiles()){
			if(f.isDirectory()){
				recursiveDelete(f);
			}
			f.delete();
		}
		path.delete();
	}
	
	/**
	* deletes recursively a directory 
	* @param path
	* 	java.io.File the directory to be deleted recursively
	*/    
	public static void recursiveCopy( File input, File output ) throws Exception{
		if(input.isDirectory()){
			if(!output.mkdir()){
				throw new MCRException("could not create dir " + output.getAbsolutePath()); 
			}
			for (File f: input.listFiles()){
				if ( f.isDirectory() ){
					File output2 = new File(output.getAbsolutePath() + "/" + f.getName());
					recursiveCopy( f, output2 );
				} else {
					File output2 = new File(output.getAbsolutePath() + "/" + f.getName());
					FileInputStream fis = new FileInputStream(f);
					FileOutputStream fos = new FileOutputStream(output2); 
					MCRUtils.copyStream(fis, fos);
					fis.close();
					fos.close();
				}
			}
		}
	}
	
	/**
	 * some tests
	 * @param args none
	 */
     public static void main(String[] args) {
      	 //just for testing
    	 System.out.println(convertToISO8601String("2003"));
    	 System.out.println(convertToISO8601String("2003-3-28"));
    	 System.out.println(convertToISO8601String("2003/03/30"));
    	 System.out.println(convertToISO8601String("12.5.2003"));
     }
   
}	
