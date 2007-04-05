package org.mycore.frontend.workflowengine.strategies;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import org.mycore.common.MCRConfiguration;

final public class MCRWorkflowDirectoryManager {
	
	private static final HashMap<String, Object> editWorkflowDirectories ;
	
	static{
		editWorkflowDirectories = new HashMap<String, Object>();
		Properties props = MCRConfiguration.instance().getProperties("MCR.WorkflowEngine.EditDirectory.");
		for(Enumeration e = props.keys(); e.hasMoreElements();){
			String propKey = (String)e.nextElement();
			String hashKey = propKey.substring("MCR.WorkflowEngine.EditDirectory.".length());
			editWorkflowDirectories.put(hashKey,props.getProperty(propKey));
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
