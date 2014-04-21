<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>
<%@ page pageEncoding="UTF-8" %>

<fmt:message var="pageTitle" key="WF.registeruser" /> 
<stripes:layout-render name="../../../WEB-INF/layout/default.jsp" pageTitle = "${pageTitle}">
	<stripes:layout-component name="contents">

<c:set var="WebApplicationBaseURL" value="${applicationScope.WebApplicationBaseURL}" />

  <h2>
	   <fmt:message key="WF.registeruser.RegisterUser" /> - 
	   <fmt:message key="WF.registerUser.Intro" /> 
	   <fmt:message key="Nav.MyCoRe" /> 
  </h2>

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
  
</stripes:layout-component>
</stripes:layout-render>