package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;
import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.DOMOutputter;
import org.mycore.common.JSPUtils;
import org.mycore.frontend.jsp.NavServlet;

public class MCRBrowsePageCtrlTag extends SimpleTagSupport
{
	private int size;
	private int totalSize;
	private int offset;
	private int maxDisplayedPages;
	private String var;
	private String path;
	
	public void setPath(String inputPath) {
		path = inputPath;
		return;
	}
	public void setVar(String inputVar) {
		var = inputVar;
		return;
	}
	public void setSize(int inputSize) {
		size = inputSize;
	}
	public void setMaxDisplayedPages(int inputMaxDisplayedPages) {
		maxDisplayedPages = inputMaxDisplayedPages;
	}	
	public void setTotalSize(int inputTotalSize) {
		totalSize = inputTotalSize;
	}	
	public void setOffset(int inputOffset) {
		offset = inputOffset;
	}

	public void doTag() throws JspException, IOException {
		PageContext pageContext = (PageContext) getJspContext();		
		Element mcr_resultpages = new Element("mcr_resultpages");
        int totalNumPages= (totalSize % size == 0)? totalSize / size : totalSize / size + 1;
        int currentPage = offset / size +1 ;
        String baseResultURL = new StringBuffer(NavServlet.getNavigationBaseURL()).append("nav?path=")
        	  .append(path).toString();
        int start = Math.max(1,currentPage - (maxDisplayedPages / 2));
        int end = Math.min(start + maxDisplayedPages,totalNumPages);
        if (start > 1) {
        	mcr_resultpages.setAttribute("cutted-left","true");
        }
        for (int i = start; i <= end ; i++) {
           	Element mcr_resultpage = new Element("mcr_resultpage");
        	StringBuffer linkSB = new StringBuffer(baseResultURL).append("&size=").append(size)
        		.append("&offset=").append((i - 1)*size);
        	mcr_resultpage.setAttribute("href", linkSB.toString());
        	mcr_resultpage.setAttribute("pageNr",String.valueOf(i));
        	if( i == currentPage) {
        		mcr_resultpage.setAttribute("current","true");
           }else {
        	   mcr_resultpage.setAttribute("current","false");
           }
        	mcr_resultpages.addContent(mcr_resultpage);
        }
        if (end < totalNumPages) {
        	mcr_resultpages.setAttribute("cutted-right","true");
        }
		org.jdom.Document browseDoc = new org.jdom.Document(mcr_resultpages);
		org.w3c.dom.Document domDoc = null;
		try {
			domDoc = new DOMOutputter().output(browseDoc);
		} catch (JDOMException e) {
			Logger.getLogger(MCRSetResultListTag.class).error("Domoutput failed: ", e);
		}
		pageContext.setAttribute(var, domDoc);
		if(pageContext.getAttribute("debug") != null && pageContext.getAttribute("debug").equals("true")) {
			JspWriter out = pageContext.getOut();
			StringBuffer debugSB = new StringBuffer("<textarea cols=\"80\" rows=\"30\">")
				.append("this is the jdom for the browse-control delivered by mcr:browsePageCtrl\r\n")
				.append(JSPUtils.getPrettyString(browseDoc))
				.append("</textarea>");
			out.println(debugSB.toString());
		}        
        return;
	}	

}