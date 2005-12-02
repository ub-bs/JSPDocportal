<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<c:set var="nodeID" value="${requestScope.nodeID}" />
<c:set var="pathID" value="${requestScope.pathID}" />
<c:set var="Navigation" value="${applicationScope.navDom}" />
<c:set var="lang" value="${requestScope.lang}" />
<fmt:setLocale value="${lang}" />
<fmt:setBundle basename='messages'/>
<x:forEach select="$Navigation//navitem[@systemID = $nodeID]/navitem">
    <x:set var="href1" select="string(./@path)" />
    <x:set var="labelKey1" select="string(./@label)" />
    <img title="" alt="" src="images/greenArrow.gif">
    <a target="_self" href="${href1}"><fmt:message key="${labelKey1}" /></a>
    <br/>    
</x:forEach>