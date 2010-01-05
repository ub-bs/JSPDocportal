package org.mycore.frontend.jsp.taglibs;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.mycore.common.xml.MCRXMLHelper;
import org.mycore.frontend.editor.MCREditorServlet;
import org.mycore.frontend.jsp.NavServlet;
import org.xml.sax.SAXParseException;


public class MCRIncludeEditorTag extends SimpleTagSupport
{
	@SuppressWarnings("unused")
	private String editorSessionID;
	
	private String editorPath;
	private String cancelPage;

	private static Logger logger = Logger.getLogger("MCRIncludeEditorTag.class");
	

/*	public void setEditorSessionID(String editorSessionID){
		this.editorSessionID = editorSessionID;
	}*/
	
	
	public void setEditorPath(String editorPath){
		this.editorPath = editorPath;
	}
	
/*	public void setCancelPage(String cancelPage){
		this.cancelPage = cancelPage;
	}*/
	

	public void doTag() throws JspException, IOException {
		PageContext pageContext = (PageContext) getJspContext();
		String editorBase = "";
	/*
		if(editorSessionID != null && !editorSessionID.equals("")){			
			parameters.put("XSL.editor.session.id",editorSessionID);				
			editorBase = (String)pageContext.getSession().getAttribute("editorPath");	
			if (editorBase == null ) {
				editorBase = new StringBuffer(NavServlet.getBaseURL())
				.append((String) pageContext.getAttribute("editorPath")).toString();
			}*/
		if(false){
		}else{
			if(cancelPage == null || cancelPage.equals("")){
			//	cancelPage 		=  NavServlet.getBaseURL() + "nav?path=~"+workflowType;
			}
			if(editorPath != null && !editorPath.equals("")) {
				if(editorPath.startsWith("http")){
					editorBase = editorPath;
				}
				else{
					editorBase = NavServlet.getBaseURL() + editorPath;
				}
			//parameters = getParameters();
			pageContext.getSession().setAttribute("editorPath", editorBase);			
		}
		JspWriter out = pageContext.getOut();
	
		try{
			String path = pageContext.getServletContext().getRealPath(editorPath);
			File editorFile = new File(path);
			
			Document xml = MCRXMLHelper.parseURI(editorFile.toURI(), false);
			MCREditorServlet.replaceEditorElements((HttpServletRequest)pageContext.getRequest(), editorFile.toURI().toString(), xml);	
			MCREditorServlet.getLayoutService().doLayout((HttpServletRequest)pageContext.getRequest(),(HttpServletResponse)pageContext.getResponse(), out, xml);
		
		}
			
		    catch ( MalformedURLException e ) {
		      logger.error( "MalformedURLException: " + e, e );
	    }
		    catch ( IOException e ) {
		      logger.error( "IOException: " + e , e);
	    }
		    catch ( SAXParseException e){
		    	logger.error( "SaxParseException: " + e , e);
		    }
		}
	}
	
	/*private Properties getParameters(){
		Properties params = new Properties();
		if(cancelPage == null || cancelPage.equals("")){
			//cancelPage 		=  NavServlet.getBaseURL() + "nav?path=~workflow-" + type;
			cancelPage = NavServlet.getNavigationBaseURL()+"~"+workflowType;
		}		
		PageContext pageContext = (PageContext) getJspContext();
		HttpSession session = pageContext.getSession();		
	    String jSessionID = MCRConfiguration.instance().getString("MCR.session.param", ";jsessionid=");		
	    String sessionID = jSessionID + session.getId();

		params.put("lang" , MCRSessionMgr.getCurrentSession().getCurrentLanguage());
		params.put("MCRSessionID" , MCRSessionMgr.getCurrentSession().getID());
		params.put("cancelUrl", cancelPage);
		params.put("XSL.editor.source.new", isNewEditorSource);
		params.put("mcrid", mcrid);
		params.put("processid", String.valueOf(processid));
		params.put("type", type);
		params.put("step", step);
		params.put("target", target);
		params.put("workflowType", workflowType);
		params.put("HttpSessionID",sessionID);	
		params.put("JSessionID",sessionID);	
		
		
		if(mcrid2 != null && !mcrid2.equals("")) {
			params.put("mcrid2", mcrid2);
		}
		if(uploadID != null && !uploadID.equals("")){
			params.put("XSL.UploadID", uploadID);
		}
		String url="";
		if(editorSource != null && !editorSource.equals("")){
			try{
				url = new File(editorSource).toURI().toURL().toString();
			}
			catch(MalformedURLException mue){
				logger.error("Wrong URL", mue);
			}
			
		}else if(!isNewEditorSource.equals("true") && mcrid != null && !mcrid.equals("") && type != null && !type.equals("")){
			try{
				url = new File(MCRWorkflowDirectoryManager.getWorkflowDirectory(type)+"/"+mcrid+".xml").toURI().toURL().toString();
			}
			catch(MalformedURLException mue){
				logger.error("Wrong URL", mue);
			}			
		}
		params.put("sourceUri", url);
		if(nextPath != null && !nextPath.equals("")){
			params.put("nextPath", nextPath);
		}

		return params; */
	
	
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
/*
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
	} */	
}