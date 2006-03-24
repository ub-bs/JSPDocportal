<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>

<mcr:session method="get" var="username" type="userID" />
<c:set  var="baseURL" value="${applicationScope.WebApplicationBaseURL}"/>

<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages' />
<c:set var="debug" value="true" />

<div class="headline"><fmt:message key="WorkflowEngine.MyTasks" /></div>

<mcr:getWorkflowTaskBeanList var="myTaskList" mode="activeTasks" debugUser="author1A" varTotalSize="total1" />
<c:forEach var="task" items="${myWorkflowList}">
   <c:set var="task" scope="request" value="${task}" />
   <c:import url="/content/workflow/${task.workflowProcessType}/tasks.jsp" />
</c:forEach>

<div class="headline"><fmt:message key="WorkflowEngine.MyInititiatedProcesses" /></div>

<mcr:getWorkflowTaskBeanList var="myProcessList" mode="initiatedProcesses" debugUser="author1A" varTotalSize="total2" />
<c:forEach var="task" items="${myProcessList}">
   <c:set var="task" scope="request" value="${task}" />
   <c:import url="/content/workflow/${task.workflowProcessType}/tasks.jsp" />
</c:forEach>