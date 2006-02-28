package org.mycore.frontend.jsp.taglibs;

import java.io.BufferedReader;
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


public class MCRIncludeEditorTag extends SimpleTagSupport
{
	private String isNewEditorSource;
	private String mcrid;
	private String step;
	private String type;
	private String target;
	
	private String mcrid2;
	private String uploadID;
	private String editorPath;
	private String cancelPage;

	private static Logger logger = Logger.getLogger("MCRIncludeEditorTag.class");

	public void setIsNewEditorSource(String isNewEditorSource) {
		this.isNewEditorSource = isNewEditorSource;
	}

	public void setMcrid(String mcrid) {
		this.mcrid = mcrid;
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
		if(cancelPage == null || cancelPage.equals("")){
			cancelPage 		=  NavServlet.getBaseURL() + "nav?path=~workflow-" + type;
		}
		PageContext pageContext = (PageContext) getJspContext();
		JspWriter out = pageContext.getOut();
		
		String editorBase = "";
		if(editorPath != null && !editorPath.equals("")) {
			editorBase = editorPath;
		}else {
			editorBase = new StringBuffer(NavServlet.getBaseURL())
			.append("editor/workflow/editor_form_").append(step).append('-').append(type)
			.append(".xml").toString();	
		}
	
		try{
			URL url = new URL(buildEncodedURL(editorBase, getParameters()));
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