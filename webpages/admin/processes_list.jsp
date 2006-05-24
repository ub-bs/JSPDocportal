<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr"%>

<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages' />

<div class="headline"><fmt:message key="Admin.Process" /></div>
<br />
<c:set var="debug" value="false" />

<c:set var="pid" value="${param.pid}" />
<c:set var="type" value="${param.workflowProcessType}" />


<c:if test="${!empty(pid)}">
	<mcr:deleteProcess result="result" pid="${pid}"
		workflowProcessType="${type}" />
	<table class="access" cellspacing="1" cellpadding="0">
		<tr>
			<td>zu löschender Prozess: <c:out value="${pid}" /></td>
		</tr>
		<tr>
			<td>Typ: <c:out value="${type}" /></td>
		</tr>
		<tr>
			<td colspan="2">Status: <fmt:message key="${result}" /></td>
		</tr>
	</table>
	<hr />
</c:if>


<mcr:listWorkflowProcess var="processlist" workflowProcessType="${type}" />

<table class="access" cellspacing="0" cellpadding="3">
	<x:forEach select="$processlist/processlist">
		<x:set var="type" select="string(./@type)" />
		<tr>
			<th colspan="2">Prozessdaten vom Typ: ${type}</th>
			<th>Löschen</th>
		</tr>
		<x:forEach select="./process">
			<x:set var="pid" select="string(./@pid)" />
			<x:set var="status" select="string(./@status)" />
			<tr>
				<td>
				<table cellpadding="1" cellspacing="0">
					<tr>
						<td valign="top"><b>Prozess Nr: <x:out select="./@pid" /></b></td>
						<td valign="top"><b>Status: <x:out select="./@status" /></b></td>
					</tr>
					<tr>
						<td colspan="2">
						<table cellpadding="1" cellspacing="0">
							<x:forEach select="./variable">
								<tr valign="top">
									<td><x:out select="./@name" /></td>
									<td><x:out select="./@value" /></td>
								</tr>
							</x:forEach>
						</table>
						</td>
					</tr>
				</table>
				</td>
				<td width="50"></td>
				<td align="center">
				<form method="get"
					action="${applicationScope.WebApplicationBaseURL}nav"><input
					type=hidden name="path" value="~process-${type}" /> <input
					type=hidden name="pid" value="${pid}" /> <input type="image"
					title="Prozess löschen"
					src="${applicationScope.WebApplicationBaseURL}admin/images/delete.gif"
					onClick="return questionDel()"></form>
				</td>
			</tr>
		</x:forEach>
	</x:forEach>
</table>

