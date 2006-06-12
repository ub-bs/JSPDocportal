<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages' />
<c:set var="debug" value="false" />
<!-- available user status  
	 status = { user.login, user.invalid_password, user.welcome, user.disabled, user.unknown, user.unkwnown_error
             
-->

<mcr:login uid="${param.uid}" pwd="${param.pwd}" var="loginresult" />

<div class="headline"><fmt:message key="Webpage.login.ChangeUserID" /></div>

<x:forEach select="$loginresult">
  <x:set var="status" select="string(./@status)" />
  <x:set var="username" select="string(./@username)" />
  <x:set var="loginOK" select="string(./@loginOK)" />
</x:forEach>	

<table id="metaHeading" cellpadding="0" cellspacing="0">
	<tbody>
		<tr>
			<td class="titles"><fmt:message key="Webpage.login.YouAreLoggedInAs" />:&nbsp;&nbsp;
			<c:out value="${username}"></c:out></td>
		</tr>
	</tbody>
</table>
<hr>

<c:choose>
<c:when test="${fn:contains(status,'user.welcome')}">
	<table class="frameintern">
		<colgroup>
			<col width="4%">
			<col width="48%" span="2">
		</colgroup>
		<tr class="result">
			<td colspan="3" style="font-weight:bolder;" class="header">Was wollen
			Sie tun?</td>
		</tr>
		<tr style="height:10px;">
			<td colspan="2">&nbsp;</td>
		</tr>

		<tr class="result">
			<td colspan="3"
				style="font-weight:bolder;font-size:12px;padding-left:30px;"
				class="desc"><a href="nav">R&uuml;ckkehr zur MyCoRe Anwendung</a></td>
		</tr>

		<tr class="result">
			<td colspan="3"
				style="font-weight:bolder;font-size:12px;padding-left:30px;"
				class="desc"><a href="nav?path=~login">Benutzerkennung wechseln</a></td>
		</tr>

		<tr class="result">
			<td colspan="3"
				style="font-weight:bolder;font-size:12px;padding-left:30px;"
				class="desc"><a href="nav?path=~logout" class="resultcmd">Abmelden
			und als Gast weiterarbeiten</a></td>
		</tr>
	</table>
</c:when>
<c:otherwise>
    <div id="userStatus"><fmt:message key="Webpage.loging.status.${status}" /></div>
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

