<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr"%>
<%@ taglib prefix="mcrdd" uri="http://www.mycore.de/jspdocportal/docdetails" %>
<stripes:layout-definition>
	<%-- parameters: pageTitle, currentPath  --%>
	<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
	
	<mcrdd:setnamespace prefix="nav" uri="http://www.mycore.org/jspdocportal/navigation" />
	<c:set var="WebApplicationBaseURL"	value="${applicationScope.WebApplicationBaseURL}" />
	<c:set var="path" value="${requestScope.path}" />
	<%-- set the current language --%>
	<c:choose>
		<c:when test="${!empty(param.lang)}">
			<c:set var="lang" value="${param.lang}" />
			<c:if test="${!fn:contains('de,en',lang)}">
				<c:set var="lang" value="de" />
			</c:if>
			<mcr:session method="set" type="language" var="lang" />
		</c:when>
		<c:otherwise>
			<mcr:session method="get" type="language" var="lang" />
		</c:otherwise>
	</c:choose>

	<html>
		<head>
			<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
			<title>
				<c:if test="${empty (pageTitle)}">
					<fmt:message var="pageTitle" key="Title.${path}" />
					<c:choose>
						<c:when test="${not empty param.id}">
							<c:set var="docType" value="${fn:substringBefore(fn:substringAfter(param.id, '_'),'_')}" />
							<c:if test="${not empty docType}">
								<jsp:include page="content/webpageitems/webpageitem-${docType}.jsp" >
									<jsp:param name="pageFragment" value="pagetitle" />
									<jsp:param name="mcrid" value="${param.id}" />
								</jsp:include>
							</c:if>
						</c:when>
						<c:otherwise>
							<c:out value="${pageTitle}" />
						</c:otherwise>
					</c:choose>
					@ <fmt:message key="Webpage.intro.title" />
				</c:if>
				<c:if test="${not empty (pageTitle)}">
					${pageTitle} @ <fmt:message key="Webpage.intro.title" />
				</c:if>
			</title>
			<script src="${WebApplicationBaseURL}javascript/jspdocportal.js"	type="text/javascript"></script>
			<link type="text/css" rel="stylesheet" href="${WebApplicationBaseURL}css/style_general.css">
			<link type="text/css" rel="stylesheet" href="${WebApplicationBaseURL}css/style_navigation.css">
			<link type="text/css" rel="stylesheet" href="${WebApplicationBaseURL}css/style_content.css">
			<link type="text/css" rel="stylesheet" href="${WebApplicationBaseURL}css/style_docdetails.css">
			<link rel="shortcut icon" href="${WebApplicationBaseURL}images/icon_rosdok.ico">
			<stripes:layout-component name="html_head"/>
		</head>
		<body topmargin="0" rightmargin="0" leftmargin="0">
			<table id="maintable" cellpadding="0" cellspacing="0">
				<tr valign="top" >
					<td rowspan="7" width="5">
		  				<span style="padding-left:5px">&nbsp;</span>
					</td>
					<td id="navi_line" colspan="2">
		 				<table class="navi_line" cellspacing="0" border="0" cellpadding="0" width="100%" >
		 					<tr>
		  						<td align="center" >
								<!-- NAVIGATION TOP RIGHT -->
									<mcr:outputNavigation id="top" currentPath="top" mode="top" separatorString="|"/>
									<span style="padding-left:10px;padding-right:10px">
										<mcr:outputLanguageSelector languages="de,en" separatorString="&nbsp;&nbsp;|&nbsp;&nbsp;" />
									</span>		    
								</td>
							</tr>
						</table>
	  				</td>
				</tr>
				<tr>
					<td width="126" valign="bottom">
		  				<a href="${WebApplicationBaseURL}">
		   					<img id="logo" alt="Logo RosDok" src="${WebApplicationBaseURL}images/logo_rosdok.gif"  />
		   				</a>
					</td>
					<td>
						<table cellpadding="0" cellspacing="0" width="100%">
							<tr>
								<td valign="bottom">
									<!--Schriftzug RosDok--> 
									<img src="${WebApplicationBaseURL}/images/logo_rosdok_lang.gif" border="0" alt="Rostocker Dokumentenserver" />
								</td>
								<td valign="bottom" align="right">
									<img src="${WebApplicationBaseURL}/images/ub_pictures.jpg" border="0" alt="" />
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td colspan="2" >
		 				<table class="navi_below_line" cellspacing="0" cellpadding="0">
		 					<tr>
		  						<td style="width:212px" class="navi_history_user" >
				   					<mcr:session method="get" var="username" type="userID" />
				   					<fmt:message key="Webpage.user" />: 
   				   					<a href="${WebApplicationBaseURL}nav?path=~userdetail">${username}</a>
								</td>		  
		  						<td class="navi_history" >
			    					<!-- Navigation history -->
 		        					<fmt:message key="Nav.Navigation" />:&nbsp;
 		        					<mcr:outputNavigation id="left" currentPath="${currentPath}" mode="breadcrumbs" separatorString="&gt;&gt;"/>				
		   						</td>
		  					</tr>
						</table>
					</td>
				</tr>
				<tr class="max">
					<td id="mainLeftColumn" class="navi_table" >
						<!-- NAVIGATION LEFT BEGIN -->
						<div  class="navi_left"  >
							<table class="navi_left" cellpadding="0" cellspacing="0">
							<!--Main Menu -->
								<tr>
									<td>
										<mcr:outputNavigation id="left" expanded="false" currentPath="${currentPath}" mode="left"/>
									</td>
								</tr>
								<!--Services Tips -->
								<tr>
									<td>
										<mcr:outputNavigation id="tips" expanded="false" currentPath="${currentPath}" mode="left"/>
									</td>				
								</tr>
								<tr>
									<td>
										<mcr:outputNavigation id="publish" expanded="false" currentPath="${currentPath}" mode="left"/>
									</td>				
								</tr>
								<!-- Admin Menu -->
								<tr>
									<td>
										<mcr:outputNavigation id="admin" expanded="false" currentPath="${currentPath}" mode="left"/>
									</td>				
								</tr>
							</table>
						</div>
					</td>
					<!-- NAVIGATION LEFT END -->

					<td class="max autowidth">
						<table class="max" cellpadding="0" cellspacing="0">
							<tr>
								<td id="contentArea">
									<!--  MAIN CONTENT -->
									<div id="contentWrapper">									 
									<c:catch var="e">
					 					<stripes:layout-component name="contents">
	                						<div>This is the main page ...</div>
    	        						</stripes:layout-component>  
									</c:catch>
				 					<c:if test="${e!=null}">
										<%Throwable error = (Throwable) pageContext.getAttribute("e");
										  org.apache.log4j.Logger.getLogger("frame.jsp").error("error", error); %>
										<c:import url="${WebApplicationBaseURL}mycore-error.jsp">
											<c:param name="message">${e.class} ${e.message} $e.localisedMessage} hh</c:param>
										</c:import>
											<textarea cols="100" rows="25">
                            					<%error.printStackTrace(new java.io.PrintWriter(out)); %>
                        					</textarea>
									</c:if>
									</div>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
	  				<td id="footer" colspan="2" >
	        			<a href="${WebApplicationBaseURL}nav?path=~impressum">Impressum</a>
	  				</td>
				</tr>	
			</table>
		</body>
	</html>
</stripes:layout-definition>