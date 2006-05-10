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
    
<mcr:listWorkflowDerivates varDom="der" docID="${itemID}" derivates="${attachedDerivates}" />
        <tr>
         <td class="resultTitle">
           <b><c:out value="${wfoTitle}" /></b>  	
         </td>
		 <td width="50">	&nbsp;		</td>
		 <td align="right">
				<table cellpadding="0" cellspacing="0">
					<tr>
                         <mcr:checkAccess var="modifyAllowed" permission="writedb" key="${itemID}" />
                         <c:if test="${modifyAllowed}">						
								<c:if test="${fn:contains('document,disshab,professorum',itemDocType)}">
										<td align="center" valign="top" width="30">
											<form method="get" action="${baseURL}workflowaction">
												<input name="processid" value="${processid}" type="hidden">
												<input name="todo" value="WFAddNewDerivateToWorkflowObject" type="hidden">
												<input title="<fmt:message key="Derivate.AddDerivate" />" src="${baseURL}images/workflow_derivateadd.gif" type="image" class="imagebutton">
											</form>
										</td>
								</c:if>
										<td align="center" valign="top" width="30">
											<form method="get" action="${baseURL}workflowaction">
												<input name="processid" value="${processid}" type="hidden">
												<input name="todo" 		value="WFEditWorkflowObject" type="hidden">
												<input title="<fmt:message key="Object.EditObject" />" src="${baseURL}images/workflow_objedit.gif" type="image" class="imagebutton">
											</form>
										</td>
						   </c:if>										
                           <mcr:checkAccess var="modifyAllowed" permission="commitdb" key="${itemID}" />
                           <c:if test="${modifyAllowed}">	
                              <c:if test="${not fn:contains('disshab,author',itemDocType)}">					
										<td align="center" valign="top" width="30">
											<form method="get" action="${baseURL}workflowaction">
												<input name="processid" value="${processid}" type="hidden">
												<input name="todo" 		value="WFCommitWorkflowObject" type="hidden">
												<input title="<fmt:message key="Object.CommitObject" />" src="${baseURL}images/workflow_objcommit.gif" type="image" class="imagebutton">
											</form>
										</td>
                              </c:if>
							</c:if>										
                            <mcr:checkAccess var="modifyAllowed" permission="deletewf" key="${itemID}" />
                            <c:if test="${modifyAllowed}">						
										<td align="center" valign="top" width="30">
											<form method="get" action="${baseURL}workflowaction">
												<input name="processid" value="${processid}" type="hidden">
												<input name="todo" 		value="WFDeleteWorkflowObject" type="hidden">
												<input title="<fmt:message key="Object.DelObject" />" src="${baseURL}images/workflow_objdelete.gif" type="image" class="imagebutton">
											</form>
						    </c:if>										
                            <mcr:checkAccess var="modifyAllowed" permission="read" key="${itemID}" />
                            <c:if test="${modifyAllowed}">						
								   <c:if test="${param.type == 'disshab' }">
										<td align="center" valign="top" width="30">
											<form method="get" action="${baseURL}content/results-config/docdetails-disshab-deliver.jsp" target="new" >
												<input name="id" value="${itemID}" type="hidden">
												<input name="fromWForDB" value="workflow" type="hidden">
												<input title="<fmt:message key="Object.DisshabPreview" />" src="${baseURL}images/workflow_disshabpreview.gif" type="image" class="imagebutton">
											</form>
                                        </td>
								   </c:if>
										<td align="center" valign="top" width="60">
											<form method="get" action="${baseURL}nav">
												<input value="~workflow-preview" name="path" type="hidden">
												<input name="id" value="${itemID}" type="hidden">
												<input name="fromWForDB" value="workflow" type="hidden">
												<input title="<fmt:message key="Object.Preview" />" src="${baseURL}images/workflow_objpreview.gif" type="image" class="imagebutton">
											</form>
										</td>								   
						    </c:if>										
							</tr>
						</table>
					</td>
				</tr>        
	       		<tr>
					 <td class="description" colspan="3">
 						<fmt:message key="WorkflowEngine.Description.${requestScope.task.workflowProcessType}" />, ${itemID}
 					</td>
           		</tr>				
		  	   
		       <x:forEach select="$der/derivates/derivate">
        		 <x:set var="derivateID" select="string(./@ID)" />
        		 <x:set var="derivateLabel" select="string(./@label)" />
		          <tr>
							<td align="left" valign="top">${derivateLabel}&#160;	</td>
							<td width="30">	&nbsp;	</td>
							<td>
					          <mcr:checkAccess var="modifyAllowed" permission="writedb" key="${derivateID}" />
					          <c:if test="${modifyAllowed}">						
								<table cellpadding="0" cellspacing="0">
									<tr>
										<td align="center" valign="top" width="30">
											<form method="get" action="${baseURL}workflowaction">
												<input name="derivateID" value="${derivateID}" type="hidden">
												<input name="processid" value="${processid}" type="hidden">
												<input name="todo" value="WFAddNewFileToDerivate" type="hidden">
												<input title="<fmt:message key="Derivate.AddFile" />" src="${baseURL}images/workflow_derivatenew.gif" type="image" class="imagebutton">
											</form>
										</td>
										<td width="10"></td>
										<td align="center" valign="top" width="30">
											<form method="get" action="${baseURL}workflowaction">
												<input name="derivateID" value="${derivateID}" type="hidden">
												<input name="processid" value="${processid}" type="hidden">
												<input name="todo" value="WFEditDerivateFromWorkflowObject" type="hidden">
												<input title="<fmt:message key="Derivate.EditDerivate" />" src="${baseURL}images/workflow_derivateedit.gif" type="image" border="0" class="imagebutton">
											</form>
										</td>
										<td width="10"></td>
										<td align="center" valign="top" width="30">
											<form method="get" action="${baseURL}workflowaction">
												<input name="derivateID" value="${derivateID}" type="hidden">
												<input name="processid" value="${processid}" type="hidden">
												<input name="todo" value="WFRemoveDerivateFromWorkflowObject" type="hidden">
												<input title="<fmt:message key="Derivate.DelDerivate" />" src="${baseURL}images/workflow_derivatedelete.gif" type="image" border="0" class="imagebutton">
											</form>
										</td>
									</tr>
								</table>
							   </c:if>
							</td>
					</tr>		       

       		    <x:set var="numFiles" select="count(./file)" />					
				<x:forEach select="file">
       		        <x:set var="fileSize" select="string(./@size)" />    	    		 
					<tr valign="top" >
							<td>
								<a class="linkButton" href="${baseURL}servlets/MCRFileViewWorkflowServlet/<x:out select="."/>?type=${itemDocType}" target="_blank">
								  <x:out select="."/>
								</a>	[	${fileSize}	]
							</td>
							<td width="30">			&nbsp;		</td>
							<td>
					          <mcr:checkAccess var="modifyAllowed" permission="writedb" key="${derivateID}" />
					          <c:if test="${modifyAllowed}">						
								<table cellpadding="0" cellspacing="0">
									<tr>
										<td align="center" valign="top" width="30">
											<c:if test="${numFiles gt 1}">
												<form method="post" action="${baseURL}workflowaction">
													<input name="derivateID" value="${derivateID}" type="hidden">
													<input name="processid" value="${processid}" type="hidden">
													<input value="WFRemoveFileFromDerivate" name="todo" type="hidden">
													<input name="filename" value="<x:out select="."/>" type="hidden">
													<input title="Löschen dieser Datei" src="${baseURL}images/delete.png" type="image" class="imagebutton">
												</form>
											</c:if>
										</td>
									</tr>
								</table>
							  </c:if>
							</td>
						</tr>
			     </x:forEach>			
	       </x:forEach>



