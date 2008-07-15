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
<x:forEach select="$dom/mycoreobject/structure/parents/parent">
		<%--instead of string(./@xlink:href) to avoid  XPathStylesheetDOM3Exception: Prefix must resolve to a namespace: xlink--%>
		<c:set var="parentID"><x:out select="string(./@*[local-name()='href'])" /></c:set>
		<jsp:include page="docdetailitem-parent-root-sub.jsp" flush="true">
		<jsp:param name="mcrid" value="${parentID}"/>
		<jsp:param name="volume" value="${param.volume}"/>
	</jsp:include>
</x:forEach>
    
	<%-- NUR Schriftenreihen hier anzeigen --%>
	<c:set var="type"><mcr:simpleXpath jdom="${jdom}" xpath="/mycoreobject/metadata/types/type/@categid" /></c:set>
	<c:if test="${fn:contains('TYPE0004.002',type)}" >
    	<c:set var="title"><mcr:simpleXpath jdom="${jdom}" xpath="/mycoreobject/metadata/titles/title[@type='short']" /></c:set>
		<c:if test="${empty(title)}">
			<c:set var="title"><mcr:simpleXpath jdom="${jdom}" xpath="/mycoreobject/metadata/titles/title[1]" /></c:set>
		</c:if>
		<tr>
   			<td class="metaname"> <fmt:message key="OMD.published-in" />:</td>
   		<td class="metavalue">
		  
		<%--<jsp:include page="../resultlistitems/resultlistitem-${fn:split(mcrid,'_')[1]}.jsp" >
	   	  	<jsp:param name="pageFragment" value="icon" />
	        <jsp:param name="contentType" value="${type}" />
   	        <jsp:param name="formatType" value="" />  
		</jsp:include>--%>
		<a href="${WebApplicationBaseURL}metadata/<c:out value='${mcrid}'/>"><c:out value='${title}' /></a>
		<c:if test="${not empty(param.volume)}">
			(<c:out value="${param.volume}" />)
		</c:if>
		</td>
	</c:if>
</c:if>
<c:if test="${(empty(jdom))}" ><br />Could not load document</c:if>
