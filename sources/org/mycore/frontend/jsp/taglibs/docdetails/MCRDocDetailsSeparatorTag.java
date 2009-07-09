package org.mycore.frontend.jsp.taglibs.docdetails;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class MCRDocDetailsSeparatorTag extends SimpleTagSupport {
	private boolean showLine;

	public void setShowLine(boolean showLine) {
		this.showLine = showLine;
	}

	public void doTag() throws JspException, IOException{
		MCRDocDetailsTag docdetails = (MCRDocDetailsTag) findAncestorWithClass(this, MCRDocDetailsTag.class);
		if(docdetails==null){
			throw new JspException("This tag must be nested in tag called 'docdetails' of the same tag library");
		}
		if(docdetails.getPreviousOutput()>0){
			JspWriter out = getJspContext().getOut();
			out.print("<tr><td colspan=\"3\" class=\""+docdetails.getStylePrimaryName()+"-separator\">");
			if(showLine){
				out.print("<hr />");
			}
			else{
				out.print("&nbsp;");
			}
			out.println("</td></tr>");
		}
		docdetails.setPreviousOutput(0);		
	}
}
