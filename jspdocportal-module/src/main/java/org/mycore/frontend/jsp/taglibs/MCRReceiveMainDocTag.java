package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;
import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.mycore.backend.hibernate.MCRHIBConnection;
import org.mycore.datamodel.ifs.MCRDirectory;
import org.mycore.datamodel.ifs.MCRFilesystemNode;

public class MCRReceiveMainDocTag extends SimpleTagSupport
{
	private static Logger logger = Logger.getLogger(MCRReceiveMainDocTag.class);
	
	private String derid;
	private String var;
	
	public void setDerid(String derid) {
		this.derid = derid;
	}
	
	public void setVar(String var) {
		this.var = var;
	}
	
	
	public void doTag() throws JspException, IOException {
		Transaction t1=null;
		try {
    		Transaction tx  = MCRHIBConnection.instance().getSession().getTransaction();
	   		if(tx==null || tx.getStatus() != TransactionStatus.ACTIVE){
				t1 = MCRHIBConnection.instance().getSession().beginTransaction();
			}
	   		
	   		MCRDirectory root = MCRDirectory.getRootDirectory(derid);
			MCRFilesystemNode[] myfiles = root.getChildren(MCRDirectory.SORT_BY_NAME);//getChildren();
			if(myfiles.length>0){
				PageContext pageContext = (PageContext) getJspContext();
				pageContext.setAttribute(var, myfiles[0].getName());
			}
			if(tx==null || tx.getStatus() != TransactionStatus.ACTIVE){
				t1.commit();
			}
    	} catch (Exception e) {
    		logger.error("error in receiving mcr_obj for jdom and dom", e);
    	}
    	finally{
    		if(t1!=null){
    			t1.commit();
    		}
    	}
	}	

}