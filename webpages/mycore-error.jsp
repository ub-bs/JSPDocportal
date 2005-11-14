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
<h2><fmt:message key="Error" /></h2>
<h3><fmt:message key="GoToTheAdministrator" /></h3>
<p>
<c:if test="${!empty param.messageKey}">
<fmt:message key="${param.messageKey}" />
</c:if>
</p>
<p>
<c:if test="${!empty param.message}">
<c:out value="${param.message}" />
</c:if>
</p>
<c:if test="${!empty requestScope.messageKey}">
<fmt:message key="${requestScope.messageKey}" />
</c:if>
</p>
<p>
<c:if test="${!empty requestScope.message}">
<c:out value="${requestScope.message}" />
</c:if>
</p>