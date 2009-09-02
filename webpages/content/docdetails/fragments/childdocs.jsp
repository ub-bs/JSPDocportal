<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages' />
<%--Parameter: mcrid --%>
<c:set var="mcrid" value="${param.mcrid}" />
<mcr:receiveMcrObjAsJdom mcrid="${mcrid}" var="jdom" varDom="dom" fromWForDB="false"/>

<c:if test="${not(empty(jdom))}" >
    <c:set var="title"><mcr:simpleXpath jdom="${jdom}" xpath="/mycoreobject/metadata/titles/title[@type='short']" /></c:set>
	<c:if test="${empty(title)}">
		<c:set var="title"><mcr:simpleXpath jdom="${jdom}" xpath="/mycoreobject/metadata/titles/title[1]" /></c:set>
	</c:if>
	<c:set var="c"><mcr:simpleXpath jdom="${jdom}" xpath="/mycoreobject/metadata/types/type/@categid" /></c:set>
	<li>
		<table><tr>
			<td style="padding-right:10px">	
				<c:import url="${application.WebApplicationBaseURL}/content/resultdetails/fragments/document_icon.jsp">
					<c:param name="contentType" value="${type}" />
					<c:param name="formatType" value="${format}" />
					<c:param name="docType" value="${fn:substringBefore(fn:substringAfter(mcrid, '_'),'_')}" />
				</c:import>		
			</td>
			<td>
				<a href="${WebApplicationBaseURL}resolve?id=<c:out value='${mcrid}'/>">
				<c:out value='${title}' /></a>
			</td>
		</tr></table>
 		<ul style="line-height:1.5em; list-style:none;list-style-position: inside;">
			<x:forEach select="$dom/mycoreobject/structure/children/child">
				<%--instead of string(./@xlink:href) to avoid  XPathStylesheetDOM3Exception: Prefix must resolve to a namespace: xlink--%>
				<c:set var="childID"><x:out select="string(./@*[local-name()='href'])" /></c:set>
				<jsp:include page="childdocs.jsp" flush="true">
					<jsp:param name="mcrid" value="${childID}"/>
				</jsp:include>
			</x:forEach>
		</ul>
	</li>
</c:if>