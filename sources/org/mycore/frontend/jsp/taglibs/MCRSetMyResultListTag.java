package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;

import org.apache.log4j.Logger;
import org.jdom.JDOMException;
import org.jdom.output.DOMOutputter;
import org.mycore.access.MCRAccessManagerBase;
import org.mycore.access.MCRAccessManagerBase;
import org.mycore.backend.query.MCRQueryManager;
import org.mycore.common.JSPUtils;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRSession;
import org.mycore.common.MCRSessionMgr;
import org.mycore.frontend.jsp.format.MCRResultFormatter;
import org.mycore.services.fieldquery.MCRHit;
import org.mycore.services.fieldquery.MCRResults;


public class MCRSetMyResultListTag extends SimpleTagSupport
{
	private static MCRResultFormatter formatter;
	private static MCRAccessManagerBase AM = (MCRAccessManagerBase) MCRConfiguration.instance().getInstanceOf("MCR.Access_class_name");
	private int from;
	private int until;
	private String var;
	private String navPath;
	private String lang;
	private org.jdom.Document query;
	private String resultlistType;
	
	public void setNavPath(String inputNavPath) {
		navPath = inputNavPath;
		return;
	}
	public void setVar(String inputVar) {
		var = inputVar;
		return;
	}
	public void setFrom(int inputFrom) {
		from = inputFrom;
	}
	public void setUntil(int inputUntil) {
		until = inputUntil;
	}	
	public void setQuery(org.jdom.Document inputQuery) {
		query = inputQuery;
	}
	public void setResultlistType(String inputResultlistType) {
		resultlistType = inputResultlistType ;
	}
	public void setLang(String inputLang) {
		lang = inputLang;
	}
	public void initialize() {
		formatter = (MCRResultFormatter) MCRConfiguration.instance().getSingleInstanceOf("MCR.ResultFormatter_class_name","org.mycore.frontend.jsp.format.MCRResultFormatter");
	}	
	public void doTag() throws JspException, IOException {
		if (formatter == null) initialize();

		PageContext pageContext = (PageContext) getJspContext();		
        HttpSession session = pageContext.getSession();

        MCRResults result = null;
        if (query != null && resultlistType != null) {
        	session.setAttribute(navPath + "-jdomQuery", query);
        	session.setAttribute(navPath + "-resultlistType", resultlistType);
        } else{
        	query = (org.jdom.Document) session.getAttribute(navPath + "-jdomQuery");
        	resultlistType = (String) session.getAttribute(navPath + "-resultlistType");
        	pageContext.setAttribute("resultlistType",resultlistType);
        	pageContext.setAttribute("query",query);
        }

        session.setAttribute("lastSearchListPath",navPath);        
        
        if (query != null && resultlistType != null) {
            MCRResults allresult = null;            
        	allresult = MCRQueryManager.getInstance().search(query);
            allresult.setComplete();            
            result = new MCRResults();
            MCRSession mcrSession = MCRSessionMgr.getCurrentSession();
            for (int i=0; i< allresult.getNumHits(); i++){
            	MCRHit myhit = allresult.getHit(i);            	
    			if (AM.checkAccess(myhit.getID(), "modify", mcrSession  )) {
    				result.addHit(myhit);    				
    			}    			
            }
            result.setComplete();
            if (result.getNumHits() <= 100) {
                session.setAttribute("lastMCRResults",result);
            }else {
                session.setAttribute("lastMCRResults",null);
            }            
    		org.jdom.Document resultContainer = formatter.getFormattedResultContainer(result,lang, from, until);
    		org.w3c.dom.Document domDoc = null;
    		try {
    			domDoc = new DOMOutputter().output(resultContainer);
    		} catch (JDOMException e) {
    			Logger.getLogger(MCRSetResultListTag.class).error("Domoutput failed: ", e);
    		}
    		if(pageContext.getAttribute("debug") != null && pageContext.getAttribute("debug").equals("true")) {
    			JspWriter out = pageContext.getOut();
    			StringBuffer debugSB = new StringBuffer("<textarea cols=\"80\" rows=\"30\">")
    				.append("found this result container:\r\n")
    				.append(JSPUtils.getPrettyString(resultContainer))
    				.append("--------------------\r\nfor the query\r\n")
    				.append(JSPUtils.getPrettyString(query))
    				.append("</textarea>");
    			out.println(debugSB.toString());
    		}
    		pageContext.setAttribute(var, domDoc);
        }
		return;
	}	

}
