package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.mycore.common.MCRConfiguration;
import org.mycore.frontend.jsp.format.MCRResultFormatter;


/**
 * checks whether a query-result was sorted by a given field 
 * or a given order. an example is given below. (needed for sort form)
 *
 */
public class MCRIfSortedTag extends SimpleTagSupport
{
	private Document query;
	private int sortorder;
	private String attributeName;
	private String attributeValue;
	
	public void setQuery(Document inputQuery) {
		query = inputQuery;
		return;
	}
	public void setSortorder(int inputSortorder) {
		sortorder = inputSortorder;
		return;
	}
	public void setAttributeName(String inputAttributeName) {
		attributeName = inputAttributeName;
	}
	public void setAttributeValue(String inputAttributeValue) {
		attributeValue = inputAttributeValue;
	}	

	public void doTag() throws JspException, IOException {
		PageContext pageContext = (PageContext) getJspContext();
        JspWriter out = pageContext.getOut();
       
		int sortprio = (sortorder == 0) ? 1: sortorder;
	    Element sortField = (Element) query.getRootElement().getChild("sortby").getChildren("field").get(sortprio - 1);
		if (sortField != null) {
			if (sortField.getAttributeValue(attributeName) != null &&
				sortField.getAttributeValue(attributeName).equals(attributeValue) ) {
					JspFragment body = getJspBody();
					StringWriter stringWriter = new StringWriter();
					body.invoke(stringWriter);
					out.println(stringWriter);
			}
		} 
		return;		
	}	

}
