package org.mycore.frontend.jsp.taglibs;

import java.io.File;
import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;
import org.hibernate.Transaction;
import org.jdom2.output.DOMOutputter;
import org.mycore.backend.hibernate.MCRHIBConnection;
import org.mycore.datamodel.common.MCRXMLMetadataManager;
import org.mycore.datamodel.metadata.MCRMetadataManager;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.workflowengine.strategies.MCRWorkflowDirectoryManager;

public class MCRReceiveMcrObjAsJdomTag extends SimpleTagSupport
{
	private static Logger logger = Logger.getLogger(MCRReceiveMcrObjAsJdomTag.class);
	
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
		Transaction t1=null;
		try {
    		Transaction tx  = MCRHIBConnection.instance().getSession().getTransaction();
	   		if(tx==null || !tx.isActive()){
				t1 = MCRHIBConnection.instance().getSession().beginTransaction();
			}
			org.mycore.datamodel.metadata.MCRObject mcr_obj = new org.mycore.datamodel.metadata.MCRObject();
			if (fromWF) {
				String[] mcridParts = mcrid.split("_");
				String savedir = MCRWorkflowDirectoryManager.getWorkflowDirectory(mcridParts[1]);
				String filename = savedir + "/" + mcrid + ".xml";			
				File file = new File(filename);
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
			if(tx==null || !tx.isActive()){
				t1.commit();
			}
    	} catch (Exception e) {
    		logger.error("error in receiving mcr_obj for jdom and dom", e);
    	}
    	finally{
    		if(t1!=null && t1.isActive()){
    			t1.commit();
    		}
    	}
	}	

}