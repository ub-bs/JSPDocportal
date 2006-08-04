<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>
<c:choose>
   <c:when test="${!empty(param.debug)}">
      <c:set var="debug" value="true" />
   </c:when>
   <c:when test="${!empty(requestScope.debug)}">
      <c:set var="debug" value="true" />
   </c:when>   
   <c:otherwise>
      <c:set var="debug" value="false" />
   </c:otherwise>
</c:choose>
<mcr:session method="get" var="username" type="userID" />
<c:set  var="baseURL" value="${applicationScope.WebApplicationBaseURL}"/>

<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages' />

<c:set var="processid" value="${requestScope.task.processID}" />
<c:set var="dom" value="${requestScope.task.variables}" />
<c:set var="itemID"><x:out select="$dom/variables/variable[@name = 'createdDocID']/@value" /></c:set>
<c:set var="itemDocType" value="${fn:split(itemID,'_')[1]}" />
<c:set var="attachedDerivates"><x:out select="$dom/variables/variable[@name = 'attachedDerivates']/@value" /></c:set> 
<c:set var="wfoTitle"><x:out select="$dom/variables/variable[@name = 'wfo-title']/@value" /></c:set>

<c:if test="${debug}">
	processid   <c:out value="${processid}" /><br>
	itemid   <c:out value="${itemID}" /><br>
	docType <c:out value="${itemDocType}" /><br>
	attachedDerivates   <c:out value="${attachedDerivates}" /><br>		
</c:if> 
    
<mcr:listWorkflowDerivates varDom="der" docID="${itemID}" derivates="${attachedDerivates}" workflowprocesstype="${itemDocType}" />

<table width="100%" class="task" >
	<tr>
		<td class="resultTitle"><b><c:out value="${wfoTitle}" /></b></td>
		<td width="30">&nbsp;</td>
		<td align="right">
		<table>
			<tr>
				<mcr:checkAccess var="modifyAllowed" permission="writedb"	key="${itemID}" />
				
				<c:if test="${modifyAllowed}">
					<c:if test="${fn:contains('document,disshab,professorum',itemDocType)}">
						<td align="center" valign="top" width="30">
						<form method="get" action="${baseURL}workflowaction">
							<input	name="processid" value="${processid}" type="hidden"> 
							<input	name="todo" value="WFAddNewDerivateToWorkflowObject" type="hidden"> 
							<input	title="<fmt:message key="WF.common.derivate.AddDerivate" />"src="${baseURL}images/workflow_derivateadd.gif" type="image"			
								    class="imagebutton">
						</form>
						</td>
					</c:if>
					<td align="center" valign="top" width="30">
					<form method="get" action="${baseURL}workflowaction">
						<input	name="processid" value="${processid}" type="hidden"> 
						<input	name="todo" value="WFEditWorkflowObject" type="hidden"> 
						<input	title="<fmt:message key="WF.common.object.EditObject" />"
							src="${baseURL}images/workflow_objedit.gif" type="image"
							class="imagebutton">
					</form>
					</td>
				</c:if>

				<c:if test="${itemDocType == 'disshab' }">
					<td align="center" valign="top" width="30">
					<form method="get"	action="${baseURL}content/results-config/docdetails-disshab-deliver.jsp"	target="new">
						<input name="id" value="${itemID}" type="hidden"> 
						<input name="fromWForDB" value="workflow" type="hidden"> 
						<input title="<fmt:message key="WF.common.object.DisshabPreview" />"
							src="${baseURL}images/workflow_disshabpreview.gif" type="image"
							class="imagebutton">
					</form>
					</td>
				</c:if>
				<td align="center" valign="top" width="30">
				<form method="get" action="${baseURL}nav">
					<input	value="~workflow-preview" name="path" type="hidden"> 
					<input	name="id" value="${itemID}" type="hidden"> 
					<input name="fromWForDB" value="workflow" type="hidden"> 
					<input	title="<fmt:message key="WF.common.object.Preview" />"
						src="${baseURL}images/workflow_objpreview.gif" type="image"
						class="imagebutton">
				</form>
				</td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td class="description" colspan="3">
		 <fmt:message key="WF.${requestScope.task.workflowProcessType}.Description" />, ${itemID}
		 <br/>
		 <mcr:getWorkflowEngineVariable pid="${requestScope.task.processID}" var="error" workflowVar="varnameERROR" /> 
	     <font color="red">${error}</font><br/>	         		 
		</td>
	</tr>

	<x:forEach select="$der/derivates/derivate">
		<x:set var="derivateID" select="string(./@ID)" />
		<x:set var="derivateLabel" select="string(./@label)" />
	    <mcr:checkAccess var="modifyAllowed" permission="writedb"	key="${derivateID}" /> 
		<tr>
			<td align="left" valign="top"><b>${derivateLabel}</b></td>
			<td width="30">&nbsp;</td>
			<td align="right">
			  <c:if	test="${modifyAllowed}">
				<table>
					<tr>
						<td align="center" valign="top" width="30">
						<form method="get" action="${baseURL}workflowaction">
							<input	name="derivateID" value="${derivateID}" type="hidden"> 
							<input	name="processid" value="${processid}" type="hidden"> 
							<input	name="todo" value="WFAddNewFileToDerivate" type="hidden"> 
							<input	title="<fmt:message key="WF.common.derivate.AddFile" />"
								src="${baseURL}images/workflow_derivatenew.gif" type="image"
								class="imagebutton">
						</form>
						</td>
						<td align="center" valign="top" width="30">
						<form method="get" action="${baseURL}workflowaction">
							<input	name="derivateID" value="${derivateID}" type="hidden"> 
							<input	name="processid" value="${processid}" type="hidden"> 							
							<input	name="todo" value="WFEditDerivateFromWorkflowObject" type="hidden"> 
							<input	title="<fmt:message key="WF.common.derivate.EditDerivate" />"
								src="${baseURL}images/workflow_derivateedit.gif" type="image"
								border="0" class="imagebutton">
						</form>
						</td>
						<td align="center" valign="top" width="30">
						<form method="get" action="${baseURL}workflowaction">
							<input	name="derivateID" value="${derivateID}" type="hidden"> 
							<input	name="processid" value="${processid}" type="hidden"> 
							<input	name="todo" value="WFRemoveDerivateFromWorkflowObject" type="hidden"> 
							<input	title="<fmt:message key="WF.common.derivate.DelDerivate" />"
								src="${baseURL}images/workflow_derivatedelete.gif" type="image"
								border="0" class="imagebutton">
						</form>
						</td>
					</tr>
				</table>
			</c:if></td>
		</tr>

		<x:set var="numFiles" select="count(./file)" />
		<x:forEach select="file">
			<x:set var="fileSize" select="string(./@size)" />
			<tr valign="top">
				<td>
				 <!--  http://139.30.48.72:8080/mycoresample/file/DocPortal_derivate_00000008/dissertation.pdf?hosts=local -->
				 <a class="linkButton"				
					href="${baseURL}file/<x:out select="."/>?type=${itemDocType}"
					target="_blank"> <x:out select="." /> </a> [ ${fileSize} ]</td>
				<td width="30">&nbsp;</td>
				<td>
					<c:if test="${modifyAllowed && numFiles gt 1}">
					<table>
						<tr>
							<td align="center" valign="top" width="30">
							<form method="post" action="${baseURL}workflowaction">
								<input	name="derivateID" value="${derivateID}" type="hidden"> 
								<input	name="processid" value="${processid}" type="hidden"> 
								<input	value="WFRemoveFileFromDerivate" name="todo" type="hidden"> 
								<input	name="filename" value="<x:out select="."/>" type="hidden"> 
								<input	title="Löschen dieser Datei" src="${baseURL}images/delete.png"
									type="image" class="imagebutton">
							</form>
							</td>
						</tr>
					</table>
				</c:if></td>
			</tr>
		</x:forEach>

	</x:forEach>
	<tr>
		<td colspan="2">&nbsp;</td>
		<td align="right">
		<table>
			<tr>
				<mcr:checkAccess var="modifyAllowed" permission="deletewf"	key="${itemID}" />
				<c:if test="${modifyAllowed}">
					<td align="center" valign="top" width="30">
					<form method="get" action="${baseURL}workflowaction">
						<input	name="processid" value="${processid}" type="hidden"> 
						<input	name="todo" value="WFDeleteWorkflowObject" type="hidden"> 
						<input	title="<fmt:message key="WF.common.object.DelWFObject" />"
							src="${baseURL}images/workflow_objdelete.gif" type="image"
							class="imagebutton">
					</form>
					</td>
				</c:if>

				<mcr:checkAccess var="modifyAllowed" permission="deletedb"	key="${itemID}" />
				<c:if test="${modifyAllowed}">
					<td align="center" valign="top" width="30">
					<form method="get" onSubmit="return reallyDeletefromDB();" action="${baseURL}workflowaction">
						<input name="processid"	value="${processid}" type="hidden"> 
						<input name="todo" value="WFDeleteObject" type="hidden"> 
						<input onClick="return reallyDeletefromDB();" title="<fmt:message key="WF.common.object.DelObject" />"
							src="${baseURL}images/database_objdelete.gif" type="image"
							class="imagebutton">
					</form>
					</td>
				</c:if>
			</tr>
		</table>

		</td>
	</tr>
</table>


