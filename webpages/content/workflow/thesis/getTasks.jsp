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

<c:if test="${requestScope.task.taskName ne 'initialization'}">
   <fmt:message key="WF.thesis.Thesis" /> <fmt:message key="WF.common.Processnumber" /> <b>${requestScope.task.processID}</b>: <br>
</c:if>
<c:choose>
   <c:when test="${requestScope.task.taskName eq 'initialization'}">
      <p><fmt:message key="WF.thesis.ActualStateOfYourThesis" />(<fmt:message key="WF.common.Processnumber" /> <b>${requestScope.task.processID}</b>): 
      </p>
      <p><b><fmt:message key="WF.thesis.status.${requestScope.task.workflowStatus}" /></b>
      </p>
   </c:when>
   <c:when test="${requestScope.task.taskName eq 'taskprocessEditInitialized' }" >
      <p><img title="" alt="" src="${baseURL}images/greenArrow.gif"><fmt:message key="WF.thesis.completethesisandsendtolibrary" />
      </p>
      
      <mcr:checkDecisionNode var="transition" processID="${requestScope.task.processID}" workflowType="thesis" decision="canThesisBeSubmitted" />
      
      <c:import url="/content/workflow/editorButtons.jsp" />
      <p>                              
	      <c:if test="${transition eq 'thesisCanBeSubmitted'}">
	        <img title="" alt="" src="${baseURL}images/greenArrow.gif">         
            <a href="${baseURL}nav?path=~thesis&transition=&endTask=taskprocessEditInitialized&processID=${requestScope.task.processID}"><fmt:message key="WF.thesis.taskCompleteThesisAndSendToLibrary" /></a>
	      </c:if>      
	  </p>    
   </c:when>
   
   <c:when test="${requestScope.task.taskName eq 'taskCompleteThesisAndSendToLibrary'}">
      <p><img title="" alt="" src="${baseURL}images/greenArrow.gif"><fmt:message key="WF.thesis.completethesisandsendtolibrary" />
      </p>
      
      <mcr:checkDecisionNode var="transition" processID="${requestScope.task.processID}" workflowType="thesis" decision="canThesisBeSubmitted" />
      
      <c:import url="/content/workflow/editorButtons.jsp" />
      
	  <p>
	      <c:if test="${transition eq 'thesisCanBeSubmitted'}">
		     <img title="" alt="" src="${baseURL}images/greenArrow.gif">         
	         <a href="${baseURL}nav?path=~thesis&transition=&endTask=taskCompleteThesisAndSendToLibrary&processID=${requestScope.task.processID}"><fmt:message key="WF.thesis.taskCompleteThesisAndSendToLibrary" /></a>
	      </c:if>      
	  </p>    
   </c:when>
   <c:when test="${requestScope.task.taskName eq 'taskGetInitiatorsEmailAddress'}">
   	  <p><fmt:message key="WF.common.getInitiatorsEmailAddress" /></p>
	     <c:import url="/content/workflow/setworkflowvariable.jsp" >
	     	<c:param name="workflowType" value="thesis" />
	     	<c:param name="endTask" value="taskGetInitiatorsEmailAddress" />
	     </c:import>
   </c:when>   
   <c:when test="${requestScope.task.taskName eq 'taskCheckCompleteness'}">
      <c:import url="/content/workflow/editorButtons.jsp" />
      <mcr:checkDecisionNode var="transition" processID="${requestScope.task.processID}" workflowType="thesis" decision="canThesisBeCommitted" />
         <p><fmt:message key="WF.common.AreTheMetadataOK" /></p>
         <p><img title="" alt="" src="${baseURL}images/greenArrow.gif">
         <a href="${baseURL}nav?path=~thesis&transition=go2canThesisBeCommitted&endTask=taskCheckCompleteness&processID=${requestScope.task.processID}"><fmt:message key="WF.common.MetadataOk_Continue" /></a>
         </p>
         <p><img title="" alt="" src="${baseURL}images/greenArrow.gif">
         <a href="${baseURL}nav?path=~thesis&transition=go2sendBackToThesisCreated&endTask=taskCheckCompleteness&processID=${requestScope.task.processID}"><fmt:message key="WF.common.MetadataNotOk_SendToInitiator" /></a>
         </p>
         <p><img title="" alt="" src="${baseURL}images/greenArrow.gif">
         <a href="${baseURL}nav?path=~thesis&transition=go2suspendThesis&endTask=taskCheckCompleteness&processID=${requestScope.task.processID}"><fmt:message key="WF.common.MetadataOk_Suspend" /></a>
         </p>                                              
   </c:when>   
   <c:when test="${requestScope.task.taskName eq 'getEndOfSuspensionDate'}">
   	  <p><fmt:message key="WF.thesis.getEndOfSuspensionDate" /></p>
	     <c:import url="/content/workflow/setworkflowvariable.jsp" >
	     	<c:param name="workflowType" value="thesis" />
	     	<c:param name="endTask" value="getEndOfSuspensionDate" />
	     </c:import>
   </c:when>   

   <c:when test="${requestScope.task.taskName eq 'waitInSuspension'}">
       <c:import url="/content/workflow/editorButtons.jsp" />
	  <p><img title="" alt="" src="${baseURL}images/greenArrow.gif">   
      <a href="${baseURL}nav?path=~thesis&transition=endSuspension&endTask=waitInSuspension&processID=${requestScope.task.processID}"><fmt:message key="WF.thesis.ReturnFromSuspension" /></a>
      </p>
   </c:when>   

   <c:when test="${requestScope.task.taskName eq 'taskEnterMessageData'}">
	     <c:import url="/content/workflow/setworkflowvariable.jsp" >
	     	<c:param name="workflowType" value="thesis" />
	     	<c:param name="endTask" value="taskentermessagedata" />
	     </c:import>
   </c:when>
   <c:when test="${requestScope.task.taskName eq 'taskCheckIfSignedAffirmationYetAvailable'}">
	  <c:import url="/content/workflow/editorButtons.jsp" />
	  <p><img title="" alt="" src="${baseURL}images/greenArrow.gif">   
      <a href="${baseURL}nav?path=~thesis&transition=go2canThesisBeCommitted&endTask=taskCheckIfSignedAffirmationYetAvailable&processID=${requestScope.task.processID}"><fmt:message key="WF.thesis.AffirmationIsAvailableCanBeCommitted" /></a>
      </p>
      <p><img title="" alt="" src="${baseURL}images/greenArrow.gif">      
      <a href="${baseURL}nav?path=~thesis&transition=go2requireAffirmation&endTask=taskCheckIfSignedAffirmationYetAvailable&processID=${requestScope.task.processID}"><fmt:message key="WF.thesis.RequireAffirmation" /></a>
      </p>
   </c:when>   
   <c:when test="${requestScope.task.taskName eq 'taskRequireSignedAffirmation'}">
      <c:set var="lastSendDate"><x:out select="$dom/variables/variable[@name = 'lastRequiredAffirmation']/@value" /></c:set>
      <p>
      <c:if test="${not empty lastSendDate}">
         Letzte Mail wurde gesendet am <i>${lastSendDate}</i>
      </c:if>
	  </p>
	  <p>
      <img title="" alt="" src="${baseURL}images/greenArrow.gif">         
      <a href="${baseURL}nav?path=~thesis&transition=go2checkNonDigitalRequirementsWithoutMail&endTask=taskRequireSignedAffirmation&processID=${requestScope.task.processID}"><fmt:message key="WF.thesis.ContinueWaitingForAffirmation" /></a>
      </p>
      
      <form action="${baseURL}setworkflowvariable" accept-charset="utf-8">
   	     <input name="dispatcherForward" value="/nav?path=~thesis" type="hidden" />
         <input name="transition" value="" type="hidden" />
         <input name="endTask" value="taskRequireSignedAffirmation" type="hidden" />
         <input name="processID" value="${requestScope.task.processID}" type="hidden" />
         <input name="jbpmVariableNames" value="tmpTaskMessage" type="hidden" /> 
         <mcr:getWorkflowEngineVariable pid="${requestScope.task.processID}" var="docID" workflowVar="${applicationScope.constants.objectID}" /> 

         
	     <textarea name="tmpTaskMessage" cols="50" rows="4"><fmt:message key="WF.thesis.mail.body.requireAffirmation" />
	     ${baseURL}content/results-config/docdetails-thesis-deliver.jsp?id=${docID}&fromWForDB=workflow
	     </textarea>  
	     <br>&nbsp;<br>
         <input name=submit" type="submit" value="<fmt:message key="WF.common.Send" />"/>      
      </form>
   </c:when>
   <c:when test="${requestScope.task.taskName eq 'taskAdminCheckCommitmentNotSuccessFul'}">
      <p>
      <a href="${baseURL}nav?path=~thesis&transition=go2thesisCommitted&endTask=taskAdminCheckCommitmentNotSuccessFul&processID=${requestScope.task.processID}"><fmt:message key="Nav.Application.dissertation.sendAffirmationOfSubmission" /></a><br>      
      </p>
   </c:when>
   <c:otherwise>
    <p> what else? TASK = ${requestScope.task.taskName} </p>
   </c:otherwise>
</c:choose>
