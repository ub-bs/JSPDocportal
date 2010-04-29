<%@ page import="org.mycore.frontend.workflowengine.jbpm.MCRWorkflowConstants" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>

<%--<mcr:getWorkflowEngineVariable 
	pid="${requestScope.pid}" var="personID" 
	workflowVar="<%= MCRWorkflowConstants.WFM_VAR_METADATA_OBJECT_IDS %>" /> --%>

<div class="headline">
   <fmt:message key="WF.person.StartWorkflow" />
</div>

<table cellspacing="3" cellpadding="3" >
   <tr>
      <td>      
        <p><fmt:message key="WF.person.AccessOK" /></p>      
      </td>
   </tr> 
   <tr>
      <td>      
         <img title="" alt="" src="images/greenArrow.gif">
         <a href="${WebApplicationBaseURL}nav?path=~person"><fmt:message key="WF.common.forwardToWorkflow" /></a>
      </td>
   </tr> 
   <tr>
     <td>
     <hr/>
     <p><fmt:message key="Webpage.intro.Service.Hinweis1" /></p>
     <p>
        <mcr:getConfigProperty var="mail" prop="MCR.WorkflowEngine.contactemail.person" defaultValue="mycore@mycore.de" />
        <a href="mailto:${mail}">${mail}</a>
     </p>
     </td>
    </tr>
</table>