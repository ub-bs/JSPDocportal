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
<stripes:layout-render name="/WEB-INF/layout/default.jsp" pageTitle="${pageTitle}" layout="2columns_right_wide">
	<stripes:layout-component name="html_header">
		<title>${pageTitle}@ <fmt:message key="Webpage.title" /></title>
		<link type="text/css" rel="stylesheet" href="${WebApplicationBaseURL}css/style_docdetails.css">
		<link type="text/css" rel="stylesheet" href="${WebApplicationBaseURL}css/style_searchresult.css">
		<link type="text/css" rel="stylesheet" href="${WebApplicationBaseURL}modules/shariff/shariff.complete.css">
		<style>
			div.shariff span.share_text{
				display:none;
			}
	
			div.shariff li.shariff-button{
				display:inline;
				min-width:30px !important;
				margin-left:0px;
				flex-grow: 0;
			}

			div.shariff a{
				display:inline-block;
				height: 30px;
				width: 30px;
			}
		</style>
	</stripes:layout-component>
	
	<stripes:layout-component name="contents">
		<div class="row">
			<div class="col-sm-12">
				<div class="ir-box ir-docdetails-header">
					<mcr:transformXSL xml="${doc}" xslt="xsl/docdetails/${objectType}2header_html.xsl" />
				</div>
			</div>			
		</div>
	
		<div class="row">
			<div class="col-sm-12">
				<div class="ir-box">
					<ul id="main_navbar" class="nav nav-tabs">
						<x:if select="$doc/mycoreobject[not(contains(@ID, '_bundle_'))]/structure/derobjects/derobject[@xlink:title='fulltext' or @xlink:title='DV_METS']">
							<li id="nav_fulltext" role="presentation"><a data-toggle="collapse" href="#div_fulltext">Volltext</a></li>
  						</x:if>
  						<x:if select="contains($doc/mycoreobject/@ID, '_bundle_')">
  							<li id="nav_structure" role="presentation"><a data-toggle="collapse" href="#div_structure">Strukturbaum</a></li>
						</x:if>
						<li id="nav_metadata" role="presentation"><a data-toggle="collapse" href="#div_metadata">Metadaten</a></li>
						<x:if select="$doc/mycoreobject/structure/derobjects/derobject">
							<li id="nav_files" role="presentation"><a data-toggle="collapse" href="#div_files">Dateien</a></li>
						</x:if>
					</ul>
				</div>
			</div>
		</div>
		<div id="main_display" class="row">
		<x:if select="$doc/mycoreobject[not(contains(@ID, '_bundle_'))]/structure/derobjects/derobject[@xlink:title='fulltext' or @xlink:title='DV_METS']">
			<div id="div_fulltext" class="collapse col-sm-12">
				<x:if select="$doc/mycoreobject/structure/derobjects/derobject[@xlink:title='fulltext']">
					<search:mcrviewer mcrid="${param.id}" recordIdentifier="${param.id}" doctype="pdf" id="divMCRViewer_2" />
					<div id="divMCRViewer_2" style="height:600px; margin:0px 16px; position:relative;"></div>
				</x:if>
				<x:if select="$doc/mycoreobject/structure/derobjects/derobject[@xlink:title='DV_METS']">
					<c:set var="recordidentifier"><x:out select="$doc/mycoreobject/metadata/def.modsContainer/modsContainer/mods:mods/mods:recordInfo/mods:recordIdentifier" /></c:set>
					<search:mcrviewer mcrid="${param.id}" recordIdentifier="${recordidentifier}" doctype="mets" id="divMCRViewer_1" />
					<div id="divMCRViewer_1" style="height:600px; margin:0px 16px; position:relative;"></div>
				</x:if>		
			</div>
		</x:if>
		<x:if select="contains($doc/mycoreobject/@ID, '_bundle_')">
			<div id="div_structure" class="collapse col-sm-12">
				<p>TODO</p>
			</div>
		</x:if>
		<div id="div_metadata" class="collapse col-sm-12">
			<div class="ir-box ir-docdetails-data">
				<mcr:transformXSL xml="${doc}" xslt="xsl/docdetails/${objectType}2details_html.xsl" />
			</div>
		</div>
		<x:if select="$doc/mycoreobject/structure/derobjects/derobject">
			<div id="div_files" class="collapse col-sm-12">
				<div class="ir-box">
			 		<x:forEach var="x" select="$doc/mycoreobject/structure/derobjects/derobject/@xlink:href">
			 			<c:set var="id"><x:out select="$x" /></c:set>
			 			<search:derivate-list derid="${id}" showSize="true" />
			 		</x:forEach>
			 	</div>
			</div>
		</x:if>
		</div>
<script type="text/javascript">
	$(document).ready(function(){
		$('#main_navbar li:first-child').addClass('active');
		$('#main_navbar li:first-child a').attr('href', '#');
		$('#main_display div:first-child').collapse('show');
		
		$('#div_fulltext').on('show.bs.collapse', function () {
			$('#nav_metadata').removeClass('active');
			$('#nav_metadata a').attr('href', '#div_metadata');
			$('#nav_structure').removeClass('active');
			$('#nav_structure a').attr('href', '#div_structure');
			$('#nav_files').removeClass('active');
			$('#nav_files a').attr('href', '#div_files');
			
			$('#nav_fulltext').addClass('active');
			$('#nav_fulltext a').attr('href', '#');
			
			$('#div_metadata').collapse('hide');
			$('#div_structure').collapse('hide');
			$('#div_files').collapse('hide');
		});
		$('#div_metadata').on('show.bs.collapse', function () {
			$('#nav_fulltext').removeClass('active');
			$('#nav_fulltext a').attr('href', '#div_fulltext');
			$('#nav_structure').removeClass('active');
			$('#nav_structure a').attr('href', '#div_structure');
			$('#nav_files').removeClass('active');
			$('#nav_files a').attr('href', '#div_files');
			
			$('#nav_metadata').addClass('active');
			$('#nav_metadata a').attr('href', '#');
			
			$('#div_fulltext').collapse('hide');
			$('#div_structure').collapse('hide');
			$('#div_files').collapse('hide');
		});
		$('#div_structure').on('show.bs.collapse', function () {
			$('#nav_fulltext').removeClass('active');
			$('#nav_fulltext a').attr('href', '#div_fulltext');
			$('#nav_metadata').removeClass('active');
			$('#nav_metadata a').attr('href', '#div_metadata');
			$('#nav_files').removeClass('active');
			$('#nav_files a').attr('href', '#div_files');

			$('#nav_structure').addClass('active');
			$('#nav_structure a').attr('href', '#');
			
			$('#div_fulltext').collapse('hide');
			$('#div_metadata').collapse('hide');
			$('#div_files').collapse('hide');
		});
		$('#div_files').on('show.bs.collapse', function () {
			$('#nav_fulltext').removeClass('active');
			$('#nav_fulltext a').attr('href', '#div_fulltext');
			$('#nav_metadata').removeClass('active');
			$('#nav_metadata a').attr('href', '#div_metadata');
			$('#nav_structure').removeClass('active');
			$('#nav_structure a').attr('href', '#div_structure');
			
			$('#nav_files').addClass('active');
			$('#nav_files a').attr('href', '#');
			
			$('#div_fulltext').collapse('hide');
			$('#div_metadata').collapse('hide');
			$('#div_structure').collapse('hide');
		});
	});

</script>
	<div id="main_footer" class="row" style="margin-top:24px">
		<div class="col-sm-12">
			<div class="ir-box ir-docdetails-data">
				<mcr:transformXSL xml="${doc}" xslt="xsl/docdetails/all2footer_html.xsl" />
			</div>
		</div>
	</div>
		

	</stripes:layout-component>
	<stripes:layout-component name="right_side">
		<div class="ir-box ir-box-bordered ir-infobox" style="margin-bottom:32px; padding: 18px 6px 6px 6px;">
			<search:result-navigator mcrid="${mcrid}" mode="one_line"/>
			
			<div class="container-fluid">
				<div class="pull-right">
    	   			<button type="button" class="btn btn-default btn-sm pull-right hidden-xs" style="border:none;color:#DEDEDE;" 
    	   		        data-toggle="collapse" data-target="#hiddenTools" title="<fmt:message key="Webpage.tools.menu4experts" />">
   						<span class="glyphicon glyphicon-wrench"></span>
       				</button>
       				<search:show-edit-button mcrid="${mcrid}" cssClass="btn btn-sm btn-primary ir-edit-btn col-xs-3" />
   				</div>
   				<div id="hiddenTools" class="collapse">
   					<div class="row" style="padding-bottom:6px">
   						<a class="btn btn-warning btn-sm" target="_blank" title="<fmt:message key="Webpage.tools.showXML" />"
		   		   		   href="${WebApplicationBaseURL}api/v1/objects/${mcrid}" rel="nofollow">XML</a>
       					<a class="btn btn-warning btn-sm" style="margin-left:6px" target="_blank" title="<fmt:message key="Webpage.tools.showSOLR" />"
			   		   		href="${WebApplicationBaseURL}receive/${mcrid}?XSL.Style=solrdocument" rel="nofollow">SOLR</a>
			   		   		
			   		   	<x:if select="$doc/mycoreobject/structure/derobjects/derobject[@xlink:title='REPOS_METS']">
						 	<c:set var="derid"><x:out select="$doc/mycoreobject/structure/derobjects/derobject[@xlink:title='REPOS_METS']/@xlink:href" /></c:set>
						 	<a class="btn btn-warning btn-sm" style="margin-left:6px" target="_blank" href="${WebApplicationBaseURL}api/v1/objects/${param.id}/derivates/${derid}/open" class="btn btn-default" title="<fmt:message key="Webpage.tools.showREPOS_METS" />">METS</a>
						</x:if>
    	  			</div>
   				</div>
   			</div>
   		</div>
   		<div class="ir-box ir-box-bordered ir-infobox" style="margin-bottom:32px; padding: 18px 6px 6px 6px;">
   		 <x:forEach var="x" select="$doc/mycoreobject/metadata/def.modsContainer/modsContainer/mods:mods/mods:identifier[@type='purl']">
			  	<div class="btn-group btn-group-sm ir-btn-group-resolving" role="group" aria-label="...">
			  		<button type="button" class="btn btn-default">PURL:</button>
			  		<a class="btn btn-default"  href="<x:out select="$x" />"><x:out select="$x" /></a>
			  	</div>
			  </x:forEach>
			  <x:forEach var="x" select="$doc/mycoreobject/metadata/def.modsContainer/modsContainer/mods:mods/mods:identifier[@type='urn']">
			  	<div class="btn-group btn-group-sm ir-btn-group-resolving" role="group" aria-label="...">
			  		<button type="button" class="btn btn-default">URN:</button>
			  		<a class="btn btn-default"  href="http://nbn-resolving.org/<x:out select="$x" />"><x:out select="$x" /></a>
			  	</div>
			  </x:forEach>
			  <x:forEach var="x" select="$doc/mycoreobject/metadata/def.modsContainer/modsContainer/mods:mods/mods:identifier[@type='doi']">
			  	<div class="btn-group btn-group-sm ir-btn-group-resolving" role="group" aria-label="...">
			  		<button type="button" class="btn btn-default">DOI:</button>
			  		<a class="btn btn-default"  href="http://dx.doi.org/<x:out select="$x" />"><x:out select="$x" /></a>
			  	</div>
			  </x:forEach>
			  <br />
			  <br />
			  <div class="shariff" data-url="${WebApplicationBaseURL}resolve/id/${param.id}"
			       data-services="[&quot;twitter&quot;, &quot;facebook&quot;, &quot;googleplus&quot;, &quot;linkedin&quot;, &quot;xing&quot;, &quot;whatsapp&quot;, &quot;mail&quot;, &quot;info&quot;]"
			       data-mail-url="mailto:" data-mail-subject="Dokument auf RosDok" data-mail-body="${WebApplicationBaseURL}resolve/id/${param.id}"
			       data-orientation="horizontal" data-theme="standard"></div> <%--data-theme=standard|grey|white --%>
			   <script src="${WebApplicationBaseURL}modules/shariff/shariff.min.js"></script>   			  
   		</div>
   		<div class="ir-box ir-box-bordered ir-infobox" style="margin-bottom:32px; padding: 18px 6px 6px 6px;">
			<x:if select="$doc/mycoreobject/structure/derobjects/derobject[@xlink:title='fulltext']">
				<a class="btn btn-default ir-button-download pull-right"  
			  	   href="${WebApplicationBaseURL}resolve/id/${mcrid}/file/fulltext" target="_blank">
			  		<img style="vertical-align:middle;" src="${WebApplicationBaseURL}images/pdf_icon.png" title = "<fmt:message key="Webpage.docdetails.pdfdownload" />" />
			  			<fmt:message key="Webpage.docdetails.pdfdownload" />
				</a>
			</x:if>
			<x:if select="$doc/mycoreobject/structure/derobjects/derobject[@xlink:title='DV_METS']">
				<c:set var="recordID"><x:out select="$doc/mycoreobject/metadata/def.modsContainer/modsContainer/mods:mods/mods:recordInfo/mods:recordIdentifier[@source='DE-28']" /></c:set>
				<c:if test="${not empty recordID}">
					<a class="btn btn-default ir-button-download pull-right"  
			  		   href="${WebApplicationBaseURL}pdfdownload/recordIdentifier/${fn:replace(recordID, '/','%252F')}" target="_blank">
			  	    	<img style="vertical-align:middle;" src="${WebApplicationBaseURL}images/pdf_icon.png" title = "<fmt:message key="Webpage.docdetails.pdfdownload" />" />
			  			<fmt:message key="Webpage.docdetails.pdfdownload" />
			  		</a>
				</c:if>
			</x:if>
			<x:if select="$doc/mycoreobject/structure/derobjects/derobject[@xlink:title='DV_METS']">
			<a class="btn btn-default ir-button-download"  
			   href="${WebApplicationBaseURL}resolve/id/${mcrid}/dfgviewer" target="_blank">
			  	<img style="vertical-align:middle;height: 28px;margin-right:6px;" src="${WebApplicationBaseURL}images/dfg_icon.png" title = "<fmt:message key="Webpage.docdetails.dfgviewer" />" />
			  <fmt:message key="Webpage.docdetails.viewer" />
			 </a>
			 </x:if>
			 <div style="clear:both"></div>
			</div>
			
			<x:if select="$doc/mycoreobject/structure/derobjects/derobject[@xlink:title='cover']">
				<div class="ir-box ir-box-bordered ir-infobox" style="margin-bottom:32px; padding: 18px 6px 6px 6px;">
					<search:derivate-image mcrid="${param.id}" width="50%" labelContains="cover" />
				</div>
			</x:if>
		
	</stripes:layout-component>
</stripes:layout-render>
