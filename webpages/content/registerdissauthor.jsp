<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn" %>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>

<%@ page import="org.mycore.common.MCRConfiguration"%>
<%@ page import="org.mycore.common.MCRSession"%>
<%@ page import="org.mycore.frontend.servlets.MCRServlet"%>
<%@ page import="org.mycore.datamodel.metadata.MCRObject"%>
<%@ page import="org.mycore.frontend.workflow.MCRDisshabWorkflowManager"%>
<%@ page import="org.jdom.Document"%>

<mcr:session method="get" var="username" type="userID" />
<c:set var="WebApplicationBaseURL" value="${applicationScope.WebApplicationBaseURL}" />
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages'/>
<div class="headline"><fmt:message key="Nav.Application.mydiss" /> - <fmt:message key="Nav.Application.mydiss.begin" /></div>

<table  class="bg_background" >
 <tr><td><div class="subtitle" >Hiermit möchte ich meine Dissertation anmelden.</div> </td> </tr>
 <tr><td> 	
    <br/>
<%
	String guest = MCRConfiguration.instance().getString("MCR.users_guestuser_username");
	MCRSession mcrSession = MCRServlet.getSession(request);
    String sessionID = mcrSession.getID();
    StringBuffer sbURL = new StringBuffer(	MCRServlet.getBaseURL()).append("start_edit");

 	if( mcrSession.getCurrentUserID().equals(guest) ) {
%> 	
	<p class="error"><fmt:message key="Login.GuestAccount" /></p>
	<br/>
	<br/>	
<%  } else {	
		MCRDisshabWorkflowManager dhwf = MCRDisshabWorkflowManager.instance();
		String authorID = "";
		String userid = mcrSession.getCurrentUserID();
		try {
	    	authorID = dhwf.getDisshabAuthor(userid);
	    } catch (Exception ex ) {
			request.setAttribute("message", ex.getMessage());
   	    	getServletContext().getRequestDispatcher("/mycore-error.jsp").forward(request,response);	    	    
	    }
	    if ( authorID.length() > 0 ) {
	    	// hat schon Autorendaten!
%>    	
			<p class="error"><fmt:message key="SWF.Dissertation.AuthorExist" /></p>
			<br/>
			<br/>
<% 	
    	} else {
    		authorID = dhwf.createAuthorforDisshab(userid);
    		 
%>   	    
   	    <p> Es wurden Autorendaten aus Ihren Nutzerdaten angelegt. Wenn sie diese noch anpassen oder ändern 
   	        wollen, können sie das über den Menüpunkt  'meine Autoren' </p>
<% 		}
%>   	 
 
	    <c:set var="mcrid" value="${authorID}" />
   	    <table class="editor" >
   	    <tr><td>ID: <c:out value="${mcrid}" /></td> </tr>
        <tr><td>   	        
			<c:import url="content/docdetails.jsp" >
    	   		<c:param name="mcrid" value="${mcrid}" />
     		</c:import>
     	</td></tr>
     	</table>	
     	<p>&#160;</p>
   	    <p> Ihre Dissertation wird mit Ihren Autorendaten angelegt. </p>    
<%
	}
%>    
	<hr/>
	<p><fmt:message key="Nav.Service.Text1" /></p>
	<p><a href="mailto:atlibri@uni-rostock.de">atlibri@uni-rostock.de</a></p>  	
  </td>
</tr>
</table>
