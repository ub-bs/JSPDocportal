<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.mycore.org/jspdocportal/base.tld" prefix="mcr" %>
<c:choose>
   <c:when test="${not empty param.debug}">
      <c:set var="debug" value="true" />
   </c:when>
   <c:when test="${not empty requestScope.debug}">
      <c:set var="debug" value="true" />
   </c:when>   
   <c:otherwise>
      <c:set var="debug" value="false" />
   </c:otherwise>
</c:choose>
<mcr:session method="get" var="username" type="userID" />
<c:set  var="baseURL" value="${applicationScope.WebApplicationBaseURL}"/>

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

<table width="100%" class="tasklistObject" >
	<tr>
		<td colspan="3">
		  <table>
			<tr>
				<mcr:checkAccess var="modifyAllowed" permission="writedb" key="${itemID}" />
				
				<c:if test="${modifyAllowed}">
					<c:if test="${fn:contains('document,disshab,thesis,series,person',itemDocType)}">
						<td align="center" valign="top" width="30">
						<form method="get" action="${baseURL}workflowaction">
							<input	name="processid" value="${processid}" type="hidden"> 
							<input	name="todo" value="WFAddNewDerivateToWorkflowObject" type="hidden"> 
							<input	title="<fmt:message key="WF.common.derivate.AddDerivate" />"src="${baseURL}images/workflow_derivateadd.gif" type="image"			
								    class="imagebutton">
						</form>
						</td>
					</c:if>
					
					<!-- Update from Formularserver for HS NB -->
					<c:if test="${fn:contains('disshab,thesis',itemDocType) && fn:contains(itemID, 'dbhsnb')}">
						<td align="center" valign="top" width="30">
						<form method="get"	action="${baseURL}nav">
							<input name="path" value="~importdata-thesis" type="hidden" />
							<input name="id" value="${itemID}" type="hidden" /> 
							<input name="returnPath" value="${path}" type="hidden" />
							<input title="<fmt:message key="WF.common.object.ImportFromFormserver" />"
									src="${baseURL}images/workflow_import_from_formserver.gif" type="image"
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
				
				<c:if test="${itemDocType == 'document' }">
					<td align="center" valign="top" width="30">
							<form method="get" action="${baseURL}workflowaction">
						<input	name="processid" value="${processid}" type="hidden"> 
						<input	name="todo" value="WFCreateURN" type="hidden"> 
						<input	title="<fmt:message key="WF.common.object.CreateURN" />"
							src="${baseURL}images/workflow_createurn.gif" type="image"
							class="imagebutton">
					</form>
					</td>
				</c:if>
				

				<c:if test="${itemDocType == 'disshab' && !fn:contains(itemID, 'dbhsnb') }">
					<td align="center" valign="top" width="30">
					<form method="get"	action="${baseURL}content/docdetails/docdetails_disshab-deliver.jsp"	target="new">
						<input name="id" value="${itemID}" type="hidden"> 
						<input name="fromWF" value="true" type="hidden"> 
						<input title="<fmt:message key="WF.common.object.DisshabAgreement" />"
							src="${baseURL}images/workflow_disshabpreview.gif" type="image"
							class="imagebutton">
					</form>
					</td>
				</c:if>
				<c:if test="${fn:contains('document,disshab,thesis,series,series-volume,author,institution',itemDocType)&& !fn:contains(itemID, 'dbhsnb') && !fn:contains(itemID, 'rosdok')}">
				<td align="center" valign="top" width="30">   								
    			<!-- 			<form method="get" action="${baseURL}content/workflow/accessruleeditor.jsp">--> <!--  target="new"> -->
				<form method="get" action="${baseURL}nav">
					<input	value="~workflow-editaccess" name="path" type="hidden"> 
					<input name="returnPath" value="${path}" type="hidden" />
					<input	name="id" value="${itemID}" type="hidden"> 
					<input	name="processid" value="${processid}" type="hidden"> 
					<input	title="<fmt:message key="Editor.Access.icondescr" />"
						src="${baseURL}images/workflow_accessedit.gif" type="image"
						class="imagebutton">
				</form>
				</td>
				</c:if>
				<td align="center" valign="top" width="30">
				<form method="get" action="${baseURL}nav">
					<input	value="~workflow-preview" name="path" type="hidden"> 
					<input	name="id" value="${itemID}" type="hidden"> 
					<input name="fromWF" value="true" type="hidden">  
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
	
		<td class="tasklistTitle"><c:out value="${wfoTitle}" /></td>
		<td width="30" rowspan="2">&nbsp;</td>
		<td align="right" rowspan="2">

		</td>
	</tr>
	<tr>
		<td class="description">
  	 	<fmt:message key="WF.${requestScope.task.workflowProcessType}.Description" />, ${itemID}
		<mcr:getWorkflowEngineVariable pid="${requestScope.task.processID}" var="authors" workflowVar="authorNames" />
		<c:if test="${not empty authors}">
			<br /><fmt:message key="Editor.Search.Label.creator" />:&nbsp;<i><c:out value="${authors}" /></i>
		</c:if>
		<mcr:getWorkflowEngineVariable pid="${requestScope.task.processID}" var="urn" workflowVar="reservatedURN" />
		<c:if test="${not empty urn}">
			  <br />[<fmt:message key="WF.xmetadiss.URN" />:&nbsp;<i><c:out value="${urn}" /></i>]
		</c:if>		 
		 <br/>
		 <mcr:getWorkflowEngineVariable pid="${requestScope.task.processID}" var="date" workflowVar="endOfSuspension" />
		<c:if test="${not empty date}">
			  <br />[<fmt:message key="WF.xmetadiss.suspendedUntil" /><b><c:out value="${date}" /></b>]
			  <br/>
		</c:if>		 
		 
		 <mcr:getWorkflowEngineVariable pid="${requestScope.task.processID}" var="error" workflowVar="varnameERROR" /> 
	     <font color="red">${error}</font>
	     <mcr:getWorkflowEngineVariable pid="${requestScope.task.processID}" var="hint" workflowVar="hint" />
		<c:if test="${not empty hint}">
			  <br /><font color="red"><c:out value="${hint}" /></font>
		</c:if>		  
	     <br/>	         
		</td>
	</tr>

	<tr><td>
	<x:forEach select="$der/derivates/derivate">
		<x:set var="derivateID" select="string(./@ID)" />
		<x:set var="derivateLabel" select="string(./@label)" />
		<x:set var="derivateTitle" select="string(./@title)" />
	    <mcr:checkAccess var="modifyAllowed" permission="writedb"	key="${derivateID}" /> 
		<mcr:checkAccess var="commitAllowed" permission="commitdb"	key="${derivateID}" />
		<table width=100% class="tasklistDerivate">
		<tr>
			<th align="left" valign="top">
				<fmt:message key="OMD.derivatelabel.${derivateLabel}" /><br />				
			</th>
			<th width="30">&nbsp;</th>
			<th align="right">
			  <c:if	test="${modifyAllowed}">
				<table>
					<tr>
						<c:if test="${!fn:contains('disshab,thesis,series',itemDocType) or commitAllowed}">
							<td align="center" valign="top" width="30">
							<form method="get" action="${baseURL}workflowaction">
								<input	name="derivateID" value="${derivateID}" type="hidden"> 
								<input	name="processid" value="${processid}" type="hidden"> 
								<input	name="todo" value="WFAddNewFileToDerivate" type="hidden"> 
								<input	title="<fmt:message key="WF.common.derivate.AddFile" />"
									src="${baseURL}images/workflow_derivateaddfile.gif" type="image"
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
							<input	title="<fmt:message key="WF.common.derivate.DelFile" />"
								src="${baseURL}images/workflow_derivatedelete.gif" type="image"
								border="0" class="imagebutton">
						</form>
						</td>
						  <x:choose>
						  <x:when select=". != ./../derivate[position()= 1]">
							<td align="center" valign="top" width="30">
							<form method="get" action="${baseURL}workflowaction">
								<input	name="derivateID" value="${derivateID}" type="hidden"> 
								<input	name="processid" value="${processid}" type="hidden"> 
								<input	name="todo" value="WFMoveDerivateUp" type="hidden"> 
								<input	title="<fmt:message key="WF.common.derivate.MoveUpDerivate" />"
									src="${baseURL}images/workflow_derivatemoveup.gif" type="image"
									border="0" class="imagebutton">
							</form>
							</td>
						</x:when>
						<x:otherwise>
							<td align="center" valign="top" width="30">
							<form method="get" action="">
								<input	title="<fmt:message key="WF.common.derivate.MoveUpDerivate" />"
									src="${baseURL}images/workflow_derivatemoveempty.gif" type="image"
									border="0" class="imagebutton">
							</form>
							</td>						
						</x:otherwise>
						</x:choose>
   					    <x:choose>
						  <x:when select=". != ./../derivate[position()= last()]">
							<td align="center" valign="top" width="30">
							<form method="get" action="${baseURL}workflowaction">
								<input	name="derivateID" value="${derivateID}" type="hidden"> 
								<input	name="processid" value="${processid}" type="hidden"> 
								<input	name="todo" value="WFMoveDerivateDown" type="hidden"> 
								<input	title="<fmt:message key="WF.common.derivate.DelDerivate" />"
									src="${baseURL}images/workflow_derivatemovedown.gif" type="image"
									border="0" class="imagebutton">
							</form>
							</td>
						</x:when>
						<x:otherwise>
							<td align="center" valign="top" width="30">
							<form method="get" action="">
								<input	title="<fmt:message key="WF.common.derivate.MoveUpDerivate" />"
									src="${baseURL}images/workflow_derivatemoveempty.gif" type="image"
									border="0" class="imagebutton">
							</form>
							</td>						
						</x:otherwise>
						</x:choose>
						</c:if>

						<c:if test="${fn:contains('disshab,thesis,series',itemDocType) and !commitAllowed}">
							<%-- for disshab only delete is allowed --%>
							<td align="center" valign="top" width="30">
							<form method="get" action="${baseURL}workflowaction">
								<input	name="derivateID" value="${derivateID}" type="hidden"> 
								<input	name="processid" value="${processid}" type="hidden"> 
								<input	name="todo" value="WFRemoveDerivateFromWorkflowObject" type="hidden"> 
								<input	title="<fmt:message key="WF.common.derivate.DelFile" />"
									src="${baseURL}images/workflow_derivatedelete.gif" type="image"
									border="0" class="imagebutton">
							</form>
							</td>
						</c:if>
					</tr>
				</table>
			</c:if></th>
		</tr>
		<tr><td colspan="3">${derivateTitle}</td></tr>

		<x:set var="numFiles" select="count(./file)" />
		<x:forEach select="file">
			<x:set var="fileSize" select="string(./@size)" />
			<tr valign="top">
				<td>
				 <!--  http://139.30.48.72:8080/mycoresample/wffile/DocPortal_derivate_00000008/dissertation.pdf?hosts=local -->
				 <a 				
					href="${baseURL}wffile/<x:out select="."/>?type=${itemDocType}"
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
	</table>
	
	</x:forEach>
	</td>

		<td>&nbsp;</td>
		<td align="right" style="vertical-align: bottom;" height="100%">
		<table>
			<tr>
				<mcr:checkAccess var="modifyAllowed" permission="deletewf"	key="${itemID}" />
				<c:if test="${modifyAllowed}">
					<td align="center" valign="bottom" width="30">
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
					<td align="center" valign="bottom" width="30">
					<form method="get" onSubmit="return reallyDeletefromDB();" action="${baseURL}workflowaction">
						<input name="processid"	value="${processid}" type="hidden"> 
						<input name="todo" value="WFDeleteObject" type="hidden"> 
						<input title="<fmt:message key="WF.common.object.DelObject" />"
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


