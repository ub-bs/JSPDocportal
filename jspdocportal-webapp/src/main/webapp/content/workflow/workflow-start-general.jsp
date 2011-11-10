<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"   %>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>

<fmt:message var="pageTitle" key="Nav.Archive" /> 
<stripes:layout-render name="../WEB-INF/layout/default.jsp" pageTitle = "${pageTitle}">
	<stripes:layout-component name="contents">

<mcr:session method="get" var="username" type="userID" />
<c:set  var="baseURL" value="${applicationScope.WebApplicationBaseURL}"/>

<c:choose>
   <c:when test="${not empty param.debug}">
      <c:set var="debug" value="true" />
   </c:when>
   <c:otherwise>
      <c:set var="debug" value="false" />
   </c:otherwise>
</c:choose>

<c:choose>
   <c:when test="${not empty param.lastInitiatedAction}">
   </c:when>
   <c:otherwise>
   </c:otherwise>
</c:choose>

<div class="headline"><fmt:message key="WF.common.MyTasks" /></div>

<mcr:getWorkflowTaskBeanList var="myTaskList" mode="activeTasks" debugUser="author1A" varTotalSize="total1" size="100" />
<table>
<c:forEach var="task" items="${myTaskList}">
   <c:set var="task" scope="request" value="${task}" />
   <c:import url="/content/workflow/${task.workflowProcessType}/getTasks.jsp" />
</c:forEach>
</table>

<br>&nbsp;<br>

<div class="headline"><fmt:message key="WF.common.MyInititiatedProcesses" /></div>

<mcr:getWorkflowTaskBeanList var="myProcessList" mode="initiatedProcesses" debugUser="author1A" varTotalSize="total2" size="100" />
<table>
<c:forEach var="task" items="${myProcessList}">
   <c:set var="task" scope="request" value="${task}" />
   <c:import url="/content/workflow/${task.workflowProcessType}/getTasks.jsp" />
</c:forEach>
</table>
	</stripes:layout-component>
</stripes:layout-render>  