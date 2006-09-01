package org.mycore.frontend.jsp.taglibs;

import java.io.File;
import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpUtils;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRSessionMgr;


public class MCRIncludeWebContentTag extends SimpleTagSupport
{
	private String file;

	private static Logger logger = Logger.getLogger("MCRIncludeEditorTag.class");
	
	public void setFile(String file) {
		this.file = file;
	}

	public void doTag() throws JspException, IOException {
		String foldername = MCRConfiguration.instance().getString("MCR.WebContent.Folder");
		String lang = MCRSessionMgr.getCurrentSession().getCurrentLanguage();
		PageContext pageContext = (PageContext) getJspContext();
		JspWriter out = pageContext.getOut();
		out.flush();
		String[] s = file.split("\\.");
		String path = foldername+"/"+s[0]+"_"+lang+"."+s[1];
		File dir = new File( pageContext.getServletContext().getRealPath("content")); 
		File f= new File(dir, path);
		if(!f.exists()){
			path=foldername+"/"+file;
		}
		try{
			path="/content/"+path;
			RequestDispatcher dispatcher = pageContext.getServletContext().getRequestDispatcher(path); 
			dispatcher.include(pageContext.getRequest(), pageContext.getResponse()); 
			//pageContext.include("/"+path);
		}
		catch(Exception e1){
			out.write(path+"\n");
			out.write("Content not found");
		}
			
	}	
}