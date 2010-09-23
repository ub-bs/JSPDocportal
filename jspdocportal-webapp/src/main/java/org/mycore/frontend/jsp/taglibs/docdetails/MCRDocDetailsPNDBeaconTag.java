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
import java.util.Set;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.log4j.Logger;

/**
 * resolves links from PND Beacon Resolve (http://beacon.findbuch.de/seealso/pnd-aks)
 * 
 * @author Robert Stephan
 *
 */
public class MCRDocDetailsPNDBeaconTag extends SimpleTagSupport {
	private static Logger LOGGER=Logger.getLogger(MCRDocDetailsLinkItemTag.class);
	private String pnd="";
	private String css=null;
	private String whitelist=null;

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


	public void doTag() throws JspException, IOException {
		MCRDocDetailsRowTag docdetailsRow = (MCRDocDetailsRowTag) findAncestorWithClass(this, MCRDocDetailsRowTag.class);
		if(docdetailsRow==null){
			throw new JspException("This tag must be nested in tag called 'row' of the same tag library");
		}
		MCRDocDetailsTag docdetails = (MCRDocDetailsTag) findAncestorWithClass(this, MCRDocDetailsTag.class);
		StringBuffer result = new StringBuffer();
		try {
			
			URL u = new URL("http://beacon.findbuch.de/seealso/pnd-aks?format=seealso&id="+pnd);
		    URLConnection uc = u.openConnection();
		    BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
		    String inputLine="";
		        
		    while(br.ready()){
		    	inputLine = inputLine+br.readLine(); 
		    }		            
		    br.close();
		        
		    JSONArray jsonArray = (JSONArray)JSONSerializer.toJSON( inputLine );
		    String pndLink = jsonArray.getString(0);
		    JSONArray titles = jsonArray.getJSONArray(1);
		    //JSONArray institutions = jsonArray.getJSONArray(2);
		    JSONArray links = jsonArray.getJSONArray(3);
		    int size = links.size();
		    
		    if(size>1){
		    	
		    	result.append("<ul>");
		    	if(whitelist==null){
		    		result.append("<li><a href=\""+pndLink+"\">Eintrag in der Personennamendatei (PND)</a></li>");
		    		for(int i=0;i<size;i++){
		    			if(!links.getString(i).startsWith("http://cpr.uni-rostock.de")){
		    				result.append("<li><a href=\""+links.getString(i)+"\">"+titles.getString(i)+"</a></li>");
		    			}
		    		}
		    	}
		    	else{
		    		JSONObject jo = (JSONObject)JSONSerializer.toJSON(whitelist);
		    		@SuppressWarnings("unchecked")
		    		Set<String> keys = (Set<String>)jo.keySet();
		    		for( String s: keys){
		    			 if(pndLink.startsWith(s)){
		    				 String title = jo.getString(s);
		    				 if(title.length()==0){
		    					 title = "Eintrag in der Personennamendatei (PND)";
		    				 }
		    				 result.append("<li><a href=\""+pndLink+"\">"+title+"</a></li>");
		    			 }
				     }
		    		 for(int i=0;i<size;i++){
		    			 for( String s: keys){
			    			 if(links.getString(i).startsWith(s)){
			    				 String title = jo.getString(s);
			    				 if(title.length()==0){
			    					 title = titles.getString(i);
			    				 }
			    				 result.append("<li><a href=\""+links.getString(i)+"\">"+title+"</a></li>");
			    			 }
					     }
			    	 }
		    	}		    	
		        result.append("</ul>");
		    }			
			
	    	if(result.length()>0){
	    		if(css!=null && !"".equals(css)){
	    			getJspContext().getOut().print("<td class=\""+css+"\">");
	    		}
	    		else{
	    			getJspContext().getOut().print("<td class=\""+docdetails.getStylePrimaryName()+"-value\">");
	    		}
	    		getJspContext().getOut().print(result.toString());		
	    		getJspContext().getOut().print("</td>");
	    	}
	    } catch (Exception e) {
		   LOGGER.debug("Exception in MCRDocDetailsPNDBeaconTag");
		}
	}
}