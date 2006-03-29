<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>
<%@ page pageEncoding="UTF-8" %>

<mcr:session method="get" var="username" type="userID" />
<c:set  var="baseURL" value="${applicationScope.WebApplicationBaseURL}"/>

<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages' />
<c:set var="debug" value="true" />
<c:choose>
   <c:when test="${requestScope.task.taskName eq 'initialization'}">
      <fmt:message key="WorfklowEngine.Processnumber" /> ${requestScope.task.processID}<br>
      <fmt:message key="WorkflowEngine.ActualStateOfYourDissertation.xmetadiss" />: 
      <b><fmt:message key="WorkflowEngine.initiator.statusMessage.${requestScope.task.workflowStatus}.xmetadiss" /></b>
   </c:when>
   <c:when test="${fn:toLowerCase(requestScope.task.taskName) eq 'completedisshabandsendtolibrary'}">
      <p>
         <fmt:message key="WorkflowEngine.description.completedisshabandsendtolibrary.xmetadiss" />
      </p>
      <c:import url="/content/workflow/editorButtons.jsp" />
      <mcr:checkBooleanDecisionNode var="canBeSent" processID="${requestScope.task.processID}" workflowType="xmetadiss" decision="canDisshabBeSubmitted" />
      <c:if test="${canBeSent}">
         <a href="${baseURL}nav?path=~workflow-disshab&transition=&endTask=completedisshabandsendtolibrary&processID=${requestScope.task.processID}"><fmt:message key="Nav.Application.dissertation.submit" /></a>
      </c:if>
   </c:when>
   <c:when test="${fn:toLowerCase(requestScope.task.taskName) eq 'taskcheckcompleteness'}">
      <c:import url="/content/workflow/editorButtons.jsp" />
      <mcr:checkBooleanDecisionNode var="canBeCommitted" processID="${requestScope.task.processID}" workflowType="xmetadiss" decision="canDisshabBeCommitted" />
      <c:if test="${canBeCommitted}">
         <a href="${baseURL}nav?path=~workflow-disshab&transition=go2canDisshabBeCommitted&endTask=taskCheckCompleteness&processID=${requestScope.task.processID}"><fmt:message key="Nav.Application.dissertation.commit" /></a><br>
      </c:if>
      <a href="${baseURL}nav?path=~workflow-disshab&transition=go2sendBackToDisshabCreated&endTask=taskCheckCompleteness&processID=${requestScope.task.processID}"><fmt:message key="Nav.Application.dissertation.sendBackToInitiator" /></a><br>
   </c:when>   
   <c:when test="${fn:toLowerCase(requestScope.task.taskName) eq 'taskentermessagedata'}">
      <form action="${baseURL}nav" accept-charset="utf-8">
         <input name="path" value="~workflow-disshab" type="hidden" />
         <input name="transition" value="" type="hidden" />
         <input name="endTask" value="taskentermessagedata" type="hidden" />
         <input name="processID" value="${requestScope.task.processID}" type="hidden" />
         <input name="mode" value="taskMessage" type="hidden" /> 
         <textarea name="message" cols="50" rows="4">Sie müssen noch...</textarea>  
         <input name=submit" type="submit" value="<fmt:message key="WorkflowEngine.Form.SendTask" />"/>      
      </form>
   </c:when>   
   <c:otherwise>
    <h1>what else? TODO</h1>
   </c:otherwise>
</c:choose>
