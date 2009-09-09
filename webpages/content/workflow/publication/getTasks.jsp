<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>
<%@ page pageEncoding="UTF-8" %>

<mcr:session method="get" var="username" type="userID" />
<mcr:session var="sessionid" method="get" type="ID"/>

<c:set  var="baseURL" value="${applicationScope.WebApplicationBaseURL}"/>

<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages' />
<c:set var="debug" value="true" />
<c:set var="dom" value="${requestScope.task.variables}" />
<!-- 
<b><c:out value="${requestScope.task.taskName}"></c:out></b><br/>
 -->
<c:if test="${requestScope.task.taskName ne 'initialization'}">
   <fmt:message key="WF.publication.Publication" /> <fmt:message key="WF.common.Processnumber" /> <b>${requestScope.task.processID}</b>: <br>
</c:if>
<c:choose>
   <c:when test="${requestScope.task.taskName eq 'initialization' }">
      <p><fmt:message key="WF.publication.ActualStateOfYourDocument" />(<fmt:message key="WF.common.Processnumber" /> <b>${requestScope.task.processID}</b>): 
	  </p>
	  <p><b><fmt:message key="WF.publication.status.${requestScope.task.workflowStatus}" /></b></p>
   </c:when>
	<c:when	test="${requestScope.task.taskName eq 'taskGetPublicationType'}">
		<p><fmt:message key="WF.publication.GetPublicationType" /></p>
		 <c:import url="/content/workflow/setworkflowvariable.jsp" >
	     	<c:param name="workflowType" value="publication" />
	     	<c:param name="endTask" value="taskGetPublicationType" />	     	
	     </c:import>
		
		<%-- <c:url var="url" value="${WebApplicationBaseURL}editor/workflow/getPublicationTypes.xml">
			<c:param name="dispatcherForward" value="/nav?path=~publication" />
			<c:param name="transition" value="" />
			<c:param name="endTask" value="taskGetPublicationType" />
			<c:param name="processID" value="${requestScope.task.processID}" />
			<c:param name="jbpmVariableNames" value="/publication/Type"	 />
			<c:param name="XSL.editor.source.new" value="true" />
			<c:param name="XSL.editor.cancel.url" value="/nav?path=~publication" />
		    <c:param name="MCRSessionID" value="${sessionid}" />
			<c:param name="lang" value="${requestScope.lang}" />
		</c:url>
		<c:import url="${url}" /> --%>
		
	</c:when>
	<c:when	test="${requestScope.task.taskName eq 'taskprocessInitialized' }">
		<mcr:getWorkflowEngineVariable pid="${requestScope.task.processID}" var="urn" workflowVar="reservatedURN" /> 
		<mcr:getWorkflowEngineVariable pid="${requestScope.task.processID}" var="docID" workflowVar="createdDocID" /> 
	    <mcr:getWorkflowEngineVariable pid="${requestScope.task.processID}" var="docType" workflowVar="wfo-type" /> 
	    
		<table cellspacing="3" cellpadding="3" >
		<c:if test="${not empty docID}">
		   <tr valign="top">
		        <td class="metaname"><fmt:message key="WF.publication.ID" /><br/></td>
		        <td class="metavalue"> <b><c:out value="${docID}" /></b>
		      </td>
		   </tr>     
		</c:if>		
		<c:if test="${not empty docType}">
		   <tr valign="top">
		        <td class="metaname"><fmt:message key="WF.publication.Type" /><br/></td>
		        <td class="metavalue"><b><c:out value="${docType}" /></b>  <br/>
		      </td>
		   </tr>     
		</c:if>
		<c:if test="${not empty urn}">
		   <tr valign="top">
		        <td class="metaname"><fmt:message key="WF.publication.URN" /><br/></td>
		        <td class="metavalue"><b><c:out value="${urn}" /></b> 
		         <br/> <small><i><fmt:message key="WF.publication.URN.Hinweis" /></i></small>
		      </td>
		   </tr>     
		</c:if>
	      <tr>
		    <td colspan="2">
		      <fmt:message key="WF.publication.Created" /><br/>
		      <fmt:message key="WF.publication.Created2" /><br/>
		      <br/>
		      <img title="" alt="" src="${baseURL}images/greenArrow.gif">
		      <a href="${baseURL}nav?path=~publication&transition=&endTask=taskprocessInitialized&processID=${requestScope.task.processID}">		         
		         <fmt:message key="WF.common.object.EditObject" /></a>
		    </td>
		  </tr> 
		</table>	
	</c:when>
   <c:when test="${requestScope.task.taskName eq 'taskprocessEditInitialized' }" >
	   <p><img title="" alt="" src="${baseURL}images/greenArrow.gif"><fmt:message key="WF.publication.completedocumentandsendtolibrary" />
       </p>
       
       <mcr:checkDecisionNode var="transition" processID="${requestScope.task.processID}" workflowType="publication" decision="canDocumentBeSubmitted" />
       
       <c:import url="/content/workflow/editorButtons.jsp" />
       
       <p>
	       <c:if test="${transition eq 'documentCanBeSubmitted'}">
	       	  <img title="" alt="" src="${baseURL}images/greenArrow.gif">         
	          <a href="${baseURL}nav?path=~publication&transition=&endTask=taskprocessEditInitialized&processID=${requestScope.task.processID}"><fmt:message key="WF.publication.taskCompleteDocumentAndSendToLibrary" /></a>
	       </c:if>      
       </p>
   </c:when>
 
   <c:when test="${requestScope.task.taskName eq 'taskGetInitiatorsEmailAddress'}" >
   		 <p> <fmt:message key="WF.common.getInitiatorsEmailAddress" />	 </p>
	     <c:import url="/content/workflow/setworkflowvariable.jsp" >
	     	<c:param name="workflowType" value="publication" />
	     	<c:param name="endTask" value="taskGetInitiatorsEmailAddress" />
	     </c:import>
   </c:when>   
   
   <c:when test="${requestScope.task.taskName eq 'taskCompleteDocumentAndSendToLibrary' }" >
   		 <p><img title="" alt="" src="${baseURL}images/greenArrow.gif"><fmt:message key="WF.publication.completedocumentandsendtolibrary" />
         </p>         
         <mcr:checkDecisionNode var="transition" processID="${requestScope.task.processID}" workflowType="publication" decision="canDocumentBeSubmitted" />
         <c:import url="/content/workflow/editorButtons.jsp" />

         <p>
	         <c:if test="${transition eq 'documentCanBeSubmitted'}">
		        <img title="" alt="" src="${baseURL}images/greenArrow.gif">         
	            <a href="${baseURL}nav?path=~publication&transition=&endTask=taskCompleteDocumentAndSendToLibrary&processID=${requestScope.task.processID}"><fmt:message key="WF.publication.taskCompleteDocumentAndSendToLibrary" /></a>
	         </c:if>  
         </p>    
   </c:when>
   
   <c:when test="${requestScope.task.taskName eq 'taskCheckCompleteness'}">
      <c:import url="/content/workflow/editorButtons.jsp" />
      <mcr:checkDecisionNode var="transition" processID="${requestScope.task.processID}" workflowType="publication" decision="canDocumentBeCommitted" />
         <p><fmt:message key="WF.common.AreTheMetadataOK" /></p>
         <p><img title="" alt="" src="${baseURL}images/greenArrow.gif">
         	<a href="${baseURL}nav?path=~publication&transition=go2canDocumentBeCommitted&endTask=taskCheckCompleteness&processID=${requestScope.task.processID}"><fmt:message key="WF.common.MetadataOk_Continue" /></a>
         </p>
         <p><img title="" alt="" src="${baseURL}images/greenArrow.gif">
         	<a href="${baseURL}nav?path=~publication&transition=go2sendBackToDocumentCreated&endTask=taskCheckCompleteness&processID=${requestScope.task.processID}"><fmt:message key="WF.common.MetadataNotOk_SendToInitiator" /></a>
         </p>
   </c:when>   
   <c:when test="${requestScope.task.taskName eq 'taskEnterMessageData'}">
	     <c:import url="/content/workflow/setworkflowvariable.jsp" >
	     	<c:param name="workflowType" value="publication" />
	     	<c:param name="endTask" value="taskentermessagedata" />
	     </c:import>
   </c:when>
   <c:when test="${requestScope.task.taskName eq 'taskAdminCheckCommitmentNotSuccessFul'}">
     
      <a href="${baseURL}nav?path=~publication&transition=go2documentCommitted&endTask=taskAdminCheckCommitmentNotSuccessFul&processID=${requestScope.task.processID}"><fmt:message key="Nav.Application.dissertation.sendAffirmationOfSubmission" /></a><br>      
   </c:when>
   <c:otherwise>
    <p> what else? TASK = ${requestScope.task.taskName} </p>
   </c:otherwise>
</c:choose>
