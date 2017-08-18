package org.mycore.frontend.jsp.taglibs;

import java.io.File;
import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.output.DOMOutputter;
import org.mycore.activiti.MCRActivitiUtils;
import org.mycore.datamodel.metadata.MCRMetadataManager;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.jsp.MCRHibernateTransactionWrapper;

public class MCRReceiveMcrObjAsJdomTag extends SimpleTagSupport
{
	private static Logger logger = LogManager.getLogger(MCRReceiveMcrObjAsJdomTag.class);
	
	private String mcrid;
	private String var;
	private String varDom;
	private boolean fromWF=false;
	
	public void setMcrid(String mcrid) {
		this.mcrid = mcrid;
	}
	
	public void setVarDom(String varDom){
		this.varDom = varDom; 
	}
	
	public void setVar(String var) {
		this.var = var;
	}
	
	public void setFromWF(boolean b){
		fromWF = b;
	}
	
	public void doTag() throws JspException, IOException {
		try (MCRHibernateTransactionWrapper htw = new MCRHibernateTransactionWrapper()) {
    		org.mycore.datamodel.metadata.MCRObject mcr_obj = new org.mycore.datamodel.metadata.MCRObject();
			if (fromWF) {
				File savedir = MCRActivitiUtils.getWorkflowDirectory(MCRObjectID.getInstance(mcrid));
				File file = new File(savedir, mcrid+".xml");
				if (file.isFile()) {
					mcr_obj = new MCRObject(file.toURI());
				}
			} else {
				mcr_obj = MCRMetadataManager.retrieveMCRObject(MCRObjectID.getInstance(mcrid));
			}
			PageContext pageContext = (PageContext) getJspContext();
			org.jdom2.Document docJdom = mcr_obj.createXML();
			if(var != null && !var.equals("")) {
				pageContext.setAttribute(var, docJdom);
			}
			if (varDom != null && !varDom.equals("")) {
		    	org.w3c.dom.Document domDoc = null;
	    		domDoc =  new DOMOutputter().output( mcr_obj.createXML());
	    		pageContext.setAttribute(varDom, domDoc);
			}		
    	} catch (Exception e) {
    		logger.error("error in receiving mcr_obj as jdom and dom for " + mcrid, e);
    	}
    }	
}