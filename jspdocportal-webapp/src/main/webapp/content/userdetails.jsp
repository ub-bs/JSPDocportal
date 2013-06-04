<%@page import="org.mycore.user2.MCRRole"%>
<%@page import="org.mycore.user2.MCRUserResolver"%>
<%@page import="org.mycore.user2.MCRUser"%>
<%@page import="org.mycore.user2.MCRUserCommands"%>
<%@page import="org.mycore.user2.MCRRoleManager"%>
<%@page import="org.mycore.user2.MCRRoleResolver"%>
<%@page import="org.mycore.user2.MCRUserManager"%>
<%@page import="org.mycore.common.MCRUserInformation"%>
<%@page import="org.mycore.common.MCRSessionMgr"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"   %>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>

<fmt:message var="pageTitle" key="Nav.Archive" /> 
<stripes:layout-render name="../WEB-INF/layout/default.jsp" pageTitle = "${pageTitle}">
	<stripes:layout-component name="contents">

<h2>Angemeldeter Nutzer</h2>
<div class="text">
<p>Zur Zeit ist der folgende Nutzer angemeldet: </p>
	<table>
<% 
  MCRUserInformation userInfo = MCRSessionMgr.getCurrentSession().getUserInformation();
  out.print("<tr><th>ID: </th><td><strong>"+userInfo.getUserID()+"</strong></td></tr>");
  out.print("<tr><td></td><td></td></tr>");
  out.print("<tr><th>Gruppenmitgliedschaften: </th><td>");
  for(MCRRole r: MCRRoleManager.listSystemRoles()){
    if(userInfo.isUserInRole(r.getName())){
    	out.print(r.getName()+"<br />");
    }
  }
  out.print("</td></tr>");
  
%>

	</table>
</div>


	</stripes:layout-component>
</stripes:layout-render>



