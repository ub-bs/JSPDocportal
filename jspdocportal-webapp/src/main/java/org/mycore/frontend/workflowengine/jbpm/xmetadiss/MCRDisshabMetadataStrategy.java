package org.mycore.frontend.workflowengine.jbpm.xmetadiss;
import java.util.Iterator;

import org.jbpm.context.exe.ContextInstance;
import org.jdom2.Element;
import org.jdom2.Text;
import org.jdom2.filter.ElementFilter;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowConstants;
import org.mycore.frontend.workflowengine.strategies.MCRDefaultMetadataStrategy;

public class MCRDisshabMetadataStrategy extends MCRDefaultMetadataStrategy {
	//private static Logger logger = Logger.getLogger(MCRDisshabMetadataStrategy.class.getName());
	public MCRDisshabMetadataStrategy(){
		super("disshab");
	}
	
	public void setWorkflowVariablesFromMetadata(ContextInstance ctxI, Element metadata) {
		StringBuffer sbTitle = new StringBuffer("");
		for(Iterator<Element> it = metadata.getDescendants(new ElementFilter("title")); it.hasNext();){
			Element title = it.next();
			if(title.getAttributeValue("type").equals("original-main"))
				sbTitle.append(title.getText());
		}
		if(sbTitle.length()>200){
			ctxI.setVariable("wfo-title", sbTitle.substring(0,200)+"...");
		}
		else{
			ctxI.setVariable("wfo-title", sbTitle.toString());	
		}
		StringBuffer sbAuthorNames = new StringBuffer("");
		XPathExpression<Text> xpeText = XPathFactory.instance().compile(".//*/text()", Filters.text());

		for(Iterator<Element> it = metadata.getDescendants(new ElementFilter("creator")); it.hasNext();){
			Element creator = it.next();
			for(Text t: xpeText.evaluate(creator)){
				sbAuthorNames.append(t.getTextNormalize()).append(" ");
			}
			if(it.hasNext()){ sbAuthorNames.append("; ");}
		}	
		
		ctxI.setVariable(MCRWorkflowConstants.WFM_VAR_AUTHOR_NAMES, sbAuthorNames.toString());
	}
}
