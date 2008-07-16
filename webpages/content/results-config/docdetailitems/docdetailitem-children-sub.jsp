<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages' />
<c:set var="mcrid" value="${param.mcrid}" />
<c:set var="fromWForDB" value="${param.fromWForDB}" />
<mcr:receiveMcrObjAsJdom mcrid="${mcrid}" var="jdom" varDom="dom" fromWForDB="${fromWForDB}"/>
<c:if test="${not(empty(jdom))}" >
    <c:set var="title"><mcr:simpleXpath jdom="${jdom}" xpath="/mycoreobject/metadata/titles/title[@type='short']" /></c:set>
	<c:if test="${empty(title)}">
		<c:set var="title"><mcr:simpleXpath jdom="${jdom}" xpath="/mycoreobject/metadata/titles/title[1]" /></c:set>
	</c:if>
	<c:set var="c"><mcr:simpleXpath jdom="${jdom}" xpath="/mycoreobject/metadata/types/type/@categid" /></c:set>
	<li><table><tr><td style="padding-right:10px">	<jsp:include page="../resultlistitems/resultlistitem-${fn:split(mcrid,'_')[1]}.jsp" >
	      	<jsp:param name="pageFragment" value="icon" />
	        <jsp:param name="contentType" value="${c}" />
   	        <jsp:param name="formatType" value="" /> 
	</jsp:include></td>
	<td><a href="${WebApplicationBaseURL}metadata/<c:out value='${mcrid}'/>">
	<c:out value='${title}' /></a></td></tr></table>
 <ul style="line-height:1.5em; list-style:none;list-style-position: inside;">
<x:forEach select="$dom/mycoreobject/structure/children/child">
	<%--instead of string(./@xlink:href) to avoid  XPathStylesheetDOM3Exception: Prefix must resolve to a namespace: xlink--%>
	<c:set var="childID"><x:out select="string(./@*[local-name()='href'])" /></c:set>
	<jsp:include page="docdetailitem-children-sub.jsp" flush="true">
		<jsp:param name="mcrid" value="${childID}"/>
	</jsp:include>
</x:forEach>
</ul>
</li>
</c:if>
<c:if test="${(empty(jdom))}" ><br />Could not load document</c:if>
