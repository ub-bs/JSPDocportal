package org.mycore.frontend.workflowengine.jbpm.series;
import java.util.Iterator;

import org.jbpm.context.exe.ContextInstance;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowConstants;
import org.mycore.frontend.workflowengine.strategies.MCRDefaultMetadataStrategy;

public class MCRSeriesMetadataStrategy extends MCRDefaultMetadataStrategy {
	//private static Logger logger = Logger.getLogger(MCRDisshabMetadataStrategy.class.getName());
	public MCRSeriesMetadataStrategy(){
		super("series");
	}
	
	public void setWorkflowVariablesFromMetadata(ContextInstance ctxI, Element metadata) {
		StringBuffer sbTitle = new StringBuffer("");
		for(Iterator it = metadata.getDescendants(new ElementFilter("title")); it.hasNext();){
			Element title = (Element)it.next();
			if(title.getAttributeValue("type").equals("original-main"))
				sbTitle.append(title.getText());
		}
		ctxI.setVariable("wfo-title", sbTitle.toString());	
		
		StringBuffer sbAuthorNames = new StringBuffer("");
		for(Iterator it = metadata.getDescendants(new ElementFilter("creator")); it.hasNext();){
				Element creator = (Element)it.next();
				sbAuthorNames.append(creator.getText());
				if(it.hasNext()){ sbAuthorNames.append("; ");}
			}	
		ctxI.setVariable(MCRWorkflowConstants.WFM_VAR_AUTHOR_NAMES, sbAuthorNames.toString());
	}
}