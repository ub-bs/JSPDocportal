<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn" %>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>
<c:set var="debug" value="true" />
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages'/>


<mcr:session var="user" method="get" key="registereduser" />
<c:set var="debug" value="true" />

<div class="headline">
 <fmt:message key="Nav.Application.registerUser" /> - <fmt:message key="SWF.registerUser.Intro" />
</div>

<table  class="bg_background" >
 <tr>
  <td>
	<c:choose>	
    <c:when test="${empty(user)}">
		<br/>
		<p>	<fmt:message key="SWF.registerUser.ErrorMessage" />	</p>
		<hr/>	
    </c:when>
    <c:otherwise>
		<br/>
		<p>	<fmt:message key="SWF.registerUser.Registered" />	</p>
		<p>	<fmt:message key="SWF.registerUser.Registered2" />	</p>
		<hr/>
       	 <x:forEach select="$user">
    	  <table class="editor">
    	  <tr><td class="metaname">
    	  			 <x:out select=".//contact.salutation" /> 
    	       &#160;<x:out select=".//contact.firstname" />
    	  	   &#160;<x:out select=".//contact.lastname" />
    	  </td></tr>
    	  <tr><td class="metavalue"><x:out select=".//contact.institution" /></td></tr>
    	  <tr><td class="metavalue"><x:out select=".//contact.faculty" /></td></tr>
    	  <tr><td class="metavalue"><x:out select=".//contact.email" /></td></tr>
    	  </table>
     	</x:forEach>
    	<hr/>
    </c:otherwise>
	</c:choose>
	<p><fmt:message key="Nav.Service.Text1" /></p>
	<p><a href="mailto:atlibri@uni-rostock.de">atlibri@uni-rostock.de</a></p>
 </td>
</tr>
</table>
