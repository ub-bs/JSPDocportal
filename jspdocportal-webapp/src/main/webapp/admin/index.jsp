<%@ page import="org.mycore.user.MCRUserMgr,
    org.mycore.user.MCRUser,
    org.mycore.user.MCRGroup,
    org.mycore.common.MCRSession,
    org.mycore.frontend.servlets.MCRServlet"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>

<mcr:session method="get" var="username" type="userID" />
<c:set var="pageurl" value="${requestScope.page}" />
<mcr:checkAccess var="hasAccess" permission="admininterface-access" />
<c:if test="${!hasAccess}">
   <c:set var="pageurl" value="error.jsp" />
</c:if>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html>
    <head>
        <meta http-equiv="content-type" content="text/html; charset=UTF-8" />
        <title>Administration Interface</title>
        <script src="${WebApplicationBaseURL}admin/admin.js" type=text/javascript></script>
        <link type="text/css" rel="stylesheet" href="${WebApplicationBaseURL}admin/css/admin.css" />
    </head>
    <body>
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
                <td colspan="2" valign="bottom"><div id="footer"><small>User: ${username}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ver. 0.3</small></div></td>
            </tr>
        </table>       
       </c:otherwise>
    </c:choose>
    </body>
</html>
