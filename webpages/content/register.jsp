<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn" %>
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages'/>
<div class="headline">
	<fmt:message key="Nav.User.Registration" />
</div>

<table  class="bg_background" >
 <tr>
  <td >
	<div class="subtitle" ><fmt:message key="Nav.User.Register.ApplyForPermission" /></div>
  </td>
 </tr>
 <tr>
  <td> 	
  	<p>	<fmt:message key="SWF.User.Register1" />	</p>
	<p>	<fmt:message key="SWF.User.Register2" />	</p>
    <c:import url="${applicationScope.WebApplicationBaseURL}editor/workflow/editor-registeruser.xml?XSL.editor.source.new=true&XSL.editor.cancel.url=${applicationScope.WebApplicationBaseURL}nav?path=${requestScope.path}&usecase=register-user&page=nav?path=~registered" />    
	<hr/>
	<p><fmt:message key="Nav.Service.Text1" /></p>
	<p><a href="mailto:jspdocportal@mycore.de">jspdocportal@mycore.de</a></p>  
  </td>
</tr>
</table>
