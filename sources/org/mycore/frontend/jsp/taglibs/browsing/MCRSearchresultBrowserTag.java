package org.mycore.frontend.jsp.taglibs.browsing;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;
import org.hibernate.Transaction;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.mycore.backend.hibernate.MCRHIBConnection;
import org.mycore.common.JSPUtils;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRSessionMgr;
import org.mycore.frontend.servlets.MCRServlet;

/**
 * Tag to include an IndexBrowser into a web page implementation based on
 * 
 * @see org.mycore.frontend.indexbrowser.lucene.MCRIndexBrowserServlet
 * 
 * @author Robert Stephan
 * 
 */
public class MCRSearchresultBrowserTag extends SimpleTagSupport {
	private static final int DEFAULT_NUMPERPAGE=25;
	private static final Logger LOGGER = Logger.getLogger(MCRSearchresultBrowserTag.class);
	protected static String languageBundleBase = MCRConfiguration.instance().getString(
			"MCR.languageResourceBundleBase", "messages");
	private static Namespace NS_MCR = Namespace.getNamespace("http://www.mycore.org/");
	
	private String varMCRID;
	private String varURL;
	private String sortfields="";

	/**
	 * required: the name of the variable which holds the MCRObject as xml
	 * 
	 * @param name
	 *            - as string
	 */
	public String getVarmcrid() {
		return varMCRID;
	}

	public void setVarmcrid(String name) {
		this.varMCRID = name;
	}
	
	/**
	 * required: the name of the variable which holds the MCRObject as xml
	 * 
	 * @param name
	 *            - as string
	 */
	
	public String getVarurl() {
		return varURL;
	}

	public void setVarurl(String name) {
		this.varURL = name;
	}

	public void doTag() {
		Transaction t1=null;
		String baseurl = MCRServlet.getBaseURL();
		try {
    		Transaction tx  = MCRHIBConnection.instance().getSession().getTransaction();
	   		if(tx==null || !tx.isActive()){
				t1 = MCRHIBConnection.instance().getSession().beginTransaction();
			}
			ResourceBundle messages = PropertyResourceBundle.getBundle(languageBundleBase, new Locale(MCRSessionMgr
					.getCurrentSession().getCurrentLanguage()));

			PageContext ctx = (PageContext) getJspContext();
			Document results = (Document) ctx.getAttribute("results", PageContext.REQUEST_SCOPE);

			JspWriter out = ctx.getOut();
			
			if(ctx.getRequest().getParameter("debug") != null && ctx.getRequest().getParameter("debug").equals("true")) {
				StringBuffer debugSB = new StringBuffer("<textarea cols=\"120\" rows=\"30\">")
					.append("MCRResults as XML.\r\n")
					.append(JSPUtils.getPrettyString(results))
					.append("</textarea>");
				out.println(debugSB.toString());
			}        
			
			int numPages = 1;
			int numHits = 0;
			int numPerPage = 1;
			int page = 1;
			String mask = "";
			String id="";
			
			try{
				numHits = Integer.parseInt(results.getRootElement().getAttributeValue("numHits"));
				numPerPage = Integer.parseInt(results.getRootElement().getAttributeValue("numPerPage"));
				numPages = Integer.parseInt(results.getRootElement().getAttributeValue("numPages"));
				page = Integer.parseInt(results.getRootElement().getAttributeValue("page"));
				mask = results.getRootElement().getAttributeValue("mask");
				id = results.getRootElement().getAttributeValue("id");
			
			}
			catch(Exception e){
				//do nothing 
			}
			//@TODO - only necessary for initial developement
			if(numPerPage==0){
				numPerPage = DEFAULT_NUMPERPAGE;
			}
			
			if(numPages < Math.round((float)Math.ceil((float)numHits / numPerPage))){
				numPages = Math.round((float)Math.ceil((float)numHits / numPerPage)); 
			}
			if(sortfields.length()>0){
				writeResortForm(out, sortfields, id, messages);
			}
			if(numHits>0){
				writePageNavigation(results, out, id, numHits, numPerPage, numPages, page);
			}
			
			List l = results.getRootElement().getChildren("hit", NS_MCR);
			for(int j=0;j<l.size();j++){
				if (j>=numPerPage){break;}
					if(j>0){
						out.write("<hr />");
					}
					Element e = (Element)l.get(j);
					String mcrid = e.getAttributeValue("id");
					ctx.setAttribute(varMCRID, mcrid);
		    		
		    		String doctype = mcrid.split("_")[1];
		    		
		    		//http://localhost:8080/cpr/nav?id=cpr_professor_000000001451&offset=9&doctype=professor&path=left.search.allmeta.searchresult-allmeta.docdetail&resultid=-xst2nllmkdqafx2bcdj2
		    		StringBuffer sbURL = new StringBuffer(baseurl);
		    		sbURL.append("nav?id=").append(mcrid);
		    		sbURL.append("&offset=").append(((page-1)*numPerPage)+j);
		    		sbURL.append("&doctype=").append(doctype);
		    		sbURL.append("&path=").append(ctx.getAttribute("path", PageContext.REQUEST_SCOPE)+".docdetail");
		    		sbURL.append("&resultid=").append(id);
		    		ctx.setAttribute(varURL, sbURL.toString());
		    		
		    		
		    		getJspBody().invoke(out);
				
			}
			if(numHits>0){
				writePageNavigation(results, out, id, numHits, numPerPage, numPages, page);
			}
		} catch (Exception e) {
			LOGGER.error("The following exception was thrown in MCRSearchResultBrowserTag: ", e);
		}
		finally{
    		if(t1!=null){
    			t1.commit();
    		}
    	}
	}
	
	private void writeResortForm(JspWriter out, String sortfields, String id, ResourceBundle messages) throws IOException{
		String webBaseURL = MCRServlet.getBaseURL();
		Document query = (Document) ((PageContext) getJspContext()).getAttribute("query", PageContext.REQUEST_SCOPE);
		out.write("<div class=\"searchresult-resortform\">");
			  
		out.write("<form action=\""+webBaseURL+"servlets/MCRJSPSearchServlet\"	method=\"get\">");
		out.write("   <input type=\"hidden\" name=\"mode\" value=\"resort\">");
		out.write("   <input type=\"hidden\" name=\"id\" value=\""+id+"\">");
		String[]fieldnames = sortfields.trim().split("\\s");
		int count;
		try {
			count = Math.max(1, query.getRootElement().getChild("sortBy").getChildren("field").size());
	    } catch ( Exception allE){
		    count = 1;
		}
		
	    for(int i=0;i<count;i++){
	    	out.write("   <select name=\"field"+Integer.toString(i+1)+"\">");
	    	for(String fieldname:fieldnames){
	    		if(fieldname.length()==0){
	    			continue;
	    		}
	    		out.write("      <option value=\""+fieldname+"\"");
	    		if(isSorted(query, "name", fieldname, i)){
	    			out.write(" selected=\"true\"");
	    		}
	    		out.write(">");
	    		out.write(messages.getString("Webpage.searchresults.sortfield."+fieldname));
	    		out.write("</option>");
	    	}
	    	out.write("   </select>&nbsp;&nbsp;&nbsp;");
	    	
			out.write("   <select name=\"order"+Integer.toString(i+1)+"\">");
			for(String ordername:"ascending descending".split("\\s")){
				out.write("      <option value=\""+ordername+"\"");
				if(isSorted(query, "order", ordername, i)){
					out.write(" selected=\"true\"");
				}
				out.write(">");
				out.write(messages.getString("Webpage.searchresults.order."+ordername));
				out.write("</option>");
			}
			out.write("   </select>&nbsp;&nbsp;&nbsp;");
			out.write("<input value=\""+messages.getString("Webpage.searchresults.resort")+"\" type=\"submit\">");
			out.write("</form></div>");
	    }
	}

	
	private boolean isSorted(Document query, String attributename, String fieldname, int sortorder){
	   try { 
	    	Element sortField = (Element) query.getRootElement().getChild("sortBy").getChildren("field").get(sortorder);
			if (sortField != null) {
				if (sortField.getAttributeValue(attributename) != null &&
					sortField.getAttributeValue(attributename).equals(fieldname) ) {
						return true;
				}
			} 
	    } catch ( Exception allE){
	    	//No sortField in query -
	    }
		return false;	
	}
	
	
	
	//36.168 Publications	      Erste Seite | 11-20 | 21-30 | 31-40 | 41-50 | 51-60 | Nächste Seite
	private void writePageNavigation(Document results, JspWriter out, String id, int numHits, int numPerPage, int numPages, int page) throws IOException{
		String webBaseURL = MCRServlet.getBaseURL();
		ResourceBundle messages = PropertyResourceBundle.getBundle(languageBundleBase, new Locale(MCRSessionMgr
				.getCurrentSession().getCurrentLanguage()));
		out.write("<!-- Searchresult PageNavigation -->");
		out.write("<table class=\"searchresult-navigation\"><tr>");
		out.write("   <td style=\"width:100%\"><b>");
		out.write(Integer.toString(numHits)+" "+messages.getString("Webpage.searchresults.foundMCRObjects"));
		out.write("   </b></td>");
		if(numPages>1){
			out.write("   <td><a href=\""
					+webBaseURL+"/servlets/MCRJSPSearchServlet?mode=results&id="+id+"&page=1&numPerPage="+numPerPage
					+"\">"+messages.getString("Webpage.searchresults.firstPage")+"</a></td>");
			if(page-2>0){
				out.write("   <td><a href=\""
						+webBaseURL+"/servlets/MCRJSPSearchServlet?mode=results&id="+id+"&page="+Integer.toString(page-2)+"&numPerPage="+numPerPage
						+"\">["+Integer.toString((page-3)*numPerPage+1)+"-"+Integer.toString((page-2)*numPerPage)+"]</a></td>");
			}
			if(page-1>0){
				out.write("   <td><a href=\""
						+webBaseURL+"/servlets/MCRJSPSearchServlet?mode=results&id="+id+"&page="+Integer.toString(page-1)+"&numPerPage="+numPerPage
						+"\">["+Integer.toString((page-2)*numPerPage+1)+"-"+Integer.toString((page-1)*numPerPage)+"]</a></td>");
			}
			out.write("   <td>["+Integer.toString((page-1)*numPerPage+1)+"-"+Integer.toString(Math.min((page)*numPerPage, numHits))+"]</td>");
			if(page+1<=numPages){
				out.write("   <td><a href=\""
						+webBaseURL+"/servlets/MCRJSPSearchServlet?mode=results&id="+id+"&page="+Integer.toString(page+1)+"&numPerPage="+numPerPage
						+"\">["+Integer.toString((page)*numPerPage+1)+"-"+Integer.toString(Math.min((page+1)*numPerPage,numHits))+"]</a></td>");
			}
			if(page+2<=numPages){
				out.write("   <td><a href=\""
						+webBaseURL+"/servlets/MCRJSPSearchServlet?mode=results&id="+id+"&page="+Integer.toString(page+2)+"&numPerPage="+numPerPage
						+"\">["+Integer.toString((page+1)*numPerPage+1)+"-"+Integer.toString(Math.min((page+2)*numPerPage,numHits))+"]</a></td>");
			}
			out.write("   <td><a href=\""
					+webBaseURL+"/servlets/MCRJSPSearchServlet?mode=results&id="+id+"&page="+numPages+"&numPerPage="+numPerPage
					+"\">"+messages.getString("Webpage.searchresults.lastPage")+"</a></td>");
		}
		out.write("</tr></table>");
	}

	public String getSortfields() {
		return sortfields;
	}

	public void setSortfields(String sortfields) {
		this.sortfields = sortfields;
	}
	
}
