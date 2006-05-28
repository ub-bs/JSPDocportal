package org.mycore.frontend.workflowengine.strategies;

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
					AI.addRule(objID.getId(), defaultPermissionTypes[i], rule, "");
				}
			}		
		}
	}
	
}
