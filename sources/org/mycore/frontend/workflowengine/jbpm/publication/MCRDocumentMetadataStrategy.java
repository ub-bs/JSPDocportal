package org.mycore.frontend.workflowengine.jbpm.publication;
import java.util.Iterator;

import org.jbpm.context.exe.ContextInstance;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRSessionMgr;
import org.mycore.datamodel.classifications2.MCRCategoryDAOFactory;
import org.mycore.datamodel.classifications2.MCRCategoryID;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowConstants;
import org.mycore.frontend.workflowengine.strategies.MCRDefaultMetadataStrategy;

public class MCRDocumentMetadataStrategy extends MCRDefaultMetadataStrategy {
	//private static Logger logger = Logger.getLogger(MCRDocumentMetadataStrategy.class.getName());
	public MCRDocumentMetadataStrategy(){
		super("document");
	}
	
	public void setWorkflowVariablesFromMetadata(ContextInstance ctxI, Element metadata) {
		super.setWorkflowVariablesFromMetadata(ctxI, metadata);
        StringBuffer sbTitle = new StringBuffer("");
        for(Iterator it = metadata.getDescendants(new ElementFilter("title")); it.hasNext();){
            Element title = (Element)it.next();
            if(title.getAttributeValue("type").equals("main"))
                sbTitle.append(title.getText());
                break;
        }
        
        ctxI.setVariable("wfo-title", sbTitle.toString());  
        String publicationType = (String) ctxI.getVariable(MCRWorkflowConstants.WFM_VAR_METADATA_PUBLICATIONTYPE);			
		if ( publicationType != null) {
			String clid = MCRConfiguration.instance().getString("MCR.ClassificationID.Type");
			String label = MCRCategoryDAOFactory.getInstance().getCategory(new MCRCategoryID(clid, publicationType), 0).getLabels().get(MCRSessionMgr.getCurrentSession().getCurrentLanguage()).getDescription();
			ctxI.setVariable("wfo-type", label);
		}
	}
}
