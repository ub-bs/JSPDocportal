package org.mycore.frontend.workflowengine.jbpm;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
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
		MCRJbpmSendmail.sendMail(from, to, replyTo, bcc, subject,
				body, mode, jbpmVariableName, dateOfSubmissionVariable, executionContext);
	}
}
