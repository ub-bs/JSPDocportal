<%@ page language="java"%>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr"%>
<%@ taglib prefix="mcrdd" uri="http://www.mycore.de/jspdocportal/docdetails" %>

<mcrdd:setnamespace prefix="nav" uri="http://www.mycore.org/jspdocportal/navigation" />

<%-- creates the left navigation menu by recursively calling itself--%>

<x:forEach select="$sessionScope:recNavPath/nav:navitem[not(@hidden = 'true')]">
	<x:set var="href" select="string(./@href)" />
	<x:set var="labelKey" select="string(./@i18n)" />
    <x:set var="permission" select="string(./@permission)" /> 
    <c:set var="canDo" value="true"/>
    <c:if test="${right!=''}">
		<mcr:checkAccess var="canDo" permission="${permission}" key="" />        
    </c:if>
	<c:if test="${canDo}">
		<c:set var="cssClass" value="navi_left_subentry" />						
		<x:if select="./@level ='1'">
			<c:set var="cssClass" value="navi_left_mainentry" />
		</x:if>
		<div class="${cssClass}">
			<a target="_self" href='${href}'><fmt:message key="${labelKey}" /></a>
			<c:choose>
			<c:when test="${not (empty param.open)}">
						<x:set scope="session" var="recNavPath" select="./nav:navitem"/>
						<c:import url="/content/navi_left_rec.jsp" />
			</c:when>
				<c:otherwise>
					<%-- funktioniert nur, wenn IDs global eindeutig sind --%>
					<x:if select="contains(./@pathID,./@id)">
						<x:set scope="session" var="recNavPath" select="./nav:navitem"/>
						<c:import url="/content/navi_left_rec.jsp" />
					</x:if>
				</c:otherwise>
			</c:choose>
		</div>
	</c:if>
</x:forEach>

