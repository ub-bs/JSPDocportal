<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn" %>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>
<mcr:session method="get" var="username" type="userID" />
<c:set var="WebApplicationBaseURL" value="${applicationScope.WebApplicationBaseURL}" />
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages'/>
<c:choose>
   <c:when test="${empty param.workflowType}">
      <c:set var="workflowType" value="xmetadiss" />
   </c:when>
   <c:otherwise>
      <c:set var="workflowType" value="${param.workflowType}" />
   </c:otherwise>
</c:choose>
<mcr:initWorkflowProcess userid="${username}" status="status" workflowProcessType="${workflowType}" processidVar="pid" scope="request" />


<c:choose>
<c:when test="${fn:contains(status,'errorUserGuest')}">
<div class="headline"><fmt:message key="Dissertation.Subtitle1" /></div>
	<p><fmt:message key="SWF.Dissertation.errorUserGuest" /></p>
	<p><fmt:message key="SWF.Dissertation.next.errorUserGuest" /></p>
	<p><fmt:message key="DocumentManagement.FetchLogin" /></p>
</c:when>
<c:when test="${fn:contains(status,'errorWFM')}">
<div class="headline"><fmt:message key="Dissertation.Subtitle1" /></div>
	<p><fmt:message key="SWF.Dissertation.errorWfM" /></p>
	<p><fmt:message key="SWF.Dissertation.next.errorWfM" /></p>
</c:when>
<c:otherwise>
     <c:import url="/content/workflow/xmetadiss/dissertationData.jsp" />
</c:otherwise>
</c:choose>
