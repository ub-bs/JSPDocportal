<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr"%>

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