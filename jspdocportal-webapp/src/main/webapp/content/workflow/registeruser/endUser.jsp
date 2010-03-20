<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn" %>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>
<c:set var="debug" value="true" />

<mcr:session var="user" method="get" key="registereduser" />
<c:set var="debug" value="true" />

<div class="headline">
 <fmt:message key="WF.registeruser.RegisterUser" /> - <fmt:message key="WF.registerUser.Intro" />
</div>

<table  class="bg_background" >
 <tr>
  <td>
	<c:choose>	
    <c:when test="${empty user}">
		<br/>
		<p>	<fmt:message key="WF.registerUser.ErrorMessage" />	</p>
		<hr/>	
    </c:when>
    <c:otherwise>
		<br/>
		<p>	<fmt:message key="WF.registerUser.Registered" />	</p>
		<p>	<fmt:message key="WF.registerUser.Registered2" />	</p>
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
    	  <tr><td class="metavalue"><b><fmt:message key="WF.registerUser.YourID"/> <x:out select=".//@ID" /></b></td></tr> 
    	  </table>
     	</x:forEach>
    	<hr/>
    </c:otherwise>
	</c:choose>
</td>
</tr>
</table>
