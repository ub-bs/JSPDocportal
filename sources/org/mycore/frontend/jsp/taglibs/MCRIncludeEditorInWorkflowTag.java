package org.mycore.frontend.jsp.taglibs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRSessionMgr;
import org.mycore.common.xml.MCRXMLHelper;
import org.mycore.frontend.editor.MCREditorServlet;
import org.mycore.frontend.jsp.NavServlet;
import org.mycore.frontend.workflowengine.strategies.MCRWorkflowDirectoryManager;
import org.xml.sax.SAXParseException;


public class MCRIncludeEditorInWorkflowTag extends SimpleTagSupport
{
	private String isNewEditorSource;
	private String editorSource;
	private String mcrid;
	private String step;
	private String type;
	private String workflowType;
	private String publicationType;
	private String target;
	private long processid;
	
	private String nextPath;
	
	private String editorSessionID;
	private String mcrid2;
	private String uploadID;
	private String editorPath;
	private String cancelPage;

	private static Logger logger = Logger.getLogger("MCRIncludeEditorTag.class");
	
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
	
	public void setWorkflowType(String workflowType){
		this.workflowType = workflowType;
	}
	
	public void setPublicationType(String publicationType) {
		this.publicationType = publicationType;
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
	
	public void setProcessid(long processid){
		this.processid = processid;
	}

	@SuppressWarnings("unchecked")
	public void doTag() throws JspException, IOException {
		PageContext pageContext = (PageContext) getJspContext();
		
		Properties parameters = new Properties();
		String editorBase = "";
		if(editorSessionID != null && !editorSessionID.equals("")){			
			parameters.put("XSL.editor.session.id",editorSessionID);				
			editorBase = (String)pageContext.getSession().getAttribute("editorPath");	
			if (editorBase == null ) {
				editorBase = new StringBuffer(NavServlet.getBaseURL())
				.append((String) pageContext.getAttribute("editorPath")).toString();
			}
		}else{
			if(cancelPage == null || cancelPage.equals("")){
				cancelPage 		=  NavServlet.getBaseURL() + "nav?path=~"+workflowType;
			}
			if(editorPath != null && !editorPath.equals("")) {
				if(editorPath.startsWith("http")){
					editorBase = editorPath;
				}
				else{
					editorBase = NavServlet.getBaseURL() + editorPath;
				}
			}else if(uploadID == null || uploadID.equals("") ) {
				StringBuffer base = new StringBuffer(NavServlet.getBaseURL()).
						append("editor/workflow/editor_form_").append(step).append('-').append(type);			
				if (publicationType != null && !publicationType.equals("")) {
					if ( publicationType.endsWith("TYPE0002") )
						base.append("-").append("TYPE0002");
					if ( publicationType.endsWith("TYPE0001") ) 
						base.append("-").append("TYPE0001");
				}
				base.append(".xml");	
				editorBase = base.toString();

			}else{
				editorBase = new StringBuffer(NavServlet.getBaseURL())
				.append("editor/workflow/fileupload_new.xml").toString();
			}
			parameters = getParameters();
			pageContext.getSession().setAttribute("editorPath", editorBase);			
		}
		JspWriter out = pageContext.getOut();
	
		try{
			/*URL url = new URL(buildEncodedURL(editorBase, parameters));
			Reader is = new InputStreamReader( url.openStream(), "utf-8");
		    BufferedReader in = new BufferedReader( is );
		    for ( String s; ( s = in.readLine() ) != null; ){
		        out.print( s );
		    }
		    in.close();*/
		
		
			String path = pageContext.getServletContext().getRealPath(editorBase.substring(NavServlet.getBaseURL().length()));
			File editorFile = new File(path);
			
			HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
			Map params = request.getParameterMap();
			if(params.getClass().getName().equals("org.apache.catalina.util.ParameterMap")){
				 try{
					 Class[] argTypes = new Class[] { boolean.class };
					 Method main = params.getClass().getDeclaredMethod("setLocked", argTypes);
					 main.invoke(params, false);
				 }
				 catch(Exception e){
					 logger.error( "Error when unlocking parameter map: " , e );
				 }
			}
			params.clear();
			for(Object key: parameters.keySet()){
				request.getParameterMap().put(key, new String[]{parameters.getProperty((String)key)});
			}
			
			
			Document xml = MCRXMLHelper.parseURI(editorFile.toURI(), false);
			MCREditorServlet.replaceEditorElements(request, editorFile.toURI().toString(), xml);	
			MCREditorServlet.getLayoutService().doLayout(request,(HttpServletResponse)pageContext.getResponse(), out, xml);
		
		}
			
		    catch ( MalformedURLException e ) {
		      logger.error( "MalformedURLException: " + e, e );
	    }
		    catch ( IOException e ) {
		      logger.error( "IOException: " + e , e);
	    }
	}
	
	private Properties getParameters(){
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