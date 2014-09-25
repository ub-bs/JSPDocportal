<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="errorpage.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld"%>
<%@ taglib prefix="mcrb" uri="http://www.mycore.org/jspdocportal/browsing.tld"%>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>

<fmt:message var="pageTitle"
	key="Webpage.indexbrowser.${actionBean.modus}.title" />
<stripes:layout-render name="../WEB-INF/layout/default.jsp"
	pageTitle="${pageTitle}" layout="3columns">
	<stripes:layout-component name="html_header">
	
	</stripes:layout-component>
	<stripes:layout-component name="contents">
		<div class="ur-box ur-text">
			<h1>
				<fmt:message key="Webpage.indexbrowser.${actionBean.modus}.title" />
			</h1>
			<fmt:message key="Webpage.indexbrowser.${actionBean.modus}.intro" />
			<div class="container">
				<div class="row">
					<div class="col-xs-12">
						<c:forEach var="x" items="${actionBean.firstSelector}">
							<c:set var="active"></c:set>
							<c:if test="${fn:startsWith(actionBean.select, x)}"><c:set var="active">active</c:set></c:if>
							<a href="${WebApplicationBaseURL}indexbrowser.action?modus=${actionBean.modus}&amp;select=${x}"
								class="btn btn-default btn-sm ${active}" style="padding:0.4em 0.6em" role="button">${x}</a>
						</c:forEach>
					</div>
				</div>
				<c:if test="${not empty actionBean.secondSelector}">
					<div class="row">
						<div class="col-xs-12">
							<c:forEach var="x" items="${actionBean.secondSelector}">
							<c:set var="active"></c:set>
							<c:if test="${fn:startsWith(actionBean.select, x.key)}"><c:set var="active">active</c:set></c:if>
								<a href="${WebApplicationBaseURL}indexbrowser.action?modus=${actionBean.modus}&amp;select=${x.key}"
									class="btn btn-default btn-sm ${active}" style="padding:0.4em 0.6em"  role="button">${x.key} <span class="badge" style="font-size:80%;margin-left:8px">${x.value}</span></a>
							</c:forEach>
					</div>
				</div>
				</c:if>
				<stripes:form beanclass="org.mycore.frontend.jsp.stripes.actions.IndexBrowserAction"
					id="indexbrowserForm" enctype="multipart/form-data" acceptcharset="UTF-8">
					<stripes:hidden name="modus">${actionBean.modus}</stripes:hidden>
					<div>
						<stripes:label for="txtSelect"><fmt:message key="Webpage.indexbrowser.form.label" />:</stripes:label>
						<stripes:text id="txtSelect" name="select" />
						<fmt:message var="output" key="Webpage.indexbrowser.form.button" />
						<stripes:submit name="doSearch" value="${output}" class="submit" />
					</div>
				</stripes:form>
				
				<c:forEach var="r" items="${actionBean.results}">
				<div class="row">
					<div class="col-xs_12">
						<b><a href="${WebApplicationBaseURL}resolve/id/${r.mcrid}">${r.label}</a></b>
						<table style="font-size:90%">
							<c:forEach var="d" items="${r.data}">
								<tr><th><fmt:message key="Webpage.indexbrowser.${actionBean.modus}.label.${d.key}" />:&#160;</th>
								<c:choose>
									<c:when test="${fn:endsWith(d.key, '_msg')}"><td><fmt:message key="${d.value}" /></td></c:when>
									<c:when test="${fn:endsWith(d.key, '_class')}"><td><mcr:displayClassificationCategory classid="${fn:substringBefore(d.value,':')}" categid="${fn:substringAfter(d.value,':')}"  lang="de" /></td></c:when>
									<c:otherwise>
										<td>${d.value}</td>
									</c:otherwise>
								</c:choose>
								</tr>
							</c:forEach>
						</table>
					</div>
				</div>
				</c:forEach>
			</div>
		</div>
	</stripes:layout-component>
</stripes:layout-render>
