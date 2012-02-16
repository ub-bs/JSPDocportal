package org.mycore.frontend.jsp.taglibs;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.transform.JDOMSource;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRSessionMgr;
import org.mycore.common.xml.MCRLayoutService;
import org.mycore.common.xml.MCRURIResolver;
import org.mycore.common.xml.MCRXMLParserFactory;
import org.mycore.datamodel.ifs2.MCRContent;
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
		parameters = getParameters();
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
						append("editor/workflow/editor-").append(step).append('-').append(type);			
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
			request.getParameterMap().clear();
			for(Object key: parameters.keySet()){
				request.getParameterMap().put(key, new String[]{parameters.getProperty((String)key)});
			}
			
			Source xmlSource = new StreamSource(editorFile);


			try{
				Document xml = MCRXMLParserFactory.getParser(false).parseXML(MCRContent.readFrom(editorFile.toURI()));
				MCREditorServlet.replaceEditorElements(request, editorFile.toURI().toString(), xml);
				xmlSource = new JDOMSource(xml);
			
			}
			catch(SAXParseException e){
				//do nothing
			}
			
	        Source xsltSource = new StreamSource(getClass().getResourceAsStream("/xsl/editor_standalone.xsl"));

	        // das Factory-Pattern unterstützt verschiedene XSLT-Prozessoren
	        TransformerFactory transFact = TransformerFactory.newInstance();
	        transFact.setURIResolver(MCRURIResolver.instance());
	        Transformer transformer = transFact.newTransformer(xsltSource);
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
	        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
	        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		
	        transformer.clearParameters();
	        Properties props = MCRLayoutService.buildXSLParameters((HttpServletRequest)pageContext.getRequest());
	        props.putAll(parameters);
	        MCRLayoutService.setXSLParameters(transformer, props);	
	       
	        transformer.transform(xmlSource, new StreamResult(out));
		}
		
		catch ( TransformerConfigurationException e ) {
		      logger.error( "TransformerConfigurationExceptio: " + e , e);
		}
		catch ( TransformerException e ) {
		      logger.error( "TransformerException: " + e , e);
		}
	}
	
	private Properties getParameters(){
		Properties params = new Properties();
		PageContext pageContext = (PageContext) getJspContext();
		if(cancelPage == null || cancelPage.equals("")){
			cancelPage 	=  NavServlet.getBaseURL() + "nav?path=~workflow-" + workflowType;			
		}		
		
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
		//params.put("XSL.editor.source.url", url);
		params.put("sourceUri", url);
		
		if(nextPath != null && !nextPath.equals("")){
			params.put("nextPath", nextPath);
		}

		return params;
	}
}