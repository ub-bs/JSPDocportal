package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.filter.ElementFilter;
import org.jdom.output.DOMOutputter;
import org.mycore.common.JSPUtils;
import org.mycore.common.MCRConfiguration;
import org.mycore.frontend.jsp.format.MCRResultFormatter;


public class MCRSetResultListTag extends SimpleTagSupport
{
	private static MCRResultFormatter formatter;
	private int from;
	private int until;
	private String var;
	
	private String lang;
	private String objectType;
	
	private org.jdom.Document results;
	
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
	public void setLang(String inputLang) {
		lang = inputLang;
	}
	public void setResults(org.jdom.Document results) {
		this.results = results;
	}
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}	
	public void initialize() {
		formatter = (MCRResultFormatter) MCRConfiguration.instance().getSingleInstanceOf("MCR.ResultFormatter_class_name","org.mycore.frontend.jsp.format.MCRResultFormatter");
	}	
	
	public void doTag() throws JspException, IOException {
		if (formatter == null) initialize();

		PageContext pageContext = (PageContext) getJspContext();	
    
        if (results != null ) {
        	
        	/** mcr_results example - includes the query
        	<mcr:results xmlns:mcr="http://www.mycore.org/" id="1iljqgz8zqp6merg8xiel" sorted="false" numHits="1" numPerPage="0" numPages="1" page="1" mask="-">
        	  <mcr:hit id="atlibri_document_000000000014" host="local" />
        	  <condition format="text">title like "mehrwalk*"</condition>
        	  <condition format="xml">
        	    <condition field="title" operator="like" value="mehrwalk*" />
        	  </condition>
        	</mcr:results>
    		
    		/* the resultlist output
    		 * */    		 
    		org.jdom.Document resultContainer = formatter.getFormattedResultContainer(results.getRootElement(),lang, from, until);
    		
    		/* objecttype extrahieren */
			Iterator it = results.getRootElement().getDescendants(new ElementFilter("condition"));
			String ot = "";
			while( it.hasNext()){
				//nur den ersten Titelsatz!
				Element condition = (Element)it.next();
				if ( "objectType".equalsIgnoreCase(condition.getAttributeValue("field")) ) {
					ot += condition.getAttributeValue("value") +  " ";
				}
			}			
    		org.w3c.dom.Document domDoc = null;
    		try {
    			domDoc = new DOMOutputter().output(resultContainer);
    		} catch (JDOMException e) {
    			Logger.getLogger(MCRSetResultListTag.class).error("Domoutput failed: ", e);
    		}
    		
    		pageContext.setAttribute(var, domDoc);
    		pageContext.setAttribute(objectType, ot);
    		
    		
    		if(pageContext.getAttribute("debug") != null && pageContext.getAttribute("debug").equals("true")) {
    			JspWriter out = pageContext.getOut();
    			StringBuffer debugSB = new StringBuffer("<textarea cols=\"80\" rows=\"30\">")
    				.append("found this result container:\r\n")
    				.append(JSPUtils.getPrettyString(resultContainer))
    				.append("--------------------\r\nfor the query in the result container\r\n")
    				.append(JSPUtils.getPrettyString(results))
    				.append("</textarea>");
    			out.println(debugSB.toString());
    		}
    		
        }
		return;
	}	

}