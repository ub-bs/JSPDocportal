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
   <fmt:message key="WF.series.Series" /> <fmt:message key="WF.common.Processnumber" /> <b>${requestScope.task.processID}</b>: <br>
</c:if>
<c:choose>
   <c:when test="${requestScope.task.taskName eq 'initialization'}">
      <p><fmt:message key="WF.series.ActualStateOfYourSeries" />(<fmt:message key="WF.common.Processnumber" /> <b>${requestScope.task.processID}</b>): 
      </p>
      <p><b><fmt:message key="WF.series.status.${requestScope.task.workflowStatus}" /></b>
      </p>
   </c:when>
   <c:when test="${requestScope.task.taskName eq 'taskprocessEditInitialized' }" >
      <p><img title="" alt="" src="${baseURL}images/greenArrow.gif"><fmt:message key="WF.series.completeseriesandsendtolibrary" />
      </p>
      
      <mcr:checkDecisionNode var="transition" processID="${requestScope.task.processID}" workflowType="series" decision="canSeriesBeSubmitted" />
      
      <c:import url="/content/workflow/editorButtons.jsp" />
      <p>                              
	      <c:if test="${transition eq 'seriesCanBeSubmitted'}">
	        <img title="" alt="" src="${baseURL}images/greenArrow.gif">         
            <a href="${baseURL}nav?path=~series&transition=&endTask=taskprocessEditInitialized&processID=${requestScope.task.processID}"><fmt:message key="WF.series.taskCompleteSeriesAndSendToLibrary" /></a>
	      </c:if>      
	  </p>    
   </c:when>
   <c:when test="${requestScope.task.taskName eq 'taskvolumeEditInitialized' }" >
      <p><img title="" alt="" src="${baseURL}images/greenArrow.gif"><fmt:message key="WF.series.completevolumeandsendtolibrary" />
      </p>
      
      <mcr:checkDecisionNode var="transition" processID="${requestScope.task.processID}" workflowType="series" decision="canVolumeBeSubmitted" />
      
      <c:import url="/content/workflow/editorButtons.jsp" />
      <p>                              
	      <c:if test="${transition eq 'volumeCanBeSubmitted'}">
	        <img title="" alt="" src="${baseURL}images/greenArrow.gif">         
            <a href="${baseURL}nav?path=~series&transition=&endTask=taskvolumeEditInitialized&processID=${requestScope.task.processID}"><fmt:message key="WF.series.taskCompleteVolumeAndSendToLibrary" /></a>
	      </c:if>      
	  </p>    
   </c:when>
   
   <c:when test="${requestScope.task.taskName eq 'taskCompleteSeriesAndSendToLibrary'}">
      <p><img title="" alt="" src="${baseURL}images/greenArrow.gif"><fmt:message key="WF.series.completeseriesandsendtolibrary" />
      </p>
      
      <mcr:checkDecisionNode var="transition" processID="${requestScope.task.processID}" workflowType="series" decision="canSeriesBeSubmitted" />
      
      <c:import url="/content/workflow/editorButtons.jsp" />
      
	  <p>
	      <c:if test="${transition eq 'seriesCanBeSubmitted'}">
		     <img title="" alt="" src="${baseURL}images/greenArrow.gif">         
	         <a href="${baseURL}nav?path=~series&transition=&endTask=taskCompleteSeriesAndSendToLibrary&processID=${requestScope.task.processID}"><fmt:message key="WF.series.taskCompleteSeriesAndSendToLibrary" /></a>
	      </c:if>      
	  </p>    
   </c:when>
   <c:when test="${requestScope.task.taskName eq 'taskCompleteVolumeAndSendToLibrary'}">
      <p><img title="" alt="" src="${baseURL}images/greenArrow.gif"><fmt:message key="WF.series.completesvolumeandsendtolibrary" />
      </p>
      
      <mcr:checkDecisionNode var="transition" processID="${requestScope.task.processID}" workflowType="series" decision="canVolumeBeSubmitted" />
      
      <c:import url="/content/workflow/editorButtons.jsp" />
      
	  <p>
	      <c:if test="${transition eq 'volumeCanBeSubmitted'}">
		     <img title="" alt="" src="${baseURL}images/greenArrow.gif">         
	         <a href="${baseURL}nav?path=~series&transition=&endTask=taskCompleteVolumeAndSendToLibrary&processID=${requestScope.task.processID}"><fmt:message key="WF.series.taskCompleteVolumeAndSendToLibrary" /></a>
	      </c:if>      
	  </p>    
   </c:when>   
   
   <c:when test="${requestScope.task.taskName eq 'taskGetInitiatorsEmailAddress'}">
   	  <p><fmt:message key="WF.common.getInitiatorsEmailAddress" /></p>
	     <c:import url="/content/workflow/setworkflowvariable.jsp" >
	     	<c:param name="workflowType" value="series" />
	     	<c:param name="endTask" value="taskGetInitiatorsEmailAddress" />
	     </c:import>
   </c:when>   

   <c:when test="${requestScope.task.taskName eq 'taskGetVolumeInitiatorsEmailAddress'}">
   	  <p><fmt:message key="WF.common.getInitiatorsEmailAddress" /></p>
	       <form action="${baseURL}setworkflowvariable" accept-charset="utf-8">
    	     <input name="dispatcherForward" value="/nav?path=~series" type="hidden" />
        	 <input name="transition" value="" type="hidden" />
	         <input name="endTask" value="taskGetVolumeInitiatorsEmailAddress" type="hidden" />
    	     <input name="processID" value="${requestScope.task.processID}" type="hidden" />
    	     <input name="jbpmVariableNames" value="initiatorEmail" type="hidden" />
        	 <input type="text" size="80" name="initiatorEmail">
        	 <br>&nbsp;<br>
         	 <input name=submit" type="submit" value="<fmt:message key="WF.common.Send" />"/>      
	     </form>	
   </c:when>   


   <c:when test="${requestScope.task.taskName eq 'taskCheckCompleteness'}">
      <c:import url="/content/workflow/editorButtons.jsp" />
      <mcr:checkDecisionNode var="transition" processID="${requestScope.task.processID}" workflowType="series" decision="canSeriesBeCommitted" />
         <p><fmt:message key="WF.common.AreTheMetadataOK" /></p>
         <p><img title="" alt="" src="${baseURL}images/greenArrow.gif">
         <a href="${baseURL}nav?path=~series&transition=go2canSeriesBeCommitted&endTask=taskCheckCompleteness&processID=${requestScope.task.processID}"><fmt:message key="WF.common.MetadataOk_Continue" /></a>
         </p>
         <p><img title="" alt="" src="${baseURL}images/greenArrow.gif">
         <a href="${baseURL}nav?path=~series&transition=go2sendBackToSeriesCreated&endTask=taskCheckCompleteness&processID=${requestScope.task.processID}"><fmt:message key="WF.common.MetadataNotOk_SendToInitiator" /></a>
         </p>                                              
      </c:when>
   
     <c:when test="${requestScope.task.taskName eq 'taskCheckVolumeCompleteness'}">
      <c:import url="/content/workflow/editorButtons.jsp" />
      <mcr:checkDecisionNode var="transition" processID="${requestScope.task.processID}" workflowType="series" decision="canVolumeBeCommitted" />
         <p><fmt:message key="WF.common.AreTheMetadataOK" /></p>
         <p><img title="" alt="" src="${baseURL}images/greenArrow.gif">
         <a href="${baseURL}nav?path=~series&transition=go2canVolumeBeCommitted&endTask=taskCheckVolumeCompleteness&processID=${requestScope.task.processID}"><fmt:message key="WF.common.MetadataOk_Continue" /></a>
         </p>
         <p><img title="" alt="" src="${baseURL}images/greenArrow.gif">
         <a href="${baseURL}nav?path=~series&transition=go2sendBackToVolumeCreated&endTask=taskCheckVolumeCompleteness&processID=${requestScope.task.processID}"><fmt:message key="WF.common.MetadataNotOk_SendToInitiator" /></a>
         </p>                                              
   </c:when>
      
  <c:when test="${requestScope.task.taskName eq 'taskEnterMessageData'}">
	     <c:import url="/content/workflow/setworkflowvariable.jsp" >
	     	<c:param name="workflowType" value="series" />
	     	<c:param name="endTask" value="taskentermessagedata" />
	     </c:import>
   </c:when>
   <c:when test="${requestScope.task.taskName eq 'taskEnterVolumeMessageData'}">
	     <c:import url="/content/workflow/setworkflowvariable.jsp" >
	     	<c:param name="workflowType" value="series" />
	     	<c:param name="endTask" value="taskentervolumemessagedata" />
	     </c:import>
   </c:when>
   
   <c:when test="${requestScope.task.taskName eq 'taskAdminCheckCommitmentNotSuccessFul'}">
      <p>
      <a href="${baseURL}nav?path=~series&transition=go2seriesCommitted&endTask=taskAdminCheckCommitmentNotSuccessFul&processID=${requestScope.task.processID}"><fmt:message key="Nav.Application.dissertation.sendAffirmationOfSubmission" /></a><br>      
      </p>
   </c:when>
   <c:when test="${requestScope.task.taskName eq 'taskAdminCheckVolumeCommitmentNotSuccessFul'}">
      <p>
      <a href="${baseURL}nav?path=~series&transition=go2volumeCommitted&endTask=taskAdminCheckVolumeCommitmentNotSuccessFul&processID=${requestScope.task.processID}"><fmt:message key="Nav.Application.dissertation.sendAffirmationOfSubmission" /></a><br>      
      </p>
   </c:when>
   <c:otherwise>
    <p> what else? TASK = ${requestScope.task.taskName} </p>
   </c:otherwise>
</c:choose>
