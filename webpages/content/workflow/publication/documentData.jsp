<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages'/>
<mcr:getWorkflowEngineVariable pid="${requestScope.pid}" var="urn" workflowVar="reservatedURN" /> 
<mcr:getWorkflowEngineVariable pid="${requestScope.pid}" var="docID" workflowVar="createdDocID" /> 

<div class="headline">
   <fmt:message key="Nav.Workflow.publication.begin" />
</div>

<table cellspacing="3" cellpadding="3" >
<c:if test="${!empty(docID)}">
   <tr valign="top">
        <td class="metaname"><fmt:message key="SWF.Document.Created" /> </td>
        <td class="metavalue">            
         <b><c:out value="${docID}" /></b>
         <br/>
         <br/>
         <i><fmt:message key="SWF.Document.Created.next" /></i>
      </td>
   </tr>     
</c:if>
<c:if test="${!empty(urn)}">
   <tr valign="top">
        <td class="metaname"><fmt:message key="SWF.Document.URN" /> </td>
        <td class="metavalue">            
         <b><c:out value="${urn}" /></b>
         <br/>
         <br/>
         <i><fmt:message key="SWF.Document.URN.Hinweis" /></i>
      </td>
   </tr>     
</c:if>
   <tr>
      <td colspan="2">
         <img title="" alt="" src="images/greenArrow.gif">
         <a href="${WebApplicationBaseURL}nav?path=~publication"><fmt:message key="WorkflowEngine.forwardToWorkflow" /></a>
      </td>
   </tr> 
   <tr><td colspan="2">
     <hr/>
     <p><fmt:message key="Service.Hinweis1" /></p>
     <p>
        <mcr:getConfigProperty var="mail" prop="MCR.WorkflowEngine.contactemail.publication" defaultValue="mycore@mycore.de" />
        <a href="mailto:${mail}">${mail}</a>
     </p>
     </td>
    </tr>
</table>
