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
<%@ taglib prefix="mcrdd" 	uri="http://www.mycore.org/jspdocportal/docdetails.tld"%>

<mcrdd:setnamespace prefix="xlink" uri="http://www.w3.org/1999/xlink" />
<mcrdd:setnamespace prefix="mods" uri="http://www.loc.gov/mods/v3" />
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

		<script src="${applicationScope.WebApplicationBaseURL}webjars/jquery/2.1.1/jquery.min.js" type="text/javascript"></script>
		<script src="${applicationScope.WebApplicationBaseURL}webjars/bootstrap/3.3.6/js/bootstrap.min.js" type="text/javascript"></script>

		
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
							<span class="btn btn-default ir-button-download">  
			  					<a href="${WebApplicationBaseURL}mcrviewer/id/${mcrid}" target="_blank">
			  						<img style="vertical-align:middle;height: 30px;width:30px" src="${WebApplicationBaseURL}images/mycore_logo_abstract_48x48.png" title = "<fmt:message key="Webpage.docdetails.mcrviewer" />" />
			  							<fmt:message key="Webpage.docdetails.mcrviewer" /></a>    
							</span>
						</div>
					</div>
					<div class="col-sm-3">
						<div class="pull-right">
							<search:derivate-image mcrid="${param.id}" width="100%" labelContains="cover" />
						</div>
					</div>
				</div>
				
				<x:if select="$doc/mycoreobject/structure/derobjects/derobject[@xlink:title='fulltext']">
				<div class="row">
					<div class="col-sm-9">
						<hr />
					</div>
					<search:mcrviewer mcrid="${param.id}" recordidentifier="${param.id}" doctype="pdf" id="divMCRViewer_2" />
					<div id="divMCRViewer_2" class="col-sm-9" style="height:600px;"></div>
				</div>
				</x:if>
				
				<x:if select="$doc/mycoreobject/structure/derobjects/derobject[@xlink:title='DV_METS']">
				<c:set var="recordidentifier"><x:out select="$doc/mycoreobject/metadata/def.modsContainer/modsContainer/mods:mods/mods:recordInfo/mods:recordIdentifier" /></c:set>
				
				<div class="row">
					<div class="col-sm-9">
						<hr />
					</div>
					<search:mcrviewer mcrid="${param.id}" recordidentifier="${recordidentifier}" doctype="mets" id="divMCRViewer_1" />
					<div id="divMCRViewer_1" class="col-sm-9" style="height:600px;"></div>
				</div>
				</x:if>
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
						<c:if test="${empty(param.print)}">
								<a href="${WebApplicationBaseURL}content/print_details.jsp?id=${param.id}&fromWF=${param.fromWF}" target="_blank"
								 class="btn btn-default" title="<fmt:message key="WF.common.printdetails" />"> <span class="glyphicon glyphicon-print"></span></a>
								 <a href="${WebApplicationBaseURL}api/v1/objects/${param.id}" class="btn btn-default" ><span class="glyphicon glyphicon-cog"></span></a>

								<c:if test="${(not from)}">
									<search:show-edit-button mcrid="${mcrid}" />
								</c:if>
						</c:if>
						
					</div>
				</div>
			</div>
		</div>
	</stripes:layout-component>
</stripes:layout-render>
