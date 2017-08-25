
<%@tag import="org.mycore.datamodel.classifications2.MCRCategory"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld"%>

<%@ taglib prefix="search" tagdir="/WEB-INF/tags/search"%>

<%@ attribute name="category" required="true" type="org.mycore.datamodel.classifications2.MCRCategory" %>
<%@ attribute name="facetField" required="false" type="java.lang.String" %>
<%@ attribute name="mask" required="false" type="java.lang.String" %>

<c:if test="${category.hasChildren()}">
	<ul>
		<c:forEach var="c" items="${category.children}">
			<%--
			<li>${c.currentLabel.get().text}
				<c:if test="${not empty facetField}">
					<span class="mcr-facet-count" data-mcr-facet-field="${facetField}" data-mcr-facet-value="${c.id.getRootID()}:${c.id.ID}"></span>
				</c:if>
				<search:browse-classification-inner category="${c}" facetField="${facetField}" mask="${mask}" />
			</li> --%>
			<li>
				<button class="btn btn-sm btn-default ir-facets-btn" style="border:none; display:block;text-align:left;white-space:normal;width:100%" 
					    onclick="changeFacetIncludeURL('${facetField}','${c.id.getRootID()}:${c.id.ID}', '${mask}', '${result.id}');">
					<span style="display:table-cell;vertical-align:middle;">
							${c.currentLabel.get().text}
					</span>
					<span style="display:table-cell;vertical-align:middle;padding-left:12px;">
							<span class="badge ir-badge mcr-facet-count" data-mcr-facet-field="${facetField}" data-mcr-facet-value="${c.id.getRootID()}:${c.id.ID}"></span>
					</span>
				</button>
			</li>
			<search:browse-classification-inner category="${c}" facetField="${facetField}" mask="${mask}" />
		</c:forEach>
	</ul>
</c:if>
