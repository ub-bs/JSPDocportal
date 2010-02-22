package org.mycore.frontend.jsp.taglibs.docdetails;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;
import org.apache.taglibs.standard.tag.common.xml.JSTLXPathNamespaceContext;

/**
 * This class will add Namespace declarations (prefix uri pairs) to the XPathUtil class
 * which is used by the JSTL XML Tag Library to process XPath expressions.
 * 
 * @author Robert Stephan
 *
 */
public class MCRDocDetailsSetNamespaceTag extends SimpleTagSupport {
	private String prefix="";
	private String uri="";

	/**
	 * the prefix
	 * @param prefix
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	
	/**
	 * the uri 
	 * @param uri
	 */
	public void setUri(String uri){
		this.uri = uri;
	}
	
	
	@SuppressWarnings("unchecked")
	public void doTag() throws JspException, IOException {
		try
		{
			Class c_XPathUtil = Class.forName("org.apache.taglibs.standard.tag.common.xml.XPathUtil");
			// XPathUtil
			// private static JSTLXPathNamespaceContext jstlXPathNamespaceContext = null;
			Field field = c_XPathUtil.getDeclaredField("jstlXPathNamespaceContext");
			field.setAccessible( true );
			JSTLXPathNamespaceContext nsContext = (JSTLXPathNamespaceContext)field.get(null);
			if(nsContext == null){
				nsContext = new JSTLXPathNamespaceContext();
			}
			
			// JSTLXPathNamespaceContext
			// protected void addNamespace(String prefix, String uri ) {
			Class c_JSTLXPathNamespaceContext = Class.forName("org.apache.taglibs.standard.tag.common.xml.JSTLXPathNamespaceContext");
			Method m_addNamespace=  c_JSTLXPathNamespaceContext.getDeclaredMethod("addNamespace", String.class, String.class);
			m_addNamespace.setAccessible(true);
			m_addNamespace.invoke(nsContext, prefix, uri);
		}
		catch( Exception e )
		{
			Logger.getLogger(MCRDocDetailsSetNamespaceTag.class).error("Something went wrong adding the namespace", e);
		}
	}
}
