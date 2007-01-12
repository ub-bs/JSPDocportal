   
package org.mycore.frontend.jsp.taglibs.iview;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.mycore.frontend.servlets.MCRServlet;

/**
 * Return the url of the main picture if imageViewer is supported for this derivate
 * 
 * * Functionality should be equal to:
 * /docportal/modules/UNINSTALLED_module-iview/xsl/mcr-module-startIview.xsl
 * Template: iview.getEmbedded.iframe 
 * 
 * based on CVS-Version 1.7 of the xsl file
 * @author Robert Stephan
 */
public class MCRImageViewerGetSupportTag extends SimpleTagSupport
{
	private String derivID; 	//Derivate-ID
	private String var; 		//Variable, die das R�ckgabeergebnis aufnimmt.
	
	/**
	 * Setzen der Derivate-ID
	 * @param derivID the derivID to set
	 */
	public void setDerivID(String derivID) {
		this.derivID = derivID;
	}

	/**Setzen des Namens, der Variable, die den R�ckgabewert enth�lt
	 * @param var the name of the variable
	 */
	public void setVar(String var) {
		this.var = var;
	}
	
	
	public void doTag() throws JspException, IOException {
		PageContext pageContext = (PageContext) getJspContext();
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		String httpSession = MCRImageViewerTag.getHttpSession(request);
		
		   //<xsl:value-of select="document(concat($iview.home,$derivID,$JSessionID,'?mode=getMetadata&amp;type=support&amp;XSL.Style=xml'))/mcr-module/support/@mainFile"/>
		String url =
			MCRServlet.getServletBaseURL()+"MCRIViewServlet/"+derivID+httpSession+
		    "?mode=getMetadata&type=support&XSL.Style=xml";
	
		String result ="";
    	SAXBuilder saxBuilder=new SAXBuilder("org.apache.xerces.parsers.SAXParser");
    	try{
    		org.jdom.Document jdomDocument=saxBuilder.build(url);
    		XPath xpath = XPath.newInstance("/mcr-module/support/@mainFile");
    		result = xpath.valueOf(jdomDocument);    		
    	}
    	catch(JDOMException jde){
    		result="";
    	}		
		pageContext.setAttribute(var, result);
		
	}
	
//	public void doTag() throws JspException, IOException {
//		PageContext pageContext = (PageContext) getJspContext();
//		 String resolver="webapp:WEB-INF/stylesheets/mcr-module-startIview.xsl";
//		 String xml="<iview_getsupport derivid=\""+derivID+"\" />";
//		 StringWriter strW = new StringWriter();
//		 strW.write("");
//		 String result = ""; 
//		try{
//			Source xsltSource = MCRURIResolver.instance().resolve(resolver,"MCRImageViewerGetSupportTag.java");
//			Source xmlSource = new StreamSource(new StringReader(xml));
//           	// 	das Factory-Pattern unterst�tzt verschiedene XSLT-Prozessoren
//        	TransformerFactory transFact = TransformerFactory.newInstance();
//        	Transformer trans = transFact.newTransformer(xsltSource);
//        	
//        	HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
//        	Properties props = MCRImageViewerTag.buildXSLParameters(request);
//        	Enumeration eKeys = props.keys();
//        	while(eKeys.hasMoreElements()){
//        		String key = (String)eKeys.nextElement();
//        		Object value = props.get(key);
//        		trans.setParameter(key, value);
//        	}      	
//        	
//        	//parameter - end
//        	trans.transform(xmlSource, new StreamResult(strW));
//        	
//        	SAXBuilder saxBuilder=new SAXBuilder("org.apache.xerces.parsers.SAXParser");
//        	Reader stringReader=new StringReader(strW.toString());
//        
//        	try{
//        		org.jdom.Document jdomDocument=saxBuilder.build(stringReader);
//        		result = jdomDocument.getContent(0).getValue();
//        	}
//        	catch(JDOMException jde){
//        		result="";
//        	}
//        		
//		}
//        catch(TransformerException tfe){
//        	
//        }
//        pageContext.setAttribute(var, result);
//	}	
}