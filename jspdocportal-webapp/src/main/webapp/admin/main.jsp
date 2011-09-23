<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="mcr" uri="/WEB-INF/lib/mycore-taglibs.jar" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>

<fmt:message var="pageTitle" key="Nav.AdminMenue" /> 
<stripes:layout-render name="../WEB-INF/layout/default.jsp" pageTitle = "${pageTitle}">
	<stripes:layout-component name="contents">
		<c:set var="type" value="${param.workflowProcessType}" />
		<c:choose>
			<c:when test="${empty type}">
				<div class="headline"><fmt:message key="Nav.AdminMenue" /></div>
 				<div class="textblock2"><fmt:message key="Webpage.admin.AllowedFunctions" />:
 				<br />
				<mcr:outputNavigation currentPath="admin.admin" mode="toc" id="admin"/>
				</div>
			</c:when>
			<c:otherwise>
				<div class="headline"><fmt:message key="Nav.Admin.${type}" /></div>
			</c:otherwise>
		</c:choose>
		<p><fmt:message key="Webpage.admin.AllowedFunctions.Text" /></p>                
	 </stripes:layout-component>
</stripes:layout-render> 	 



