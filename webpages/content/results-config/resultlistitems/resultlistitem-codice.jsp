<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>

<c:set var="pageFragment" value="${param.pageFragment}" />
<c:set var="formatType" value="${param.formatType}" />
<c:set var="contentType" value="${param.contentType}" />
<c:choose>
	<c:when test="${pageFragment eq 'icon'}">
		<img src="${WebApplicationBaseURL}/images/pubtype/handwriting.gif"
			alt="historische Sammlung" />
	</c:when>
	
	<c:when test="${pageFragment=='headline'}">
		<x:forEach select="$data">
			<x:out select="./metaname[1]/metavalues[2]/metavalue/@text" />
			<x:out select="./metaname[@name='OMD.title']/metavalues[3]/metavalue/@text" />
		</x:forEach>
	</c:when>
	
	<c:otherwise>
		<b>Unknown "pageFragment"-Parameter</b>
	</c:otherwise>
</c:choose>