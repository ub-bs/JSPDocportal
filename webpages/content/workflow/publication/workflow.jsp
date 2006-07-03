<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
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
    <c:set var="endTask" scope="request" value="${param.endTask}" />
    <c:set var="processID" scope="request" value="${param.processID}" />
    <c:set var="transition" scope="request" value="${param.transition}" />
    <c:import url="/content/workflow/publication/endTasks.jsp" />
</c:if>

<!--  task management part -->

<mcr:getWorkflowTaskBeanList var="myTaskList" mode="activeTasks" workflowTypes="publication" varTotalSize="total1" />
<mcr:getWorkflowTaskBeanList var="myProcessList" mode="initiatedProcesses" workflowTypes="publication"    varTotalSize="total2" />

<c:choose>
   <c:when test="${empty(myTaskList)&& empty(myProcessList)}">   
    <mcr:session var="sessionID" method="get" type="ID" />
	<div class="headline"><fmt:message key="WF.publication.info" /></div>
	<table>
	<tr><td>  
	      <img title="" alt="" src="${baseURL}images/greenArrow.gif">
	      <a target="_self" href="${baseURL}nav?path=~publicationbegin"><fmt:message key="WF.publication.StartWorkflow" /></a>
	      <br/>&nbsp;<br>
	    </td>  
	<tr>
		<td><img title="" alt="" src="${baseURL}images/greenArrow.gif"><fmt:message key="WF.publication.SearchToEdit" /> </td>
	</tr>
	<tr>
		<td>
			<c:url var="url" value="${WebApplicationBaseURL}editor/searchmasks/SearchMask_PublicationEdit.xml">
				<c:param name="XSL.editor.source.new" value="true" />
				<c:param name="XSL.editor.cancel.url" value="${WebApplicationBaseURL}" />
			    <c:param name="MCRSessionID" value="${sessionID}"/>
				<c:param name="lang" value="${requestScope.lang}" />
			</c:url>
			<c:import url="${url}" />
			<br/>        
		</td>
	</tr>      
      <img title="" alt="" src="${baseURL}images/greenArrow.gif">
      <fmt:message key="WF.common.EmptyWorkflow" />   
      <br>&nbsp;<br>
      <mcr:checkAccess permission="administrate-publication" var="curUserIsAdminUser" />
      <c:if test="${!curUserIsAdminUser}">
	      <c:import url="/content/workflow/publication/information.jsp" /> 
    	  <br/>&nbsp;<br>
   	  </c:if>
   </c:when>
   <c:otherwise>
        <div class="headline"><fmt:message key="WF.publication" /></div>
        <img title="" alt="" src="${baseURL}images/greenArrow.gif">
        <a target="_self" href="${baseURL}nav?path=~publicationbegin"><fmt:message key="WF.publication.StartWorkflow" /></a>
        <br/>&nbsp;<br>
        <br>&nbsp;<br>
        <div class="headline"><fmt:message key="WF.common.MyTasks" /></div>   
        
        <table cellspacing="3" >       
	        <c:forEach var="task" items="${myTaskList}">
	        <tr><td class="task" border="1">
	           <c:set var="task" scope="request" value="${task}" />
	           <c:import url="/content/workflow/${task.workflowProcessType}/getTasks.jsp" />
		    </td></tr>       
	        </c:forEach>
	        <c:if test="${empty(myTaskList)}">
	        <tr><td class="task">
	           <font color="#00ff00"><fmt:message key="WF.common.NoTasks" /></font>
	        <tr><td class="task">
	        </c:if>
        </table>
        
        <br>&nbsp;<br>
        
        <div class="headline"><fmt:message key="WF.publication.MyInititiatedProcesses" /></div>
        
        <table>
	        <c:forEach var="task" items="${myProcessList}">
	        <tr><td class="task">
	           <c:set var="task" scope="request" value="${task}" />
	           <c:import url="/content/workflow/${task.workflowProcessType}/getTasks.jsp" />
		    </td></tr>       
    	    </c:forEach>
	        <c:if test="${empty(myProcessList)}">
	        <tr><td class="task">
        	   <font color="#00ff00"><fmt:message key="WF.common.NoTasks" /></font>
		    </td></tr>       
    	    </c:if>
        </table>   
   </c:otherwise>
</c:choose>        
