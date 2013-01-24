package org.mycore.frontend.workflowengine.jbpm.institution;
import org.apache.log4j.Logger;
import org.jbpm.context.exe.ContextInstance;
import org.jdom2.Element;
import org.mycore.common.MCRException;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowConstants;
import org.mycore.frontend.workflowengine.strategies.MCRDefaultMetadataStrategy;

public class MCRInstitutionMetadataStrategy extends MCRDefaultMetadataStrategy {
	private static Logger logger = Logger.getLogger(MCRInstitutionMetadataStrategy.class.getName());
	public MCRInstitutionMetadataStrategy(){
		super("institution");
	}
	
		
	public void setWorkflowVariablesFromMetadata(ContextInstance ctxI, Element metadata) {
		try {
			ctxI.setVariable(MCRWorkflowConstants.WFM_VAR_WFOBJECT_TITLE, createWFOTitlefromMetadata(metadata));		
		} catch (MCRException ex) {
			logger.error("catched error", ex);
		} finally {
		}
	}
	
	private String createWFOTitlefromMetadata(Element metadata){
		Element name = metadata.getChild("names").getChild("name");
		
		return name.getChildText("fullname");
	}
}
