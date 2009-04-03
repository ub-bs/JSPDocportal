package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.output.DOMOutputter;
import org.mycore.common.JSPUtils;
import org.mycore.common.MCRSession;
import org.mycore.common.MCRSessionMgr;
import org.mycore.datamodel.classifications.MCRClassificationBrowserData;


public class MCRSetClassBrowserTreeTag extends SimpleTagSupport
{
	private String actUriPath;
	private String browserClass;
	private String var;	
	private String lang;

	
	public void setVar(String inputVar) {
		var = inputVar;
		return;
	}
	
	public void setActUriPath(String actUriPath) {
		this.actUriPath = actUriPath;
	}
	
	public void setBrowserClass(String browserClass) {
		this.browserClass = browserClass;
	}
	
	public void setLang(String lang) {
		this.lang = lang;
	}

	
	public void doTag() throws JspException, IOException {
		PageContext pageContext = (PageContext) getJspContext();	
    	org.w3c.dom.Document domDoc = null;

    	if (actUriPath.length() == 0) actUriPath = "/" + browserClass;
	    MCRSession mcrSession = MCRSessionMgr.getCurrentSession();
	    
	    try {
	        mcrSession.BData = new MCRClassificationBrowserData(actUriPath,"","","");
	        
		    Document doc = mcrSession.BData.createXmlTree(lang);
		    
   			domDoc = new DOMOutputter().output(doc);
   	   		if((pageContext.getAttribute("debug") != null && pageContext.getAttribute("debug").equals("true")) ||
   	   	       (pageContext.getRequest().getParameter("debug") != null && pageContext.getRequest().getParameter("debug").equals("true"))){
   	   			JspWriter out = pageContext.getOut();
   	   			StringBuffer debugSB = new StringBuffer("<textarea cols=\"80\" rows=\"30\">")
   	   				.append("found this result container:\r\n")
   	   				.append(JSPUtils.getPrettyString(doc))
   	   				.append("</textarea>");
   	   			out.println(debugSB.toString());
   	   		}
   		} catch (Exception e) {
   			Logger.getLogger(MCRSetResultListTag.class).error("Domoutput failed: ", e);
   		}    		
   		pageContext.setAttribute(var, domDoc);
    		    		
		return;
	}	

}