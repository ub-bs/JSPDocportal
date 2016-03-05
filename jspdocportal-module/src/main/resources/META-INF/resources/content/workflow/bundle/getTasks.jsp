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
	<h4>
   		<fmt:message key="WF.bundle.Bundle" /><br />
   		<fmt:message key="WF.common.Processnumber" /> <strong>${requestScope.task.processID}</strong>
   	</h4>
</c:if>
<c:choose>
   <c:when test="${requestScope.task.taskName eq 'initialization'}">
      <h4>
      	<fmt:message key="WF.bundle.ActualStateOfYourBundle" /><br />
      	(<fmt:message key="WF.common.Processnumber" /> <strong>${requestScope.task.processID}</strong>) 
      </h4>
      <fmt:message key="WF.bundle.status.${requestScope.task.workflowStatus}" />     
   </c:when>
   
   <c:when test="${requestScope.task.taskName eq 'taskprocessEditInitialized' }" >
      <p><img title="" alt="" src="${baseURL}images/greenArrow.gif"><fmt:message key="WF.bundle.completebundleandsendtolibrary" />
      </p>
      
      <mcr:checkDecisionNode var="transition" processID="${requestScope.task.processID}" workflowType="bundle" decision="canBundleBeSubmitted" />
      
      <c:import url="/content/workflow/editorButtons.jsp" />
      <p>                              
	      <c:if test="${transition eq 'bundleCanBeSubmitted'}">
	        <img title="" alt="" src="${baseURL}images/greenArrow.gif">         
            <a href="${baseURL}nav?path=~bundle&transition=&endTask=taskprocessEditInitialized&processID=${requestScope.task.processID}"><fmt:message key="WF.bundle.taskCompleteBundleAndSendToLibrary" /></a>
	      </c:if>      
	  </p>    
   </c:when>
   <c:when test="${requestScope.task.taskName eq 'taskvolumeEditInitialized' }" >
      <p><img title="" alt="" src="${baseURL}images/greenArrow.gif"><fmt:message key="WF.bundle.completevolumeandsendtolibrary" />
      </p>
      
      <mcr:checkDecisionNode var="transition" processID="${requestScope.task.processID}" workflowType="bundle" decision="canVolumeBeSubmitted" />
      
      <c:import url="/content/workflow/editorButtons.jsp" />
      <p>                              
	      <c:if test="${transition eq 'volumeCanBeSubmitted'}">
	        <img title="" alt="" src="${baseURL}images/greenArrow.gif">         
            <a href="${baseURL}nav?path=~bundle&transition=&endTask=taskvolumeEditInitialized&processID=${requestScope.task.processID}"><fmt:message key="WF.bundle.taskCompleteVolumeAndSendToLibrary" /></a>
	      </c:if>      
	  </p>    
   </c:when>
   
   <c:when test="${requestScope.task.taskName eq 'taskCompleteBundleAndSendToLibrary'}">
      <p><img title="" alt="" src="${baseURL}images/greenArrow.gif"><fmt:message key="WF.bundle.completebundleandsendtolibrary" />
      </p>
      
      <mcr:checkDecisionNode var="transition" processID="${requestScope.task.processID}" workflowType="bundle" decision="canBundleBeSubmitted" />
      
      <c:import url="/content/workflow/editorButtons.jsp" />
      
	  <p>
	      <c:if test="${transition eq 'bundleCanBeSubmitted'}">
		     <img title="" alt="" src="${baseURL}images/greenArrow.gif">         
	         <a href="${baseURL}nav?path=~bundle&transition=&endTask=taskCompleteBundleAndSendToLibrary&processID=${requestScope.task.processID}"><fmt:message key="WF.bundle.taskCompleteBundleAndSendToLibrary" /></a>
	      </c:if>      
	  </p>    
   </c:when>
   
   <c:when test="${requestScope.task.taskName eq 'taskGetInitiatorsEmailAddress'}">
   	  <p><fmt:message key="WF.common.getInitiatorsEmailAddress" /></p>
	     <c:import url="/content/workflow/setworkflowvariable.jsp" >
	     	<c:param name="workflowType" value="bundle" />
	     	<c:param name="endTask" value="taskGetInitiatorsEmailAddress" />
	     </c:import>
   </c:when>   

   <c:when test="${requestScope.task.taskName eq 'taskCheckCompleteness'}">
      <c:import url="/content/workflow/editorButtons.jsp" />
      <mcr:checkDecisionNode var="transition" processID="${requestScope.task.processID}" workflowType="bundle" decision="canBundleBeCommitted" />
         <p><fmt:message key="WF.common.AreTheMetadataOK" /></p>
         <p><img title="" alt="" src="${baseURL}images/greenArrow.gif">
         <a href="${baseURL}nav?path=~bundle&transition=go2canBundleBeCommitted&endTask=taskCheckCompleteness&processID=${requestScope.task.processID}"><fmt:message key="WF.common.MetadataOk_Continue" /></a>
         </p>
         <p><img title="" alt="" src="${baseURL}images/greenArrow.gif">
         <a href="${baseURL}nav?path=~bundle&transition=go2sendBackToBundleCreated&endTask=taskCheckCompleteness&processID=${requestScope.task.processID}"><fmt:message key="WF.common.MetadataNotOk_SendToInitiator" /></a>
         </p>                                              
      </c:when>
   
   <c:when test="${requestScope.task.taskName eq 'taskEnterMessageData'}">
	     <c:import url="/content/workflow/setworkflowvariable.jsp" >
	     	<c:param name="workflowType" value="bundle" />
	     	<c:param name="endTask" value="taskentermessagedata" />
	     </c:import>
   </c:when>
   <c:when test="${requestScope.task.taskName eq 'taskEnterVolumeMessageData'}">
	     <c:import url="/content/workflow/setworkflowvariable.jsp" >
	     	<c:param name="workflowType" value="bundle" />
	     	<c:param name="endTask" value="taskentervolumemessagedata" />
	     </c:import>
   </c:when>
   
   <c:when test="${requestScope.task.taskName eq 'taskAdminCheckCommitmentNotSuccessFul'}">
      <p>
      <a href="${baseURL}nav?path=~bundle&transition=go2bundleCommitted&endTask=taskAdminCheckCommitmentNotSuccessFul&processID=${requestScope.task.processID}"><fmt:message key="Nav.Application.dissertation.sendAffirmationOfSubmission" /></a><br>      
      </p>
   </c:when>
   <c:when test="${requestScope.task.taskName eq 'taskAdminCheckVolumeCommitmentNotSuccessFul'}">
      <p>
      <a href="${baseURL}nav?path=~bundle&transition=go2volumeCommitted&endTask=taskAdminCheckVolumeCommitmentNotSuccessFul&processID=${requestScope.task.processID}"><fmt:message key="Nav.Application.dissertation.sendAffirmationOfSubmission" /></a><br>      
      </p>
   </c:when>
   <c:otherwise>
    <p> what else? TASK = ${requestScope.task.taskName} </p>
   </c:otherwise>
</c:choose>
