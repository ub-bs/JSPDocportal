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

<mcr:getWorkflowTaskBeanList var="myTaskList" mode="activeTasks"  workflowTypes="author"  varTotalSize="total1" />
<mcr:getWorkflowTaskBeanList var="myProcessList" mode="initiatedProcesses"  workflowTypes="author" varTotalSize="total2" />
<div class="headline"><fmt:message key="WF.author" /></div>
<table>
	<tr>
		<td><img title="" alt="" src="${baseURL}images/greenArrow.gif"></td>
		<td>
			<a target="_self" href="${baseURL}nav?path=~authorbegin"><fmt:message key="WF.author.StartWorkflow" /></a>
		</td>
	</tr>
	<tr />
	<tr>
		<td><img title="" alt="" src="${baseURL}images/greenArrow.gif"></td>
			<td><fmt:message key="WF.author.SearchAuthorToEdit" /> </td>
	</tr>
	<tr>
		<td />
		<td>
			<c:url var="url" value="${WebApplicationBaseURL}editor/searchmasks/SearchMask_AuthorEdit.xml">
				<c:param name="XSL.editor.source.new" value="true" />
				<c:param name="XSL.editor.cancel.url" value="${WebApplicationBaseURL}" />
				<c:param name="lang" value="${requestScope.lang}" />
			</c:url>
			<c:import url="${url}" />        
			<br/>
		</td>
	</tr>
</table>

<c:choose>
	<c:when test="${empty(myTaskList)&& empty(myProcessList)}">
      <fmt:message key="WF.common.EmptyWorkflow" />   
      <hr/>
      <mcr:checkAccess permission="administrate-author" var="curUserIsAdminUser" />
      <c:if test="${!curUserIsAdminUser}">
	      <mcr:includeWebContent file="workflow/author_introtext.jsp"/>

   	  </c:if>
	</c:when>
	<c:otherwise>
		<br />&nbsp;<br />
		<div class="headline"><fmt:message key="WF.common.MyTasks" /></div>   
    	<table cellspacing="3">       
			<c:forEach var="task" items="${myTaskList}">
				<tr>
					<td class="task">
			        	<c:set var="task" scope="request" value="${task}" />
		    	       	<c:import url="/content/workflow/${task.workflowProcessType}/getTasks.jsp" />
			    	</td>
		    	</tr>       
			</c:forEach>
    	    <c:if test="${empty(myTaskList)}">
			  	<tr>
		  			<td class="task">
			           <font color="#00ff00"><fmt:message key="WF.common.NoTasks" /></font>
    	    		 </td>
        		</tr>
			</c:if>
		</table>
    	<br />&nbsp;<br />
    	<div class="headline"><fmt:message key="WF.author.MyInititiatedProcesses" /></div>
		<table>
    		<c:forEach var="task" items="${myProcessList}">
				<tr>
					<td class="task">
						<c:set var="task" scope="request" value="${task}" />
						<c:import url="/content/workflow/${task.workflowProcessType}/getTasks.jsp" />
		           </td>
				</tr>
	        </c:forEach>
    	    <c:if test="${empty(myProcessList)}">
				<tr>
					<td class="task">
	           			<font color="#00ff00"><fmt:message key="WF.common.NoTasks" /></font>
		           </td>
		 		</tr>
        	</c:if>
		</table>   
	</c:otherwise>
</c:choose>        
