package org.mycore.activiti.workflows.create_object_simple;

import java.io.File;
import java.io.IOException;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.mycore.access.MCRAccessManager;
import org.mycore.activiti.MCRActivitiMgr;
import org.mycore.activiti.MCRActivitiUtils;
import org.mycore.common.MCRPersistenceException;
import org.mycore.common.MCRSessionMgr;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.datamodel.classifications2.MCRCategoryID;
import org.mycore.datamodel.common.MCRActiveLinkException;
import org.mycore.datamodel.metadata.MCRDerivate;
import org.mycore.datamodel.metadata.MCRMetaIFS;
import org.mycore.datamodel.metadata.MCRMetaLinkID;
import org.mycore.datamodel.metadata.MCRMetadataManager;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.datamodel.metadata.MCRObjectMetadata;
import org.xml.sax.SAXParseException;

public abstract class MCRAbstractWorkflowMgr implements MCRWorkflowMgr {
	private static Logger LOGGER = Logger.getLogger(MCRAbstractWorkflowMgr.class);

	@Override
	public MCRObject createMCRObject(DelegateExecution execution) {
		String base = execution.getVariable(MCRActivitiMgr.WF_VAR_PROJECT_ID).toString() + "_"
				+ execution.getVariable(MCRActivitiMgr.WF_VAR_OBJECT_TYPE).toString();
		MCRObject mcrObj = new MCRObject();

		mcrObj.setSchema("datamodel-" + execution.getVariable(MCRActivitiMgr.WF_VAR_OBJECT_TYPE).toString() + ".xsd");
		mcrObj.setId(MCRObjectID.getNextFreeId(base));
		mcrObj.setLabel(mcrObj.getId().toString());
		mcrObj.setVersion("2.0");
		MCRObjectMetadata defaultMetadata = getDefaultMetadata();
		if (defaultMetadata != null) {
			mcrObj.getMetadata().appendMetadata(defaultMetadata);
		}
		mcrObj.getService().setState(new MCRCategoryID(MCRConfiguration.instance().getString("MCR.Metadata.Service.State.Classification.ID", "state"), "new"));
		mcrObj.getStructure();
		MCRMetadataManager.create(mcrObj);
		execution.setVariable(MCRActivitiMgr.WF_VAR_MCR_OBJECT_ID, mcrObj.getId().toString());

		MCRActivitiUtils.saveMCRObjectToWorkflowDirectory(mcrObj);

		return mcrObj;
	}
	
	@Override
	public MCRObject loadMCRObject(DelegateExecution execution) {
		MCRObject mcrObj = MCRMetadataManager.retrieveMCRObject(MCRObjectID.getInstance(String.valueOf(execution.getVariable(MCRActivitiMgr.WF_VAR_MCR_OBJECT_ID))));
		mcrObj.getService().setState(new MCRCategoryID(MCRConfiguration.instance().getString("MCR.Metadata.Service.State.Classification.ID", "state"), "review"));
		try{
			boolean doCommitTransaction = false;
			if(!MCRSessionMgr.getCurrentSession().isTransactionActive()){
				doCommitTransaction = true;
				MCRSessionMgr.getCurrentSession().beginTransaction();
			}
			MCRMetadataManager.update(mcrObj);
			if(doCommitTransaction){
				MCRSessionMgr.getCurrentSession().commitTransaction();
			}
		}
		catch(MCRActiveLinkException e){
			LOGGER.error(e);
		}
		MCRActivitiUtils.saveMCRObjectToWorkflowDirectory(mcrObj);
		execution.setVariable(MCRActivitiMgr.WF_VAR_MCR_OBJECT_ID, mcrObj.getId().toString());
		return mcrObj;
	}
	
	@Override
	public MCRObject dropMCRObject(DelegateExecution execution) {
		MCRObject mcrObj = MCRMetadataManager.retrieveMCRObject(MCRObjectID.getInstance(String.valueOf(execution.getVariable(MCRActivitiMgr.WF_VAR_MCR_OBJECT_ID))));
		mcrObj.getService().setState(new MCRCategoryID(MCRConfiguration.instance().getString("MCR.Metadata.Service.State.Classification.ID", "state"), "deleted"));
		try{
			boolean doCommitTransaction = false;
			if(!MCRSessionMgr.getCurrentSession().isTransactionActive()){
				doCommitTransaction = true;
				MCRSessionMgr.getCurrentSession().beginTransaction();
			}
			MCRMetadataManager.delete(mcrObj);
			if(doCommitTransaction){
				MCRSessionMgr.getCurrentSession().commitTransaction();
			}
		}
		catch(MCRActiveLinkException e){
			LOGGER.error(e);
		}
		MCRActivitiUtils.saveMCRObjectToWorkflowDirectory(mcrObj);
		return mcrObj;
	}
	
	@Override
	public MCRDerivate createMCRDerivate(MCRObjectID owner, String label, String title){
		MCRDerivate der = new MCRDerivate();
		der.setId(MCRObjectID.getInstance(owner.getProjectId()+"_derivate_0"));
		der.setSchema("datamodel-derivate.xsd");
		der.getDerivate().setLinkMeta(new MCRMetaLinkID("linkmeta", owner, null, null));
		
		der.getDerivate().setInternals(new MCRMetaIFS("internal", null));
		der.getService().setState(new MCRCategoryID(MCRConfiguration.instance().getString("MCR.Metadata.Service.State.Classification.ID", "state"), "new"));
		if(!StringUtils.isBlank(title)){
			der.getService().addFlag("title", title);
		}
		if(!StringUtils.isBlank(label)){
			der.setLabel(label);
		}
		else{
			der.setLabel(der.getId().toString());
		}
		
		
		if (MCRAccessManager.checkPermission("create-" + owner.getBase())
				|| MCRAccessManager.checkPermission("create-" + owner.getTypeId())) {
			if (der.getId().getNumberAsInteger() == 0) {

				MCRObjectID newDerID = MCRObjectID.getNextFreeId(der.getId().getBase());
				if (der.getLabel().equals(der.getId()))
					der.setLabel(newDerID.toString());
				der.setId(newDerID);
			
				MCRMetadataManager.create(der);
				MCRActivitiUtils.saveMCRDerivateToWorkflowDirectory(der);
			}
			MCRObject mcrObj = MCRActivitiUtils.loadMCRObjectFromWorkflowDirectory(owner);
			mcrObj.getStructure().addDerivate(new MCRMetaLinkID("derobject", der.getId(), der.getLabel(), null));
			MCRActivitiUtils.saveMCRObjectToWorkflowDirectory(mcrObj);

		} else {
			throw new MCRPersistenceException("You do not have \"create\" permission on " + der.getId().getTypeId() + ".");
		}
		return der;
	}

	@Override
	public boolean deleteProcessInstance(String processInstanceId) {
		RuntimeService rs = MCRActivitiMgr.getWorfklowProcessEngine().getRuntimeService();
		String id = String.valueOf(rs.getVariable(processInstanceId, MCRActivitiMgr.WF_VAR_MCR_OBJECT_ID));
		rs.deleteProcessInstance(processInstanceId, "Deletion requested by admin");
		if (!id.equals("null")) {
			MCRObjectID mcrObjID = MCRObjectID.getInstance(id);
			return resetState(mcrObjID);
		}
		return false;
	}

	/**
	 * provide default metadata = initial metadata for new mycore object null is
	 * allowed;
	 * 
	 * @return
	 */
	public abstract MCRObjectMetadata getDefaultMetadata();

	@Override
	public boolean commitMCRObject(DelegateExecution execution) {
		String id = String.valueOf(execution.getVariable(MCRActivitiMgr.WF_VAR_MCR_OBJECT_ID));
		if (!id.equals("null")) {
			MCRObjectID mcrObjID = MCRObjectID.getInstance(id);
			File wfFile = new File(MCRActivitiUtils.getWorkflowDirectory(mcrObjID), mcrObjID.toString() + ".xml");
			try {
				MCRObject mcrWFObj = new MCRObject(wfFile.toURI());
				MCRObject mcrObj = MCRMetadataManager.retrieveMCRObject(mcrObjID);
				MCRObjectMetadata mcrObjMeta = mcrObj.getMetadata();
				while (mcrObjMeta.size() > 0) {
					mcrObjMeta.removeMetadataElement(0);
				}
				mcrObjMeta.appendMetadata(mcrWFObj.getMetadata());
				
				mcrObj.getService().setState(new MCRCategoryID(MCRConfiguration.instance().getString("MCR.Metadata.Service.State.Classification.ID", "state"), "published"));
				
				
				boolean doCommitTransaction = false;
				if(!MCRSessionMgr.getCurrentSession().isTransactionActive()){
					doCommitTransaction = true;
					MCRSessionMgr.getCurrentSession().beginTransaction();
				}
				MCRMetadataManager.update(mcrObj);
				if(doCommitTransaction){
					MCRSessionMgr.getCurrentSession().commitTransaction();
				}

			} catch (IOException | SAXParseException | MCRActiveLinkException e) {
				LOGGER.error(e);
			}
			
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean rollbackMCRObject(DelegateExecution execution) {
		String id = String.valueOf(execution.getVariable(MCRActivitiMgr.WF_VAR_MCR_OBJECT_ID));
		if (!id.equals("null")) {
			MCRObjectID mcrObjID = MCRObjectID.getInstance(id);
			return resetState(mcrObjID);
		} else {
			return false;
		}
	}
	
	@Override
	public boolean validateMCRObject(DelegateExecution execution) {
		String id = String.valueOf(execution.getVariable(MCRActivitiMgr.WF_VAR_MCR_OBJECT_ID));
		if (!id.equals("null")) {
			MCRObjectID mcrObjID = MCRObjectID.getInstance(id);
			String result = validate(mcrObjID);
			if(result==null){
				execution.setVariable(MCRActivitiMgr.WF_VAR_VALIDATION_RESULT, true);
				if(execution.hasVariable(MCRActivitiMgr.WF_VAR_VALIDATION_MESSAGE)){
					execution.removeVariable(MCRActivitiMgr.WF_VAR_VALIDATION_MESSAGE);
				}
				return true;
			}
			else{
				execution.setVariable(MCRActivitiMgr.WF_VAR_VALIDATION_RESULT, false);
				execution.setVariable(MCRActivitiMgr.WF_VAR_VALIDATION_MESSAGE, result);
				return false;
			}
		}	
		return false;
	}
	
	protected abstract String validate(MCRObjectID mcrObjID);
	
	public boolean resetState(MCRObjectID mcrObjID){
		if (MCRMetadataManager.exists(mcrObjID)) {
			MCRObject mcrObj = MCRMetadataManager.retrieveMCRObject(mcrObjID);
			if(mcrObj.getService().getState()==null){
				return true;
			}
			String state = mcrObj.getService().getState().getID();
			if(state.equals("review")){
				mcrObj.getService().setState(new MCRCategoryID(MCRConfiguration.instance().getString("MCR.Metadata.Service.State.Classification.ID", "state"), "published"));	
				try {
					boolean doCommitTransaction = false;
					if(!MCRSessionMgr.getCurrentSession().isTransactionActive()){
						doCommitTransaction = true;
						MCRSessionMgr.getCurrentSession().beginTransaction();
					}
					MCRMetadataManager.update(mcrObj);
					if(doCommitTransaction){
						MCRSessionMgr.getCurrentSession().commitTransaction();
					}
				} catch (MCRActiveLinkException e) {
					LOGGER.error(e);
					return false;
				}
			}
			if(state.equals("new")){
				try{
					boolean doCommitTransaction = false;
					if(!MCRSessionMgr.getCurrentSession().isTransactionActive()){
						doCommitTransaction = true;
						MCRSessionMgr.getCurrentSession().beginTransaction();
					}
					MCRMetadataManager.delete(mcrObj);
					if(doCommitTransaction){
						MCRSessionMgr.getCurrentSession().commitTransaction();
					}
				}
				catch(MCRActiveLinkException e){
					LOGGER.error(e);
					return false;
				}
			}
			return true;
		}
		return false;
	}
}
