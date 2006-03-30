<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>

<fmt:setLocale value='${requestScope.lang}'/>
<fmt:setBundle basename='messages'/>
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

<mcr:getFreeUserIDs var="userIDList" count="${count}"  userid="${userID}" />

<div class="headline">
	<fmt:message key="Nav.AccountChangeID" />
</div>

<table  class="bg_background" width="90%" >
 <tr>
  <td>
	<br/>
	<p><fmt:message key="SWF.User.ChangeID" />	</p>
	<p>Gewähltes Kennzeichen: <c:out value="${param.userid}" /></p>

	<form method="post" action="servlets/MCRPassToTargetServlet">
	<input type="hidden" name="target" value="MCRCheckUserRegistrationServlet" />
	<input type="hidden" name="userID" value="${param.userid}" />
	<input type="hidden" name="page" value="nav?path=~registeredUser" />	
	<table class="editor" >
	 <tr>
	 <td class="metaname" >Auswählen</td> <td class="metaname" >Vorschläge</td>
	 </tr>
     <x:forEach select="$userIDList/freeUserID/userid">                            
	   <x:set var="newID" select="string(./@ID)" />
       <tr>
       	<td valign="top" >
       	 	<input type="radio" value="${newID}" name="newUserID" /> 
       	 </td>
    	<td valign="top" > 
    		<x:out select="./@ID"/>
    	</td>
      </tr>
	 </x:forEach>
	 <tr> <td colspan="2" ><INPUT type="submit" name="submit" value="Auswählen" /> </td></tr>
	 </table>
	 </form>
    </td>  
 </td>
</tr>
</table>
