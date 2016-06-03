<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld"%>

<%@ taglib prefix="search" tagdir="/WEB-INF/tags/search"%>

<%@ attribute name="url" required="true" type="java.lang.String"%>
<%@ attribute name="entry" required="true" type="org.mycore.frontend.jsp.search.MCRSearchResultEntry"%>

<tr>
	<td><search:show-edit-button mcrid="${entry.mcrid}" /></td>
	<td>
		<h4>
			<a href="${url}">${entry.label}</a>
		</h4>
		<table
			style="border-spacing: 4px; border-collapse: separate; font-size: 100%">
			<c:forEach var="d" items="${entry.data}">
				<tr>
					<th style="min-width: 120px; vertical-align: top"><fmt:message
							key="Webpage.searchresult.${entry.objectType}.label.${d.key}" />:&#160;</th>
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
	</td>
	<td>
		<c:if test="${not empty entry.coverURL}">
			<img src="${pageContext.request.contextPath}/${entry.coverURL}"
				class="pull-right img-thumbnail" alt="Cover" style="max-width:120px" />
		</c:if>
	</td>
</tr>