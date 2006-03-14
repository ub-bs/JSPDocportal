package org.mycore.frontend.jsp.taglibs;

import java.io.File;
import java.io.IOException;

import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;

import org.apache.log4j.Logger;
import org.jdom.output.DOMOutputter;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerFactory;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerInterface;

public class MCRReceiveMcrObjAsJdomTag extends SimpleTagSupport
{
	private static Logger logger = Logger.getLogger(MCRReceiveMcrObjAsJdomTag.class);
	private static MCRWorkflowEngineManagerInterface defaultWFI = MCRWorkflowEngineManagerFactory.getDefaultImpl();
	
	private String mcrid;
	private String var;
	private String varDom;
	private String fromWForDB;
	
	public void setMcrid(String mcrid) {
		this.mcrid = mcrid;
	}
	
	public void setVarDom(String varDom){
		this.varDom = varDom; 
	}
	
	public void setVar(String var) {
		this.var = var;
	}
	
	public void setFromWForDB(String fromWForDB) {
		this.fromWForDB = fromWForDB;
	}
	
	public void doTag() throws JspException, IOException {
    	try {
			org.mycore.datamodel.metadata.MCRObject mcr_obj = new org.mycore.datamodel.metadata.MCRObject();
			if ( fromWForDB != null && fromWForDB.equals("workflow") ) {
				String[] mcridParts = mcrid.split("_");
				String savedir = defaultWFI.getWorkflowDirectory(mcridParts[1]);
				String filename = savedir + "/" + mcrid + ".xml";			
				File file = new File(filename);
				if (file.isFile()) {
					mcr_obj.setFromURI(file.getAbsolutePath());
				}
			} else {
				mcr_obj.receiveFromDatastore(mcrid);
			}
			PageContext pageContext = (PageContext) getJspContext();
			org.jdom.Document docJdom = mcr_obj.createXML();
			if(var != null && !var.equals("")) {
				pageContext.setAttribute(var, docJdom);
			}
			if (varDom != null && !varDom.equals("")) {
		    	org.w3c.dom.Document domDoc = null;
	    		domDoc =  new DOMOutputter().output( mcr_obj.createXML());
	    		pageContext.setAttribute(varDom, domDoc);
			}
    	} catch (Exception e) {
    		logger.error("error in receiving mcr_obj for jdom and dom", e);
    	}
	}	

}