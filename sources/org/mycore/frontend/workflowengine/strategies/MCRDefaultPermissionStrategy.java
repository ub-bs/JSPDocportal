package org.mycore.frontend.workflowengine.strategies;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.jbpm.context.exe.ContextInstance;
import org.jdom.Element;
import org.mycore.access.MCRAccessInterface;
import org.mycore.access.MCRAccessManager;
import org.mycore.access.mcrimpl.MCRAccessControlSystem;
import org.mycore.access.mcrimpl.MCRAccessRule;
import org.mycore.access.mcrimpl.MCRAccessStore;
import org.mycore.access.mcrimpl.MCRRuleMapping;
import org.mycore.access.mcrimpl.MCRRuleStore;
import org.mycore.backend.hibernate.MCRHIBConnection;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.xml.MCRXMLHelper;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowAccessRuleEditorUtils;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowConstants;

public class MCRDefaultPermissionStrategy implements MCRPermissionStrategy {

	private static MCRAccessInterface AI = MCRAccessManager.getAccessImpl();

	private static MCRConfiguration config = MCRConfiguration.instance();

	private static Logger logger = Logger.getLogger(MCRWorkflowAccessRuleEditorUtils.class.getName());

	private static MCRRuleStore rstore = MCRRuleStore.getInstance();

	/**
	 * sets default permissions for a given mcrobj and a given user in the
	 * specific workflow
	 * 
	 * @param mcrid
	 * @param userid
	 */
	public void setPermissions(String mcrid, String userid,
			String workflowProcessType, ContextInstance ctxI, int mode) {
		if (mode == MCRWorkflowConstants.PERMISSION_MODE_DEFAULT) {
			MCRObjectID objID = new MCRObjectID(mcrid);
			for (int i = 0; i < defaultPermissionTypes.length; i++) {
				String propName = new StringBuffer(
						"MCR.WorkflowEngine.defaultACL.").append(
						objID.getTypeId()).append(".").append(
						defaultPermissionTypes[i]).append(".").append(
						workflowProcessType).toString();

				String strRule = config
						.getString(propName,
								"<condition format=\"xml\"><boolean operator=\"false\" /></condition>");
				strRule = strRule.replaceAll("\\$\\{user\\}", userid);
				Element rule = (Element) MCRXMLHelper.parseXML(strRule, false)
						.getRootElement().detach();
				String permissionType = defaultPermissionTypes[i];
				if (AI.hasRule(objID.getId(), permissionType)) {
					AI.updateRule(objID.getId(), permissionType, rule, "");
				} else {
					AI.addRule(objID.getId(), permissionType, rule, "");
				}
			}
		}
		if (mode == MCRWorkflowConstants.PERMISSION_MODE_PUBLISH) {
			createReadRuleFromWorkflow(mcrid, ctxI);
		}

		if (mode == MCRWorkflowConstants.PERMISSION_MODE_EDITING) {
		}

		if (mode == MCRWorkflowConstants.PERMISSION_MODE_CREATORRREAD) {
			MCRObjectID objID = new MCRObjectID(mcrid);
			String propName = new StringBuffer("MCR.WorkflowEngine.defaultACL.")
					.append(objID.getTypeId()).append(".").append("read")
					.append(".").append(workflowProcessType).toString();
			String strReadRule = MCRConfiguration.instance()
					.getString(propName);
			String x = strReadRule.replaceAll("\\$\\{user\\}", userid);
			Element readRule = (Element) MCRXMLHelper.parseXML(x, false)
					.getRootElement().detach();
			AI.addRule(mcrid, "read", readRule, "");
		}

	}

	private void createReadRuleFromWorkflow(String mcrid, ContextInstance ctxI) {
		MCRAccessStore accessStore = MCRAccessStore.getInstance();
		MCRAccessControlSystem ACS = (MCRAccessControlSystem) MCRAccessControlSystem
				.instance();

		Object xmlRuleString = ctxI.getVariable(
				MCRWorkflowConstants.WFM_VAR_READRULE_XMLSTRING);
		Object oRuletype = ctxI.getVariable(
				MCRWorkflowConstants.WFM_VAR_READRULE_TYPE);
		//default value		
		
		if (oRuletype== null) {
			oRuletype = new String("public");
		}

		if (xmlRuleString == null) {
			xmlRuleString = MCRConfiguration
					.instance()
					.getString("MCR.AccessRuleEditor.defaultrules." + oRuletype.toString(),
							   "<condition format=\"xml\"><boolean operator=\"true\" /></condition>");
		}

		Element eRule = (Element) MCRXMLHelper.parseXML(xmlRuleString.toString(),false)
				.getRootElement().detach();
		String rule = ACS.getNormalizedRuleString(eRule);

		ArrayList ruleIDs = rstore.retrieveRuleIDs(rule, "");
		/* entferne alle nicht durch den Ruleeditor erzeugten regeln */
		Iterator it = ruleIDs.iterator();
		while (it.hasNext()) {
			if (!((String) it.next())
					.startsWith(MCRWorkflowConstants.ACCESSRULE_PREFIX + "_"+ oRuletype.toString().toUpperCase())) {
				it.remove();
			}
		}

		if (ruleIDs.size() == 0) {
			// if the rule does not exist - create new one and give it a special
			// name, to be able to distinguish it
			// MCRAccessManager will later pickup this rule and use it
			DecimalFormat nrFormat = new DecimalFormat(
					MCRWorkflowConstants.ACCESSRULE_NUMBERFORMAT);

			String ruleID = MCRWorkflowConstants.ACCESSRULE_PREFIX + "_"+ oRuletype.toString().toUpperCase()+ "_"
					+ nrFormat.format(rstore.getNextFreeRuleID(MCRWorkflowConstants.ACCESSRULE_PREFIX
											+ "_"+ oRuletype.toString().toUpperCase()+ "_"));
			// (String id, String creator, Date creationTime, String rule,
			// String description)
			MCRAccessRule mcrARule = new MCRAccessRule(ruleID, "RuleEditor",
					new Date(), rule, "");
			rstore.createRule(mcrARule);
			ruleIDs = rstore.retrieveRuleIDs(rule, "");
			// delete all rules not created by editor
			it = ruleIDs.iterator();
			while (it.hasNext()) {
				if (!((String) it.next())
						.startsWith(MCRWorkflowConstants.ACCESSRULE_PREFIX
								+ "_" + oRuletype.toString().toUpperCase())) {
					it.remove();
				}
			}
		}

		// reimplementation of to change creator
		// MCRAccessManager.updateRule(mcrOID,EDITPOOL,eCond, "");

		String pool = "read";
		MCRRuleMapping ruleMapping = ACS.getAutoGeneratedRuleMapping(eRule,
				"RuleEditor", pool, mcrid, "");
		ruleMapping.setRuleId((String)ruleIDs.get(0));
		String oldRuleID = accessStore.getRuleID(mcrid, pool);
		if (oldRuleID == null || oldRuleID.equals("")) {
			logger.debug("updateRule called for id <" + mcrid + "> and pool <"
					+ pool
					+ ">, but no rule is existing, so new rule was created");
			accessStore.createAccessDefinition(ruleMapping);
		} else {
			accessStore.updateAccessDefinition(ruleMapping);
		}
	}
}
