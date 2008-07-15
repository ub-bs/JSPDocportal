<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>

<c:set var="pageFragment" value="${param.pageFragment}" />
<c:set var="formatType" value="${param.formatType}" />
<c:set var="contentType" value="${param.contentType}" />
<c:choose>
	<c:when test="${pageFragment=='icon'}">
			<c:choose>
			<c:when test="${fn:contains('TYPE0004.001', contentType)}">
				<img src="${WebApplicationBaseURL}/images/pubtype/zeitschrift.gif" align="middle"
					alt="icon" />
			</c:when>
			<c:otherwise>
				<img src="${WebApplicationBaseURL}/images/pubtype/series-volume.gif" alt="icon" align="middle" />
					
			</c:otherwise>
		</c:choose>
	</c:when>
	
	<c:when test="${pageFragment=='headline'}">
		<x:forEach select="$data">
			<x:out select="./metaname[1]/metavalues/metavalue/@text" />
		</x:forEach>
	</c:when>

	<c:otherwise>
		<b>Wrong "pageFragment"-Parameter</b>
	</c:otherwise>
</c:choose>