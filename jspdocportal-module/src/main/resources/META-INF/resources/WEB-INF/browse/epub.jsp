<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c"       uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="x"       uri="http://java.sun.com/jsp/jstl/xml"%>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn"      uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="mcr" 	uri="http://www.mycore.org/jspdocportal/base.tld"%>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>
	
<%@ taglib prefix="search" tagdir="/WEB-INF/tags/search"%>

<fmt:message var="pageTitle" key="Webpage.browse.title.${actionBean.result.mask}" />
<stripes:layout-render name="/WEB-INF/layout/default.jsp" pageTitle="${pageTitle}" layout="1column">
	<stripes:layout-component name="html_header">
		<meta name="mcr:search.id" content="${actionBean.result.id}" />
	</stripes:layout-component>
	<stripes:layout-component name="contents">
		<div class="row">
			<div class="col-xs-12">
				<h2>${pageTitle}</h2>
			</div>
		</div>
		<div class="row">
			<div class="col-md-8">
				<div class="ir-box">
					<search:result-browser result="${actionBean.result}">
						<c:set var="doctype" value="${fn:substringBefore(fn:substringAfter(mcrid, '_'),'_')}" />
						<search:show-edit-button mcrid="${mcrid}" />
						<c:choose>
							<c:when test="${doctype eq 'disshab'}">
								<search:result-entry-disshab entry="${entry}" url="${url}" />
							</c:when>
							<c:when test="${doctype eq 'document'}">
								<search:result-entry-document entry="${entry}" url="${url}" />
							</c:when>
							<c:otherwise>
								<search:result-entry entry="${entry}" url="${url}" />
							</c:otherwise>
						</c:choose>
						<div style="clear:both"></div>
					</search:result-browser>
				</div>
			</div>
			<div class="col-md-4">
				<div class="ir-box ir-box-bordered ir-infobox hidden-sm hidden-xs">
         			 <h3>Filter</h3>
          			 <div class="panel panel-default">
          			 	<c:if test="${not empty actionBean.result.filterQueries}">
  							<div class="panel-heading">
  								<c:forEach var="fq" items="${actionBean.result.filterQueries}">
  									<c:url var="url" value="${WebApplicationBaseURL}browse/epub">
  										<c:param name="_search" value="${actionBean.result.id}" />
  										<c:param name="_remove-filter" value="${fq}" />
									</c:url>
  									<div class="input-group" style="margin-bottom:6px">
  										<span class="input-group-btn">
        									<button class="btn btn-default" type="button" disabled="disabled" style="background-color:white">
        										<c:if test="${fn:startsWith(fq, '-')}">
  													<span class="glyphicon glyphicon-minus"></span>
  												</c:if>
  												<c:if test="${not fn:startsWith(fq, '-')}">
  													<span class="glyphicon glyphicon-plus"></span>
  												</c:if>
  											</button>
      									</span>
      									<c:set var="c"><fmt:message key="Browse.Filter.histbest.${fn:substringBefore(fn:substring(fq, 1, -1),':')}"/>:${fn:substringAfter(fn:substring(fq, 1, -1),':')}</c:set>
      									<input type="text" class="form-control" readonly="readonly" style="background-color:white" value="${c}" />
      									<span class="input-group-btn">
      										<a class="btn btn-default" href="${url}" role="button">
  												<span style="color:red" class="glyphicon glyphicon-trash"></span>
  											</a>
  										</span>
								  	</div>
  								</c:forEach>
  							</div>
  						</c:if>
  						<div class="panel-body row">
  							<div class="col-sm-9">
    							<div class="form-group">
    							  	<select id="filterField" name="filterField" class="form-control" style="width:100%">
  										<option value="ir.creator_all"><fmt:message key="Browse.Filter.epub.ir.creator_all"/></option>
  										<option value="ir.title_all"><fmt:message key="Browse.Filter.epub.ir.title_all"/></option>
  										<option value="ir.pubyear_start"><fmt:message key="Browse.Filter.epub.ir.pubyear_start"/></option>
  										<option value="ir.pubyear_end"><fmt:message key="Browse.Filter.epub.ir.pubyear_end" /></option>
 									</select>
   								</div>
  								<div class="form-group">
   									<input class="form-control" id="filterValue" name="filterValue" style="width:100%" placeholder="Wert" type="text">
   								</div>
  							</div>
  							<script type="text/javascript">
  								function changeFilterIncludeURL() {
  									window.location=$("meta[name='mcr:baseurl']").attr("content")
  										 	       + "browse/epub?_search="
  										           + $("meta[name='mcr:search.id']").attr("content")
  											       + "&_add-filter="
  											       + encodeURIComponent("+" + $("#filterField option:selected").val()+":"+$("#filterValue").val());
  								}
  								function changeFilterExcludeURL() {
  									window.location=$("meta[name='mcr:baseurl']").attr("content")
  											       + "browse/epub?_search="
  										           + $("meta[name='mcr:search.id']").attr("content")
  											       + "&_add-filter="
  											       + encodeURIComponent("-" + $("#filterField option:selected").val()+":"+$("#filterValue").val());
  								}
  							</script>
  							<div class="col-sm-3">
								<button id="filterInclude" class="btn btn-primary" style="margin-top:6px; margin-left:-9px"
								        onclick="changeFilterIncludeURL();">
									<span class="glyphicon glyphicon-plus"></span>
								</button> 	
								<button id="filterExclude" class="btn btn-primary" style="margin-top:3px; margin-left:-9px"
								        onclick="changeFilterExcludeURL();">
								   <span class="glyphicon glyphicon-minus"></span>
								</button>
          					</div> 		
						</div>
  					</div>
				</div>
        	</div>
		</div>
	</stripes:layout-component>
</stripes:layout-render>
