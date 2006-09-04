package org.mycore.frontend.workflowengine.jbpm.publication;
import org.jbpm.context.exe.ContextInstance;
import org.jdom.Element;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRSessionMgr;
import org.mycore.datamodel.classifications.MCRCategoryItem;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowConstants;
import org.mycore.frontend.workflowengine.strategies.MCRDefaultMetadataStrategy;

public class MCRDocumentMetadataStrategy extends MCRDefaultMetadataStrategy {
	//private static Logger logger = Logger.getLogger(MCRDocumentMetadataStrategy.class.getName());
	public MCRDocumentMetadataStrategy(){
		super("document");
	}
	
	public void setWorkflowVariablesFromMetadata(ContextInstance ctxI, Element metadata) {
		super.setWorkflowVariablesFromMetadata(ctxI, metadata);
		String publicationType = (String) ctxI.getVariable(MCRWorkflowConstants.WFM_VAR_METADATA_PUBLICATIONTYPE);			
		if ( publicationType != null) {
			String clid = MCRConfiguration.instance().getString("MCR.ClassificationID.Type");
			MCRCategoryItem clItem = MCRCategoryItem.getCategoryItem(clid,publicationType);
			String label = clItem.getDescription(MCRSessionMgr.getCurrentSession().getCurrentLanguage());
			ctxI.setVariable("wfo-type", label);
		}
	}
}
