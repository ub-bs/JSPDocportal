package org.mycore.activiti.workflows.create_object_simple;

import org.activiti.engine.delegate.DelegateExecution;
import org.mycore.activiti.MCRActivitiMgr;
import org.mycore.activiti.MCRActivitiUtils;
import org.mycore.datamodel.metadata.MCRMetadataManager;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.datamodel.metadata.MCRObjectMetadata;

public abstract class MCRAbstractWorkflowMgr implements MCRWorkflowMgr{
	
	public MCRObject createMCRObject(DelegateExecution execution){
		String base = execution.getVariable(MCRActivitiMgr.WF_VAR_PROJECT_ID).toString()+"_"+execution.getVariable(MCRActivitiMgr.WF_VAR_OBJECT_TYPE).toString();
		MCRObject mcrObj = new MCRObject();

		mcrObj.setSchema("datamodel-"+execution.getVariable(MCRActivitiMgr.WF_VAR_OBJECT_TYPE).toString()+".xsd");
		mcrObj.setId(MCRObjectID.getNextFreeId(base));
		mcrObj.setLabel(mcrObj.getId().toString());
		mcrObj.setVersion("2.0");
		MCRObjectMetadata defaultMetadata = getDefaultMetadata();
		if(defaultMetadata!=null){
			mcrObj.getMetadata().appendMetadata(defaultMetadata);
		}
		mcrObj.getService().removeFlags("status");
		mcrObj.getService().addFlag("status", "new");
		MCRMetadataManager.create(mcrObj);
		execution.setVariable(MCRActivitiMgr.WF_VAR_OBJECT_ID, mcrObj.getId().toString());

		MCRActivitiUtils.saveToWorkflowDirectory(mcrObj);
		return mcrObj;
		
	}
	
	/**
	 * provide default metadata   = initial metadata for new mycore object
	 * null is allowed;
	 * 
	 * @return
	 */
	public abstract MCRObjectMetadata getDefaultMetadata();
}
