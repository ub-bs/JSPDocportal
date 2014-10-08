package org.mycore.frontend.jsp.taglibs.browsing;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.mycore.common.MCRSession;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.frontend.jsp.search.MCRSearchResultDataBean;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.services.i18n.MCRTranslation;


public class MCRSearchDetailBrowserTag extends SimpleTagSupport
{
	private String mcrid;
	
	protected static String languageBundleBase = MCRConfiguration.instance().getString(
			"MCR.languageResourceBundleBase", "messages");
	
	public void doTag() throws JspException, IOException {
		PageContext pageContext = (PageContext) getJspContext();
		MCRSession mcrSession = MCRServlet.getSession((HttpServletRequest) pageContext.getRequest());
		String lang = mcrSession.getCurrentLanguage();
		ResourceBundle messages = MCRTranslation.getResourceBundle(languageBundleBase, new Locale(lang));
		JspWriter out = getJspContext().getOut();

		String searchID = pageContext.getRequest().getParameter("_search");

		MCRSearchResultDataBean result = MCRSearchResultDataBean.retrieveSearchresultFromSession(((HttpServletRequest)pageContext.getRequest()), searchID);
		if(result!=null){
			int pos = result.getMcrIDs().indexOf(mcrid); 
			if(pos >= 0){
				result.setCurrent(result.getStart()+pos);
			}
			out.write("\n<!-- Searchresult PageNavigation -->");
			out.write("\n<div class=\"searchdetail-navigation\">");
			out.write("\n   <div class=\"headline\">"+messages.getString("Webpage.searchdetails.headline")+"</div>");    	
			long numHits = result.getNumFound();
			pageContext.setAttribute("numHits", numHits);
			out.write("\n   <div class=\"hitcount\">");
			out.write("\n      "+messages.getString("Webpage.searchdetails.hits")+":&#160;&#160;");
			out.write(Integer.toString(result.getCurrent()+1)+"&#160;/&#160;"+Long.toString(numHits));
			out.write("\n   </div>");

			if (result.getCurrent() > 0) {
				out.write("\n   <div class=\"button\" style=\"float:left\"><a href=\""+pageContext.getAttribute("WebApplicationBaseURL", PageContext.APPLICATION_SCOPE)
						+"searchresult.action?_search="+result.getId()+"&amp;_hit="+Integer.toString(result.getCurrent()-1)
						+"\">"+messages.getString("Webpage.searchdetails.previous")+"</a></div>");      			
			}


			if (result.getCurrent() < numHits - 1) {
				out.write("\n   <div class=\"button\" style=\"float:right\"><a href=\""+pageContext.getAttribute("WebApplicationBaseURL", PageContext.APPLICATION_SCOPE)
						+"searchresult.action?_search="+result.getId()+"&amp;_hit="+Integer.toString(result.getCurrent()+1)
						+"\">"+messages.getString("Webpage.searchdetails.next")+"</a></div>"); 
			}
		} 

		out.write("\n   <div class=\"button centerbutton\"><a href=\""+pageContext.getAttribute("WebApplicationBaseURL", PageContext.APPLICATION_SCOPE)
				+"searchresult.action?_search="+result.getId()+"\">"+messages.getString("Webpage.searchdetails.back")+"</a></div>");

		out.write("\n</div>");


	}

	public String getMcrid() {
		return mcrid;
	}

	public void setMcrid(String mcrid) {
		this.mcrid = mcrid;
	}	
}