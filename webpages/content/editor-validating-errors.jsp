<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>
<%@ page import="org.apache.log4j.Logger" %>

<c:catch var="e">
	<c:set var="mcrid" value="${requestScope.mcrid}" />
	<c:set var="type" value="${requestScope.type}" />
	<c:set var="step" value="${requestScope.step}" />
	<c:set var="workflowType" value="${requestScope.workflowType}" />
	<c:set var="nextPath" value="${requestScope.nextPath}" />
	<c:set var="target" value="${requestScope.target}" />
	<c:set var="processID" value="${requestScope.processID}" />
	<c:set var="publicationType" value="${requestScope.publicationType}" />
	
	<c:set var="errorList" value="${requestScope.errorList}" />
	<fmt:setLocale value='${requestScope.lang}'/>
	<fmt:setBundle basename='messages'/>
	<div class="headline"><fmt:message key="WF.editor.ValidatorError.Headline" /></div>
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

	<mcr:includeEditor 
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
