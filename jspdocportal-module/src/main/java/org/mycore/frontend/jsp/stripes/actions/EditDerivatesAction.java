package org.mycore.frontend.jsp.stripes.actions;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.FileBean;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.controller.StripesRequestWrapper;

import org.activiti.engine.TaskService;
import org.apache.log4j.Logger;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.mycore.activiti.MCRActivitiMgr;
import org.mycore.activiti.MCRActivitiUtils;
import org.mycore.activiti.workflows.create_object_simple.MCRWorkflowMgr;
import org.mycore.common.MCRSessionMgr;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.datamodel.metadata.MCRDerivate;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.services.i18n.MCRTranslation;

@UrlBinding("/editDerivates.action")
public class EditDerivatesAction extends MCRAbstractStripesAction implements ActionBean {
	private static Logger LOGGER = Logger.getLogger(EditDerivatesAction.class);
	ForwardResolution fwdResolution = new ForwardResolution(
			"/content/workspace/edit-derivates.jsp");

	private List<String> messages = new ArrayList<String>();
	private String mcrobjid;
	private String taskid;
	private String wfTitle;

	public EditDerivatesAction() {

	}

	@Before(stages = LifecycleStage.BindingAndValidation)
	public void rehydrate() {
		super.rehydrate();
		if (getContext().getRequest().getParameter("taskid") != null) {
			taskid = getContext().getRequest().getParameter("taskid");
		}
		if (getContext().getRequest().getParameter("mcrobjid") != null) {
			mcrobjid = getContext().getRequest().getParameter("mcrobjid");
		}
	}

	@DefaultHandler
	public Resolution defaultRes() {
		TaskService ts = MCRActivitiMgr.getWorfklowProcessEngine().getTaskService();
		MCRDerivate der =  null;
		for(String s: getContext().getRequest().getParameterMap().keySet()){
			if(s.startsWith("doCreateNewDerivate-task_")){
				taskid = s.substring(s.indexOf("_")+1);
				mcrobjid = ts.getVariable(taskid, MCRActivitiMgr.WF_VAR_MCR_OBJECT_ID, String.class);

				try{
					boolean doCommitTransaction = false;
					if(!MCRSessionMgr.getCurrentSession().isTransactionActive()){
						doCommitTransaction = true;
						MCRSessionMgr.getCurrentSession().beginTransaction();
					}

					MCRWorkflowMgr wfm = MCRActivitiMgr.getWorkflowMgr(ts.createTaskQuery().taskId(taskid).singleResult().getProcessInstanceId());
					FileBean fb = ((StripesRequestWrapper)getContext().getRequest()).getFileParameterValue("newDerivate_file-task_"+taskid);
					
					der = wfm.createMCRDerivate(MCRObjectID.getInstance(mcrobjid), getContext().getRequest().getParameter("newDerivate_label-task_"+taskid), getContext().getRequest().getParameter("newDerivate_title-task_"+taskid));

					if(fb!=null){
						File derDir = new File(new File(MCRActivitiUtils.getWorkflowDirectory(MCRObjectID.getInstance(mcrobjid)), mcrobjid), der.getId().toString());
						derDir.mkdirs();
						Files.copy(fb.getInputStream(), new File(derDir, fb.getFileName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
					
						der.getDerivate().getInternals().setSourcePath(derDir.toString());
						MCRActivitiUtils.saveMCRDerivateToWorkflowDirectory(der);
					}

					if(doCommitTransaction){
						MCRSessionMgr.getCurrentSession().commitTransaction();
					}
				}
				catch(Exception e){
					LOGGER.error(e);
				}
			}		
		}

		if(taskid!=null && mcrobjid!=null){
			boolean doCommitTransaction = false;
			if(!MCRSessionMgr.getCurrentSession().isTransactionActive()){
				doCommitTransaction = true;
				MCRSessionMgr.getCurrentSession().beginTransaction();
			}

			updateWFObjectMetadata(taskid, MCRObjectID.getInstance(mcrobjid));

			if(doCommitTransaction){
				MCRSessionMgr.getCurrentSession().commitTransaction();
			}
		}
		else{
			messages.add("URL Parameter taskid was not set!");
		}
		return fwdResolution;

	}

	private void updateWFObjectMetadata(String taskid, MCRObjectID mcrObjID){
		if(mcrobjid==null){
			LOGGER.error("WFObject could not be read.");
		}
		
		MCRObject mcrObj = MCRActivitiUtils.loadMCRObjectFromWorkflowDirectory(mcrObjID);
		
		String xpTitle = MCRConfiguration.instance().getString("MCR.Activiti.MCRObject.Display.Title.XPath."+mcrObjID.getBase(), "/mycoreobject/@ID");
		XPathExpression<String> xpath =
				XPathFactory.instance().compile(xpTitle, Filters.fstring());
		String txt = xpath.evaluateFirst(mcrObj.createXML());
		if (txt != null) {
			wfTitle = txt;
			MCRActivitiMgr.getWorfklowProcessEngine().getTaskService().setVariable(taskid, MCRActivitiMgr.WF_VAR_DISPLAY_TITLE, wfTitle);
		}
		else{
			wfTitle=MCRTranslation.translate("Wf.common.newObject");
		}
	}
		
	public String getMcrobjid_base() {
		return MCRObjectID.getInstance(mcrobjid).getBase();
	}
	public String getMcrobjid(){
		return mcrobjid;
	}
	public String getTaskid(){
		return taskid;
	}
}
