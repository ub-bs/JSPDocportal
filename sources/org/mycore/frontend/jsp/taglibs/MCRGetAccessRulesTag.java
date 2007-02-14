package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;
import java.util.Locale;
import java.util.PropertyResourceBundle;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.DOMOutputter;
import org.mycore.common.JSPUtils;
import org.mycore.common.MCRSessionMgr;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowAccessRuleEditorUtils;
import org.mycore.user.MCRUserMgr;


public class MCRGetAccessRulesTag extends SimpleTagSupport
{
	private String var;
	private String mcrid;
	private String processid;
	private String step;
	private String choosenRule;
	
	public void setVar(String inputVar) {
		var = inputVar;
		return;
	}

	public void setProcessid(String processid) {
		this.processid = processid;
	}
	
	public void setMcrid(String mcrid) {
		this.mcrid = mcrid;
	}

	public void setStep(String step) {
		this.step = step;
	}
	
	public void setChoosenRule(String choosenRule) {
		this.choosenRule = choosenRule;
	}
	
	public void doTag() throws JspException, IOException {
		PageContext pageContext = (PageContext) getJspContext();
		String lang = MCRSessionMgr.getCurrentSession().getCurrentLanguage();
		Element options = new Element("rules");
		
		if ( step == "getCurrentRule" ) {
			choosenRule = MCRWorkflowAccessRuleEditorUtils.getCurrentRule(mcrid, processid);
			pageContext.setAttribute(var, choosenRule);
		}
		else if ( step == "getRules") {
			String[] defRules  = MCRWorkflowAccessRuleEditorUtils.getDefaultRules();
			if (choosenRule== null || choosenRule.length()< 1) 
				choosenRule = MCRWorkflowAccessRuleEditorUtils.getCurrentRule(mcrid, processid);
			options = new Element("rules");
			for (int  i=0; i< defRules.length; i++) {
				Element option = new Element("rule");
				option.setAttribute("name",
						PropertyResourceBundle.getBundle("messages",
						new Locale(lang)).getString("MCR.AccessRuleEditor.defaultrules." + defRules[i]));
				option.setAttribute("value", defRules[i]);
				if ( choosenRule != null &&  choosenRule.equals(defRules[i]) ) {
					option.setAttribute("aktiv", "selected");
				}
				options.addContent(option);
			}
			org.jdom.Document domOptions = new org.jdom.Document(options);
			org.w3c.dom.Document domDoc = null;
			try {
				domDoc = new DOMOutputter().output(domOptions);
			} catch (JDOMException e) {
				Logger.getLogger(MCRSetResultListTag.class).error("Domoutput failed: ", e);
			}
			pageContext.setAttribute(var, domDoc);
		}
				
		else if ( step == "getGroups") {
			String[] availGroups = (String[]) MCRUserMgr.instance().getAllGroupIDs().toArray(new String[]{});
			String[] selectedGroups = MCRWorkflowAccessRuleEditorUtils.getChoosenGroups(mcrid, processid);
			options = new Element("groups");
			Element currGroups = new Element("currGroups");
			for(int i=0;i<availGroups.length;i++){
				Element option = new Element("group");
				option.setAttribute("name",  availGroups[i]);
				option.setAttribute("value", availGroups[i]);
				if(java.util.Arrays.asList(selectedGroups).contains(availGroups[i])){
					option.setAttribute("aktiv", "selected");						
					Element currGroup = new Element("currGroup");
					currGroup.setAttribute("name", availGroups[i]);						
					currGroups.addContent(currGroup);
					
				}
				options.addContent(option);
			}
			options.addContent(currGroups);

			org.jdom.Document domOptions = new org.jdom.Document(options);
			org.w3c.dom.Document domDoc = null;
			try {
				domDoc = new DOMOutputter().output(domOptions);
			} catch (JDOMException e) {
				Logger.getLogger(MCRSetResultListTag.class).error("Domoutput failed: ", e);
			}
			pageContext.setAttribute(var, domDoc);			
		}
		
		if(pageContext.getAttribute("debug") != null && pageContext.getAttribute("debug").equals("true")) {
			JspWriter out = pageContext.getOut();
			StringBuffer debugSB = new StringBuffer("<textarea cols=\"80\" rows=\"30\">")
				.append("found this ids:\r\n")
				.append(JSPUtils.getPrettyString(options))
				.append("\r\n--------------------\r\n")
				.append("</textarea>");
			out.println(debugSB.toString());
		}
		
		return;
	}	

}