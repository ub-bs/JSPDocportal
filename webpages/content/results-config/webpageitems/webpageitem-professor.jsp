<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>

<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages' />

<c:set var="pageFragment" value="${param.pageFragment}" />
<c:set var="mcrid" value="${param.mcrid}" />
<c:if test="${empty jdom}">
	<mcr:receiveMcrObjAsJdom mcrid="${mcrid}" var="jdom"/>
</c:if>

<c:choose>
	<c:when test="${pageFragment=='pagetitle'}">
		<mcr:simpleXpath jdom="${jdom}" xpath="/mycoreobject/metadata/firstnames/firstname" />
		<mcr:simpleXpath jdom="${jdom}" xpath="/mycoreobject/metadata/surnames/surname" />				                         	
	</c:when>
		
	<c:otherwise>
		<b>Wrong "pageFragment"-Parameter</b>
	</c:otherwise>
</c:choose>