package org.mycore.frontend.jsp.stripes.actions;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.mycore.access.MCRAccessManager;
import org.mycore.activiti.MCRActivitiMgr;
import org.mycore.activiti.MCRActivitiUtils;
import org.mycore.common.MCRConstants;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.datamodel.metadata.MCRDerivate;
import org.mycore.datamodel.metadata.MCRMetaLinkID;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.MCRFrontendUtil;
import org.mycore.frontend.jsp.MCRHibernateTransactionWrapper;
import org.mycore.frontend.jsp.stripes.actions.util.MCRMODSGVKImporter;
import org.mycore.frontend.xeditor.MCREditorSession;
import org.mycore.frontend.xeditor.MCREditorSessionStore;
import org.mycore.frontend.xeditor.MCREditorSessionStoreFactory;
import org.mycore.services.i18n.MCRTranslation;
import org.mycore.user2.MCRUser;
import org.mycore.user2.MCRUserManager;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.controller.LifecycleStage;

@UrlBinding("/showWorkspace.action")
public class ShowWorkspaceAction extends MCRAbstractStripesAction implements ActionBean {
    private static Logger LOGGER = LogManager.getLogger(ShowWorkspaceAction.class);

    ForwardResolution fwdResolution = new ForwardResolution("/content/workspace/workspace.jsp");

    private List<String> messages = new ArrayList<String>();

    private String mcr_base = "";

    private List<Task> myTasks = new ArrayList<Task>();

    private List<Task> availableTasks = new ArrayList<Task>();

    private String editorPath;

    private String sourceURI;

    private String cancelURL;

    public ShowWorkspaceAction() {

    }

    @Before(stages = LifecycleStage.BindingAndValidation)
    public void rehydrate() {
        super.rehydrate();
        if (getContext().getRequest().getParameter("mcr_base") != null) {
            mcr_base = getContext().getRequest().getParameter("mcr_base");
        }
    }

    @DefaultHandler
    public Resolution defaultRes() {
        if (mcr_base == null) {
            return new RedirectResolution("/");
        }
       
        
        //open XEditor
        if (getContext().getRequest().getParameter(MCREditorSessionStore.XEDITOR_SESSION_PARAM) != null) {
            String xEditorStepID = getContext().getRequest().getParameter(MCREditorSessionStore.XEDITOR_SESSION_PARAM);
            String sessionID = xEditorStepID.split("-")[0];
            MCREditorSession session = MCREditorSessionStoreFactory.getSessionStore().getSession(sessionID);

            if (session == null) {
                LOGGER.error("Editor session invalid !!!");
                //ToDo - Forward to error page
                //String msg = getErrorI18N("xeditor.error", "noSession", sessionID);
                try {
                    getContext().getResponse().sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                            "EditorSession not found: " + sessionID);
                } catch (IOException e) {
                    LOGGER.error(e);
                }
                return null;
            }

            String mcrID = session.getEditedXML().getRootElement().getAttributeValue("ID");
            return editObject(mcrID, null);
        }
        
        try (MCRHibernateTransactionWrapper mtw = new MCRHibernateTransactionWrapper()) {
            String objectType = mcr_base.substring(mcr_base.indexOf("_") + 1);
            if (getContext().getRequest().getSession(false)==null || !MCRAccessManager.checkPermission("administrate-" + objectType)) {
                return new RedirectResolution("/login.action");
            }
        }

        for (String s : getContext().getRequest().getParameterMap().keySet()) {
            if (s.equals("doPublishAllTasks")) {
                publishAllTasks();
            }
            if (s.startsWith("doAcceptTask-task_")) {
                String id = s.substring(s.indexOf("_") + 1);
                acceptTask(id);
            }
            if (s.startsWith("doReleaseTask-task_")) {
                String id = s.substring(s.indexOf("_") + 1);
                releaseTask(id);
            }
            //doFollowt-task_[ID]-[mcrObjID]
            if (s.startsWith("doGoto-task_")) {
                String id = s.substring(s.indexOf("-") + 1);
                String taskID = id.substring(0, id.indexOf("-"));
                taskID = taskID.substring(taskID.indexOf("_") + 1);
                String transactionID = id.substring(id.indexOf("-") + 1);
                followTransaction(taskID, transactionID);
            }

            //doEditObject-task_[ID]-[mcrObjID]
            if (s.startsWith("doEditObject-task_")) {
                String id = s.substring(s.indexOf("-") + 1);
                String taskID = id.substring(0, id.indexOf("-"));
                taskID = taskID.substring(taskID.indexOf("_") + 1);
                String mcrObjID = id.substring(id.indexOf("-") + 1);
                return editObject(mcrObjID, taskID);
            }

            //doEditDerivates-task_[ID]-[mcrObjID]
            if (s.startsWith("doEditDerivates-task_")) {
                String id = s.substring(s.indexOf("-") + 1);
                String taskID = id.substring(0, id.indexOf("-"));
                taskID = taskID.substring(taskID.indexOf("_") + 1);
                String mcrObjID = id.substring(id.indexOf("-") + 1);
                ForwardResolution res = new ForwardResolution(
                        "/editDerivates.action?taskid=" + taskID + "&mcrobjid=" + mcrObjID);
                return res;
            }

            //doImportMODS-task_[ID]-[mcrObjID]
            if (s.startsWith("doImportMODS-task_")) {
                String id = s.substring(s.indexOf("-") + 1);
                String taskID = id.substring(0, id.indexOf("-"));
                taskID = taskID.substring(taskID.indexOf("_") + 1);
                String mcrObjID = id.substring(id.indexOf("-") + 1);

                importMODSFromGVK(mcrObjID);
            }
        }

        try (MCRHibernateTransactionWrapper mtw = new MCRHibernateTransactionWrapper()) {
            MCRUser user = MCRUserManager.getCurrentUser();

            String objectType = mcr_base.substring(mcr_base.indexOf("_") + 1);
            TaskService ts = MCRActivitiMgr.getWorfklowProcessEngine().getTaskService();
            myTasks = ts.createTaskQuery().taskAssignee(user.getUserID())
                    .processVariableValueEquals(MCRActivitiMgr.WF_VAR_OBJECT_TYPE, objectType).orderByTaskCreateTime()
                    .desc().list();

            for (Task t : myTasks) {
                updateWFObjectMetadata(t);
                updateWFDerivateList(t);
            }

            availableTasks = ts.createTaskQuery().taskCandidateUser(user.getUserID())
                    .processVariableValueEquals(MCRActivitiMgr.WF_VAR_OBJECT_TYPE, objectType).orderByTaskCreateTime()
                    .desc().list();

        }
        return fwdResolution;
    }

    public Resolution doCreateNewTask() {
        if (mcr_base != null) {
            try (MCRHibernateTransactionWrapper mtw = new MCRHibernateTransactionWrapper()) {
                String projectID = mcr_base.substring(0, mcr_base.indexOf("_"));
                String objectType = mcr_base.substring(mcr_base.indexOf("_") + 1);
                if (getContext().getRequest().getSession(false)!=null && MCRAccessManager.checkPermission("create-" + objectType)) {
                    Map<String, Object> variables = new HashMap<String, Object>();
                    variables.put(MCRActivitiMgr.WF_VAR_OBJECT_TYPE, objectType);
                    variables.put(MCRActivitiMgr.WF_VAR_PROJECT_ID, projectID);

                    RuntimeService rs = MCRActivitiMgr.getWorfklowProcessEngine().getRuntimeService();
                    //ProcessInstance pi = rs.startProcessInstanceByKey("create_object_simple", variables);
                    ProcessInstance pi = rs.startProcessInstanceByMessage("start_create", variables);
                    TaskService ts = MCRActivitiMgr.getWorfklowProcessEngine().getTaskService();
                    for (Task t : ts.createTaskQuery().processInstanceId(pi.getId()).list()) {
                        ts.setAssignee(t.getId(), MCRUserManager.getCurrentUser().getUserID());
                    }
                } else {
                    messages.add(MCRTranslation.translate("WF.messages.create.forbidden"));
                }
            }
        }

        return defaultRes();
    }

    private void acceptTask(String taskId) {
        LOGGER.debug("Accepted Task" + taskId);
        TaskService ts = MCRActivitiMgr.getWorfklowProcessEngine().getTaskService();
        ts.setAssignee(taskId, MCRUserManager.getCurrentUser().getUserID());
    }

    private void releaseTask(String taskId) {
        LOGGER.debug("Release Task" + taskId);
        TaskService ts = MCRActivitiMgr.getWorfklowProcessEngine().getTaskService();
        ts.setAssignee(taskId, null);
    }

    private Resolution editObject(String mcrID, String taskID) {
        MCRObjectID mcrObjID = MCRObjectID.getInstance(mcrID);
        editorPath = "/editor/metadata/editor-" + mcrObjID.getTypeId() + "-default.xed";
        Path wfFile = MCRActivitiUtils.getWorkflowObjectFile(mcrObjID);
        sourceURI = wfFile.toUri().toString();
        ForwardResolution res = new ForwardResolution("/content/editor/fullpageEditor.jsp");
        StringBuffer sbCancel = new StringBuffer(MCRFrontendUtil.getBaseURL() + "showWorkspace.action?");
        if (!mcr_base.isEmpty()) {
            sbCancel.append("&mcr_base=").append(mcr_base);
        }
        if (taskID != null) {
            sbCancel.append("#task_").append(taskID);
        }
        cancelURL = sbCancel.toString();

        return res;
    }

    private void followTransaction(String taskId, String transactionID) {
        TaskService ts = MCRActivitiMgr.getWorfklowProcessEngine().getTaskService();
        ts.setVariable(taskId, "goto", transactionID);
        
        if(transactionID.equals("edit_object.do_save")) {
           Task t = ts.createTaskQuery().taskId(taskId).singleResult();
           updateWFObjectMetadata(t);
           String mcrid = String.valueOf(ts.getVariable(t.getId(), MCRActivitiMgr.WF_VAR_MCR_OBJECT_ID));
           String title = String.valueOf(ts.getVariable(t.getId(),  MCRActivitiMgr.WF_VAR_DISPLAY_TITLE));
           String url = MCRFrontendUtil.getBaseURL()+"resolve/id/"+mcrid+"?_cache=clear";
           messages.add(MCRTranslation.translate("WF.messages.publish.completed",  title, url, url));
        }
        ts.complete(taskId);
    }

    private void updateWFObjectMetadata(Task t) {
        MCRObjectID mcrObjID = MCRObjectID.getInstance(String.valueOf(MCRActivitiMgr.getWorfklowProcessEngine()
                .getTaskService().getVariable(t.getId(), MCRActivitiMgr.WF_VAR_MCR_OBJECT_ID)));
        if (mcrObjID == null) {
            LOGGER.error("WFObject could not be read.");
        }

        //Title
        MCRObject mcrObj = MCRActivitiUtils.loadMCRObjectFromWorkflowDirectory(mcrObjID);
        String txt = null;
        try {
            String xpTitle = MCRConfiguration.instance()
                    .getString("MCR.Activiti.MCRObject.Display.Title.XPath." + mcrObjID.getBase(), null);
            if (xpTitle == null) {
                xpTitle = MCRConfiguration.instance().getString(
                        "MCR.Activiti.MCRObject.Display.Title.XPath.default_" + mcrObjID.getTypeId(),
                        "/mycoreobject/@ID");
            }
            XPathExpression<String> xpath = XPathFactory.instance().compile(xpTitle, Filters.fstring(), null,
                    MCRConstants.MODS_NAMESPACE);
            txt = xpath.evaluateFirst(mcrObj.createXML());
        } catch (Exception e) {
            LOGGER.error(e);
            txt = e.getMessage();
        }
        if (txt != null) {
            MCRActivitiMgr.getWorfklowProcessEngine().getTaskService().setVariable(t.getId(),
                    MCRActivitiMgr.WF_VAR_DISPLAY_TITLE, txt);
        } else {
            MCRActivitiMgr.getWorfklowProcessEngine().getTaskService().setVariable(t.getId(),
                    MCRActivitiMgr.WF_VAR_DISPLAY_TITLE, MCRTranslation.translate("Wf.common.newObject"));
        }

        //Description
        try {
            String xpDescr = MCRConfiguration.instance()
                    .getString("MCR.Activiti.MCRObject.Display.Description.XPath." + mcrObjID.getBase(), null);
            if (xpDescr == null) {
                xpDescr = MCRConfiguration.instance().getString(
                        "MCR.Activiti.MCRObject.Display.Description.XPath.default_" + mcrObjID.getTypeId(),
                        "/mycoreobject/@label");
            }
            XPathExpression<String> xpath = XPathFactory.instance().compile(xpDescr, Filters.fstring(), null,
                    MCRConstants.MODS_NAMESPACE);
            txt = xpath.evaluateFirst(mcrObj.createXML());
        } catch (Exception e) {
            LOGGER.error(e);
            txt = e.getMessage();
        }
        if (txt != null) {
            MCRActivitiMgr.getWorfklowProcessEngine().getTaskService().setVariable(t.getId(),
                    MCRActivitiMgr.WF_VAR_DISPLAY_DESCRIPTION, txt);
        } else {
            MCRActivitiMgr.getWorfklowProcessEngine().getTaskService().setVariable(t.getId(),
                    MCRActivitiMgr.WF_VAR_DISPLAY_DESCRIPTION, "");
        }

        //PersistentIdentifier
        try {
            String xpPI = MCRConfiguration.instance()
                    .getString("MCR.Activiti.MCRObject.Display.PersistentIdentifier.XPath." + mcrObjID.getBase(), null);
            if (xpPI == null) {
                xpPI = MCRConfiguration.instance().getString(
                        "MCR.Activiti.MCRObject.Display.PersistentIdentifier.XPath.default_" + mcrObjID.getTypeId(),
                        "/mycoreobject/@ID");
            }
            XPathExpression<String> xpath = XPathFactory.instance().compile(xpPI, Filters.fstring(), null,
                    MCRConstants.MODS_NAMESPACE);
            txt = xpath.evaluateFirst(mcrObj.createXML());
        } catch (Exception e) {
            LOGGER.error(e);
            txt = e.getMessage();
        }
        if (txt != null) {
            MCRActivitiMgr.getWorfklowProcessEngine().getTaskService().setVariable(t.getId(),
                    MCRActivitiMgr.WF_VAR_DISPLAY_PERSISTENT_IDENTIFIER, txt);
        } else {
            MCRActivitiMgr.getWorfklowProcessEngine().getTaskService().setVariable(t.getId(),
                    MCRActivitiMgr.WF_VAR_DISPLAY_PERSISTENT_IDENTIFIER, "");
        }

    }

    private void updateWFDerivateList(Task t) {
        MCRObjectID mcrObjID = MCRObjectID.getInstance(String.valueOf(MCRActivitiMgr.getWorfklowProcessEngine()
                .getTaskService().getVariable(t.getId(), MCRActivitiMgr.WF_VAR_MCR_OBJECT_ID)));
        if (mcrObjID == null) {
            LOGGER.error("WFObject could not be read.");
        }

        MCRObject mcrObj = MCRActivitiUtils.loadMCRObjectFromWorkflowDirectory(mcrObjID);
        StringWriter result = new StringWriter();
        if (mcrObj != null && mcrObj.getStructure().getDerivates().size() > 0) {
            Map<String, List<String>> derivateFiles = MCRActivitiUtils.getDerivateFiles(mcrObjID);
            for (MCRMetaLinkID derID : mcrObj.getStructure().getDerivates()) {
                result.append(
                        "<span class=\"badge pull-left\" style=\"margin-left:128px; margin-right:24px; margin-top:3px;\">"
                                + derID.getXLinkHref() + "</span>");
                MCRDerivate der = MCRActivitiUtils.loadMCRDerivateFromWorkflowDirectory(mcrObjID,
                        derID.getXLinkHrefID());
                result.append("<div style=\"margin-left:300px\">");
                result.append("    <strong>["
                        + MCRTranslation.translate("OMD.derivatedisplay." + mcrObjID.getBase() + "." + der.getLabel())
                        + "]</strong>");
                for (String s : der.getService().getFlags("title")) {
                    result.append("<br />" + s);
                }
                result.append("</div>");
                result.append("<div style=\"clear:both; padding-top:12px; margin-left:192px;\">");
                result.append("\n    <ul style=\"list-style-type: none;\">");
                for (String fileName : derivateFiles.get(derID.getXLinkHref())) {
                    result.append("\n        <li>");
                    if (fileName.contains(".")) {
                        result.append("<i class=\"fa fa-file\"></i> ");
                    } else {
                        result.append("<i class=\"fa fa-folder-open\"></i> ");
                    }
                    result.append(fileName);

                    if (fileName.equals(der.getDerivate().getInternals().getMainDoc())) {
                        result.append("<span style=\"margin-left:16px; color:grey;\" class=\"fa fa-star\" title=\""
                                + MCRTranslation.translate("Editor.Common.derivate.maindoc") + "\"></span>");
                    }
                    result.append("</li>");
                }
                result.append("\n    </ul>");
                result.append("</div>");
            }
        }
        MCRActivitiMgr.getWorfklowProcessEngine().getTaskService().setVariable(t.getId(),
                MCRActivitiMgr.WF_VAR_DISPLAY_DERIVATELIST, result.toString());

    }

    private void publishAllTasks() {
        MCRUser user = MCRUserManager.getCurrentUser();
        String objectType = mcr_base.substring(mcr_base.indexOf("_") + 1);
        TaskService ts = MCRActivitiMgr.getWorfklowProcessEngine().getTaskService();
        myTasks = ts.createTaskQuery().taskAssignee(user.getUserID())
                .processVariableValueEquals(MCRActivitiMgr.WF_VAR_OBJECT_TYPE, objectType).orderByTaskCreateTime()
                .desc().list();

        for (Task t : myTasks) {
            followTransaction(t.getId(), "edit_object.do_save");
        }
    }

    private void importMODSFromGVK(String mcrID) {
        MCRObjectID mcrObjID = MCRObjectID.getInstance(mcrID);
        MCRMODSGVKImporter.updateWorkflowFile(mcrObjID);
    }

    public String getMcr_base() {
        return mcr_base;
    }

    public void setMcr_base(String mcr_base) {
        this.mcr_base = mcr_base;
    }

    public String getObjectType() {
        return mcr_base.substring(mcr_base.indexOf("_") + 1);
    }

    public List<Task> getMyTasks() {
        return myTasks;
    }

    public List<Task> getAvailableTasks() {
        return availableTasks;
    }

    public static Logger getLOGGER() {
        return LOGGER;
    }

    public ForwardResolution getFwdResolution() {
        return fwdResolution;
    }

    public List<String> getMessages() {
        return messages;
    }

    public String getEditorPath() {
        return editorPath;
    }

    public String getSourceURI() {
        return sourceURI;
    }

    public String getCancelURL() {
        return cancelURL;
    }
}
