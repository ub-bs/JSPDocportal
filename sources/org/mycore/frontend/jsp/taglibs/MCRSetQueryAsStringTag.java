package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;

import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.output.XMLOutputter;



public class MCRSetQueryAsStringTag extends SimpleTagSupport
{
	private Document jdom;
	private String var;
	
	public void setVar(String inputVar) {
		var = inputVar;
		return;
	}
	public void setJdom(Document inputJdom) {
		jdom = inputJdom;
	}

	public void doTag() throws JspException, IOException {
		PageContext pageContext = (PageContext) getJspContext();	
		
		XMLOutputter output = new XMLOutputter(org.jdom.output.Format.getRawFormat());
		String str = output.escapeAttributeEntities(output.escapeElementEntities(output.outputString(jdom)));		
		
		pageContext.setAttribute(var, str);
        
        return;
	}	

}