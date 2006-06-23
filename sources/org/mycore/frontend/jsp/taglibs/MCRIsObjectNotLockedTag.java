package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;
import org.mycore.common.MCRConfiguration;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowConstants;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManager;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManagerFactory;

public class MCRIsObjectNotLockedTag extends SimpleTagSupport
{
	private String var;
	private String objectid;
	
	private static Logger LOGGER = Logger.getLogger(MCRIsObjectNotLockedTag.class);

	public void setVar(String inputVar) {
		var = inputVar;
		return;
	}
	
	public void setObjectid(String objectid) {
		this.objectid = objectid;
	}
	
	public void doTag() throws JspException, IOException {
		try{
			boolean bhasAccess = false;
			PageContext pageContext = (PageContext) getJspContext();
			MCRWorkflowManager	WFM = null; 
			String type = objectid.split("_")[1];
			String workflowTypes[] = (MCRConfiguration.instance().getString("MCR.WorkflowEngine.WorkflowTypes")).split(",");
			for ( int i = 0; i< workflowTypes.length; i++ ) {
				workflowTypes[i]=workflowTypes[i].trim();
				WFM = MCRWorkflowManagerFactory.getImpl(workflowTypes[i]);
				if ( WFM != null && WFM.getMainDocumentType().equalsIgnoreCase(type)) {
					List lpids = WFM.getCurrentProcessIDsForVariable(MCRWorkflowConstants.WFM_VAR_METADATA_OBJECT_IDS, objectid);
					if( lpids == null || lpids.size() == 0 ){
						// not in use by another process
						bhasAccess = true;
						break;
					}						
				}
				
			}
			pageContext.setAttribute(var, new Boolean(bhasAccess));
			return;
		}catch(Exception e){
			LOGGER.error("could not check access", e);
		}
	}	

}