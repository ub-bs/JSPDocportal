<%@ page pageEncoding="UTF-8"
	contentType="application/xhtml+xml; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="mcr"
	uri="http://www.mycore.org/jspdocportal/base.tld"%>
<%@ taglib prefix="mcrb"
	uri="http://www.mycore.org/jspdocportal/browsing.tld"%>
<%@ taglib prefix="stripes"
	uri="http://stripes.sourceforge.net/stripes.tld"%>

<fmt:message var="pageTitle"
	key="Webpage.search.title.${actionBean.mask}" />
<stripes:layout-render name="../../WEB-INF/layout/default.jsp"
	pageTitle="${pageTitle}" layout="2columns">
	<stripes:layout-component name="html_header">

	</stripes:layout-component>
	<stripes:layout-component name="contents">
		<div class="ur-box ur-text">
			<c:set var="classCollapse" value="" />
			<c:if test="${not actionBean.showMask}">
				<button id="buttonCollapseSearchmask"
					class="btn btn-default pull-right" type="button"
					data-toggle="collapse" data-target="#searchmask"
					aria-expanded="false" aria-controls="searchmask">
                    Suche verfeinern</button>
				<c:set var="classCollapse">collapse</c:set>
			</c:if>

			<h2>${pageTitle}</h2>

			<div class="${classCollapse}" id="searchmask">
				<mcr:includeXEditor editorPath="${actionBean.editorPath}"
					pageURL="${actionBean.pageURL}" />
			</div>
			<script type="text/javascript">
              	$('#searchmask').on('show.bs.collapse', function () {
            		$('#buttonCollapseSearchmask').hide();
        		})
             </script>
		</div>
		<c:if test="${not empty actionBean.solrResponse and actionBean.showResults}">
			<c:set var="solrResponse" value="${actionBean.solrResponse}" />
			<div class="panel panel-default">
				<div class="panel-heading">
					<h3 class="panel-title">Treffer: ${actionBean.numFound}</h3>
				</div>
				<ul class="list-group">
					<c:forEach var="result" items="${actionBean.solrResponse.results}">
						<c:set var="objectType"
							value="${result.getFieldValueMap()['objectType']}" />
						<li class="list-group-item"><c:set var="resultDoc"
								value="${result}" scope="request" /> <jsp:include
								page="result_${objectType}.jsp" /></li>
					</c:forEach>
				</ul>

				<div class="panel-footer">${actionBean.solrResponse.getRequestUrl()}</div>
			</div>

		</c:if>
	</stripes:layout-component>
</stripes:layout-render>
