package org.mycore.frontend.jsp.taglibs.browsing;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.mycore.common.config.MCRConfiguration;
import org.mycore.frontend.jsp.search.MCRSearchResultDataBean;
import org.mycore.services.i18n.MCRTranslation;


public class MCRSearchDetailBrowserTag extends SimpleTagSupport
{
	private String mcrid;
	
	protected static String languageBundleBase = MCRConfiguration.instance().getString(
			"MCR.languageResourceBundleBase", "messages");
	
	public void doTag() throws JspException, IOException {
		PageContext pageContext = (PageContext) getJspContext();
		JspWriter out = pageContext.getOut();
		String searchID = pageContext.getRequest().getParameter("_search");

		MCRSearchResultDataBean result = MCRSearchResultDataBean.retrieveSearchresultFromSession(((HttpServletRequest)pageContext.getRequest()), searchID);
		if(result!=null){
			int pos = result.findEntryPosition(mcrid); 
			if(pos >= 0){
				result.setCurrent(result.getStart()+pos);
			}
			out.write("\n<!-- Searchresult PageNavigation -->");
			out.write("\n<div id=\"searchdetail-navigation\" class=\"panel panel-default\">");
			long numHits = result.getNumFound();
			pageContext.setAttribute("numHits", numHits);
			
			out.write("\n   <div class=\"panel-heading\" style=\"text-align:center\">"+MCRTranslation.translate("Webpage.Searchresult.hitXofY", result.getCurrent()+1, numHits)+"</div>");    	
			out.write("\n   <div class=\"panel-body\">");
			out.write("\n       <a style=\"font-size:1.5em\" class=\"btn btn-default btn-xs\" href=\""+pageContext.getAttribute("WebApplicationBaseURL", PageContext.APPLICATION_SCOPE)
				+result.getAction()+"?_search="+result.getId()+"\""
				+" title=\""+MCRTranslation.translate("Webpage.Searchresult.back.hint")+"\">▲</a>");

		
			out.write("\n       <div class=\"btn-group pull-right\">");

			if (result.getCurrent() > 0) {
				out.write("\n           <a style=\"font-size:1.5em\" class=\"btn btn-default btn-xs\" href=\""+pageContext.getAttribute("WebApplicationBaseURL", PageContext.APPLICATION_SCOPE)
						+result.getAction()+"?_search="+result.getId()+"&amp;_hit="+Integer.toString(result.getCurrent()-1)+"\""
						+" title=\""+MCRTranslation.translate("Webpage.Searchresult.prevPage.hint")+"\">◀</a>");
			}


			if (result.getCurrent() < numHits - 1) {
				out.write("\n           <a style=\"font-size:1.5em\" class=\"btn btn-default btn-xs\" href=\""+pageContext.getAttribute("WebApplicationBaseURL", PageContext.APPLICATION_SCOPE)
						+result.getAction()+"?_search="+result.getId()+"&amp;_hit="+Integer.toString(result.getCurrent()+1)+"\""
						+" title=\""+MCRTranslation.translate("Webpage.Searchresult.nextPage.hint")+"\">▶</a>");
			}
			out.write("\n      </div>");
		

		out.write("\n   </div>");
		out.write("\n</div>");
		}

	}

	public void setMcrid(String mcrid) {
		this.mcrid = mcrid;
	}
}