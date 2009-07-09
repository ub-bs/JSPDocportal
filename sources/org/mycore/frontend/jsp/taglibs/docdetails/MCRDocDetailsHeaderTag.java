package org.mycore.frontend.jsp.taglibs.docdetails;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class MCRDocDetailsHeaderTag extends SimpleTagSupport {
@Override
	public void doTag() throws JspException, IOException {
		JspWriter out = getJspContext().getOut();
		MCRDocDetailsTag docdetails = (MCRDocDetailsTag) findAncestorWithClass(this,	MCRDocDetailsTag.class);
		if (docdetails == null) {
			throw new JspException(
					"This tag must be nested in tag called 'docdetails' of the same tag library");
		}
		out.write("<tr><th class=\""+docdetails.getStylePrimaryName()+"-header\" colspan=\"4\">\n");
		getJspBody().invoke(out);
		out.write("</th></tr>");
	}
}
