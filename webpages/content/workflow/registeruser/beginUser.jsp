<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn" %>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>

<c:set var="WebApplicationBaseURL" value="${applicationScope.WebApplicationBaseURL}" />
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages'/>

  <div class="headline">
	   <fmt:message key="Nav.Application.registerUser" /> - 
	   <fmt:message key="SWF.registerUser.Intro" /> 
	   <fmt:message key="Nav.MyCoRe" /> 
  </div>

  <table>
   <tr>
	<td><fmt:message key="SWF.registerUser.Register" /> </td>
   </tr>
   <tr>
	<td><fmt:message key="SWF.registerUser.Register2" /> </td>
   </tr>
  </table>

   <mcr:includeEditor 
          isNewEditorSource="true" 
          mcrid="" type="registeruser" workflowType="registeruser" processid=""
          step=""  target="MCRRegisterUserWorkflowServlet" nextPath="~registered" editorPath="editor/workflow/editor-registeruser.xml" />        
  
