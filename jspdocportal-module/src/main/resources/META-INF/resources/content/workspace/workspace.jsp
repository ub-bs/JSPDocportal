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

<fmt:message var="pageTitle" key="WF.workspace" /> 
<stripes:layout-render name="../../WEB-INF/layout/default.jsp" pageTitle = "${pageTitle}" layout="2columns">
	<stripes:layout-component name="html_header">
		<link type="text/css" rel="stylesheet" href="${WebApplicationBaseURL}themes/ir/css/style_ir.css" />	
	</stripes:layout-component>
	<stripes:layout-component name="contents">
		<div class="ir-box">
	 		<h2><fmt:message key="WF.workspace.headline.${actionBean.mode}" /></h2>
				<div class="stripesinfo">
					<stripes:errors />
					<stripes:messages />
				</div>
		<c:set  var="baseURL" value="${applicationScope.WebApplicationBaseURL}"/>
		<%-- <%out.println("ThreadLocal: "+MCRSessionMgr.getCurrentSession()+"<br />HTTP Request: "+MCRServlet.getSession(request)); %> --%>
			<stripes:form
				beanclass="org.mycore.frontend.jsp.stripes.actions.ShowWorkspaceAction"
				id="workspaceForm" enctype="multipart/form-data" acceptcharset="UTF-8">
				<stripes:hidden name="mode" />
				<c:forEach var="msg" items="${actionBean.messages}">
					<div class="alert alert-warning ir-workflow-message">
						<c:out value="${msg}" escapeXml="false" />
 					</div>
				</c:forEach>

				<div class="panel panel-info">
  					<div class="panel-heading">
                        <c:forEach var="mcrBase" items="${actionBean.newObjectBases}">
  						  <button class="btn btn-default btn-sm pull-right"  name="doCreateNewTask-${mcrBase}" value="doit" style="margin-top:-6px; margin-left:12px">Neues <strong>${mcrBase}</strong> erstellen</button>
    			        </c:forEach>		
                        <h3 class="panel-title"><fmt:message key="WF.workspace.info.headline.new_task" /></h3>
  					</div>
  				</div>
			
				<div class="panel panel-info">
  					<div class="panel-heading">
  					<button class="btn btn-default btn-sm pull-right" style="margin-top:-6px" type="button" data-toggle="collapse" data-target="#publish-dialog-task_${currentTask.id}"><fmt:message key="WF.workspace.button.publish_all_objects" /></button>
					<h3 class="panel-title"><fmt:message key="WF.workspace.info.headline.assumed_tasks" /></h3>
    			</div>
  					<div id="publish-dialog-task_${currentTask.id}" class="collapse">
						<div class="panel-body" style="background-color: rgb(252, 248, 227);border: 2px solid rgb(238, 162, 54); padding-left: 4em;">
							<button name="doPublishAllTasks" value="" class="btn btn-warning btn-sm" type="submit"><i class="fa fa-check-square-o"></i> <fmt:message key="WF.workspace.button.publish_all" /></button>
							<label style="vertical-align:bottom; margin-left:2em;"><fmt:message key="WF.workspace.label.publish_all" /></label>
						</div>
					</div>
  					<div class="panel-body">
    					<c:forEach var="task" items="${actionBean.myTasks}" >
							<div class="panel panel-default" id="task_${task.id}">
								<div class="panel-heading clearfix">
									<button class="btn btn-default btn-sm pull-right" name="doReleaseTask-task_${task.id}"><fmt:message key="WF.workspace.submit.task" /></button>
									<span class="btn btn-none btn-sm pull-right"><strong><fmt:message key="WF.workspace.start" /></strong> <fmt:formatDate pattern="dd.MM.yyyy HH:mm" value="${task.createTime}" /></span>
									<h5 class="panel-title" style="margin-top:0.33em;"><span class="badge" style="margin-right:3em">${task.executionId}</span> <fmt:message key="WF.workspace.task" /> ${task.name}</h5>
								</div>
								<c:if test="${not empty actionBean.mode}">
									<c:set var="currentTask" value="${task}" scope="request" />
									<c:choose>
										<c:when test="${currentTask.name eq 'Objekt bearbeiten'}">
							 				<div class="panel-body">
							 				
							 	   				<%pageContext.setAttribute("currentVariables", MCRActivitiMgr.getWorfklowProcessEngine().getRuntimeService().getVariables(((Task)request.getAttribute("currentTask")).getExecutionId()), PageContext.REQUEST_SCOPE); %>
							  					<c:if test="${not empty currentVariables.validationMessage}">
													<div class="alert alert-danger" role="alert">${currentVariables.validationMessage}</div>
												</c:if>	
												<div>
													<a href="${WebApplicationBaseURL}resolve/id/${currentVariables.mcrObjectID}?fromWF=true" class="btn btn-default pull-right">
							    	 					<i class="fa fa-newspaper-o"></i> <fmt:message key="WF.workspace.preview" />
							     					</a>	
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
											<div class="panel-body">
												<c:if test="${not fn:contains(currentVariables.mcrObjectID,'_person_')}">
													<a href="${WebApplicationBaseURL}pubform/pica3?urn=${currentVariables.wfObjectDisplayPersistentIdentifier}" 
												   		class="btn btn-default" target="_blank"><i class="fa fa-book"></i> <fmt:message key="WF.workspace.button.pica3" />
													</a>
							     					<button class="btn btn-default" type="button" data-toggle="collapse" data-target="#import_mods-dialog-task_${currentTask.id}">
							     						<i class="fa fa-download"></i> <fmt:message key="WF.workspace.button.download" />
							     					</button>
							     				</c:if>
							     				<button name="doEditObject-task_${currentTask.id}-${currentVariables.mcrObjectID}" value="" class="btn btn-default" type="submit">
							     					<i class="fa fa-tag"></i> <fmt:message key="WF.workspace.button.edit_metadata" />
							     				</button>
							     				<button name="doEditDerivates-task_${currentTask.id}-${currentVariables.mcrObjectID}" value="" class="btn btn-default" type="submit">
							    	 				<i class="fa fa-file"></i> <fmt:message key="WF.workspace.button.edit_derivate" />
							     				</button>
							   			 	</div>
							   			 	<div id="import_mods-dialog-task_${currentTask.id}" class="collapse">
							  					<div class="panel-body" style="background-color: rgb(242, 222, 222);border: 2px solid rgb(169, 68, 66); padding-left: 4em;">
							  						<button name="doImportMODS-task_${currentTask.id}-${currentVariables.mcrObjectID}" value="" class="btn btn-danger btn-sm" type="submit"><i class="fa fa-download"></i> <fmt:message key="WF.workspace.button.import" /></button>
								  					<label style="vertical-align:bottom; margin-left:2em;"><fmt:message key="WF.workspace.label.import" /></label>
							  					</div>
							  				</div>
								  			<div class="panel-footer">
								  				<button name="doGoto-task_${currentTask.id}-edit_object.do_save" value="" class="btn btn-primary" type="submit"><i class="fa fa-check"></i> <fmt:message key="WF.workspace.button.publish" /></button>
												<button name="doGoto-task_${currentTask.id}-edit_object.do_cancel" value="" class="btn btn-default" type="submit"><i class="fa fa-times"></i> <fmt:message key="WF.workspace.button.cancel" /></button>
							  					<button class="btn btn-danger btn-sm pull-right" style="margin-top:0.2em" type="button" data-toggle="collapse" data-target="#delete-dialog-task_${currentTask.id}"><i class="fa fa-trash-o"></i> <fmt:message key="WF.workspace.button.delete_object" /></button>
							  				</div>
							  				<div id="delete-dialog-task_${currentTask.id}" class="collapse">
							  					<div class="panel-footer" style="background-color: rgb(242, 222, 222);border: 2px solid rgb(169, 68, 66); padding-left: 4em;">
								  					<button name="doGoto-task_${currentTask.id}-edit_object.do_drop" value="" class="btn btn-danger btn-sm" type="submit"><i class="fa fa-trash-o"></i> <fmt:message key="WF.workspace.button.delete" /></button>
								  					<label style="vertical-align:bottom; margin-left:2em;"><fmt:message key="WF.workspace.label.delete" /></label>
							  					</div>
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
    					<h3 class="panel-title"><fmt:message key="WF.workspace.info.headline.available_tasks" /></h3>
  					</div>
  					<div class="panel-body">
  						<c:forEach var="task" items="${actionBean.availableTasks}">
							<div class="panel panel-default" id="available_task_${task.id}">
								<div class="panel-heading clearfix">
									<button class="btn btn-default btn-sm pull-right" name="doAcceptTask-task_${task.id}"><fmt:message key="WF.workspace.submit.accept_task" /></button>
									<span class="btn btn-none btn-sm pull-right"><strong><fmt:message key="WF.workspace.start" /> </strong><fmt:formatDate pattern="dd.MM.yyyy HH:mm" value="${task.createTime}" /></span>
									<h5 class="panel-title" style="margin-top:0.33em;"><span class="badge" style="margin-right:3em">${task.executionId}</span> <fmt:message key="WF.workspace.task" /> ${task.name}</h5>
								</div>
						
								<c:set var="currentTask" value="${task}" scope="request" />
								<div class="panel-body clearfix">
									<%pageContext.setAttribute("currentVariables", MCRActivitiMgr.getWorfklowProcessEngine().getRuntimeService().getVariables(((Task)request.getAttribute("currentTask")).getExecutionId()), PageContext.REQUEST_SCOPE); %>
									<c:if test="${not empty currentVariables.validationMessage}">
										<div class="alert alert-danger" role="alert">${currentVariables.validationMessage}</div>
									</c:if>	
									<div>
										<span class="badge pull-left" style="margin-right:24px;margin-top:3px">${currentVariables.mcrObjectID}</span>
										<div class="pull-left">
											<h3 style="margin-top:0px">${currentVariables.wfObjectDisplayTitle}</h3>
											<c:out value="${currentVariables.wfObjectDisplayDescription}" escapeXml="false" />
										</div>
									</div>
								</div>
							</div>
						</c:forEach>
					</div>
				</div>
			</stripes:form>
		</div>
	</stripes:layout-component>
</stripes:layout-render>
