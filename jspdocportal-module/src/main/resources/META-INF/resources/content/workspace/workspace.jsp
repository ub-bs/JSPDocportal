<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8"%>
<%@page import="org.activiti.engine.task.Task"%>
<%@page import="org.mycore.activiti.MCRActivitiMgr"%>
<%@page import="org.mycore.frontend.servlets.MCRServlet"%>
<%@page import="org.mycore.common.MCRSessionMgr"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>


<%--Parameter: objectType --%>

<fmt:message var="pageTitle" key="WF.${param.objectType}" /> 
<stripes:layout-render name="../../WEB-INF/layout/default.jsp" pageTitle = "${pageTitle}" layout="2columns">
	<stripes:layout-component name="html_header">
		<link type="text/css" rel="stylesheet" href="${WebApplicationBaseURL}css/style_workspace.css" />	
	</stripes:layout-component>
	<stripes:layout-component name="contents">
		<div class="ur-box ur-text">
	 		<h2>Mein Arbeitsplatz</h2>
				<div class="stripesinfo">
					<stripes:errors />
					<stripes:messages />
				</div>
		<c:set  var="baseURL" value="${applicationScope.WebApplicationBaseURL}"/>
		<%-- <%out.println("ThreadLocal: "+MCRSessionMgr.getCurrentSession()+"<br />HTTP Request: "+MCRServlet.getSession(request)); %> --%>
			<stripes:form
				beanclass="org.mycore.frontend.jsp.stripes.actions.ShowWorkspaceAction"
				id="workspaceForm" enctype="multipart/form-data" acceptcharset="UTF-8">
				<stripes:hidden name="mcrobjid_base" />


				<%-- load first time from request parameter "returnPath --%>

				<div class="panel panel-info">
  					<div class="panel-heading">
    					<h3 class="panel-title">Neu</h3>
  					</div>
  					<div class="panel-body">
    					<stripes:submit class="btn btn-default" name="doCreateNewTask" value="Neues Objekt erstellen"/>
    				</div>
				</div>
			
				<div class="panel panel-info">
  					<div class="panel-heading">
    					<h3 class="panel-title">Übernommene Aufgaben</h3>
  					</div>
  					<div class="panel-body">
    			
			
	
				
				<c:forEach var="task" items="${actionBean.myTasks}" >
					<div class="panel panel-default" id="task_${task.id}">
						<div class="panel-heading clearfix">
							<stripes:submit class="btn btn-default btn-sm pull-right" name="doReleaseTask-task_${task.id}">Aufgabe abgeben</stripes:submit>
							<h5 class="panel-title" style="margin-top:0.33em;"><span class="badge" style="margin-right:3em">${task.executionId}</span> Aufgabe: ${task.name}</h5>
						</div>
						<c:if test="${not empty actionBean.objectType}">
							<c:set var="currentTask" value="${task}" scope="request" />
							
							<c:choose>
								<c:when test="${currentTask.name eq 'Objekt bearbeiten'}">
							 		<div class="panel-body">
							 	   		<%request.setAttribute("currentVariables", MCRActivitiMgr.getWorfklowProcessEngine().getRuntimeService().getVariables(((Task)request.getAttribute("currentTask")).getExecutionId())); %>
							  			<c:if test="${not empty currentVariables.validationMessage}">
											<div class="alert alert-danger" role="alert">${currentVariables.validationMessage}</div>
										</c:if>	
										<div>
											<span class="badge pull-left" style="margin-right:24px;margin-top:3px">${currentVariables.mcrObjectID}</span>
											<div class="pull-left">
												<h3 style="margin-top:0px">${currentVariables.wfObjectDisplayTitle}</h3>
												<c:out value="${currentVariables.wfObjectDisplayDescription}" escapeXml="false" />
											</div>
											<div style="clear:both">
												<c:if test="${not empty currentVariables.wfObjectDisplayDerivateList}">
													<c:out value="${currentVariables.wfObjectDisplayDerivateList}" escapeXml="false" />
												</c:if>
											</div>
										</div>
									</div>
									<div class="panel-body" style="margin-top:1px solid lightgrey">
							     		<button name="doEditObject-task_${currentTask.id}-${currentVariables.mcrObjectID}" value="" class="btn btn-default" type="submit">
							     			<span class="glyphicon glyphicon-tag"></span> Metadaten bearbeiten
							     		</button>
							     		<button name="doEditDerivates-task_${currentTask.id}-${currentVariables.mcrObjectID}" value="" class="btn btn-default" type="submit">
							    	 		<span class="glyphicon glyphicon-file"></span> Derivate bearbeiten
							     		</button>	
							    	</div>
								  	<div class="panel-footer">
								  		<button name="doGoto-task_${currentTask.id}-edit_object.do_save" value="" class="btn btn-default" type="submit"><span class="glyphicon glyphicon-floppy-disk"></span> Speichern</button>
										<button name="doGoto-task_${currentTask.id}-edit_object.do_cancel" value="" class="btn btn-default" type="submit"><span class="glyphicon glyphicon-remove"></span> Bearbeitung abbrechen</button>
							  			<button name="doGoto-task_${currentTask.id}-edit_object.do_drop" value="" class="btn btn-danger btn-sm pull-right" style="margin-top:0.2em" type="submit"><span class="glyphicon glyphicon-trash"></span> Objekt löschen</button>
							  		</div> 
							   	</c:when>
							   
							   	<c:otherwise>
									<p> Nothing ToDo for TASK: = ${task.name} </p>
							   	</c:otherwise>
							</c:choose>
						</c:if>			
					</div>
				</c:forEach>
				</div>
				</div>
				
				<div class="panel panel-info">
  					<div class="panel-heading">
    					<h3 class="panel-title">Übernehmbare Aufgaben</h3>
  					</div>
  					<div class="list-group">
				
					<c:forEach var="task" items="${actionBean.availableTasks}">
						<div class="list-group-item">
							<span class="badge pull-left" style="margin-right:3em;margin-top:0.66em">${task.executionId}</span>
							<c:if test="${empty task.assignee}">
								<stripes:submit class="btn btn-default" name="doAcceptTask-task_${task.id}">Aufgabe übernehmen</stripes:submit>
							</c:if>
						</div>
					</c:forEach>
				</div>
			</div>
			
				
			</stripes:form>
		</div>
	</stripes:layout-component>
</stripes:layout-render>
