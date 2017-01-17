<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.mycore.org/jspdocportal/base.tld" prefix="mcr" %>
<%@ page pageEncoding="UTF-8" %>

<mcr:session method="get" var="username" type="userID" />
<c:set  var="baseURL" value="${applicationScope.WebApplicationBaseURL}"/>

<c:set var="debug" value="true" />
<c:set var="dom" value="${requestScope.task.variables}" />

<c:if test="${requestScope.task.taskName ne 'initialization'}">
   <fmt:message key="WF.xmetadiss.Dissertation" /> <fmt:message key="WF.common.Processnumber" /> <b>${requestScope.task.processID}</b>: <br>
</c:if>
<c:choose>
   <c:when test="${requestScope.task.taskName eq 'initialization'}">
      <p><fmt:message key="WF.xmetadiss.ActualStateOfYourDissertation" />(<fmt:message key="WF.common.Processnumber" /> <b>${requestScope.task.processID}</b>): 
      </p>
      <p><b><fmt:message key="WF.xmetadiss.status.${requestScope.task.workflowStatus}" /></b>
      </p>
   </c:when>
   <c:when test="${requestScope.task.taskName eq 'taskprocessEditInitialized' }" >
      <p><img title="" alt="" src="${baseURL}images/greenArrow.gif"><fmt:message key="WF.xmetadiss.completedisshabandsendtolibrary" />
      </p>
      
      <mcr:checkDecisionNode var="transition" processID="${requestScope.task.processID}" workflowType="xmetadiss" decision="canDisshabBeSubmitted" />
      
      <c:import url="/content/workflow/editorButtons.jsp" />
      <p>
	      <c:if test="${transition eq 'disshabCanBeSubmitted'}">
	        <img title="" alt="" src="${baseURL}images/greenArrow.gif">         
            <a href="${baseURL}nav?path=~xmetadiss&transition=&endTask=taskprocessEditInitialized&processID=${requestScope.task.processID}"><fmt:message key="WF.xmetadiss.taskCompleteDisshabAndSendToLibrary" /></a>
	      </c:if>      
	  </p>    
   </c:when>
   
   <c:when test="${requestScope.task.taskName eq 'taskCompleteDisshabAndSendToLibrary'}">
      <p><img title="" alt="" src="${baseURL}images/greenArrow.gif"><fmt:message key="WF.xmetadiss.completedisshabandsendtolibrary" />
      </p>
      
      <mcr:checkDecisionNode var="transition" processID="${requestScope.task.processID}" workflowType="xmetadiss" decision="canDisshabBeSubmitted" />
      
      <c:import url="/content/workflow/editorButtons.jsp" />
      
	  <p>
	      <c:if test="${transition eq 'disshabCanBeSubmitted'}">
		     <img title="" alt="" src="${baseURL}images/greenArrow.gif">         
	         <a href="${baseURL}nav?path=~xmetadiss&transition=&endTask=taskCompleteDisshabAndSendToLibrary&processID=${requestScope.task.processID}"><fmt:message key="WF.xmetadiss.taskCompleteDisshabAndSendToLibrary" /></a>
	      </c:if>      
	  </p>    
   </c:when>
   <c:when test="${requestScope.task.taskName eq 'taskGetInitiatorsEmailAddress'}">
   	  <p><fmt:message key="WF.common.getInitiatorsEmailAddress" /></p>
	     <c:import url="/content/workflow/setworkflowvariable.jsp" >
	     	<c:param name="workflowType" value="xmetadiss" />
	     	<c:param name="endTask" value="taskGetInitiatorsEmailAddress" />
	     </c:import>
   </c:when>   
   <c:when test="${requestScope.task.taskName eq 'taskCheckCompleteness'}">
      <c:import url="/content/workflow/editorButtons.jsp" />
      <mcr:checkDecisionNode var="transition" processID="${requestScope.task.processID}" workflowType="xmetadiss" decision="canDisshabBeCommitted" />
         <p><fmt:message key="WF.common.AreTheMetadataOK" /></p>
         <p><img title="" alt="" src="${baseURL}images/greenArrow.gif">
         <a href="${baseURL}nav?path=~xmetadiss&transition=go2canDisshabBeCommitted&endTask=taskCheckCompleteness&processID=${requestScope.task.processID}"><fmt:message key="WF.common.MetadataOk_Continue" /></a>
         </p>
         <p><img title="" alt="" src="${baseURL}images/greenArrow.gif">
         <a href="${baseURL}nav?path=~xmetadiss&transition=go2sendBackToDisshabCreated&endTask=taskCheckCompleteness&processID=${requestScope.task.processID}"><fmt:message key="WF.common.MetadataNotOk_SendToInitiator" /></a>
         </p>
         <p><img title="" alt="" src="${baseURL}images/greenArrow.gif">
         <a href="${baseURL}nav?path=~xmetadiss&transition=go2suspendDisshab&endTask=taskCheckCompleteness&processID=${requestScope.task.processID}"><fmt:message key="WF.common.MetadataOk_Suspend" /></a>
         </p>                                              
   </c:when>   
   <c:when test="${requestScope.task.taskName eq 'getEndOfSuspensionDate'}">
   	  <p><fmt:message key="WF.xmetadiss.getEndOfSuspensionDate" /></p>
	     <c:import url="/content/workflow/setworkflowvariable.jsp" >
	     	<c:param name="workflowType" value="xmetadiss" />
	     	<c:param name="endTask" value="getEndOfSuspensionDate" />
	     </c:import>
   </c:when>   

   <c:when test="${requestScope.task.taskName eq 'waitInSuspension'}">
       <c:import url="/content/workflow/editorButtons.jsp" />
	  <p><img title="" alt="" src="${baseURL}images/greenArrow.gif">   
      <a href="${baseURL}nav?path=~xmetadiss&transition=endSuspension&endTask=waitInSuspension&processID=${requestScope.task.processID}"><fmt:message key="WF.xmetadiss.ReturnFromSuspension" /></a>
      </p>
   </c:when>   

   <c:when test="${requestScope.task.taskName eq 'taskEnterMessageData'}">
	     <c:import url="/content/workflow/setworkflowvariable.jsp" >
	     	<c:param name="workflowType" value="xmetadiss" />
	     	<c:param name="endTask" value="taskentermessagedata" />
	     </c:import>
   </c:when>
   <c:when test="${requestScope.task.taskName eq 'taskCheckIfSignedAffirmationYetAvailable'}">
	  <c:import url="/content/workflow/editorButtons.jsp" />
	  <p><img title="" alt="" src="${baseURL}images/greenArrow.gif">   
      <a href="${baseURL}nav?path=~xmetadiss&transition=go2canDisshabBeCommitted&endTask=taskCheckIfSignedAffirmationYetAvailable&processID=${requestScope.task.processID}"><fmt:message key="WF.xmetadiss.AffirmationIsAvailableCanBeCommitted" /></a>
      </p>
      <p><img title="" alt="" src="${baseURL}images/greenArrow.gif">      
      <a href="${baseURL}nav?path=~xmetadiss&transition=go2requireAffirmation&endTask=taskCheckIfSignedAffirmationYetAvailable&processID=${requestScope.task.processID}"><fmt:message key="WF.xmetadiss.RequireAffirmation" /></a>
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
      <a href="${baseURL}nav?path=~xmetadiss&transition=go2checkNonDigitalRequirementsWithoutMail&endTask=taskRequireSignedAffirmation&processID=${requestScope.task.processID}"><fmt:message key="WF.xmetadiss.ContinueWaitingForAffirmation" /></a>
      </p>
      
      <form action="${baseURL}setworkflowvariable" accept-charset="utf-8">
   	     <input name="dispatcherForward" value="/nav?path=~xmetadiss" type="hidden" />
         <input name="transition" value="" type="hidden" />
         <input name="endTask" value="taskRequireSignedAffirmation" type="hidden" />
         <input name="processID" value="${requestScope.task.processID}" type="hidden" />
         <input name="jbpmVariableNames" value="tmpTaskMessage" type="hidden" /> 
         <mcr:getWorkflowEngineVariable pid="${requestScope.task.processID}" var="docID" workflowVar="${applicationScope.constants.objectID}" /> 

         
	     <textarea name="tmpTaskMessage" cols="50" rows="4"><fmt:message key="WF.xmetadiss.mail.body.requireAffirmation" />
	     ${baseURL}content/docdetails/docdetails_disshab-deliver.jsp?id=${docID}&fromWF=true
	     </textarea>  
	     <br>&nbsp;<br>
         <input name=submit" type="submit" value="<fmt:message key="WF.common.Send" />"/>      
      </form>
   </c:when>
   <c:when test="${requestScope.task.taskName eq 'taskAdminCheckCommitmentNotSuccessFul'}">
      <p>
      <a href="${baseURL}nav?path=~xmetadiss&transition=go2disshabCommitted&endTask=taskAdminCheckCommitmentNotSuccessFul&processID=${requestScope.task.processID}"><fmt:message key="Nav.Application.dissertation.sendAffirmationOfSubmission" /></a><br>      
      </p>
   </c:when>
   <c:otherwise>
    <p> what else? TASK = ${requestScope.task.taskName} </p>
   </c:otherwise>
</c:choose>
