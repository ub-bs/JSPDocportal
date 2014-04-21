package org.mycore.frontend.jsp.taglibs.browsing;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.mycore.common.MCRConfiguration;


public class MCRSearchDetailBrowserTag extends SimpleTagSupport
{
	protected static String languageBundleBase = MCRConfiguration.instance().getString(
			"MCR.languageResourceBundleBase", "messages");
	
	public void doTag() throws JspException, IOException {
		//TODO SOLR Migration
		/*
		PageContext pageContext = (PageContext) getJspContext();
	    MCRSession mcrSession = MCRServlet.getSession((HttpServletRequest) pageContext.getRequest());
        String lang = mcrSession.getCurrentLanguage();
        ResourceBundle messages = MCRTranslation.getResourceBundle(languageBundleBase, new Locale(lang));
		JspWriter out = getJspContext().getOut();
		
		/*
		MCRResults results = null;
		String id = pageContext.getRequest().getParameter("resultid");
		int offset=-1; 
		try {
			//results = (MCRResults)((MCRCache) MCRSessionMgr.getCurrentSession().get(MCRSearchServlet.getResultsKey())).get(id);
			 MCRCachedQueryData qd = MCRCachedQueryData.getData( id );
			results = qd.getResults();
			offset = Integer.parseInt(pageContext.getRequest().getParameter("offset"));
			
		} catch ( Exception all) {
			results = null;
			offset = -1;
		}
	
		
        if ( results != null) {
           	out.write("\n<!-- Searchresult PageNavigation -->");
    		out.write("\n<div class=\"searchdetail-navigation\">");
    	    out.write("\n   <div class=\"headline\">"+messages.getString("Webpage.searchdetails.headline")+"</div>");    	
        	int numHits = results.getNumHits();
        	pageContext.setAttribute("numHits", numHits);
        	out.write("\n   <div class=\"hitcount\">");
        	out.write("\n      "+messages.getString("Webpage.searchdetails.hits")+":&nbsp;&nbsp;");
    		out.write(Integer.toString(offset+1)+"&nbsp;/&nbsp;"+Integer.toString(numHits));
    		out.write("\n   </div>");
    		        	
    		if (offset > 0) {
        		MCRHit hit = results.getHit(offset - 1);
        		if(hit!=null){
        			out.write("\n   <div class=\"button\" style=\"float:left\"><a href=\""+pageContext.getAttribute("WebApplicationBaseURL", PageContext.APPLICATION_SCOPE)
        					+"nav?path="+pageContext.getRequest().getParameter("path")
        					+"&id="+hit.getID()
        					+"&offset="+Integer.toString(offset-1)
        					+"&resultid="+id
        					+"\">"+messages.getString("Webpage.searchdetails.previous")+"</a></div>");      			
        		}
        	}

        	if (offset < numHits - 1) {
        		MCRHit hit = results.getHit(offset + 1);
        		if(hit!=null){
        			out.write("\n   <div class=\"button\" style=\"float:right\"><a href=\""+pageContext.getAttribute("WebApplicationBaseURL", PageContext.APPLICATION_SCOPE)
        					+"nav?path="+pageContext.getRequest().getParameter("path")
        					+"&id="+hit.getID()
        					+"&offset="+Integer.toString(offset+1)
        					+"&resultid="+id
        					+"\">"+messages.getString("Webpage.searchdetails.next")+"</a></div>"); 
        		}
        	} 
    		
        	out.write("\n   <div class=\"button centerbutton\"><a href=\""+pageContext.getAttribute("WebApplicationBaseURL", PageContext.APPLICATION_SCOPE)
        			+"servlets/MCRJSPSearchServlet?mode=results&id="+id+"\">"+messages.getString("Webpage.searchdetails.back")+"</a></div>");
        	
        	out.write("\n</div>");
        }
        */
	}	
}