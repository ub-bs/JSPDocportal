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
   <fmt:message key="WorkflowEngine.Publication" /> <fmt:message key="WorkflowEngine.Processnumber" /> <b>${requestScope.task.processID}</b>: <br>
</c:if>
<c:choose>
   <c:when test="${requestScope.task.taskName eq 'initialization' }">
      <fmt:message key="WorkflowEngine.ActualStateOfYourDocument.publication" />
      (<fmt:message key="WorkflowEngine.Processnumber" /> <b>${requestScope.task.processID}</b>): <br>
      <b><fmt:message key="WorkflowEngine.initiator.statusMessage.${requestScope.task.workflowStatus}.publication" /></b>
      <br/>
   </c:when>
   <c:when test="${requestScope.task.taskName eq 'taskCompleteDocumentAndSendToLibrary' }" >
      <p>
         <img title="" alt="" src="${baseURL}images/greenArrow.gif">
         <fmt:message key="WorkflowEngine.description.completedocumentandsendtolibrary.publication" />
         <br>
         <mcr:checkDecisionNode var="transition" processID="${requestScope.task.processID}" workflowType="publication" decision="canDocumentBeSubmitted" />
         <c:if test="${transition eq 'documentCanBeSubmitted'}">
	        <img title="" alt="" src="${baseURL}images/greenArrow.gif">         
            <a href="${baseURL}nav?path=~publication&transition=&endTask=taskCompleteDocumentAndSendToLibrary&processID=${requestScope.task.processID}"><fmt:message key="WorkflowEngine.taskCompleteDocumentAndSendToLibrary.publication" /></a>
         </c:if>      
      </p>
      <c:import url="/content/workflow/editorButtons.jsp" />
   </c:when>
   
   <c:when test="${requestScope.task.taskName eq 'taskprocessEditInitialized' }" >
      <p>
         <img title="" alt="" src="${baseURL}images/greenArrow.gif">
         <fmt:message key="WorkflowEngine.description.completedocumentandsendtolibrary.publication" />
         <br>
         <mcr:checkDecisionNode var="transition" processID="${requestScope.task.processID}" workflowType="publication" decision="canDocumentBeSubmitted" />
         <c:if test="${transition eq 'documentCanBeSubmitted'}">
	        <img title="" alt="" src="${baseURL}images/greenArrow.gif">         
            <a href="${baseURL}nav?path=~publication&transition=&endTask=taskprocessEditInitialized&processID=${requestScope.task.processID}"><fmt:message key="WorkflowEngine.taskCompleteDocumentAndSendToLibrary.publication" /></a>
         </c:if>      
      </p>
      <c:import url="/content/workflow/editorButtons.jsp" />
   </c:when>

   <c:when test="${requestScope.task.taskName eq 'taskGetInitiatorsEmailAddress'}" >
      <p>
         <fmt:message key="WorkflowEngine.description.getInitiatorsEmailAddress" />
		 <br>&nbsp;<br>
	     <form action="${baseURL}setworkflowvariable" accept-charset="utf-8">
    	     <input name="dispatcherForward" value="/nav?path=~publication" type="hidden" />
        	 <input name="transition" value="" type="hidden" />
	         <input name="endTask" value="taskGetInitiatorsEmailAddress" type="hidden" />
    	     <input name="processID" value="${requestScope.task.processID}" type="hidden" />
    	     <input name="jbpmVariableNames" value="initiatorEmail" type="hidden" />
        	 <input type="text" size="80" name="initiatorEmail">
        	 <br>&nbsp;<br>
         	 <input name=submit" type="submit" value="<fmt:message key="WorkflowEngine.Form.Send" />" />     
	     </form>	
     </p>
   </c:when>   
   <c:when test="${requestScope.task.taskName eq 'taskCheckCompleteness'}">
      <c:import url="/content/workflow/editorButtons.jsp" />
      <mcr:checkDecisionNode var="transition" processID="${requestScope.task.processID}" workflowType="publication" decision="canDocumentBeCommitted" />
         <br>&nbsp;<br>
         <fmt:message key="WorkflowEngine.AreTheMetadataOk" />
         <br>&nbsp;<br>
         <img title="" alt="" src="${baseURL}images/greenArrow.gif">
         <a href="${baseURL}nav?path=~publication&transition=go2canDocumentBeCommitted&endTask=taskCheckCompleteness&processID=${requestScope.task.processID}"><fmt:message key="WorkflowEngine.MetadataOk.Continue" /></a>
         <br>
         <img title="" alt="" src="${baseURL}images/greenArrow.gif">
         <a href="${baseURL}nav?path=~publication&transition=go2sendBackToDocumentCreated&endTask=taskCheckCompleteness&processID=${requestScope.task.processID}"><fmt:message key="WorkflowEngine.MetadataNotOk.SendToInitiator" /></a>
         <br>
   </c:when>   
   <c:when test="${requestScope.task.taskName eq 'taskEnterMessageData'}">
     <form action="${baseURL}setworkflowvariable" accept-charset="utf-8">
   	     <input name="dispatcherForward" value="/nav?path=~publication" type="hidden" />
         <input name="transition" value="" type="hidden" />
         <input name="endTask" value="taskentermessagedata" type="hidden" />
         <input name="processID" value="${requestScope.task.processID}" type="hidden" />
         <input name="jbpmVariableNames" value="tmpTaskMessage" type="hidden" /> 
	     <textarea name="tmpTaskMessage" cols="50" rows="4">Sie müssen noch...</textarea>  
	     <br>&nbsp;<br>
    	<input name=submit" type="submit" value="<fmt:message key="WorkflowEngine.Form.SendTask" />"/>      
      </form>
   </c:when>
   <c:when test="${requestScope.task.taskName eq 'taskAdminCheckCommitmentNotSuccessFul'}">
      <a href="${baseURL}nav?path=~publication&transition=go2documentCommitted&endTask=taskAdminCheckCommitmentNotSuccessFul&processID=${requestScope.task.processID}"><fmt:message key="Nav.Application.dissertation.sendAffirmationOfSubmission" /></a><br>      
   </c:when>
   
       

   
   <c:otherwise>
    <p> what else? TASK = ${requestScope.task.taskName} </p>
   </c:otherwise>
</c:choose>
