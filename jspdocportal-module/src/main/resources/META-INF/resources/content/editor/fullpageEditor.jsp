<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8"%>
<%@ page import="org.mycore.frontend.servlets.MCRServlet"%>
<%@ page import="org.mycore.common.MCRSessionMgr"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>


<%--Parameter: objectType --%>

<fmt:message var="pageTitle" key="WF.editobject" /> 
<stripes:layout-render name="../../WEB-INF/layout/default.jsp" pageTitle = "${pageTitle}" layout="1column">
<stripes:layout-component name="html_header">
	<link type="text/css" rel="stylesheet" href="${WebApplicationBaseURL}css/style_workspace.css" />
</stripes:layout-component>
	<stripes:layout-component name="contents">
		<mcr:includeXEditor editorPath="${actionBean.editorPath}" cancelURL="${actionBean.cancelURL}" sourceURI="${actionBean.sourceURI}" />
	</stripes:layout-component>
</stripes:layout-render>
