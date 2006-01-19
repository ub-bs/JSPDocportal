/*
 * Created on 12.04.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.mycore.frontend.jsp.format;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;

import org.apache.log4j.Logger;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;
import org.jdom.output.DOMOutputter;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.transform.XSLTransformException;
import org.jdom.transform.XSLTransformer;
import org.jdom.xpath.XPath;
import org.mycore.backend.query.MCRQueryManager;
import org.mycore.common.JSPUtils;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRDefaults;
import org.mycore.common.MCRException;
import org.mycore.common.MCRUsageException;
import org.mycore.common.xml.MCRURIResolver;
import org.mycore.common.xml.MCRXMLHelper;
import org.mycore.datamodel.classifications.MCRCategoryItem;
import org.mycore.datamodel.ifs.MCRDirectory;
import org.mycore.datamodel.ifs.MCRFile;
import org.mycore.datamodel.ifs.MCRFilesystemNode;
import org.mycore.datamodel.metadata.MCRDerivate;
import org.mycore.datamodel.metadata.MCRMetaISO8601Date;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.jsp.NavServlet;
import org.mycore.services.fieldquery.MCRHit;
import org.mycore.services.fieldquery.MCRResults;

/**
 * @author Heiko Helmbrecht
 *
 * useful methods for jsp-development
 * 
 */
public class MCRResultFormatter {

	protected static Logger logger;
	protected static MCRConfiguration CONFIG = MCRConfiguration.instance();
	protected static String languageBundleBase = CONFIG.getString("MCR.languageResourceBundleBase","messages");
	protected static String WebApplicationBaseURL ;

	protected static Map resultlistMap;
	protected static Map docdetailsMap;
	
	protected static org.jdom.Namespace xlinkNamespace;
	
    static {
        logger=Logger.getLogger(MCRResultFormatter.class);
        WebApplicationBaseURL = NavServlet.getNavigationBaseURL();
        xlinkNamespace = org.jdom.Namespace.getNamespace("xlink",
    			MCRDefaults.XLINK_URL);
    }	
    
    private static MCRResultFormatter singleton;

	public MCRResultFormatter() {
		initialize();
	}
	
    public static MCRResultFormatter getInstance() {
        if (singleton == null) {
            singleton = new MCRResultFormatter();
        }

        return singleton;
    }  
    
    private void initialize(){
    	if (resultlistMap == null) {
    		resultlistMap = new HashMap();
    	}
    	if (docdetailsMap == null) {
    		docdetailsMap = new HashMap();
    	}    	
    }

	/**
	 * evaluates whether a query-result was sorted by a given field 
	 * or a given order. an example is given below. (needed for sort form)
	 *
	 * 
	 * @param sortby jdom-Elemen &lt;sortby&gt;
	 * @param sortprio order-priority of a sortfield 
	 * @param attributeName attributename of the field-element
	 * @param attributeValue attribute-value of the given element
	 * 
	 * @return boolean
	 * <br>&nbsp;<br>
	 * <b>Example</b><br>
	 * sortby=<br>
	 * &lt;sortby&gt;<br>
	 *    &lt;field field="title" order="ascending" /&gt;<br>
	 *    &lt;field field="author" order="decending" /&gt;<br>
	 * &lt;/sortby&gt;<br>
	 * <br>
	 * isSorted(sortby,1,"order","ascending") = *true*<br>
	 * isSorted(sortby,2,"field","author") = *true*<br>
	 * isSorted(sortby,2,"order","ascending") = *false*<br> 
	 */
	public boolean isSorted(org.jdom.Element sortby, int sortprio, String attributeName, String attributeValue) {
	    org.jdom.Element sortField = (Element) sortby.getChildren("field").get(sortprio - 1);
		if (sortField != null) {
			if (sortField.getAttributeValue(attributeName) != null &&
				sortField.getAttributeValue(attributeName).equals(attributeValue) ) {
				return true;
			}
		} 
		return false;
	}
	
	/**
	 * returns the value of a given jdom-Content and the relative xpath expression
	 * @param jdom a jdom Element
	 * @param xpath xpath-expression, namespaces includable
	 * @return String
	 */
    public String getSingleXPathValue(org.jdom.Content jdom,String xpath) {
    	try {
    		Object obj = XPath.selectSingleNode( jdom, xpath);
    		if ( obj instanceof org.jdom.Attribute) 
    			return ((org.jdom.Attribute) obj).getValue();
    		if ( obj instanceof org.jdom.Element)
    			return ((org.jdom.Element) obj).getText();
		} catch (Exception e) {
		   logger.debug("wrong xpath expression: " + xpath);
		}
    	return "";
    }
	
	/**
	 * returns the value of a given jdom-Document and the relative xpath expression
	 * @param jdom a jdom Element
	 * @param xpath xpath-expression, namespaces includable
	 * @return String
	 */
    public String getSingleXPathValue(org.jdom.Document jdom,String xpath) {
    	return getSingleXPathValue(jdom.getRootElement(), xpath);
    }    
   
    /**
     * returns the MyCoRe-Category-Text of an given MyCoRe-Classification and language
     * @param jdom a jdom element
     * @param xpath xpath-expression leading to an classification-element
     * @param lang requested language
     * @return
     */
    public String getCategoryText(org.jdom.Content jdom, String xpath, String lang) {
    	try {
    		Element classification = (Element) XPath.selectSingleNode(jdom,xpath);
    		String classifID = classification.getAttributeValue("classid");
    		String categID = classification.getAttributeValue("categid");
            org.mycore.datamodel.classifications.MCRCategoryItem categItem = 
            	org.mycore.datamodel.classifications.MCRCategoryItem.getCategoryItem(classifID, categID); 
            return categItem.getText(lang);    		
		} catch (Exception e) {
		   //logger.debug("wrong xpath expression: " + xpath);
		}
    	return "";    	
    }    

    /**
     * Parses an XML file from the WEB-INF/classes-directory and returns it as JDOM.
     * @param resourceName File of the xml file
     * @return jdom-Document
     */
    public Document parseXmlClassResource(String resourceName){
        InputStream in = MCRResultFormatter.class.getResourceAsStream("/" + resourceName);
        Document document = null;
        if (in == null) {
            logger.error("Missing field configuration file: " + resourceName );
        }else{
            try {
                SAXBuilder builder = new SAXBuilder(false);
                document = builder.build(in);
            } catch (Exception e) {
                logger.error(e);
            }
        }
        return document;
    } 

    public Element getDateValues(Document doc, String xpath, String separator, String terminator, String lang, String introkey,String escapeXml, String datePatternType) {
    	Element metaValues = new Element("metavalues");
    	metaValues.setAttribute("type","linkedCategory");
    	metaValues.setAttribute("separator",separator);
    	metaValues.setAttribute("terminator",terminator);     	
    	metaValues.setAttribute("introkey", introkey);
    	metaValues.setAttribute("escapeXml", escapeXml);    	
    	try {
			for(Iterator it = XPath.selectNodes(doc,xpath).iterator(); it.hasNext(); ) {
				// ignore the lang-tags, in every lang-tag, if availabe, must be the same date
			    Element el = (Element) it.next();
			    Element metaValue = new Element("metavalue");
				metaValue.setAttribute("href","");
				String dateString = el.getText();
				if (lang.equals("")) lang = "de";
				if (datePatternType == null || datePatternType.equals("")) 
					datePatternType = "Standard";
				String datePattern;
				try {
					datePattern = PropertyResourceBundle.getBundle(languageBundleBase, new Locale(lang)).getString("MCR.Dateformat." + datePatternType);	
				}catch(MissingResourceException m) {
					datePattern = "EEEE, d MMMM yyyy HH:mm:ss";
				}
				dateString = formateDate(dateString, datePattern, lang); 
				metaValue.setAttribute("text",dateString);
				metaValues.addContent(metaValue);			    
			    break;
			}
		} catch (JDOMException e) {
			logger.debug("error occured", e);
			return metaValues;
		} 
    	return metaValues;
    }    

    
    public static Element getXPathValues(Document doc, String xpath, String separator, String terminator, String lang, String introkey, String escapeXml, String sStart) {
    	Element metaValues = new Element("metavalues");
    	metaValues.setAttribute("type","linkedCategory");
    	metaValues.setAttribute("separator",separator);
    	metaValues.setAttribute("terminator",terminator);    	
    	metaValues.setAttribute("introkey", introkey); 
    	metaValues.setAttribute("escapeXml", escapeXml);
    	metaValues.setAttribute("start", sStart);
    	try {
    		int cnt =1;
    		int start = 1; 
    		try { start = Integer.parseInt(sStart);}
    		catch (Exception parameterfailes) { start=1; }
    		
			for(Iterator it = XPath.selectNodes(doc,xpath).iterator(); it.hasNext(); ) {
				Object obj = (Object) it.next();
				String text = "";
				if (obj instanceof org.jdom.Element) {
				    Element el = (Element) obj;
				    String attrLang = el.getAttributeValue("lang",Namespace.XML_NAMESPACE); 
				    if ( lang.equals("") || attrLang == null || attrLang.equals(lang) ) {
				    		text = el.getText();
				    }
				} else if (obj instanceof org.jdom.Attribute) {
					org.jdom.Attribute at = (org.jdom.Attribute) obj;
					text = at.getValue();
				}
				if ( (text != null) && (!text.equals("")) && cnt >= start) {
					Element metaValue = new Element("metavalue");
					metaValue.setAttribute("href","");
					metaValue.setAttribute("text", text);
					metaValues.addContent(metaValue);					
				}
				cnt++;
			}
		} catch (JDOMException e) {
			logger.debug("error occured", e);
			return metaValues;
		} catch (Throwable e) {
			logger.debug("error occured", e);
			return metaValues;
		} 
		
    	return metaValues;
    }
    
    public static Element getImagesFromObject(Document doc, String xpath, String separator, String terminator, String lang, String introkey, String escapeXml) {
    	Element digitalObjects = new Element("images");
    	digitalObjects.setAttribute("type","image");
    	digitalObjects.setAttribute("separator",separator);
    	digitalObjects.setAttribute("terminator",terminator);    	
    	digitalObjects.setAttribute("introkey", introkey);
    	digitalObjects.setAttribute("escapeXml", escapeXml);    	
    	MCRDerivate mcr_der = new MCRDerivate();
    	try {
			for(Iterator it = XPath.selectNodes(doc,xpath).iterator(); it.hasNext(); ) {
			    String derivateID = ((Element) it.next()).getAttributeValue("href",Namespace.getNamespace("xlink",MCRDefaults.XLINK_URL));
			    mcr_der.receiveFromDatastore(derivateID);

			    MCRDirectory root;
			    root = MCRDirectory.getRootDirectory(derivateID);
			    MCRFilesystemNode[] myfiles = root.getChildren();
			    for ( int i=0; i< myfiles.length; i++) {
			    	MCRFile theFile = (MCRFile) myfiles[i];
			    	if ( theFile.getContentTypeID().indexOf("jpeg")>= 0 ) {
					    Element digitalObject = new Element("image");			    		
			    		digitalObject.setAttribute("derivid",derivateID);
			    		digitalObject.setAttribute("path",myfiles[i].getPath());
			    		digitalObject.setAttribute("name",myfiles[i].getName());
					    digitalObjects.addContent(digitalObject);
			    	}
			    }	
			}
    	} catch (Exception e) {
    		logger.debug("error occured", e);
    		return digitalObjects;
    	} 
		return digitalObjects;
    }    
    
    public Element getConcatenatedPersons(Document doc, String xpath, String separator, String terminator, String introkey, String escapeXml) {
    	Element metaValues = new Element("metavalues");
    	metaValues.setAttribute("type","linkedCategory");
    	metaValues.setAttribute("separator",separator);   
    	metaValues.setAttribute("terminator",terminator);    	
    	metaValues.setAttribute("introkey", introkey);
    	metaValues.setAttribute("escapeXml", escapeXml);    	
    	MCRObject mcr_obj = new MCRObject();
    	try {
			for(Iterator it = XPath.selectNodes(doc,xpath).iterator(); it.hasNext(); ) {
			    Element personlink = (Element) it.next();
			    MCRObjectID person_id = new MCRObjectID(personlink
			        .getAttributeValue("href",Namespace.getNamespace("xlink",MCRDefaults.XLINK_URL)));
			    try{
			        mcr_obj.receiveFromDatastore(person_id); 
			        Element creator_root = mcr_obj.createXML().getRootElement();
			        String creatorName = (String) XPath.selectSingleNode(creator_root,
			            "concat(metadata/names/name/callname,' ',metadata/names/name/surname)");
					Element metaValue = new Element("metavalue");
					metaValue.setAttribute("href","");
					metaValue.setAttribute("text",creatorName);			        
					metaValues.addContent(metaValue);			        
			    }catch (Exception ex) {
			        logger.debug("error occured", ex);
			    	return metaValues;			        
			    }   
			}
		} catch (MCRException e) {
			logger.debug("error occured", e);
	    	return metaValues;
		} catch (JDOMException e) {
			logger.debug("error occured", e);
	    	return metaValues;
		}
    	return metaValues;		
    }

    public Element getCategoryTexts(Document doc, String xpath, String separator, String terminator, String lang, String escapeXml) {
    	Element metaValues = new Element("metavalues");
    	metaValues.setAttribute("type","linkedCategory");
    	metaValues.setAttribute("separator",separator);
    	metaValues.setAttribute("terminator",terminator);
    	metaValues.setAttribute("escapeXml", escapeXml);    	
    	try {
			for(Iterator it = XPath.selectNodes(doc,xpath).iterator(); it.hasNext(); ) {
				Element elClass = (Element) it.next();
				String classID = elClass.getAttributeValue("classid");
				String categID = elClass.getAttributeValue("categid");
				MCRCategoryItem categItem = MCRCategoryItem.getCategoryItem(classID,categID);
				Element metaValue = new Element("metavalue");
				metaValue.setAttribute("text",categItem.getText(lang));
				metaValues.addContent(metaValue);
			}
		} catch (MCRUsageException e) {
			logger.debug("error occured", e);
			return null;
		} catch (JDOMException e) {
			logger.debug("error occured", e);
			return null;
		} 
    	return metaValues;
    }
    
    public Element getLinkedCategoryTexts(Document doc, String xpath, String separator, String terminator, String lang, String escapeXml) {
    	Element metaValues = new Element("metavalues");
    	metaValues.setAttribute("type","linkedCategory");
    	metaValues.setAttribute("separator",separator);
    	metaValues.setAttribute("terminator",terminator);
    	metaValues.setAttribute("escapeXml", escapeXml);    	
    	try {
			for(Iterator it = XPath.selectNodes(doc,xpath).iterator(); it.hasNext(); ) {
				Element elClass = (Element) it.next();
				String classID = elClass.getAttributeValue("classid");
				String categID = elClass.getAttributeValue("categid");
				if ( classID != null ) {
					MCRCategoryItem categItem = MCRCategoryItem.getCategoryItem(classID,categID);
					Element metaValue = new Element("metavalue");
					metaValue.setAttribute("href",categItem.getURL());
					metaValue.setAttribute("target","new");
					metaValue.setAttribute("text",categItem.getText(lang));
					metaValues.addContent(metaValue);
				}
			}
		} catch (MCRUsageException e) {
			logger.debug("error occured", e);
			return null;
		} catch (JDOMException e) {
			logger.debug("error occured", e);
			return null;
		} 
    	return metaValues;
    }    
    
    public static Element getDigitalObjectsValues(Document doc, 
        	String xpath, String separator, String terminator, String lang, String introkey, String escapeXml, boolean all) {
        	Element digitalObjects = new Element("digitalobjects");
        	digitalObjects.setAttribute("type","digitalObject");
        	digitalObjects.setAttribute("separator",separator);
        	digitalObjects.setAttribute("terminator",terminator);    	
        	digitalObjects.setAttribute("introkey", introkey);
        	digitalObjects.setAttribute("escapeXml", escapeXml);    	
        	MCRDerivate mcr_der = new MCRDerivate();
        	try {
    			for(Iterator it = XPath.selectNodes(doc,xpath).iterator(); it.hasNext(); ) {
    			    String derivateID = ((Element) it.next()).getAttributeValue("href",Namespace.getNamespace("xlink",MCRDefaults.XLINK_URL));
    			    mcr_der.receiveFromDatastore(derivateID);
    			    String derivlabel = mcr_der.getLabel();
    			    String derivmain = mcr_der.getDerivate().getInternals().getMainDoc();
    			    
    			    MCRDirectory root;
    			    root = MCRDirectory.getRootDirectory(derivateID);
    			    MCRFilesystemNode[] myfiles = root.getChildren();
    			    Element digitalObject = new Element("digitalobject");
    			    
    			    int fileCnt = 0;
    			    
    			    if (all) {  //all data objects 
    			    	fileCnt = myfiles.length;
    			    } else { 	//only the maindoc
    			    	fileCnt = 1; 
    			    	myfiles[0] = (MCRFile) root.getChildByPath(derivmain);
    			    	if (myfiles[0] == null) {
    				    	logger.error("SEEMS TO EXIST A DERIVATE WITHOUT A DIGITAL OBJECT. REVIEW DERIVATE WITH ID: " + derivateID);
    				    	continue;
    				    }
    			    }
    				for ( int i=0; i< fileCnt; i++) {
    			    	    MCRFile theFile = (MCRFile) myfiles[i];					    
    					    String size = String.valueOf(theFile.getSize());
    					    String lastModified = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format( theFile.getLastModified().getTime());
    					    String contentType = theFile.getContentTypeID();
    					    String md5 = theFile.getMD5();
    					    
    					    digitalObject = new Element("digitalobject");
    					    digitalObject.setAttribute("derivid",derivateID);
    					    digitalObject.setAttribute("derivlabel",derivlabel);
    				    	digitalObject.setAttribute("derivmain",theFile.getName());
    					    digitalObject.setAttribute("size",size);
    					    digitalObject.setAttribute("lastModified",lastModified);
    					    digitalObject.setAttribute("contentType",contentType);
    					    digitalObject.setAttribute("md5",md5);
    					    if ( i==0) 	digitalObject.setAttribute("pos", "first");
    					    else if ( i+1 == fileCnt ) 	digitalObject.setAttribute("pos", "last");
    					    else     	digitalObject.setAttribute("pos", String.valueOf(i));
    					    digitalObjects.addContent(digitalObject);
    			    }	
    			}
    		} catch (Exception e) {
    			logger.debug("error occured", e);
    			return digitalObjects;
    		} 
        	return digitalObjects;
        }

    public Element getMailValues(Document doc, String xpath, String separator, String terminator, String lang, String introkey, String escapeXml) {
    	Element metaValues = new Element("metavalues");
    	metaValues.setAttribute("type","MailValues");
    	metaValues.setAttribute("separator",separator);   
    	metaValues.setAttribute("terminator",terminator);    	
    	metaValues.setAttribute("introkey", introkey);
    	metaValues.setAttribute("escapeXml", escapeXml);    	
    	try {
			for(Iterator it = XPath.selectNodes(doc,xpath).iterator(); it.hasNext(); ) {
			    Element el = (Element) it.next();
			    String attrLang = el.getAttributeValue("lang",Namespace.XML_NAMESPACE); 
			    if ( lang.equals("") || attrLang == null || attrLang.equals(lang) ) {
						Element metaValue = new Element("metavalue");
						String emailaddress = el.getText();
						if (emailaddress == null)
							continue;
						metaValue.setAttribute("href","mailto:" + emailaddress);
						metaValue.setAttribute("type",el.getName());
						metaValue.setAttribute("text",emailaddress);
						metaValues.addContent(metaValue);			    	
			    }
			}
		} catch (JDOMException e) {
			logger.debug("error occured", e);
			return metaValues;
		} 
    	return metaValues;
    }
    
    public Element getLinkValues(Document doc, String xpath, String separator, String terminator, String lang, String introkey, String escapeXml) {
    	Element metaValues = new Element("metavalues");
    	metaValues.setAttribute("type","LinkValues");
    	metaValues.setAttribute("separator",separator);   
    	metaValues.setAttribute("terminator",terminator);    	
    	metaValues.setAttribute("introkey", introkey);
    	metaValues.setAttribute("escapeXml", escapeXml);    	
    	try {
			for(Iterator it = XPath.selectNodes(doc,xpath).iterator(); it.hasNext(); ) {
			    Element el = (Element) it.next();
			    String attrLang = el.getAttributeValue("lang",Namespace.XML_NAMESPACE); 
			    if ( lang.equals("") || attrLang == null || attrLang.equals(lang) ) {
						Element metaValue = new Element("metavalue");
						String href = el.getAttributeValue("href", xlinkNamespace);
						String title = el.getAttributeValue("title", xlinkNamespace);
						if (href == null) 
							continue;
						if (title == null) title = href;
						metaValue.setAttribute("href", href);
						metaValue.setAttribute("type",el.getName());
						metaValue.setAttribute("text",title);
						metaValues.addContent(metaValue);			    	
			    }
			}
		} catch (JDOMException e) {
			logger.debug("error occured", e);
			return metaValues;
		} 
    	return metaValues;
    }    

    /**
     * returns the metavalues-Element for 
     * @param doc
     * @param xpath must be to xpath expression separated by a comma, with ID and name of the author
     * @param separator
     * @param terminator
     * @param lang
     * @param introkey
     * @param escapeXml
     * @return
     */
  
    public Element getAuthorJoinValues(Document doc, String xpath, String separator, String terminator, String lang, String introkey, String escapeXml) {
    	Element metaValues = new Element("metavalues");
    	metaValues.setAttribute("type","AuthorJoin");
    	metaValues.setAttribute("separator",separator);   
    	metaValues.setAttribute("terminator",terminator);    	
    	metaValues.setAttribute("introkey", introkey);
    	metaValues.setAttribute("escapeXml", escapeXml);
    	String[] xpathArray = xpath.split(",");

    	try {
    		Element docRoot = doc.getRootElement();
    		String mcrid = getSingleXPathValue(docRoot,xpathArray[0]);
    		String name = getSingleXPathValue(docRoot,xpathArray[1]);;
    		StringBuffer linkSB = new StringBuffer(WebApplicationBaseURL)
    			.append("content/authorsdocuments.jsp?id=")
    			.append(mcrid).append("&name=").append(name);
    		Element metaValue = new Element("metavalue");
			metaValue.setAttribute("href",linkSB.toString());
			metaValue.setAttribute("type","authorjoin");
			metaValue.setAttribute("text","start-search");
			metaValues.addContent(metaValue);
		} catch (Exception e) { 
			logger.debug("error occured", e);
			return metaValues;
		} 
    	return metaValues;
    }      
    
    public Element getBooleanValues(Document doc, String xpath, String separator, String terminator, String lang, String introkey, String escapeXml) {
    	Element metaValues = new Element("metavalues");
    	metaValues.setAttribute("type","BooleanValues");
    	metaValues.setAttribute("separator",separator);   
    	metaValues.setAttribute("terminator",terminator);    	
    	metaValues.setAttribute("introkey", introkey);
    	metaValues.setAttribute("escapeXml", escapeXml);    	
    	try {
			for(Iterator it = XPath.selectNodes(doc,xpath).iterator(); it.hasNext(); ) {
			    Element el = (Element) it.next();
			    String attrLang = el.getAttributeValue("lang",Namespace.XML_NAMESPACE); 
			    if ( lang.equals("") || attrLang == null || attrLang.equals(lang) ) {
						Element metaValue = new Element("metavalue");
						metaValue.setAttribute("href","");
						metaValue.setAttribute("type",el.getName());
						metaValue.setAttribute("text",el.getText());
						metaValues.addContent(metaValue);			    	
			    }
			}
		} catch (JDOMException e) {
			logger.debug("error occured", e);
			return metaValues;
		} 
    	return metaValues;
    }
    
    public Element getAddressValues(Document doc, String xpath, String separator, String terminator, String lang, String introkey, String escapeXml) {
    	Element metaValues = new Element("metavalues");
    	metaValues.setAttribute("type","Address");
    	metaValues.setAttribute("separator",separator);   
    	metaValues.setAttribute("terminator",terminator);    	
    	metaValues.setAttribute("introkey", introkey);
    	metaValues.setAttribute("escapeXml", escapeXml);    	
    	try {
			for(Iterator it = XPath.selectNodes(doc,xpath).iterator(); it.hasNext(); ) {
			    Element address = (Element) it.next();
			    String attrLang = address.getAttributeValue("lang",Namespace.XML_NAMESPACE); 
			    if ( lang.equals("") || attrLang == null || attrLang.equals(lang) ) {
						Element street = new Element("metavalue");
						street.setAttribute("href","");
						StringBuffer sb = new StringBuffer(address.getChildText("street"))
							.append(" ").append(address.getChildText("number"));
						street.setAttribute("text",sb.toString());
					    metaValues.addContent(street);
					    Element city = new Element("metavalue");
					    city.setAttribute("href","");
					    sb = new StringBuffer(address.getChildText("zipcode"))
					    	.append(" ").append(address.getChildText("city"));
					    city.setAttribute("text",sb.toString());
					    metaValues.addContent(city);
					    Element country = new Element("metavalue");
					    country.setAttribute("href","");
					    sb = new StringBuffer(address.getChildText("state"))
					    	.append(", ").append(address.getChildText("country"));
					    country.setAttribute("text",sb.toString());
					    metaValues.addContent(country);					    
			    }
			}
		} catch (JDOMException e) {
			logger.debug("error occured", e);
			return metaValues;
		}catch (Throwable e) {
			logger.debug("error occured", e);
			return metaValues;
		}
		
		
    	return metaValues;
    }   
    
    public static Element getChildsFromObject(Document doc, String xpath, String separator, String terminator, String lang, String introkey, String escapeXml) {
    	Element childObjects = new Element("childs");
    	childObjects.setAttribute("type","child");
    	childObjects.setAttribute("separator",separator);
    	childObjects.setAttribute("terminator",terminator);    	
    	childObjects.setAttribute("introkey", introkey);
    	childObjects.setAttribute("escapeXml", escapeXml);    	
    	//MCRObject mcr_obj = new MCRObject();
    	try {
			for(Iterator it = XPath.selectNodes(doc,xpath).iterator(); it.hasNext(); ) {
			    String childID = ((Element) it.next()).getAttributeValue("href",Namespace.getNamespace("xlink",MCRDefaults.XLINK_URL));
			    //mcr_obj.receiveFromDatastore(childID);
			    Element childObject = new Element("child");			    		
			    childObject.setAttribute("childID",childID);
			    childObjects.addContent(childObject);
			}
    	} catch (Exception e) {
    		logger.debug("error occured", e);
    		return childObjects;
    	} 
		return childObjects;
    }    

    public Element getFormattedMCRDocDetailContent(org.jdom.Document doc, String xpath, 
    		String separator, String terminator, String lang, 
    		String templatetype, String introkey, String escapeXml, String start) {
    	

    	if (templatetype.equals("tpl-author_links"))
    		return getConcatenatedPersons(doc,xpath,separator,terminator,introkey,escapeXml);
    	if (templatetype.equals("tpl-text-values"))
    		return getXPathValues(doc,xpath,separator,terminator,lang,introkey,escapeXml,start);
    	if (templatetype.equals("tpl-classification"))
    		return getLinkedCategoryTexts(doc,xpath,separator,terminator,lang,escapeXml);
    	if (templatetype.startsWith("tpl-date-values"))
    		return getDateValues(doc,xpath,separator,terminator,lang,introkey,escapeXml, templatetype.substring("tpl-date-values".length()));
    	if (templatetype.equals("tpl-document"))
    		return getDigitalObjectsValues(doc,xpath,separator,terminator,lang,introkey,escapeXml,false);
    	if (templatetype.equals("tpl-alldocument"))
    		return getDigitalObjectsValues(doc,xpath,separator,terminator,lang,introkey,escapeXml,true);     	

    	if (templatetype.equals("tpl-boolean"))
    		return getBooleanValues(doc,xpath,separator,terminator,lang,introkey,escapeXml);
    	if (templatetype.equals("tpl-address"))
    		return getAddressValues(doc,xpath,separator,terminator,lang,introkey,escapeXml);    	
    	if (templatetype.equals("tpl-email"))
    		return getMailValues(doc,xpath,separator,terminator,lang,introkey,escapeXml);
    	if (templatetype.equals("tpl-metalink"))
    		return getLinkValues(doc,xpath,separator,terminator,lang,introkey,escapeXml);    	
    	if (templatetype.equals("tpl-authorjoin"))
    		return getAuthorJoinValues(doc,xpath,separator,terminator,lang,introkey,escapeXml);    	
    	
    	if (templatetype.equals("tpl-image"))
    		return getImagesFromObject(doc,xpath,separator,terminator,lang,introkey,escapeXml);    	
    	if (templatetype.equals("tpl-child"))
    		return getChildsFromObject(doc,xpath,separator,terminator,lang,introkey,escapeXml);    	

    	return null;
    }
    
    
    public Document getFormattedDocDetails(Document doc, String lang) {
    	MCRObjectID mcrid = new MCRObjectID(doc.getRootElement().getAttributeValue("ID"));
		String docType = mcrid.getTypeId();
        Element definition = (docdetailsMap.containsKey(docType)) ?
        		(Element)docdetailsMap.get(docType) : addDocType2DocdetailsMap(docType);		
        Element allmetavalues = processDocDetails(doc, definition, lang, "", docType);
        Document allMetaValues = new Document(allmetavalues);
        //System.out.println(JSPUtils.getPrettyString(allMetaValues));
        return allMetaValues;
    }
    
    public Element processDocDetails(Document doc, Element definition, String lang, String resultlistLink, String docType ) {
    	Element mycoreobject = doc.getRootElement();
    	String mcrObjId = mycoreobject.getAttributeValue("ID");
    	Element allMetaValuesRoot = new Element("all-metavalues");
        allMetaValuesRoot.setAttribute("ID",mcrObjId);
        allMetaValuesRoot.setAttribute("docType",docType);
        
        for (Iterator it = definition.getDescendants(new ElementFilter("MCRDocDetail")); it.hasNext();) {
        	
            Element field = (Element) it.next();
            Element metaname = new Element("metaname");
            if (field.getAttributeValue("rowtype").equals("standard")
	            || field.getAttributeValue("rowtype").equals("image")
	            || field.getAttributeValue("rowtype").equals("children") ) {
            			metaname.setAttribute("name", field.getAttributeValue("labelkey"));
            			metaname.setAttribute("type",field.getAttributeValue("rowtype"));
                        List lContent = field.getChildren("MCRDocDetailContent");
                        for(Iterator it2 = lContent.iterator(); it2.hasNext();) {
                        	                        	
                            Element content = (Element) it2.next();
                            String languageRequired = content.getAttributeValue("languageRequired");
                            String paramLang = languageRequired.equals("no") ? "":lang;
                            String templatetype = content.getAttributeValue("templatetype");
                            String xpath = content.getAttributeValue("xpath");
                            String contentSeparator = content.getAttributeValue("separator"); 
                            String contentTerminator = content.getAttributeValue("terminator"); 
                            String introkey = content.getAttributeValue("introkey");
                            String escapeXml = content.getAttributeValue("escapeXml");
                            String start = content.getAttributeValue("start");
                            String withResultlistLink = content.getAttributeValue("withResultlistLink");
                            if (introkey == null) introkey = "";
                            if (contentSeparator == null) contentSeparator = ", ";
                            if (contentTerminator == null) contentTerminator = ", ";                        
                            if (escapeXml == null) escapeXml = "true";
                            if (start == null) start = "1";
                            Element metaValues = getFormattedMCRDocDetailContent(doc, xpath, 
                                    contentSeparator, contentTerminator, paramLang, templatetype, introkey, escapeXml, start);
                            if ((metaValues != null) && (metaValues.getChildren().size() > 0))
                                metaname.addContent(metaValues);
                            if (withResultlistLink != null && withResultlistLink.equals("true")) {
                            	Element resultlistLinkElement = new Element("resultlistLink");
                            	resultlistLinkElement.setAttribute("href", resultlistLink);
                            	metaname.addContent(resultlistLinkElement);
                            }
                        }
                        if (metaname.getChildren() != null && metaname.getChildren().size() > 0) {
                        	allMetaValuesRoot.addContent(metaname);
                        }
            }else if(field.getAttributeValue("rowtype").equals("space")){
            	metaname.setAttribute("type","space");
            	allMetaValuesRoot.addContent(metaname);
            }else if(field.getAttributeValue("rowtype").equals("line")){
                  metaname.setAttribute("type","line");
                  allMetaValuesRoot.addContent(metaname);
            }
        }
        return allMetaValuesRoot ;    	
    }
    
	public Document getFormattedResultContainer(MCRResults result, String lang, int from, int until) {
		MCRObject mcr_obj = new MCRObject();
		Element mcr_results = new Element("mcr_results");
		mcr_results.setAttribute("total-hitsize", String.valueOf(result.getNumHits()));
		// is the same now, but could be different for browsing over huge collections
		// not every hit will be build then
		mcr_results.setAttribute("resultlist-hitsize", String.valueOf(result.getNumHits()));
		int max = Math.min(until,result.getNumHits());
		for (int k = from; k < max; k++) {
			String hitID = result.getHit(k).getID();
		    org.jdom.Document hit = mcr_obj.receiveJDOMFromDatastore(hitID);
	        Element mycoreobject = hit.getRootElement();
	        String mcrID = mycoreobject.getAttributeValue("ID");
	        String docType = mcrID.substring(mcrID.indexOf("_")+1,mcrID.lastIndexOf("_"));
	        StringBuffer doclink = new StringBuffer(NavServlet.getNavigationBaseURL())
		            .append("nav?path=~docdetail&id=").append(hitID)
		            .append("&offset=").append(k).append("&doctype=").append(docType);
	        Element definition = (resultlistMap.containsKey(docType)) ?
	        		(Element)resultlistMap.get(docType) : addDocType2ResultlistMap(docType);
	        Element containerHit = processDocDetails(hit,definition,lang,doclink.toString(), docType);
	        Element mcr_result = new Element("mcr_result");
	        mcr_result.addContent(containerHit);
	        mcr_results.addContent(mcr_result);
		}
		return new Document(mcr_results);
	}    
  
    protected Element addDocType2ResultlistMap(String docType) {
    	String resultlistResource = new StringBuffer("resource:resultlist-").append(docType).append(".xml").toString();
    	Element resultlistElement = MCRURIResolver.instance().resolve(resultlistResource);
    	resultlistMap.put(docType,resultlistElement);
		return resultlistElement;
	}
    
    protected Element addDocType2DocdetailsMap(String docType) {
    	String docdetailsResource = new StringBuffer("resource:docdetails-").append(docType).append(".xml").toString();
    	Element docdetailsElement = MCRURIResolver.instance().resolve(docdetailsResource);
    	docdetailsMap.put(docType,docdetailsElement);
		return docdetailsElement;
	}    

    private String formateDate(String dateString, String datePattern, String iso639Lang){
    	SimpleDateFormat fmt = new SimpleDateFormat( datePattern, new Locale(iso639Lang));
        MCRMetaISO8601Date date = new MCRMetaISO8601Date();
    	date.setDate(dateString);
    	return fmt.format(date.getDate()) ;
    }
	public static void main(String[] args) {
    	MCRResultFormatter formatter = new MCRResultFormatter();
    	Document neu = formatter.getFormattedDocDetails((new MCRObject()).receiveJDOMFromDatastore("DocPortal_document_00410903"),"de");
    	if (neu == null) System.out.println("is null");
        System.out.println(JSPUtils.getPrettyString(neu));
        
        System.out.println(formatter.getSingleXPathValue(neu.getRootElement(),"/all-metavalues/metaname/@name"));
    }

}