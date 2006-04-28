<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>

<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages' />
<c:set var="userHasAdminPermission" value="false" />

<table width="100%">
	<tr>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td>
			<div class="headline"><fmt:message key="Nav.AdminMenue" /></div>
			<h3><fmt:message key="Admin.AllowedFunctions" />:</h3>
		
			<ul>
			<mcr:checkAccess var="canDo" permission="administrate-accessrules" key="" />
			<c:if test="${canDo}">				
			    <c:set var="userHasAdminPermission" value="true" />
				<li><a href="${applicationScope.WebApplicationBaseURL}nav?path=admin.accessrules"><fmt:message key="Nav.Admin.AccessRules" /></a></li>
			</c:if>				
			<mcr:checkAccess var="canDo" permission="administrate-user" key="" />
			<c:if test="${canDo}">				
     			<c:set var="userHasAdminPermission" value="true" />
				<li><a href="${applicationScope.WebApplicationBaseURL}nav?path=admin.usermanagement"><fmt:message key="Nav.Admin.UserManagement" /></a></li>
			</c:if>	
			<mcr:checkAccess var="canDo" permission="administrate-xmetadiss" key="" />
			<c:if test="${canDo}">
     			<c:set var="userHasAdminPermission" value="true" />			
				<li><a href="${applicationScope.WebApplicationBaseURL}nav?path=admin.xmetadiss"><fmt:message key="Nav.Admin.Xmetadiss" /></a></li>
			</c:if>	
			</ul>
			
		</td>
	</tr>
</table>

<c:if test="${!userHasAdminPermission}">
   <font color="red"><fmt:message key="Admin.NoPermissionError" /></font>
</c:if>