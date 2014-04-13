<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>
<%@ page import="org.mycore.frontend.servlets.MCRServlet" %>

<stripes:layout-render name="../WEB-INF/layout/default.jsp" pageTitle = "${pageTitle}">
	<stripes:layout-component name="contents">
		<h1>Zugriffsverletzung</h1>
			<br />
			<br />
			<br />
			<p class="error">Sie besitzen nicht die notwendige Berechtigung, um den Administrationsbereich zu nutzen.<br />
				Bitte wenden Sie sich an Ihren Administrator, falls Sie weitere Fragen haben</p>

			<p>&nbsp;</p><p>&nbsp;</p>
			<p align="center"><a href="<%= MCRServlet.getBaseURL() %>">Sie müssen Sich erst in der Digitalen Bibliothek anmelden</a>
	</stripes:layout-component>
</stripes:layout-render>    

