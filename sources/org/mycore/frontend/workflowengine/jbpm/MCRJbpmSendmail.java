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
import org.jbpm.graph.exe.ExecutionContext;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRException;
import org.mycore.common.MCRMailer;
import org.mycore.user2.MCRGroup;
import org.mycore.user2.MCRUser;
import org.mycore.user2.MCRUserMgr;

public class MCRJbpmSendmail{
	
	private static final long serialVersionUID = 1L;
	protected static Logger logger = Logger.getLogger(MCRJbpmSendmail.class);
	private static Calendar cal = new GregorianCalendar( TimeZone.getTimeZone("ECT"));
	protected static String workflowAdminEmail = MCRConfiguration.instance().getString("MCR.WorkflowEngine.Administrator.Email", "admin@mycore.de");
	
	
	public static void sendMail(String from,
			String to, String replyTo, String bcc, 
			String subject, String body, String mode,
			String jbpmVariableName, String dateOfSubmissionVariable, 
			ExecutionContext executionContext) throws MCRException{

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
		if(jbpmVariableName != null && !jbpmVariableName.equals("")){
			body += executionContext.getVariable(jbpmVariableName);  
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
			logger.error("could not send email, but the workflow goes on");
			logger.error("mail subject: " + subject);
			logger.error("mail body: " + body);
			logger.error("mail main recipients: " + to);
		}		
	}
	
	private static List getEmailAddressesFromStringList(String addresses, ExecutionContext executionContext){
		List ret = new ArrayList();
		if(addresses == null || addresses.equals(""))
			return ret;
		String[] array = addresses.split(";");
		for (int i = 0; i < array.length; i++) {
			if(array[i].indexOf("@") >= 0){
				ret.add(array[i]);
			}else if(array[i].trim().equals("initiator")){
				String email = getUserEmailAddress((String)executionContext.getVariable(MCRJbpmWorkflowBase.varINITIATOR));
				if(email == null || email.equals("")){
					email = (String)executionContext.getVariable(MCRJbpmWorkflowBase.varINITIATOREMAIL);
				}
				if(email == null || email.equals("")){
					email = getUserEmailAddress("administrator");
				}
				ret.add(email);
			}else{
				ret.addAll(getGroupMembersEmailAddresses(array[i]));
			}
		}
		return ret;
	}
	
	private static String getUserEmailAddress(String userid){
		if ( MCRUserMgr.instance().existUser(userid) ) {
			MCRUser user = MCRUserMgr.instance().retrieveUser(userid);
			return user.getUserContact().getEmail();
		}
		return null;
	}
	
	private static List getGroupMembersEmailAddresses(String groupid){
		List ret = new ArrayList();
		if (MCRUserMgr.instance().existGroup(groupid) ) {
			MCRGroup group = MCRUserMgr.instance().retrieveGroup(groupid);
			for (Iterator it = group.getMemberUserIDs().iterator(); it.hasNext();) {
				String email = getUserEmailAddress((String)it.next());
				if(email != null && email.indexOf("@") > -1){
					ret.add(email);
				}
			}
		}
		if(ret.size() == 0){
			logger.error("no group member of [" + "] has a known e-mail-address");
			ret.add(workflowAdminEmail);
		}
		return ret;
	}
}
