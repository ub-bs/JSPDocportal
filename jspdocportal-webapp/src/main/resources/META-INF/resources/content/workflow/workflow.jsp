<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>
<%@ page pageEncoding="UTF-8" %>

<%--Parameter: wftype = workflow type --%>

<fmt:message var="pageTitle" key="WF.${param.wftype}" /> 
<stripes:layout-render name="../../WEB-INF/layout/default.jsp" pageTitle = "${pageTitle}" layout="2columns">
<stripes:layout-component name="html_header">
	<link type="text/css" rel="stylesheet" href="${WebApplicationBaseURL}css/style_tasks.css">
</stripes:layout-component>
	<stripes:layout-component name="contents">
		<c:set  var="baseURL" value="${applicationScope.WebApplicationBaseURL}"/>
		

		<!--  handle task ending parameters -->
		<c:if test="${not empty param.endTask}">
			<mcr:endTask success="success" processID="${param.processID}" 	taskName="${param.endTask}" transition="${param.transition}"/>
		</c:if>

		<!--  task management part -->

		<mcr:getWorkflowTaskBeanList var="myTaskList" mode="activeTasks" workflowTypes="${param.wftype}" 	varTotalSize="total1" size="50" />
		<mcr:getWorkflowTaskBeanList var="myProcessList" mode="initiatedProcesses" workflowTypes="${param.wftype}"    varTotalSize="total2" size="50" />

		<c:choose>
   			<c:when test="${empty myTaskList && empty myProcessList}">
	  			<h2><fmt:message key="WF.${param.wftype}.info" /></h2>
	  			<ul class="action">    
	    			<li><a target="_self" href="${baseURL}nav?path=~${param.wftype}begin"><fmt:message key="WF.${param.wftype}.StartWorkflow" /></a></li>
	    		</ul>
      			<p>
      				<fmt:message key="WF.common.EmptyWorkflow" />
      			</p>   
      			<hr/>
	      		<mcr:includeWebContent file="workflow/${param.wftype}_introtext.html"/>
    	  		<br/>&nbsp;<br>
   			</c:when>
   			<c:otherwise>
        		<h2><fmt:message key="WF.${param.wftype}" /></h2>
	   				<mcr:checkAccess var="createallowed" permission="administrate-${param.wftype}" />
   					<c:if test="${createallowed}">
   						<ul class="action">    
	    					<li><a target="_self" href="${baseURL}nav?path=~${param.wftype}begin"><fmt:message key="WF.${param.wftype}.StartWorkflow" /></a></li>
	    				</ul>	    				
  					</c:if>
   
   					<br />&nbsp;<br />
        
        			<h3><fmt:message key="WF.common.MyTasks" /></h3>   
        			<div class="tasklist">
        				<c:forEach var="task" items="${myTaskList}">
	        				<div class="task">
			  						<c:set var="task" scope="request" value="${task}" />
		           					<c:import url="/content/workflow/${task.workflowProcessType}/getTasks.jsp" />
		      
		    				</div>       
	        			</c:forEach>
	        			<c:if test="${empty myTaskList}">
			        	   <span style="color:#00ff00;"><fmt:message key="WF.common.NoTasks" /></span>
        				</c:if>
        			</div>
        
        			<h3><fmt:message key="WF.common.MyInititiatedProcesses" /></h3>
        			<div class="processlist">
        				<c:forEach var="task" items="${myProcessList}">
    	    				<div class="process">
        	   					<c:set var="task" scope="request" value="${task}" />
	           					<c:import url="/content/workflow/${task.workflowProcessType}/getTasks.jsp" />
	        				</div>
        				</c:forEach>
        				<c:if test="${empty myProcessList}">
    							<span style="color:#00ff00;"><fmt:message key="WF.common.NoTasks" /></span>
    					</c:if>
	     			</div>      
				  </c:otherwise>
			</c:choose>
</stripes:layout-component>
</stripes:layout-render>
