package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;
import org.jdom.JDOMException;
import org.jdom.output.DOMOutputter;
import org.mycore.access.MCRAccessInterface;
import org.mycore.access.MCRAccessManager;
import org.mycore.common.JSPUtils;
import org.mycore.common.MCRConfiguration;
import org.mycore.datamodel.metadata.MCRXMLTableManager;


public class MCRSetAccessRuleEditorDataTag extends SimpleTagSupport
{
	private String var;
	private String docType;
	private int start;
	private int step;
	
	private static MCRAccessInterface AI = MCRAccessManager.getAccessImpl();
	private static String[] permissions = MCRConfiguration.instance().getString("MCR.AccessPools", "read,modify,delete").split(",");
	

	public void setVar(String inputVar) {
		var = inputVar;
		return;
	}
	public void setDocType(String inputDocType) {
		docType = inputDocType;
	}
	public void setStart(int inputStart){
		start = inputStart;
	}
	public void setStep(int inputStep){
		step = inputStep;
	}	

	public static List mergeSortedDistinctStringLists(List list1, List list2){
		int size1 = list1.size();
		int size2 = list2.size();
		List ret = new ArrayList();
		
		int i1=0;
		int i2=0; 
		while (i1 < size1 && i2 < size2) {
			String s1 = (String)list1.get( i1 ); 
			String s2 = (String)list2.get( i2 ); 
			int diff = s1.compareTo(s2);
			if(diff < 0) {
				ret.add(s1);
				i1++;
			}else if(diff > 0) {
				ret.add(s2);
				i2++;
			}else{
				ret.add(s1);
				i1++;
				i2++;
			}
		}
		for (int i = i1; i < size1; i++) {
			ret.add(list1.get(i));
		}
		for (int i = i2; i < size2; i++) {
			ret.add(list2.get(i));
		}	
		return ret;
	}	
			
	public void doTag() throws JspException, IOException {
		PageContext pageContext = (PageContext) getJspContext();
		List merged;
		String indexType;
		if(docType == null || docType.equals("")) {
			List controlledIDs = AI.getAllControlledIDs();
			List allMCRIDs = MCRXMLTableManager.instance().retrieveAllIDs();
			Collections.sort(allMCRIDs);
			// controlledIDs is sorted yet
			// Collections.sort(controlledIDs)
			merged = mergeSortedDistinctStringLists(controlledIDs, allMCRIDs);
			indexType = "allIDS";
		}else{
			merged = MCRXMLTableManager.instance().retrieveAllIDs(docType);
			Collections.sort(merged);
			indexType = docType;
		}
		if(step == 0) step = 25;
		
		org.jdom.Element root = new org.jdom.Element("accessrule-index");
		root.setAttribute("type", indexType);
		
		org.jdom.Element permissionsEl = new org.jdom.Element("permissions");
		permissionsEl.setAttribute("numHits",String.valueOf(permissions.length));
		for (int i = 0; i < permissions.length; i++) {
			org.jdom.Element permissionEl = new org.jdom.Element("permission");
			permissionEl.setText(permissions[i]);
			permissionsEl.addContent(permissionEl);
		}
		root.addContent(permissionsEl);
		
		org.jdom.Element results = new org.jdom.Element("result");
		results.setAttribute("numHits", String.valueOf(merged.size()));
		int i;
		for (i = start; i < merged.size() && i < start + step; i++ ) {
			org.jdom.Element value = new org.jdom.Element("value");
			value.setAttribute("pos", String.valueOf(i));
			org.jdom.Element idx = new org.jdom.Element("idx");
			idx.setText(String.valueOf(merged.get(i)));
			value.addContent(idx);
			for (int j = 0; j < permissions.length; j++) {
				org.jdom.Element permissionForId = new org.jdom.Element("permission");
				permissionForId.setAttribute("name", permissions[j]);
				if(AI.hasRule((String)merged.get(i),permissions[j]))
					permissionForId.setAttribute("value","X");
				else
					permissionForId.setAttribute("value","");
				value.addContent(permissionForId);
			}
			
			results.addContent(value);
		}
		results.setAttribute("displayedHits", String.valueOf(i - start));
		root.addContent(results);
		org.jdom.Document doc = new org.jdom.Document(root);
		
   		org.w3c.dom.Document domDoc = null;
   		try {
   			domDoc = new DOMOutputter().output(doc);
   		} catch (JDOMException e) {
    		Logger.getLogger(MCRSetAccessRuleEditorDataTag.class).error("Domoutput failed: ", e);
   		}
    	if(pageContext.getAttribute("debug") != null && pageContext.getAttribute("debug").equals("true")) {
			JspWriter out = pageContext.getOut();
			StringBuffer debugSB = new StringBuffer("<textarea cols=\"80\" rows=\"30\">")
				.append("found this indexfile for accessrule-editor:\r\n")
				.append(JSPUtils.getPrettyString(doc))
				.append("</textarea>");
			out.println(debugSB.toString());
		}
    	pageContext.setAttribute(var, domDoc);
		return;
	}	

}