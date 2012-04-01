package org.mycore.frontend.jsp.taglibs.browsing;

import java.io.IOException;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;
import org.hibernate.Transaction;
import org.jdom.Element;
import org.mycore.backend.hibernate.MCRHIBConnection;
import org.mycore.common.JSPUtils;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRSessionMgr;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.services.fieldquery.MCRCachedQueryData;
import org.mycore.services.fieldquery.MCRQuery;
import org.mycore.services.fieldquery.MCRResults;
import org.mycore.services.i18n.MCRTranslation;

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
			String qid = ctx.getRequest().getParameter("id");
			MCRCachedQueryData qd = MCRCachedQueryData.getData(qid);

			JspWriter out = ctx.getOut();
			
			if(ctx.getRequest().getParameter("debug") != null && ctx.getRequest().getParameter("debug").equals("true")) {
				StringBuffer debugSB = new StringBuffer("<textarea cols=\"120\" rows=\"30\">")
					.append("MCRResults as XML.\r\n")
					.append(JSPUtils.getPrettyString(qd.getResults().buildXML()))
					.append("</textarea>");
				out.println(debugSB.toString());
			}        
			
			int numHits = qd.getResults().getNumHits();
			int numPerPage=qd.getNumPerPage();
			if(numPerPage==0){numPerPage = DEFAULT_NUMPERPAGE;}
			int numPages = Math.round((float)Math.ceil((float)numHits / numPerPage)); 
			int page = qd.getPage();
			String mask = ctx.getRequest().getParameter("mask");
			String id = qd.getResults().getID();
			
			
			if(sortfields.length()>0){
				writeResortForm(qd.getQuery(), out, sortfields, id, messages, mask);
			}
			if(numHits>0){
				writePageNavigation(out, id, numHits, numPerPage, numPages, page, mask);
			}
			
			int start =(page-1)*numPerPage; 
			int stop = Math.min(numHits,page*numPerPage)-1;
			
			//TODO This is en expensive operation since the search is executed a 2nd
			//     time - cleanup after new implementation of MCRSearch
			
			qd = MCRCachedQueryData.cache(qd.getQuery(), qd.getInput());
			MCRResults results = qd.getResults();
			id = results.getID();
			results.buildXML(start, stop);

			String path = (String)MCRSessionMgr.getCurrentSession().get("navPath");
			if(path==null){
				path="";
			}
			for(int j=start;j<=stop;j++){
				if(j>start){
					out.write("<hr />");
				}
				String mcrid = results.getHit(j).getID();
				ctx.setAttribute(varMCRID, mcrid);
		    	
		    	//http://localhost:8080/cpr/nav?id=cpr_professor_000000001451&offset=9&path=left.search.allmeta.searchresult-allmeta.docdetail&resultid=-xst2nllmkdqafx2bcdj2
		    	StringBuffer sbURL = new StringBuffer(baseurl);
		    	sbURL.append("nav?id=").append(mcrid);
		    	sbURL.append("&offset=").append(j);
		    	sbURL.append("&path=").append(path+".docdetail");
		    	sbURL.append("&resultid=").append(id);
		    	ctx.setAttribute(varURL, sbURL.toString());
		    	
		    	getJspBody().invoke(out);
			}
			if(numHits>0){
				writePageNavigation(out, id, numHits, numPerPage, numPages, page, mask);
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
	
	private void writeResortForm(MCRQuery query, JspWriter out, String sortfields, String id, ResourceBundle messages, String searchmask) throws IOException{
		String webBaseURL = MCRServlet.getBaseURL();
		
		out.write("<div class=\"searchresult-resortform\">");
		out.write("<form action=\""+webBaseURL+"servlets/MCRJSPSearchServlet\"	method=\"get\">");
		out.write("   <input type=\"hidden\" name=\"mode\" value=\"resort\">");
		out.write("   <input type=\"hidden\" name=\"id\" value=\""+id+"\">");
		out.write("   <input type=\"hidden\" name=\"mask\" value=\""+searchmask+"\">");
		String[]fieldnames = sortfields.trim().split("\\s");
		int count;
		try {
			count = Math.max(1, query.getSortBy().size());
	    } catch ( Exception allE){
		    count = 1;
		}
		
	    for(int i=0;i<count;i++){
	    	out.write(messages.getString("Webpage.Searchresult.resort-label"));
	    	out.write("   <select name=\"field"+Integer.toString(i+1)+"\">");
	    	out.write("      <option value=\""+"\"></option>");
	    	for(String fieldname:fieldnames){
	    		if(fieldname.length()==0){
	    			continue;
	    		}
	    		out.write("      <option value=\""+fieldname+"\"");
	    		if(isSorted(query, "name", fieldname, i)){
	    			out.write(" selected=\"true\"");
	    		}
	    		out.write(">");
	    		out.write(MCRTranslation.translate("Webpage.searchresults.sortfield."+fieldname));
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
				out.write(messages.getString("Webpage.Searchresult.order."+ordername));
				out.write("</option>");
			}
			out.write("   </select>&nbsp;&nbsp;&nbsp;");
			out.write("<input value=\""+messages.getString("Webpage.Searchresult.resort")+"\" type=\"submit\">");
			out.write("</form></div>");
	    }
	}

	
	private boolean isSorted(MCRQuery query, String attributename, String fieldname, int sortorder){
	   try { 
	    	Element sortField = (Element) query.buildXML().getRootElement().getChild("sortBy").getChildren("field").get(sortorder);
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
	private void writePageNavigation(JspWriter out, String id, int numHits, int numPerPage, int numPages, int page, String mask) throws IOException{
		String webBaseURL = MCRServlet.getBaseURL();
		ResourceBundle messages = PropertyResourceBundle.getBundle(languageBundleBase, new Locale(MCRSessionMgr
				.getCurrentSession().getCurrentLanguage()));
		out.write("<!-- Searchresult PageNavigation -->");
		out.write("\n<div class=\"searchresult-navigation\">");
		out.write("\n   <div class=\"hitcount\">");
		out.write(Integer.toString(numHits)+" "+messages.getString("Webpage.Searchresult.numHits"));
		out.write("   </div>");
		out.write("\n   <div class=\"navi\">");
		out.write("\n   <ol>");
		if(numPages>1){
			out.write("\n      <li class=\"item\"><a href=\""
					+webBaseURL+"/servlets/MCRJSPSearchServlet?mode=results&id="+id+"&page=1&numPerPage="+numPerPage
					+"\">"+messages.getString("Webpage.Searchresult.firstPage")+"</a></li>");
			if(page-2>0){
				out.write("\n      <li class=\"item\"><a href=\""
						+webBaseURL+"/servlets/MCRJSPSearchServlet?mode=results&id="+id+"&page="+Integer.toString(page-2)+"&numPerPage="+numPerPage
						+"\">["+Integer.toString((page-3)*numPerPage+1)+"-"+Integer.toString((page-2)*numPerPage)+"]</a></li>");
			}
			if(page-1>0){
				out.write("\n      <li class=\"item\"><a href=\""
						+webBaseURL+"/servlets/MCRJSPSearchServlet?mode=results&id="+id+"&page="+Integer.toString(page-1)+"&numPerPage="+numPerPage
						+"\">["+Integer.toString((page-2)*numPerPage+1)+"-"+Integer.toString((page-1)*numPerPage)+"]</a></li>");
			}
			out.write("\n      <li class=\"item active\">["+Integer.toString((page-1)*numPerPage+1)+"-"+Integer.toString(Math.min((page)*numPerPage, numHits))+"]</li>");
			if(page+1<=numPages){
				out.write("\n      <li><a href=\""
						+webBaseURL+"/servlets/MCRJSPSearchServlet?mode=results&id="+id+"&page="+Integer.toString(page+1)+"&numPerPage="+numPerPage
						+"\">["+Integer.toString((page)*numPerPage+1)+"-"+Integer.toString(Math.min((page+1)*numPerPage,numHits))+"]</a></li>");
			}
			if(page+2<=numPages){
				out.write("\n      <li class=\"item\"><a href=\""
						+webBaseURL+"/servlets/MCRJSPSearchServlet?mode=results&id="+id+"&page="+Integer.toString(page+2)+"&numPerPage="+numPerPage
						+"\">["+Integer.toString((page+1)*numPerPage+1)+"-"+Integer.toString(Math.min((page+2)*numPerPage,numHits))+"]</a></li>");
			}
			out.write("\n      <li class=\"item\"><a href=\""
					+webBaseURL+"/servlets/MCRJSPSearchServlet?mode=results&id="+id+"&page="+numPages+"&numPerPage="+numPerPage
					+"\">"+messages.getString("Webpage.Searchresult.lastPage")+"</a></li>");
		}
		out.write("\n   </ol></div>");
		out.write("\n   <div style=\"clear:both\"></div>");
		out.write("\n</div>");
	}

	public String getSortfields() {
		return sortfields;
	}

	public void setSortfields(String sortfields) {
		this.sortfields = sortfields;
	}
	
}
