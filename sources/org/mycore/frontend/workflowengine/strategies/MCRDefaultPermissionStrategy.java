package org.mycore.frontend.workflowengine.strategies;

import java.util.Iterator;
import java.util.List;

import org.jdom.Element;
import org.mycore.access.MCRAccessInterface;
import org.mycore.access.MCRAccessManager;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.xml.MCRXMLHelper;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowConstants;

public class MCRDefaultPermissionStrategy implements MCRPermissionStrategy{

	private static MCRAccessInterface AI = MCRAccessManager.getAccessImpl();
	private static MCRConfiguration config = MCRConfiguration.instance();
	/**
	 * sets default permissions for a given mcrobj 
	 * and a given user in the specific workflow
	 * 
	 * @param mcrid
	 * @param userid
	 */
	public void setPermissions(String mcrid, String userid, String workflowProcessType, int mode){
		if(mode == MCRWorkflowConstants.PERMISSION_MODE_DEFAULT){
			MCRObjectID objID = new MCRObjectID(mcrid);
			for (int i = 0; i < defaultPermissionTypes.length; i++) {
				String propName = new StringBuffer("MCR.WorkflowEngine.defaultACL.")
					.append(objID.getTypeId()).append(".").append(defaultPermissionTypes[i]).append(".")
					.append(workflowProcessType).toString();
				
				String strRule = config.getString(propName,"<condition format=\"xml\"><boolean operator=\"false\" /></condition>");
				strRule = strRule.replaceAll("\\$\\{user\\}",userid);
				Element rule = (Element)MCRXMLHelper.parseXML(strRule).getRootElement().detach();
				String permissionType = defaultPermissionTypes[i];
				if(AI.hasRule(objID.getId(), permissionType )){
					AI.updateRule(objID.getId(), permissionType, rule, "");
				}else{
					AI.addRule(objID.getId(), permissionType, rule, "");
				}
			}		
		}
		if(mode==MCRWorkflowConstants.PERMISSION_MODE_PUBLISH){
			String strRule =config.getString("MCR.AccessRule.STANDARD-READ-RULE","<condition format=\"xml\"><boolean operator=\"true\" /></condition>");
			Element rule = (Element)MCRXMLHelper.parseXML(strRule).getRootElement().detach();
			String permissionType = "read";

			if(AI.hasRule(mcrid, permissionType )){
				AI.updateRule(mcrid, permissionType, rule, "");
			}else{
				AI.addRule(mcrid, permissionType, rule, "");
			}			
		}
		if(mode==MCRWorkflowConstants.PERMISSION_MODE_EDITING){
			String strRule = MCRConfiguration.instance().getString("MCR.WorkflowEngine.defaultACL.editorMode."+workflowProcessType, "<condition format=\"xml\"><boolean operator=\"or\"><condition field=\"group\" operator=\"=\" value=\"editorgroup1\" /></boolean></condition>");
			Element editorModeRule = (Element)MCRXMLHelper.parseXML(strRule).getRootElement().detach();
			List permissions = AI.getPermissionsForID(mcrid);
			for (Iterator it2 = permissions.iterator(); it2.hasNext();) {
				String permissionType = (String)it2.next();
				if(AI.hasRule(mcrid, permissionType )){
					AI.updateRule(mcrid, permissionType, editorModeRule, "");
				}else{
					AI.addRule(mcrid, permissionType, editorModeRule, "");
				}
			}		
		}
		
		if(mode==MCRWorkflowConstants.PERMISSION_MODE_CREATORRREAD){
			MCRObjectID objID = new MCRObjectID(mcrid);
			String propName = new StringBuffer("MCR.WorkflowEngine.defaultACL.")
			.append(objID.getTypeId()).append(".").append("read").append(".")
			.append(workflowProcessType).toString();
			String strReadRule = MCRConfiguration.instance().getString(propName);
			String x = strReadRule.replaceAll("\\$\\{user\\}", userid);
			Element readRule = (Element)MCRXMLHelper.parseXML(x).getRootElement().detach();
			AI.addRule(mcrid, "read", readRule, "");
		}
		
	}
	
}
