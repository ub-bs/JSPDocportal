<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Map" %>
<%@ page import="org.mycore.frontend.servlets.MCRServlet" %>
<%@ page import="org.mycore.common.MCRSession" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="org.mycore.common.MCRConfiguration" %>
<%@ page import="org.mycore.common.JSPUtils" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn" %>
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages'/>
<%
    String editorFile = request.getParameter("id");
    String xslSessionID = request.getParameter("XSL.editor.session.id");
    StringBuffer sbURL = new StringBuffer((String)request.getAttribute("WebApplicationBaseURL"))
    	.append("editor/workflow/").append(editorFile).append(".xml")
    	.append("?XSL.editor.session.id=").append(xslSessionID);
%>
<c:import url="<%= sbURL.toString() %>" />





