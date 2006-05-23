<%@ page import="org.jdom.Element,
				 org.jdom.Document,
				 org.mycore.frontend.servlets.MCRServlet,
				 org.mycore.common.MCRSession,
				 org.mycore.common.JSPUtils,
				 java.util.List,
				 java.util.Iterator"%>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>	 
<%
	String WebApplicationBaseURL = (String) getServletContext().getAttribute("WebApplicationBaseURL");
	
	String actUriPath = request.getParameter("actUriPath");
	String browserClass = request.getParameter("browserClass");
    
    /** ========== Subselect Parameter ========== **/
    /**
     Enumeration ee = request.getParameterNames();
     while ( ee.hasMoreElements() ) {
         String param = (String) ee.nextElement();
    	 System.out.println("PARAM: " + param + " VALUE: "  + 	request.getParameter(param) );
     }
    **/
    
    String subselectSession = request.getParameter("XSL.subselect.session");
    String subselectVarpath = request.getParameter("XSL.subselect.varpath");
    String subselectWebpage = request.getParameter("XSL.subselect.webpage");
    
    String url = WebApplicationBaseURL
    	       + "servlets/XMLEditor?_action=end.subselect"
    	       + "&amp;subselect.session="+subselectSession
  		 	   + "&amp;subselect.varpath="+subselectVarpath
    		   + "&amp;subselect.webpage="+subselectWebpage;

   String subselectParams  =  "XSL.subselect.session="+subselectSession
	        		 		+ "&amp;XSL.subselect.varpath="+subselectVarpath
    		     			+ "&amp;XSL.subselect.webpage="+subselectWebpage;
    		   
    String formAction=WebApplicationBaseURL + subselectWebpage+"?XSL.editor.session.id="+subselectSession;        		         		
	
	System.out.println("formAction: " + formAction);
	System.out.println("url: " + url);
	
    /** ========== Subselect Parameter ENDE ========== **/
	
	if (actUriPath == null) actUriPath = "/" + browserClass;
	String lang = (String) request.getAttribute("lang");
    MCRSession mcrSession = MCRServlet.getSession(request);
    Document doc = mcrSession.BData.createXmlTree(lang);   
	String path = request.getParameter("path");
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
	<div class="headline"><fmt:message key="Browse.generalTitle" /></div>
    <table id="metaHeading" cellpadding="0" cellspacing="0">
        <tr>
         <td style="width:60%;" class="desc">
		  <form action="<%= formAction %>" method="post">
		   <input type="submit" class="submit" value="Auswahl abbrechen" />
		   <br/>
		 </form>
		 </td>
		</tr>
		<tr>
          <td class="titles">
                <fmt:message key="Browse.numberOf" /> : <%= browser.getChildText("cntDocuments") %>
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
		  		.append("&").append(subselectParams)
		  		.toString();
		  		
   	   		String subSelectItem = new StringBuffer(url)
		  		.append("&amp;_var_@categid=").append(col2.getAttributeValue("lineID"))
		  		.append("&amp;_var_@title=").append(col2.getText())
		  		.toString();

		  	String img1 = new StringBuffer(WebApplicationBaseURL)
		  		.append("images/").append(col1.getAttributeValue("folder1")).append(".gif")
		  		.toString();
		  	/***
		  	String img2 = new StringBuffer(WebApplicationBaseURL)
		  		.append("images/").append(col1.getAttributeValue("folder2")).append(".gif")
		  		.toString();
		  	***/
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
        	      [<%= displayedNumber %> <fmt:message key="Browse.doc" />]
        	   </td>
        	   <td class="descr">
    	          <a href="<%= subSelectItem %>"><%= col2.getText() %></a>
        	   </td>
        	      <% if ( (col2.getChildText("comment") != null) && !(col2.getChildText("comment").equals(""))) {   %>
        	      <td>
        	         <%= col2.getChildText("comment") %>
        	      <td>
       	          <% }    %>
        	</tr>
        	<% } %>        
	 </table>	
	 </td>
	 </tr>	
</table>
 	