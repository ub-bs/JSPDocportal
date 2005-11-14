<% {
	String authorID = (String)request.getAttribute("authorid");
%>

<table class="frameintern">
<colgroup>
<col width="4%">
<col width="48%" span="2">
</colgroup>
<tr class="result">
<td colspan="3" style="font-weight:bolder;" class="header">Was wollen Sie tun?</td>
</tr>
<tr style="height:10px;">
<td colspan="2">&nbsp;</td>
</tr>

<tr class="result">
<td colspan="3" style="font-weight:bolder;font-size:12px;padding-left:30px;" class="desc">
<a href="nav">R&uuml;ckkehr zur MyCoRe Anwendung</a></td>
</tr>

<tr class="result">
<td colspan="3" style="font-weight:bolder;font-size:12px;padding-left:30px;" class="desc">
<a href="nav?path=~login">Passwort &auml;ndern</a></td>
</tr>

<tr class="result">
<td colspan="3" style="font-weight:bolder;font-size:12px;padding-left:30px;" class="desc">
<a href="nav?path=~login">Benutzerkennung wechseln</a></td>
</tr>

<tr class="result">
<td colspan="3" style="font-weight:bolder;font-size:12px;padding-left:30px;" class="desc">
<a href="nav?query=%2fmycoreobject[%40ID='<%=authorID%>']&type=allpers&mode=ObjectMetadata&status=1&path=~userdetail&hosts=local">Daten der aktuellen Benutzerkennung anzeigen</a></td>
</tr>

<tr class="result">
<td colspan="3" style="font-weight:bolder;font-size:12px;padding-left:30px;" class="desc">
<a href="nav?path=~logout" class="resultcmd">Abmelden und als Gast weiterarbeiten</a></td>
</tr>

</table>

<% }
%>
