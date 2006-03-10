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
  <td >
	<div class="subtitle" >Hiermit beantrage ich, eine Autorenberechtigung für @libri </div>
  </td>
 </tr>
 <tr>
  <td> 	
  	<p>	<fmt:message key="SWF.User.Register" />	</p>
	<p>	<fmt:message key="SWF.User.Register2" />	</p>
    <c:import url="${applicationScope.WebApplicationBaseURL}editor/workflow/editor-registeruser.xml?XSL.editor.source.new=true&XSL.editor.cancel.url=${applicationScope.WebApplicationBaseURL}nav?path=${requestScope.path}&usecase=register-user&page=nav?path=~registered" />    
	<hr/>
	<p><fmt:message key="Nav.Service.Text1" /></p>
	<p><a href="mailto:atlibri@uni-rostock.de">atlibri@uni-rostock.de</a></p>  
  </td>
</tr>
</table>
