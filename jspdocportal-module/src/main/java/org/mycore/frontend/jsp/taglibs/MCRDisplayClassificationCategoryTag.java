package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;
import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.mycore.backend.hibernate.MCRHIBConnection;
import org.mycore.datamodel.classifications2.MCRCategoryDAO;
import org.mycore.datamodel.classifications2.MCRCategoryDAOFactory;
import org.mycore.datamodel.classifications2.MCRCategoryID;

public class MCRDisplayClassificationCategoryTag extends SimpleTagSupport
{
	private static MCRCategoryDAO categoryDAO = MCRCategoryDAOFactory.getInstance();
	private static Logger LOGGER = Logger.getLogger(MCRDisplayClassificationCategoryTag.class.getName());
	
	private String lang;
	private String classid;
	private String categid;
	
	
	public void doTag() throws JspException, IOException {
		try{
			String text = "";
			Transaction tx  = MCRHIBConnection.instance().getSession().getTransaction();
	   		if(tx==null || tx.getStatus() != TransactionStatus.ACTIVE){
				Transaction t1 = MCRHIBConnection.instance().getSession().beginTransaction();
				text = categoryDAO.getCategory(new MCRCategoryID(classid, categid), 0).getLabel(lang).get().getText();
				t1.commit();
			}
	   		else{
	   			text = categoryDAO.getCategory(new MCRCategoryID(classid, categid), 0).getLabel(lang).get().getText();
	   		}
			getJspContext().getOut().write(text);
		}catch(Exception e){
			LOGGER.error("could not check access", e);
		}
	}
	
	public void setLang(String lang) {
		this.lang = lang;
	}
	public void setClassid(String classid) {
		this.classid = classid;
	}
	public void setCategid(String categid) {
		this.categid = categid;
	}	

}