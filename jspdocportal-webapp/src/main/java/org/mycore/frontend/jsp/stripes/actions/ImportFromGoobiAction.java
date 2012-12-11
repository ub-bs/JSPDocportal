package org.mycore.frontend.jsp.stripes.actions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.controller.LifecycleStage;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.mycore.frontend.workflowengine.strategies.MCRWorkflowDirectoryManager;
import org.mycore.tools.goobiimport.GoobiSFTPDownloader;
import org.mycore.tools.gvkmods.GVKMODSImport;

@UrlBinding("/importFromGoobi.action")
public class ImportFromGoobiAction implements ActionBean {
    ForwardResolution fwdResolution = new ForwardResolution("/content/workflow/import-from-goobi.jsp");
    private ActionBeanContext context;

    private String returnPath = "";
    private String mcrID = "";
    private String goobiFolderID = "";
    private String output = "";

    public ActionBeanContext getContext() {
        return context;
    }

    public void setContext(ActionBeanContext context) {
        this.context = context;
    }

    public ImportFromGoobiAction() {

    }

    @Before(stages = LifecycleStage.BindingAndValidation)
    public void rehydrate() {
        if (getContext().getRequest().getParameter("mcrid") != null) {
            mcrID = getContext().getRequest().getParameter("mcrid");         
        }
        if (getContext().getRequest().getParameter("returnPath") != null) {
            returnPath = getContext().getRequest().getParameter("returnPath");
        }
    }

    @DefaultHandler
    public Resolution defaultRes() {
        return fwdResolution;
    }

    public Resolution doRetrieve() {
        if(goobiFolderID!=null){
            String gvkPPN = goobiFolderID.split("_")[1];
            Element eMODS = GVKMODSImport.retrieveMODS(gvkPPN);
            if(eMODS!=null){
                XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
                output = outputter.outputString(eMODS);
            }            
        }        
        return fwdResolution;
    }

    public Resolution doSave() {
        if (!mcrID.equals("")) {
            
            
            String[] mcridParts = mcrID.split("_");
            String savedir = MCRWorkflowDirectoryManager.getWorkflowDirectory(mcridParts[1]);
            String filename = savedir + "/" + mcrID + ".xml";
            
            GoobiSFTPDownloader.sftpDownload(goobiFolderID, new File(savedir));
            File file = new File(filename);
            try {
                SAXBuilder sb = new SAXBuilder(false);
                Document docJdom = sb.build(new InputStreamReader(new FileInputStream(file), "UTF-8"));
                Element eMeta = docJdom.getRootElement().getChild("metadata");
                if(eMeta!=null){
                    Element eDefMods = eMeta.getChild("def.modsContainer");
                    if(eDefMods == null){
                        eDefMods = new Element("def.modsContainer");
                        eMeta.addContent(0,  eDefMods);
                        eDefMods.setAttribute("class", "MCRMetaXML");
                    }
                    eDefMods.removeContent();
                    Element eMods = new Element("modsContainer");
                    eDefMods.addContent(eMods);
                    Element eModsData = sb.build(new StringReader(output)).getRootElement();
                    eMods.addContent(eModsData.detach());
                }
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
                XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
                xout.output(docJdom, bw);
            } catch (JDOMException jdome) {
            //do nothing
            } catch (IOException e) {
            //do nothing
            }
    }
        
        
        
        
        return new ForwardResolution(returnPath);
    }

    public Resolution doCancel() {
        return new ForwardResolution(returnPath);
    }

    public String getMcrID() {
        return mcrID;
    }

    public void setMcrID(String mcrid) {
        this.mcrID = mcrid;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getReturnPath() {
        return returnPath;
    }

    public void setReturnPath(String returnPath) {
        this.returnPath = returnPath;
    }

  
    public String getGoobiFolderID() {
        return goobiFolderID;
    }

    public void setGoobiFolderID(String goobiFolderID) {
        this.goobiFolderID = goobiFolderID;
    }
}
