<%@page import="org.mycore.frontend.servlets.MCRServlet"%>
<%@page import="org.mycore.common.MCRSessionMgr"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>
<%@ page pageEncoding="UTF-8" %>

<%--Parameter: objectType --%>

<fmt:message var="pageTitle" key="WF.${param.objectType}" /> 
<stripes:layout-render name="../../WEB-INF/layout/default.jsp" pageTitle = "${pageTitle}" layout="2columns">
<stripes:layout-component name="html_header">
	<link type="text/css" rel="stylesheet" href="${WebApplicationBaseURL}css/style_workspace.css">
</stripes:layout-component>
	<stripes:layout-component name="contents">
		<c:set  var="baseURL" value="${applicationScope.WebApplicationBaseURL}"/>
		<%out.println("ThreadLocal: "+MCRSessionMgr.getCurrentSession()+"<br />HTTP Request: "+MCRServlet.getSession(request)); %>
			<stripes:form
				beanclass="org.mycore.frontend.jsp.stripes.actions.ShowWorkspaceAction"
				id="workspaceForm" enctype="multipart/form-data" acceptcharset="UTF-8">
				<h1>Mein Arbeitsplatz</h1>
				<div class="stripesinfo">
					<stripes:errors />
					<stripes:messages />
				</div>
				<stripes:hidden name="projectID" />
				<stripes:hidden name="objectType" />


				<%-- load first time from request parameter "returnPath --%>

				<div>
					ProjektID: ${actionBean.projectID} <br />
					ObjectType: ${actionBean.objectType}
				</div>	
				
				<h3>Neu</h3>
				<stripes:submit name="doCreateNewTask" value="Neues Objekt erstellen" class="submit" />

				<h3>Übernommene Aufgaben</h3>
				<c:forEach var="task" items="${actionBean.myTasks}" >
					<div>
						Aufgabe: <c:out value="${task.id}" />
						<c:if test="${not empty actionBean.objectType}">
							<c:set var="currentTask" value="${task}" scope="request" />
							<jsp:include page="edit-${actionBean.objectType}.jsp" />
						</c:if>
						<stripes:submit name="doReleaseTask_${task.id}">Aufgabe abgeben</stripes:submit>
					
					</div>
				</c:forEach>
				
				<h3>Übernehmbare Aufgaben</h3>
				<c:forEach var="task" items="${actionBean.availableTasks}">
					<div>
						Aufgabe: <c:out value="${task.id}" />
						<c:if test="${empty task.assignee}">
							<stripes:submit name="doAcceptTask_${task.id}">Aufgabe übernehmen</stripes:submit>
						</c:if>
					</div>
				</c:forEach>
			
				
			</stripes:form>
</stripes:layout-component>
</stripes:layout-render>
