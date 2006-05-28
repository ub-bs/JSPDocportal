package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;
import org.jdom.JDOMException;
import org.jdom.output.DOMOutputter;
import org.mycore.common.JSPUtils;
import org.mycore.common.MCRConfiguration;
import org.mycore.frontend.jsp.format.MCRResultFormatter;


public class MCRDocDetailsTag extends SimpleTagSupport
{
	private static MCRResultFormatter formatter;
	private org.jdom.Document mcrObj;
	private String var;
	private String lang;
	private String style;
	
	public void setStyle(String style) {
		this.style = style;
	}
	
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
	public void initialize() {
		formatter = (MCRResultFormatter) MCRConfiguration.instance().getSingleInstanceOf("MCR.ResultFormatter_class_name","org.mycore.frontend.jsp.format.MCRResultFormatter");
	}
	public void doTag() throws JspException, IOException {
		if (formatter == null) initialize();
		
		org.jdom.Document allMetaValues = formatter.getFormattedDocDetails(mcrObj,lang,style);
		org.w3c.dom.Document domDoc = null;
		try {
			domDoc = new DOMOutputter().output(allMetaValues);
		} catch (JDOMException e) {
			Logger.getLogger(MCRDocDetailsTag.class).error("Domoutput failed: ", e);
		}
		PageContext pageContext = (PageContext) getJspContext();		
		pageContext.setAttribute(var, domDoc);
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
//        try {
//            JspFragment body = getJspBody();
//            StringWriter stringWriter = new StringWriter();
//            body.invoke(stringWriter);
//            out.println(stringWriter);
//            
//        } catch (Exception e) {
//        	Logger.getLogger(MCRDocDetailsTag.class).error("catched error: ", e);
//        } 		
		return;
	}	

}