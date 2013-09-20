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

package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.mycore.common.MCRSession;
import org.mycore.frontend.servlets.MCRServlet;

/**
 * Tag that renders the navigation
 * 
 * @author Robert Stephan
 *
 */
public class MCROutputLanguageSelectorTag extends SimpleTagSupport
{
	private String languages;
	private String separatorString="";
	
	private ResourceBundle rbMessages;
	private String baseURL;
		
	public void doTag() throws JspException, IOException {
	    MCRSession mcrSession = MCRServlet.getSession((HttpServletRequest)((PageContext) getJspContext()).getRequest());
		String currentLang = mcrSession.getCurrentLanguage();
		if(currentLang == null){currentLang = "de";}
		rbMessages = ResourceBundle.getBundle("messages", new Locale(currentLang));
		baseURL = (String)getJspContext().getAttribute("WebApplicationBaseURL", PageContext.APPLICATION_SCOPE);
		HttpServletRequest request = (HttpServletRequest)((PageContext)getJspContext()).getRequest();
		StringBuffer url = request.getRequestURL();
		url.append("?");
		JspWriter out = getJspContext().getOut(); 
		
		@SuppressWarnings("unchecked")
		Enumeration<String>pnames = (Enumeration<String>)request.getParameterNames();
		while(pnames.hasMoreElements()){
			String pName = pnames.nextElement();
			if(pName.equals("lang")){
				continue;			
			}
			url.append(pName).append("=").append(request.getParameter(pName));
		}
		boolean first = true;
		for(String lang: languages.split(",")){
			lang = lang.trim();
			if(lang.equals(currentLang)){
				continue;
			}
			if(first){
				first = false;
			}
			else{
				out.append(separatorString);
			}
			out.append("<a href =\""+url+"&lang="+lang+"\">");
			out.append("<img style=\"border-style: none; width: 24px; height: 12px; vertical-align: bottom;\"");
			out.append(" alt=\""+retrieveI18N("secondLanguage")+"\"");
			out.append(" src=\""+baseURL+"images/lang-"+lang+".gif\"></a>");			
		}		
	}
			
	/**
	 * set the separator string
	 * @param separatorString - the String which should be printed between items 
	 */
	public void setSeparatorString(String separatorString) {
		this.separatorString = separatorString;
	}
	
	/**
	 * set the available languages (use comma separated list)
	 * @param languages
	 */
	public void setLanguages(String languages) {
		this.languages = languages;
	}
	
	
	
	private String retrieveI18N(String key){
		if(key==null || key.equals("")){
			return "";
		}
		else{
			if(rbMessages.containsKey(key)){
				return rbMessages.getString(key);
			}
			else{
				return "???"+key+"???";
			}
		}
	}

	
	
}