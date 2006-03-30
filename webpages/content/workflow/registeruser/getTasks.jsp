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
<c:choose>
   <c:when test="${requestScope.task.taskName eq 'initialization'}">
      <fmt:message key="WorfklowEngine.Processnumber" /> ${requestScope.task.processID}<br>
      <fmt:message key="WorkflowEngine.ActualStateOf.registeruser" />: 
      <b><fmt:message key="WorkflowEngine.initiator.statusMessage.${requestScope.task.workflowStatus}.registeruser" /></b>
   </c:when>
   <c:when test="${fn:toLowerCase(requestScope.task.taskName) eq 'completeUserAndSendToLibrary'}">
      <c:import url="/content/workflow/editorButtons.jsp" />
      <mcr:checkBooleanDecisionNode var="canBeSent" processID="${requestScope.task.processID}" workflowType="registeruser" decision="canUserSubmitted" />
      <c:if test="${canBeSent}">
         <a href="${baseURL}nav?path=~workflow-registeruser&endTask=completeUserAndSendToLibrary&processID=${requestScope.task.processID}"><fmt:message key="Nav.Application.registeruser.end" /></a>
      </c:if>
   </c:when>
   <c:otherwise>
    <h1>what else? TODO</h1>
   </c:otherwise>
</c:choose>
