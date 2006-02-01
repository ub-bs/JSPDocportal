<%@ page import="org.mycore.user.MCRUserMgr,
    org.mycore.user.MCRUser,
    org.mycore.user.MCRGroup,
    org.mycore.common.MCRSession,
    org.mycore.frontend.servlets.MCRServlet"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ page import="org.mycore.user.MCRGroup" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages' />

<%
    MCRSession mcrSession = MCRServlet.getSession(request);
    MCRUser user = MCRUserMgr.instance().retrieveUser(mcrSession.getCurrentUserID());
    String WebApplicationBaseURL = MCRServlet.getBaseURL();
    String pageurl = (String) request.getAttribute("page");

    if(! user.isMemberOf(new MCRGroup("admingroup"))){
        pageurl="error.jsp";
    }

%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html>
    <head>
        <meta http-equiv="content-type" content="text/html; charset=UTF-8" />
        <title>Administration Interface</title>
        <script src="${WebApplicationBaseURL}admin/admin.js" type=text/javascript></script>
        <link type="text/css" rel="stylesheet" href="${WebApplicationBaseURL}admin/css/admin.css" />
    </head>
    <body>
<%
    if (((String) request.getAttribute("page")).equals("rules_editor.jsp")){
%>
        <jsp:include page="<%=pageurl%>"/>

<%  // normal page
    }else{
%>
        <table cellpadding="0" cellspacing="0" id="mytable" border="0">
            <tr valign="bottom"  >
                <td class="adminheadline">
                    <img src="images/white20px.gif" /><img src="${WebApplicationBaseURL}images/logo_atlibri.gif" alt="@libri" />
                </td>
                <td valign="bottom" align="right"  style="padding:10px" class="adminheadline" >
                    <fmt:message key="Admin.Text2" />
                </td>
            </tr>
            <tr>
                <td id="mainLeftColumn" >
                    <br />
                    <jsp:include page='navigation.jsp'/>
                </td>

                <td valign="top">
                    <table cellpadding="0" cellspacing="0" id="maintable">
                        <tr>
                            <td valign="top">
                                <table width="100%" height="100%" cellpadding="0" cellspacing="0">
                                    <tr>
                                        <td valign="top" style="padding:5px">
                                            <jsp:include page="<%=pageurl%>"/>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
            <tr>
                <td colspan="2" valign="bottom"><div id="footer"><small>User: <%=mcrSession.getCurrentUserID()%>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ver. 0.3</small></div></td>
            </tr>
        </table>
<%
    }
%>
    </body>
</html>
