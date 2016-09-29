<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"   %>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>

<fmt:message var="pageTitle" key="Webpage.title.${fn:replace(actionBean.path, '/', '.')}" />
<c:set var="layout">2columns_left</c:set>
<c:if test="${not empty actionBean.info}"><c:set var="layout">3columns</c:set></c:if>

<stripes:layout-render name="/WEB-INF/layout/default.jsp" pageTitle = "${pageTitle}" layout="${layout}">
	<stripes:layout-component name="left_side">
		<div class="ir-box ir-box-bordered">
		<div class="main_navigation">
			<mcr:outputNavigation id="left" cssClass="nav ir-sidenav" expanded="true" mode="left" />
		</div>

		<div style="padding-top: 32px; padding-bottom: 32px; text-align: center;">
			<a href="http://www.mycore.org"> <img
		       alt="powered by MyCoRe 2016_LTS"
			   src="${WebApplicationBaseURL}images/mycore_logo_powered_129x34_knopf_hell.png"
			   style="border: 0; text-align: center;" />
			</a>
		</div>
		</div>
	</stripes:layout-component>
	<stripes:layout-component name="contents">
		<div class="ir-box">
			<mcr:includeWebcontent id="${fn:replace(actionBean.path, '/', '.')}" file="${actionBean.path}.html" />
		</div>
	</stripes:layout-component>
	<stripes:layout-component name="right_side">
		<c:forEach var="id" items="${fn:split(actionBean.info,',')}" >
			<div class="ir-box ir-box-bordered ir-infobox">
				<mcr:includeWebcontent id="${id}" file="${fn:replace(id, '.', '/')}.html" />
			</div>
		</c:forEach>
	</stripes:layout-component>
</stripes:layout-render>
