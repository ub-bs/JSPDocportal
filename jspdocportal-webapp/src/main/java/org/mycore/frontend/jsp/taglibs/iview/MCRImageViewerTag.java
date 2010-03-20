package org.mycore.frontend.jsp.taglibs.iview;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.mycore.common.MCRConfiguration;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.services.i18n.MCRTranslation;

/**
 * Include the ImageViewer into a website
 * Functionality should be equal to:
 * /docportal/modules/UNINSTALLED_module-iview/xsl/mcr-module-startIview.xsl
 * Template: iview.getEmbedded.iframe 
 * 
 * based on CVS-Version 1.7 of the xsl file
 * 
 * @author Robert Stephan
 */
public class MCRImageViewerTag extends SimpleTagSupport {

	private String derivID; // Derivate-ID

	private String pathOfImage; // absoluter Pfad des Bildes oder Ordners

	private String height; // Höhe des eingebetteten Fensters

	private String width; // Breite des eingebetteten Fensters

	private String scaleFactor; // Zoom:

	// 0.1 .. 1.0, "fitToWidth", "fitToScreen"
	private String display; // Ansicht des Viewers

	// "minimal" (nur Navibar), "normal" (obere Menüleiste), "extended" (obere
	// und erweiterte Menüleiste)

	private String style; // Modus, in dem das Bild angezeigt wird

	// "thumbnail", "image", "text" (technische Metadaten)

	/**
	 * Setzen der Derivate-ID
	 * 
	 * @param derivID
	 *            the derivID to set
	 */
	public void setDerivID(String derivID) {
		this.derivID = derivID;
	}

	/**
	 * Setzen der Ansicht des Viewers erlaubte Werte sind: "minimal" (nur
	 * Navibar) "normal" (obere Menüleiste) "extended" (obere und erweiterte
	 * Menüleiste)
	 * 
	 * @param display
	 *            the display to set
	 */
	public void setDisplay(String display) {
		this.display = display;
	}

	/**
	 * Setzen der Höhe des eingebetteten Fensters
	 * 
	 * @param height
	 *            the height to set
	 */
	public void setHeight(String height) {
		this.height = height;
	}

	/**
	 * Setzen des Pfades zur Bilddatei oder -verzeichnis
	 * 
	 * @param pathOfImage
	 *            the absolute path to the image or folder
	 */
	public void setPathOfImage(String pathOfImage) {
		this.pathOfImage = pathOfImage;
	}

	/**
	 * Setzen des Skalierunsgfaktors indem das Bild angezeigt wird erlaubte
	 * Werte sind: "0.1" ... "1.0" "fitToWidth" "fitToScreen"
	 * 
	 * @param scaleFactor
	 *            the scaleFactor to set
	 */
	public void setScaleFactor(String scaleFactor) {
		this.scaleFactor = scaleFactor;
	}

	/**
	 * Setzen des Modus, indem das Bild angezeigt werden soll Erlaubte Werte
	 * sind: "thumbnail" "image" "text" (technische Metadaten)
	 * 
	 * @param style
	 *            the style to set
	 */
	public void setStyle(String style) {
		this.style = style;
	}

	/**
	 * Setzen der Höhe des eingebetteten Fensters
	 * 
	 * @param width
	 *            the width to set
	 */
	public void setWidth(String width) {
		this.width = width;
	}

	public void doTag() throws JspException, IOException {
		PageContext pageContext = (PageContext) getJspContext();
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		String httpSession = getHttpSession(request);
		String lastEmbeddedURL=getCompleteURL(request);
		//
		JspWriter out = getJspContext().getOut();
		out.write(
				"<iframe marginheight=\"0\""+
				"marginwidth=\"0\" frameborder=\"0\" src=\"" +
//				//{concat($iview.home,$derivID,$pathOfImage,$HttpSession,'?mode=generateLayout&amp;XSL.MCR.Module-iview.navi.zoom.SESSION=',$scaleFactor,'&amp;XSL.MCR.Module-iview.display.SESSION=',$display,'&amp;XSL.MCR.Module-iview.style.SESSION=',$style,'&amp;XSL.MCR.Module-iview.lastEmbeddedURL.SESSION=',$lastEmbeddedURL,'&amp;XSL.MCR.Module-iview.embedded.SESSION=true&amp;XSL.MCR.Module-iview.move=reset')}"
				MCRServlet.getServletBaseURL()+"MCRIViewServlet/"+derivID+pathOfImage+httpSession+
				"?mode=generateLayout&amp;XSL.MCR.Module-iview.navi.zoom.SESSION="+scaleFactor+
				"&amp;XSL.MCR.Module-iview.display.SESSION="+display+
				"&amp;XSL.MCR.Module-iview.style.SESSION="+style+
				"&amp;XSL.MCR.Module-iview.lastEmbeddedURL.SESSION="+lastEmbeddedURL+
				"&amp;XSL.MCR.Module-iview.embedded.SESSION=true"+
				"&amp;XSL.MCR.Module-iview.move=reset\""+
				
				 
				"name=\"iview\" width=\""+width+"\" height=\""+height+"\" align=\"left\">"+
			"<p>"+MCRTranslation.translate("iview.error")+"</p>"+
		"</iframe>");		
	}
//	public void doTag() throws JspException, IOException {
//		PageContext pageContext = (PageContext) getJspContext();
//		String resolver = "webapp:WEB-INF/stylesheets/mcr-module-startIview.xsl";
//		String xml = "<iview " + "derivid=\"" + derivID + "\" "
//				+ "pathofimage=\"" + pathOfImage + "\" " + "height=\"" + height
//				+ "\" " + "width=\"" + width + "\" " + "scalefactor=\""
//				+ scaleFactor + "\" " + "display=\"" + display + "\" "
//				+ "style=\"" + style + "\" " + " />";
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
//			Properties props = buildXSLParameters(request);
//			Enumeration eKeys = props.keys();
//			while (eKeys.hasMoreElements()) {
//				String key = (String) eKeys.nextElement();
//				Object value = props.get(key);
//				trans.setParameter(key, value);
//			}
//
//			// parameter - end
//			trans.transform(xmlSource, new StreamResult(pageContext.getOut()));
//		} catch (TransformerException tfe) {
//
//		}
//
//		return;
//	}

//	protected static Properties buildXSLParameters(HttpServletRequest request) {
//		Properties parameters = new Properties();
//		// PROPERTIES: Read all properties from system configuration
//		Properties cfgProps = (Properties) (MCRConfiguration.instance()
//				.getProperties().clone());
//		Enumeration en = cfgProps.keys();
//		while (en.hasMoreElements()) {
//			String key = (String) en.nextElement();
//			if (key.startsWith("MCR.Module-iview.")) {
//				parameters.put(key, cfgProps.get(key));
//			}
//		}
//
//		// handle HttpSession
//		HttpSession session = request.getSession(false);
//		if (session != null) {
//			String jSessionID = MCRConfiguration.instance().getString(
//					"MCR.session.param", ";jsessionid=");
//			parameters.put("HttpSession", jSessionID + session.getId());
//			parameters.put("JSessionID", jSessionID + session.getId());
//		}
//
//		MCRSession mcrSession = MCRSessionMgr.getCurrentSession();
//		parameters.put("CurrentUser", mcrSession.getCurrentUserID());
//		parameters.put("RequestURL", getCompleteURL(request));
//		parameters.put("WebApplicationBaseURL", MCRServlet.getBaseURL());
//		parameters.put("ServletsBaseURL", MCRServlet.getServletBaseURL());
//		parameters.put("DefaultLang", MCRConfiguration.instance().getString(
//				"MCR.metadata_default_lang", "en"));
//		parameters.put("CurrentLang", mcrSession.getCurrentLanguage());
//		parameters.put("Referer",
//				(request.getHeader("Referer") != null) ? request
//						.getHeader("Referer") : "");
//
//		return parameters;
//	}

	protected static final String getCompleteURL(HttpServletRequest request) throws UnsupportedEncodingException{
		String url = request.getRequestURL().toString();
		String queryString = request.getQueryString();
		if (queryString != null && queryString.length() > 0) {
			url = url+"?"+java.net.URLEncoder.encode(queryString,"UTF-8");
		}

		return url;
	}
	
	protected static final String getHttpSession(HttpServletRequest request){
		HttpSession session = request.getSession(false);
		if (session != null) {
			String jSessionID = MCRConfiguration.instance().getString(
					"MCR.session.param", ";jsessionid=");
			return jSessionID + session.getId();
			
		}
		else return "";
	}

}