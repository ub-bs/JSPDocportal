package org.mycore.frontend.workflowengine.jbpm.publication;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.jbpm.graph.exe.ExecutionContext;
import org.mycore.common.MCRConfiguration;
import org.mycore.frontend.workflowengine.jbpm.MCRAbstractAction;
import org.mycore.frontend.workflowengine.jbpm.MCRJbpmSendmail;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowConstants;

public class MCRSendmailActionPublication  extends MCRAbstractAction {
	
	private static final long serialVersionUID = 1L;
	
	private String from;
	private String to;
	private String replyTo;
	private String bcc;
	private String subject;
	private String body;
	private String mode;
	
	private String jbpmVariableName;
	
	private String dateOfSubmissionVariable;
	
	
	public void executeAction(ExecutionContext executionContext) {
		String myreplyTo="";
		String mybcc ="";
		String mybody =null;
		//Either in the variable is the ConfigString or the real value
		String myfrom = MCRConfiguration.instance().getString(from,from);
		String myto = MCRConfiguration.instance().getString(to,to);
		if ( replyTo != null )
			myreplyTo = MCRConfiguration.instance().getString(replyTo,replyTo);
		if ( bcc != null)
			mybcc = MCRConfiguration.instance().getString(bcc,bcc);
		ResourceBundle rb = ResourceBundle.getBundle("messages", new Locale("de"));
		
		String mysubject = null;
		try{
			mysubject = rb.getString(subject);
		}
		catch(MissingResourceException mre){
			mysubject = subject;
		}
		
		mybody = getBody(executionContext, mode);
		MCRJbpmSendmail.sendMail(myfrom, myto, myreplyTo, mybcc, mysubject,
				mybody, mode, jbpmVariableName, dateOfSubmissionVariable, executionContext);
	}
	
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
			String id = (String)executionContext.getVariable(MCRWorkflowConstants.WFM_VAR_METADATA_OBJECT_IDS);
			String title = (String)executionContext.getVariable(MCRWorkflowConstants.WFM_VAR_WFOBJECT_TITLE);
			if(salutation != null)
				ret += salutation + "\r\n\r\n";
			else
				ret += "Sehr geehrte(r) Autor(in)";
			String body = " Ihre Publikation '" + title + "' (" + id + ") wurde angenommen und publiziert.";
			try {
				body = PropertyResourceBundle.getBundle("messages", new Locale(lang)).getString("WF.Mails.SuccessMessage.publication");
			} catch (java.util.MissingResourceException mRE) {
				// ignore and take the standard text
				;
			}
			
			if(body != null)
				ret += body + "\r\n\r\n";
			String footer = PropertyResourceBundle.getBundle("messages", new Locale(lang)).getString("WF.Mails.Footer");
			if(footer != null)
				ret += footer;
		}
		if(ret != null)
			return ret;
		return "";
	}


}
