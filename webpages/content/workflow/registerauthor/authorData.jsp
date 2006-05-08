<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages'/>
<mcr:getWorkflowEngineVariable pid="${requestScope.pid}" var="authorID" workflowVar="authorID" /> 
<mcr:getWorkflowEngineVariable pid="${requestScope.pid}" var="urn" workflowVar="reservatedURN" /> 

<div class="headline">
   <fmt:message key="WorkflowEngine.StartRegisterAuthorWorkflow" />
</div>

<table cellspacing="3" cellpadding="3" >
   <tr>
      <td colspan="2">
         <img title="" alt="" src="images/greenArrow.gif">
         <a href="${WebApplicationBaseURL}nav?path=~registerauthor"><fmt:message key="WorkflowEngine.forwardToWorkflow" /></a>
      </td>
   </tr> 
   <tr><td colspan="2">
     <hr/>
     <p><fmt:message key="Service.Hinweis1" /></p>
     <p>
        <mcr:getConfigProperty var="mail" prop="MCR.WorkflowEngine.contactemail.registerauthor" defaultValue="mycore@mycore.de" />
        <a href="mailto:${mail}">${mail}</a>
     </p>
     </td>
    </tr>
</table>
