<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>
<%@ page pageEncoding="UTF-8" %>

<fmt:message var="pageTitle" key="WF.institution" /> 
<stripes:layout-render name="../../../WEB-INF/layout/default.jsp" pageTitle = "${pageTitle}">
	<stripes:layout-component name="contents">
<mcr:session method="get" var="username" type="userID" />
<c:set var="WebApplicationBaseURL" value="${applicationScope.WebApplicationBaseURL}" />

<c:choose>
   <c:when test="${empty param.workflowType}">
      <c:set var="workflowType" value="institution" />
   </c:when>
   <c:otherwise>
      <c:set var="workflowType" value="${param.workflowType}" />
   </c:otherwise>
</c:choose>
<mcr:initWorkflowProcess userid="${username}" status="status" workflowProcessType="${workflowType}" processidVar="pid" scope="request" transition="go2IsInitiatorsEmailAddressAvailable"/>


<c:choose>
<c:when test="${fn:contains(status,'errorPermission')}">
<h2><fmt:message key="WF.institution.StartWorkflow" /></h2>
	<p><fmt:message key="WF.common.errorUserGuest" /></p>
	<p><fmt:message key="WF.institution.errorUserGuest2" /></p>
	<p><fmt:message key="Webpage.admin.DocumentManagement.FetchLogin" /></p>
</c:when>
<c:when test="${fn:contains(status,'errorWFM')}">
<h2><fmt:message key="WF.institution.StartWorkflow" /></h2>
	<p><fmt:message key="WF.institution.errorWfM" /></p>
	<p><fmt:message key="WF.institution.errorWfM2" /></p>
</c:when>
<c:otherwise>

    <c:import url="/content/workflow/institution/institutionData.jsp" />
</c:otherwise>
</c:choose>
</stripes:layout-component>
</stripes:layout-render>