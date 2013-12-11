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

import static javax.servlet.jsp.PageContext.PAGE_SCOPE;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.mycore.common.MCRSession;
import org.mycore.frontend.servlets.MCRServlet;

/**
 * Tag that renders the language selector
 * 
 * It uses "languages" attribute to define a comma-separated list of available languages.
 * It iterates through this list of languages and executes its body.
 * 
 * You may use the following page variables within the JSP body:
 * - lang_currentlang - the language currently used in the application
 * - lang_first - the first language in the list of available languages
 * - lang_lang - the current language while iterating through the list of languages
 * - lang_href - the link to be displayed for the current language
 * - lang_imageURL - the URL to the image to be displayed for the current language
 *  
 *  Sample Usage (simple):
 *  
 *  <mcr:outputLanguageSelector languages="de,en">
 *    <c:if test="${lang_currentLang != lang_lang}">
 *       <a href="${lang_href}">
 *       <img src="${lang_imageURL}"
 *           style="border-style: none; width: 24px; height: 12px; vertical-align: bottom;" />
 *       </a>
 *    </c:if>
 *  </mcr:outputLanguageSelector>
 *  
 *  Sample Usage (advanced):
 *  
 *  <mcr:outputLanguageSelector languages="de,en">
 *   <fmt:message var="lang_label" key="Webpage.lang.${lang_lang }" />
 *   <fmt:message var="lang_title" key="Webpage.lang.${lang_lang }.title" />
 *   <c:if test="${!lang_first}">&nbsp;|&nbsp;</c:if>                     
 *   <c:choose>
 *      <c:when test="${lang_lang == lang_currentLang }">
 *          <span class="lang_link_active">${lang_label}</span>
 *      </c:when>
 *      <c:otherwise>
 *         <a href="${lang_href}" title="${lang_title }" class="lang_link">${lang_label}</a>
 *      </c:otherwise>
 *   </c:choose>                     
 * </mcr:outputLanguageSelector>
 *
 *  
 *  
 * @author Robert Stephan
 *
 */
public class MCROutputLanguageSelectorTag extends SimpleTagSupport
{
	private String languages;
	private String baseURL;
		
	public void doTag() throws JspException, IOException {
	    MCRSession mcrSession = MCRServlet.getSession((HttpServletRequest)((PageContext) getJspContext()).getRequest());
		String currentLang = mcrSession.getCurrentLanguage();
		if(currentLang == null){currentLang = "de";}
		baseURL = (String)getJspContext().getAttribute("WebApplicationBaseURL", PageContext.APPLICATION_SCOPE);
		HttpServletRequest request = (HttpServletRequest)((PageContext)getJspContext()).getRequest();
		StringBuffer url = request.getRequestURL();
		url.append("?");
		JspWriter out = getJspContext().getOut();
		JspContext context = getJspContext();
		JspFragment body = getJspBody();
		
		@SuppressWarnings("unchecked")
		Enumeration<String>pnames = (Enumeration<String>)request.getParameterNames();
		
		context.setAttribute("lang_currentLang", currentLang, PAGE_SCOPE);
		
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
			context.setAttribute("lang_first", first, PAGE_SCOPE);
			if(first){
				first = false;
			}
			
			context.setAttribute("lang_href", url.toString() + "&lang=" + lang, PAGE_SCOPE);
			context.setAttribute("lang_lang", lang, PAGE_SCOPE);
			StringBuilder imageURL = new StringBuilder(baseURL);
			imageURL.append("images/lang-").append(lang).append(".png"); 
			context.setAttribute("lang_imageURL", imageURL, PAGE_SCOPE);
			
			body.invoke(out);
		}		
	}
			
	/**
	 * set the available languages (use comma separated list)
	 * @param languages
	 */
	public void setLanguages(String languages) {
		this.languages = languages;
	}
}