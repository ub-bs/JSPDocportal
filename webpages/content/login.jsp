<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages' />
<c:set var="debug" value="${param.debug}" />
<!-- available user status  
	 status = { user.login, user.invalid_password, user.welcome, user.disabled, user.unknown, user.unkwnown_error
             
-->

<mcr:login uid="${param.uid}" pwd="${param.pwd}" var="loginresult" />

<div class="headline"><fmt:message key="Webpage.login.ChangeUserID" /></div>
<x:forEach select="$loginresult">
  <x:set var="status" select="string(./@status)" />
  <x:set var="username" select="string(./@username)" />
  <x:set var="loginOK" select="string(./@loginOK)" />
  <x:set var="name" select="string(./@name)" />
</x:forEach>	

<table id="metaHeading" cellpadding="0" cellspacing="0">
	<tbody>
		<tr>
			<td><fmt:message key="Webpage.login.YouAreLoggedInAs" />:&nbsp;	<c:out value="${username}"></c:out></td>
		</tr>
	</tbody>
</table>
<hr>

<c:choose>
<c:when test="${fn:contains(status,'user.welcome') or fn:contains(status,'user.disabled_member')}">

	<table class="frameintern">
		<tr class="result" valign="bottom" >
			<td colspan="3" valign="bottom" ><b><fmt:message key="Webpage.login.status.${status}" >
			<fmt:param value="${name}" /></fmt:message></b></td>
		</tr>
		<tr class="result">
			<td colspan="3" > 			    
 			    <table>
 			      <tr><td colspan="2" ><b>Sie haben folgende Arbeitsmöglichkeiten:</b></td></tr>
 			      <x:forEach select="$loginresult">
	 			    <x:forEach select="./groups/group">
	 				<tr>
	 				<td><img src="${requestScope.WebApplicationBaseURL}images/greenArrow.gif"></td>
	 				<td>
	 					<!--<x:out select="./@description" /> (<x:out select="./@gid" />)-->
	 					<c:set var="gid"><x:out select="./@gid" /></c:set>
	 					<c:set var="gdescr"><x:out select="./@description" /></c:set>
	 					<mcr:login_startlink group_id="${gid}" group_description="${gdescr}" />
	 				</td>
	 				</tr>
	 				</x:forEach>
				  </x:forEach> 
				</table>				
			</td>
		</tr>

	</table>
</c:when>
<c:otherwise>
    <div id="userStatus"><fmt:message key="Webpage.login.status.${status}" /></div>
	<form method="post" action="nav?path=~login"><input name="url"
		value="<c:out value="${requestScope.WebApplicationBaseURL}" />"
		type="hidden">
	<table id="userAction">
		<tbody>
			<tr>
				<td class="inputCaption"><fmt:message key="Webpage.login.UserLogin" />:</td>
				<td class="inputField"><input maxlength="30" class="text" name="uid"
					type="text"></td>
			</tr>
			<tr>
				<td class="inputCaption"><fmt:message key="Webpage.login.Password" />:</td>
				<td class="inputField"><input maxlength="30" class="text" name="pwd"
					type="password"></td>
			</tr>
		</tbody>
	</table>
	<hr>
	<div class="submitButton">&nbsp; <input name="LoginSubmit"
		value="<fmt:message key="Webpage.login.Login" />" class="submitbutton"
		type="submit"> &nbsp; <input name="LoginReset"
		value="<fmt:message key="Webpage.login.Cancel" />" class="submitbutton" type="reset">
	</div>
	</form>

</c:otherwise>
</c:choose>

