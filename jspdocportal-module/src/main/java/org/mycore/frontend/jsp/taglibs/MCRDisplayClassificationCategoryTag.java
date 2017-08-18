package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mycore.datamodel.classifications2.MCRCategoryDAO;
import org.mycore.datamodel.classifications2.MCRCategoryDAOFactory;
import org.mycore.datamodel.classifications2.MCRCategoryID;
import org.mycore.frontend.jsp.MCRHibernateTransactionWrapper;

public class MCRDisplayClassificationCategoryTag extends SimpleTagSupport
{
	private static MCRCategoryDAO categoryDAO = MCRCategoryDAOFactory.getInstance();
	private static Logger LOGGER = LogManager.getLogger(MCRDisplayClassificationCategoryTag.class.getName());
	
	private String lang;
	private String classid;
	private String categid;
	
	
	public void doTag() throws JspException, IOException {
	    try(MCRHibernateTransactionWrapper mtw = new MCRHibernateTransactionWrapper()){
	   		String text = categoryDAO.getCategory(new MCRCategoryID(classid, categid), 0).getLabel(lang).get().getText();
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