<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>
<%@ taglib uri="http://www.mycore.de/jspdocportal/browsing" prefix="mcrb"%>
<%@ page import="org.apache.log4j.Logger" %>

<fmt:setLocale value='${requestScope.lang}'/>
<fmt:setBundle basename='messages'/>
<c:set var="WebApplicationBaseURL" value="${applicationScope.WebApplicationBaseURL}" />
<c:set var="mcrid">
   <c:choose>
      <c:when test="${!empty(requestScope.id)}">${requestScope.id}</c:when>
      <c:otherwise>${param.id}</c:otherwise>
   </c:choose>
</c:set>
<c:set var="from"  value="${param.fromWForDB}" /> 
<c:set var="debug" value="${param.debug}" />
<c:set var="style" value="${param.style}" />
<c:set var="type"  value="${fn:split(mcrid,'_')[1]}" /> 
<c:choose>
 <c:when test="${fn:contains(from,'workflow')}" >
     <c:set var="layout" value="preview" />
 </c:when>
 <c:otherwise>
     <c:set var="layout" value="normal" />
 </c:otherwise> 
</c:choose>

<c:catch var="e">
<mcr:debugInfo />
<div class="docdetails-toolbar">
	<div class="docdetails-toolbar-item">
		<mcrb:searchDetailBrowser/>
	</div>
     <c:if test="${empty(param.print) and !fn:contains(style,'user')}">
     		<div class="docdetails-toolbar-item">
		     <a href="${WebApplicationBaseURL}content/print_details.jsp?id=${param.id}&print=true&from=${param.fromWForDB}" target="_blank">
	          	<img src="${WebApplicationBaseURL}images/workflow_print.gif" border="0" alt="<fmt:message key="WF.common.printdetails" />"  class="imagebutton" height="30"/>
	         </a>
	         </div>
     </c:if>
 
   <c:if test="${!(fn:contains(from,'workflow')) && !fn:contains(style,'user')}" > 
     <mcr:checkAccess var="modifyAllowed" permission="writedb" key="${mcrid}" />
     <mcr:isObjectNotLocked var="bhasAccess" objectid="${mcrid}" />
      <c:if test="${modifyAllowed}">
        <c:choose>
         <c:when test="${bhasAccess}"> 
	         <!--  Editbutton -->
			<div class="docdetails-toolbar-item">
	         <form method="get" action="${WebApplicationBaseURL}StartEdit" class="resort">                 
	            <input name="page" value="nav?path=~workflowEditor-${type}"  type="hidden">                                       
	            <input name="mcrid" value="${mcrid}" type="hidden"/>
					<input title="<fmt:message key="WF.common.object.EditObject" />" border="0" src="${WebApplicationBaseURL}images/workflow1.gif" type="image"  class="imagebutton" height="30" />
	         </form> 
	         </div>
         </c:when>
         <c:otherwise>
           <div class="docdetails-toolbar-item">  
           		<img title="<fmt:message key="WF.common.object.EditObjectIsLocked" />" border="0" src="${WebApplicationBaseURL}images/workflow_locked.gif" height="30" />
           </div>
         </c:otherwise>
        </c:choose>
      </c:if>      
   </c:if>
</div>

<div class="headline">
   <fmt:message key="Webpage.docdetails.title">
   	<fmt:param>${mcrid}</fmt:param>   
   </fmt:message>
</div>

<c:set var="type" value="${fn:substringBefore(fn:substringAfter(mcrid, '_'),'_')}" />
<c:choose>
 <c:when test="${fn:contains('thesis', type)}">
     <c:import url="content/docdetails/docdetails_${type}.jsp">
     	<c:param name="id">${mcrid}</c:param>
     	<c:param name="fromWF">${fn:contains(from,'workflow')}</c:param>
     </c:import>
 </c:when>
 <c:otherwise>
     <c:import url="content/results-config/docdetails-document.jsp" />
 </c:otherwise>
 </c:choose>


</c:catch>
<c:if test="${e!=null}">
An error occured, hava a look in the logFiles!
<% 
  Logger.getLogger("test.jsp").error("error", (Throwable) pageContext.getAttribute("e"));   
%>
</c:if>
