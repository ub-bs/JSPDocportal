<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>

<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages' />
<c:set var="type" value="${param.workflowProcessType}" />

<c:choose>
<c:when test="${empty(type)}">
	<div class="headline"><fmt:message key="Nav.AdminMenue" /></div>
 	<p><fmt:message key="Webpage.admin.AllowedFunctions" />:</p>
</c:when>
<c:otherwise>
	<div class="headline"><fmt:message key="Nav.Admin.${type}" /></div>
</c:otherwise>
</c:choose>
 	<p><c:import url="content/node.jsp" /></p> 


