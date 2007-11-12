<%@ page language="java"%>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr"%>


<%-- creates the left navigation menu by recursively calling itself--%>
<fmt:setLocale value="${lang}" />
<fmt:setBundle basename='messages' />
<x:forEach select="$sessionScope:recNavPath/navitem[not(@hidden = 'true')]">
	<x:set var="href" select="string(./@path)" />
	<x:set var="labelKey" select="string(./@label)" />
    <x:set var="right" select="string(./@right)" /> 
    <c:set var="canDo" value="true"/>
    <c:if test="${right!=''}">
		<mcr:checkAccess var="canDo" permission="${right}" key="" />        
    </c:if>
	<c:if test="${canDo}">						
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
			<c:choose>
			<c:when test="${not (empty param.open)}">
						<x:set scope="session" var="recNavPath" select="./navitem"/>
						<c:import url="/content/navi_left_rec.jsp" />
			</c:when>
				<c:otherwise>
					<x:if select="contains($pathID,./@systemID)">
						<x:set scope="session" var="recNavPath" select="./navitem"/>
						<c:import url="/content/navi_left_rec.jsp" />
					</x:if>
				</c:otherwise>
			</c:choose>
		</div>
	</c:if>
</x:forEach>

