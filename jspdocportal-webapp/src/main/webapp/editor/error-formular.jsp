<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Iterator" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn" %>

<div class="headline"><fmt:message key="Webpage.editor.CheckFormData" /></div>
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
                <font color="red"><fmt:message key="Webpage.editor.YouErrorMessageIsAbove" /></font>
            </td>
        </tr>
    </table>
    </center>
    <p></p>
    <br />
    <p></p>
    <form action="${applicationScope.WebApplicationBaseURL}nav" method="post">
    	<input type="hidden" name="path" value="~workflow-edit" />
        <input type="hidden" name="start" value="withdata"/>
        <input name="lang" type="hidden" value="${requestScope.lang}" />
        <input name="mcrid" type="hidden" value="${requestScope.mcrID}" />
        <input name="type" type="hidden" value="${requestScope.type}" />
        <input name="step" type="hidden" value="editor" />
        <input name="todo" type="hidden" value="weditobj" />
        <input type="submit" class="submitbutton" value="<fmt:message key="Webpage.editor.EditWrongDataset" />" />
    </form>   
