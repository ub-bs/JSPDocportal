<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn" %>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages'/>
<div class="headline">
	<fmt:message key="Nav.AccountRegistered" />
</div>

<table  class="bg_background" >
 <tr>
  <td>
	<div class="subtitle" >Autorenberechtigung für DocPortal </div>
	<br/>
	<p>	<fmt:message key="SWF.User.Registered" />	</p>
	<p>	<fmt:message key="SWF.User.Registered2" />	</p>
	<hr/>
	<p><fmt:message key="Nav.Service.Text1" /></p>
	<mcr:getConfigProperty prop="MCR.WorkflowEngine.Administrator.Email" var="email" defaultValue="mycore@mycore.de" />
	<p><a href="mailto:${email}">${email}</a></p>
 </td>
</tr>
</table>
