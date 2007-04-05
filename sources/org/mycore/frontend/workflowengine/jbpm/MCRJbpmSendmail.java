package org.mycore.frontend.workflowengine.jbpm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jbpm.graph.exe.ExecutionContext;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRException;
import org.mycore.common.MCRMailer;
import org.mycore.user.MCRGroup;
import org.mycore.user.MCRUser;
import org.mycore.user.MCRUserMgr;

/**
 * @author mcradmin
 *
 */
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
		subject = replaceMessageKeys(subject);
		subject += " (Bearbeitungsnummer: " + executionContext.getProcessInstance().getId() + ")";
		if(body == null){
			body = "";
		}
		body = replaceMessageKeys(body);
		if(jbpmVariableName != null && !jbpmVariableName.equals("")){
			body += executionContext.getVariable(jbpmVariableName);  
		}
		try {
			String id = (String)executionContext.getVariable(MCRWorkflowConstants.WFM_VAR_METADATA_OBJECT_IDS);
			String title = (String)executionContext.getVariable(MCRWorkflowConstants.WFM_VAR_WFOBJECT_TITLE);
			if ( title != null)
				body += "\nTitel: '" + title + "'";
			if ( id != null)
				body += " ID: " + id;
		} catch ( Exception all) {
			;
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
			
			// if no correct email is set
			if (listTo.size() + listReplyTo.size() + listBcc.size() < 1)	{
				
				logger.error("could not send email, but the workflow goes on");
				logger.error("main recipients are empty" );
				return;
			}
			if (listTo.size() < 1) {
				if (listReplyTo.size() > 0)
					  listTo = listReplyTo;
				else  listTo = listBcc;
			}			

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
	
	private static List getEmailAddressesFromStringList(String addresses,
			ExecutionContext executionContext) {
		List<String> ret = new ArrayList<String>();
		if (addresses == null || addresses.equals(""))
			return ret;
		String[] array = addresses.split(";");
		for (int i = 0; i < array.length; i++) {
			if (array[i].indexOf("@") >= 0) {
				ret.add(array[i]);
			} else if (array[i].trim().equals("initiator")) {
				String email = getUserEmailAddress((String) executionContext
						.getVariable(MCRWorkflowConstants.WFM_VAR_INITIATOR));
				if (email == null || email.equals("")) {
					email = (String) executionContext
							.getVariable(MCRWorkflowConstants.WFM_VAR_INITIATOREMAIL);
				}
				if (email == null || email.equals("")) {
					email = getUserEmailAddress("administrator");
				}
				ret.add(email);
			} else if (array[i].trim().equals("user")){
				MCRUser user = MCRUserMgr.instance().getCurrentUser();
				String email = user.getUserContact().getEmail();
				if(email!=null && !email.equals("")){
					ret.add(email);
				}
			} else if (array[i].trim().equals("administrator")){
				String email  = getUserEmailAddress("administrator");
				if(email==null || email.equals("")){
					email = workflowAdminEmail;
				}
				ret.add(email);
				
			}			
			else {
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
	
	private static List<String> getGroupMembersEmailAddresses(String groupid){
		List<String> ret = new ArrayList<String>();
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
	
	
	
	/**
	 * replaces all occurrences of ${variable} 
	 * with the proper entry from the message property files
	 * @param text the input, that should be translated
	 * 
	 * @return the translated string
	 * 
	 * @author Robert Stephan
	 */
	public static String replaceMessageKeys(String text){
		ResourceBundle rb = ResourceBundle.getBundle("messages", new Locale("de"));
		Pattern p = Pattern.compile("\\$\\{[^\\}]*\\}");
		Matcher m = p.matcher(text);
		StringBuffer sb = new StringBuffer();
		while(m.find()){
			String key = m.group();
			key = key.substring(2,key.length()-1);
			String replacement="???"+key+"???";
			try{
				replacement = rb.getString(key);
			} catch(MissingResourceException mre){
				
			}
			m.appendReplacement(sb, replacement);
		}
		m.appendTail(sb);
		return sb.toString();
}
}
