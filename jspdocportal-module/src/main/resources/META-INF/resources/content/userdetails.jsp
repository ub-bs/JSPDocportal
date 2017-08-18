<%@page import="org.mycore.user2.MCRRole"%>
<%@page import="org.mycore.user2.MCRRoleManager"%>
<%@page import="org.mycore.common.MCRSessionMgr"%>
<%@page import="org.mycore.common.MCRUserInformation"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"   %>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>

<fmt:message var="pageTitle" key="Webpage.userdetails.title" /> 
<stripes:layout-render name="../WEB-INF/layout/default.jsp" pageTitle = "${pageTitle}">
	<stripes:layout-component name="contents">
      <h2><fmt:message key="Webpage.userdetails.title" /></h2>
      <div class="text">
        <p><fmt:message key="Webpage.userdetails.intro" /></p>
          <% MCRUserInformation userInfo = MCRSessionMgr.getCurrentSession().getUserInformation(); %>          
          <table>
            <tr><th><fmt:message key="Webpage.userdetails.data.id" />:</th><td><strong><%= userInfo.getUserID()%></strong></td></tr>
            <tr><td></td><td></td></tr>
            <tr><th><fmt:message key="Webpage.userdetails.data.memberships" />:</th>
              <td>
                <%
                  for(MCRRole r: MCRRoleManager.listSystemRoles()){
                    if(userInfo.isUserInRole(r.getName())){
    	              out.print(r.getName()+"<br />");
                    }
                  }
                %>
              </td>
            </tr>
	</table>
</div>


	</stripes:layout-component>
</stripes:layout-render>



