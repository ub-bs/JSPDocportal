<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld"%>

<%@ taglib prefix="search" tagdir="/WEB-INF/tags/search"%>

<%@ attribute name="url" required="true" type="java.lang.String"%>
<%@ attribute name="entry" required="true" type="org.mycore.frontend.jsp.search.MCRSearchResultEntry"%>

<div class="row">
	<div class="col-sm-9">
		<table style="border-spacing: 4px; border-collapse: separate; font-size: 100%">
			<tr><td>
				<span>${entry.data['ir.creator.result']}</span>
				<h4>
					<a href="${url}">
						<c:if test="${empty(entry.data['ir.partTitle.result'])}">
							${entry.label}
						</c:if>
						<c:if test="${not empty(entry.data['ir.partTitle.result'])}">
							<span style="font-weight:normal">${entry.label}</span><br />${entry.data['ir.partTitle.result']}
						</c:if>
					</a>
				</h4>
			</td></tr>
			<c:if test="${not empty(entry.data['ir.host.title.result'])}">
				<tr><td><span class="display-label">in:</span> ${entry.data['ir.host.title.result']}</td></tr>
			</c:if>
			<tr><td>${entry.data['ir.originInfo.result']}</td></tr>
			<tr><td>${entry.data['purl']}</td></tr>
			<tr><td style="font-size: 80%; text-align:justify">${entry.data['ir.abstract300.result']}</td></tr>
				
		</table>
	</div>
	<c:if test="${not empty entry.coverURL}">
		<div class="col-sm-3 hidden-xs">
			<div class="img-thumbnail pull-right ir-resultentry-image">
				<div style="position:relative">
   					<c:if test="${protectDownload}">
   						<img style="opacity:0.01;position:absolute;top:0px;left:0px;width:100%;height:100%;z-index:1" src="${pageContext.request.contextPath}/images/image_terms_of_use.png"/>
	   				</c:if>
   					<img style="position:relative;top:0px;left:0px;width:98%;padding:1%;display:block;max-width:120px; max-height:180px; object-fit:contain;" src="${pageContext.request.contextPath}/${entry.coverURL}" border="0" />
				</div>
			</div>
		</div>
	</c:if>
</div>