package org.mycore.frontend.workflowengine.jbpm.thesis;
import java.util.Iterator;

import org.jbpm.context.exe.ContextInstance;
import org.jdom2.Element;
import org.jdom2.filter.ElementFilter;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowConstants;
import org.mycore.frontend.workflowengine.strategies.MCRDefaultMetadataStrategy;

public class MCRThesisMetadataStrategy extends MCRDefaultMetadataStrategy {
	//private static Logger logger = Logger.getLogger(MCRDisshabMetadataStrategy.class.getName());
	public MCRThesisMetadataStrategy(){
		super("thesis");
	}
	
	public void setWorkflowVariablesFromMetadata(ContextInstance ctxI, Element metadata) {
		StringBuffer sbTitle = new StringBuffer("");
		for(Iterator<Element> it = metadata.getDescendants(new ElementFilter("title")); it.hasNext();){
			Element title = it.next();
			if(title.getParentElement().getName().equals("titles") && title.getAttributeValue("type").equals("original-main"))
				sbTitle.append(title.getText());
		}
        if(sbTitle.length()>200){
        	ctxI.setVariable("wfo-title", sbTitle.toString().substring(0,200)+"...");	
        }
        else{
        	ctxI.setVariable("wfo-title", sbTitle.toString());
        }
			
		
		StringBuffer sbAuthorNames = new StringBuffer("");
		for(Iterator<Element> it = metadata.getDescendants(new ElementFilter("creator")); it.hasNext();){
				Element creator = it.next();
				sbAuthorNames.append(creator.getText());
				if(it.hasNext()){ sbAuthorNames.append("; ");}
			}	
		ctxI.setVariable(MCRWorkflowConstants.WFM_VAR_AUTHOR_NAMES, sbAuthorNames.toString());
		
		StringBuffer sbURN = new StringBuffer("");
		for(Iterator<Element> it = metadata.getDescendants(new ElementFilter("urn")); it.hasNext();){
			Element e = it.next();
			sbURN.append(e.getText());
			//should not happen!!!
			if(it.hasNext()){ sbURN.append(", ");}
		}
		ctxI.setVariable(MCRWorkflowConstants.WFM_VAR_RESERVATED_URN, sbURN.toString());
	}
}
