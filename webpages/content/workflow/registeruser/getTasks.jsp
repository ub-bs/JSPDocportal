<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>

<c:set  var="baseURL" value="${applicationScope.WebApplicationBaseURL}"/>
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages' /> 

<c:choose>
   <c:when test="${requestScope.task.taskName eq 'userCreated'}">
      <tr>
       <td class="metaname"> 
         <fmt:message key="Nav.WorkflowRegisteruser.${requestScope.task.taskName}" />
       </td>
      </tr>
      <tr> 
	   <td>       
        <table width="100%" >
	       <c:import url="/content/workflow/registeruser/editorButtons.jsp" />
	    <tr><td>	       
       	   <a href="${baseURL}nav?path=~workflow-registeruser&endTask=userCantBeSubmitted&processID=${requestScope.task.processID}">Annehmen</a>
       	</td></tr>   
	    <tr><td>
	       <a href="${baseURL}nav?path=~workflow-registeruser&endTask=userCanBeSubmitted&processID=${requestScope.task.processID}">Ablehnen</a>
       	</td></tr>   
        </table>
	   </td>
	  </tr>	      
   </c:when>
   <c:otherwise>
    <h1>what else? TODO</h1>
   </c:otherwise>
</c:choose>
