<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>

<fmt:message var="pageTitle" key="Webpage.admin.Process" /> 
<stripes:layout-render name="../WEB-INF/layout/default.jsp" pageTitle = "${pageTitle}" layout="2columns">
	<stripes:layout-component name="contents">
		<h2><fmt:message key="Webpage.admin.Process" /></h2>

		<c:set var="debug" value="false" />
		<c:set var="pid" value="${param.pid}" />
		<c:set var="type" value="${param.workflowProcessType}" />


		<c:if test="${not empty pid}">
			<mcr:deleteProcess result="result" pid="${pid}" workflowProcessType="${type}" />
			<div style="padding:6px; border: 1px solid blue;">
				<h3>zu löschender Prozess: <c:out value="${pid}" /></h3>
				<p>Typ: <c:out value="${type}" /></p>
				<p style="font-weight: bold;padding-top:12px">Status: <fmt:message key="${result}" /></p>
				
			</div>
			<hr />
		</c:if>


		<mcr:listWorkflowProcess var="processlist" workflowProcessType="${type}" />
		<x:forEach select="$processlist/processlist">
			<x:set var="type" select="string(./@type)" />
			<h2>Prozessdaten vom Typ: ${type}</h2>
		
			<x:forEach select="./process">
				<x:set var="pid" select="string(./@pid)" />
				<x:set var="status" select="string(./@status)" />
				<table style="max-width: 600px;margin-top:24px;">
				<tr>
					<th><h3>Prozess Nr: <x:out select="./@pid" /></h3></th>
					<th></th>
					<th style="text-align: right;">
						<form method="get" action="${applicationScope.WebApplicationBaseURL}nav">
							<input type=hidden name="path" value="~process-${type}" /> 
							<input type=hidden name="pid" value="${pid}" /> 
							<input type="image" title="Prozess löschen"
								   src="${applicationScope.WebApplicationBaseURL}admin/images/delete.gif"
						       	   onClick="return questionDel()" />
						</form>
					</th>
				</tr>
	
				<tr>
					<td>Status</td>
					<td width="30px">&nbsp;</td>
					<td> <x:out select="./@status" /></td>
				</tr>
				<x:forEach select="./variable">
					<tr valign="top">
						<td><x:out select="./@name" /></td>
						<td width="30px">&nbsp;</td>
						<td><x:out select="./@value" /></td>
					</tr>
				</x:forEach>
			 	</table>		
			</x:forEach>
		</x:forEach>
	</stripes:layout-component>
</stripes:layout-render>