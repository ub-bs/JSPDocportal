<%@ page import="org.mycore.frontend.jsp.navigation.NavNode,
                 java.util.Iterator,
                 org.mycore.frontend.jsp.navigation.NavEntry"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<% 
  { NavNode n2 = (NavNode)request.getAttribute("node");
    String WebApplicationBaseURL = (String)request.getAttribute("WebApplicationBaseURL");
    Iterator i = n2.iterator();
    while(i.hasNext()) {
	   NavNode n = (NavNode)i.next();
       NavEntry e = n.getValue();
       String eLink = (e.isExtern())? e.getLink() : (WebApplicationBaseURL + e.getLink()) ;
       String lang = (String) request.getAttribute("lang");
    %>
    <fmt:setLocale value="<%= lang %>" />
    <fmt:setBundle basename='messages'/>
    <img title="" alt="" src="images/greenArrow.gif">
    <a target="_self" href="<%= eLink %>"><fmt:message key="<%=e.getDescription()%>" /></a>
    <br/>
    <%
    }
  }
%>