<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"   %>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>

<fmt:message var="pageTitle" key="Webpage.title.${fn:replace(actionBean.path, '/', '.')}" />
<c:set var="layout">2columns</c:set>
<c:if test="${not empty actionBean.info}"><c:set var="layout">3columns</c:set></c:if>

<stripes:layout-render name="../WEB-INF/layout/default.jsp" pageTitle = "${pageTitle}" layout="${layout}">
	<stripes:layout-component name="contents">
		<div class="ur-box ur-text">
			<h2>Pfad: ${actionBean.path}</h2>
			<mcr:includeWebcontent id="${fn:replace(actionBean.path, '/', '.')}" file="${actionBean.path}.html" />
		</div>
	</stripes:layout-component>
	<stripes:layout-component name="right_side">
		<c:forEach var="id" items="${fn:split(actionBean.info,',')}" >
			<div class="ur-box ur-box-bordered ur-infobox">
				<mcr:includeWebcontent id="${id}" file="${fn:replace(id, '.', '/')}.html" />
			</div>
		</c:forEach>
	</stripes:layout-component>
</stripes:layout-render>
