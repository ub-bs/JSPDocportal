package org.mycore.frontend.jsp.stripes.actions;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;

import org.jdom2.Document;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.mycore.activiti.MCRActivitiUtils;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.tools.dissonline.formimport.DissOnlineFormImport;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.controller.LifecycleStage;

@UrlBinding("/importFromForm.action")
public class ImportFromFormAction implements ActionBean {
    ForwardResolution fwdResolution = new ForwardResolution("/content/workflow/import-from-form.jsp");
    private ActionBeanContext context;

    private String returnPath = "";
    private String mcrid = "";
    private String folderName = "";
    private String[] listOfMetadataVersions = new String[]{};
    private String metadataContent = "";
    private String metadataVersion = "";
    
    private String processid="";

    public ActionBeanContext getContext() {
        return context;
    }

    public void setContext(ActionBeanContext context) {
        this.context = context;
    }

    public ImportFromFormAction() {

    }

    @Before(stages = LifecycleStage.BindingAndValidation)
    public void rehydrate() {
        if (getContext().getRequest().getParameter("mcrid") != null) {
            mcrid = getContext().getRequest().getParameter("mcrid");         
        }
        if (getContext().getRequest().getParameter("returnPath") != null) {
            returnPath = getContext().getRequest().getParameter("returnPath");
        }
        if (getContext().getRequest().getParameter("processid") != null) {
            processid = getContext().getRequest().getParameter("processid");
        }
        if (getContext().getRequest().getParameterValues("listOfMetadataVersions") !=null) {
            listOfMetadataVersions = getContext().getRequest().getParameterValues("listOfMetadataVersions");
        }
        if (getContext().getRequest().getParameter("folderName") != null) {
            folderName = getContext().getRequest().getParameter("folderName");
        }
        if (getContext().getRequest().getParameter("metadataContent") != null) {
            metadataContent = getContext().getRequest().getParameter("metadataContent");
        }
        if (getContext().getRequest().getParameter("metadataVersion") != null) {
            metadataVersion = getContext().getRequest().getParameter("metadataVersion");
        }

    }

    @DefaultHandler
    public Resolution defaultRes() {
        return fwdResolution;
    }

    public Resolution doRetrieveMetadataVersions() {
        if(folderName!=null){
            listOfMetadataVersions = DissOnlineFormImport.retrieveMetadataVersions(folderName);
        }        
        return fwdResolution;
    }
    
    public Resolution doRetrieveMetadataContent() {
        metadataContent=null;
        if(folderName!=null){
            Document  data = DissOnlineFormImport.retrieveMetadataContent(folderName, metadataVersion);
            if(data!=null){
            StringWriter sw = new StringWriter();
            XMLOutputter xmlout = new XMLOutputter(Format.getPrettyFormat());
            try{
                xmlout.output(data,  sw);
            }
            catch(IOException e){
                e.printStackTrace();
            }
            metadataContent = sw.toString();
            }
        }        
        return fwdResolution;
    }

    public Resolution doSave() {
        if (!mcrid.equals("")) {
			Path file = MCRActivitiUtils.getWorkflowObjectFile(MCRObjectID.getInstance(mcrid));
            DissOnlineFormImport.loadFormDataIntoMCRObject(metadataContent, file);
        }        
        
        return new ForwardResolution(returnPath);
    }

    public Resolution doCancel() {
        return new ForwardResolution(returnPath);
    }

    public String getMcrid() {
        return mcrid;
    }

    public void setMcrid(String mcrid) {
        this.mcrid = mcrid;
    }

    public String getMetadataContent() {
        return metadataContent;
    }

    public void setMetadataContent(String metadataContent) {
        this.metadataContent = metadataContent;
    }

    public String getReturnPath() {
        return returnPath;
    }

    public void setReturnPath(String returnPath) {
        this.returnPath = returnPath;
    }

  
    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String[] getListOfMetadataVersions() {
        return listOfMetadataVersions;
    }

    public String getMetadataVersion() {
        return metadataVersion;
    }

    public void setMetadataVersion(String metadataVersion) {
        this.metadataVersion = metadataVersion;
    }

    public String getProcessid() {
        return processid;
    }

    public void setProcessid(String processid) {
        this.processid = processid;
    }

    public void setListOfMetadataVersions(String[] listOfMetadataVersions) {
        this.listOfMetadataVersions = listOfMetadataVersions;
    }
}
