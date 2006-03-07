<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn" %>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>
<mcr:session method="get" var="username" type="userID" />
<c:set var="WebApplicationBaseURL" value="${applicationScope.WebApplicationBaseURL}" />
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages'/>
<c:choose>
   <c:when test="${empty param.workflowType}">
      <c:set var="workflowType" value="xmetadiss" />
   </c:when>
   <c:otherwise>
      <c:set var="workflowType" value="${param.workflowType}" />
   </c:otherwise>
</c:choose>
<mcr:getWorkflowDocumentID userid="${username}" mcrid="mcrid" status="status"  workflowProcessType="${workflowType}"/>

<c:set var="documentid" value="${mcrid}" />

<div class="headline">
   <fmt:message key="Nav.Application.dissertation" /> - 
   <fmt:message key="Nav.Application.dissertation.edit" />
   | ID: <c:out value="${mcrid}" />
</div>


<table cellspacing="3" cellpadding="3" >
   <tr>   
      <td class="metaname" >Ergebnis:</td>
      <td class="metavalue"><fmt:message key="SWF.Dissertation.${status}" /> </td>       
   </tr>    
   <tr>
      <td class="metaname" >Nächste Aktionen:</td>
      <td class="metavalue"><fmt:message key="SWF.Dissertation.next.${status}" /> </td>       
   </tr>    
</table>

<c:if test="${!empty(documentid)}">
   <mcr:includeEditor 
		isNewEditorSource="false" 
		mcrid="${documentid}" type="disshab" step="author" target="MCRCheckMetadataServlet" nextPath="~mydiss"/>
</c:if>
   
