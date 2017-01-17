<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.mycore.org/jspdocportal/base.tld" prefix="mcr"%>
<c:set var="baseURL" value="${applicationScope.WebApplicationBaseURL}" />
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
		<div class="headline"><fmt:message key="WF.Registeruser" /></div>
		<br>&nbsp;<br>
		<div class="headline"><fmt:message key="WF.common.MyTasks" /></div>
		<mcr:getWorkflowTaskBeanList var="myTaskList" mode="activeTasks" workflowTypes="registeruser" varTotalSize="total1" />
		<table>
			<c:forEach var="task" items="${myTaskList}">
			 <tr><td class="task">
			   <table width="100%">			
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
