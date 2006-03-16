<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>

<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages' />
 
<div class="headline"><fmt:message key="Admin.Process" /></div>
<br/>
<c:set var="debug" value="false" />

<c:set var="pid" value="${param.pid}" />
<c:set var="type" value="${param.workflowProcessType}" />


<c:if test="${!empty(pid)}" >
    <mcr:deleteProcess result="result" pid="${pid}" workflowProcessType="${type}" />
	<table class="access" cellspacing="1" cellpadding="0" >
		<tr><td> zu löschender Prozess: <c:out value="${pid}" /> </td>
	        <td> Typ: <c:out value="${type}" /> </td>
	    </tr>
	    <tr>    
		    <td colspan="2" > Status: <fmt:message key="${result}" /> </td>
		</tr>
	  </table>	
	<hr/>
</c:if>
  
<mcr:listAllProcesses var="myWorkflowList" workflowProcessType="${type}" />

<table class="access" cellspacing="0" cellpadding="3" >
  <x:forEach select="$myWorkflowList/mcr_workflow">	
     <x:set var="type" select="string(./@type)"/>
	 <tr><th colspan="3" >Prozessdaten ${type}</th><th>Löschen</th></tr>	 
	 <x:forEach select="./process">	
      <x:set var="pid" select="string(./@pid)" />	
      <tr>
        <td valign="top"><b>Prozess ID: <x:out select="./@pid" /></b></td>
        <td>
          Initiator: <x:out select="./@initiator" /><br/>
          Autoren ID: <x:out select="./@authorID" /><br/>
          Document ID: <x:out select="./@createdDocID" />
        </td>  
        <td width="50"> </td>
	    <td align="center" >
			<form method="get" action="${applicationScope.WebApplicationBaseURL}admin" >
				<input type=hidden name="path" value="processes_list" />
				<input type=hidden name="pid" value="${pid}" />
				<input type=hidden name="workflowProcessType" value="${type}" />
				<input type="image" title="Prozess löschen" src="${applicationScope.WebApplicationBaseURL}admin/images/delete.gif" onClick="return questionDel()">	
			</form>					
        </td>
      </tr>
      </x:forEach>     
  </x:forEach>
</table>

