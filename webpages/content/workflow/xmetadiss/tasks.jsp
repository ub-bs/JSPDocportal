<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>

<mcr:session method="get" var="username" type="userID" />
<c:set  var="baseURL" value="${applicationScope.WebApplicationBaseURL}"/>

<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages' />
<c:set var="debug" value="true" />
<c:choose>
   <c:when test="${requestScope.task eq 'start'}">
      <c:set var="statusKey" value="WorkflowEngine
      <fmt:message key="WorkflowEngine.ActualStateOfYourDissertation.xmetadiss">:<fmt:message key="WorkflowEngine.initiator.statusMessage.${requestScope.workflowStatus}.xmetadiss" />
   </c:when>
   <c:when test="completeDisshabAndSendToLibrary">
   
   </c:when>
   <c:otherwise>
    heiko<br>
       ${requestScope.task.taskName} <br>
       ${requestScope.task.processID} <br>
       ${requestScope.task.workflowStatus} <br>
       ${requestScope.task.workflowProcessType} <br>
       <c:set var="dom" value="${requestScope.task.variables}" />
       <x:forEach select="$dom/variables/variable"> 
          <x:out select="./@name" />
       </x:forEach>   
   </c:otherwise>
</c:choose>
