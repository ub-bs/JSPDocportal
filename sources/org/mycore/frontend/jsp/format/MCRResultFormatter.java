/*
 * Created on 12.04.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.mycore.frontend.jsp.format;


import java.io.InputStream;
import java.text.SimpleDateFormat;
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
import org.jdom.xpath.XPath;
import org.mycore.common.JSPUtils;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRException;
import org.mycore.common.MCRUsageException;
import org.mycore.common.xml.MCRURIResolver;
import org.mycore.datamodel.classifications2.MCRCategory;
import org.mycore.datamodel.classifications2.MCRCategoryDAO;
import org.mycore.datamodel.classifications2.MCRCategoryDAOFactory;
import org.mycore.datamodel.classifications2.MCRCategoryID;
import org.mycore.datamodel.ifs.MCRDirectory;
import org.mycore.datamodel.ifs.MCRFile;
import org.mycore.datamodel.ifs.MCRFilesystemNode;
import org.mycore.datamodel.metadata.MCRDerivate;
import org.mycore.datamodel.metadata.MCRMetaISO8601Date;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.jsp.NavServlet;
import org.mycore.services.fieldquery.MCRFieldDef;

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
	protected static String URN_RESOLVER_URL;

	protected static Map<String, Element> resultlistMap;
	protected static Map<String, Element> docdetailsMap;
	protected static MCRCategoryDAO categoryDAO;
	protected static org.jdom.Namespace xlinkNamespace;
	
    static {
    	categoryDAO = MCRCategoryDAOFactory.getInstance();
        logger=Logger.getLogger(MCRResultFormatter.class);
        WebApplicationBaseURL = NavServlet.getNavigationBaseURL();
        String XLINK_URL = "http://www.w3.org/1999/xlink";
        xlinkNamespace = org.jdom.Namespace.getNamespace("xlink", XLINK_URL);
        URN_RESOLVER_URL = CONFIG.getString("MCR.URN_RESOLVER.URL", "http://nbn-resolving.de/");
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
    		resultlistMap = new HashMap<String, Element>();
    	}
    	if (docdetailsMap == null) {
    		docdetailsMap = new HashMap<String, Element>();
    	}    	
    }

	/**
	 * evaluates whether a query-result was sorted by a given field 
	 * or a given order. an example is given below. (needed for sort form)
	 *
	 * 
	 * @param sortBy jdom-Elemen &lt;sortBy&gt;
	 * @param sortprio order-priority of a sortfield 
	 * @param attributeName attributename of the field-element
	 * @param attributeValue attribute-value of the given element
	 * 
	 * @return boolean
	 * <br>&nbsp;<br>
	 * <b>Example</b><br>
	 * sortBy=<br>
	 * &lt;sortBy&gt;<br>
	 *    &lt;field field="title" order="ascending" /&gt;<br>
	 *    &lt;field field="author" order="decending" /&gt;<br>
	 * &lt;/sortBy&gt;<br>
	 * <br>
	 * isSorted(sortBy,1,"order","ascending") = *true*<br>
	 * isSorted(sortBy,2,"field","author") = *true*<br>
	 * isSorted(sortBy,2,"order","ascending") = *false*<br> 
	 */
	public boolean isSorted(org.jdom.Element sortBy, int sortprio, String attributeName, String attributeValue) {
	    org.jdom.Element sortField = (Element) sortBy.getChildren("field").get(sortprio - 1);
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
    		return categoryDAO.getCategory(new MCRCategoryID(classifID, categID), 0).getLabels().get(lang).getText();    		
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
				try {
					dateString = formateDate(dateString, datePattern, lang);
				} catch (Exception ignored) {
					//take it as it is
				}
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
				
				int max = CONFIG.getInt("MCR.Searchresults.description.maxlength", 250);
				if ( terminator.contains("...") & text.length() > max){
					text = text.substring(0,max) + terminator;
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
			    String derivateID = ((Element) it.next()).getAttributeValue("href",xlinkNamespace);
			    try {
			    	mcr_der.receiveFromDatastore(derivateID);
			    } catch (Exception noDerivate) {
			    	logger.error("There ist no Derivate with ID:" + derivateID);
			    	continue;
			    }
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
			    		digitalObject.setAttribute("derivatelabel", mcr_der.getLabel());
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
    	//MCRObject mcr_obj = new MCRObject();
    	try {
			for(Iterator it = XPath.selectNodes(doc,xpath).iterator(); it.hasNext(); ) {
			    Element personlink = (Element) it.next();
			    //MCRObjectID person_id = new MCRObjectID(personlink.getAttributeValue("href",xlinkNamespace));
			    try{
	        		Element metaValue = new Element("metavalue");
	        		String href = WebApplicationBaseURL + "metadata/" + personlink.getAttributeValue("href",xlinkNamespace);
					metaValue.setAttribute("href",href);
					metaValue.setAttribute("text", personlink.getAttributeValue("title", xlinkNamespace));			        
					metaValues.addContent(metaValue);
					
					/**
	        		mcr_obj.receiveFromDatastore(person_id); 
					Element creator_root = mcr_obj.createXML().getRootElement();			        
			        String creatorName = (String) XPath.selectSingleNode(creator_root,"string(./metadata/names/name/fullname)");
	        		Element metaValue = new Element("metavalue");
					metaValue.setAttribute("href","");
					metaValue.setAttribute("text",creatorName);			        
					metaValues.addContent(metaValue);
					**/
					
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
				//MCRCategoryItem categItem = MCRClassificationManager.instance().retrieveCategoryItem(classID,categID);
				Element metaValue = new Element("metavalue");
				String text = categoryDAO.getCategory(new MCRCategoryID(classID, categID), 0).getLabels().get(lang).getText();
				metaValue.setAttribute("text", text);
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
    
    public Element getLinkedCategoryTexts(Document doc, String xpath, String separator, String terminator, String lang, String escapeXml, boolean noLink) {
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
				//	MCRCategoryItem categItem = MCRClassificationManager.instance().retrieveCategoryItem(classID,categID);
					MCRCategory categ = categoryDAO.getCategory(new MCRCategoryID(classID, categID), 0);
					Element metaValue = new Element("metavalue");
					if (categ.getURI()!=null && categ.getURI().toString().length() >0 ){
						metaValue.setAttribute("href",categ.getURI().toString());
						metaValue.setAttribute("target","new");
					} else {
						//http://localhost:8080/atlibri/servlets/MCRJSPSearchServlet?query=ddc+=+9&mask=~searchstart-classddc
						try {
							String searchfield = MCRConfiguration.instance().getString("MCR.Class.SearchField."+ classID);
							if ( searchfield != null ){
								String href = WebApplicationBaseURL + "servlets/MCRJSPSearchServlet?query="
									+ searchfield + "+=+" + categID + "&mask=~searchstart-simple";
								metaValue.setAttribute("href",href);
								metaValue.setAttribute("target","");
							}
						}catch (Exception all) {
							//MCR.Class.SearchField.<classID> is not set
							logger.debug("Property MCR.Class.SearchField." + classID + " is not set!!");
						}
					}
					if(noLink){
						metaValue.removeAttribute("href");
					}
					
					metaValue.setAttribute("text",categ.getLabels().get(lang).getText());
					metaValue.setAttribute("classid",classID);
					metaValue.setAttribute("categid",categID);
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
    			    String derivateID = ((Element) it.next()).getAttributeValue("href", xlinkNamespace);
    			    try {
    			    	mcr_der.receiveFromDatastore(derivateID);
	    			} catch (Exception noDerivate) {
				    	logger.error("There ist no Derivate with ID:" + derivateID);
				    	continue;
				    }
    			    String derivlabel = mcr_der.getLabel();
    			    String derivmain = mcr_der.getDerivate().getInternals().getMainDoc();
    			      			    
    			    MCRDirectory root;
    			    root = MCRDirectory.getRootDirectory(derivateID);
    			    MCRFilesystemNode[] myfiles = root.getChildren(MCRDirectory.SORT_BY_NAME);//getChildren();
    			    Element digitalObject = new Element("digitalobject");
    			    
    			    int fileCnt = 0;
    			    
    			    if (all) {  //all data objects 
    			    	fileCnt = myfiles.length;
    			    } else { 	//only the maindoc
    			    	fileCnt = 1; 
    			    	myfiles[0] = (MCRFile) root.getChildByPath(derivmain);
    			    	if (myfiles[0] == null) {
    				    	logger.error("SEEMS TO EXIST A DERIVATE WITHOUT A MAIN DOCUMENT. REVIEW DERIVATE WITH ID: " + derivateID);
    				    	//maindoc not set -> use the first file
    				    	if(root.getChildren().length>0)	myfiles[0] = (MCRFile) root.getChild(0);
    				    	if(myfiles[0]==null){
    				    		logger.error("SEEMS TO EXIST A DERIVATE WITHOUT A DIGITAL OBJECT. REVIEW DERIVATE WITH ID: " + derivateID);
    				    	   	continue;
    				    	}
    				    }
    			    }
    				for ( int i=0; i< fileCnt; i++) {
    					if(myfiles[i] instanceof MCRFile){
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
			    
						//metaValue.setAttribute("href","mailto:" + emailaddress);
						metaValue.setAttribute("href","");
						metaValue.setAttribute("type",el.getName());
						metaValue.setAttribute("text",decodeEMailAdress(emailaddress));
						metaValues.addContent(metaValue);			    	
			    }
			}
		} catch (JDOMException e) {
			logger.debug("error occured", e);
			return metaValues;
		} 
    	return metaValues;
    }
    
    //Codiere Email durch umgekehrte Schreibweise -> Aufl�sung durch CSS  
    //<div style="direction: rtl; unicode-bidi: bidi-override;">ed.liam@ofni</div> -> info@mail.de 
    
    private String decodeEMailAdress(String mail){
    	String address = mail.replace("@", ")at(");             //&#64; = '@'
    	StringBuffer sb = new StringBuffer();
    	sb.append("<div style=\"direction: rtl; unicode-bidi: bidi-override;\">");
    	for(int i=address.length()-1;i>=0;i--){
    		sb.append(address.charAt(i));
    	}
    	sb.append("</div>");      
    	return sb.toString();    	
    }

    public Element getUrnValues(Document doc, String xpath, String separator, String terminator, String lang, String introkey, String escapeXml) {
    	Element metaValues = new Element("metavalues");
    	metaValues.setAttribute("type","LinkValues");
    	metaValues.setAttribute("separator",separator);   
    	metaValues.setAttribute("terminator",terminator);    	
    	metaValues.setAttribute("introkey", introkey);
    	metaValues.setAttribute("escapeXml", escapeXml);    	
    	try {
    		boolean breakLoop = false;
			for(Iterator it = XPath.selectNodes(doc,xpath).iterator(); it.hasNext(); ) {
			    Element el = (Element) it.next();
			    String type = el.getAttributeValue("type");
			    if(type == null || type.equals(""))
			    	continue;
			    String urn = "";
			    if("urn_new_version,urn_new,url_update_general".indexOf(type) != -1 ){
			    	urn = el.getText();
			    	breakLoop = true;
			    }else if("urn_new_version,urn_new, url_update_general, urn_first, urn_last".indexOf(type) != -1){
			    	urn = el.getText();
			    }
			    if ( urn != null && !urn.equals("")) {
						Element metaValue = new Element("metavalue");
						String href = URN_RESOLVER_URL+  urn;
						metaValue.setAttribute("href", href);
						metaValue.setAttribute("type",el.getName());
						metaValue.setAttribute("text",urn);
						metaValues.addContent(metaValue);			    	
			    }
			    if(breakLoop)
			    	break;
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
						String to = el.getAttributeValue("to");
						if( to==null) to ="";
						metaValue.setAttribute("type",to);
						metaValue.setAttribute("text",title);
						metaValue.setAttribute("target","new");						
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
    		String name = getSingleXPathValue(docRoot,xpathArray[1]);
    		Element metaValue = new Element("metavalue");
			metaValue.setAttribute("querytext","query=creator+=+" + name + "+OR+id+=+"+mcrid);
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
						String sStreet = (address.getChildText("street")!= null)?address.getChildText("street"):"";
						String sNumber = (address.getChildText("number")!= null)?address.getChildText("number"):"";
						StringBuffer sb = new StringBuffer(sStreet).append(" ").append(sNumber);
						street.setAttribute("text",sb.toString());
					    metaValues.addContent(street);
					    Element city = new Element("metavalue");
					    city.setAttribute("href","");
						String szipcode = (address.getChildText("zipcode")!= null)?address.getChildText("zipcode"):"";
						String scity = (address.getChildText("city")!= null)?address.getChildText("city"):"";
						sb = new StringBuffer(szipcode).append(" ").append(scity);
					    city.setAttribute("text",sb.toString());
					    metaValues.addContent(city);
					    Element country = new Element("metavalue");
					    country.setAttribute("href","");
						String sstate = (address.getChildText("state")!= null)?address.getChildText("state"):"";
						String scountry = (address.getChildText("country")!= null)?address.getChildText("country"):"";
					    sb = new StringBuffer(sstate).append(", ").append(scountry);
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
			    String childID = ((Element) it.next()).getAttributeValue("href",xlinkNamespace);
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
    
    public static Element getParentFromObject(Document doc, String xpath, String separator, String terminator, String lang, String introkey, String escapeXml) {
    	Element parentObjects = new Element("parents");
    	parentObjects.setAttribute("type","parent");
    	parentObjects.setAttribute("separator",separator);
    	parentObjects.setAttribute("terminator",terminator);    	
    	parentObjects.setAttribute("introkey", introkey);
    	parentObjects.setAttribute("escapeXml", escapeXml);    	
    	//MCRObject mcr_obj = new MCRObject();
    	try {
			for(Iterator it = XPath.selectNodes(doc,xpath).iterator(); it.hasNext(); ) {
			    String parentID = ((Element) it.next()).getAttributeValue("href",xlinkNamespace);
			    //mcr_obj.receiveFromDatastore(childID);
			    Element parentObject = new Element("parent");			    		
			    parentObject.setAttribute("parentID",parentID);
			    parentObjects.addContent(parentObject);
			}
    	} catch (Exception e) {
    		logger.debug("error occured", e);
    		return parentObjects;
    	} 
		return parentObjects;
    }    

    public Element getFormattedMCRDocDetailContent(org.jdom.Document doc, String xpath, 
    		String separator, String terminator, String lang, 
    		String templatetype, String introkey, String escapeXml, String start) {
    	

    	if (templatetype.equals("tpl-author_links"))
    		return getConcatenatedPersons(doc,xpath,separator,terminator,introkey,escapeXml);
    	if (templatetype.equals("tpl-text-messagekey")) {
    		Element metaValues = getXPathValues(doc,xpath,separator,terminator,lang,introkey,escapeXml,start);
        	metaValues.setAttribute("type","messagekey");
    		return metaValues;
    	}
    	if (templatetype.equals("tpl-text-values")){
    	    Element result = getXPathValues(doc,xpath,separator,terminator,lang,introkey,escapeXml,start);
            if(result.getContentSize()==0){
                result = getXPathValues(doc,xpath,separator,terminator,"",introkey,escapeXml,start); 
            }
            return result;
        }
    	
    	if (templatetype.startsWith("tpl-classification"))
    		return getLinkedCategoryTexts(doc,xpath,separator,terminator,lang,escapeXml, templatetype.length()>"tpl-classification".length());
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
    	
    	if (templatetype.equals("tpl-urn"))
    		return getUrnValues(doc,xpath,separator,terminator,lang,introkey,escapeXml);
    	
    	if (templatetype.equals("tpl-image"))
    		return getImagesFromObject(doc,xpath,separator,terminator,lang,introkey,escapeXml);    	
    	if (templatetype.equals("tpl-child"))
    		return getChildsFromObject(doc,xpath,separator,terminator,lang,introkey,escapeXml);    	
    	if (templatetype.equals("tpl-parent"))
    		return getParentFromObject(doc,xpath,separator,terminator,lang,introkey,escapeXml);    	

    	
    	return null;
    }
    
    
    public Document getFormattedDocDetails(Document doc, String lang, String style) {
    	String docType = style;
    	if (! style.equalsIgnoreCase("user")) {
        	MCRObjectID mcrid = new MCRObjectID(doc.getRootElement().getAttributeValue("ID"));
    		docType = mcrid.getTypeId();    		
    	}
		String resultlistStyle = (style == null || style.length() == 0 )?docType:style;
        Element definition = (docdetailsMap.containsKey(resultlistStyle)) ?
        		docdetailsMap.get(resultlistStyle) : addDocType2DocdetailsMap(resultlistStyle);		
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
            if(field.getAttributeValue("rowtype").equals("space")){
            	metaname.setAttribute("type","space");
            	allMetaValuesRoot.addContent(metaname);
            }else if(field.getAttributeValue("rowtype").equals("line")){
                  metaname.setAttribute("type","line");
                  allMetaValuesRoot.addContent(metaname);
            }else{
         //   if (field.getAttributeValue("rowtype").equals("standard")
         //       || field.getAttributeValue("rowtype").equals("hidden")
         //       || field.getAttributeValue("rowtype").equals("image")
	     //       || field.getAttributeValue("rowtype").equals("children") 
	     //       || field.getAttributeValue("rowtype").equals("table") ) {
            	
            			metaname.setAttribute("name", field.getAttributeValue("labelkey"));
            			metaname.setAttribute("type",field.getAttributeValue("rowtype"));
            			if(field.getAttributeValue("style")!=null){
            				metaname.setAttribute("style",field.getAttributeValue("style"));
            			}
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
                            if (contentSeparator == null) contentSeparator = "";
                            if (contentTerminator == null) contentTerminator = "";                        
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
            }
            
        }
        return allMetaValuesRoot ;    	
    }
    
    public Document getFormattedResultContainer(Element results, String lang, int from, int until) {
		MCRObject mcr_obj = new MCRObject();
		Element mcr_results = new Element("mcr_results");
		String numHits = results.getAttributeValue("numHits");
		mcr_results.setAttribute("id", results.getAttributeValue("id"));
		mcr_results.setAttribute("mask", results.getAttributeValue("mask"));
		mcr_results.setAttribute("total-hitsize", numHits);
		// is the same now, but could be different for browsing over huge collections
		// not every hit will be build then
		mcr_results.setAttribute("resultlist-hitsize", numHits);
		// int max = Math.min(until,Integer.parseInt(numHits));
		// the resultset contains only the hits from page x from 0 to numPerPage - cutting is taken in MCRJSPSearchservlet
		List hits = results.getChildren("hit",MCRFieldDef.mcrns);		
		//needed for calculation of the proper offset
		int firstItemNumber;
		try{
			firstItemNumber = (Integer.parseInt(results.getAttributeValue("page"))-1)*Integer.parseInt(results.getAttributeValue("numPerPage"));
		}catch(NumberFormatException nfe){
			firstItemNumber=0;
		}
		
		
		for (int k = from; k < until && k < hits.size() ; k++) {
			String hitID = ((Element)(hits.get(k))).getAttributeValue("id");		
			org.jdom.Document hit=null;
			try{
		    	hit = mcr_obj.receiveJDOMFromDatastore(hitID);
		    }
		    catch(Exception e){
		    	logger.error("Object with ID "+hitID+" does not exist in data store!!!");
		    	continue;
		    }
	        Element mycoreobject = hit.getRootElement();
	        String mcrID = mycoreobject.getAttributeValue("ID");
	        String docType = mcrID.substring(mcrID.indexOf("_")+1,mcrID.lastIndexOf("_"));
	        StringBuffer doclink = new StringBuffer(WebApplicationBaseURL)
		            //.append("nav?path=~docdetail&id=").append(hitID)
		            .append("nav?id=").append(hitID)
		            .append("&offset=").append(firstItemNumber+k).append("&doctype=").append(docType);
	        Element definition = (resultlistMap.containsKey(docType)) ?
	        		resultlistMap.get(docType) : addDocType2ResultlistMap(docType);
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

    public static String formateDate(String dateString, String datePattern, String iso639Lang){
    	String ret = dateString;
    	try {
    		SimpleDateFormat fmt = new SimpleDateFormat( datePattern, new Locale(iso639Lang));
            MCRMetaISO8601Date date = new MCRMetaISO8601Date();
        	date.setDate(dateString);
        	ret =  fmt.format(date.getDate()) ;
    	} catch( Exception uparsableDate) {
    		;
    	}
    	return ret;
    }
	public static void main(String[] args) {
    	MCRResultFormatter formatter = new MCRResultFormatter();
    	Document neu = formatter.getFormattedDocDetails((new MCRObject()).receiveJDOMFromDatastore("DocPortal_document_00410903"),"de", "");
    	if (neu == null) System.out.println("is null");
        System.out.println(JSPUtils.getPrettyString(neu));
        
        System.out.println(formatter.getSingleXPathValue(neu.getRootElement(),"/all-metavalues/metaname/@name"));
    }

}