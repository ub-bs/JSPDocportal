<%@ page import="org.mycore.frontend.workflowengine.jbpm.MCRWorkflowConstants" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>
<%@ page pageEncoding="UTF-8" %>

<mcr:session method="get" var="username" type="userID" />
<c:set  var="baseURL" value="${applicationScope.WebApplicationBaseURL}"/>

<c:set var="debug" value="true" />
<c:set var="dom" value="${requestScope.task.variables}" />
<c:set var="objid" value="" />

<c:if test="${requestScope.task.taskName ne 'taskInitialization'}">
   <fmt:message key="WF.institution.Institution" /> <fmt:message key="WF.common.Processnumber" /> <b>${requestScope.task.processID}</b>: <br>
</c:if>
<c:choose>
   <c:when test="${requestScope.task.taskName eq 'initialization'}">
      <fmt:message key="WF.institution.ActualStateOfYourInstitution" />
      (<fmt:message key="WF.common.Processnumber" /> <b>${requestScope.task.processID}</b>): <br>
       <b><fmt:message key="WF.institution.status.${requestScope.task.workflowStatus}" /></b>
       <br />&nbsp;<br />
   </c:when>
   
     <c:when test="${requestScope.task.taskName eq 'taskDisplayInstitutionData'}">
      <p>
         <img title="" alt="" src="${baseURL}images/greenArrow.gif">
         <fmt:message key="WF.institution.editInstitution" />
         <br />
       </p>
      <c:import url="/content/workflow/editorButtons.jsp" />
      <p>
         <mcr:checkDecisionNode var="transition" processID="${requestScope.task.processID}" workflowType="institution" decision="canChangesBeCommitted" />
         <c:if test="${transition eq 'canChangesBeCommitted_yes'}">
	        <img title="" alt="" src="${baseURL}images/greenArrow.gif">         
            <a href="${baseURL}nav?path=~institution&transition=go2CanChangesBeCommitted&endTask=taskDisplayInstitutionData&processID=${requestScope.task.processID}"><fmt:message key="WF.institution.taskCommitChanges" /></a>
            <br />
         </c:if>     
      </p>
   </c:when> 
  
   
  <c:when test="${requestScope.task.taskName eq 'taskCompleteInstitutionAndSendToLibrary'}">
      <p>
         <img title="" alt="" src="${baseURL}images/greenArrow.gif">
         <fmt:message key="WF.institution.completeinstitutionandsendtolibrary" />
         <br />
       </p>  
      <c:import url="/content/workflow/editorButtons.jsp" />
     <p>
         <mcr:checkDecisionNode var="transition" processID="${requestScope.task.processID}" workflowType="institution" decision="canInstitutionBeSubmitted" />
         <c:if test="${transition eq 'canInstitutionBeSubmitted_yes'}">
	        <img title="" alt="" src="${baseURL}images/greenArrow.gif">         
            <a href="${baseURL}nav?path=~institution&transition=go2CanInstitutionBeSubmitted&endTask=taskCompleteInstitutionAndSendToLibrary&processID=${requestScope.task.processID}"><fmt:message key="WF.institution.taskCompleteInstitutionAndSendToLibrary" /></a>
            <br />
         </c:if>     
      </p>
     
   </c:when> 


   
   <c:when test="${requestScope.task.taskName eq 'taskGetInitiatorsEmailAddress'}">
      <p>
         <fmt:message key="WF.common.getInitiatorsEmailAddress" />
		 <br>&nbsp;<br>
	     <c:import url="/content/workflow/setworkflowvariable.jsp" >
	     	<c:param name="workflowType" value="institution" />
	     	<c:param name="endTask" value="taskGetInitiatorsEmailAddress" />	     	
	     </c:import>
		 
     </p>
   </c:when>   

   <c:when test="${requestScope.task.taskName eq 'taskCheckCompleteness'}">
      <c:import url="/content/workflow/editorButtons.jsp" />
      <mcr:checkDecisionNode var="transition" processID="${requestScope.task.processID}" workflowType="institution" decision="canInstitutionBeCommitted" />
         <br>
         <fmt:message key="WF.common.AreTheMetadataOK" />
         <br>&nbsp;<br>
		 <c:if test="${transition eq 'canInstitutionBeCommitted_yes'}">
	        <img title="" alt="" src="${baseURL}images/greenArrow.gif">         
            <a href="${baseURL}nav?path=~institution&transition=go2CanInstitutionBeCommitted&endTask=taskCheckCompleteness&processID=${requestScope.task.processID}"><fmt:message key="WF.common.MetadataOk_Continue" /></a>
            <br>
         </c:if>     
         <br>
         <img title="" alt="" src="${baseURL}images/greenArrow.gif">
         <a href="${baseURL}nav?path=~institution&transition=go2SendBackToInstitutionCreated&endTask=taskCheckCompleteness&processID=${requestScope.task.processID}"><fmt:message key="WF.common.MetadataNotOk_SendToInitiator" /></a>
         <br>
   </c:when>   

   <c:when test="${requestScope.task.taskName eq 'taskEnterMessageData'}">
     <form action="${baseURL}setworkflowvariable" accept-charset="utf-8">
   	     <input name="dispatcherForward" value="/nav?path=~institution" type="hidden" />
         <input name="transition" value="" type="hidden" />
         <input name="endTask" value="taskentermessagedata" type="hidden" />
         <input name="processID" value="${requestScope.task.processID}" type="hidden" />
         <input name="jbpmVariableNames" value="tmpTaskMessage" type="hidden" /> 
	     <textarea name="tmpTaskMessage" cols="50" rows="4">Sie müssen noch...</textarea>  
	     <br>&nbsp;<br>
    	<input name=submit" type="submit" value="<fmt:message key="WF.common.SendTask" />"/>      
      </form>
   </c:when>

   <c:when test="${requestScope.task.taskName eq 'taskAdminCheckCommitmentNotSuccessful'}">
      <a href="${baseURL}nav?path=~institution&transition=go2InstitutionCommitted2&endTask=taskAdminCheckCommitmentNotSuccessful&processID=${requestScope.task.processID}"><fmt:message key="WF.institution.admin.instiutionValidated" /></a><br>      
   </c:when>
   
   <c:otherwise>
    <h1>what else? TODO</h1>
   </c:otherwise>
</c:choose>
