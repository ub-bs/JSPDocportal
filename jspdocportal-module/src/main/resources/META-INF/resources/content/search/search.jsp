<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c"       uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="x"       uri="http://java.sun.com/jsp/jstl/xml"%>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn"      uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="mcr" 	uri="http://www.mycore.org/jspdocportal/base.tld"%>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>
	
<%@ taglib prefix="search" tagdir="/WEB-INF/tags/search"%>

<fmt:message var="pageTitle" key="Webpage.search.title.${actionBean.result.mask}" />
<stripes:layout-render name="/WEB-INF/layout/default.jsp" pageTitle="${pageTitle}" layout="2columns">
	<stripes:layout-component name="html_header">
		<link type="text/css" rel="stylesheet" href="${WebApplicationBaseURL}themes/ir/css/style_ir.css" />	
	</stripes:layout-component>
	<stripes:layout-component name="contents">
		<c:if test="${not empty actionBean.result.mask}">
			<div class="ir-box">
				<c:set var="classCollapse" value="" />
				<c:if test="${not actionBean.showMask and actionBean.result.numFound>0}">
					<button id="buttonCollapseSearchmask" class="btn btn-default pull-right" type="button"
						    data-toggle="collapse" data-target="#searchmask" aria-expanded="false" aria-controls="searchmask">
						<fmt:message key="Webpage.Searchresult.redefine" />
					</button>
					<c:set var="classCollapse">collapse</c:set> 
				</c:if>
			
				<h2>${pageTitle}</h2>
				<div>
					<mcr:includeWebcontent id="search_intro" file="search/${actionBean.result.mask}.html" />
				</div>

				<div class="${classCollapse}" id="searchmask">
					<c:out value="${actionBean.xeditorHtml}" escapeXml="false" />
				</div>
				<script type="text/javascript">
              		$('#searchmask').on('show.bs.collapse', function () {
            			$('#buttonCollapseSearchmask').hide();
        			})
            	 </script>
			</div>
		</c:if>
		<c:if test="${actionBean.showResults}">
			<div class="ir-box">
			  	<search:result-browser result="${actionBean.result}">
			  		<c:set var="doctype" value="${fn:substringBefore(fn:substringAfter(mcrid, '_'),'_')}" /> 
						<search:result-entry entry="${entry}" url="${url}" protectDownload="true"/>
						<div style="clear:both"></div>
			  	</search:result-browser>
			</div>
		</c:if>
		
		<script>
		$.urlParam = function(name){
		    var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
		    if (results==null){
		       return null;
		    }
		    else{
		       return results[1] || 0;
		    }
		}
		<%-- $(function(){   = document.ready() --%>
		$(function(){
			var field = $.urlParam('searchField');
			var value = $.urlParam('searchValue');
			if(field!=null && value!=null){
				$('input#'+field.replace('.', '\\.')).val(decodeURIComponent(value));
			}
		});
		</script>
	</stripes:layout-component>
</stripes:layout-render>
