<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>

<fmt:message var="pageTitle" key="Webpage.admin.Process" /> 
<stripes:layout-render name="../WEB-INF/layout/default.jsp" pageTitle = "${pageTitle}">
	<stripes:layout-component name="contents">
		<c:set var="debug" value="false" />
		<c:set var="pid" value="${param.pid}" />
		<c:set var="wfVar" value="${param.workflowVar}" />
		<c:set var="type" value="${param.workflowProcessType}" />

		<h2><fmt:message key="Webpage.admin.Process" /></h2>
		<p> </p>

		<c:if test="${not empty pid && not empty wfVar}" >
   		<mcr:deleteWorkflowEngineVariable var="result" pid="${pid}" workflowVar="${wfVar}" />
		<table class="access" cellspacing="1" cellpadding="0" >
			<tr><td> Prozess: </td><td> <c:out value="${pid}" /></td></tr>
	    	<tr><td> Variable: </td>
	     		<td>
	       			<c:choose>
			 			<c:when test="${wfVar eq 'null'}" >
							<fmt:message key="Variable.null" />
			 			</c:when>
			 			<c:otherwise>
	     					<c:out value="${wfVar}" />
	     	 			</c:otherwise>
		    		</c:choose>           
	     		</td>
	     		</tr>
	    		<tr><td colspan="2" > Status: <fmt:message key="${result}" /></td></tr>
	  		</table>	
		<hr/>
		</c:if>

<mcr:listAllProcesses var="myWorkflowList" workflowProcessType="${type}" />

<p> </p>

<table class="access" cellspacing="0" cellpadding="3" >
  <x:forEach select="$myWorkflowList/mcr_workflow">	
     <x:set var="type" select="string(./@type)"/>
	 <tr><th colspan="3" >Prozess ${type}</th><th colspan="2"> Werte </th><th>Löschen</th></tr>	 
	 <x:forEach select="./process">	
      <x:set var="pid" select="string(./@pid)" />	
	  <form method="get" action="${applicationScope.WebApplicationBaseURL}admin" >
      <tr>
        <td valign="top"><b>Prozess ID: <x:out select="./@pid" /></b></td>
        <td valign= "top" >
         <table cellpadding="5" cellspacing="0" >
          <tr valign="top">
	           <td> Initiator: </td>
	           <td>
					<x:choose>
					<x:when select="./@initiator = 'null'">
						<fmt:message key="Variable.null" />
					</x:when>
					<x:otherwise>
		                <x:out select="./@initiator" />
					</x:otherwise>
					</x:choose>           

			   </td>
	           <td> <input type=radio value="initiator" name="workflowVar" /> </td>
          </tr>
          <tr> 
	           <td>Autoren ID:</td>
	           <td>
					<x:choose>
					<x:when select="./@authorID = 'null'">
						<fmt:message key="Variable.null" />
					</x:when>
					<x:otherwise>
						<x:out select="./@authorID" />  
					</x:otherwise>
					</x:choose>           
			   </td>
	            <td><input type=radio value="authorID" name="workflowVar" /></td>
          </tr>
          <tr>
	           <td>Document ID: </td>
	           <td>
					<x:choose>
					<x:when select="./@createdDocID = 'null'">
						<fmt:message key="Variable.null" />
					</x:when>
					<x:otherwise>
						<x:out select="./@createdDocID" />  
					</x:otherwise>
					</x:choose>           

			   </td>	           
	           <td><input type=radio value="createdDocID" name="workflowVar" /></td>
          </tr>
         </table>
        </td>  
    	<td width="10"> </td>        
	    <td align="center" >
				<input type=hidden name="path" value="processes_edit" />
				<input type=hidden name="pid" value="${pid}" />
				<input type=hidden name="workflowProcessType" value="${type}" />
				<input type="image" title="Prozess löschen" src="${applicationScope.WebApplicationBaseURL}admin/images/delete.gif" onClick="return questionDel()">	
        </td>
      </tr>
	  </form>					
     </x:forEach>     
  </x:forEach>
</table>

		
</stripes:layout-component>
</stripes:layout-render>    
  
