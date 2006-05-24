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
<c:set var="objid" value="" />

<c:if test="${requestScope.task.taskName ne 'initialization'}">
   <fmt:message key="WorkflowEngine.author" /> <fmt:message key="WorkflowEngine.Processnumber" /> <b>${requestScope.task.processID}</b>: <br>
</c:if>
<c:choose>
   <c:when test="${requestScope.task.taskName eq 'initialization'}">
      <fmt:message key="WorkflowEngine.ActualStateOfAuthor.author" />
      (<fmt:message key="WorkflowEngine.Processnumber" /> <b>${requestScope.task.processID}</b>): <br>
       <b><fmt:message key="WorkflowEngine.initiator.statusMessage.${requestScope.task.workflowStatus}.author" /></b>
       <br />&nbsp;<br />
   </c:when>


    <c:when test="${requestScope.task.taskName eq 'taskInputAuthorEqualsInitator'}">
      <br>&nbsp;<br>
      <img title="" alt="" src="${baseURL}images/greenArrow.gif">                         
      <a href="${baseURL}nav?path=~author&transition=go2CreateAuthorFromInitiator&endTask=taskInputAuthorEqualsInitator&processID=${requestScope.task.processID}"><fmt:message key="Nav.Workflow.author.iAmTheAuthor" /></a>
      <br>                                                   
      <img title="" alt="" src="${baseURL}images/greenArrow.gif">      
      <a href="${baseURL}nav?path=~author&transition=go2CreateNewAuthor&endTask=taskInputAuthorEqualsInitator&processID=${requestScope.task.processID}"><fmt:message key="Nav.Workflow.author.iAmNotTheAuthor" /></a>
   </c:when>   
   
   <c:when test="${requestScope.task.taskName eq 'displayAuthorForUser'}">
    	<mcr:getWorkflowEngineVariable pid="${requestScope.task.processID}" var="objid" workflowVar="authorID" /> 
    	<%-- alternativ:
	   	<c:set var="authorID"><x:out select="$dom/variables/variable[@name = 'authorID']/@value" /></c:set> --%>
		<br>&nbsp;<br>
	    <fmt:message key="WorkflowEngine.AuthorForUserExists.author">
			<fmt:param>${objid}</fmt:param>                             
	    </fmt:message>
    	<br>&nbsp;<br>
	    <img title="" alt="" src="${baseURL}images/greenArrow.gif">      
    	<a href="${baseURL}nav?path=~author&transition=go2End&endTask=displayAuthorForUser&processID=${requestScope.task.processID}"><fmt:message key="Nav.Workflow.author.finishWorkflow" /></a>
   </c:when>
   
  <c:when test="${requestScope.task.taskName eq 'taskCompleteAuthorAndSendToLibrary'}">
      <p>
         <img title="" alt="" src="${baseURL}images/greenArrow.gif">
         <fmt:message key="WorkflowEngine.description.completeauthorandsendtolibrary.author" />
         <br>
       <mcr:checkDecisionNode var="transition" processID="${requestScope.task.processID}" workflowType="author" decision="canAuthorBeSubmitted" />
         <c:if test="${transition eq 'authorCanBeSubmitted'}">
	        <img title="" alt="" src="${baseURL}images/greenArrow.gif">         
            <a href="${baseURL}nav?path=~author&transition=go2canAuthorBeSubmitted&endTask=taskCompleteAuthorAndSendToLibrary&processID=${requestScope.task.processID}"><fmt:message key="WorkflowEngine.taskCompleteAuthorAndSendToLibrary.author" /></a>
         </c:if>     
      </p>
      <c:import url="/content/workflow/editorButtons.jsp" />
   </c:when> 

   <c:when test="${requestScope.task.taskName eq 'taskGetInitiatorsEmailAddress'}">
      <p>
         <fmt:message key="WorkflowEngine.description.getInitiatorsEmailAddress" />
		 <br>&nbsp;<br>
	     <form action="${baseURL}setworkflowvariable" accept-charset="utf-8">
    	     <input name="dispatcherForward" value="/nav?path=~author" type="hidden" />
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
      <mcr:checkDecisionNode var="transition" processID="${requestScope.task.processID}" workflowType="author" decision="canAuthorBeCommitted" />
         <br>&nbsp;<br>
         <fmt:message key="WorkflowEngine.AreTheMetadataOk" />
         <br>&nbsp;<br>
		 <c:if test="${transition eq 'authorCanBeCommitted'}">
	        <img title="" alt="" src="${baseURL}images/greenArrow.gif">         
            <a href="${baseURL}nav?path=~author&transition=go2canAuthorBeCommitted&endTask=taskCheckCompleteness&processID=${requestScope.task.processID}"><fmt:message key="WorkflowEngine.MetadataOk.Continue" /></a>
            <br>
         </c:if>     
         <br>
         <img title="" alt="" src="${baseURL}images/greenArrow.gif">
         <a href="${baseURL}nav?path=~author&transition=go2sendBackToAuthorCreated&endTask=taskCheckCompleteness&processID=${requestScope.task.processID}"><fmt:message key="WorkflowEngine.MetadataNotOk.SendToInitiator" /></a>
         <br>
   </c:when>   

   <c:when test="${requestScope.task.taskName eq 'taskEnterMessageData'}">
     <form action="${baseURL}setworkflowvariable" accept-charset="utf-8">
   	     <input name="dispatcherForward" value="/nav?path=~author" type="hidden" />
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
      <a href="${baseURL}nav?path=~author&transition=go2authorCommitted2&endTask=taskAdminCheckCommitmentNotSuccessFul&processID=${requestScope.task.processID}"><fmt:message key="Nav.Workflow.author.admin.authorValidated" /></a><br>      
   </c:when>
   
       

   
   <c:otherwise>
    <h1>what else? TODO</h1>
   </c:otherwise>
</c:choose>
