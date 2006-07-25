package org.mycore.frontend.workflowengine.jbpm;

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
		
		//Either in the variable is the ConfigString or the real value
		String myfrom = MCRConfiguration.instance().getString(from,from);
		String myto = MCRConfiguration.instance().getString(to,to);
		if ( replyTo != null )
			myreplyTo = MCRConfiguration.instance().getString(replyTo,replyTo);
		if ( bcc != null)
			mybcc = MCRConfiguration.instance().getString(bcc,bcc);
		
		MCRJbpmSendmail.sendMail(myfrom, myto, myreplyTo, mybcc, subject,
				body, mode, jbpmVariableName, dateOfSubmissionVariable, executionContext);
	}
}
