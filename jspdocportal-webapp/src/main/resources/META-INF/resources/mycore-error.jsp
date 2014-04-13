<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"   %>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>

<c:set var="pageTitle">
<c:choose>
 <c:when test="${!empty param.messageKey}">
  <fmt:message key="${param.messageKey}" /> 
 </c:when>
 <c:when test="${!empty requestScope.messageKey}">
	<fmt:message key="${requestScope.messageKey}" />
 </c:when>
 <c:otherwise>
	 <fmt:message key="Webpage.error.Error" />
 </c:otherwise>
</c:choose>
</c:set>
 
<stripes:layout-render name="../WEB-INF/layout/default.jsp" pageTitle = "${pageTitle}">
	<stripes:layout-component name="contents">
	
<c:if test="${empty requestScope.lang && empty param.lang}">
   <c:redirect url="nav">
      <c:param name="path" value="~mycore-error" />
      <c:param name="messageKey" value="${param.messageKey}" />
   </c:redirect>
</c:if>

<h2>${pageTitle}</h2>

<p>
<c:if test="${!empty param.message}">
	<c:out value="${param.message}" />
</c:if>
</p>
<p>
<c:if test="${!empty requestScope.message}">
	<c:out value="${requestScope.message}" />
</c:if>
</p>
	</stripes:layout-component>
</stripes:layout-render>   