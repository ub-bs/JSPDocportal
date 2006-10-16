package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;
import org.mycore.common.MCRCache;
import org.mycore.common.MCRSessionMgr;
import org.mycore.services.fieldquery.MCRResults;
import org.mycore.services.fieldquery.MCRSearchServlet;


public class MCRBrowseCtrlTag extends SimpleTagSupport
{
	private String id;
	private int offset;
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setOffset(int inputOffset) {
		offset = inputOffset;
		return;
	}	
	public void doTag() throws JspException, IOException {
		PageContext pageContext = (PageContext) getJspContext();
		MCRResults results = null;
		try {
			results = (MCRResults)((MCRCache) MCRSessionMgr.getCurrentSession().get(MCRSearchServlet.getResultsKey())).get(id);
		} catch ( Exception all) {
			results = null;
		}
		
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