<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>

<c:set  var="baseURL" value="${applicationScope.WebApplicationBaseURL}"/>

<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages' />

<c:choose>
   <c:when test="${!empty(param.debug)}">
      <c:set var="debug" value="true" />
   </c:when>
   <c:otherwise>
      <c:set var="debug" value="false" />
   </c:otherwise>
</c:choose>

<c:if test="${!empty(param.endTask)}">
    <c:set var="endTask" scope="request" value="${param.endTask}" />
    <c:set var="processID" scope="request" value="${param.processID}" />
    <c:set var="transition" scope="request" value="${param.transition}" />
    <c:if test="${!empty(param.setWorkflowVariableName)}">
       <mcr:setWorkflowEngineVariable workflowVar="${param.setWorkflowVariableName}" value="${param.setWorkflowVariableValue}" pid="${param.processID}" />
    </c:if>
    <c:import url="/content/workflow/xmetadiss/endTasks.jsp" />
</c:if>



<div class="headline"><fmt:message key="Nav.WorkflowDisshab" /></div>

<br>&nbsp;<br>

<div class="headline"><fmt:message key="WorkflowEngine.MyTasks" /></div>

<mcr:getWorkflowTaskBeanList var="myTaskList" mode="activeTasks" workflowTypes="xmetadiss" 
	varTotalSize="total1" />
<table>
<c:forEach var="task" items="${myTaskList}">
   <c:set var="task" scope="request" value="${task}" />
   <c:import url="/content/workflow/${task.workflowProcessType}/getTasks.jsp" />
</c:forEach>
<c:if test="${empty(myTaskList)}">
   <font color="#00ff00"><fmt:message key="WorkflowEngine.NoTasks" /></font>
</c:if>
</table>

<br>&nbsp;<br>

<div class="headline"><fmt:message key="WorkflowEngine.MyInititiatedProcesses" /></div>

<mcr:getWorkflowTaskBeanList var="myProcessList" mode="initiatedProcesses" workflowTypes="xmetadiss"
	varTotalSize="total2" />
<table>
<c:forEach var="task" items="${myProcessList}">
   <c:set var="task" scope="request" value="${task}" />
   <c:import url="/content/workflow/${task.workflowProcessType}/getTasks.jsp" />
</c:forEach>
<c:if test="${empty(myProcessList)}">
   <font color="#00ff00"><fmt:message key="WorkflowEngine.NoTasks" /></font>
</c:if>
</table>