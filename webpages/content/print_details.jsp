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
<%@ taglib prefix="mcr" uri="/WEB-INF/lib/mycore-taglibs.jar" %>

<html>
<head><title>Docdetails</title>
	<link type="text/css" rel="stylesheet" href="${applicationScope.WebApplicationBaseURL}css/style_general.css">
	<link type="text/css" rel="stylesheet" href="${applicationScope.WebApplicationBaseURL}css/style_navigation.css">
	<link type="text/css" rel="stylesheet" href="${applicationScope.WebApplicationBaseURL}css/style_content.css">

</head>
<body bgcolor="#FFFFFF">
<table id="maintable">
<tr><td id="contentArea">

<mcr:session var="lang" method="get" type="language"/>
<c:set var="lang" value="${lang}" scope="request" />
<jsp:include page="docdetails.jsp">
	<jsp:param name="fromWForDB"  value="${param.from}" />
	<jsp:param name="id" value="${param.id}"/>
	<jsp:param name="print" value="${true}"/>
</jsp:include>
</td></tr>
</table>
</body>
</html>