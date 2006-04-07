package org.mycore.frontend.workflowengine.jbpm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRException;
import org.mycore.common.MCRMailer;
import org.mycore.user2.MCRGroup;
import org.mycore.user2.MCRUser;
import org.mycore.user2.MCRUserMgr;

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
