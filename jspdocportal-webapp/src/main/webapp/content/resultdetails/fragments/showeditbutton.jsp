<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://www.mycore.org/jspdocportal/base.tld" prefix="mcr" %>

<%-- parameter: mcrid --%>

 <mcr:checkAccess var="modifyAllowed" permission="writedb" key="${param.mcrid}" />
 <c:if test="${modifyAllowed}">
 	<mcr:isObjectNotLocked var="bhasAccess" objectid="${param.mcrid}" />
 	<c:choose>
 		<c:when test="${bhasAccess}">
 			<!--  Editbutton -->
 			<form method="get" action="${applicationScope.WebApplicationBaseURL}StartEdit" class="resort">                 
				<%--<input name="page" value="nav?path=~workflowEditor-${doctype}"  type="hidden"> --%>                                       
				<input name="mcrid" value="${param.mcrid}" type="hidden"/>
				<input title="<fmt:message key="WF.common.object.EditObject" />" border="0" 
				       src="${applicationScope.WebApplicationBaseURL}images/workflow1.gif" type="image"  class="imagebutton" height="30" />
			</form> 
		</c:when>
		<c:otherwise>
			<img title="<fmt:message key="WF.common.object.EditObjectIsLocked" />" border="0" 
			     src="${applicationScope.WebApplicationBaseURL}images/workflow_locked.gif" height="30" />
		</c:otherwise>
	</c:choose>         
</c:if>   