<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib  prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>
<%@ page import="org.apache.log4j.Logger" %>


<fmt:message var="pageTitle" key="WF.editor.ValidatorError.Headline" /> 
<stripes:layout-render name="../WEB-INF/layout/default.jsp" pageTitle = "${pageTitle}" layout="1column">
	<stripes:layout-component name="contents">

<c:catch var="e">
	<c:set var="mcrid" value="${requestScope.mcrid}" />
	<c:set var="type" value="${requestScope.type}" />
	<c:set var="step" value="${requestScope.step}" />
	<c:set var="workflowType" value="${requestScope.workflowType}" />
	<c:set var="nextPath" value="${requestScope.nextPath}" />
	<c:set var="target" value="${requestScope.target}" />
	
	<c:choose>
   <c:when test="${not empty param.processid}">
      <c:set  var="processid" value="${param.processid}"/>
   </c:when>
   <c:otherwise>
      <c:set  var="processid" value="${requestScope.processid}"/>
   </c:otherwise>
</c:choose>

	<c:set var="publicationType" value="${requestScope.publicationType}" />
	
	<c:set var="errorList" value="${requestScope.errorList}" />

	<h2>${pageTitle}></h2>
	<table>
	   <c:forEach items="${errorList}" var="errorEntry" varStatus="status">
	      <tr>
	         <td style="color:red;">${errorEntry}</td>
	      </tr>
	   </c:forEach>
	</table>
	<div>
	   <fmt:message key="WF.editor.ValidatorError.Instructions" />
	</div>
 
	<mcr:includeEditorInWorkflow
          isNewEditorSource="false" 
          mcrid="${mcrid}" 
          type="${type}" processid="${processid}" workflowType="${workflowType}"
          publicationType="${publicationType}" step="${step}" target="${target}" 
          nextPath="${nextPath}" editorPath="${editorPath}" />        

</c:catch>
<c:if test="${e!=null}">
An error occured, hava a look in the logFiles!
<% 
  Logger.getLogger("test.jsp").error("error", (Throwable) pageContext.getAttribute("e"));   
%>
</c:if>
	</stripes:layout-component>
</stripes:layout-render>