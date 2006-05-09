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
<c:set var="dom" value="${requestScope.task.variables}" />

<c:if test="${requestScope.task.taskName ne 'initialization'}">
   <fmt:message key="WorkflowEngine.Dissertaton" /> <fmt:message key="WorkflowEngine.Processnumber" /> <b>${requestScope.task.processID}</b>: <br>
</c:if>
<c:choose>
   <c:when test="${requestScope.task.taskName eq 'initialization'}">
      <fmt:message key="WorkflowEngine.ActualStateOfYourDissertation.xmetadiss" />
      (<fmt:message key="WorkflowEngine.Processnumber" /> <b>${requestScope.task.processID}</b>): <br>
      <b><fmt:message key="WorkflowEngine.initiator.statusMessage.${requestScope.task.workflowStatus}.xmetadiss" /></b>
   </c:when>
   <c:when test="${requestScope.task.taskName eq 'taskCompleteDisshabAndSendToLibrary'}">
      <p>
         <img title="" alt="" src="${baseURL}images/greenArrow.gif">
         <fmt:message key="WorkflowEngine.description.completedisshabandsendtolibrary.xmetadiss" />
         <br>
         <mcr:checkDecisionNode var="transition" processID="${requestScope.task.processID}" workflowType="xmetadiss" decision="canDisshabBeSubmitted" />
         <c:if test="${transition eq 'disshabCanBeSubmitted'}">
	        <img title="" alt="" src="${baseURL}images/greenArrow.gif">         
            <a href="${baseURL}nav?path=~xmetadiss&transition=&endTask=taskCompleteDisshabAndSendToLibrary&processID=${requestScope.task.processID}"><fmt:message key="WorkflowEngine.taskCompleteDisshabAndSendToLibrary.xmetadiss" /></a>
         </c:if>      
      </p>
      <c:import url="/content/workflow/editorButtons.jsp" />
   </c:when>
   <c:when test="${requestScope.task.taskName eq 'taskGetInitiatorsEmailAddress'}">
      <p>
         <fmt:message key="WorkflowEngine.description.getInitiatorsEmailAddress" />
		 <br>&nbsp;<br>
	     <form action="${baseURL}setworkflowvariable" accept-charset="utf-8">
    	     <input name="dispatcherForward" value="/nav?path=~xmetadiss" type="hidden" />
        	 <input name="transition" value="" type="hidden" />
	         <input name="endTask" value="taskGetInitiatorsEmailAddress" type="hidden" />
    	     <input name="processID" value="${requestScope.task.processID}" type="hidden" />
    	     <input name="jbpmVariableNames" value="initiatorEmail" type="hidden" />
        	 <input type="text" size="80" name="initiatorEmail">
        	 <br>&nbsp;<br>
         	 <input name=submit" type="submit" value="<fmt:message key="WorkflowEngine.Form.Send" />"/>      
	     </form>	
     </p>
   </c:when>   
   <c:when test="${requestScope.task.taskName eq 'taskCheckCompleteness'}">
      <c:import url="/content/workflow/editorButtons.jsp" />
      <mcr:checkDecisionNode var="transition" processID="${requestScope.task.processID}" workflowType="xmetadiss" decision="canDisshabBeCommitted" />
         <br>&nbsp;<br>
         <fmt:message key="WorkflowEngine.AreTheMetadataOk" />
         <br>&nbsp;<br>
         <img title="" alt="" src="${baseURL}images/greenArrow.gif">
         <a href="${baseURL}nav?path=~xmetadiss&transition=go2canDisshabBeCommitted&endTask=taskCheckCompleteness&processID=${requestScope.task.processID}"><fmt:message key="WorkflowEngine.MetadataOk.Continue" /></a>
         <br>
         <img title="" alt="" src="${baseURL}images/greenArrow.gif">
         <a href="${baseURL}nav?path=~xmetadiss&transition=go2sendBackToDisshabCreated&endTask=taskCheckCompleteness&processID=${requestScope.task.processID}"><fmt:message key="WorkflowEngine.MetadataNotOk.SendToInitiator" /></a>
         <br>
   </c:when>   
   <c:when test="${requestScope.task.taskName eq 'taskEnterMessageData'}">
     <form action="${baseURL}setworkflowvariable" accept-charset="utf-8">
   	     <input name="dispatcherForward" value="/nav?path=~xmetadiss" type="hidden" />
         <input name="transition" value="" type="hidden" />
         <input name="endTask" value="taskentermessagedata" type="hidden" />
         <input name="processID" value="${requestScope.task.processID}" type="hidden" />
         <input name="jbpmVariableNames" value="tmpTaskMessage" type="hidden" /> 
	     <textarea name="tmpTaskMessage" cols="50" rows="4">Sie müssen noch...</textarea>  
	     <br>&nbsp;<br>
    	<input name=submit" type="submit" value="<fmt:message key="WorkflowEngine.Form.SendTask" />"/>      
      </form>
   </c:when>
   <c:when test="${requestScope.task.taskName eq 'taskCheckIfSignedAffirmationYetAvailable'}">
      <br>&nbsp;<br>
      <img title="" alt="" src="${baseURL}images/greenArrow.gif">   
      <a href="${baseURL}nav?path=~xmetadiss&transition=go2canDisshabBeCommitted&endTask=taskCheckIfSignedAffirmationYetAvailable&processID=${requestScope.task.processID}"><fmt:message key="Nav.Application.dissertation.AffirmationIsAvailableCanBeCommitted" /></a>
      <br>      
      <img title="" alt="" src="${baseURL}images/greenArrow.gif">      
      <a href="${baseURL}nav?path=~xmetadiss&transition=go2requireAffirmation&endTask=taskCheckIfSignedAffirmationYetAvailable&processID=${requestScope.task.processID}"><fmt:message key="Nav.Application.dissertation.RequireAffirmation" /></a>
      <br>            
   </c:when>   
   <c:when test="${requestScope.task.taskName eq 'taskRequireSignedAffirmation'}">
      <c:set var="lastSendDate"><x:out select="$dom/variables/variable[@name = 'lastRequiredAffirmation']/@value" /></c:set>
      <c:if test="${!empty(lastSendDate)}">
         Letzte Mail wurde gesendet am <i>${lastSendDate}</i>
      </c:if>
	  <br>&nbsp;<br>
      <img title="" alt="" src="${baseURL}images/greenArrow.gif">         
      <a href="${baseURL}nav?path=~xmetadiss&transition=go2checkNonDigitalRequirementsWithoutMail&endTask=taskRequireSignedAffirmation&processID=${requestScope.task.processID}"><fmt:message key="Nav.Application.dissertation.ContinueWaitingForAffirmation" /></a>
      <br>&nbsp;<br>
      <form action="${baseURL}setworkflowvariable" accept-charset="utf-8">
   	     <input name="dispatcherForward" value="/nav?path=~xmetadiss" type="hidden" />
         <input name="transition" value="" type="hidden" />
         <input name="endTask" value="taskRequireSignedAffirmation" type="hidden" />
         <input name="processID" value="${requestScope.task.processID}" type="hidden" />
         <input name="jbpmVariableNames" value="tmpTaskMessage" type="hidden" /> 
	     <textarea name="tmpTaskMessage" cols="50" rows="4">Sehr geehrter Herr XY Sie müssen uns noch das Formblatt für elektronische Dissertationen unterschrieben zukommen lassen. Sonst können wir Ihre Dissertation nicht veröffentlichen</textarea>  
	     <br>&nbsp;<br>
         <input name=submit" type="submit" value="<fmt:message key="WorkflowEngine.Form.Send" />"/>      
      </form>
   </c:when>
   <c:when test="${requestScope.task.taskName eq 'taskAdminCheckCommitmentNotSuccessFul'}">
      <a href="${baseURL}nav?path=~xmetadiss&transition=go2disshabCommitted&endTask=taskAdminCheckCommitmentNotSuccessFul&processID=${requestScope.task.processID}"><fmt:message key="Nav.Application.dissertation.sendAffirmationOfSubmission" /></a><br>      
   </c:when>
   
       

   
   <c:otherwise>
    <h1>what else? TODO</h1>
   </c:otherwise>
</c:choose>
