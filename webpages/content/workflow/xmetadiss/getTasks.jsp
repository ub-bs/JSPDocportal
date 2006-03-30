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
   <c:when test="${requestScope.task.taskName eq 'taskCompleteDisshabAndSendToLibrary'}">
   TODO check TASKMESSAGE
      <p>
         <fmt:message key="WorkflowEngine.description.completedisshabandsendtolibrary.xmetadiss" />
      </p>
      <c:import url="/content/workflow/editorButtons.jsp" />
      <mcr:checkDecisionNode var="transition" processID="${requestScope.task.processID}" workflowType="xmetadiss" decision="canDisshabBeSubmitted" />
      <c:if test="${transition eq 'disshabCanBeSubmitted'}">
         <a href="${baseURL}nav?path=~workflow-disshab&transition=&endTask=taskCompleteDisshabAndSendToLibrary&processID=${requestScope.task.processID}"><fmt:message key="Nav.Application.dissertation.submit" /></a>
      </c:if>
   </c:when>
   <c:when test="${requestScope.task.taskName eq 'taskCheckCompleteness'}">
      <c:import url="/content/workflow/editorButtons.jsp" />
      <mcr:checkDecisionNode var="transition" processID="${requestScope.task.processID}" workflowType="xmetadiss" decision="canDisshabBeCommitted" />
         <a href="${baseURL}nav?path=~workflow-disshab&transition=go2canDisshabBeCommitted&endTask=taskCheckCompleteness&processID=${requestScope.task.processID}"><fmt:message key="Nav.Application.dissertation.MetadataOk" /></a><br>
         <a href="${baseURL}nav?path=~workflow-disshab&transition=go2sendBackToDisshabCreated&endTask=taskCheckCompleteness&processID=${requestScope.task.processID}"><fmt:message key="Nav.Application.dissertation.sendBackToInitiator" /></a><br>
   </c:when>   
   <c:when test="${requestScope.task.taskName eq 'taskEnterMessageData'}">
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
   <c:when test="${requestScope.task.taskName eq 'taskCheckIfSignedAffirmationYetAvailable'}">
      <a href="${baseURL}nav?path=~workflow-disshab&transition=go2canDisshabBeCommitted&endTask=taskCheckIfSignedAffirmationYetAvailable&processID=${requestScope.task.processID}"><fmt:message key="Nav.Application.dissertation.AffirmationIsAvailableCanBeCommitted" /></a><br>      
      <a href="${baseURL}nav?path=~workflow-disshab&transition=go2requireAffirmation&endTask=taskCheckIfSignedAffirmationYetAvailable&processID=${requestScope.task.processID}"><fmt:message key="Nav.Application.dissertation.RequireAffirmation" /></a><br>            
   </c:when>   
   <c:when test="${requestScope.task.taskName eq 'taskRequireSignedAffirmation'}">
      <c:if test="${true}">
         Letzte Mail wurde gesendet am: <a href="${baseURL}nav?path=~workflow-disshab&transition=go2checkNonDigitalRequirementsWithoutMail&endTask=taskRequireSignedAffirmation&processID=${requestScope.task.processID}"><fmt:message key="Nav.Application.dissertation.ContinueWaitingForAffirmation" /></a><br>      
      </c:if>
      <form action="${baseURL}nav" accept-charset="utf-8">
         <input name="path" value="~workflow-disshab" type="hidden" />
         <input name="transition" value="" type="hidden" />
         <input name="endTask" value="taskRequireSignedAffirmation" type="hidden" />
         <input name="processID" value="${requestScope.task.processID}" type="hidden" />
         <input name="mode" value="taskMessage" type="hidden" /> 
         <textarea name="message" cols="50" rows="4">Sehr geehrter Herr XY Sie müssen uns noch das Formblatt für elektronische Dissertationen unterschrieben zukommen lassen. Sonst können wir Ihre Dissertation nicht veröffentlichen</textarea>  
         <input name=submit" type="submit" value="<fmt:message key="WorkflowEngine.Form.SendTask" />"/>      
      </form>
   </c:when>
   <c:when test="${requestScope.task.taskName eq 'taskSendAffirmation'}">
      <a href="${baseURL}nav?path=~workflow-disshab&transition=go2checkNonDigitalRequirements&endTask=taskSendAffirmation&processID=${requestScope.task.processID}"><fmt:message key="Nav.Application.dissertation.sendAffirmationOfSubmission" /></a><br>      
   </c:when>    

   
   <c:otherwise>
    <h1>what else? TODO</h1>
   </c:otherwise>
</c:choose>
