<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld"%>

<%@ attribute name="mcrid" required="true" type="java.lang.String" %>

<mcr:hasAccess var="modifyAllowed" permission="writedb" mcrid="${mcrid}" />
<c:if test="${modifyAllowed}">
	<mcr:isLocked var="locked" mcrid="${mcrid}" />
	<c:choose>
		<c:when test="${not locked}">
			<!--  Editbutton -->
			<a class="btn btn-primary btn-lg pull-right" style="padding:6px" 
			   href="${WebApplicationBaseURL}startedit.action?mcrid=${mcrid}" title="<fmt:message key="WF.common.object.EditObject" />">
   				<span class="glyphicon glyphicon-pencil"></span>
   			</a> 
		</c:when>
		<c:otherwise>
			<button class="btn btn-default btn-lg pull-right" style="padding:6px" disabled="disabled" 
      		        title="<fmt:message key="WF.common.object.EditObjectIsLocked" />">
   				<span class="glyphicon glyphicon-ban-circle"></span>
   			</button>
		</c:otherwise>
	</c:choose>         
</c:if>  