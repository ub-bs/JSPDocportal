<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c"       uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="x"       uri="http://java.sun.com/jsp/jstl/xml"%>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn"      uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="mcr" 	uri="http://www.mycore.org/jspdocportal/base.tld"%>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>
	
<%@ taglib prefix="search" tagdir="/WEB-INF/tags/search"%>

<fmt:message var="pageTitle" key="Webpage.browse.title.${actionBean.result.mask}" />
<stripes:layout-render name="/WEB-INF/layout/default.jsp" pageTitle="${pageTitle}" layout="1column">
	<stripes:layout-component name="html_header">
	</stripes:layout-component>
	<stripes:layout-component name="contents">
		<h2>${pageTitle}</h2>
		<div class="ur-box">
			<search:result-browser result="${actionBean.result}">
				<c:set var="doctype" value="${fn:substringBefore(fn:substringAfter(mcrid, '_'),'_')}" />
				<search:show-edit-button mcrid="${mcrid}" />
				<c:choose>
					<c:when test="${doctype eq 'disshab'}">
					   <search:result-entry-disshab data="${entry}" url="${url}" />
					</c:when>
					<c:otherwise>
						<search:result-entry data="${entry}" url="${url}" />
					</c:otherwise>
				</c:choose>
				<div style="clear:both"></div>
			</search:result-browser>
		</div>
	</stripes:layout-component>
</stripes:layout-render>
