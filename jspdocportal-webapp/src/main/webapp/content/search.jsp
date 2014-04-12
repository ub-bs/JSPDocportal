<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>

<fmt:setBundle basename="messages" />
<fmt:message var="pageTitle" key="Nav.Search" /> 
<stripes:layout-render name="../WEB-INF/layout/default.jsp" pageTitle = "${pageTitle}">
	<stripes:layout-component name="html_header">
			<link type="text/css" rel="stylesheet" href="${WebApplicationBaseURL}css/style_editor.css" />
	</stripes:layout-component>
	<stripes:layout-component name="contents">
		<%--
			<mcr:session var="sessionID" method="get" type="ID" />
			<c:set var="WebApplicationBaseURL" value="${applicationScope.WebApplicationBaseURL}" />
		--%>
		<h2><fmt:message key="Nav.Search" /></h2>
			<p><fmt:message key="Webpage.intro.search.Possibilities" /> </p>
			<p><mcr:includeEditor editorPath="editor/searchmasks/SearchMask_AllMetadataFields.xml"/></p>

			<p><mcr:outputNavigation expanded="false" mode="toc" id="left"/></p>
			<p><mcr:includeWebContent file="search_introtext.html"/></p>
	</stripes:layout-component>
</stripes:layout-render>