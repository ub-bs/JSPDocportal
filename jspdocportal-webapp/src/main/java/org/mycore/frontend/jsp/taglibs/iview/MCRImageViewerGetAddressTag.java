package org.mycore.frontend.jsp.taglibs.iview;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.mycore.frontend.servlets.MCRServlet;

/**
 * Erzeugen eines Links, der den ImageViewer im Vollbildmodus öffnet
 * 
 * Functionality should be equal to:
 * /docportal/modules/UNINSTALLED_module-iview/xsl/mcr-module-startIview.xsl
 * Template: iview.getAddress 
 * 
 * based on CVS-Version 1.7 of the xsl file
 * 
 * @author Robert Stephan
 */
public class MCRImageViewerGetAddressTag extends SimpleTagSupport {
	private String derivID; // Derivate-ID

	private String pathOfImage; // absoluter Pfad des Bildes oder Ordners

	private String height; // Höhe des eingebetteten Fensters

	private String width; // Bretei des eingebetteten Fensters

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
	
//	concat($iview.home,$derivID,$pathOfImage,$HttpSession,'?mode=generateLayout&amp;XSL.MCR.Module-iview.navi.zoom.SESSION=',$scaleFactor,'&amp;XSL.MCR.Module-iview.display.SESSION=',$display,'&amp;XSL.MCR.Module-iview.style.SESSION=',$style,'&amp;XSL.MCR.Module-iview.lastEmbeddedURL.SESSION=',$lastEmbeddedURL,'&amp;XSL.MCR.Module-iview.embedded.SESSION=false&amp;XSL.MCR.Module-iview.move=reset')"
	public void doTag() throws JspException, IOException {
		PageContext pageContext = (PageContext) getJspContext();
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		String httpSession = MCRImageViewerTag.getHttpSession(request);
		String lastEmbeddedURL=MCRImageViewerTag.getCompleteURL(request);
		//
		JspWriter out = getJspContext().getOut();
//		concat($iview.home,$derivID,$pathOfImage,$HttpSession,'?mode=generateLayout&amp;XSL.MCR.Module-iview.navi.zoom.SESSION=',$scaleFactor,'&amp;XSL.MCR.Module-iview.display.SESSION=',$display,'&amp;XSL.MCR.Module-iview.style.SESSION=',$style,'&amp;XSL.MCR.Module-iview.lastEmbeddedURL.SESSION=',$lastEmbeddedURL,'&amp;XSL.MCR.Module-iview.embedded.SESSION=false&amp;XSL.MCR.Module-iview.move=reset')"
		out.write(
			MCRServlet.getServletBaseURL()+"MCRIViewServlet/"+derivID+pathOfImage+httpSession+
			"?mode=generateLayout"+
			"&amp;XSL.MCR.Module-iview.navi.zoom.SESSION="+scaleFactor+
			"&amp;XSL.MCR.Module-iview.display.SESSION="+display+
			"&amp;XSL.MCR.Module-iview.style.SESSION="+style+
			"&amp;XSL.MCR.Module-iview.lastEmbeddedURL.SESSION="+lastEmbeddedURL+
			"&amp;XSL.MCR.Module-iview.embedded.SESSION=false"+
			"&amp;XSL.MCR.Module-iview.move=reset");		
	}

//	public void doTag() throws JspException, IOException {
//		PageContext pageContext = (PageContext) getJspContext();
//		String resolver = "webapp:WEB-INF/stylesheets/mcr-module-startIview.xsl";
//		String xml = "<iview_getaddress " + "derivid=\"" + derivID + "\" "
//				+ "pathofimage=\"" + pathOfImage + "\" " + "height=\"" + height
//				+ "\" " + "width=\"" + width + "\" " + "scalefactor=\""
//				+ scaleFactor + "\" " + "display=\"" + display + "\" "
//				+ "style=\"" + style + "\" " + " />";
//
//		StringWriter strW = new StringWriter();
//		strW.append("");
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
//
//		String result = strW.toString();
//		result = result.substring(result.indexOf(">")+1);
//		
//		getJspContext().getOut().write(result);
//		return;
//	}

}