package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;

import org.apache.log4j.Logger;
import org.jdom.JDOMException;
import org.jdom.output.DOMOutputter;
import org.mycore.common.JSPUtils;
import org.mycore.frontend.jsp.query.MCRResultFormatter;


public class MCRDocDetailsTag extends SimpleTagSupport
{
	private org.jdom.Document mcrObj;
	private String var;
	private String lang;
	
	public void setMcrObj(org.jdom.Document inputDoc) {
		mcrObj = inputDoc;
		return;
	}
	public void setLang(String inputLang) {
		lang = inputLang;
		return;
	}
	public void setVar(String inputVar) {
		var = inputVar;
		return;
	}	
	public void doTag() throws JspException, IOException {
		org.jdom.Document allMetaValues = MCRResultFormatter.getFormattedDocDetails(mcrObj,lang);
		org.w3c.dom.Document domDoc = null;
		try {
			domDoc = new DOMOutputter().output(allMetaValues);
		} catch (JDOMException e) {
			Logger.getLogger(MCRDocDetailsTag.class).error("Domoutput failed: ", e);
		}
		PageContext pageContext = (PageContext) getJspContext();		
		pageContext.setAttribute(var, domDoc);
        JspFragment body = getJspBody();
        JspWriter out = pageContext.getOut();
		if(pageContext.getAttribute("debug") != null && pageContext.getAttribute("debug").equals("true")) {
			StringBuffer debugSB = new StringBuffer("<textarea cols=\"80\" rows=\"30\">")
				.append("MCROBJ:\r\n").append(JSPUtils.getPrettyString(mcrObj))
				.append("\r\n--------------------\r\n")
				.append("formatted Content\r\n")
				.append(JSPUtils.getPrettyString(allMetaValues))
				.append("</textarea>");
			out.println(debugSB.toString());
		}        
        try {
            StringWriter stringWriter = new StringWriter();
            body.invoke(stringWriter);
            out.println(stringWriter);
            
        } catch (Exception e) {
        	Logger.getLogger(MCRDocDetailsTag.class).error("catched error: ", e);
        } 		
		return;
	}	

}