<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c"       uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="x"       uri="http://java.sun.com/jsp/jstl/xml"%>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn"      uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="mcr" 	uri="http://www.mycore.org/jspdocportal/base.tld"%>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>
	
<%@ taglib prefix="search" tagdir="/WEB-INF/tags/search"%>

<fmt:message var="pageTitle" key="Webpage.browse.title.${actionBean.result.mask}" />
<stripes:layout-render name="/WEB-INF/layout/default.jsp" pageTitle="${pageTitle}" layout="2columns">
	<stripes:layout-component name="html_header">
		<meta name="mcr:search.id" content="${actionBean.result.id}" />
	</stripes:layout-component>
	<stripes:layout-component name="left_side">
		<div class="ir-box ir-box-bordered">
			<div class="main_navigation">
				<mcr:outputNavigation id="left" cssClass="nav ir-sidenav" expanded="true" mode="left" />
			</div>
			<div class="main_navigation">
				<mcr:outputNavigation id="publish" cssClass="nav ir-sidenav" expanded="false" mode="left" />
			</div>
		</div>
		
		
		<div class="ir-box ir-box-bordered" style="margin-top:36px">
		<h3>Filter und Facetten</h3>
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
  					<div class="row" style="margin-bottom:24px;">
  				<div class="col-sm-12">
					<c:forEach var="fq" items="${actionBean.result.filterQueries}">
						<c:if test="${not fn:contains(fq, '.facet:')}">
  							<c:url var="url" value="${WebApplicationBaseURL}browse/histbest">
  								<c:param name="_search" value="${actionBean.result.id}" />
  								<c:param name="_remove-filter" value="${fq}" />
							</c:url>
							<c:set var="c"><fmt:message key="Browse.Filter.histbest.${fn:substringBefore(fn:substring(fq, 1, -1),':')}"/>: ${actionBean.calcFacetOutputString(fn:substringBefore(fn:substring(fq, 1, -1),':'), fn:substringAfter(fn:substring(fq, 1, -1),':'))}</c:set>
							<a class="btn btn-sm btn-default ir-btn-facet" style="display:block;text-align:left;white-space:normal;margin-bottom:3px;color:black;width:100%" href="${url}">
								<span class="glyphicon glyphicon-remove pull-right" style="margin-top:3px; color:darkred;"></span>
								${c}										
							</a>
						</c:if>
					</c:forEach>
				</div>
			</div>
						
			<search:result-facets result="${actionBean.result}" mask="epub" />

		</div>
		</stripes:layout-component>
				
	
	<stripes:layout-component name="contents">
		<div class="row ir-box">
    		<div class="col-xs-12">
				<h2>${pageTitle}</h2>
			</div>
		</div>
		<div class="row ir-box">
			<div class="col-xs-12">
					<search:result-sorter result="${actionBean.result}" 
			                      fields="score,ir.pubyear_start,modified,ir.creator.result,ir.title.result" mask="epub"/>

					<search:result-browser result="${actionBean.result}">
						<c:set var="doctype" value="${fn:substringBefore(fn:substringAfter(mcrid, '_'),'_')}" />
						<search:show-edit-button mcrid="${mcrid}" />
						<c:choose>
							<c:when test="${(doctype eq 'disshab') or (doctype eq 'thesis')}">
								<search:result-entry-disshab entry="${entry}" url="${url}" />
							</c:when>
							<c:when test="${(doctype eq 'document') or (doctype eq 'bunlde')}">
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
	</stripes:layout-component>
</stripes:layout-render>
