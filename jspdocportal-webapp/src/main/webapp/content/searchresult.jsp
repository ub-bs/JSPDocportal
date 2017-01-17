<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://www.mycore.org/jspdocportal/base.tld" prefix="mcr"%>
<%@ taglib uri="http://www.mycore.de/jspdocportal/browsing" prefix="mcrb"%>
<fmt:setLocale value="${requestScope.lang}"/>
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
   <mcr:debugInfo />
   <mcrb:searchresultBrowser varmcrid="mcrid" varurl="url" sortfields="title author modified">
   		<c:set var="doctype" value="${fn:substringBefore(fn:substringAfter(mcrid, '_'),'_')}" />
   		<c:catch var ="catchException">
			<jsp:include page="resultdetails/resultdetails_${doctype}.jsp">
				<jsp:param name="id" value="${mcrid}" />
				<jsp:param name="url" value="${url}" />
			</jsp:include>
		</c:catch>
		<c:if test = "${catchException!=null}">
			<br />
			An error occured while displaying resultlist details for ${doctype} : ${catchException.message}
			<br />
		</c:if>
   </mcrb:searchresultBrowser>