package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.mycore.common.config.MCRConfiguration;

/**
 * 
 * @author ??? - its unused ???
 * 
 * @deprecated
 *
 */
public class MCRSetAccessRuleEditorDataTag extends SimpleTagSupport {
    private String var;
    private String docType;
    private int start;
    private int step;

    private static String[] permissions = MCRConfiguration.instance().getString("MCR.AccessPools", "read,modify,delete")
            .split(",");

    public void setVar(String inputVar) {
        var = inputVar;
        return;
    }

    public void setDocType(String inputDocType) {
        docType = inputDocType;
    }

    public void setStart(int inputStart) {
        start = inputStart;
    }

    public void setStep(int inputStep) {
        step = inputStep;
    }

    @SuppressWarnings("unchecked")
    public static List mergeSortedDistinctStringLists(List list1, List list2) {
        int size1 = list1.size();
        int size2 = list2.size();
        List ret = new ArrayList();

        int i1 = 0;
        int i2 = 0;
        while (i1 < size1 && i2 < size2) {
            String s1 = (String) list1.get(i1);
            String s2 = (String) list2.get(i2);
            int diff = s1.compareTo(s2);
            if (diff < 0) {
                ret.add(s1);
                i1++;
            } else if (diff > 0) {
                ret.add(s2);
                i2++;
            } else {
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
        /*		PageContext pageContext = (PageContext) getJspContext();
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
        		
        		org.jdom2.Element root = new org.jdom2.Element("accessrule-index");
        		root.setAttribute("type", indexType);
        		
        		org.jdom2.Element permissionsEl = new org.jdom2.Element("permissions");
        		permissionsEl.setAttribute("numHits",String.valueOf(permissions.length));
        		for (int i = 0; i < permissions.length; i++) {
        			org.jdom2.Element permissionEl = new org.jdom2.Element("permission");
        			permissionEl.setText(permissions[i]);
        			permissionsEl.addContent(permissionEl);
        		}
        		root.addContent(permissionsEl);
        		
        		org.jdom2.Element results = new org.jdom2.Element("result");
        		results.setAttribute("numHits", String.valueOf(merged.size()));
        		int i;
        		for (i = start; i < merged.size() && i < start + step; i++ ) {
        			org.jdom2.Element value = new org.jdom2.Element("value");
        			value.setAttribute("pos", String.valueOf(i));
        			org.jdom2.Element idx = new org.jdom2.Element("idx");
        			idx.setText(String.valueOf(merged.get(i)));
        			value.addContent(idx);
        			for (int j = 0; j < permissions.length; j++) {
        				org.jdom2.Element permissionForId = new org.jdom2.Element("permission");
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
        		org.jdom2.Document doc = new org.jdom2.Document(root);
        		
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
        		return;*/
    }

}