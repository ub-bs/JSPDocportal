package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;

import org.apache.log4j.Logger;
import org.jdom.JDOMException;
import org.jdom.output.DOMOutputter;
import org.mycore.frontend.jsp.query.MCRResultFormatter;


public class MCRReceiveMcrObjAsJdomTag extends SimpleTagSupport
{
	private String mcrid;
	private String var;
	
	public void setMcrid(String inputID) {
		mcrid = inputID;
		return;
	}
	public void setVar(String inputVar) {
		var = inputVar;
		return;
	}
	public void doTag() throws JspException, IOException {
		org.mycore.datamodel.metadata.MCRObject mcr_obj = new org.mycore.datamodel.metadata.MCRObject();
		mcr_obj.receiveFromDatastore(mcrid);
		PageContext pageContext = (PageContext) getJspContext();
		pageContext.setAttribute(var, mcr_obj.createXML());
		return;
	}	

}