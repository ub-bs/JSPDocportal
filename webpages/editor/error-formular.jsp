<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Iterator" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn" %>
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages'/>
<div class="headline"><fmt:message key="Editor.CheckFormData" /></div>
    <p></p>
    <br />
    <p></p>
    <center>
    <table width="80%">
        <%
            ArrayList errorList = (ArrayList)request.getAttribute("logtext");
            for(Iterator it = errorList.iterator(); it.hasNext();) {
                String error = (String)it.next();
                %>
                <tr><td><font color="red"><%= error %></font></td></tr>
                <%
            }
        %>
    </table>
    <table width="80%">
        <tr align="center">
            <td align="center">
                <font color="red"><fmt:message key="Editor.YouErrorMessageIsAbove" /></font>
            </td>
        </tr>
    </table>
    </center>
    <p></p>
    <br />
    <p></p>
    <form action="${requestScope.WebApplicationBaseURL}nav" method="post">
    	<input type="hidden" name="path" value="~workflow-edit" />
        <input type="hidden" name="start" value="withdata"/>
        <input name="lang" type="hidden" value="${requestScope.lang}" />
        <input name="se_mcrid" type="hidden" value="${requestScope.mcrID}" />
        <input name="type" type="hidden" value="${requestScope.type}" />
        <input name="step" type="hidden" value="editor" />
        <input name="todo" type="hidden" value="weditobj" />
        <input type="submit" class="submitbutton" value="<fmt:message key="Editor.EditWrongDataset" />" />
    </form>   