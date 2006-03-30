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
<c:set var="userID">
	<x:out select="$dom/variables/variable[@name = 'userID']/@value" />
</c:set>
<c:set var="email">
	<x:out select="$dom/variables/variable[@name = 'email']/@value" />
</c:set>
<c:set var="name">
	<x:out select="$dom/variables/variable[@name = 'name']/@value" />
</c:set>
<c:set var="faculty">
	<x:out select="$dom/variables/variable[@name = 'faculty']/@value" />
</c:set>
<c:set var="institution">
	<x:out select="$dom/variables/variable[@name = 'institution']/@value" />
</c:set>


<tr valign="top">
	<td class="metaname">Nutzerangaben</td>
	<td>
		<b><c:out value="${userID}" /></b><br/>
		<c:out value="${name}" /> <br />
		<c:out value="${faculty}" /> <c:out value="${institution}" />
	</td>
	<td width="50">&#160;</td>
	<td align="center" valign="top" width="50">
		<form method="get" action="${baseURL}workflowaction">
			<input	name="processid" value="${processid}" type="hidden"> 
			<input	name="todo" value="WFEditWorkflowObject" type="hidden"> 
			<input	title="<fmt:message key="Object.EditObject" />"	src="${baseURL}images/workflow_objedit.gif" type="image"	class="imagebutton">
		</form>
	</td>
	<td align="center" valign="top" width="50">
		<form method="get" action="${baseURL}workflowaction">
			<input	name="processid" value="${processid}" type="hidden"> 
			<input	name="todo" value="WFCommitWorkflowObject" type="hidden"> 
			<input	title="<fmt:message key="Nav.WorkflowRegisteruser.submit" />"	src="${baseURL}images/workflow_objcommit.gif" type="image"	class="imagebutton">
		</form>
	</td>
	<td align="center" valign="top" width="50">
		<form method="get" action="${baseURL}workflowaction">
			<input		name="processid" value="${processid}" type="hidden"> 
			<input		name="todo" value="WFDeleteWorkflowObject" type="hidden"> 
			<input		title="<fmt:message key="Nav.WorkflowRegisteruser.cancel" />" src="${baseURL}images/workflow_objdelete.gif" type="image" class="imagebutton">
		</form>
 </td>
</tr>

