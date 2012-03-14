<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt"uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>
<%@ page pageEncoding="UTF-8" %>

<c:set  var="baseURL" value="${applicationScope.WebApplicationBaseURL}"/>
<fmt:message var="pageTitle" key="WF.registeruser" /> 
<stripes:layout-render name="../../../WEB-INF/layout/default.jsp" pageTitle = "${pageTitle}">
	<stripes:layout-component name="contents">

<c:choose>
	<c:when test="${not empty param.debug}">
		<c:set var="debug" value="true" />
	</c:when>
	<c:otherwise> 
		<c:set var="debug" value="false" />
	</c:otherwise>
</c:choose>

<mcr:checkAccess var="adminuser" permission="administrate-user" />

<!--  handle task ending parameters -->
<c:if test="${not empty param.endTask}">
	<mcr:endTask success="success" processID="${param.processID}" 	taskName="${param.endTask}" transition="${param.transition}"/>
    
</c:if>

<c:choose>
	<c:when test="${adminuser eq 'true'}"> 
		<h2><fmt:message key="WF.Registeruser" />
			<br>&nbsp;<br>
			<fmt:message key="WF.common.MyTasks" />
		</h2>
		<mcr:getWorkflowTaskBeanList var="myTaskList" mode="activeTasks" workflowTypes="registeruser" varTotalSize="total1" />
		<table>
			<c:forEach var="task" items="${myTaskList}">
			 <tr><td class="task">
			   <table style="width:100%">			
				<c:set var="task" scope="request" value="${task}" />
				<c:import url="/content/workflow/${task.workflowProcessType}/getTasks.jsp" />
			   </table>
			 </td></tr>	
			</c:forEach>
			<c:if test="${empty myTaskList}">
			           <fmt:message key="WF.common.NoTasks" />
           	</c:if>			
		</table>
	</c:when>
	<c:otherwise>
		<fmt:message key="Admin.PrivilegesError" />
	</c:otherwise>
</c:choose>
</stripes:layout-component>
</stripes:layout-render>