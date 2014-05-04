<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"   %>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>

<fmt:message var="pageTitle" key="Nav.Start" /> 
<stripes:layout-render name="WEB-INF/layout/default.jsp" pageTitle = "${pageTitle}">
	<stripes:layout-component name="contents">
	
<h2><fmt:message key="Webpage.intro.Webpage.intro" /></h2>
<mcr:includeWebContent file="index_introtext.html" />

<p style="text-align:right">
<a style="color:white" href="<c:out value="${requestScope.WebApplicationBaseURL}" />/index/robotd/index.html"><fmt:message key="Webpage.intro.AccessForRobots" /> (<fmt:message key="documents" />)</a>
</p>
<p style="text-align:right">
<a style="color:white" href="<c:out value="${requestScope.WebApplicationBaseURL}" />/index/robotp/index.html"><fmt:message key="Webpage.intro.AccessForRobots" /> (<fmt:message key="authors" />)</a>
</p>
<p style="text-align:right">
<a style="color:white" href="<c:out value="${requestScope.WebApplicationBaseURL}" />/index/roboti/index.html"><fmt:message key="Webpage.intro.AccessForRobots" /> (<fmt:message key="institutions" />)</a>
</p>
	</stripes:layout-component>
</stripes:layout-render>   