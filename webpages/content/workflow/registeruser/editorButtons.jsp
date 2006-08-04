<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr"%>
<c:set var="baseURL" value="${applicationScope.WebApplicationBaseURL}" />
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages' />

<c:set var="processid" value="${requestScope.task.processID}" />
<c:set var="dom" value="${requestScope.task.variables}" />
<c:set var="description">	<x:out select="$dom/variables/variable[@name = 'initiatorIntend']/@value" /></c:set>
<c:set var="email">	<x:out select="$dom/variables/variable[@name = 'initiatorEmail']/@value" /></c:set>
<c:set var="faculty">	<x:out select="$dom/variables/variable[@name = 'initiatorFaculty']/@value" /></c:set>
<c:set var="institution">	<x:out select="$dom/variables/variable[@name = 'initiatorInstitution']/@value" /></c:set>


	<tr valign="top">
		<td>
			<c:out value="${faculty}" /> <c:out value="${institution}" />
			<br/>
			<c:out value="${email}" />
			<br/>
			<c:out value="${description}" />
			<br/>
			 <mcr:getWorkflowEngineVariable pid="${requestScope.task.processID}" var="error" workflowVar="varnameERROR" /> 
		     <font color="red">${error}</font><br/>	         		 
			
		</td>		
		<td>&#160;</td>		
		<td align="center" valign="top" width="50">
				<form method="get" action="${baseURL}servlets/MCRRegisterUserWorkflowServlet">
				    <input  name="nextPath" value="~workflow-registeruser" type="hidden">
					<input	name="processid" value="${processid}" type="hidden"> 
					<input	name="todo" value="WFModifyWorkflowUser" type="hidden"> 					
					<input title="<fmt:message key="WF.common.object.EditObject" />" src="${baseURL}images/workflow_objedit.gif" type="image" class="imagebutton">					
				</form>
		</td>
	</tr>

