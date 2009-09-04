<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>

<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages'/>

<c:choose>
   <c:when test="${!empty(param.processid)}">
      <c:set  var="processid" value="${param.processid}"/>
   </c:when>
   <c:otherwise>
      <c:set  var="processid" value="${requestScope.processid}"/>
   </c:otherwise>
</c:choose>

<c:choose>
   <c:when test="${!empty(param.mcrid)}">
      <c:set  var="mcrid" value="${param.mcrid}"/>
   </c:when>
   <c:otherwise>
      <c:set  var="mcrid" value="${requestScope.mcrid}"/>
   </c:otherwise>
</c:choose>

<c:choose>
   <c:when test="${!empty(param.isNewEditorSource)}">
      <c:set  var="isNewEditorSource" value="${param.isNewEditorSource}"/>
   </c:when>
   <c:otherwise>
      <c:set  var="isNewEditorSource" value="${requestScope.isNewEditorSource}"/>
   </c:otherwise>
</c:choose>

<c:choose>
   <c:when test="${!empty(param.type)}">
      <c:set  var="type" value="${param.type}"/>
   </c:when>
   <c:otherwise>
      <c:set  var="type" value="${requestScope.type}"/>
   </c:otherwise>
</c:choose>

<c:choose>
   <c:when test="${!empty(param.workflowType)}">
      <c:set  var="workflowType" value="${param.workflowType}"/>
   </c:when>
   <c:otherwise>
      <c:set  var="workflowType" value="${requestScope.workflowType}"/>
   </c:otherwise>
</c:choose>

<c:choose>
   <c:when test="${!empty(param.publicationType)}">
      <c:set  var="publicationType" value="${param.publicationType}"/>
   </c:when>
   <c:otherwise>
      <c:set  var="publicationType" value="${requestScope.publicationType}"/>
   </c:otherwise>
</c:choose>

<c:choose>
   <c:when test="${!empty(param.step)}">
      <c:set  var="step" value="${param.step}"/>
   </c:when>
   <c:otherwise>
      <c:set  var="step" value="${requestScope.step}"/>
   </c:otherwise>
</c:choose>

<c:choose>
   <c:when test="${!empty(param.nextPath)}">
      <c:set  var="nextPath" value="${param.nextPath}"/>
   </c:when>
   <c:otherwise>
      <c:set  var="nextPath" value="${requestScope.nextPath}"/>
   </c:otherwise>
</c:choose>

<c:choose>
   <c:when test="${!empty(param.uploadID)}">
      <c:set  var="uploadID" value="${param.uploadID}"/>
   </c:when>
   <c:otherwise>
      <c:set  var="uploadID" value="${requestScope.uploadID}"/>
   </c:otherwise>
</c:choose>

<c:choose>
   <c:when test="${!empty(param.mcrid2)}">
      <c:set  var="mcrid2" value="${param.mcrid2}"/>
   </c:when>
   <c:otherwise>
      <c:set  var="mcrid2" value="${requestScope.mcrid2}"/>
   </c:otherwise>
</c:choose>

<c:set  var="editorSessionID" value="${param['XSL.editor.session.id']}"/>
<c:if test="${empty(editorSessionID)}">
	<c:set  var="editorSessionID" value="${param.XSL.editor.session.id}"/>
</c:if>

<c:choose>
   <c:when test="${!empty(param.target)}">
      <c:set  var="target" value="${param.target}"/>
   </c:when>
   <c:when test="${!empty(requestScope.target)}">
      <c:set  var="target" value="${requestScope.target}"/>
   </c:when>   
   <c:otherwise>
      <c:set  var="target" value="MCRCheckMetadataServlet"/>
   </c:otherwise>
</c:choose>

<c:choose>
   <c:when test="${!empty(param.editorPath)}">
      <c:set  var="editorPath" value="${param.editorPath}"/>
   </c:when>
   <c:when test="${!empty(requestScope.editorPath)}">
      <c:set  var="editorPath" value="${requestScope.editorPath}"/>
   </c:when>   
   <c:otherwise>
      <c:set  var="editorPath" value=""/>
   </c:otherwise>
</c:choose>

<c:choose>
   <c:when test="${!empty(param.editorSource)}">
      <c:set  var="editorSource" value="${param.editorSource}"/>
   </c:when>
   <c:when test="${!empty(requestScope.editorSource)}">
      <c:set  var="editorSource" value="${requestScope.editorSource}"/>
   </c:when>   
   <c:otherwise>
      <c:set  var="editorSource" value=""/>
   </c:otherwise>
</c:choose>


<hr/>

<mcr:checkAccess var="modifyAllowed" permission="writedb" key="${mcrid}" />
<%-- TODO in subselect übergeben von mcrid--%>
<c:set var="modifyAllowed" value="true" />
<c:choose>
    <c:when test="${modifyAllowed}">
        <mcr:includeEditorInWorkflow 
          editorSessionID="${editorSessionID}"  
          isNewEditorSource="${isNewEditorSource}" 
          mcrid2="${mcrid2}"  
          uploadID="${uploadID}"    
          mcrid="${mcrid}" 
          type="${type}" 
          processid="${processid}" 
          workflowType="${workflowType}"
          publicationType="${publicationType}" 
          step="${step}" 
          target="${target}" 
          nextPath="${nextPath}" 
          editorPath="${editorPath}" 
          editorSource="${editorSource}"/>        
    </c:when>
    <c:otherwise>
        <span class="error"><fmt:message key="WF.common.PrivilegesError" /></span>
    </c:otherwise>
</c:choose>

       
<hr/>	
