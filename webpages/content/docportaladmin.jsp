<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>

<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages' />
<mcr:checkAccess var="adminuser" permission="administrate-user" key="" />
<mcr:checkAccess var="adminxmetadiss" permission="administrate-xmetadiss" key="" />
<mcr:checkAccess var="adminaccessrules" permission="administrate-accessrules" key="" />

<div class="adminheadline">
	<fmt:message key="Admin.Application" />
</div>
<br />

<div class="textblock">
	<fmt:message key="Admin.Text1" />
</div>
<hr />
<table class="editor">
	<tr>
		<td class="metaname">	Administration von Nutzern		</td>
	</tr>	
	<tr>
	    <td>
			<c:choose>
				<c:when test="${adminuser eq 'true'}">
					<img src="${applicationScope.WebApplicationBaseURL}images/link_intern.gif" border="0" />&#160; <a href="${applicationScope.WebApplicationBaseURL}nav?path=~workflow-registeruser">
						<fmt:message key="Nav.WorkflowRegisteruser" />
					</a><br />
				</c:when>
				<c:otherwise>
						<fmt:message key="Admin.PrivilegesError" />	
				</c:otherwise>
			</c:choose>
			<br/>
			<br/>
			<br/>
		</td>
	</tr>
	<tr>
		<td class="metaname">	Administration des  Workflows für Dissertation		</td>
	</tr>	
	<tr>
	    <td>
			<c:choose>
				<c:when test="${adminxmetadiss eq 'true'}">
					<img src="${applicationScope.WebApplicationBaseURL}images/link_intern.gif" border="0" />&#160; <a href="${applicationScope.WebApplicationBaseURL}nav?path=~workflow-disshab">
						Arbeitsmappe Dissertationen
					</a>	<br />
					<img src="${applicationScope.WebApplicationBaseURL}images/link_intern.gif" border="0" />&#160;<a href="${applicationScope.WebApplicationBaseURL}nav?path=~workflow-disshab">
						Prüfung abgegebener Dissertationen
					</a>	<br />
					<img src="${applicationScope.WebApplicationBaseURL}images/link_intern.gif" border="0" />&#160;<a href="${applicationScope.WebApplicationBaseURL}admin?path=processes_list&workflowProcessType=xmetadiss" target="admin">
						Laufende Prozesse anzeigen
					</a>	<br />
					<img src="${applicationScope.WebApplicationBaseURL}images/link_intern.gif" border="0" />&#160;<a href="${applicationScope.WebApplicationBaseURL}admin?path=processes_edit&workflowProcessType=xmetadiss" target="admin">
						Laufende Prozesse anzeigen und Status zurücksetzten
					</a>	<br />
				</c:when>
				<c:otherwise>
						<fmt:message key="Admin.PrivilegesError" />	
				</c:otherwise>
			</c:choose>
			<br/>
			<br/>
			<br/>
		</td>
	</tr>
	<tr>
		<td class="metaname">	Administration von Zugriffsrechten		</td>
	</tr>	
	<tr>
	    <td>
			<c:choose>
				<c:when test="${adminaccessrules eq 'true'}">
					<img src="${applicationScope.WebApplicationBaseURL}images/link_intern.gif" border="0" />&#160;<a href="${applicationScope.WebApplicationBaseURL}admin" target="admin">
						Zugriffsrechte für Dokumente verwalten
					</a><br />
				</c:when>
				<c:otherwise>
						<fmt:message key="Admin.PrivilegesError" />	
				</c:otherwise>
			</c:choose>
			<br/>
			<br/>
		</td>
	</tr>
</table>


