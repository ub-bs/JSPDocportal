package org.mycore.frontend.jsp.stripes.actions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
import org.apache.commons.lang3.StringUtils;
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
import org.mycore.datamodel.metadata.MCRMetaLinkID;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.services.i18n.MCRTranslation;
import org.w3c.dom.Document;

@UrlBinding("/editDerivates.action")
public class EditDerivatesAction extends MCRAbstractStripesAction implements ActionBean {
	private static Logger LOGGER = Logger.getLogger(EditDerivatesAction.class);
	ForwardResolution fwdResolution = new ForwardResolution(
			"/content/workspace/edit-derivates.jsp");

	private static DocumentBuilder DOC_BLDR;
	private List<String> messages = new ArrayList<String>();
	private String mcrobjid;
	private String taskid;
	private String wfTitle;

	
	static{
		try{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DOC_BLDR = dbf.newDocumentBuilder();
		}
		catch(ParserConfigurationException e){
			LOGGER.debug(e);
		}
	}
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
		for(String s: getContext().getRequest().getParameterMap().keySet()){
			if(s.startsWith("doCreateNewDerivate-task_")){
				taskid = s.substring(s.indexOf("_")+1);
				mcrobjid = ts.getVariable(taskid, MCRActivitiMgr.WF_VAR_MCR_OBJECT_ID, String.class);
				createNewDerivate();
			}
			//doSaveDerivateMeta-task_${actionBean.taskid}-derivate_${derID}
			if(s.startsWith("doSaveDerivateMeta-")){
				int start = s.indexOf("task_")+5;
				taskid = s.substring(start, s.indexOf("-", start));
				mcrobjid = ts.getVariable(taskid, MCRActivitiMgr.WF_VAR_MCR_OBJECT_ID, String.class);
				start = s.indexOf("derivate_")+9;
				String derid = s.substring(start);
				saveDerivateMetadata(taskid, derid, getContext().getRequest());
			}
			
			//doAddFile-task_${actionBean.taskid}-derivate_${derID}
			if(s.startsWith("doAddFile-")){
				int start = s.indexOf("task_")+5;
				taskid = s.substring(start, s.indexOf("-", start));
				mcrobjid = ts.getVariable(taskid, MCRActivitiMgr.WF_VAR_MCR_OBJECT_ID, String.class);
				start = s.indexOf("derivate_")+9;
				String derid = s.substring(start);
				addFileToDerivate(taskid, derid, getContext().getRequest());
			}
			
			//doDeleteFile-task_${actionBean.taskid}-derivate_${derID}-file_${f}
			if(s.startsWith("doDeleteFile-")){
				int start = s.indexOf("task_")+5;
				taskid = s.substring(start, s.indexOf("-", start));
				mcrobjid = ts.getVariable(taskid, MCRActivitiMgr.WF_VAR_MCR_OBJECT_ID, String.class);
				start = s.indexOf("derivate_")+9;
				String derid = s.substring(start, s.indexOf("-", start));
				start = s.indexOf("file_")+5;
				String file = s.substring(start);
				deleteFileFromDerivate(taskid, derid, file);
			}
			
			//doRenameFile-task_${actionBean.taskid}-derivate_${derID}-file_${f}
			if(s.startsWith("doRenameFile-")){
				int start = s.indexOf("task_")+5;
				taskid = s.substring(start, s.indexOf("-", start));
				mcrobjid = ts.getVariable(taskid, MCRActivitiMgr.WF_VAR_MCR_OBJECT_ID, String.class);
				start = s.indexOf("derivate_")+9;
				String derid = s.substring(start, s.indexOf("-", start));
				start = s.indexOf("file_")+5;
				String file = s.substring(start);
				renameFileInDerivate(taskid, derid, file, getContext().getRequest());
			}
			
			//doDeleteDerivate-task_${actionBean.taskid}-derivate_${derID}
			if(s.startsWith("doDeleteDerivate-")){
				int start = s.indexOf("task_")+5;
				taskid = s.substring(start, s.indexOf("-", start));
				mcrobjid = ts.getVariable(taskid, MCRActivitiMgr.WF_VAR_MCR_OBJECT_ID, String.class);
				start = s.indexOf("derivate_")+9;
				String derid = s.substring(start);
				deleteDerivate(taskid, derid);
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
	
	//Request-Parameter:
	//Submit: doSaveDerivateMeta-task_${actionBean.taskid}-derivate_${derID}
	//Label: saveDerivateMeta_label-task_${actionBean.taskid}-derivate_${derID}
	//Title: saveDerivateMeta_title-task_${actionBean.taskid}-derivate_${derID}
	private void saveDerivateMetadata(String taskid, String derid, HttpServletRequest request){
		String label = request.getParameter("saveDerivateMeta_label-task_"+taskid+"-derivate_"+derid);
		String title = request.getParameter("saveDerivateMeta_title-task_"+taskid+"-derivate_"+derid);
		
		MCRDerivate der = MCRActivitiUtils.loadMCRDerivateFromWorkflowDirectory(MCRObjectID.getInstance(mcrobjid), MCRObjectID.getInstance(derid));
		
		if(!StringUtils.isBlank(label)){
			der.setLabel(label);
		}
		if(!StringUtils.isBlank(title)){
			der.getService().removeFlags("title");
			der.getService().addFlag("title", title);
		}
		
		MCRActivitiUtils.saveMCRDerivateToWorkflowDirectory(der);
		
		MCRObject mcrObj = MCRActivitiUtils.loadMCRObjectFromWorkflowDirectory(MCRObjectID.getInstance(mcrobjid));
		for(MCRMetaLinkID link: mcrObj.getStructure().getDerivates()){
			if(link.getXLinkHrefID().equals(der.getId())){
				link.setXLinkLabel(label);
				break;
			}
		}
		MCRActivitiUtils.saveMCRObjectToWorkflowDirectory(mcrObj);
	}
	
	
	//File: addFile_file-task_${actionBean.taskid}-derivate_${derID}
	private void deleteFileFromDerivate(String taskid, String derid, String fileName){
		MCRDerivate der = MCRActivitiUtils.loadMCRDerivateFromWorkflowDirectory(MCRObjectID.getInstance(mcrobjid), MCRObjectID.getInstance(derid));
		File derDir = new File(new File(MCRActivitiUtils.getWorkflowDirectory(MCRObjectID.getInstance(mcrobjid)), mcrobjid), der.getId().toString());
		derDir.mkdirs();
		File f = new File(derDir, fileName);
		try{
			Files.delete(f.toPath());
		}
		catch(IOException e){
			LOGGER.error(e);
		}
	}
	
	//File: renameFile_new-task_${actionBean.taskid}-derivate_${derID}-file_${f}
		private void renameFileInDerivate(String taskid, String derid, String fileName, HttpServletRequest request){
			MCRDerivate der = MCRActivitiUtils.loadMCRDerivateFromWorkflowDirectory(MCRObjectID.getInstance(mcrobjid), MCRObjectID.getInstance(derid));
			File derDir = new File(new File(MCRActivitiUtils.getWorkflowDirectory(MCRObjectID.getInstance(mcrobjid)), mcrobjid), der.getId().toString());
			derDir.mkdirs();
			File f = new File(derDir, fileName);
			String newName = request.getParameter("renameFile_new-task_"+taskid+"-derivate_"+derid+"-file_"+fileName);
			if(!StringUtils.isBlank(newName)){
				newName = newName.replace("ä",  "ae").replace("ö", "oe").replace("ü", "ue").replace("ß", "ss");
				newName = newName.replace("Ä", "AE").replace("Ö", "OE").replace("Ü", "OE");
				newName = newName.replaceAll("[^a-zA-Z0-9_\\-\\.]","_");
		
				File fNew = new File(f.getParentFile(), newName);
				f.renameTo(fNew);
			}
		}
	
	//File: addFile_file-task_${actionBean.taskid}-derivate_${derID}
	private void deleteDerivate(String taskid, String derid){
		MCRObject mcrObj = MCRActivitiUtils.loadMCRObjectFromWorkflowDirectory(MCRObjectID.getInstance(mcrobjid));
		MCRObjectID derID = MCRObjectID.getInstance(derid);
		mcrObj.getStructure().removeDerivate(derID);
		MCRActivitiUtils.saveMCRObjectToWorkflowDirectory(mcrObj);
		MCRActivitiUtils.deleteMCRDerivateFromWorkflowDirectory(mcrObj.getId(), derID);
	}

	//File: addFile_file-task_${actionBean.taskid}-derivate_${derID}
	private void addFileToDerivate(String taskid, String derid, HttpServletRequest request){
		FileBean fb = ((StripesRequestWrapper)getContext().getRequest()).getFileParameterValue("addFile_file-task_"+ taskid +"-derivate_"+derid);
		if(fb!=null){
			MCRDerivate der = MCRActivitiUtils.loadMCRDerivateFromWorkflowDirectory(MCRObjectID.getInstance(mcrobjid), MCRObjectID.getInstance(derid));
			File derDir = new File(new File(MCRActivitiUtils.getWorkflowDirectory(MCRObjectID.getInstance(mcrobjid)), mcrobjid), der.getId().toString());
			derDir.mkdirs();
			try{
				Files.copy(fb.getInputStream(), new File(derDir, fb.getFileName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
			}
			catch(IOException e){
				LOGGER.error(e);
			}
		}
	}

	private void createNewDerivate(){
		TaskService ts = MCRActivitiMgr.getWorfklowProcessEngine().getTaskService();
		MCRDerivate der =  null;
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
	public Document getMcrobjXML(){
		Document doc = null;
		try{
			doc = DOC_BLDR.parse(new File(MCRActivitiUtils.getWorkflowDirectory(MCRObjectID.getInstance(mcrobjid)), mcrobjid+".xml"));
		}
		catch(Exception e){
			LOGGER.error(e);
		}
		return doc;
	}
	
	public Map<String, Document> getDerivateXMLs(){
		HashMap<String, Document> result = new HashMap<String, Document>();
		File baseDir = new File(MCRActivitiUtils.getWorkflowDirectory(MCRObjectID.getInstance(mcrobjid)), mcrobjid);
		MCRObject obj = MCRActivitiUtils.loadMCRObjectFromWorkflowDirectory(MCRObjectID.getInstance(mcrobjid));
		try{
			for(MCRMetaLinkID derID: obj.getStructure().getDerivates()){
				String id = derID.getXLinkHref();
				try{ 
					Document doc = DOC_BLDR.parse(new File(baseDir, id+".xml"));
					result.put(id, doc);
				}
				catch(Exception e){
					LOGGER.error(e);
				}
			}
		} catch(Exception e){
			LOGGER.error(e);
		}
		return result;
	}
	
	public Map<String, List<String>> getDerivateFiles(){
		HashMap<String, List<String>> result = new HashMap<String, List<String>>();
		File baseDir = new File(MCRActivitiUtils.getWorkflowDirectory(MCRObjectID.getInstance(mcrobjid)), mcrobjid);
		MCRObject obj = MCRActivitiUtils.loadMCRObjectFromWorkflowDirectory(MCRObjectID.getInstance(mcrobjid));
		try{
			for(MCRMetaLinkID derID: obj.getStructure().getDerivates()){
				String id = derID.getXLinkHref();
				List<String> fileNames = new ArrayList<String>();
				try{
					File root = new File(baseDir, id);
					for(File f: root.listFiles()){
						fileNames.add(f.getName());
					}
				}
				catch(Exception e){
					LOGGER.error(e);
				}
				result.put(id, fileNames);
			}
		} catch(Exception e){
			LOGGER.error(e);
		}
		return result;
	}
}
