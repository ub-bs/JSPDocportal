<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages'/>
<mcr:getWorkflowEngineVariable pid="${requestScope.pid}" var="authorID" workflowVar="${applicationScope.constants.authorIdWorkflowVariable}" /> 
<mcr:getWorkflowEngineVariable pid="${requestScope.pid}" var="urn" workflowVar="${applicationScope.constants.reservatedUrnWorkflowVariable}" /> 
<mcr:receiveMcrObjAsJdom varDom="authorobject" mcrid="${authorID}" />

<div class="headline">
   <fmt:message key="WF.xmetadiss.begin" />
</div>

<table cellspacing="3" cellpadding="3" >
<c:if test="${!empty(authorobject)}">
 <x:forEach select="$authorobject/mycoreobject" >
   <tr valign="top">
      <td class="metaname"><fmt:message key="WF.xmetadiss.Author" /> </td>
      <td class="metavalue">  
         <x:out select="./metadata/names/name/fullname" escapeXml="false" />
      </td>
   </tr>  
   <tr valign="top" >
      <td class="metaname"><fmt:message key="WF.xmetadiss.Author.ID" /> 
      <%-- LINK AUF WORKFLOW DER AUTOREN --%>
      </td>
      <td class="metavalue">  
         <x:out select="./@ID" />
         <x:set var="mcrid" select="string(./@ID)" />
         &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 
   <c:if test="${!(fn:contains(from,'workflow')) && !fn:contains(style,'user')}" > 
     <mcr:checkAccess var="modifyAllowed" permission="writedb" key="${mcrid}" />
     <mcr:isObjectNotLocked var="bhasAccess" objectid="${mcrid}" />
      <c:if test="${modifyAllowed}">
        <c:choose>
         <c:when test="${bhasAccess}"> 
	         <!--  Editbutton -->
		 <%--         <form method="get" action="${WebApplicationBaseURL}StartEdit" class="resort">                 
	            <input name="page" value="nav?path=~workflowEditor-author"  type="hidden">                                       
	            <input name="mcrid" value="${mcrid}" type="hidden"/>
					<input title="<fmt:message key="WF.common.object.EditObject" />" border="0" src="${WebApplicationBaseURL}images/workflow1.gif" type="image"  class="imagebutton" />
	         </form>  --%>
	         <a href="${WebApplicationBaseURL}StartEdit?page=nav?path=~workflowEditor-author&mcrid=${mcrid}"><fmt:message key="WF.common.object.EditObjectAuthor" /></a>
         </c:when>
         <c:otherwise>
         <%--   <img title="<fmt:message key="WF.common.object.EditObjectIsLocked" />" border="0" src="${WebApplicationBaseURL}images/workflow_locked.gif" /> --%>
         (<fmt:message key="WF.common.object.EditObjectIsLockedAuthor"/>)
         </c:otherwise>
        </c:choose>         
      </c:if>      
   </c:if>
	</td>
   </tr>  
   <tr valign="top">
        <td class="metaname"><fmt:message key="WF.xmetadiss.URN" /> </td>
        <td class="metavalue">            
         <x:set var="mcrid" select="string(./@ID)" />
         <b><c:out value="${urn}" /></b>
         <br/>
         <br/>
         <i><fmt:message key="WF.xmetadiss.URN.Hinweis" /></i>
      </td>
   </tr>     
 </x:forEach>
</c:if>
   <tr>
      <td colspan="2">
         <img title="" alt="" src="images/greenArrow.gif">
         <a href="${WebApplicationBaseURL}nav?path=~xmetadiss"><fmt:message key="WF.common.forwardToWorkflow" /></a>
      </td>
   </tr> 
   <tr><td colspan="2">
     <hr/>
     <p><fmt:message key="Webpage.intro.Service.Hinweis1" /></p>
     <p>
        <mcr:getConfigProperty var="mail" prop="MCR.WorkflowEngine.contactemail.xmetadiss" defaultValue="mycore@mycore.de" />
        <a href="mailto:${mail}">${mail}</a>
     </p>
     </td>
    </tr>
</table>
