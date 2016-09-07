<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld"%>

<%@ taglib prefix="search" tagdir="/WEB-INF/tags/search"%>

<%@ attribute name="result" required="true" type="org.mycore.frontend.jsp.search.MCRSearchResultDataBean"%>
<%@ attribute name="mask" required="true" type="java.lang.String"%>

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
				<c:forEach var="countsKey" items="${facets.get(facetKey).keySet()}">
					<c:set var="key">+${facetKey}:${countsKey}</c:set>
					<c:if test="${result.filterQueries.contains(key)}">
					  	<c:url var="url" value="${WebApplicationBaseURL}browse/${mask}">
							<c:param name="_search" value="${result.id}" />
							<c:param name="_remove-filter" value="${key}" />
						</c:url>
						<a class="btn btn-sm btn-default ir-btn-facet" style="display:block;text-align:left;white-space:normal;margin:3px 0px;color:black;width:100%" href="${url}">
							<span class="glyphicon glyphicon-remove pull-right" style="margin-top:3px; color:darkred;"></span>
							<span style="display:table-cell;vertical-align:middle;">
								${actionBean.calcFacetOutputString(facetKey, countsKey)}
							</span>
							<span style="display:table-cell;vertical-align:middle;padding-left:12px">
								<span class="badge">${facets.get(facetKey).get(countsKey)}</span>
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
								<span class="badge">${facets.get(facetKey).get(countsKey)}</span>
							</span>
						</button>
					</c:if>
				</c:forEach>
			</div>
		</div>
	</c:if>
</c:forEach>