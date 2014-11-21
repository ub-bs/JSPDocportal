package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.mycore.common.MCRSessionMgr;
import org.mycore.common.content.MCRContent;
import org.mycore.common.content.MCRJDOMContent;
import org.mycore.common.content.MCRStringContent;
import org.mycore.common.content.MCRURLContent;
import org.mycore.frontend.MCRFrontendUtil;
import org.mycore.frontend.xeditor.MCRStaticXEditorFileServlet;
import org.xml.sax.SAXException;

/**
 * This tag includes an xeditor definition, which can be provided
 * as attribute editorPath or in the body of the tag.
 * 
 * 
 * @author Robert Stephan
 *
 */
public class MCRIncludeXEditorTag extends SimpleTagSupport {
	private static Logger LOGGER = Logger.getLogger(MCRIncludeXEditorTag.class);
	public static Namespace NS_XED = Namespace.getNamespace("xed", "http://www.mycore.de/xeditor");

	private String editorPath = null;
	private String cancelURL = null;
	private String sourceURI = null;

	/**
	 * Path to external editor definition
	 * 
	 * @param editorPath
	 */
	public void setEditorPath(String editorPath) {
		this.editorPath = editorPath;
	}
	
	/**
	 * Path to cancel URL
	 * 
	 * @param editorPath
	 */
	public void setCancelURL(String cancelURL) {
		this.cancelURL = cancelURL;
	}

	public void doTag() throws JspException, IOException {
		PageContext pageContext = (PageContext) getJspContext();

		boolean doCommitTransaction = false;
		if(!MCRSessionMgr.getCurrentSession().isTransactionActive()){
			doCommitTransaction = true;
			MCRSessionMgr.getCurrentSession().beginTransaction();
		}
		MCRContent editorContent = null;
		if (editorPath != null && !editorPath.equals("")) {
			if (editorPath.startsWith("http")) {
				editorContent = new MCRURLContent(new URL(editorPath));
			} else {
				editorContent = new MCRURLContent(new URL(MCRFrontendUtil.getBaseURL() + editorPath));
			}
		} else {
			if(getJspBody()!=null){
				StringWriter sw = new StringWriter();
				getJspBody().invoke(sw);
				editorContent = new MCRStringContent(sw.toString());
			}
		}
		if (editorContent != null) {
			try {
				JspWriter out = pageContext.getOut();
				Document doc = editorContent.asXML();
				if (doc.getRootElement().getName().equals("form") && doc.getRootElement().getNamespace().equals(NS_XED)) {
					if (cancelURL != null && cancelURL.length() > 0) {
						// setze xed:cancel
						Element elCancel = new Element("cancel", NS_XED).setAttribute("url", cancelURL);
						doc.getRootElement().addContent(0, elCancel);
					}
					if (sourceURI != null && sourceURI.length() > 0) {
						// setze xed:cancel
						Element elSource = new Element("source", NS_XED).setAttribute("uri", sourceURI);
						doc.getRootElement().addContent(0, elSource);
					}
					
					editorContent = new MCRJDOMContent(doc);
					editorContent.setDocType("MyCoReWebPage");
					
					HttpServletRequest request = (HttpServletRequest) pageContext.getRequest(); 
					String pageURL = request.getRequestURL().toString();
					String referer = request.getHeader("Referer");
		            if(referer!=null){
		            	pageURL = referer;
		            }
		            editorContent = MCRStaticXEditorFileServlet.doExpandEditorElements(editorContent, request, (HttpServletResponse) pageContext.getResponse(), pageURL);

					out.append(editorContent.asString().replaceAll("<\\?xml.*?\\?>", ""));
				} else {
					LOGGER.error("JSPTag <mcr:includeXEditor> can only contain an <xed:form> element");
					out.append("<span class=\"error\">Please provide an &lt;xed:form&gt; element here!</span>");

				}
			} catch (SAXException e) {
				LOGGER.error("SAXException " + e, e);
			} catch (JDOMException e) {
				LOGGER.error("JDOMException " + e, e);
			}
		}
		if(doCommitTransaction){
			MCRSessionMgr.getCurrentSession().commitTransaction();
		}
		
	}

	public void setSourceURI(String sourceURI) {
		this.sourceURI = sourceURI;
	}
}