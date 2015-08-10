<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld"%>

<%@ taglib prefix="search" tagdir="/WEB-INF/tags/search"%>

<%@ attribute name="url" required="true" type="java.lang.String"%>
<%@ attribute name="data" required="true" type="org.mycore.frontend.jsp.search.MCRSearchResultEntry"%>

<div class="rows">
	<div class="col-sm-9">
		<h4>
			<a href="${url}">${data.label}</a>
		</h4>
		<table
			style="border-spacing: 4px; border-collapse: separate; font-size: 100%">
			<c:forEach var="d" items="${data.data}">
				<tr>
					<th style="min-width: 120px; vertical-align: top"><fmt:message
							key="Webpage.searchresult.${data.objectType}.label.${d.key}" />:&#160;</th>
					<c:choose>
						<c:when test="${fn:endsWith(d.key, '_msg')}">
							<td><fmt:message key="${d.value}" /></td>
						</c:when>
						<c:when test="${fn:endsWith(d.key, '_class')}">
							<td><mcr:displayClassificationCategory
									classid="${fn:substringBefore(d.value,':')}"
									categid="${fn:substringAfter(d.value,':')}" lang="de" /></td>
						</c:when>
						<c:otherwise>
							<td><c:out value="${fn:replace(d.value, '|', '<br />')}"
									escapeXml="false" /></td>
						</c:otherwise>
					</c:choose>
				</tr>
			</c:forEach>
		</table>
	</div>
	<c:if test="${not empty data.coverURL}">
		<div class="col-sm-3">
			<img src="${pageContext.request.contextPath}/${data.coverURL}"
				class="pull-right img-thumbnail" alt="Portrait" style="max-width:120px" />
		</div>
	</c:if>
</div>