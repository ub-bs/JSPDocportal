<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld" %>
<%@ taglib prefix="mcrb" uri="http://www.mycore.org/jspdocportal/browsing.tld" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>


<fmt:message var="pageTitle" key="Webpage.browse.title.${param.browserClass}" /> 
<stripes:layout-render name="../WEB-INF/layout/default.jsp" pageTitle = "${pageTitle}">
	<stripes:layout-component name="html_header">
		<title><fmt:message key="Nav.ClassificationsSearch" /> @ <fmt:message key="Webpage.title" /></title>
		<link type="text/css" rel="stylesheet" href="${WebApplicationBaseURL}css/style_classification-browser.css">
	</stripes:layout-component>

	<stripes:layout-component name="contents">
		<h2><fmt:message key="Webpage.browse.generalTitle" /></h2>
		<fmt:message key="Webpage.browse.subitem" /><br /><br />

		<c:if test="${param.browserClass eq 'ddc_sub'}">
			<mcr:getConfigProperty var="clf" prop="MCR.ClassificationID.DDC" defaultValue="DocPortal_class_00000009"/>
			<mcrb:classificationBrowser 
				classification="${clf}" 
				count="false" hideemptyleaves="false" 
				level="-1" expand="false" 
				linkall="true" 
				showdescription="true" showuri="false" showid="false" />
		</c:if>

		<c:if test="${param.browserClass eq 'bkl_sub'}">
			<mcr:getConfigProperty var="clf" prop="MCR.ClassificationID.Bkl" defaultValue="DocPortal_class_00000020"/>
			<mcrb:classificationBrowser
				classification="${clf}"
				count="true" hideemptyleaves="false" 
				level="1" expand="false" 
				searchfield="bkl" searchmask="~searchstart-classbkl" 
				showdescription="true" showuri="false" showid="false" />
		</c:if>		
	</stripes:layout-component>
</stripes:layout-render> 



