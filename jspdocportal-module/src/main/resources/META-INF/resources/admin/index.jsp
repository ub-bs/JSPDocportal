<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ page import="org.mycore.user.MCRUserMgr,
    org.mycore.user.MCRUser,
    org.mycore.user.MCRGroup,
    org.mycore.common.MCRSession,
    org.mycore.frontend.servlets.MCRServlet"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>

<c:set var="pageTitle" value="Administration Interface" /> 
<stripes:layout-render name="../WEB-INF/layout/default.jsp" pageTitle = "${pageTitle}">
   	<stripes:layout-component name="html_head">
        <script src="${WebApplicationBaseURL}admin/admin.js" type=text/javascript></script>
        <link type="text/css" rel="stylesheet" href="${WebApplicationBaseURL}admin/css/admin.css" />
    </stripes:layout-component>
    <stripes:layout-component name="contents">
		<mcr:session method="get" var="username" type="userID" />
		<c:set var="pageurl" value="${requestScope.page}" />
		<mcr:hasAccess var="hasAccess" permission="admininterface-access" />
		<c:if test="${!hasAccess}">
			<c:set var="pageurl" value="error.jsp" />
   		</c:if>
   		<c:choose>
       <c:when test="${pageurl eq 'rules_editor_jsp'}">
          <c:import url="${pageurl}"/>
       </c:when>
       <c:otherwise>
        <table cellpadding="0" cellspacing="0" id="mytable" border="0">
            <tr valign="bottom"  >
                <td valign="bottom" align="right"  style="padding:10px" class="adminheadline" >
                    <fmt:message key="Webpage.intro.admin.Text2" />
                </td>
            </tr>
            <tr>
                <td id="mainLeftColumn" >
                    <br />
                   
                </td>

                <td valign="top">
                    <table cellpadding="0" cellspacing="0" id="maintable">
                        <tr>
                            <td valign="top">
                                <table width="100%" height="100%" cellpadding="0" cellspacing="0">
                                    <tr>
                                        <td valign="top" style="padding:5px">
                                            <c:import url="${pageurl}"/>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
            <tr>
                <td colspan="2" valign="bottom"><div id="footer"><small>User: ${username}&#160;&#160;&#160;ver. 0.3</small></div></td>
            </tr>
        </table>       
       </c:otherwise>
    </c:choose>
	</stripes:layout-component>
</stripes:layout-render>