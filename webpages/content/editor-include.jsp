<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>

<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages'/>

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
<c:set var="editorSessionID" value="${param['XSL.editor.session.id']}" />

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


<hr/>
workflowType: ${workflowType} |type: ${type} | mcrid: ${mcrid} | step: ${step} | nextPath: ${nextPath} | uploadID: ${uploadID} |  isNewEditorSource: ${isNewEditorSource}

<hr/>
<mcr:checkAccess var="modifyAllowed" permission="writedb" key="${mcrid}" />

TODO übergeben von mcrid TODO bis dahin modifyAllowed = true<c:set var="modifyAllowed" value="true" />
<c:choose>
    <c:when test="${modifyAllowed eq 'true'}">
        <mcr:includeEditor 
          editorSessionID="${editorSessionID}"  isNewEditorSource="${isNewEditorSource}" 
          mcrid2="${mcrid2}"  uploadID="${uploadID}"
          mcrid="${mcrid}" type="${type}" workflowType="${workflowType}"
          step="${step}" target="${target}" nextPath="${nextPath}" editorPath="${editorPath}" />        
    </c:when>
    <c:otherwise>
        <font color="#ff0000"><fmt:message key="SWF.PrivilegesError" /></font>
    </c:otherwise>
</c:choose>

       
<hr/>	
