<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="mcr"
	uri="http://www.mycore.org/jspdocportal/base.tld"%>
<%@ taglib prefix="stripes"
	uri="http://stripes.sourceforge.net/stripes.tld"%>
<%@ taglib prefix="search" tagdir="/WEB-INF/tags/search"%>

<fmt:message var="pageTitle" key="Webpage.error.title" />
<stripes:layout-render name="../WEB-INF/layout/default.jsp"
	pageTitle="${pageTitle}" layout="2columns">
	<stripes:layout-component name="html_header">

	</stripes:layout-component>
	<stripes:layout-component name="main_part">
		<div class="ir-box">
			<div class="row">
				<div class="col-xs-12">
					<h2><c:out value="${pageTitle}" /></h2>
					<c:if test="${not empty actionBean.errorInfo.message or not empty actionBean.errorInfo.exception}">
						<fmt:message var="showDetails" key="Webpage.error.button.details" />
						<button type="button" class="btn btn-default btn-sm pull-right hidden-sm hidden-xs" style="border:none;color:#DEDEDE;" data-toggle="collapse" data-target="#errorInfoPanel" title="${showDetails}">
	    					<span class="glyphicon glyphicon-wrench"></span>
	        			</button>
	        		</c:if>
					<c:if test="${not empty actionBean.errorInfo.headline}">
						<h4>${actionBean.errorInfo.headline}</h4>
					</c:if>
				</div>
			</div>
			<div class="row">
				<c:if test="${not empty actionBean.errorInfo.message or not empty actionBean.errorInfo.exception}">
					<div id="errorInfoPanel" class="collapse col-xs-12 col-md-11 col-md-offset-1">
						<div class="panel panel-danger">
							<c:if test="${not empty actionBean.errorInfo.message}">
								<div class="panel-heading"><c:out value="${actionBean.errorInfo.message}" /></div>
							</c:if>
							<c:if test="${not empty actionBean.errorInfo.exception}">
								<div class="panel-body">
									<pre><code><strong><c:out value="${actionBean.errorInfo.exception.localizedMessage}" /></strong></code></pre>
									<pre><samp style="white-space:normal;">
										<c:forEach var="ste" items="${actionBean.errorInfo.exception.stackTrace}">
											<c:out value="${ste}" />
											<br />
										</c:forEach>
									</samp></pre>
								</div>
							</c:if>
						</div>
					</div>
				</c:if>
			</div>
		</div>
	</stripes:layout-component>
</stripes:layout-render>
