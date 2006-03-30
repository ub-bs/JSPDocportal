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
import org.mycore.common.MCRException;
import org.mycore.common.MCRMailer;
import org.mycore.user2.MCRGroup;
import org.mycore.user2.MCRUser;
import org.mycore.user2.MCRUserMgr;

public class MCRSendmailAction implements ActionHandler{
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(MCRSendmailAction.class);
	private static Calendar cal = new GregorianCalendar( TimeZone.getTimeZone("ECT"));
	
	private String from;
	private String to;
	private String replyTo;
	private String bcc;
	private String subject;
	private String body;
	private String mode;
	
	private String dateOfSubmissionVariable;
	
	
	public void execute(ExecutionContext executionContext) throws MCRException {
		if(to == null || to.equals("")){
			String errMsg = "no recipient was given";
			logger.error(errMsg);
			throw new MCRException(errMsg);			
		}
		
		if(subject == null || subject.equals("")){
			subject = PropertyResourceBundle.getBundle("messages", new Locale("de")).getString("WorkflowEngine.Mails.DefaultSubject");
		}
		subject += " (Bearbeitungsnummer: " + executionContext.getProcessInstance().getId() + ")";
		if(body == null)
			body = "";
		
		if(mode != null){
				if(mode.toLowerCase().equals("taskmessage")){
					body += executionContext.getVariable(MCRJbpmWorkflowBase.varTASKMESSAGE);
				}
		}
		if(body.equals("")){
			String errMsg = "no body for mail was given";
			logger.error(errMsg);
			throw new MCRException(errMsg);
		}
		try{
			List listTo = getEmailAddressesFromStringList(to, executionContext);
			List listReplyTo = getEmailAddressesFromStringList(replyTo, executionContext);
			List listBcc = getEmailAddressesFromStringList(bcc, executionContext);
			String fromAddress = (String)getEmailAddressesFromStringList(from, executionContext).iterator().next();
			MCRMailer.send(fromAddress, listReplyTo, listTo, listBcc, subject, body, null);
			if(dateOfSubmissionVariable != null && !dateOfSubmissionVariable.equals("")) {
				SimpleDateFormat formater = new SimpleDateFormat();
			    executionContext.setVariable(dateOfSubmissionVariable, formater.format( cal.getTime() ) );	
			}
		}catch(Exception e){
			String errMsg = "could not send email";
			logger.error(errMsg, e);
			throw new MCRException(errMsg);
		}
	}
	
	private List getEmailAddressesFromStringList(String addresses, ExecutionContext executionContext){
		List ret = new ArrayList();
		if(addresses == null || addresses.equals(""))
			return ret;
		String[] array = addresses.split(";");
		for (int i = 0; i < array.length; i++) {
			if(array[i].contains("@")){
				ret.add(array[i]);
			}else if(array[i].trim().equals("initiator")){
				ret.add(getUserEmailAddress((String)executionContext.getVariable(MCRJbpmWorkflowBase.varINITIATOR)));
			}else{
				ret.addAll(getGroupMembersEmailAddresses(array[i]));
			}
		}
		return ret;
	}
	
	private String getUserEmailAddress(String userid){
		MCRUser user = MCRUserMgr.instance().retrieveUser(userid);
		return user.getUserContact().getEmail();
	}
	
	private List getGroupMembersEmailAddresses(String groupid){
		List ret = new ArrayList();
		MCRGroup group = MCRUserMgr.instance().retrieveGroup(groupid);
		for (Iterator it = group.getMemberUserIDs().iterator(); it.hasNext();) {
			ret.add(getUserEmailAddress((String)it.next()));
		}
		return ret;
	}
}
