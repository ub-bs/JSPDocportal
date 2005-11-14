<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages'/>
<h2><fmt:message key="error" /></h2>
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