package org.mycore.datamodel.metadata;

import org.jdom.Element;
import org.mycore.common.MCRCalendar;
import org.mycore.common.MCRException;

public class MCRMetaHistoryEvent extends MCRMetaHistoryDate {
	public static final int MCRHISTORYEVENT_MAX_EVENT = 1024;
	private String event;
	private MCRMetaClassification classification;
	
	public MCRMetaHistoryEvent() {
		// TODO Auto-generated constructor stub
		super();
		event = "";
		classification = new MCRMetaClassification();
		classification.setLang("de");
		classification.setSubTag("classification");
		
	}

	public MCRMetaHistoryEvent(String set_datapart, String set_subtag,
			String default_lang, String set_type, int set_inherted)
			throws MCRException {
		super(set_datapart, set_subtag, default_lang, set_type, set_inherted);
		event = "";
		classification = new MCRMetaClassification();
		classification.setLang("de");
		classification.setSubTag("classification");
		
	}
	
	 /**
     * The method set the text value.
     */
    public final void setEvent(String set) {
        if (set == null) {
            event = "";
            LOGGER.warn("The event field of MCRMeataHistoryEvent is empty.");
            return;
        }
        if (set.length() <= MCRHISTORYEVENT_MAX_EVENT) {
            event = set.trim();
        } else {
            event = set.substring(0, MCRHISTORYEVENT_MAX_EVENT);
        }
    }
    
    public final void setClassification(MCRMetaClassification set){
    	classification = set;
    }
    
    
    public final String getEvent(){
    	return event;
    }
    public final MCRMetaClassification getClassification() {
        return classification;
    }
    
    /**
     * This method reads the XML input stream part from a DOM part for the
     * metadata of the document.
     * 
     * @param element
     *            a relevant JDOM element for the metadata
     */
    public void setFromDOM(org.jdom.Element element) {
        if(element.getChild("von")==null){
        	element.addContent(new Element("von"));
        }
        if(element.getChild("bis")==null){
        	element.addContent(new Element("bis"));
        }	  
    	super.setFromDOM(element);
        setEvent(element.getChildTextTrim("event")); 
        setCalendar(element.getChildTextTrim("calendar"));
        Element eClassi = element.getChild("classification");
        if(eClassi!=null){
        	if(classification==null){
        		classification = new MCRMetaClassification();
        		classification.setLang("de");
        		classification.setSubTag("classification");
        	}
        	classification.setFromDOM(eClassi);
        }
        else{
        	classification=null;
        }
        
        
    }
    /**
     * This method creates a XML stream for all data in this class, defined by
     * the MyCoRe XML MCRMetaHistoryDate definition for the given subtag.
     * 
     * @exception MCRException
     *                if the content of this class is not valid
     * @return a JDOM Element with the XML MCRMetaHistoryDate part
     */
    public org.jdom.Element createXML() throws MCRException {
    	if (!isValid()) {
            debug();
            throw new MCRException("The content of MCRMetaHistoryEvent is not valid.");
        }

        org.jdom.Element elm = super.createXML();
        if ((event = event.trim()).length() != 0) {
            elm.addContent(new org.jdom.Element("event").addContent(event));
        }

//        if(classification!=null && !classification.isValid()){
//        	debug();
//        	throw new MCRException("The content of MCRMetaHistoryEvent's classification is not valid.");
//        
//        }
        if (classification != null && classification.isValid()) {
                elm.addContent(classification.createXML());
        }

        return elm;
    }
    
    /**
     * This method checks the validation of the content of this class. The
     * method returns <em>false</em> if
     * <ul>
     * <li>the text is null or
     * <li>the von is null or
     * <li>the bis is null
     * <li>the event is null
     * <li the classification is null
     * </ul>
     * otherwise the method returns <em>true</em>.
     * 
     * @return a boolean value
     */
    public boolean isValid() {
    	boolean b = super.isValid() && event!=null;
    	if(classification!=null){
    		b &=classification.isValid();
    	}
    	return b;
    }
    
    /**
     * This method make a clone of this class.
     */
    public Object clone() {
        MCRMetaHistoryEvent out = new MCRMetaHistoryEvent(datapart, subtag, lang, type, inherited);
        out.setText(getText("de").getText(), "de");
        out.setVonDate(getVon());
        out.setBisDate(getBis());
        out.setCalendar(getCalendar());
        out.setEvent(event);
        if(classification!=null){
        	out.setClassification((MCRMetaClassification)classification.clone());
        }
        else{
        	out.setClassification(null);
    	}
        return out;
    }
    /**
     * This method put debug data to the logger (for the debug mode).
     */
    public void debug() {
        LOGGER.debug("Start Class : MCRMetaHistoryEvent");
        super.debugDefault();
        LOGGER.debug("Text               = " + getText("de"));
        LOGGER.debug("Calendar           = " + getCalendar());
        if (getCalendar().equals(MCRCalendar.TAG_GREGORIAN)) {
            LOGGER.debug("Von (String)       = " + getVonToGregorianString());
        }
        LOGGER.debug("Von (JulianDay)    = " + getIvon());
        if (getCalendar().equals(MCRCalendar.TAG_GREGORIAN)) {
            LOGGER.debug("Bis (String)       = " + getBisToGregorianString());
        }
        LOGGER.debug("Bis (JulianDay)    = " + getIbis());
        LOGGER.debug("Event              = " + event);
        classification.debug();
        LOGGER.debug("Stop");
        LOGGER.debug("");
    }

}
