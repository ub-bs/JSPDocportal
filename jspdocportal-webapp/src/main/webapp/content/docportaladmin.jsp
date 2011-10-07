<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"   %>
<%@ taglib prefix="mcr" uri="/WEB-INF/lib/mycore-taglibs.jar" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>

<fmt:message var="pageTitle" key="Nav.Archive" /> 
<stripes:layout-render name="../WEB-INF/layout/default.jsp" pageTitle = "${pageTitle}">
	<stripes:layout-component name="contents">

<mcr:checkAccess var="adminuser" permission="administrate-user" key="" />
<mcr:checkAccess var="adminxmetadiss" permission="administrate-xmetadiss" key="" />
<mcr:checkAccess var="adminaccessrules" permission="administrate-accessrules" key="" />

<div class="adminheadline">
	<fmt:message key="Webpage.admin.Application" />
</div>
<br />

<div class="textblock">
	<fmt:message key="Webpage.intro.admin.Text1" />
</div>
<hr />
<table class="editor">
	<tr>
		<td class="metaname">	Administration von Nutzern		</td>
	</tr>	
	<tr>
	    <td>
			<c:choose>
				<c:when test="${adminuser}">
					<img src="${applicationScope.WebApplicationBaseURL}images/link_intern.gif" border="0" />&#160; <a href="${applicationScope.WebApplicationBaseURL}nav?path=~workflow-registeruser">
						<fmt:message key="WF.Registeruser" />
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
				<c:when test="${adminxmetadiss}">
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
				<c:when test="${adminaccessrules}">
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

	</stripes:layout-component>
</stripes:layout-render>    
