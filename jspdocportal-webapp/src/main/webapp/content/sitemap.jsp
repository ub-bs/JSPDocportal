<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"   %>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>
<fmt:setBundle basename="messages"/>
<fmt:message var="pageTitle" key="Nav.Archive" /> 
<stripes:layout-render name="../WEB-INF/layout/default.jsp" pageTitle = "${pageTitle}">
	<stripes:layout-component name="contents">

<c:set var="Navigation" value="${applicationScope.navDom}" />
<div class="headline"><fmt:message key="Nav.Sitemap" /></div>
<table class="max" cellspacing="0" cellpadding="0">
	<tr width="100%">
		<th class="sitemap"><fmt:message key="Nav.MainmenueLeft" /></th>
		<th class="sitemap"><fmt:message key="Nav.MenuAbove" /></th>
		<th class="sitemap"><fmt:message key="Nav.AdminMenue" /></th>
	</tr>	
	<tr valign="top">
		<td>
			<x:set scope="session" var="recNavPath" select="$Navigation//navigation[@name='left']/navitem[@name='left']"/>
			<c:import url="/content/sitemap_items_rec.jsp" />
		</td>
		<td>
			<x:set scope="session" var="recNavPath" select="$Navigation//navigation[@name='top']/navitem[@name='top']"/>
			<c:import url="/content/sitemap_items_rec.jsp" />
		</td>
		<td>
			<x:set scope="session" var="recNavPath" select="$Navigation//navigation[@name='admin']/navitem[@name='admin']"/>
			<c:import url="/content/sitemap_items_rec.jsp" />
		</td>
	</tr>
</table>
	</stripes:layout-component>
</stripes:layout-render>    