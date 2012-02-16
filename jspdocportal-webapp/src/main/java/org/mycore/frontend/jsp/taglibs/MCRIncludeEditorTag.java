package org.mycore.frontend.jsp.taglibs;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
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
import org.mycore.common.xml.MCRLayoutService;
import org.mycore.common.xml.MCRURIResolver;
import org.mycore.frontend.jsp.NavServlet;


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
			File editorFile = new File(pageContext.getServletContext().getRealPath(editorPath));
					
			Source xmlSource = new StreamSource(editorFile);
	        Source xsltSource = new StreamSource(getClass().getResourceAsStream("/xsl/editor_standalone.xsl"));

	        // das Factory-Pattern unterstützt verschiedene XSLT-Prozessoren
	        TransformerFactory transFact = TransformerFactory.newInstance();
	        transFact.setURIResolver(MCRURIResolver.instance());
	        Transformer transformer = transFact.newTransformer(xsltSource);
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
	        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
	        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	        

	        /*
	        <!-- editor-common.xsl
	          ============ Parameter aus MyCoRe LayoutServlet ============ -->
	        <xsl:param name="WebApplicationBaseURL"     />
	        <xsl:param name="ServletsBaseURL"           />
	        <xsl:param name="DefaultLang"               />
	        <xsl:param name="CurrentLang"               />
	        <xsl:param name="MCRSessionID"              />
	        <xsl:param name="HttpSession"               />
	        <xsl:param name="JSessionID"                />
	         */
	        transformer.clearParameters();
	        Properties props = MCRLayoutService.buildXSLParameters((HttpServletRequest)pageContext.getRequest());
	        MCRLayoutService.setXSLParameters(transformer, props);	
	       
	        transformer.transform(xmlSource, new StreamResult(out));
	  	}
		catch(TransformerConfigurationException e){
		      logger.error( "TransformerConfigurationException: " + e , e);
		}
		catch(TransformerException e){
			  logger.error( "TransformerException " + e , e);
		}
	}
}