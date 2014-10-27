package org.mycore.frontend.workflowengine.strategies;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.jbpm.context.exe.ContextInstance;
import org.jdom2.Element;
import org.mycore.access.MCRAccessManager;
import org.mycore.access.mcrimpl.MCRAccessControlSystem;
import org.mycore.access.mcrimpl.MCRAccessRule;
import org.mycore.access.mcrimpl.MCRAccessStore;
import org.mycore.access.mcrimpl.MCRRuleMapping;
import org.mycore.access.mcrimpl.MCRRuleStore;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.common.content.MCRStringContent;
import org.mycore.common.xml.MCRXMLParserFactory;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowAccessRuleEditorUtils;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowConstants;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowUtils;
import org.xml.sax.SAXParseException;

public class MCRDefaultPermissionStrategy implements MCRPermissionStrategy {

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
			MCRObjectID objID = MCRObjectID.getInstance(mcrid);
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
				try{
					Element rule = (Element) MCRXMLParserFactory.getParser(false).parseXML(new MCRStringContent(strRule))
							.getRootElement().detach();
					String permissionType = defaultPermissionTypes[i];
					if (MCRAccessManager.hasRule(objID.toString(), permissionType)) {
						MCRAccessManager.updateRule(objID.toString(), permissionType, rule, "");
					} else {
						MCRAccessManager.addRule(objID.toString(), permissionType, rule, "");
					}
				} catch(SAXParseException spe){
					logger.error("SAXParseException: ", spe);
				}
			}
		}
		if (mode == MCRWorkflowConstants.PERMISSION_MODE_PUBLISH) {
			createReadRuleFromWorkflow(mcrid, ctxI);
		}

		if (mode == MCRWorkflowConstants.PERMISSION_MODE_EDITING) {
		}

		if (mode == MCRWorkflowConstants.PERMISSION_MODE_CREATORRREAD) {
			MCRObjectID objID = MCRObjectID.getInstance(mcrid);
			String propName = new StringBuffer("MCR.WorkflowEngine.defaultACL.")
					.append(objID.getTypeId()).append(".").append("read")
					.append(".").append(workflowProcessType).toString();
			String strReadRule = MCRConfiguration.instance()
					.getString(propName);
			String x = strReadRule.replaceAll("\\$\\{user\\}", userid);
			try{
				Element readRule = (Element) MCRXMLParserFactory.getParser(false).parseXML(new MCRStringContent(x))
							.getRootElement().detach();
				MCRAccessManager.addRule(mcrid, "read", readRule, "");
			} catch(SAXParseException spe){
				logger.error("SAXParseException: ", spe);
			}
			
		}

	}
	
	public void removeAllPermissions(String mcrid){
		MCRAccessManager.removeAllRules(MCRObjectID.getInstance(mcrid));
	}

	private void createReadRuleFromWorkflow(String mcrid, ContextInstance ctxI) {
		MCRAccessStore accessStore = MCRAccessStore.getInstance();
		MCRAccessControlSystem ACS = (MCRAccessControlSystem) MCRAccessControlSystem
				.instance();

		String xmlRuleString = MCRWorkflowUtils.getLargeStringVariableFromWorkflow(MCRWorkflowConstants.WFM_VAR_READRULE_XMLSTRING, ctxI);
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
		Collection<String> ruleIDs = null;
		try{
			Element eRule = (Element) MCRXMLParserFactory.getParser(false).parseXML(new MCRStringContent(xmlRuleString.toString()))
				.getRootElement().detach();
			String rule = ACS.getNormalizedRuleString(eRule);

			ruleIDs=rstore.retrieveRuleIDs(rule, "");
			/* entferne alle nicht durch den Ruleeditor erzeugten regeln */
			Iterator<String> it = ruleIDs.iterator();
			while (it.hasNext()) {
				if (! it.next().startsWith(MCRWorkflowConstants.ACCESSRULE_PREFIX + "_"+ oRuletype.toString().toUpperCase(Locale.GERMAN))) {
					it.remove();
				}
			}
		
		if (ruleIDs.size() == 0) {
			// if the rule does not exist - create new one and give it a special
			// name, to be able to distinguish it
			// MCRAccessManager will later pickup this rule and use it
			DecimalFormat nrFormat = new DecimalFormat(
					MCRWorkflowConstants.ACCESSRULE_NUMBERFORMAT, DecimalFormatSymbols.getInstance(Locale.GERMANY));

			String ruleID = MCRWorkflowConstants.ACCESSRULE_PREFIX + "_"+ oRuletype.toString().toUpperCase(Locale.GERMAN)+ "_"
					+ nrFormat.format(rstore.getNextFreeRuleID(MCRWorkflowConstants.ACCESSRULE_PREFIX
											+ "_"+ oRuletype.toString().toUpperCase(Locale.GERMAN)+ "_"));
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
		ruleMapping.setRuleId((String)ruleIDs.iterator().next());
		String oldRuleID = accessStore.getRuleID(mcrid, pool);
		if (oldRuleID == null || oldRuleID.equals("")) {
			logger.debug("updateRule called for id <" + mcrid + "> and pool <"
					+ pool
					+ ">, but no rule is existing, so new rule was created");
			accessStore.createAccessDefinition(ruleMapping);
		} else {
			accessStore.updateAccessDefinition(ruleMapping);
		}
	} catch(SAXParseException spe){
		logger.error("SAXParseException: ", spe);
	}
	}
}
