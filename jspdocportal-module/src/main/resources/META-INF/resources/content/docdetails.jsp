<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="org.apache.log4j.Logger"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld"%>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>
<%@ taglib prefix="jspdp-ui" tagdir="/WEB-INF/tags/ui"%>
<%@ taglib prefix="search" tagdir="/WEB-INF/tags/search"%>

<c:set var="WebApplicationBaseURL" value="${applicationScope.WebApplicationBaseURL}" />
<c:set var="mcrid">
	<c:choose>
		<c:when test="${!empty(requestScope.id)}">${requestScope.id}</c:when>
		<c:otherwise>${param.id}</c:otherwise>
	</c:choose>
</c:set>
<c:set var="from" value="${param.fromWF}" />
<c:set var="debug" value="${param.debug}" />
<c:set var="style" value="${param.style}" />

<c:set var="objectType" value="${fn:substringBefore(fn:substringAfter(mcrid, '_'),'_')}" />

<mcr:retrieveObject mcrid="${mcrid}" fromWorkflow="${param.fromWF}" varDOM="doc" />
<fmt:message var="pageTitle" key="OMD.headline">
	<fmt:param>${mcrid}</fmt:param>
</fmt:message>
<stripes:layout-render name="/WEB-INF/layout/default.jsp" pageTitle="${pageTitle}" layout="1column">
	<stripes:layout-component name="html_header">
		<title>${pageTitle}@ <fmt:message key="Webpage.title" /></title>
		<link type="text/css" rel="stylesheet" href="${WebApplicationBaseURL}css/style_docdetails.css">
		<link type="text/css" rel="stylesheet" href="${WebApplicationBaseURL}css/style_searchresult.css">
	</stripes:layout-component>
	<stripes:layout-component name="contents">
		<div class="row">
			<div class="col-md-9">
				<div class="row">
					<div class="col-sm-9">
						<div class="ur-box ir-docdetails-header">
							<mcr:transformXSL xml="${doc}" xslt="xsl/docdetails/${objectType}2header_html.xsl" />
							<span class="btn btn-default ir-button-download pull-right">  
			  					<a href="${WebApplicationBaseURL}resolve/id/${mcrid}/file/fulltext" target="_blank">
			  						<img style="vertical-align:middle;" src="${WebApplicationBaseURL}images/pdf_icon.png" title = "<fmt:message key="Webpage.docdetails.pdfdownload" />" />
			  							<fmt:message key="Webpage.docdetails.pdfdownload" /></a>    
							</span>
						</div>
					</div>
					<div class="col-sm-3">
						<div class="pull-right">
							<search:derivate-image mcrid="${param.id}" width="100%" labelContains="cover" />
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-12">
						<hr />
					</div>
					<div class="col-sm-12">
						<div class="ur-box ir-docdetails-data">
							<mcr:transformXSL xml="${doc}" xslt="xsl/docdetails/${objectType}2details_html.xsl" />
						</div>
					</div>
				</div>
			</div>
			<div class="col-md-3">
				<div class="ur-box">
					<search:result-navigator mcrid="${mcrid}" />
					
					<div class="docdetails-toolbar">
						<c:if test="${empty(param.print) and !fn:contains(style,'user')}">
							<div class="docdetails-toolbar-item">
								<a href="${WebApplicationBaseURL}content/print_details.jsp?id=${param.id}&fromWF=${param.fromWF}" target="_blank"> <img
									src="${WebApplicationBaseURL}images/workflow_print.gif" border="0" alt="<fmt:message key="WF.common.printdetails" />" class="imagebutton" height="30" />
								</a>
							</div>
						</c:if>
						<c:if test="${(not from) && !fn:contains(style,'user')}">
							<search:show-edit-button mcrid="${mcrid}" />
							<%-- 
 					<mcr:checkAccess var="modifyAllowed" permission="writedb" key="${mcrid}" />
    				<mcr:isObjectNotLocked var="bhasAccess" objectid="${mcrid}" />
    				<c:if test="${modifyAllowed}">
      					<c:choose>
    						<c:when test="${bhasAccess}"> 
	         					
	         					<div class="docdetails-toolbar-item">
	         						<form method="get" action="${WebApplicationBaseURL}StartEdit" class="resort">                 
	            						<input name="mcrid" value="${mcrid}" type="hidden"/>
										<input title="<fmt:message key="WF.common.object.EditObject" />" src="${WebApplicationBaseURL}images/workflow1.gif" type="image"  class="imagebutton" height="30" />
	         						</form>
	         					</div>
         					</c:when>
         					<c:otherwise>
         						<div class="docdetails-toolbar-item">
         	  						<img title="<fmt:message key="WF.common.object.EditObjectIsLocked" />" border="0" src="${WebApplicationBaseURL}images/workflow_locked.gif" height="30" />
         	  					</div>
         					</c:otherwise>
        				</c:choose>  
        			</c:if>
        			--%>
						</c:if>
						<div style="clear: both;"></div>
					</div>
				</div>
			</div>
		</div>
	</stripes:layout-component>
</stripes:layout-render>
