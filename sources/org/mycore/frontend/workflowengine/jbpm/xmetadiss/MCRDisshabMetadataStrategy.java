package org.mycore.frontend.workflowengine.jbpm.xmetadiss;
import java.util.Iterator;

import org.jbpm.context.exe.ContextInstance;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.filter.ElementFilter;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowConstants;
import org.mycore.frontend.workflowengine.strategies.MCRDefaultMetadataStrategy;

public class MCRDisshabMetadataStrategy extends MCRDefaultMetadataStrategy {
	//private static Logger logger = Logger.getLogger(MCRDisshabMetadataStrategy.class.getName());
	public MCRDisshabMetadataStrategy(){
		super("disshab");
	}
	
	public void setWorkflowVariablesFromMetadata(ContextInstance ctxI, Element metadata) {
		StringBuffer sbTitle = new StringBuffer("");
		for(Iterator it = metadata.getDescendants(new ElementFilter("title")); it.hasNext();){
			Element title = (Element)it.next();
			if(title.getAttributeValue("type").equals("original-main"))
				sbTitle.append(title.getText());
		}
		if(sbTitle.length()>240){
			ctxI.setVariable("wfo-title", sbTitle.substring(0,240)+"...");
		}
		else{
			ctxI.setVariable("wfo-title", sbTitle.toString());	
		}
		StringBuffer sbAuthorNames = new StringBuffer("");
		StringBuffer sbAuthors = new StringBuffer("");
		for(Iterator it = metadata.getDescendants(new ElementFilter("creatorlink")); it.hasNext();){
			Element creatorlink = (Element)it.next();
			String x = null;
			x = creatorlink.getAttributeValue("href", Namespace.getNamespace("http://www.w3.org/1999/xlink"));
			if(x==null){
				x = creatorlink.getAttributeValue("href");
			}
			sbAuthors.append(x);
			x= null;
			x=creatorlink.getAttributeValue("title", Namespace.getNamespace("http://www.w3.org/1999/xlink"));
			if(x==null){
				x=creatorlink.getAttributeValue("title");
			}
			
			sbAuthorNames.append(x);
			if(it.hasNext()){ sbAuthors.append(","); sbAuthorNames.append("; ");}
		}
		if(sbAuthors.length()==0){
			for(Iterator it = metadata.getDescendants(new ElementFilter("creator")); it.hasNext();){
				Element creator = (Element)it.next();
				sbAuthorNames.append(creator.getText());
				if(it.hasNext()){ sbAuthorNames.append("; ");}
			}	
		}
		ctxI.setVariable(MCRWorkflowConstants.WFM_VAR_AUTHOR_IDS, sbAuthors.toString());
		ctxI.setVariable(MCRWorkflowConstants.WFM_VAR_AUTHOR_NAMES, sbAuthorNames.toString());
//		ctxI.setVariable(MCRWorkflowConstants.WFM_VAR_CONTAINS_PDF, "true");
	}
}
