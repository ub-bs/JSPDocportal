<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr"%>

<c:set var="WebApplicationBaseURL"	value="${applicationScope.WebApplicationBaseURL}" />
<c:set var="Navigation" value="${applicationScope.navDom}" />
<c:set var="path" value="${requestScope.path}" />
<c:set var="contentPage" value="${requestScope.content}" />
<c:set var="nodeID" value="${requestScope.nodeID}" />
<c:set var="pathID" value="${requestScope.pathID}" />
<c:set var="youAreHere" value="${requestScope.youAreHere}" />
<c:set var="langfreeRequestURL" value="${langfreeRequestURL}" />
<c:choose>
	<c:when test="${!empty(param.lang)}">
		<c:set var="lang" value="${param.lang}" />
		<c:if test="${!fn:contains('de-en',lang)}">
			<c:set var="lang" value="de" />
		</c:if>
		<mcr:session method="set" type="language" var="lang" />
	</c:when>
	<c:otherwise>
		<mcr:session method="get" type="language" var="lang" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test="${fn:contains('de',lang)}">
		<c:set var="translateLang" value="en" />
	</c:when>
	<c:otherwise>
		<c:set var="translateLang" value="de" />
	</c:otherwise>
</c:choose>
<c:set var="lang" value="${pageScope.lang}" scope="request" />
<c:import var="includePage" url='${contentPage}' />
<html>
<head>
	<fmt:setLocale value="${lang}" />
	<fmt:setBundle basename='messages' />
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<fmt:message var="pageTitle" key="Title.${path}" />
	<title><c:choose>
		<c:when test="${fn:startsWith(pageTitle,'???')}">
			<x:set var="altTitle"
				select="string($youAreHere//navitem[last()]/@label)" />
			<fmt:message key="${altTitle}" />
		</c:when>
		<c:otherwise>
			<c:out value="${pageTitle}" />
		</c:otherwise>
	</c:choose> @ <fmt:message key="Title.DocPortalTrailer" /></title>
	
	<script src="${WebApplicationBaseURL}javascript/jspdocportal.js" type="text/javascript"></script>
	<link type="text/css" rel="stylesheet" href="${WebApplicationBaseURL}css/style_general.css">
	<link type="text/css" rel="stylesheet" href="${WebApplicationBaseURL}css/style_navigation.css">
	<link type="text/css" rel="stylesheet" href="${WebApplicationBaseURL}css/style_content.css">
	<link type="text/css" rel="stylesheet" href="${WebApplicationBaseURL}admin/css/admin.css" />
</head>
<body topmargin="0" rightmargin="0" leftmargin="0">
 <table id="maintable" cellpadding="0" cellspacing="0">
	<tr class="max">
		<td id="mainLeftColumn"><a href="${WebApplicationBaseURL}"><img	id="logo" alt="Logo" src="${WebApplicationBaseURL}images/logo.gif"></a>
		<!-- Navigation Left Start -->
		<div class="navi_main">
		<table class="navi_main" cellpadding="0" cellspacing="0">
			<tr>
				<td style="height: 1px; width: 7%;"><img title="" alt=""
					style="width: 1px; height: 1px;"
					src="${WebApplicationBaseURL}images/emtyDot1Pix.gif"></td>
				<td style="width: 9%;"><img title="" alt=""
					style="width: 1px; height: 1px;"
					src="${WebApplicationBaseURL}images/emtyDot1Pix.gif"></td>
				<td style=""><img title="" alt="" style="width: 1px; height: 1px;"
					src="${WebApplicationBaseURL}images/emtyDot1Pix.gif"></td>
				<td style="width: 7%;"><img title="" alt=""
					style="width: 1px; height: 1px;"
					src="${WebApplicationBaseURL}images/emtyDot1Pix.gif"></td>
			</tr>
			<x:forEach
				select="$Navigation//navigation[@name='left']/navitem[@name='left']/navitem[not(@hidden = 'true')]">
				<x:set var="href1" select="string(./@path)" />
				<x:set var="labelKey1" select="string(./@label)" />
				<tr>
					<td style="height:17px;"><img title="" alt=""
						style="width:1px; height:1px"
						src="${WebApplicationBaseURL}images/emtyDot1Pix.gif"></td>
					<th align="left" colspan="4"><a target="_self" href='${href1}'><fmt:message
						key="${labelKey1}" /></a></th>
				</tr>

				<x:if select="contains($pathID,./@systemID)">
					<x:forEach select="./navitem[not(@hidden = 'true')]">
						<x:set var="href2" select="string(./@path)" />
						<x:set var="labelKey2" select="string(./@label)" />
						<x:choose>
							<x:when select="../navitem[last()]/@systemID = ./@systemID">
								<x:set var="end" select="'_end'" />
							</x:when>
							<x:otherwise>
								<x:set var="end" select="''" />
							</x:otherwise>
						</x:choose>
						<c:set var="imghref2"
							value="${WebApplicationBaseURL}images/line-with-element${end}.gif" />
						<tr>
							<td style="height:17px;"><img title="" alt=""
								style="width:1px; height:17px;"
								src="${WebApplicationBaseURL}images/emtyDot1Pix.gif"></td>
							<td align="center"><img title="" alt="" src="${imghref2}"></td>
							<td colspan="2"><x:choose>
								<x:when select="contains($pathID,./@systemID)">
									<span class="marked"><a target="_self" href="${href2}"><fmt:message
										key="${labelKey2}" /></a></span>
								</x:when>
								<x:otherwise>
									<a target="_self" href="${href2}"><fmt:message
										key="${labelKey2}" /></a>
								</x:otherwise>
							</x:choose></td>
						</tr>
					</x:forEach>
					<x:if select="../navitem[last()]/@systemID != ./@systemID">
						<tr>
							<td colspan="5" style="height:10px;"><img title="" alt=""
								style="width:1px; height:1px"
								src="${WebApplicationBaseURL}images/emtyDot1Pix.gif"></td>
						</tr>
					</x:if>

				</x:if>
			</x:forEach>
			<tr>
				<td colspan="5" style="height:15px;"><img title="" alt=""
					style="width:1px; height:1px"
					src="${WebApplicationBaseURL}images/emtyDot1Pix.gif"></td>
			</tr>
			<!-- NAVIGATION ADMIN BEGIN -->
			<x:forEach
				select="$Navigation//navigation[@name='admin']/navitem[@name='admin']/navitem[not(@hidden = 'true')]">
				<x:set var="href1" select="string(./@path)" />
				<x:set var="labelKey1" select="string(./@label)" />
				<x:set var="right" select="string(./@right)" />
				<mcr:checkAccess var="canDo" permission="${right}" key="" />
			    <c:if test="${canDo}">
					<tr>
						<td style="height:17px;"><img title="" alt=""
							style="width:1px; height:1px"
							src="${WebApplicationBaseURL}images/emtyDot1Pix.gif"></td>
						<th align="left" colspan="4"><a target="_self" href='${href1}'><fmt:message
							key="${labelKey1}" /></a></th>
					</tr>
					<x:if select="contains($pathID,./@systemID)">
						<x:forEach select="./navitem[not(@hidden = 'true')]">
							<x:set var="href2" select="string(./@path)" />
							<x:set var="labelKey2" select="string(./@label)" />
							<x:set var="right" select="string(./@right)" />
							<mcr:checkAccess var="canDo" permission="${right}" key="" />
							<c:if test="${canDo}">
								<x:choose>
									<x:when select="../navitem[last()]/@systemID = ./@systemID">
										<x:set var="end" select="'_end'" />
									</x:when>
									<x:otherwise>
										<x:set var="end" select="''" />
									</x:otherwise>
								</x:choose>
								<c:set var="imghref2"	value="${WebApplicationBaseURL}images/line-with-element${end}.gif" />
								<tr>
									<td style="height:17px;"><img title="" alt=""
										style="width:1px; height:17px;"	src="${WebApplicationBaseURL}images/emtyDot1Pix.gif"></td>
									<td align="center"><img title="" alt="" src="${imghref2}"></td>
									<td colspan="2">
									 <x:choose>
										<x:when select="contains($pathID,./@systemID)">
											<span class="marked"><a target="_self" href="${href2}"><fmt:message	key="${labelKey2}" /></a></span>
										</x:when>
										<x:otherwise>
											<a target="_self" href="${href2}"><fmt:message	key="${labelKey2}" /></a>
										</x:otherwise>
									 </x:choose>
									</td>
								</tr>
							</c:if>
						</x:forEach>
						<tr>
							<td colspan="5" style="height:10px;"><img title="" alt=""
								style="width:1px; height:1px" src="${WebApplicationBaseURL}images/emtyDot1Pix.gif"></td>
						</tr>
					</x:if>
				</c:if>
			</x:forEach>
		</table>
		</div>
		<!-- NAVIGATION LEFT END --></td>
		<td class="max autowidth">
		<table class="max" cellpadding="0" cellspacing="0">
			<tr class="minheight">
				<td id="navi_below_cell"><!-- NAVIGATION TOP START -->
				<table cellpadding="0" cellspacing="0" class="navi_below">
					<tr>
						<x:forEach
							select="$Navigation//navigation[@name='top']/navitem[@name='top']/navitem[not(@hidden = 'true')]">
							<x:set var="href1" select="string(./@path)" />
							<x:set var="labelKey1" select="string(./@label)" />
							<td><a target="_self" href="${href1}"><fmt:message
								key="${labelKey1}" /></a></td>
							<x:choose>
								<x:when select="../navitem[last()]/@systemID != ./@systemID">
									<td><img alt="" style="width:6px; height:1px;"
										src="${WebApplicationBaseURL}images/emtyDot1Pix.gif"></td>
									<td>|</td>
									<td><img alt="" style="width:6px; height:1px;"
										src="${WebApplicationBaseURL}images/emtyDot1Pix.gif"></td>
								</x:when>
							</x:choose>
						</x:forEach>
						<td style="width:10px;"><img alt=""
							style="width:10px; height:1px;"
							src="${WebApplicationBaseURL}images/emtyDot1Pix.gif"></td>
						<!--                                 <td><a href="${WebApplicationBaseURL}nav?path=${path}&lang=${translateLang}"><img style="border-style: none; width: 24px; height: 12px; vertical-align: bottom;" alt="<fmt:message key="secondLanguage" />" src="${WebApplicationBaseURL}images/lang-${translateLang}.gif"></a></td>            -->
						<td><a href="${langfreeRequestURL}&lang=${translateLang}"><img
							style="border-style: none; width: 24px; height: 12px; vertical-align: bottom;"
							alt="<fmt:message key="secondLanguage" />"
							src="${WebApplicationBaseURL}images/lang-${translateLang}.gif"></a></td>
						<td style="width:10px;"><img alt=""
							style="width:10px; height:1px;"
							src="${WebApplicationBaseURL}images/emtyDot1Pix.gif"></td>
					</tr>
				</table>
				<!-- NAVIGATION TOP RIGHT --></td>
			</tr>
			<tr class="minheight">
				<td>
				<table class="navi_history">
					<tr>
						<td class="navi_history"><fmt:message key="Nav.Navigation" />:&nbsp;
						<x:forEach select="$youAreHere//navitem">
							<x:set var="href1" select="string(./@path)" />
							<x:set var="labelKey1" select="string(./@label)" />
							<x:choose>
								<x:when select="../navitem[1]/@systemID != ./@systemID">
                                            &gt; <a href="${href1}"><fmt:message
										key="${labelKey1}" /></a>
								</x:when>
								<x:otherwise>
									<a href="${href1}"><fmt:message key="${labelKey1}" /></a>
								</x:otherwise>
							</x:choose>
						</x:forEach></td>
						<td class="navi_history_user"><fmt:message key="Webpage.user" />
							<mcr:session method="get" var="username" type="userID" />												
						    : <a href="${WebApplicationBaseURL}nav?path=~login">${username}</a></td>
					</tr>
				</table>
				</td>
			</tr>
			<tr>
				<td id="contentArea">
				<div id="contentWrapper"><!-- ************************************************ -->
				<!-- including ${contentPage} --> <!-- the import statement is above for unbroken loading of the page -->
				<!-- ************************************************ --> <c:catch
					var="e">
					<c:out value="${includePage}" escapeXml="false" />
				</c:catch> <c:if test="${e!=null}">
					<%Throwable error = (Throwable) pageContext.getAttribute("e");
				org.apache.log4j.Logger.getLogger("frame.jsp").error("error",
						error);

				%>
					<c:import url="${WebApplicationBaseURL}mycore-error.jsp">
						<c:param name="message">${e.class} ${e.message} $e.localisedMessage} hh</c:param>
					</c:import>
					<textarea cols="100" rows="25">
                            <%error.printStackTrace(new java.io.PrintWriter(out));

			%>
                        </textarea>
				</c:if></div>
				</td>
			</tr>
			<tr class="minheight">
				<td id="footer">Autor: Administrator, <%java.util.Calendar cal = new java.util.GregorianCalendar(
					java.util.TimeZone.getTimeZone("ECT"));
			java.text.DateFormat formater;
			formater = java.text.DateFormat.getDateTimeInstance(
					java.text.DateFormat.FULL, java.text.DateFormat.MEDIUM);
			out.print(formater.format(cal.getTime()));

		%></td>
			</tr>
		</table>
		</td>
	</tr>
</table>
</body>
</html>
