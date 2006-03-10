package org.mycore.frontend.editor.helper;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.PropertyResourceBundle;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.xml.MCRURIResolver;
import org.mycore.common.xml.MCRXSLTransformation;
import org.mycore.common.xml.MCRURIResolver.MCRResolver;
import org.mycore.datamodel.classifications.MCRClassification;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.servlets.MCRServletJob;

/**
 * this class delivers xml objects that can be included
 * in the MyCoRe Editor Framework
 * 
 * possible mode are so far:
 * 
 * getClassificationItems
 * 
 * @author Helmbrecht
 *
 */
public class MCRGetEditorElements implements MCRResolver {
	private static Logger logger = Logger.getLogger("MCRGetEditorElements"); 
	private static MCRConfiguration CONFIG = MCRConfiguration.instance();
	
	private Properties parseQueryString(String query){
		Properties params = new Properties();
		String[] splitParams = query.replaceAll("&amp;","&").split("&");
		for (int i = 0; i < splitParams.length; i++) {
			String[] splitParam = splitParams[i].split("=");
			params.put(splitParam[0], splitParam[1]);
		}
		return params;
	}
	
	public Element resolveElement(String URI) {
		try{
			String query = URI.substring(URI.indexOf("?") + 1);
			Properties params = parseQueryString(query);
			String mode = params.getProperty("mode");
			if(mode.equals("getHiddenVar")){
				return getHiddenVar(params);
			}else if(mode.equals("getHiddenAttributesForClass")) {
				return getHiddenAttributesForClass(params);
			}else if(mode.equals("getClassificationInItems")) {
				return getClassificationInItems(params);
			}else if(mode.equals("getSpecialCategoriesInItems")) {
				return getSpecialCategoriesInItems(params);
			}
			return null;
		}catch(Exception ex){
			logger.error("could not resolve URI " + URI);
			return new Element("error");
		}
	}

	private Element getClassificationInItems(Properties params) throws TransformerException{
		String classid = params.getProperty("classid");
        if(classid == null || classid.equals("")){
            String prop = params.getProperty("prop");
            String defaultValue = params.getProperty("defaultValue");
            if(defaultValue == null || defaultValue.equals("")) defaultValue = "DocPortal_class_1";
            if(prop != null && !prop.equals("")) {
                classid = MCRConfiguration.instance().getString(prop, defaultValue);
            }else{
                classid = defaultValue ;
            }
        }
        return transformClassToItems(classid);        
	}
	
	private Element transformClassToItems(String classid) throws TransformerException{
        Document classJdom = MCRClassification.receiveClassificationAsJDOM(classid);
        Source xsl = MCRURIResolver.instance().resolve("webapp:WEB-INF/stylesheets/classifications-to-items.xsl","classifications-to-items.jspx");
        return MCRXSLTransformation.transform(classJdom, xsl, new Properties()).getRootElement();		
	}
	
	private Element getSpecialCategoriesInItems(Properties params) throws TransformerException{
		Element retitems = new Element("items");
		String classProp = params.getProperty("classProp");
		String categoryProp = params.getProperty("categoryProp");
		if(classProp != null && categoryProp != null) {
			String classid = MCRConfiguration.instance().getString(classProp, "DocPortal_class_1");
			Element items = transformClassToItems(classid);
			List values = null;
			try{
				values = Arrays.asList(CONFIG.getString(categoryProp).split(","));
			}catch(Exception ex){
				logger.warn("config property " + categoryProp + " must be a comma separated list [" + ex.getMessage() + "]" );
				return items;
			}
			for (Iterator it = items.getDescendants(new ElementFilter("item")); it.hasNext();) {
				Element	item = (Element) it.next();
				if(values.contains(item.getAttributeValue("value"))) {
					retitems.addContent((Element)item.clone());
				}
			}			
		}

		return retitems;
	}	
	
	private Element getHiddenAttributesForClass(Properties params){
		String var = params.getProperty("var").replaceAll("\\.","/");
		String classname = params.getProperty("classname");
		String parasearch = params.getProperty("parasearch");
		String textsearch = params.getProperty("textsearch");
		String notinherit = params.getProperty("notinherit");
		String heritable = params.getProperty("heritable");
		
		// Default-Values
		if(parasearch == null || parasearch.equals("")) parasearch = "true";
		if(textsearch == null || textsearch.equals("")) textsearch = "true";
		if(notinherit == null || notinherit.equals("")) notinherit = "false";
		if(heritable == null || heritable.equals("")) heritable = "true";
		
		Element hiddens = new Element("hiddens");
		Element hidden1 = new Element("hidden");
		hidden1.setAttribute("default", classname);
		hidden1.setAttribute("var", var + "/@class");
		Element hidden2 = new Element("hidden");
		hidden2.setAttribute("default", parasearch);
		hidden2.setAttribute("var", var + "/@parasearch");
		Element hidden3 = new Element("hidden");
		hidden3.setAttribute("default", textsearch);
		hidden3.setAttribute("var", var + "/@textsearch");
		Element hidden4 = new Element("hidden");
		hidden4.setAttribute("default", notinherit);
		hidden4.setAttribute("var", var + "/@notinherit");
		Element hidden5 = new Element("hidden");
		hidden5.setAttribute("default", heritable);
		hidden5.setAttribute("var", var + "/@heritable");
		hiddens.addContent(hidden1);
		hiddens.addContent(hidden2);
		hiddens.addContent(hidden3);
		hiddens.addContent(hidden4);
		hiddens.addContent(hidden5);
		return hiddens;
	}
	
	private Element getHiddenVar(Properties params) throws IOException{
        String lang = params.getProperty("lang");
        String bundle = params.getProperty("bundle");
        String prop = params.getProperty("prop");
        String defaultValue = params.getProperty("defaultValue"); 
        String var = params.getProperty("var");
        
        String propValue = "";
        if(bundle != null && !bundle.equals("")) {
        	if(lang == null || lang.equals("")) {
        		lang = "de";
        	}
        	propValue = PropertyResourceBundle.getBundle(bundle, new Locale(lang)).getString(prop);
        }else{
        	if(defaultValue == null) defaultValue = "";
        	propValue = CONFIG.getString(prop, defaultValue);
        }
        
        Element hiddens = new Element("hiddens");
        Element hidden = new Element("hidden");
        hidden.setAttribute("var", var.replaceAll("\\.","/"));
        hidden.setAttribute("default", propValue);
        
        hiddens.addContent(hidden);
        return hiddens;
	}

}
