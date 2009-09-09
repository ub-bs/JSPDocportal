<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>

<c:set var="WebApplicationBaseURL" value="${applicationScope.WebApplicationBaseURL}" />
<c:choose>
   <c:when test="${empty param.userID}">
	<c:set var="userID" value="${requestScope.userID}" /> 
   </c:when>
   <c:otherwise>
	 <c:set var="userID" value="${param.userID}" /> 
   </c:otherwise>
</c:choose>

<c:set var="count" value="4" /> 
<c:set var="debug" value="false" />

<div class="headline">
	<fmt:message key="Nav.AccountChangeID" />
</div>

<table  class="bg_background" width="90%" >
 <tr>
  <td>
	<br/>
    <p><fmt:message key="WF.registerUser.UserIDExist" />	</p>
	<p><fmt:message key="WF.registerUser.YourID" /> <c:out value="${userID}" /></p> 

	<form method="post" action="MCRPassToTargetServlet">
	<input type="hidden" name="target" value="MCRRegisterUserWorkflowServlet" />
	<input type="hidden" name="userID" value="${userID}" />
	<input type="hidden" name="newUserID" value="" /> 
	<input type="hidden" name="page" value="nav?path=~registerUser" />	
	<input type="hidden" value="~registeruser" name="nextPath" /> 
	<INPUT type="submit" name="submit" value="Zurück" /> </td></tr>
	 </table>
	 </form>
    </td>  
 </td>
</tr>
</table>
