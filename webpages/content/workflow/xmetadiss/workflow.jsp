<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>
<%@ page pageEncoding="UTF-8" %>

<c:set  var="baseURL" value="${applicationScope.WebApplicationBaseURL}"/>

<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages' />
<!--  debug handling -->
<c:choose>
   <c:when test="${!empty(param.debug)}">
      <c:set var="debug" value="true" />
   </c:when>
   <c:otherwise>
      <c:set var="debug" value="false" />
   </c:otherwise>
</c:choose>

<!--  handle task ending parameters -->
<c:if test="${!empty(param.endTask)}">
	<mcr:endTask success="success" processID="${param.processID}" 	taskName="${param.endTask}" transition="${param.transition}"/>
</c:if>

<!--  task management part -->

<mcr:getWorkflowTaskBeanList var="myTaskList" mode="activeTasks" workflowTypes="xmetadiss" 	varTotalSize="total1" />
<mcr:getWorkflowTaskBeanList var="myProcessList" mode="initiatedProcesses" workflowTypes="xmetadiss"    varTotalSize="total2" />

<c:choose>
   <c:when test="${empty(myTaskList)&& empty(myProcessList)}">
	  <div class="headline"><fmt:message key="WF.xmetadiss.info" /></div>
      <img title="" alt="" src="images/greenArrow.gif">
      <a target="_self" href="${baseURL}nav?path=~xmetadissbegin"><fmt:message key="WF.xmetadiss.StartWorkflow" /></a>
      <br/>&nbsp;<br>
      <fmt:message key="WF.common.EmptyWorkflow" />   
      <hr/>
	      <mcr:includeWebContent file="workflow/xmetadiss_introtext.html"/>
    	  <br/>&nbsp;<br>
   </c:when>
   <c:otherwise>
        <div class="headline"><fmt:message key="WF.xmetadiss" /></div>
        <br>&nbsp;<br>
        <div class="headline"><fmt:message key="WF.common.MyTasks" /></div>   
        
        <table>       
	        <c:forEach var="task" items="${myTaskList}">
	        <tr><td class="task">
			  
		           <c:set var="task" scope="request" value="${task}" />
		           <c:import url="/content/workflow/${task.workflowProcessType}/getTasks.jsp" />
		      
		    </td></tr>       
	        </c:forEach>
        <c:if test="${empty(myTaskList)}">
			           <fmt:message key="WF.common.NoTasks" />
        </c:if>
        </table>
        
        <br>&nbsp;<br>
        
        <div class="headline"><fmt:message key="WF.common.MyInititiatedProcesses" /></div>
        
        <table>
        <c:forEach var="task" items="${myProcessList}">
    	    <tr> <td class="task">
        	   <c:set var="task" scope="request" value="${task}" />
	           <c:import url="/content/workflow/${task.workflowProcessType}/getTasks.jsp" />
	        </td></tr>
        </c:forEach>
        <c:if test="${empty(myProcessList)}">
    		<tr> <td class="task">
			           <fmt:message key="WF.common.NoTasks" />
	        </td></tr>
        </c:if>
        </table>   
   </c:otherwise>
</c:choose>        
