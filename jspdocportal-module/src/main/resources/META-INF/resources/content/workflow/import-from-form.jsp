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
 --%>

<c:set var="WebApplicationBaseURL"
	value="${applicationScope.WebApplicationBaseURL}" />

<stripes:useActionBean beanclass="org.mycore.frontend.jsp.stripes.actions.ImportFromFormAction" var="actionBean"/>
 
<mcr:checkAccess var="isAllowed" permission="writedb" key="${actionBean.mcrid}" />
<c:if test="${isAllowed}">
	<stripes:layout-render name="../../WEB-INF/layout/default.jsp"
		pageTitle="Daten / Dateienimport aus Goobi">
		<stripes:layout-component name="html_header">

		</stripes:layout-component>

		<stripes:layout-component name="contents">
			<h2>Daten / Dateien aus einem Abgabeformular importieren</h2>
			<stripes:form
				beanclass="org.mycore.frontend.jsp.stripes.actions.ImportFromFormAction"
				id="importMODS" enctype="multipart/form-data" acceptcharset="UTF-8">
				<div class="stripesinfo">
					<stripes:errors />
					<stripes:messages />
				</div>
				<stripes:hidden name="returnPath" />
				<stripes:hidden name="processid" />
				<stripes:hidden name="listOfMetadataVersions" />
				<%-- load first time from request parameter "returnPath --%>

				<table>
					<col width="200px" />
					<col width="500px" />
					<col width="200px" />
					<tr>
						<td><stripes:label for="mcrid">MyCoRe ID:</stripes:label></td>
						<td><stripes:text style="width:100%" id="mcrid" name="mcrid" readonly="true" /></td>
					</tr>
					<tr>
						<td><stripes:label for="folderName">Abgabe-Ordner:</stripes:label></td>
						<td><stripes:text style="width:100%" id="folderName" name="folderName" /></td>
						<td>
							<stripes:submit name="doRetrieveMetadataVersions" value="Metadaten-Versionen neu einlesen" class="submit" />		
						</td>
					</tr>
					<tr>
						<td><stripes:label for="folderName">Metadaten-Versionen:</stripes:label></td>
						<td>
							<c:forEach var="item" items="${actionBean.listOfMetadataVersions}">
								<stripes:radio value="${item}" name="metadataVersion" />&nbsp;${item}<br />
							</c:forEach>
						</td>
						<td>
							<c:if test="${not empty actionBean.listOfMetadataVersions}">
								<stripes:submit name="doRetrieveMetadataContent" value="Metadaten-Inhalt neu einlesen" class="submit" />
							</c:if>		
						</td>
					</tr>
					<tr>
						<td colspan="3" style="vertical-align: top;"><stripes:label for="output">Daten zum Import:</stripes:label></td>
					</tr>
					<tr>						
						<td colspan="3"><stripes:textarea rows="30" style="scroll:auto;width:100%" id="metadataContent" name="metadataContent" readonly="readonly"/></td>
					</tr>
					<tr>
						<td></td>
						<td colspan="2">
							<c:if test="${not empty actionBean.metadataContent}">
								<stripes:submit name="doSave" value="Daten übernehmen" class="submit" />
							</c:if>
							<stripes:submit name="doCancel" value="Abbrechen" class="submit" />
						</td>
						
					</tr>
				</table>
				
			</stripes:form>
		</stripes:layout-component>
	</stripes:layout-render>
</c:if>