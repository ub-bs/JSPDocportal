<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld"%>

<%@ taglib prefix="search" tagdir="/WEB-INF/tags/search"%>

<%@ attribute name="url" required="true" type="java.lang.String"%>
<%@ attribute name="data" required="true" type="org.mycore.frontend.jsp.search.MCRSearchResultEntry"%>

<div class="rows">
	<div class="col-sm-9">
		<table style="border-spacing: 4px; border-collapse: separate; font-size: 100%">
			<tr><td>${data.data['ir.creator.result']}</td></tr>
			<tr><td>
				<h4>
					<a href="${url}">${data.label}</a>
				</h4>
			</td></tr>
			<tr><td>${data.data['ir.originInfo.result']}</td></tr>
			<tr><td>${data.data['ir.shelfLocator.result']}</td></tr>
			<tr><td style="font-style: italic; text-align:justify">${data.data['ir.abstract300.result']}</td></tr>
			<tr><td>${data.data['purl']}</td></tr>
		</table>
	</div>
	<c:if test="${not empty data.coverURL}">
		<div class="col-sm-3">
			<img src="${pageContext.request.contextPath}/${data.coverURL}"
				class="pull-right img-thumbnail" alt="Cover" style="max-width:120px" />
		</div>
	</c:if>
</div>