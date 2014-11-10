<%@page import="org.mycore.frontend.jsp.stripes.actions.EditDerivatesAction"%>
<%@page import="org.mycore.activiti.MCRActivitiMgr"%>
<%@ page pageEncoding="UTF-8" contentType="application/xhtml+xml; charset=UTF-8"%>
<%@page import="org.mycore.frontend.servlets.MCRServlet"%>
<%@page import="org.mycore.common.MCRSessionMgr"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>


<%--Parameter: objectType --%>

<fmt:message var="pageTitle" key="WF.EditDerivates" /> 
<stripes:layout-render name="../../WEB-INF/layout/default.jsp" pageTitle = "${pageTitle}" layout="2columns">
	<stripes:layout-component name="html_header">
		<link type="text/css" rel="stylesheet" href="${WebApplicationBaseURL}css/style_workspace.css" />	
	</stripes:layout-component>
	<stripes:layout-component name="contents">
		<div class="ur-box ur-text">
		 	<h2><fmt:message key="WF.EditDerivates" /></h2>
		 			<div>
					Base of MCRObjID: ${actionBean.mcrobjid_base}
				</div>
		</div>	
			<c:set  var="baseURL" value="${applicationScope.WebApplicationBaseURL}" />
			 <%request.setAttribute("currentVariables", MCRActivitiMgr.getWorfklowProcessEngine().getTaskService().getVariables(((EditDerivatesAction)request.getAttribute("actionBean")).getTaskid())); %>
			
				<stripes:form
				beanclass="org.mycore.frontend.jsp.stripes.actions.EditDerivatesAction"
				id="workspaceForm" enctype="multipart/form-data" acceptcharset="UTF-8">
				<stripes:hidden name="mcrobjid_base" />


				<%-- load first time from request parameter "returnPath --%>

		
				
				<div class="panel panel-info">
  					<div class="panel-heading">
  						<a class="btn btn-default pull-right" style="margin-top:-8px;" href="${baseURL}showWorkspace.action?mcrobjid_base=${actionBean.mcrobjid_base}"><span class="glyphicon glyphicon-off"></span> Zurück</a>
					  	<h3 class="panel-title"><span class="badge">${currentVariables.mcrObjectID}</span> ${currentVariables.wfObjectDisplayTitle}</h3>
  					</div>
  					<div class="panel-body">
    					Übersicht über Derivate
    				</div>
    				<div class="panel-footer container-fluid">
    					<div class="row">
  							<div class="col-md-2" style="margin-top:0px"><h4>Neues Derivat:</h4></div>
  							<div class="col-md-8">
								<div class="form-horizontal" role="form">
  									<div class="form-group">
    									<label for="inputLabel" class="col-sm-1 control-label">Label</label>
   										<div class="col-sm-11">
      										<select class="form-control" name="newDerivate_label-task_${actionBean.taskid}">
  												<option value="fulltext">Volltext</option>
 												<option value="attachement">Anhang</option>
  											</select>
    									</div>
  									</div>
  									<div class="form-group">
    									<label for="inputTitle" class="col-sm-1 control-label">Titel</label>
    									<div class="col-sm-11">
      										<input type="text" name="newDerivate_title-task_${actionBean.taskid}" class="form-control" id="inputTitle" placeholder="Titel"></input>
    									</div>
  									</div>
  									<div class="form-group">
    									<label for="inputFile" class="col-sm-1 control-label">Datei</label>
    									<div class="col-sm-11">
      										<input type="file" name="newDerivate_file-task_${actionBean.taskid}" class="form-control" style="height:auto" id="inputFile" placeholder="Datei"></input>
    									</div>
  									</div>
  								</div>
							</div>
  							<div class="col-md-2">
								<stripes:submit class="btn btn-default" style="width:100%" name="doCreateNewDerivate-task_${actionBean.taskid}" value="Erstellen"/>
							</div>
						</div>
    				</div>
				</div>
			
					
			
				
			</stripes:form>

	</stripes:layout-component>
</stripes:layout-render>
