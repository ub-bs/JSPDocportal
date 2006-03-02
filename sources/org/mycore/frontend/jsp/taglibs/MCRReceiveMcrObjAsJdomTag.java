package org.mycore.frontend.jsp.taglibs;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;

import org.apache.log4j.Logger;
import org.jdom.JDOMException;
import org.jdom.output.DOMOutputter;
import org.mycore.common.MCRConfiguration;
import org.mycore.frontend.jsp.format.MCRResultFormatter;


public class MCRReceiveMcrObjAsJdomTag extends SimpleTagSupport
{
	private String mcrid;
	private String var;
	private String fromWForDB;
	
	public void setMcrid(String inputID) {
		mcrid = inputID;
		return;
	}
	public void setVar(String inputVar) {
		var = inputVar;
		return;
	}
	
	public void setFromWForDB(String inputFromWForDB) {
		fromWForDB = inputFromWForDB;
	}
	
	public void doTag() throws JspException, IOException {
		org.mycore.datamodel.metadata.MCRObject mcr_obj = new org.mycore.datamodel.metadata.MCRObject();
		if ( fromWForDB != null && fromWForDB.equals("workflow") ) {
			MCRConfiguration CONFIG = MCRConfiguration.instance();
			String[] mcridParts = mcrid.split("_");
			String savedir = CONFIG.getString("MCR.editor_" + mcridParts[1] + "_directory");
			String filename = savedir + "/" + mcrid + ".xml";			
			File file = new File(filename);
			if (file.isFile()) {
				mcr_obj.setFromURI(file.getAbsolutePath());
			}
		} else {
			mcr_obj.receiveFromDatastore(mcrid);
		}
		PageContext pageContext = (PageContext) getJspContext();
		pageContext.setAttribute(var, mcr_obj.createXML());
		return;
	}	

}