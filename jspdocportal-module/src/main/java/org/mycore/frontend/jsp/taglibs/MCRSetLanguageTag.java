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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.mycore.common.MCRConstants;
import org.mycore.common.MCRSession;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.frontend.servlets.MCRServlet;
/**
 * Tag that retrieves the current languages from session
 * and stores it in a given variable.
 * 
 * If the request contains a lang parameter
 * the given language will be set into session
 * 
 * @author Robert Stephan
 *
 */
public class MCRSetLanguageTag extends SimpleTagSupport
{
	private String var;
	private String allowedLanguages="";
	
	/**
	 * Sets the variable name, where the language should be saved to.
	 * @param inputVar
	 */
	public void setVar(String inputVar) {
		var = inputVar;
	}
	
	/**
	 * Provides a list of space separated language names (2 letter ISO 639-1 code) 
	 * which are valid as current language.
	 * @param allowedLanguages
	 */
	public void setAllowedLanguages(String allowedLanguages) {
		this.allowedLanguages = allowedLanguages;
	}	
		
	public void doTag() throws JspException, IOException {
		PageContext pageContext = (PageContext) getJspContext();
		MCRSession mcrSession = MCRServlet.getSession((HttpServletRequest)pageContext.getRequest());
		
		String requestParamLang = pageContext.getRequest().getParameter("lang");
		if(requestParamLang!=null){
			String[] langArray = allowedLanguages.split("\\s");
			List<String> languages = new ArrayList<String>();
			for(int i=0;i<langArray.length;i++){
				if(langArray[i].trim().length()>0){
					languages.add(langArray[i].trim());
				}
			}
			if(languages.contains(requestParamLang)){
				mcrSession.setCurrentLanguage(requestParamLang);
				Locale loc = new Locale(requestParamLang);
				Config.set(pageContext.getSession(), Config.FMT_LOCALE, loc);
			}
			else{
				mcrSession.setCurrentLanguage(MCRConfiguration.instance().getString("MCR.Metadata.DefaultLang", MCRConstants.DEFAULT_LANG));
			}
		}
		if(var!=null){
			pageContext.setAttribute(var, mcrSession.getCurrentLanguage());
		}
	}
}