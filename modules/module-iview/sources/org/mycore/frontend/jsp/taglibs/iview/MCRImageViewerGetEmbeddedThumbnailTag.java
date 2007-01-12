package org.mycore.frontend.jsp.taglibs.iview;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.mycore.frontend.servlets.MCRServlet;

/**
 * returns a thumbnail as "<img ..>" HTML Element
 * 
 * Functionality should be equal to:
 * /docportal/modules/UNINSTALLED_module-iview/xsl/mcr-module-startIview.xsl
 * Template: iview.getEmbedded.thumbnail 
 * 
 * based on CVS-Version 1.7 of the xsl file
 * @author Robert Stephan
 */
 
public class MCRImageViewerGetEmbeddedThumbnailTag extends SimpleTagSupport {

	private String derivID; // Derivate-ID

	private String pathOfImage; // absoluter Pfad des Bildes oder Ordners

	/**
	 * Setzen der Derivate-ID
	 * 
	 * @param derivID
	 *            the derivID to set
	 */
	public void setDerivID(String derivID) {
		this.derivID = derivID;
	}

	public void setPathOfImage(String pathOfImage) {
		this.pathOfImage = pathOfImage;
	}
	
	public void doTag() throws JspException, IOException {
	//<img src="{concat($iview.home,$derivID,$pathOfImage,$HttpSession,'?mode=getImage&amp;XSL.MCR.Module-iview.navi.zoom=thumbnail')}" />
		PageContext pageContext = (PageContext) getJspContext();
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		String httpSession = MCRImageViewerTag.getHttpSession(request);
		
		//
		JspWriter out = getJspContext().getOut();
		out.write("<img src=\""+
				MCRServlet.getServletBaseURL()+"MCRIViewServlet/"+derivID+pathOfImage+httpSession+
				"?mode=getImage&amp;XSL.MCR.Module-iview.navi.zoom=thumbnail"+
				"\"/>");		
	}
	
//	public void doTag() throws JspException, IOException {
//		PageContext pageContext = (PageContext) getJspContext();
//		String resolver = "webapp:WEB-INF/stylesheets/mcr-module-startIview.xsl";
//		String xml = "<iview_getembedded_thumbnail " + "derivid=\"" + derivID
//				+ "\" " + "pathofimage=\"" + pathOfImage + "\" " + " />";
//		StringWriter strW = new StringWriter();
//		strW.write("");
//
//		try {
//			Source xsltSource = MCRURIResolver.instance().resolve(resolver,
//					"MCRImageViewerTag.java");
//			Source xmlSource = new StreamSource(new StringReader(xml));
//			// das Factory-Pattern unterstützt verschiedene XSLT-Prozessoren
//			TransformerFactory transFact = TransformerFactory.newInstance();
//			Transformer trans = transFact.newTransformer(xsltSource);
//
//			// parameter ...
//
//			HttpServletRequest request = (HttpServletRequest) pageContext
//					.getRequest();
//			Properties props = MCRImageViewerTag.buildXSLParameters(request);
//			Enumeration eKeys = props.keys();
//			while (eKeys.hasMoreElements()) {
//				String key = (String) eKeys.nextElement();
//				Object value = props.get(key);
//				trans.setParameter(key, value);
//			}
//
//			// parameter - end
//			trans.transform(xmlSource, new StreamResult(strW));
//		} catch (TransformerException tfe) {
//
//		}
//		String result = strW.toString();
//		result = result.substring(result.indexOf(">") + 1);
//
//		getJspContext().getOut().write(result);
//		return;
//
//	}

}