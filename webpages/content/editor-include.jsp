<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>

<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages'/>

<c:set  var="mcrid"    value="${param.mcrid}"/>
<c:set  var="isNewEditorSource">
 <c:choose>
  <c:when test="${param.start=='withdata'}">false</c:when>
  <c:otherwise>true</c:otherwise>
 </c:choose>
</c:set>
<c:set  var="type"     value="${param.type}" />
<c:set  var="step"     value="${param.step}" />
<c:set  var="nextPath" value="${param.nextPath}" />

<hr/>

<mcr:includeEditor 
	isNewEditorSource="${isNewEditorSource}" 
	mcrid="${mcrid}" type="${type}" step="${step}" target="MCRCheckMetadataServlet" nextPath="${nextPath}"/>	
	
<hr/>	