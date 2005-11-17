<%@ page import="org.mycore.frontend.jsp.navigation.NavNode,
                 java.util.Iterator,
                 org.mycore.frontend.jsp.navigation.NavEntry"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages'/>

<div class="headline"><fmt:message key="Search" /></div>
<p><fmt:message key="Search.Possibilities" /></p>
<center>
<c:import url="<%= new StringBuffer((String)request.getAttribute("WebApplicationBaseURL"))
                .append("editor/searchmasks/SearchMask_AllMetadataFields.xml?XSL.editor.source.new=true")
                .append("&XSL.editor.cancel.url=").append((String)request.getAttribute("WebApplicationBaseURL"))
                .append(request.getAttribute("lang")).toString() %>" />
</center>
<p><c:import url="content/node.jsp" /></p>
<p><fmt:message key="Search.HintsBool" /></p>
<p><fmt:message key="Search.HintsFulltext" /></p>
<p><fmt:message key="Search.HintsMinLength" /></p>
