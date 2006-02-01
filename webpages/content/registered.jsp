<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn" %>
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages'/>
<div class="headline">
	<fmt:message key="Nav.Account" />
</div>

<table  class="bg_background" >
 <tr>
  <td>
	<div class="subtitle" >Autorenberechtigung für @libri </div>
	<br/>
	<p>	<fmt:message key="SWF.User.Registered" />	</p>
	<p>	<fmt:message key="SWF.User.Registered2" />	</p>
	<hr/>
	<p><fmt:message key="Nav.Service.Text1" /></p>
	<p><a href="mailto:atlibri@uni-rostock.de">atlibri@uni-rostock.de</a></p>
 </td>
</tr>
</table>
