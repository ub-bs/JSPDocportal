package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;
import org.mycore.common.MCRPersistenceException;
import org.mycore.datamodel.metadata.MCRBase;
import org.mycore.datamodel.metadata.MCRMetadataManager;
import org.mycore.datamodel.metadata.MCRObjectID;

public class MCRIsObjectNotLockedTag extends SimpleTagSupport
{
	private String var;
	private String mcrObjectID;
	
	private static Logger LOGGER = Logger.getLogger(MCRIsObjectNotLockedTag.class);

	public void setVar(String inputVar) {
		var = inputVar;
		return;
	}
	
	public void setMcrObjectID(String mcrObjectID) {
		this.mcrObjectID = mcrObjectID;
	}
	
	public void doTag() throws JspException, IOException {
		PageContext pageContext = (PageContext) getJspContext();
		boolean bhasAccess = true;
		try{
				MCRBase mcrBase = MCRMetadataManager.retrieve(MCRObjectID.getInstance(mcrObjectID));
				if(mcrBase.getService().getFlags("status").size()>0){
					for(String s: mcrBase.getService().getFlags("status")){
						if(s.equals("ok")){
							bhasAccess &= true;
						}
						else{
							bhasAccess &= false;
						}
					}
				}
				else{
					bhasAccess=true;
				}
			}
			catch(MCRPersistenceException e){
				LOGGER.debug(e.getMessage());
			}

			
			pageContext.setAttribute(var, new Boolean(bhasAccess));
			return;
		
	}	

}