<%@ page language="java"%>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr"%>

<c:set var="WebApplicationBaseURL"
	value="${applicationScope.WebApplicationBaseURL}" />
<c:set var="Navigation" value="${applicationScope.navDom}" />
<c:set var="path" value="${requestScope.path}" />
<c:set var="contentPage" value="${requestScope.content}" />
<c:set var="nodeID" value="${requestScope.nodeID}" />
<c:set var="pathID" value="${requestScope.pathID}" />
<c:set var="youAreHere" value="${requestScope.youAreHere}" />
<c:set var="langfreeRequestURL" value="${langfreeRequestURL}" />
<x:set var="recNavPath" select="$sessionScope:recNavPath" />

<%-- creates the left navigation menu by recursively calling itself--%>

<fmt:setLocale value="${lang}" />
<fmt:setBundle basename='messages' />
<%-- <x:forEach select="$recNavPath/navitem[(not(@hidden = 'true')) and (@accessAllowed = 'true')]"> --%>
<x:forEach select="$recNavPath/navitem[not(@hidden = 'true')]">
	<x:set var="href" select="string(./@path)" />
	<x:set var="labelKey" select="string(./@label)" />
    <x:set var="right" select="string(./@right)" /> 
	<mcr:checkAccess var="canDo" permission="${right}" key="" /> 
	<c:if test="${right=='' or canDo}">						
		<x:choose>
			<x:when select="./@level ='1'">
				<div class="navi_left_mainentry">
			</x:when>
			<x:otherwise>
				<div class="navi_left_subentry">
			</x:otherwise>
		</x:choose>
		<%--<div> in <x:choose> --%>
			<a target="_self" href='${href}'><fmt:message key="${labelKey}" /></a>
			<x:if select="contains($pathID,./@systemID)">
				<x:set scope="session" var="recNavPath" select="./navitem"/>
				<c:import url="/content/navi_left_rec.jsp" />
			</x:if>
		</div>
	</c:if>
</x:forEach>

