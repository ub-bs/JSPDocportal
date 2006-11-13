<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<c:if test="${empty requestScope.lang && empty param.lang}">
   <c:redirect url="nav">
      <c:param name="path" value="~mycore-error" />
      <c:param name="messageKey" value="${param.messageKey}" />
   </c:redirect>
</c:if>
<c:choose>
    <c:when test="${!empty requestScope.lang}">
        <fmt:setLocale value="${requestScope.lang}" />
    </c:when>
    <c:otherwise>
        <fmt:setLocale value="${requestScope.lang}" />
    </c:otherwise>
</c:choose>
<fmt:setBundle basename='messages'/>

<div class="headline">
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
</div>

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