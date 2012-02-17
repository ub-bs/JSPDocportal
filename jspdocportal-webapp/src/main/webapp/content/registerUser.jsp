<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"   %>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>

<fmt:message var="pageTitle" key="Nav.Account" /> 
<stripes:layout-render name="../WEB-INF/layout/default.jsp" pageTitle = "${pageTitle}">
	<stripes:layout-component name="contents">
	
<h2><fmt:message key="Nav.Account" /> </h2>


<mcr:includeWebContent file="registeruser.html" />	
<c:import url="${applicationScope.WebApplicationBaseURL}editor/workflow/editor-registeruser.xml?XSL.editor.source.new=true&XSL.editor.cancel.url=${applicationScope.WebApplicationBaseURL}nav?path=${requestScope.path}&usecase=register-user&page=nav?path=~registered" />    
	</stripes:layout-component>
</stripes:layout-render>    