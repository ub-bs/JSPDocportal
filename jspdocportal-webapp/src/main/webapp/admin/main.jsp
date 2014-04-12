<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>

<fmt:message var="pageTitle" key="Nav.AdminMenue" /> 
<stripes:layout-render name="../WEB-INF/layout/default.jsp" pageTitle = "${pageTitle}">
	<stripes:layout-component name="contents">
		<c:set var="type" value="${param.workflowProcessType}" />
		<c:choose>
			<c:when test="${empty type}">
				<h2><fmt:message key="Nav.AdminMenue" /></h2>
 				<div class="textblock2"><fmt:message key="Webpage.admin.AllowedFunctions" />:
 				<br />
				<mcr:outputNavigation mode="toc" id="admin"/>
				</div>
			</c:when>
			<c:otherwise>
				<h2><fmt:message key="Nav.Admin.${type}" /></h2>
			</c:otherwise>
		</c:choose>
		<p><fmt:message key="Webpage.admin.AllowedFunctions.Text" /></p>                
	 </stripes:layout-component>
</stripes:layout-render> 	 



