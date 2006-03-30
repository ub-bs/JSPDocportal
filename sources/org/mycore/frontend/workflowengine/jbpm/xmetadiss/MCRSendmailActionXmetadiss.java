package org.mycore.frontend.workflowengine.jbpm.xmetadiss;

import java.util.Locale;
import java.util.PropertyResourceBundle;

import org.jbpm.graph.exe.ExecutionContext;
import org.mycore.frontend.workflowengine.jbpm.MCRSendmailAction;

public class MCRSendmailActionXmetadiss extends MCRSendmailAction{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * returns the body message of a requested email
	 * @param executionContext
	 * @return
	 */
	protected String getBody(ExecutionContext executionContext, String mode){
		String ret = "";
		String lang = "de";
		if(mode.equals("success")){
			String inLang = (String)executionContext.getVariable("initiatorLanguage"); 
			if( inLang != null && !inLang.equals("")){
				lang = inLang;
			}
			String salutation = (String)executionContext.getVariable("salutation");
			if(salutation != null)
				ret += salutation + "\r\n\r\n";
			else
				ret += "Sehr geehrte(r) Doktorand(in)";
			String body = PropertyResourceBundle.getBundle("messages", new Locale(lang)).getString("WorkflowEngine.Mails.SuccessMessage.xmetadiss");
			if(body != null)
				ret += body + "\r\n\r\n";
			String footer = PropertyResourceBundle.getBundle("messages", new Locale(lang)).getString("WorkflowEngine.Mails.Footer");
			if(footer != null)
				ret += footer;
		}
		if(ret != null)
			return ret;
		return "";
	}
}
