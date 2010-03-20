package org.mycore.frontend.jsp.taglibs.browsing;

import java.io.IOException;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRSessionMgr;
import org.mycore.services.fieldquery.MCRCachedQueryData;
import org.mycore.services.fieldquery.MCRHit;
import org.mycore.services.fieldquery.MCRResults;


public class MCRSearchDetailBrowserTag extends SimpleTagSupport
{
	protected static String languageBundleBase = MCRConfiguration.instance().getString(
			"MCR.languageResourceBundleBase", "messages");
	
	public void doTag() throws JspException, IOException {
		ResourceBundle messages = PropertyResourceBundle.getBundle(languageBundleBase, new Locale(MCRSessionMgr
				.getCurrentSession().getCurrentLanguage()));
		JspWriter out = getJspContext().getOut();
		PageContext pageContext = (PageContext) getJspContext();
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
           	out.write("<!-- Searchresult PageNavigation -->");
    		out.write("<table class=\"searchdetail-navigation\" style=\"width:1%;\"><tr>");
    	        	
        	int numHits = results.getNumHits();
        	pageContext.setAttribute("numHits", numHits);
        	out.write("   <td style=\"text-align:right;\"><nobr>");
        	out.write(messages.getString("Webpage.searchdetails.hits")+":&nbsp;&nbsp;");
    		out.write(Integer.toString(offset+1)+"&nbsp;/&nbsp;"+Integer.toString(numHits));
    		out.write("   </nobr></td>");
        	if (offset > 0) {
        		MCRHit hit = results.getHit(offset - 1);
        		if(hit!=null){
        			out.write("<td><nobr><a href=\""+pageContext.getAttribute("WebApplicationBaseURL", PageContext.APPLICATION_SCOPE)
        					+"nav?path="+pageContext.getAttribute("path", PageContext.REQUEST_SCOPE)
        					+"&id="+hit.getID()
        					+"&offset="+Integer.toString(offset-1)
        					+"&resultid="+id
        					+"\">["+messages.getString("Webpage.searchdetails.previous")+"]</a></nobr></td>");      			
        		}
        	}
        	
        	out.write("<td><nobr><a href=\""+pageContext.getAttribute("WebApplicationBaseURL", PageContext.APPLICATION_SCOPE)
        			+"servlets/MCRJSPSearchServlet?mode=results&id="+id+"\">["+messages.getString("Webpage.searchdetails.back")+"]</a></nobr></td>");
        	
        	if (offset < numHits - 1) {
        		MCRHit hit = results.getHit(offset + 1);
        		if(hit!=null){
        			out.write("<td><nobr><a href=\""+pageContext.getAttribute("WebApplicationBaseURL", PageContext.APPLICATION_SCOPE)
        					+"nav?path="+pageContext.getAttribute("path", PageContext.REQUEST_SCOPE)
        					+"&id="+hit.getID()
        					+"&offset="+Integer.toString(offset+1)
        					+"&resultid="+id
        					+"\">["+messages.getString("Webpage.searchdetails.next")+"]</a></nobr></td>"); 
        		}
        	} 
        	out.write("</tr></table>");
        }
	}	
}