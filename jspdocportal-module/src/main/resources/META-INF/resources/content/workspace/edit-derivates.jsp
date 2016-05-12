<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8"%>
<%@page import="org.mycore.frontend.jsp.stripes.actions.EditDerivatesAction"%>
<%@page import="org.mycore.activiti.MCRActivitiMgr"%>
<%@page import="org.mycore.frontend.servlets.MCRServlet"%>
<%@page import="org.mycore.common.MCRSessionMgr"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld" %>
<%@ taglib prefix="mcrdd" uri="http://www.mycore.org/jspdocportal/docdetails.tld" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>


<%--Parameter: objectType --%>

<fmt:message var="pageTitle" key="WF.EditDerivates" /> 
<mcrdd:setnamespace prefix="xlink" uri="http://www.w3.org/1999/xlink" />
<stripes:layout-render name="../../WEB-INF/layout/default.jsp" pageTitle = "${pageTitle}" layout="2columns">
	<stripes:layout-component name="html_header">
		<link type="text/css" rel="stylesheet" href="${WebApplicationBaseURL}css/style_workspace.css" />
		<script type="text/javascript">
			function enableDerMetaEditing(derID){
				$('#btnEditDerMetaSave_'+derID).show();
				$('#btnEditDerMetaCancel_'+derID).show();
				$('#btnEditDerMetaEdit_'+derID).hide();
				$('#selectEditDerMetaLabel_'+derID).prop('disabled', false);
				$('#txtEditDerMetaTitle_'+derID).prop('disabled', false);
			}

			function disableDerMetaEditing(derID){
				$('#btnEditDerMetaSave_'+derID).hide();
				$('#btnEditDerMetaCancel_'+derID).hide();
				$('#btnEditDerMetaEdit_'+derID).show();

				$('#selectEditDerMetaLabel_'+derID).val($('#selectEditDerMetaLabel_'+derID).data('original-value'));
				$('#txtEditDerMetaTitle_'+derID).val($('#txtEditDerMetaTitle_'+derID).data('original-value'));
				$('#selectEditDerMetaLabel_'+derID).prop('disabled', true);
				$('#txtEditDerMetaTitle_'+derID).prop('disabled', true);
			}

			function renameFile(derid, filename){
				var x = prompt('Geben Sie einen neuen Dateinamen an:', filename);
				if(x!=null){
					document.getElementById('hiddenRenameFileNew_'+derid+'_'+filename).value = x;
					//setting value of hidden input seems to be buggy in jquery
					//$('#hiddenRenameFileNew_'+derid+'_'+filename).val(x)
					return true;
				}
				return false;
			}
				
		</script>
	</stripes:layout-component>
	<stripes:layout-component name="contents">
		<div class="ir-box">
		 	<h2><fmt:message key="WF.EditDerivates" /></h2>
		</div>
		<div class="ir-box">	
			<c:set  var="baseURL" value="${applicationScope.WebApplicationBaseURL}" />
			<%request.setAttribute("currentVariables", MCRActivitiMgr.getWorfklowProcessEngine().getTaskService().getVariables(((EditDerivatesAction)request.getAttribute("actionBean")).getTaskid())); %>
			
			<stripes:form
				beanclass="org.mycore.frontend.jsp.stripes.actions.EditDerivatesAction"
				id="workspaceForm" enctype="multipart/form-data" acceptcharset="UTF-8">
				<stripes:hidden name="mcr_base" />
				<%-- load first time from request parameter "returnPath --%>
				<div class="panel panel-primary">
  					<div class="panel-heading" style="min-height:54px">
  						<a class="btn btn-default pull-right" href="${baseURL}showWorkspace.action?mcr_base=${actionBean.mcr_base}"><span class="glyphicon glyphicon-off"></span> Zurück</a>
					  	<span class="badge pull-left" style="margin-right:24px;margin-top:9px;">${currentVariables.mcrObjectID}</span>
					  	<h3 class="pull-left" style="margin-top:6px">
					  		${currentVariables.wfObjectDisplayTitle}
					  	</h3>
  					</div>
    				<ul class="list-group">
    					<c:set var="doc" value="${actionBean.mcrobjXML}" />
    					<x:forEach var="x" select="$doc/mycoreobject/structure/derobjects/derobject">
    						<c:set var="derID"><x:out select="$x/@xlink:href" /></c:set>
    						<c:set var="derDoc" value="${actionBean.derivateXMLs[derID]}" />
    						<c:set var="maindoc"><x:out select="$derDoc/mycorederivate/derivate/internals/internal/@maindoc" /></c:set>
    						<li class="list-group-item container-fluid">
    							<div class="panel panel-info">
  									<div class="panel-heading" style="min-height:78px">
  										<button id="btnDeleteDerivate_${derID}_${f}" title="Derivate löschen" name="doDeleteDerivate-task_${actionBean.taskid}-derivate_${derID}" 
    									        onclick="return confirm('Wollen Sie das Derivate &quot;${derID}&quot; wirklich löschen?');" class="btn btn-danger pull-right"><span class="glyphicon glyphicon-trash"></span></button>
  											
  										<div class="pull-right" style="margin-right:48px">
  											<button id="btnEditDerMetaSave_${derID}" name="doSaveDerivateMeta-task_${actionBean.taskid}-derivate_${derID}" style="display:none;" class="btn btn-sm btn-primary"><span class="glyphicon glyphicon-floppy-disk"></span> Speichern</button>
  											<button id="btnEditDerMetaCancel_${derID}" type="button" style="display:none; border:1px solid darkgray;" class="btn btn-sm" onclick="disableDerMetaEditing('${derID}')"><span class="glyphicon glyphicon-remove"></span> Abbrechen</button>
  											<button id="btnEditDerMetaEdit_${derID}" type="button" class="btn btn-default" onclick="enableDerMetaEditing('${derID}')"><span class="glyphicon glyphicon-pencil"></span> Bearbeiten</button>
  										</div>
  										
  										<div class="pull-right" style="margin-right:48px">
  											<x:if select="$doc/mycoreobject/structure/derobjects/derobject[1]/@xlink:href != $x/@xlink:href">
  												<button id="btnEditDerMoveUp_${derID}" name="doMoveUpDerivate-task_${actionBean.taskid}-derivate_${derID}" class="btn btn-default" title="Nach oben verschieben"><span class="glyphicon glyphicon-arrow-up"></span></button>
  											</x:if>
  											<x:if select="$doc/mycoreobject/structure/derobjects/derobject[last()]/@xlink:href != $x/@xlink:href">
  												<button id="btnEditDerMoveUp_${derID}" name="doMoveDownDerivate-task_${actionBean.taskid}-derivate_${derID}" class="btn btn-default" title="Nach unten verschieben"><span class="glyphicon glyphicon-arrow-down"></span></button>
  											</x:if>
  										</div>
  										<c:set var="derDoc" value="${actionBean.derivateXMLs[derID]}" />
  										<span class="badge pull-left" style="margin-right:24px; margin-top:6px;">${derID}</span>
  										<h4 class="panel-title">
  											<c:set var="derLabel"><x:out select="$derDoc/mycorederivate/@label" /></c:set>
  											<select id="selectEditDerMetaLabel_${derID}" name="saveDerivateMeta_label-task_${actionBean.taskid}-derivate_${derID}" 
  											        class="form-control" disabled="disabled" style="width:33%;" data-original-value="${derLabel}">
  											<c:set var="values"><fmt:message key="OMD.derivatelabel.${actionBean.mcr_base}" /></c:set>
  											<c:forEach var="key" items="${fn:split(values,',')}">
  												<c:if test="${key eq derLabel}">
  													<option value="${key}" selected="selected"><fmt:message key="OMD.derivatelabel.${actionBean.mcr_base}.${key}" /></option>
  												</c:if>
  												<c:if test="${not(key eq derLabel)}">
  													<option value="${key}"><fmt:message key="OMD.derivatelabel.${actionBean.mcr_base}.${key}" /></option>
  												</c:if>
  											</c:forEach>
  											</select>
  											<c:set var="derTitle"><x:out select="$derDoc/mycorederivate/service/servflags/servflag[@type='title']" /></c:set>
									  		<input id="txtEditDerMetaTitle_${derID}" name="saveDerivateMeta_title-task_${actionBean.taskid}-derivate_${derID}" type="text" class="form-control" style="margin-top:12px" disabled="disabled" value="${derTitle}" data-original-value="${derTitle}" />
									  	</h4>					
  									</div>
    								<div class="panel-body">
    									<ul style="list-style:none;margin-bottom:-9px;">
    										<c:forEach var="f" items="${actionBean.derivateFiles[derID]}">
    											<li style="margin-bottom:9px;">
    											<c:choose>
    												<c:when test="${fn:endsWith(fn:toLowerCase(f), '.xml')}">
    													<img src="${WebApplicationBaseURL}images/fileicons/xml.png" style="height:48px" />		
    												</c:when>
    												<c:when test="${fn:endsWith(fn:toLowerCase(f), '.htm') or fn:endsWith(fn:toLowerCase(f), '.html')}">
    													<img src="${WebApplicationBaseURL}images/fileicons/html.png"  style="height:48px"  />		
    												</c:when>
    												<c:when test="${fn:endsWith(fn:toLowerCase(f), '.pdf')}">
    													<img src="${WebApplicationBaseURL}images/fileicons/pdf.png" style="height:48px" />		
    												</c:when>
    												<c:when test="${fn:endsWith(fn:toLowerCase(f), '.zip')}">
    													<img src="${WebApplicationBaseURL}images/fileicons/compressed.png" style="height:48px" />		
    												</c:when>
    												<c:when test="${fn:endsWith(fn:toLowerCase(f), '.tif') or fn:endsWith(fn:toLowerCase(f), '.tiff') or fn:endsWith(fn:toLowerCase(f), '.jpg') or fn:endsWith(fn:toLowerCase(f), '.jpeg') or fn:endsWith(fn:toLowerCase(f), '.png')}">
    													<img src="${WebApplicationBaseURL}images/fileicons/image.png" style="height:48px" />		
    												</c:when>
    												<c:otherwise>
    													<img src="${WebApplicationBaseURL}images/fileicons/fileicon_bg.png" style="height:48px" />
    												</c:otherwise>
    											</c:choose>
    											${f}
    											<c:if test="${maindoc eq f}">
    												<c:set var="info"><fmt:message key="Editor.Common.derivate.maindoc" /></c:set>
    												<span style="margin-left:24px" class="badge glyphicon glyphicon-star" title="${info}">&nbsp;</span>
    											</c:if>
    											<input type="hidden" id="hiddenRenameFileNew_${derID}_${f}" name="renameFile_new-task_${actionBean.taskid}-derivate_${derID}-file_${f}" value="${f}" />
    											<button id="btnDeleteFile_${derID}_${f}" title="Datei löschen" name="doDeleteFile-task_${actionBean.taskid}-derivate_${derID}-file_${f}" 
    											        onclick="return confirm('Wollen Sie die Datei &quot;${f}&quot; wirklich löschen?');" class="btn btn-sm btn-danger pull-right" style="margin-left:48px"><span class="glyphicon glyphicon-trash"></span></button>
  												<button id="btnRenameFile_${derID}_${f}" title="Datei umbenennen" name="doRenameFile-task_${actionBean.taskid}-derivate_${derID}-file_${f}" 
    											        onclick="return renameFile('${derID}', '${f}');" class="btn btn-sm pull-right" style="border:1px solid darkgrey"><span class="glyphicon glyphicon-pencil"></span></button>
  												</li>
    										</c:forEach>
    									</ul>
    								</div>
    								<div class="panel-footer">
    									<div class="form-horizontal" role="form">
    										<div class="row">
    											<label for="inputAddFile_${derID}" class="col-sm-2 control-label">Datei hochladen</label>
    											<div class="col-sm-7">
      												<input type="file" name="addFile_file-task_${actionBean.taskid}-derivate_${derID}" class="form-control" style="height:auto" id="inputAddFile_${derID}" placeholder="Datei"></input>
    											</div>
    											<div class="col-sm-2">
													<stripes:submit class="btn btn-default" name="doAddFile-task_${actionBean.taskid}-derivate_${derID}" value="Hochladen"/>
												</div>
  											</div>
										</div>
  									</div>
  								</div>
    						</li>
    					</x:forEach>
    				</ul>
    				<div class="panel-footer container-fluid">
    					<div class="row">
  							<div class="col-md-2" style="margin-top:0px"><h4>Neues Derivat</h4></div>
  							<div class="col-md-8">
								<div class="form-horizontal" role="form">
  									<div class="form-group">
    									<label for="inputLabel" class="col-sm-1 control-label">Label</label>
   										<div class="col-sm-11">
      										<select class="form-control" name="newDerivate_label-task_${actionBean.taskid}">
  												<c:set var="values"><fmt:message key="OMD.derivatelabel.${actionBean.mcr_base}" /></c:set>
  												<c:forEach var="key" items="${fn:split(values,',')}">
  													<option value="${key}"><fmt:message key="OMD.derivatelabel.${actionBean.mcr_base}.${key}" /></option>
  												</c:forEach>
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
		</div>
	</stripes:layout-component>
</stripes:layout-render>
