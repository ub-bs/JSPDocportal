package org.mycore.frontend.workflowengine.strategies;

import java.util.HashMap;
import java.util.Map;

import org.mycore.common.config.MCRConfiguration;

final public class MCRWorkflowDirectoryManager {
	
	private static final HashMap<String, Object> editWorkflowDirectories ;
	
	static{
		editWorkflowDirectories = new HashMap<String, Object>();
		Map<String, String> props = MCRConfiguration.instance().getPropertiesMap("MCR.WorkflowEngine.EditDirectory.");
		for(String propKey: props.keySet()){
			String hashKey = propKey.substring("MCR.WorkflowEngine.EditDirectory.".length());
			editWorkflowDirectories.put(hashKey,props.get(propKey));
		}	

	}
	
	/**
	 * returns the directory, where the workflow-data of a given
	 *    documentType are saved
	 *    
	 * @param documentType
	 *    String a MyCoRe document type like author, document, institution
	 * @return
	 */	
	public static String getWorkflowDirectory(String documentType){
		return (String)editWorkflowDirectories.get(documentType);
	}
	
	public static HashMap getEditWorkflowDirectories(){
		return editWorkflowDirectories;
	}
}
