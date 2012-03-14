<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>
<%@ page pageEncoding="UTF-8" %>

<fmt:message var="pageTitle" key="WF.person" /> 
<stripes:layout-render name="../../../WEB-INF/layout/default.jsp" pageTitle = "${pageTitle}">
	<stripes:layout-component name="contents">
<mcr:session method="get" var="username" type="userID" />
<c:set var="WebApplicationBaseURL" value="${applicationScope.WebApplicationBaseURL}" />
<c:choose>
   <c:when test="${empty param.workflowType}">
      <c:set var="workflowType" value="person" />
   </c:when>
   <c:otherwise>
      <c:set var="workflowType" value="${param.workflowType}" />
   </c:otherwise>
</c:choose>
<mcr:initWorkflowProcess userid="${username}" status="status" workflowProcessType="${workflowType}" processidVar="pid" scope="request" transition="go2IsInitiatorsEmailAddressAvailable"/>


<c:choose>
<c:when test="${fn:contains(status,'errorPermission')}">
<h2><fmt:message key="WF.person.StartWorkflow" /></h2>
	<p><fmt:message key="WF.common.errorUserGuest" /></p>
	<p><fmt:message key="WF.person.errorUserGuest2" /></p>
	<p><fmt:message key="Webpage.admin.DocumentManagement.FetchLogin" /></p>
</c:when>
<c:when test="${fn:contains(status,'errorWFM')}">
<h2><fmt:message key="WF.person.StartWorkflow" /></h2>
	<p><fmt:message key="WF.author.errorWfM" /></p>
	<p><fmt:message key="WF.author.errorWfM2" /></p>
</c:when>
<c:otherwise>

    <c:import url="/content/workflow/person/personData.jsp" />
</c:otherwise>
</c:choose>
</stripes:layout-component>
</stripes:layout-render>