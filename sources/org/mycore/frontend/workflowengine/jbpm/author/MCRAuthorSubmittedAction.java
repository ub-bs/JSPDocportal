package org.mycore.frontend.workflowengine.jbpm.author;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.jdom.Element;
import org.mycore.access.MCRAccessInterface;
import org.mycore.access.MCRAccessManager;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRException;
import org.mycore.common.xml.MCRXMLHelper;
import org.mycore.frontend.workflowengine.jbpm.MCRJbpmWorkflowBase;
import org.mycore.user2.MCRUser;
import org.mycore.user2.MCRUserMgr;

public class MCRAuthorSubmittedAction implements ActionHandler{
	
	String lockedVariables;

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(MCRAuthorSubmittedAction.class);
	private static MCRAccessInterface AI = MCRAccessManager.getAccessImpl();
	private static Element editorModeRule;
	private static Element authorReadRule;
	private static String strReadRule;
	
	static{  
		String strRule = MCRConfiguration.instance().getString("MCR.WorkflowEngine.defaultACL.editorMode.author", "<condition format=\"xml\"><boolean operator=\"or\"><condition field=\"group\" operator=\"=\" value=\"editorgroup1\" /></boolean></condition>");
		editorModeRule = (Element)MCRXMLHelper.parseXML(strRule).getRootElement().detach();
		strReadRule = MCRConfiguration.instance().getString("MCR.WorkflowEngine.defaultACL.author.read.author");	
	}

	public void execute(ExecutionContext executionContext) throws MCRException {
		logger.debug("locking workflow variables and setting the access control to the editor mode");
		ContextInstance contextInstance = executionContext.getContextInstance();
		contextInstance.setVariable(MCRJbpmWorkflowBase.lockedVariablesIdentifier, lockedVariables);
		// set access control to editor mode, the dissertand has no rights anymore
		List ids = new ArrayList();
		ids.add(contextInstance.getVariable("createdDocID"));
		
		String initiator = contextInstance.getVariable(MCRJbpmWorkflowBase.varINITIATOR).toString();
		MCRUser user = MCRUserMgr.instance().retrieveUser(initiator);
		
		String x = strReadRule.replaceAll("\\$\\{user\\}", user.getID());
		authorReadRule = (Element)MCRXMLHelper.parseXML(x).getRootElement().detach();
				
		for (Iterator it = ids.iterator(); it.hasNext();) {
			String id = (String) it.next();
			List permissions = AI.getPermissionsForID(id);
			for (Iterator it2 = permissions.iterator(); it2.hasNext();) {
				String permission = (String) it2.next();
				AI.addRule(id, permission, editorModeRule, "");
			}
				AI.addRule(id, "read", authorReadRule, "");
		}
	}
}
