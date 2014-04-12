<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld"%>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>

<%@ page import="org.apache.log4j.Logger"%>
<%@page import="org.hibernate.Transaction"%>
<%@page import="org.mycore.backend.hibernate.MCRHIBConnection"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>

<%--Parameter: mcrid, returnPath --%>
<%--<fmt:setLocale value='${requestScope.lang}'/>
<fmt:setBundle basename='messages'/> --%>

<c:set var="WebApplicationBaseURL"
	value="${applicationScope.WebApplicationBaseURL}" />

<stripes:useActionBean beanclass="org.mycore.frontend.jsp.stripes.actions.ImportFromGoobiAction" var="actionBean"/>
<c:set var="mcrid" value="${param.mcrid}" />
<c:if test="${empty mcrid }">
	<c:set var="mcrid" value="${actionBean.mcrID}" />
</c:if>
 
<mcr:checkAccess var="isAllowed" permission="writedb" key="${mcrid}" />
<c:set var="isAllowed" value="true" />
<c:out value="${mcrid}: ${isAllowed}" />
<c:if test="${isAllowed}">
	<stripes:layout-render name="../../WEB-INF/layout/default.jsp"
		pageTitle="Daten / Dateienimport aus Goobi">
		<stripes:layout-component name="html_header">

		</stripes:layout-component>

		<stripes:layout-component name="contents">
			<h2>Daten / Dateien aus Goobi importieren</h2>
			<stripes:form
				beanclass="org.mycore.frontend.jsp.stripes.actions.ImportFromGoobiAction"
				id="importMODS" enctype="multipart/form-data" acceptcharset="UTF-8">
				<div class="stripesinfo">
					<stripes:errors />
					<stripes:messages />
				</div>
				<stripes:hidden name="returnPath" />
				<stripes:hidden name="processid" />
				<%-- load first time from request parameter "returnPath --%>

				<table>
					<col width="100px" />
					<col width="700px" />
					<tr>
						<td><stripes:label for="mcrID">MyCoRe ID:</stripes:label></td>
						<td><stripes:text style="width:100%" id="mcrID" name="mcrID" /></td>
					</tr>
					<tr>
						<td><stripes:label for="goobiFolderID">Goobi Folder ID:</stripes:label></td>
						<td><stripes:text style="width:100%" id="goobiFolderID" name="goobiFolderID" /></td>
					</tr>
					<tr>
						<td colspan="2">
							<stripes:submit name="doRetrieve" value="Daten neu auslesen" class="submit" />		
						</td>
					</tr>
					<tr>
						<td style="vertical-align: top;"><stripes:label for="output">Output:</stripes:label></td>
						<td><stripes:textarea rows="32" style="scroll:auto;width:100%" id="output" name="output" /></td>
					</tr>
					<tr>
						<td></td>
						<td>
							<stripes:submit name="doSave" value="Speichern" class="submit" />
							<stripes:submit name="doCancel" value="Abbrechen" class="submit" />
						</td>
					</tr>
				</table>
				
			</stripes:form>
		</stripes:layout-component>
	</stripes:layout-render>
</c:if>