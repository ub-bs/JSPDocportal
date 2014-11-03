package org.mycore.frontend.jsp.stripes.actions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Set;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.controller.LifecycleStage;

import org.apache.log4j.Logger;
import org.mycore.access.MCRAccessManager;
import org.mycore.common.MCRSessionMgr;
import org.mycore.common.config.MCRConfiguration;

@UrlBinding("/saveWebcontent.action")
public class SaveWebcontentAction extends MCRAbstractStripesAction implements ActionBean {
	private static Logger LOGGER = Logger.getLogger(SaveWebcontentAction.class);
	private String file ="";
	private String content = "";
	
      
	public SaveWebcontentAction() {

	}

	@Before(stages = LifecycleStage.BindingAndValidation)
	public void rehydrate() {
		super.rehydrate();
		// if (getContext().getRequest().getParameter("q") != null) {
		//	q = getContext().getRequest().getParameter("q");
		// }
	}

	@DefaultHandler
	public Resolution defaultRes() {
		if( MCRAccessManager.checkPermission("administrate-webcontent")){
			String id = null;
			for (String s : getContext().getRequest().getParameterMap().keySet()) {
				if (s.startsWith("doSave_")) {
					id = s.substring(s.indexOf("_") + 1);
					doSave(id);
					break;
				}
				if (s.startsWith("doOpen_")) {
					id = s.substring(s.indexOf("_") + 1);
					getOpenEditorsFromSession().add(id);
					break;
				}
				if (s.startsWith("doCancel_")) {
					id = s.substring(s.indexOf("_") + 1);
					getOpenEditorsFromSession().remove(id);
					break;
				}
			}
		}
		return new RedirectResolution(getContext().getRequest().getHeader("Referer"), false);
	}
	
	private void doSave(String id){
		getOpenEditorsFromSession().remove(id);
		file= getContext().getRequest().getParameter("file_"+id);
		content = getContext().getRequest().getParameter("content_"+id);
		File saveDir = new File(MCRConfiguration.instance().getString("MCR.WebContent.SaveFolder"));
		saveDir = new File(saveDir, MCRSessionMgr.getCurrentSession().getCurrentLanguage());
		File saveFile = new File(saveDir, file);
		saveFile.getParentFile().mkdirs();
		try{
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(saveFile), "UTF-8"));
			bw.append(content);
			bw.close();
		}
		catch(IOException e){
			LOGGER.error(e);
		}
	}
	
	private Set<String> getOpenEditorsFromSession(){
		@SuppressWarnings("unchecked")
		Set<String> openEditors = (Set<String>)getContext().getRequest().getSession().getAttribute("open_webcontent_editors");
		if(openEditors == null){
			openEditors = new HashSet<String>();
			getContext().getRequest().getSession().setAttribute("open_webcontent_editors", openEditors);
		}
		return openEditors;
	}
	

	public String getFile() {
		return file;
	}
	
	public String getContent() {
		return content;
	}
}
