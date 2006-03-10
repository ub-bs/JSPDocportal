package org.mycore.frontend.jsp.taglibs;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;

import org.apache.log4j.Logger;
import org.mycore.frontend.jsp.NavServlet;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerFactory;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerInterface;


public class MCRIncludeEditorTag extends SimpleTagSupport
{
	private String isNewEditorSource;
	private String editorSource;
	private String mcrid;
	private String step;
	private String type;
	private String target;
	
	private String nextPath;
	
	private String editorSessionID;
	private String mcrid2;
	private String uploadID;
	private String editorPath;
	private String cancelPage;

	private static Logger logger = Logger.getLogger("MCRIncludeEditorTag.class");
	private static MCRWorkflowEngineManagerInterface WFI = MCRWorkflowEngineManagerFactory.getDefaultImpl();
	
	public void setIsNewEditorSource(String isNewEditorSource) {
		this.isNewEditorSource = isNewEditorSource;
	}
	
	public void setEditorSource(String editorSource){
		this.editorSource = editorSource;
	}
	
	public void setNextPath(String nextPath){
		this.nextPath = nextPath;
	}

	public void setMcrid(String mcrid) {
		this.mcrid = mcrid;
	}

	public void setEditorSessionID(String editorSessionID){
		this.editorSessionID = editorSessionID;
	}
	
	public void setMcrid2(String mcrid2) {
		this.mcrid2 = mcrid2;
	}

	public void setStep(String step) {
		this.step = step;
	}


	public void setType(String type) {
		this.type = type;
	}	
	
	public void setTarget(String target){
		this.target = target;
	}
	
	public void setUploadID(String uploadID){
		this.uploadID = uploadID;
	}
	
	public void setEditorPath(String editorPath){
		this.editorPath = editorPath;
	}
	
	public void setCancelPage(String cancelPage){
		this.cancelPage = cancelPage;
	}

	public void doTag() throws JspException, IOException {
		PageContext pageContext = (PageContext) getJspContext();
		Properties parameters = new Properties();
		String editorBase = "";
		if(editorSessionID != null && !editorSessionID.equals("")){
			parameters.put("XSL.editor.session.id",editorSessionID);
			editorBase = (String)pageContext.getSession().getAttribute("editorPath");
		}else{
			if(cancelPage == null || cancelPage.equals("")){
				cancelPage 		=  NavServlet.getBaseURL() + "nav?path=~workflow-" + type;
			}
			if(editorPath != null && !editorPath.equals("")) {
				editorBase = editorPath;
			}else if(uploadID == null || uploadID.equals("") ) {
				editorBase = new StringBuffer(NavServlet.getBaseURL())
				.append("editor/workflow/editor_form_").append(step).append('-').append(type)
				.append(".xml").toString();	
			}else{
				editorBase = new StringBuffer(NavServlet.getBaseURL())
				.append("editor/workflow/editor-author-addfile.xml").toString();
			}
			parameters = getParameters();
			pageContext.getSession().setAttribute("editorPath", editorBase);			
		}
		JspWriter out = pageContext.getOut();
	
		try{
			URL url = new URL(buildEncodedURL(editorBase, parameters));
			Reader is = new InputStreamReader( url.openStream(), "utf-8");
		    BufferedReader in = new BufferedReader( is );
		    for ( String s; ( s = in.readLine() ) != null; ){
		        out.print( s );
		    }
		    in.close();
	    }
		    catch ( MalformedURLException e ) {
		      logger.error( "MalformedURLException: " + e, e );
	    }
		    catch ( IOException e ) {
		      logger.error( "IOException: " + e , e);
	    }
		return;
	}
	
	private Properties getParameters(){
		Properties params = new Properties();
		if(cancelPage == null || cancelPage.equals("")){
			cancelPage 		=  NavServlet.getBaseURL() + "nav?path=~workflow-" + type;
		}		
		params.put("XSL.editor.cancel.url", cancelPage);
		params.put("XSL.editor.source.new", isNewEditorSource);
		params.put("mcrid", mcrid);
		params.put("type", type);
		params.put("step", step);
		params.put("target", target);
		if(mcrid2 != null && !mcrid2.equals("")) {
			params.put("mcrid2", mcrid2);
		}
		if(uploadID != null && !uploadID.equals("")){
			params.put("XSL.UploadID", uploadID);
		}
		if(editorSource != null && !editorSource.equals("")){
			params.put("XSL.editor.source.url", "file://" + editorSource );
		}else if(!isNewEditorSource.equals("true") && mcrid != null && !mcrid.equals("") && type != null && !type.equals("")){
			StringBuffer sb = new StringBuffer("file://").append(WFI.getWorkflowDirectory(type))
				.append(File.separator).append(mcrid).append(".xml");
			params.put("XSL.editor.source.url", sb.toString());
		}
		
		if(nextPath != null && !nextPath.equals("")){
			params.put("nextPath", nextPath);
		}

		return params;
	}
	
	/**
	 * Builds an url that can be used to redirect the client browser to another
	 * page, including http request parameters. The request parameters will be
	 * encoded as http get request.
	 * 
	 * @param baseURL
	 *            the base url of the target webpage
	 * @param parameters
	 *            the http request parameters
	 */
	private String buildEncodedURL(String baseURL, Properties parameters) {
		StringBuffer url = new StringBuffer(baseURL);
		boolean first = true;

		for (Enumeration e = parameters.keys(); e.hasMoreElements();) {
			if (first) {
				url.append("?");
				first = false;
			} else
				url.append("&");

			String name = (String) (e.nextElement());
			String value = null;
			try {
				value = URLEncoder
						.encode(parameters.getProperty(name), "UTF-8");
			} catch (UnsupportedEncodingException ex) {
				value = parameters.getProperty(name);
			}

			url.append(name).append("=").append(value);
		}

		logger.debug("including editor via the encoded url " + url.toString());
		return url.toString();
	}	
}