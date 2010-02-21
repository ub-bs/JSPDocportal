package org.mycore.frontend.jsp.taglibs.docdetails;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

public class MCRDocdetailsNamespaceContext implements NamespaceContext {
	private Map<String, String> prefixMapping;
	
	public MCRDocdetailsNamespaceContext(){
		prefixMapping = new Hashtable<String, String>();
		prefixMapping.put("xml", XMLConstants.XML_NS_URI);
		prefixMapping.put("xlink", "http://www.w3.org/1999/xlink");
	}
	
	public String getNamespaceURI(String prefix) {
		return prefixMapping.get(prefix);
	}

	//not needed for XPath processing
	public String getPrefix(String arg0) {
		throw new UnsupportedOperationException();
	}

	//not needed for XPath processing
	@SuppressWarnings("unchecked")
	public Iterator getPrefixes(String arg0) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}
	
	
	/**
	 * register a new namespace
	 * @param prefix the namespace prefix
	 * @param url the namespace url
	 */
	public void addNamespace(String prefix, String url){
		prefixMapping.put(prefix, url);
	}

}
