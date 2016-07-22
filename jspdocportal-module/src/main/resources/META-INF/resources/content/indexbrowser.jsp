<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="errorpage.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld"%>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>
<%@ taglib prefix="search" tagdir="/WEB-INF/tags/search"%>

<fmt:message var="pageTitle"
	key="Webpage.indexbrowser.${actionBean.modus}.title" />
<stripes:layout-render name="../WEB-INF/layout/default.jsp"
	pageTitle="${pageTitle}" layout="3columns">
	<stripes:layout-component name="html_header">
	<style>
		.indexbrowser-btn {
			margin-top:0.40em;
			padding-left:0.40em;
			padding-right:0.40em;
		}	
	</style>

	</stripes:layout-component>
	<stripes:layout-component name="contents">
		<div class="ur-box ur-text">
			   <h2>
				   <fmt:message key="Webpage.indexbrowser.${actionBean.modus}.title" />
			   </h2>
			   <p>
				   <fmt:message key="Webpage.indexbrowser.${actionBean.modus}.intro" />
			   </p>
			<div class="row">
				<div class="col-xs-12">
					<div class="navbar navbar-default" style="padding:10px">
						<div>
							<c:forEach var="x" items="${actionBean.firstSelector}">
								<c:set var="active"></c:set>
								<c:if test="${fn:startsWith(actionBean.select, x)}"><c:set var="active">active</c:set></c:if>
									<a href="${WebApplicationBaseURL}indexbrowser.action?modus=${actionBean.modus}&amp;select=${x}"
									   class="btn btn-default btn-sm navbar-btn indexbrowser-btn ${active}" role="button">${x}</a>
							</c:forEach>
						</div>
						<c:if test="${not empty actionBean.secondSelector}">
							<div>
								<c:forEach var="x" items="${actionBean.secondSelector}">
									<c:set var="active"></c:set>
									<c:if test="${fn:startsWith(actionBean.select, x.key)}"><c:set var="active">active</c:set></c:if>
										<a href="${WebApplicationBaseURL}indexbrowser.action?modus=${actionBean.modus}&amp;select=${x.key}"
										   class="btn btn-default btn-sm indexbrowser-btn ${active}" role="button">${x.key} <span class="badge" style="font-size:80%;margin-left:8px">${x.value}</span></a>
								</c:forEach>
							</div>
						</c:if>
					</div>
				</div>
			</div>
			
			<div class="row">
				<div class="col-xs-12">
					<div class="panel panel-default">
						<div class="panel-body">
							<stripes:form beanclass="org.mycore.frontend.jsp.stripes.actions.IndexBrowserAction"
					   	  		          id="indexbrowserForm" enctype="multipart/form-data" acceptcharset="UTF-8" class="form-inline">
								<stripes:hidden name="modus">${actionBean.modus}</stripes:hidden>
								<stripes:label for="txtSelect"><fmt:message key="Webpage.indexbrowser.form.label" />:</stripes:label>&#160;&#160;&#160;&#160;&#160;
								<stripes:text class="form-control input-sm" id="txtSelect" name="select" />
								<fmt:message var="output" key="Webpage.indexbrowser.form.button" />
								<stripes:submit name="doSearch" value="${output}" class="btn btn-sm btn-primary" />
							</stripes:form>
						</div>
					</div>
				</div>
			</div>
				
			<c:forEach var="r" items="${actionBean.result.entries}">
			<div class="row">
				<div class="col-xs-12">
					<div class="panel panel-default ir-resultentry-panel">
						<div class="panel-body">
							<search:result-entry data="${r}" url="${WebApplicationBaseURL}resolve/id/${r.mcrid}?_search=${actionBean.result.id}" protectDownload="true" />
						</div>
					</div>
				</div>
			</div>
			</c:forEach>
		</div>
	</stripes:layout-component>
</stripes:layout-render>
