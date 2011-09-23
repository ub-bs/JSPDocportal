<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="mcr" uri="/WEB-INF/lib/mycore-taglibs.jar" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>

<fmt:setBundle basename="messages"/>
<fmt:message var="pageTitle" key="Nav.Search" /> 
<stripes:layout-render name="../WEB-INF/layout/default.jsp" pageTitle = "${pageTitle}" currentPath="left.search">
	<stripes:layout-component name="contents">
		<%--
			<mcr:session var="sessionID" method="get" type="ID" />
			<c:set var="WebApplicationBaseURL" value="${applicationScope.WebApplicationBaseURL}" />
		--%>
		<div class="headline"><fmt:message key="Nav.Search" /></div>
			<p><fmt:message key="Webpage.intro.search.Possibilities" /> </p>
			<p><mcr:includeEditor editorPath="editor/searchmasks/SearchMask_AllMetadataFields.xml"/></p>

			<p><mcr:outputNavigation expanded="false" currentPath="left.search" mode="toc" id="left"/></p>
			<p><mcr:includeWebContent file="search_introtext.html"/></p>
	</stripes:layout-component>
</stripes:layout-render>