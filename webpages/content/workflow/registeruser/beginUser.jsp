<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn" %>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>

<c:set var="WebApplicationBaseURL" value="${applicationScope.WebApplicationBaseURL}" />

  <div class="headline">
	   <fmt:message key="WF.registeruser.RegisterUser" /> - 
	   <fmt:message key="WF.registerUser.Intro" /> 
	   <fmt:message key="Nav.MyCoRe" /> 
  </div>

  <table>
   <tr>	<td>
   	<fmt:message key="WF.registerUser.Register" /> <br/>
   	<fmt:message key="WF.registerUser.Register2" /> <br/>
   	<fmt:message key="WF.registerUser.Register3" /> 
   	</td>
   </tr>
  </table>

   <mcr:includeEditorInWorkflow 
          isNewEditorSource="true" 
          mcrid="" type="registeruser" workflowType="registeruser" processid=""
          step=""  target="MCRRegisterUserWorkflowServlet" nextPath="~registered" 
          editorPath="editor/workflow/editor-registeruser.xml" />        
  
