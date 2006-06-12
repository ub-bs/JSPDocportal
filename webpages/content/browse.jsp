<%@ page import="org.jdom.Element,
				 org.jdom.Document,
				 org.mycore.frontend.servlets.MCRServlet,
				 org.mycore.common.MCRSession,
				 org.mycore.common.MCRConfiguration,				 
				 org.mycore.common.JSPUtils,
				 org.mycore.datamodel.classifications.MCRClassificationBrowserData,
				 org.mycore.common.MCRConfigurationException,
				 java.util.List,
				 java.util.Iterator"%>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>	 
<%
	String actUriPath = request.getParameter("actUriPath");
	String browserClass = request.getParameter("browserClass");
	String searchField = "";

	if (actUriPath == null) actUriPath = "/" + browserClass;
	String lang = (String) request.getAttribute("lang");
    MCRSession mcrSession = MCRServlet.getSession(request);
    try {
		searchField = MCRConfiguration.instance().getString("MCR.ClassificationBrowser." + browserClass + ".SearchField"); 
        mcrSession.BData = new MCRClassificationBrowserData(actUriPath,"","","");
    } catch (MCRConfigurationException cErr) {
    	request.setAttribute("message",cErr.getMessage());
   	    getServletContext().getRequestDispatcher("/mycore-error.jsp").forward(request,response);
    }

    Document doc = mcrSession.BData.createXmlTree(lang);
    
	String path = request.getParameter("path");
	String WebApplicationBaseURL = (String) getServletContext().getAttribute("WebApplicationBaseURL");
    Element browser = doc.getRootElement();
    Element navigationTree = browser.getChild("navigationtree");
    //org.jdom.output.XMLOutputter xmlout = new org.jdom.output.XMLOutputter(org.jdom.output.Format.getPrettyFormat());
    //System.out.print(xmlout.outputString(doc));
    String hrefStart = new StringBuffer(WebApplicationBaseURL)
    	.append("nav?path=").append(path)
    	.append("&actUriPath=").append(request.getParameter("startUriPath"))
    	.toString();    	
%>
	<fmt:setBundle basename='messages'/>
	<div class="headline"><fmt:message key="Webpage.browse.generalTitle" /></div>
    <table id="metaHeading" cellpadding="0" cellspacing="0">
        <tr>
            <td class="titles">
                <fmt:message key="Webpage.browse.numberOf" /> : <%= browser.getChildText("cntDocuments") %>
            </td>
            <td class="browseCtrl">
                <a href="<%= hrefStart %>"><%= browser.getChildText("description") %></a>
            </td>
        </tr>
	</table>
 	<!-- IE Fix for padding and border -->
 	<hr/>
	<table cellspacing="0" cellpadding="3" width="100%" >
	<tr><td valign="top">
 	  <table id="browseClass" cellspacing="0" cellpadding="3">
		<%
		for(Iterator it = navigationTree.getChildren("row").iterator(); it.hasNext();) {
        	Element row = (Element) it.next();
        	List cols = row.getChildren("col");
        	Element col1 = (Element) cols.get(0);
        	Element col2 = (Element) cols.get(1);   
        	
		  	String href1 = new StringBuffer(WebApplicationBaseURL)
		  		.append("nav?path=").append(path)
		  		.append("&actUriPath=").append(col2.getAttributeValue("searchbase"))
		  		.toString();
		  	String href2 = new StringBuffer(WebApplicationBaseURL)
	  		.append("content/browse-search.jsp?searchField=").append(searchField)
	  		.append("&searchValue=").append(col2.getAttributeValue("lineID"))
	  		.toString();
/**
		    <xsl:variable name="href2" select="concat($ServletsBaseURL, 'MCRSearchServlet?query=',$type,'+=+', col[2]/@lineID)" />
		  	String href2 = new StringBuffer(WebApplicationBaseURL)
		  		.append("servlets/MCRSearchServlet?query=").append(searchField)
		  		.append("+=+").append(col2.getAttributeValue("lineID"))
		  		.toString();
**/		  	
		  	String img1 = new StringBuffer(WebApplicationBaseURL)
		  		.append("images/").append(col1.getAttributeValue("folder1")).append(".gif")
		  		.toString();
		  	String img3 = new StringBuffer(WebApplicationBaseURL)
		  		.append("images/folder_blank.gif")
		  		.toString();
		  	
		  	int lineLevel = Integer.valueOf(col1.getAttributeValue("lineLevel")).intValue();
		  	int numDocs = Integer.valueOf(col2.getAttributeValue("numDocs")).intValue();
		  	
		  	String displayedNumber = JSPUtils.fillToConstantLength(String.valueOf(numDocs),"&#160;",6);
		  		
            String plusminusBase = col1.getAttributeValue("plusminusbase"); 				  				  		
        	%>
        	<tr>
        	   <td class="image">
        	         <%
        	         if (lineLevel > 0) {
        	         %>
        	            <img border="0" width="<%= lineLevel * 10 %>" src="<%= img3 %>" />
        	         <%
        	         }
        	         if ((plusminusBase != null) && !(plusminusBase.equals(""))) {
        	         %>
        	         	<a href="<%= href1 %>"><img class="borderless" src="<%= img1 %>" /></a>
        	         <%
        	         }else {
        	         %>
        	         	<img class="borderless" src="<%= img1 %>" />
        	         <%
        	         }
        	         %>
        	   </td>
        	   <td class="numDocs"> 
        	      [<%= displayedNumber %> <fmt:message key="Webpage.browse.doc" />]
        	   </td>
        	   <td class="descr">
        	      <% if(numDocs > 0) {   %>
        	            <a href="<%= href2 %>"><%= col2.getText() %></a>
        	      <% }else{    %>
        	         	<%= col2.getText() %>
        	      <% } %>
       	             
        	   </td>
        	      <% if ( (col2.getChildText("comment") != null) && !(col2.getChildText("comment").equals(""))) {   %>
        	      <td>
        	         <%= col2.getChildText("comment") %>
        	      <td>
       	          <% }    %>
        	</tr>
        	<% } %>        
	 </table>
	 </td></tr>	
 	</table>
 	
