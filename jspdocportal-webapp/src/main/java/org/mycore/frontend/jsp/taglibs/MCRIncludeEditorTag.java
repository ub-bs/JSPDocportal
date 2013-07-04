package org.mycore.frontend.jsp.taglibs;

import java.io.File;
import java.io.IOException;

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
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.transform.JDOMSource;
import org.mycore.common.content.MCRFileContent;
import org.mycore.common.xml.MCRURIResolver;
import org.mycore.common.xsl.MCRParameterCollector;
import org.mycore.frontend.editor.MCREditorServlet;
import org.mycore.frontend.jsp.NavServlet;
import org.xml.sax.SAXException;

public class MCRIncludeEditorTag extends SimpleTagSupport {
	private static Logger logger = Logger.getLogger("MCRIncludeEditorTag.class");
	
	private String editorPath;
	private String cancelPage;

	public void setEditorPath(String editorPath) {
		this.editorPath = editorPath;
	}

	public void setCancelPage(String cancelPage) {
		this.cancelPage = cancelPage;
	}

	public void doTag() throws JspException, IOException {
		PageContext pageContext = (PageContext) getJspContext();
		String editorBase = "";

		if (editorPath != null && !editorPath.equals("")) {
			if (editorPath.startsWith("http")) {
				editorBase = editorPath;
			} else {
				editorBase = NavServlet.getBaseURL() + editorPath;
			}
			pageContext.getSession().setAttribute("editorPath", editorBase);
		}
		JspWriter out = pageContext.getOut();

		try {
			HttpServletRequest request = (HttpServletRequest) pageContext
					.getRequest();
			File editorFile = new File(pageContext.getServletContext()
					.getRealPath(editorPath));
			
			Document xml = new MCRFileContent(editorFile).asXML();

			MCREditorServlet.replaceEditorElements(request, editorFile.toURI()
					.toString(), xml);

			Source xmlSource = new JDOMSource(xml);
			Source xsltSource = new StreamSource(getClass()
					.getResourceAsStream("/xsl/editor.xsl"));

			// das Factory-Pattern unterstützt verschiedene XSLT-Prozessoren
			TransformerFactory transFact = TransformerFactory.newInstance();
			transFact.setURIResolver(MCRURIResolver.instance());
			Transformer transformer = transFact.newTransformer(xsltSource);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
					"yes");

			/*
			 * <!-- editor-common.xsl ============ Parameter aus MyCoRe
			 * LayoutServlet ============ --> <xsl:param
			 * name="WebApplicationBaseURL" /> <xsl:param name="ServletsBaseURL"
			 * /> <xsl:param name="DefaultLang" /> <xsl:param name="CurrentLang"
			 * /> <xsl:param name="MCRSessionID" /> <xsl:param
			 * name="HttpSession" /> <xsl:param name="JSessionID" />
			 */
			transformer.clearParameters();
	        MCRParameterCollector paramColl = new MCRParameterCollector((HttpServletRequest)pageContext.getRequest());
		    paramColl.setParametersTo(transformer);
		    if (cancelPage != null && cancelPage.length() > 0) {
				paramColl.setParameter("cancelUrl", cancelPage);
			}
		    paramColl.setParametersTo(transformer);
	        transformer.transform(xmlSource, new StreamResult(out));
		} catch (TransformerConfigurationException e) {
			logger.error("TransformerConfigurationException: " + e, e);
		} catch (TransformerException e) {
			logger.error("TransformerException " + e, e);
		} catch (SAXException e) {
			logger.error("SAXException " + e, e);
		} catch (JDOMException e) {
			logger.error("JDOMException " + e, e);
		}
	}
}