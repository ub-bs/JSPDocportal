/*
 * $RCSfile$
 * $Revision: 16360 $ $Date: 2010-01-06 00:54:02 +0100 (Mi, 06 Jan 2010) $
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
package org.mycore.frontend.jsp.taglibs.docdetails;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.log4j.Logger;
import org.mycore.frontend.jsp.taglibs.docdetails.helper.UnibibliographieHRO;

/**
 * resolves links from PND Beacon Resolver (http://beacon.findbuch.de/seealso/pnd-aks)
 * 
 * @author Robert Stephan
 *
 */
public class MCRDocDetailsPNDBeaconTag extends SimpleTagSupport {
	private static Logger LOGGER=Logger.getLogger(MCRDocDetailsLinkItemTag.class);
	private String pnd="";
	private String css=null;
	private String whitelist=null;
	private String title=null;

	/**
	 * the CSS Style name applied to the output
	 * @param style
	 */
	public void setStyleName(String style){
		this.css=style;
	}
	
	/**
	 * the PND number
	 * @param pnd
	 */
	public void setPnd(String pnd) {
		this.pnd = pnd;
	}
	
	/**
	 * sets a white list of links and titles, that should be displayed
	 * in JSON Object format eg: {'http://ws.gbv.de/seealso/pnd2gso':'Gemeinsamer Verbundkatalog','http://ws.gbv.de/seealso/pnd2vd17':'VD17'}
	 * @param wl
	 */
	public void setWhitelist(String wl){
		this.whitelist = wl;
	}

	private String createHTML(){
		StringBuffer result = new StringBuffer();
		try {
			
			URL u = new URL("http://beacon.findbuch.de/seealso/pnd-aks?format=seealso&id="+pnd);
		    URLConnection uc = u.openConnection();
		    BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
		    String beaconString="";
			 for (String line; (line = br.readLine()) != null;) {
		    	beaconString = beaconString+line; 
		    }		            
		    br.close();
		        
		    JSONArray beaconArray = (JSONArray)JSONSerializer.toJSON( beaconString );
		    String beaconPndLink = beaconArray.getString(0);
		    JSONArray beaconTitles = beaconArray.getJSONArray(1);
		    //JSONArray institutions = jsonArray.getJSONArray(2);
		    JSONArray beaconLinks = beaconArray.getJSONArray(3);
		    int size = beaconLinks.size();
		    if(beaconPndLink.length()>0){
		    	if(title!=null){
		    		result.append(title+ " (");
		    	}
	   			result.append("PND: <a href=\""+beaconPndLink+"\" title=\"Eintrag in der Personennamendatei (PND)\">"+pnd+"</a>");
	   			if(title!=null){
		    		result.append(")");
		    	}
	   			result.append("<ul>");
	   		
		    	if(whitelist==null){
		   			for(int i=0;i<size;i++){
		    			if(!beaconLinks.getString(i).startsWith("http://cpr.uni-rostock.de")){
		    				result.append("<li><a href=\""+beaconLinks.getString(i)+"\">"+beaconTitles.getString(i)+"</a></li>");
		    			}
		    		}
		    	}
		    	else{
		    		JSONObject whiteListObject = (JSONObject)JSONSerializer.toJSON(whitelist);
		    		@SuppressWarnings("unchecked")
		    		Iterator<String> keys = (Iterator<String>)whiteListObject.keys();
		    		while(keys.hasNext()){
		    			String key= keys.next();
		    			if(key.startsWith("http://katalog.ub.uni-rostock.de/DB=4/")){
		    				UnibibliographieHRO biblioApp = UnibibliographieHRO.getInstance();
		    				if(biblioApp.getHitCount(pnd)>0){
		    					String title = whiteListObject.getString(key);
		    					if(title==null || title.equals("")){
		    						title = biblioApp.getMessage(pnd);
		    					}
		    					result.append("<li><a href=\""+biblioApp.getURL(pnd)+"\">"+title+"</a></li>");	
		    				}
		    			}
		    			else{
		    				for(int i=0;i<size;i++){
		    				if(beaconLinks.getString(i).startsWith(key)){
		    					String title = whiteListObject.getString(key);
		    					if(title.length()==0){
		    						title = beaconTitles.getString(i);
		    					}
		    					result.append("<li><a href=\""+beaconLinks.getString(i)+"\">"+title+"</a></li>");
		    				}
		    			}
		    		}
		    	}
		    	result.append("</ul>");
		    	}
		    }
		 } catch (Exception e) {
			   LOGGER.debug("Exception in MCRDocDetailsPNDBeaconTag");
		}
		return result.toString();
		 
	}

	public void doTag() throws JspException, IOException {
		MCRDocDetailsRowTag docdetailsRow = (MCRDocDetailsRowTag) findAncestorWithClass(this, MCRDocDetailsRowTag.class);
		if(docdetailsRow==null){
			throw new JspException("This tag must be nested in tag called 'row' of the same tag library");
		}
		getJspContext().getOut().print(createHTML());
	}
	
	/**
	 *example and test code
	 */
	public static void main(String[] args){
		MCRDocDetailsPNDBeaconTag tag = new MCRDocDetailsPNDBeaconTag();
		tag.setPnd("118558838");
		String whitelist = "{'http://d-nb.info/gnd/':'Eintrag in der Personennamendatei (PND)',"
           +"'http://www.deutsche-biographie.de/register':'Allgemeine/Neue Deutsche Biographie (ADB/NDB) Register',"
           +"'http://www.uni-leipzig.de/unigeschichte/professorenkatalog/pnd/':'Catalogus Professorum Lipsiensis',"
           +"'http://toolserver.org/~apper/pd/person/pnd-redirect/de':'Wikipedia-Personenartikel',"
           +"'http://toolserver.org/~apper/pd/person/pnd-redirect/commons':'Wikimedia Commons',"
           +"'http://beacon.findbuch.de/portraits/ps_usbk':'Portraitsammlung USB Köln'}";
		tag.setWhitelist(whitelist);
		System.out.println(tag.createHTML());
	}

	public void setTitle(String title) {
		this.title = title;
	}
}