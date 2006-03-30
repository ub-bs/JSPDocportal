<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>

<c:set  var="baseURL" value="${applicationScope.WebApplicationBaseURL}"/>
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages' /> 

<c:choose>
   <c:when test="${requestScope.task.taskName eq 'completeUserAndSendToLibrary'}">
      <fmt:message key="Nav.WorkflowRegisteruser.${requestScope.task.taskName}" />
      <c:import url="/content/workflow/registeruser/editorButtons.jsp" />
	  <mcr:checkDecisionNode var="canBeSent" processID="${requestScope.task.processID}" workflowType="registeruser" decision="canUserSubmitted" />
      <c:if test="${!empty(canBeSent)}">
         <a href="${baseURL}nav?path=~workflow-registeruser&endTask=completeUserAndSendToLibrary&processID=${requestScope.task.processID}"><fmt:message key="Nav.Application.registeruser.end" /></a>
      </c:if>
   </c:when>
   <c:otherwise>
    <h1>what else? TODO</h1>
   </c:otherwise>
</c:choose>
