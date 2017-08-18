<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld"%>

<%@ taglib prefix="search" tagdir="/WEB-INF/tags/search"%>

<%@ attribute name="result" required="true" type="org.mycore.frontend.jsp.search.MCRSearchResultDataBean"%>
<%@ attribute name="mask" required="true" type="java.lang.String"%>
<%@ attribute name="top" required="false" type="java.lang.Integer" %>


<c:set var="top" value="${(empty top) ? 1000 : top}" />
<script type="text/javascript">
	function changeFacetIncludeURL(key, value) {
		window.location=$("meta[name='mcr:baseurl']").attr("content")
				 	       + "browse/${mask}?_search="
				           + $("meta[name='mcr:search.id']").attr("content")
					       + "&_add-filter="
				       + encodeURIComponent("+" + key +":"+ value);
		}
		function changeFacetExcludeURL(key,value) {
			window.location=$("meta[name='mcr:baseurl']").attr("content")
					       + "browse/${mask}?_search="
				           + $("meta[name='mcr:search.id']").attr("content")
					       + "&_add-filter="
					       + encodeURIComponent("-" + key +":"+ value);
		}
</script>
		
<c:set var="facets" value="${result.facetResult}" />
<c:forEach var="facetKey" items="${facets.keySet()}">
	<c:if test="${facets.get(facetKey).size() gt 0}">
		<div class="row">
			<div class="col-sm-12">
				<h5><fmt:message key="Browse.Filter.${mask}.${facetKey}" /></h5>
				<c:forEach var="countsKey" items="${facets.get(facetKey).keySet()}" varStatus="status">
					<c:set var="key">+${facetKey}:${countsKey}</c:set>
					<c:set var="facetID" value="${fn:replace(facetKey, '.', '_')}" />
					<c:if test="${status.index == top}">
						<div id="moreFacets_div_${facetID}" class="collapse">
					</c:if>
					<c:if test="${result.filterQueries.contains(key)}">
					  	<c:url var="url" value="${WebApplicationBaseURL}browse/${mask}">
							<c:param name="_search" value="${result.id}" />
							<c:param name="_remove-filter" value="${key}" />
						</c:url>
						<a class="btn btn-sm btn-default ir-btn-facet" style="display:block;text-align:left;white-space:normal;margin:3px 0px;color:black;width:100%" href="${url}">
							<i class="fa fa-times pull-right" style="margin-top:3px; color:darkred;"></i>
							<span style="display:table-cell;vertical-align:middle;">
								${actionBean.calcFacetOutputString(facetKey, countsKey)}
							</span>
							<span style="display:table-cell;vertical-align:middle;padding-left:12px">
								<span class="badge ir-badge">${facets.get(facetKey).get(countsKey)}</span>
							</span>
						</a>
					</c:if>
					<c:if test="${not result.filterQueries.contains(key)}">
						<button class="btn btn-sm btn-default ir-btn-facet" style="border:none; display:block;text-align:left;white-space:normal;width:100%" 
						        onclick="changeFacetIncludeURL('${facetKey}','${countsKey}');">
							<span style="display:table-cell;vertical-align:middle;">
								${actionBean.calcFacetOutputString(facetKey, countsKey)}
							</span>
							<span style="display:table-cell;vertical-align:middle;padding-left:12px;">
								<span class="badge ir-badge">${facets.get(facetKey).get(countsKey)}</span>
							</span>
						</button>
					</c:if>
					<c:if test="${status.index >= top and status.last}">
						</div>
						<button id="moreFacets_btn_${facetID}" class="btn btn-default btn-xs pull-right" data-toggle="collapse" data-target="#moreFacets_div_${facetID}"  >mehr ...</button>
						<script type="text/javascript">
						$('#moreFacets_div_${facetID}').on('shown.bs.collapse', function () {
							$('#moreFacets_btn_${facetID}').text('weniger ...');
						});
						$('#moreFacets_div_${facetID}').on('hidden.bs.collapse', function () {
							$('#moreFacets_btn_${facetID}').text('mehr ...')
						});
						</script>
					</c:if>
				</c:forEach>
			</div>
		</div>
	</c:if>
</c:forEach>