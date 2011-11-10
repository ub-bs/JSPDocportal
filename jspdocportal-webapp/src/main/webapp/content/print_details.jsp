<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%--
     Make the JSTL-core and the JSTL-fmt taglibs available
     within this page. JSTL is the Java Standard Tag Library,
     which defines a set of commonly used elements
     The prefix to be used for core-tags is "c"
     The prefix to be used for fmt-tags is "fmt"
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<c:set var="from"  value="${param.from}" />


<mcr:session method="get" type="language" var="lang" />
<fmt:setLocale value="${lang}" scope="session" />
<fmt:setBundle basename="messages" scope="session" />

<html>
<head><title>Print Details</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link type="text/css" rel="stylesheet" href="${applicationScope.WebApplicationBaseURL}css/style_general.css">
	<link type="text/css" rel="stylesheet" href="${applicationScope.WebApplicationBaseURL}css/style_navigation.css">
	<link type="text/css" rel="stylesheet" href="${applicationScope.WebApplicationBaseURL}css/style_content.css">
	<link type="text/css" rel="stylesheet" href="${applicationScope.WebApplicationBaseURL}css/style_docdetails.css">
</head>

<c:choose>
 <c:when test="${fn:contains(from,'workflow')}" >
     <c:set var="layout" value="preview" />
 </c:when>
 <c:otherwise>
     <c:set var="layout" value="normal" />
 </c:otherwise> 
</c:choose>

<body bgcolor="#FFFFFF">
<table class="${layout}" >

<tr><td id="contentArea">
  <jsp:include page="/content/docdetails.jsp" >
		<jsp:param name="id" value="${param.id}"/>
		<jsp:param name="print" value="${true}"/>
	</jsp:include>
</td></tr>
</table>
</body>
</html>