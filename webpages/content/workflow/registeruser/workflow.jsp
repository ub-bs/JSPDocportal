<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>

<c:set  var="baseURL" value="${applicationScope.WebApplicationBaseURL}"/>

<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages' />

<c:if test="${!empty(param.endTask)}">
    <c:set var="endTask" scope="request" value="${param.endTask}" />
    <c:set var="processID" scope="request" value="${param.processID}" />
    <c:import url="/content/workflow/registeruser/endTasks.jsp" />
</c:if>



<div class="headline"><fmt:message key="Nav.WorkflowRegisteruser" /></div>

<br>&nbsp;<br>

<div class="headline"><fmt:message key="WorkflowEngine.MyTasks" /></div>

<mcr:getWorkflowTaskBeanList var="myTaskList" mode="activeTasks" workflowTypes="registeruser" 	varTotalSize="total1" />
<table>
<c:forEach var="task" items="${myTaskList}">
   <c:set var="task" scope="request" value="${task}" />
   myTaskList
   <c:import url="/content/workflow/${task.workflowProcessType}/getTasks.jsp" />
</c:forEach>
</table>

<br>&nbsp;<br>

<div class="headline"><fmt:message key="WorkflowEngine.MyInititiatedProcesses" /></div>

<mcr:getWorkflowTaskBeanList var="myProcessList" mode="initiatedProcesses" workflowTypes="registeruser"	varTotalSize="total2" />
<table>
<c:forEach var="task" items="${myProcessList}">
   <c:set var="task" scope="request" value="${task}" />
   myProcessList
   <c:import url="/content/workflow/${task.workflowProcessType}/getTasks.jsp" />
</c:forEach>
</table>