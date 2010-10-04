<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar"   prefix="mcr"%>

<c:set var="nodeID" value="${requestScope.nodeID}" />
<c:set var="pathID" value="${requestScope.pathID}" />
<c:set var="Navigation" value="${applicationScope.navDom}" />
<c:set var="lang" value="${requestScope.lang}" />

<x:forEach select="$Navigation//navitem[@systemID = $nodeID]/navitem">
  <x:set var="hidden" select="string(./@hidden)" />
  <c:if  test="${hidden != true}">
	<x:set var="right" select="string(./@right)" />
	<c:set var="canDo" value="true" />
    <c:if  test="${not empty right && right ne 'read'}">
		<mcr:checkAccess var="canDo" permission="${right}" key="" />
    </c:if> 
    <c:if test="${canDo}">
	    <x:set var="href1" select="string(./@path)" />
	    <x:set var="labelKey1" select="string(./@label)" />
	    <img title="" alt="" src="images/greenArrow.gif">
	    <a target="_self" href="${href1}"><fmt:message key="${labelKey1}" /></a>
	    <br/>    
	</c:if>    
  </c:if>
</x:forEach>