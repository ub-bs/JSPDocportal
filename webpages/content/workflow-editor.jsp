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
    Map map = request.getParameterMap();
    String sessionID = ((MCRSession)MCRServlet.getSession(request)).getID();
    StringBuffer sbURL = new StringBuffer((String)request.getAttribute("WebApplicationBaseURL"))
        .append("start_edit");

    boolean first = true;
    for (Iterator it = map.keySet().iterator(); it.hasNext();) {
        if (first) {
            sbURL.append("?");
            first = false;
        }
        String key = (String)it.next();
        if (key.equals("path")) continue;
        if (key.equals("JSessionID") || key.equals("HttpSessionID") ) continue;
        String[] values = (String[])map.get(key);
        sbURL.append(key).append("=").append(values[0]);
        if (it.hasNext()){
            sbURL.append("&");
        }
    }
    if (session != null) {
    	sbURL.append("&HttpSessionID=").append(sessionID); 
    }
    if(map.get("lang") == null) {
    	String lang = (String)request.getAttribute("lang");
    	if (lang == null) lang = "de";
        sbURL.append("&lang=").append(lang);
    }    
%>
<c:import url="<%= response.encodeRedirectURL(sbURL.toString()) %>" />




