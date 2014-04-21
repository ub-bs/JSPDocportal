package org.mycore.frontend.workflowengine.jbpm.bundle;
import java.util.Iterator;

import org.jbpm.context.exe.ContextInstance;
import org.jdom2.Element;
import org.jdom2.filter.ElementFilter;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowConstants;
import org.mycore.frontend.workflowengine.strategies.MCRDefaultMetadataStrategy;

public class MCRBundleMetadataStrategy extends MCRDefaultMetadataStrategy {
	//private static Logger logger = Logger.getLogger(MCRDisshabMetadataStrategy.class.getName());
	public MCRBundleMetadataStrategy(){
		super("bundle");
	}
	
	public void setWorkflowVariablesFromMetadata(ContextInstance ctxI, Element metadata) {
		StringBuffer sbTitle = new StringBuffer("");
		for(Iterator<Element> it = metadata.getDescendants(new ElementFilter("title")); it.hasNext();){
			Element title = it.next();
			if(title.getParentElement().getName().equals("titles") && title.getAttributeValue("type").equals("original-main"))
				sbTitle.append(title.getText());
		}
		ctxI.setVariable("wfo-title", sbTitle.toString());	
		
		StringBuffer sbAuthorNames = new StringBuffer("");
		for(Iterator<Element> it = metadata.getDescendants(new ElementFilter("creator")); it.hasNext();){
				Element creator = it.next();
				sbAuthorNames.append(creator.getText());
				if(it.hasNext()){ sbAuthorNames.append("; ");}
			}	
		ctxI.setVariable(MCRWorkflowConstants.WFM_VAR_AUTHOR_NAMES, sbAuthorNames.toString());
	}
}
