<%@ page import="org.mycore.common.MCRSession,
                 org.mycore.common.MCRSessionMgr,
                 org.mycore.common.MCRConfiguration,
                 org.mycore.frontend.servlets.MCRServlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn" %>
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages'/>                 
<%
    MCRSession mcrSession = MCRServlet.getSession(request);
    String username = mcrSession.getCurrentUserID();
    if(username == null)
        username = MCRConfiguration.instance().getString("MCR.users_guestuser_username");

%>
<div class="headline"><fmt:message key="Login.ChangeUserID" /></div>
    <table id="metaHeading" cellpadding="0" cellspacing="0">
        <tbody>
            <tr>
                <td class="titles"><fmt:message key="Login.YouAreLoggedInAs" />:&nbsp;&nbsp;[<%= username %>]</td>
            </tr>
        </tbody>
    </table>
    <hr>
    <div id="userStatus"></div>
        <form method="post" action="nav?path=~login">
            <input name="url" value="<c:out value="${requestScope.WebApplicationBaseURL}" />" type="hidden">
            <table id="userAction">
                <tbody>
                    <tr>
                        <td class="inputCaption"><fmt:message key="Login.UserLogin" />:</td>
                        <td class="inputField"><input maxlength="30" class="text" name="uid" type="text"></td>
                    </tr>
                    <tr>
                        <td class="inputCaption"><fmt:message key="Login.Password" />:</td>
                        <td class="inputField"><input maxlength="30" class="text" name="pwd" type="password"></td>
                    </tr>
                </tbody>
            </table>
            <hr>
            <div class="submitButton">
                &nbsp;
                <input name="LoginSubmit" value="<fmt:message key="Login.Login" />" class="submitbutton" type="submit">
                &nbsp;
                <input name="LoginReset" value="<fmt:message key="Cancel" />" class="submitbutton" type="reset">
            </div>
        </form>
    </div>
</div>