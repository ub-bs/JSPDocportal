package org.mycore.frontend.jsp.stripes.actions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.controller.LifecycleStage;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import org.hibernate.Transaction;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.mycore.backend.hibernate.MCRHIBConnection;
import org.mycore.common.MCRUtils;
import org.mycore.datamodel.metadata.MCRDerivate;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowConstants;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManager;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowProcess;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowProcessManager;
import org.mycore.frontend.workflowengine.jbpm.publication.MCRDocumentDerivateStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRWorkflowDirectoryManager;
import org.mycore.tools.goobiimport.GoobiSFTPDownloader;
import org.mycore.tools.gvkmods.GVKMODSImport;
import org.xml.sax.SAXParseException;

@UrlBinding("/importFromGoobi.action")
public class ImportFromGoobiAction implements ActionBean {
    ForwardResolution fwdResolution = new ForwardResolution("/content/workflow/import-from-goobi.jsp");
    private ActionBeanContext context;

    private String returnPath = "";
    private String mcrID = "";
    private String goobiFolderID = "";
    private String output = "";
    private String processid="";

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
        if (getContext().getRequest().getParameter("processid") != null) {
            processid = getContext().getRequest().getParameter("processid");
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
            File dirSave = new File(MCRWorkflowDirectoryManager.getWorkflowDirectory(mcridParts[1]));
            
            long pid = Long.parseLong(processid);
            MCRWorkflowProcess wfp = MCRWorkflowProcessManager.getInstance().getWorkflowProcess(pid);
            String mcrid = wfp.getStringVariable(MCRWorkflowConstants.WFM_VAR_METADATA_OBJECT_IDS);
         
            MCRWorkflowManager WFM = wfp.getCurrentWorkflowManager();
           	String derivateID = WFM.addDerivate(wfp.getContextInstance(), mcrid);
            String userid = wfp.getStringVariable(MCRWorkflowConstants.WFM_VAR_INITIATOR);
            Transaction tx = MCRHIBConnection.instance().getSession().beginTransaction();
        	WFM.setDefaultPermissions(derivateID, userid, wfp.getContextInstance());
        	tx.commit();
        	
        	File dirDerivate = new File(dirSave, derivateID);
        	MCRDocumentDerivateStrategy derivateStrategy = new MCRDocumentDerivateStrategy();
        	      	
        	GoobiSFTPDownloader.sftpDownload(goobiFolderID, dirDerivate);
        	File fx = new File(dirDerivate, "meta.xml");
        	File fMets = new File(dirDerivate, "mets.xml");
        	fx.renameTo(fMets);
        	
        	ArrayList<FileItem> files = new ArrayList<FileItem>();
        	derivateStrategy.saveFiles(files, dirDerivate.getPath(), wfp.getContextInstance(), "METS", null);
        	
        	
        	File fDerivate = new File(dirDerivate.getAbsolutePath()+".xml");
        	try {
				MCRDerivate der = new MCRDerivate(fDerivate.toURI());
				der.getDerivate().getInternals().setMainDoc("mets.xml");
				
				byte[] outxml = MCRUtils.getByteArray(der.createXML());
				FileOutputStream out = new FileOutputStream(fDerivate);
				out.write(outxml);				
				out.close();
				
			} catch (SAXParseException e1) {
				Logger.getLogger(ImportFromGoobiAction.class).error("Exception reading derivate", e1);
				
			} catch (IOException e1) {
				Logger.getLogger(ImportFromGoobiAction.class).error("Exception reading derivate", e1);
			}
        	
        	
        	
        	
            //Store Metadata
          
            try {
                SAXBuilder sb = new SAXBuilder(false);
                Document docJdom = sb.build(new InputStreamReader(new FileInputStream(fMets), "UTF-8"));
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
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fDerivate), "UTF-8"));
                XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
                xout.output(docJdom, bw);
            } 
            catch (JDOMException jdome) {
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
