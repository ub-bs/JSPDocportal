package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;

import org.apache.log4j.Logger;
import org.jdom.JDOMException;
import org.jdom.output.DOMOutputter;
import org.mycore.frontend.jsp.format.MCRResultFormatter;
import org.mycore.services.fieldquery.MCRResults;


public class MCRBrowseCtrlTag extends SimpleTagSupport
{
	private MCRResults results;
	private int offset;
	
	public void setResults(MCRResults inputResults) {
		results = inputResults;
		return;
	}
	public void setOffset(int inputOffset) {
		offset = inputOffset;
		return;
	}	
	public void doTag() throws JspException, IOException {
		PageContext pageContext = (PageContext) getJspContext();
        if ( results != null) {
        	int numHits = results.getNumHits();
        	if (offset > 0) {
        		pageContext.setAttribute("lastHitID", results.getHit(offset - 1).getID());
        	}
        	if (offset < numHits - 1) {
        		pageContext.setAttribute("nextHitID", results.getHit(offset + 1).getID());
        	}
            JspFragment body = getJspBody();
            JspWriter out = pageContext.getOut();
            try {
                StringWriter stringWriter = new StringWriter();
                body.invoke(stringWriter);
                out.println(stringWriter);
                
            } catch (Exception e) {
            	Logger.getLogger(MCRBrowseCtrlTag.class).error("catched error: ", e);
            }        	
        }
		return;
	}	

}