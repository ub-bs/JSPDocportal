package org.mycore.frontend.workflowengine.jbpm;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRException;

public class MCRSendmailAction implements ActionHandler{
	
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
	
	
	public void execute(ExecutionContext executionContext) throws MCRException {
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
		if ( body != null ){
			try{
				mybody = rb.getString(body);
			}catch(MissingResourceException mre){
				mybody = body;	
			}
		}
		
		MCRJbpmSendmail.sendMail(myfrom, myto, myreplyTo, mybcc, mysubject,
				mybody, mode, jbpmVariableName, dateOfSubmissionVariable, executionContext);
	}
}
