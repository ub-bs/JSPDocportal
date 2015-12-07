<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld"%>

<%@ taglib prefix="search" tagdir="/WEB-INF/tags/search"%>

<%@ attribute name="result" required="true" type="org.mycore.frontend.jsp.search.MCRSearchResultDataBean" %>

<%@ variable name-given="mcrid" %>
<%@ variable name-given="url" %>
<%@ variable name-given="entry" %>
 
<c:set var="numHits" value="${result.numFound}" />

<c:if test="${not empty result.sortfields and numHits>0}">
	<%--Resort Form --%>
	<div class="panel panel-default">
		<div class="panel-body">
		<form style="margin-bottom:0px;" action="${pageContext.request.contextPath}/${result.action}" method="get" accept-charset="UTF-8">
			 <input type="hidden" name="_search" value="<%= java.net.URLEncoder.encode(result.getId() , "UTF-8") %>" />
			 <fmt:message key="Webpage.Searchresult.resort-label" />
			 <select name="sortField">
			 	<option value=""></option>
			 	<c:forEach var="field" items="${fn:split(fn:trim(result.sortfields), ',')}">
			 		<c:if test="${not empty field}">
			 			<c:choose>
			 				<c:when test="${fn:startsWith(result.sort, field)}">
			 					<option value="${field}" selected="selected"><fmt:message key="Webpage.searchresults.sortfield.${field}" /></option>
			 				</c:when>
			 				<c:otherwise>
			 					<option value="${field}"><fmt:message key="Webpage.searchresults.sortfield.${field}" /></option>
			 				</c:otherwise>
			 			</c:choose>
			 		</c:if>
				</c:forEach>
			</select>&#160;&#160;&#160;
			<select name="sortValue">
				<option value=""></option>
			 	<c:forEach var="order" items="${fn:split('asc desc', ' ')}">
			 		<c:choose>
			 			<c:when test="${fn:endsWith(result.sort, order)}">
			 				<option value="${order}" selected="selected"><fmt:message key="Webpage.Searchresult.order.${order}" /></option>
			 			</c:when>
			 			<c:otherwise>
			 				<option value="${order}"><fmt:message key="Webpage.Searchresult.order.${order}" /></option>
			 			</c:otherwise>
			 		</c:choose>
				</c:forEach>
			</select>&#160;&#160;&#160;
			<input class="btn btn-primary btn-sm" value="<fmt:message key='Webpage.Searchresult.resort' />" type="submit" />
		</form>
		</div>
	</div>
</c:if>

<div class="panel panel-default ur-searchresult-panel">
	<c:if test="${numHits >= 0}">	
		<c:set var="pageNavi">
			<%-- // 36.168 Treffer                   Erste Seite | 11-20 | 21-30 | 31-40 | 41-50 | Letzte Seite --%>
			<ul class="pagination pull-right" style="margin-top:-7px;margin-bottom:0px ">
			<c:if test="${result.numPages> 1}">
				<c:set var="page"><%= Math.round(Math.floor((double) result.getStart() / result.getRows()) + 1) %></c:set>
				<c:set var="start">0</c:set>
				<li><a href="${pageContext.request.contextPath}/${result.action}?_search=${result.id}&amp;_start=${start}"><fmt:message key="Webpage.Searchresult.firstPage" /></a></li>
			
				<c:if test="${page - 2 > 0}">
					<c:set var="start">${result.start - result.rows - result.rows}</c:set>
					<li><a href="${pageContext.request.contextPath}/${result.action}?_search=${result.id}&amp;_start=${start}">${start + 1}-${start + result.rows}</a></li>
				</c:if>
				<c:if test="${page - 1 > 0}">
					<c:set var="start">${result.start - result.rows}</c:set>
					<li><a href="${pageContext.request.contextPath}/${result.action}?_search=${result.id}&amp;_start=${start}">${start + 1}-${start + result.rows}</a></li>
				</c:if>

				<c:set var="start">${result.start}</c:set>
				<li><a style="font-weight:bold;color: black" href="${pageContext.request.contextPath}/${result.action}?_search=${result.id}&amp;_start=${start}">${start + 1}-<%=Math.min(Integer.parseInt(jspContext.getAttribute("start").toString()) + result.getRows(), result.getNumFound())%></a></li>

				<c:if test="${page + 1 <= result.numPages}">
					<c:set var="start">${result.start + result.rows}</c:set>
					<li><a href="${pageContext.request.contextPath}/${result.action}?_search=${result.id}&amp;_start=${start}">${start + 1}-<%=Math.min(Integer.parseInt(jspContext.getAttribute("start").toString()) + result.getRows(), result.getNumFound())%></a></li>
				</c:if>
				<c:if test="${page + 2 <= result.numPages}">
					<c:set var="start">${result.start + result.rows + result.rows}</c:set>
					<li><a href="${pageContext.request.contextPath}/${result.action}?_search=${result.id}&amp;_start=${start}">${start + 1}-<%=Math.min(Integer.parseInt(jspContext.getAttribute("start").toString()) + result.getRows(), result.getNumFound())%></a></li>
				</c:if>
			
				<c:set var="start"><%= Math.round((result.getNumPages() - 1) * result.getRows()) %></c:set>
				<li><a href="${pageContext.request.contextPath}/${result.action}?_search=${result.id}&amp;_start=${start}"><fmt:message key="Webpage.Searchresult.lastPage" /></a></li>
			</c:if>
		</ul>
		${result.numFound} <fmt:message key="Webpage.Searchresult.numHits" />

		</c:set>
	<div class="panel-heading">
		<c:out value="${pageNavi}" escapeXml="false"/>
	</div>
	
	<c:if test="${numHits > 0}">	
		<ul class="list-group">
			<c:forEach var="entry" items="${result.entries}">
				<c:set var="mcrid" value="${entry.mcrid}" />
				<c:set var="entry" value="${entry}" />
				<c:set var="url"   value="${pageContext.request.contextPath}/resolve/id/${entry.mcrid}?_search=${result.id}" />
				<li class="list-group-item">
				
					<jsp:doBody />
					
				</li>				 
			</c:forEach>
   		</ul>

		<div class="panel-footer">
			<c:out value="${pageNavi}" escapeXml="false"/>
		</div>			
	</c:if>
</c:if>			
</div>