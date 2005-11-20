<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ page import="org.mycore.frontend.jsp.navigation.NavTree,
                 org.mycore.common.MCRSession,
                 org.mycore.common.MCRSessionMgr,
                 org.mycore.common.MCRConfiguration,
                 org.apache.log4j.Logger,
                 org.mycore.frontend.servlets.MCRServlet"%>
<%@ page import="org.mycore.frontend.jsp.navigation.NavNode"%>
<%@ page import="org.mycore.frontend.jsp.navigation.NavEntry"%>
<%@ page import="org.mycore.frontend.jsp.NavServlet"%>
<%@ page import="java.util.Iterator"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn" %>
<%
    MCRSession mcrSession = MCRServlet.getSession(request);
    String WebApplicationBaseURL = NavServlet.getNavigationBaseURL();
    NavTree tree = (NavTree)request.getAttribute("nav");
    NavNode currentNode = (NavNode)request.getAttribute("node");
    String path = (String)request.getAttribute("path");
    String contentPage = (String)request.getAttribute("content");
    String lang = request.getParameter("lang");
    if (lang != null) {
       mcrSession.setCurrentLanguage(lang);
    }else {
       lang = mcrSession.getCurrentLanguage();
    }
    if ("de-en".indexOf(lang) < 0) 
        lang = "de"; 
    String translateLang = (lang.equals("de"))? "en":"de";
    request.setAttribute("lang",lang);
    request.setAttribute("WebApplicationBaseURL",WebApplicationBaseURL);
    String username = mcrSession.getCurrentUserID();
    if(username == null)
        username = MCRConfiguration.instance().getString("MCR.users_guestuser_username");
    /*"dummy.jsp";
    if(path.equals("about")) {
    contentPage = "content.jsp";
    }*/
%>

<html>
    <head>
        <fmt:setLocale value="<%= lang %>" />
        <fmt:setBundle basename='messages'/>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <fmt:message var="pageTitle" key="<%= new StringBuffer("Title.").append(path).toString() %>" />
        <title>
        <c:choose>
            <c:when test="${fn:startsWith(pageTitle,'???')}">
                <fmt:message key="<%= currentNode.getValue().getDescription() %>" />
            </c:when>
            <c:otherwise>
                <c:out value="${pageTitle}" />
            </c:otherwise>
        </c:choose> @ <fmt:message key="Title.DocPortalTrailer" />
        </title>

        <link type="text/css" rel="stylesheet" href="<%= WebApplicationBaseURL %>css/style_general.css">
        <link type="text/css" rel="stylesheet" href="<%= WebApplicationBaseURL %>css/style_navigation.css">
        <link type="text/css" rel="stylesheet" href="<%= WebApplicationBaseURL %>css/style_content.css">
    </head>
    <body topmargin="0" rightmargin="0" leftmargin="0">
    <table id="maintable" cellpadding="0" cellspacing="0">
        <tr class="max">
            <td id="mainLeftColumn">
                <a href="<%= WebApplicationBaseURL %>"><img id="logo" alt="Logo" src="<%= WebApplicationBaseURL %>images/logo.gif"></a>
                <!-- Navigation Left Start -->
                <div class="navi_main">
                    <table class="navi_main" cellpadding="0" cellspacing="0">
                        <tr>
                            <td style="height: 1px; width: 7%;"><img title="" alt="" style="width: 1px; height: 1px;" src="<%= WebApplicationBaseURL %>images/emtyDot1Pix.gif"></td>
                            <td style="width: 9%;"><img title="" alt="" style="width: 1px; height: 1px;" src="<%= WebApplicationBaseURL %>images/emtyDot1Pix.gif"></td>
                            <td style=""><img title="" alt="" style="width: 1px; height: 1px;" src="<%= WebApplicationBaseURL %>images/emtyDot1Pix.gif"></td>
                            <td style="width: 7%;"><img title="" alt="" style="width: 1px; height: 1px;" src="<%= WebApplicationBaseURL %>images/emtyDot1Pix.gif"></td>  
                        </tr>
                        <% {
                            NavNode n3 = tree.getChild("left");
                            Iterator i = n3.iterator();
                            while(i.hasNext()) {
                                NavNode n = (NavNode)i.next();
                                NavEntry e = n.getValue();
                                String eLink = (e.isExtern()) ? e.getLink() : (WebApplicationBaseURL + e.getLink()) ;
                            %>
                            <tr>
                                <td style="height:17px;"><img title="" alt="" style="width:1px; height:1px" src="<%= WebApplicationBaseURL %>images/emtyDot1Pix.gif"></td>
                                <th align="left" colspan="4"><a target="_self" href="<%= eLink %>"><fmt:message key="<%=e.getDescription()%>" /></a></th>
                            </tr>
                            <%  if(n.isOpened()) {
                                    Iterator i2 = n.iterator();
                                    while(i2.hasNext()) {
                                        NavNode n2 = (NavNode)i2.next();
                                        NavEntry e2 = n2.getValue();
                                        String e2Link = (e2.isExtern())? e2.getLink() : (WebApplicationBaseURL + e2.getLink()) ;
                                        String selected = n2.isOpened() ? "-selected":"";
                                        StringBuffer imgSB = new StringBuffer(WebApplicationBaseURL)
                                            .append("images/line-with-element").append(selected);
                                        if (!i2.hasNext())
                                           imgSB.append("_end");
                                        imgSB.append(".gif");
                                    %>
                                    <tr>
                                        <td style="height:17px;"><img title="" alt="" style="width:1px; height:17px;" src="<%= WebApplicationBaseURL %>images/emtyDot1Pix.gif"></td>
                                        <td align="center"><img title="" alt="" src="<%= imgSB.toString() %>"></td>
                                        <td colspan="2">
                                        <%  if(n2.isOpened()) {
                                        %>
                                                <span class="marked"><a target="_self" href="<%= e2Link %>"><fmt:message key="<%=e2.getDescription()%>" /></a></span>
                                        <% }else {
                                        %>
                                                <a target="_self" href="<%= e2Link %>"><fmt:message key="<%=e2.getDescription()%>" /></a>
                                        <%
                                           }
                                        %>
                                        </td>
                                    </tr>
                                    <%
                                    }
                                }
                                %>
                            <% if(i.hasNext()) {
                            %>
                                <tr>
                                    <td colspan="5" style="height:10px;"><img title="" alt="" style="width:1px; height:1px" src="<%= WebApplicationBaseURL %>images/emtyDot1Pix.gif"></td>
                                </tr>
                                <%
                            }
                            }
                        }
                        %>
                        <tr>
                            <td colspan="5" style="height:15px;"><img title="" alt="" style="width:1px; height:1px" src="<%= WebApplicationBaseURL %>images/emtyDot1Pix.gif"></td></tr>
                    </table>
                </div>
                <!-- NAVIGATION LEFT END -->
            </td>
            <td class="max autowidth">
                <table class="max" cellpadding="0" cellspacing="0">
                    <tr class="minheight">
                        <td id="navi_below_cell">
                        <!-- NAVIGATION TOP START -->
                            <table cellpadding="0" cellspacing="0" class="navi_below">
                                <tr>
                                <% { 
                                    Iterator i = tree.getChild("top").iterator();
                                    boolean first = true;
                                    while(i.hasNext()) {
                                        NavNode n = (NavNode)i.next();
                                        NavEntry e = n.getValue();
                                        String eLink = (e.isExtern()) ? e.getLink() : (WebApplicationBaseURL + e.getLink()) ;
                                        if(e==null)
                                            throw new Exception("e is null");
                                    %>
                                    <td>
                                        <a target="_self" href="<%= eLink %>">
                                            <% if(first){%><span style="font-weight:bold;"><% } %>
                                                <fmt:message key="<%=e.getDescription()%>" />
                                            <% if(first){%></span><%}%>
                                        </a>
                                    </td>
                                    <% if(i.hasNext()) {
                                       %>
                                       <td><img alt="" style="width:6px; height:1px;" src="<%= WebApplicationBaseURL %>images/emtyDot1Pix.gif"></td>
                                       <td>|</td>
                                       <td><img alt="" style="width:6px; height:1px;" src="<%= WebApplicationBaseURL %>images/emtyDot1Pix.gif"></td>
                                       <%
                                       }
                                       first = false;
                                    }
                                }
                                %>
                                <td style="width:10px;"><img alt="" style="width:10px; height:1px;" src="<%= WebApplicationBaseURL %>images/emtyDot1Pix.gif"></td>
                                <td><a href="<%= WebApplicationBaseURL %>nav?path=<%= path %>&lang=<%= translateLang %>"><img style="border-style: none; width: 24px; height: 12px; vertical-align: bottom;" alt="<fmt:message key="secondLanguage" />" src="<%= WebApplicationBaseURL %>images/lang-<%= translateLang %>.gif"></a></td>           
                                <td style="width:10px;"><img alt="" style="width:10px; height:1px;" src="<%= WebApplicationBaseURL %>images/emtyDot1Pix.gif"></td>
                            </tr>
                        </table>
                        <!-- NAVIGATION TOP RIGHT -->
                    </td>
                </tr>
                <tr class="minheight">
                    <td>
                        <table class="navi_history">
                            <tr>
                                <td class="navi_history">
                                <fmt:message key="Nav.Navigation" />:&nbsp;
                                <% {
                                    NavNode bc = (NavNode)tree;
                                    boolean first = true;
                                    while(bc.hasNextFlagged()) {
                                        bc = bc.getNextFlagged();
                                        NavEntry e = bc.getValue();
                                        String eLink = (e.isExtern())? e.getLink() : (WebApplicationBaseURL + e.getLink()) ;
                                        if(!e.isHidden()) {
                                            if(!first) {
                                            %> &gt; <%    }%>
                                            <a href="<%= eLink %>"><fmt:message key="<%=e.getDescription()%>" /></a>
                                        <%  first = false;
                                        }
                                    }
                                }
                                %>
                                </td>
                               <td class="navi_history_user">Benutzer: <a href="<%= WebApplicationBaseURL %>nav?path=~login"><%=username%></a></td>
                           </tr>
                      </table>
                  </td>
              </tr>
              <tr>
                <td id="contentArea">
                    <div id="contentWrapper">
                        <!-- ************************************************ -->
                        <!-- including <%=contentPage%> -->
                        <!-- ************************************************ -->
                        <c:catch var="e">
                            <c:import url='<%=contentPage%>' />
                        </c:catch>
                        <c:if test="${e!=null}">
                        <% 
                            Throwable error = (Throwable) pageContext.getAttribute("e");
                            Logger.getLogger("frame.jsp").error("error", error); 
                        %>
                            <c:import url="${requestScope.WebApplicationBaseURL}mycore-error.jsp">
                                <c:param name="message">${e.class} ${e.message} $e.localisedMessage} hh</c:param>
                            </c:import>
                        <textarea cols="100" rows="25">
                            <%  error.printStackTrace(new java.io.PrintWriter(out)); %>
                        </textarea>
                        </c:if>          
                    </div>
                </td>
            </tr>
            <tr class="minheight">
                <td id="footer"> Autor: Administrator, 
                <%
                    java.util.Calendar cal = new java.util.GregorianCalendar( java.util.TimeZone.getTimeZone("ECT") );
                    java.text.DateFormat formater;
                    formater = java.text.DateFormat.getDateTimeInstance(
                                     java.text.DateFormat.FULL, java.text.DateFormat.MEDIUM ); 
                    out.print(formater.format( cal.getTime() ));        
                %>
                </td>
            </tr>
        </table>
      </td>
    </tr>
   </table>
  </body>
</html>