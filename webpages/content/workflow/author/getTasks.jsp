<%@ page import="org.mycore.frontend.workflowengine.jbpm.MCRWorkflowConstants" %>
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
   <fmt:message key="WF.author.Author" /> <fmt:message key="WF.common.Processnumber" /> <b>${requestScope.task.processID}</b>: <br>
</c:if>
<c:choose>
   <c:when test="${requestScope.task.taskName eq 'initialization'}">
      <fmt:message key="WF.author.ActualStateOfYourAuthor" />
      (<fmt:message key="WF.common.Processnumber" /> <b>${requestScope.task.processID}</b>): <br>
       <b><fmt:message key="WF.author.status.${requestScope.task.workflowStatus}" /></b>
       <br />&nbsp;<br />
   </c:when>


    <c:when test="${requestScope.task.taskName eq 'taskInputAuthorEqualsInitator'}">
		<p>
	  <fmt:message key="WF.author.whatToDo" />
      <br>&nbsp;<br>
      <img title="" alt="" src="${baseURL}images/greenArrow.gif">                         
      <a href="${baseURL}nav?path=~author&transition=go2CreateAuthorFromInitiator&endTask=taskInputAuthorEqualsInitator&processID=${requestScope.task.processID}"><fmt:message key="WF.author.iAmTheAuthor" /></a>
      <br>                                                   
      <img title="" alt="" src="${baseURL}images/greenArrow.gif">      
      <a href="${baseURL}nav?path=~author&transition=go2CreateNewAuthor&endTask=taskInputAuthorEqualsInitator&processID=${requestScope.task.processID}"><fmt:message key="WF.author.iAmNotTheAuthor" /></a>
      </p>
   </c:when>   
   
   <c:when test="${requestScope.task.taskName eq 'displayAuthorForUser'}">
 	    <mcr:getWorkflowEngineVariable 
			pid="${requestScope.task.processID}" var="objid" 
			workflowVar="<%= MCRWorkflowConstants.WFM_VAR_METADATA_OBJECT_IDS %>" /> 
 	   <mcr:getWorkflowEngineVariable 
			pid="${requestScope.task.processID}" var="wfoTitle" 
			workflowVar="<%= MCRWorkflowConstants.WFM_VAR_WFOBJECT_TITLE %>" />  
    	<%-- alternativ:
	   	<c:set var="authorID"><x:out select="$dom/variables/variable[@name = 'authorID']/@value" /></c:set> --%>
		<br>&nbsp;<br>
	    <fmt:message key="WF.author.AuthorForUserExists">
			<fmt:param>${objid}</fmt:param>                             
	    </fmt:message>
		 <tr>
	         <td>
    	       <b><c:out value="${wfoTitle}" /></b>  	
        	 </td>
			 <td width="50">	&nbsp;		
			 </td>
			 <td align="right">
				<form method="get" action="${baseURL}nav">
					<input value="~workflow-preview" name="path" type="hidden" />
					<input name="id" value="${objid}" type="hidden" />
					<input name="fromWForDB" value="workflow" type="hidden"/>
					<input title="<fmt:message key="WF.common.object.Preview" />" src="${baseURL}images/workflow_objpreview.gif" type="image" class="imagebutton" />
				</form>
			</td>
		</tr>

    <br />&nbsp;<br />
	    <img title="" alt="" src="${baseURL}images/greenArrow.gif">      
    	<a href="${baseURL}nav?path=~author&transition=go2End&endTask=displayAuthorForUser&processID=${requestScope.task.processID}"><fmt:message key="WF.author.finishWorkflow" /></a>
   </c:when>
   
  <c:when test="${requestScope.task.taskName eq 'taskCompleteAuthorAndSendToLibrary'}">
      <p>
         <img title="" alt="" src="${baseURL}images/greenArrow.gif">
         <fmt:message key="WF.author.completeauthorandsendtolibrary" />
         <br />
       </p>  
	<mcr:checkDecisionNode var="transition" processID="${requestScope.task.processID}" workflowType="author" decision="canAuthorBeSubmitted" />
      <c:import url="/content/workflow/editorButtons.jsp" />
     <p>
         
         <c:if test="${transition eq 'authorCanBeSubmitted'}">
	        <img title="" alt="" src="${baseURL}images/greenArrow.gif">         
            <a href="${baseURL}nav?path=~author&transition=go2canAuthorBeSubmitted&endTask=taskCompleteAuthorAndSendToLibrary&processID=${requestScope.task.processID}"><fmt:message key="WF.author.taskCompleteAuthorAndSendToLibrary" /></a>
            <br />
         </c:if>     
      </p>
     
   </c:when> 

  <c:when test="${requestScope.task.taskName eq 'taskDisplayAuthorData'}">
      <p>
         <img title="" alt="" src="${baseURL}images/greenArrow.gif">
         <fmt:message key="WF.author.editAuthor" />
         <br />
       </p>
      <c:import url="/content/workflow/editorButtons.jsp" />
      <p>
         <mcr:checkDecisionNode var="transition" processID="${requestScope.task.processID}" workflowType="author" decision="canChangesBeCommitted" />
         <c:if test="${transition eq 'changesCanBeCommitted'}">
	        <img title="" alt="" src="${baseURL}images/greenArrow.gif">         
            <a href="${baseURL}nav?path=~author&transition=go2canChangesBeCommitted&endTask=taskDisplayAuthorData&processID=${requestScope.task.processID}"><fmt:message key="WF.author.taskCommitChanges" /></a>
            <br />
         </c:if>     
      </p>
   </c:when> 
   
   <c:when test="${requestScope.task.taskName eq 'taskGetInitiatorsEmailAddress'}">
      <p>
         <fmt:message key="WF.common.getInitiatorsEmailAddress" />
		 <br>&nbsp;<br>
	     <c:import url="/content/workflow/setworkflowvariable.jsp" >
	     	<c:param name="workflowType" value="author" />
	     	<c:param name="endTask" value="taskGetInitiatorsEmailAddress" />
	     </c:import>
     </p>
   </c:when>   

   <c:when test="${requestScope.task.taskName eq 'taskCheckCompleteness'}">
      <c:import url="/content/workflow/editorButtons.jsp" />
      <mcr:checkDecisionNode var="transition" processID="${requestScope.task.processID}" workflowType="author" decision="canAuthorBeCommitted" />
         <br>&nbsp;<br>
         <fmt:message key="WF.common.AreTheMetadataOK" />
         <br>&nbsp;<br>
		 <c:if test="${transition eq 'authorCanBeCommitted'}">
	        <img title="" alt="" src="${baseURL}images/greenArrow.gif">         
            <a href="${baseURL}nav?path=~author&transition=go2canAuthorBeCommitted&endTask=taskCheckCompleteness&processID=${requestScope.task.processID}"><fmt:message key="WF.common.MetadataOk_Continue" /></a>
            <br>
         </c:if>     
         <br>
         <img title="" alt="" src="${baseURL}images/greenArrow.gif">
         <a href="${baseURL}nav?path=~author&transition=go2sendBackToAuthorCreated&endTask=taskCheckCompleteness&processID=${requestScope.task.processID}"><fmt:message key="WF.common.MetadataNotOk_SendToInitiator" /></a>
         <br>
   </c:when>   

   <c:when test="${requestScope.task.taskName eq 'taskEnterMessageData'}">
	     <c:import url="/content/workflow/setworkflowvariable.jsp" >
	     	<c:param name="workflowType" value="author" />
	     	<c:param name="endTask" value="taskentermessagedata" />
	     </c:import>
   </c:when>

   <c:when test="${requestScope.task.taskName eq 'taskAdminCheckCommitmentNotSuccessFul'}">
      <a href="${baseURL}nav?path=~author&transition=go2authorCommitted2&endTask=taskAdminCheckCommitmentNotSuccessFul&processID=${requestScope.task.processID}"><fmt:message key="WF.author.admin.authorValidated" /></a><br>      
   </c:when>
   
   <c:otherwise>
    <h1>what else? TODO</h1>
   </c:otherwise>
</c:choose>
