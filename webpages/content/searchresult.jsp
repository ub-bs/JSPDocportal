<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr"%>
<%@ taglib uri="http://www.mycore.de/jspdocportal/browsing" prefix="mcrb"%>
<fmt:setBundle basename="messages" />
<c:choose>
	<c:when test="${fn:contains(requestScope.path,'browse')}">
		<c:set var="headlineKey"
			value="Webpage.searchresults.result-document-browse" />
	</c:when>
	<c:otherwise>
		<c:set var="headlineKey"
			value="Webpage.searchresults.result-document-search" />
	</c:otherwise>
</c:choose>
<div class="headline"><fmt:message key="${headlineKey}" /></div>
   <mcr:debugInfo/>
   <mcrb:searchresultBrowser varmcrid="mcrid" varurl="url" sortfields="title author modified">
   		<c:set var="doctype" value="${fn:substringBefore(fn:substringAfter(mcrid, '_'),'_')}" />
   		<c:catch var ="catchException">
			<c:import url="${applicationScope.WebApplicationBaseURL}content/resultdetails/resultdetails_${doctype}.jsp">
				<c:param name="id" value="${mcrid}" />
				<c:param name="url" value="${url}" />
			</c:import>
		</c:catch>
		<c:if test = "${catchException!=null}">
			<br />
			An error occured while displaying resultlist details for ${doctype} : ${catchException.message}
			<br />
		</c:if>
   </mcrb:searchresultBrowser>