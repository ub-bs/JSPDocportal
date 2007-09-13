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
			<c:when test="${fn:contains('TYPE0001.001', contentType)}">
				<img src="${WebApplicationBaseURL}/images/pubtype/monographie.gif"
					alt="text" />
			</c:when>
			<c:when test="${fn:contains('TYPE0001.002 TYPE0001.003 TYPE0001.004', contentType)}">
				<img src="${WebApplicationBaseURL}/images/pubtype/periodical.gif"
					alt="periodicals" />
			</c:when>
			<c:when test="${fn:contains('TYPE0001.005', contentType)}">
				<img src="${WebApplicationBaseURL}/images/pubtype/musikalie.gif"
					alt="music" />
			</c:when>
			<c:when test="${fn:contains('TYPE0001.006', contentType)}">
				<img src="${WebApplicationBaseURL}/images/pubtype/patent.gif"
					alt="patent" />
			</c:when>
			<c:when test="${fn:contains('TYPE0001.007', contentType)}">
				<img src="${WebApplicationBaseURL}/images/pubtype/map.gif"
					alt="map" />
			</c:when>
			<c:when test="${fn:contains('TYPE0002', contentType)}">
				<img src="${WebApplicationBaseURL}/images/pubtype/article.gif"
					alt="article" />
			</c:when>
			<c:when test="${fn:contains('TYPE0003', contentType)}">
				<img src="${WebApplicationBaseURL}/images/pubtype/disshab.gif"
					alt="disshab" />
			</c:when>
			<c:when test="${fn:contains('TYPE0010.001', contentType)}">
				<img src="${WebApplicationBaseURL}/images/pubtype/rede.gif"
					alt="speech" />
			</c:when>
			<c:otherwise>
				<c:choose>
					<c:when test="${fn:contains('FORMAT0001', formatType)}">
						<img src="${WebApplicationBaseURL}/images/pubtype/article.gif"
							alt="text" />
					</c:when>
					<c:when test="${fn:contains('FORMAT0002', formatType)}">
						<img src="${WebApplicationBaseURL}/images/pubtype/picture.gif"
							alt="image" />
					</c:when>
					<c:when test="${fn:contains('FORMAT0005 FORMAT0006  FORMAT0008', formatType)}">
						<img src="${WebApplicationBaseURL}/images/pubtype/software.gif"
							alt="software" />
					</c:when>
					<c:when test="${fn:contains('FORMAT0009', formatType)}">
						<img src="${WebApplicationBaseURL}/images/pubtype/audio.gif"
							alt="audio" />
					</c:when>
					<c:when test="${fn:contains('FORMAT0010', formatType)}">
						<img src="${WebApplicationBaseURL}/images/pubtype/audiovisual.gif"
							alt="video" />
					</c:when>
					<c:otherwise>
						<img src="${WebApplicationBaseURL}/images/pubtype/unknown.gif"
							alt="unknown" />
					</c:otherwise>
				</c:choose>
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