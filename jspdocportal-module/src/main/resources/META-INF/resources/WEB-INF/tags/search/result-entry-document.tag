<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld"%>

<%@ taglib prefix="search" tagdir="/WEB-INF/tags/search"%>

<%@ attribute name="url" required="true" type="java.lang.String"%>
<%@ attribute name="entry" required="true" type="org.mycore.frontend.jsp.search.MCRSearchResultEntry"%>

<tr>
	<td>
		<table style="border-spacing: 4px; border-collapse: separate; font-size: 100%">
			<tr><td>${entry.data['ir.creator.result']}</td></tr>
			<tr><td>
				<h4>
					<a href="${url}">${entry.label}</a>
				</h4>
			</td></tr>
			<tr><td>${entry.data['ir.originInfo.result']}</td></tr>
			<tr><td>${entry.data['ir.shelfLocator.result']}</td></tr>
			<tr><td style="font-style: italic; text-align:justify">${entry.data['ir.abstract300.result']}</td></tr>
			<tr><td>${entry.data['purl']}</td></tr>
		</table>
	</td>
	<td>
		<c:if test="${not empty entry.coverURL}">
			<img src="${pageContext.request.contextPath}/${entry.coverURL}"
				 class="pull-right img-thumbnail" alt="Cover" style="max-width:120px" />
		</c:if>
	</td>
</tr>
