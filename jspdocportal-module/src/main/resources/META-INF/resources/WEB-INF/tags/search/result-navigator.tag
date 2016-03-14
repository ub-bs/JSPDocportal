<%@tag import="org.mycore.frontend.jsp.search.MCRSearchResultDataBean" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld"%>

<%@ attribute name="mcrid" required="true" type="java.lang.String" %>

<%
	String searchID = request.getParameter("_search");
	if(searchID!=null){
		MCRSearchResultDataBean result = MCRSearchResultDataBean.retrieveSearchresultFromSession(request, searchID);
		jspContext.setAttribute("result", result);
	}
%>

<c:if test="${not empty result}">
	<!-- Searchresult PageNavigation -->
	<div id="searchdetail-navigation" class="panel panel-default">
		<c:set var="numHits" value="${result.numFound}" />
		<div class="panel-heading" style="text-align:center">
			<fmt:message key="Webpage.Searchresult.hitXofY">
				<fmt:param>${result.current + 1}</fmt:param>
				<fmt:param>${numHits}</fmt:param>	
			</fmt:message>
		</div>
		<div class="panel-body">
			<a style="font-size:1.5em" class="btn btn-default btn-xs" 
			    href="${pageContext.request.contextPath}/${result.action}?_search=${result.id}"
				title="<fmt:message key="Webpage.Searchresult.back.hint" />">▲</a>

			<div class="btn-group pull-right">
				<c:if test="${result.current > 0}">
					<a style="font-size:1.5em" class="btn btn-default btn-xs" 
					   href="${pageContext.request.contextPath}/${result.action}?_search=${result.id}&amp;_hit=${result.current-1}"
					   title="<fmt:message key="Webpage.Searchresult.prevPage.hint" />">◀</a>
				</c:if>
				<c:if test="${result.current < numHits - 1}">
					<a style="font-size:1.5em" class="btn btn-default btn-xs" 
					   href="${pageContext.request.contextPath}/${result.action}?_search=${result.id}&amp;_hit=${result.current+1}"
					   title="<fmt:message key="Webpage.Searchresult.nextPage.hint" />">▶</a>
				</c:if>
			</div>
		</div>	
	</div>
</c:if>
