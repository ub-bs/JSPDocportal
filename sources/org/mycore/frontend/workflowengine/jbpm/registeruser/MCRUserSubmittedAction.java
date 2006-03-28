package org.mycore.frontend.workflowengine.jbpm.registeruser;


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

public class MCRUserSubmittedAction implements ActionHandler{
	
	String lockedVariables;

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(MCRUserSubmittedAction.class);
	private static MCRAccessInterface AI = MCRAccessManager.getAccessImpl();
	private static Element editorModeRule;
	
	static{
		String strRule = MCRConfiguration.instance().getString("MCR.WorkflowEngine.defaultACL.editorMode.userregistration", "<condition format=\"xml\"><boolean operator=\"or\"><condition field=\"group\" operator=\"=\" value=\"admingroup\" /></boolean></condition>");
		editorModeRule = (Element)MCRXMLHelper.parseXML(strRule).getRootElement().detach();
	}

	public void execute(ExecutionContext executionContext) throws MCRException {
		logger.debug("locking workflow variables and setting the access control to the admin mode");
		ContextInstance contextInstance = executionContext.getContextInstance();
		contextInstance.setVariable(MCRJbpmWorkflowBase.lockedVariablesIdentifier, lockedVariables);
		// set access control to admin mode, the user  has no rights anymore
		String id = (String) contextInstance.getVariable("userID");
		List permissions = AI.getPermissionsForID(id);
		for (Iterator it2 = permissions.iterator(); it2.hasNext();) {
			String permission = (String) it2.next();
			AI.addRule(id, permission, editorModeRule, "");
		}
	}

}
