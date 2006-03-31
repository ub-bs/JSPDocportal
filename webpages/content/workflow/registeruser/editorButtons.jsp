<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr"%>
<c:set var="baseURL" value="${applicationScope.WebApplicationBaseURL}" />
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages' />

<c:set var="processid" value="${requestScope.task.processID}" />
<c:set var="dom" value="${requestScope.task.variables}" />
<c:set var="userID">	<x:out select="$dom/variables/variable[@name = 'userID']/@value" /></c:set>
<c:set var="description">	<x:out select="$dom/variables/variable[@name = 'description']/@value" /></c:set>
<c:set var="email">	<x:out select="$dom/variables/variable[@name = 'email']/@value" /></c:set>
<c:set var="name">	<x:out select="$dom/variables/variable[@name = 'name']/@value" /></c:set>
<c:set var="faculty">	<x:out select="$dom/variables/variable[@name = 'faculty']/@value" /></c:set>
<c:set var="institution">	<x:out select="$dom/variables/variable[@name = 'institution']/@value" /></c:set>


	<tr valign="top">
		<td>
			<b>Benutzerkennzeichen: <c:out value="${userID}" /></b><br/>
			<c:out value="${name}" /> <br />
			<c:out value="${faculty}" /> <c:out value="${institution}" />
		</td>		
		<td>&#160;</td>		
	</tr><tr>
	    <td class="description" >
		    <c:out value="${description}" />
	    </td>	
		<td align="right">
		 <table >
		  <tr>
			<td align="center" valign="top" width="50">
				<form method="get" action="${baseURL}registeruserworkflow">
				    <input  name="nextPath" value="~workflow-registeruser-modify" type="hidden">
					<input	name="processid" value="${processid}" type="hidden"> 
					<input	name="todo" value="WFModifyWorkflowUser" type="hidden"> 
					<input	title="<fmt:message key="Object.EditObject" />"	src="${baseURL}images/workflow_objedit.gif" type="image"	class="imagebutton">
				</form>
			</td>
		  </tr>		 
		 </table>
		</td>
	</tr>
	<tr>
		<td colspan="3" class="description">&#160;</td>
	</tr>

