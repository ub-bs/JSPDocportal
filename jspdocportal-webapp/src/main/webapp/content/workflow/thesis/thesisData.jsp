<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.mycore.org/jspdocportal/base.tld" prefix="mcr" %>

<mcr:getWorkflowEngineVariable pid="${requestScope.pid}" var="urn" workflowVar="${applicationScope.constants.reservatedUrnWorkflowVariable}" /> 


<h2>
   <fmt:message key="WF.thesis.begin" />
</h2>

<table>
	<c:if test="${not empty urn}">
		   <tr valign="top">
		        <td class="metaname"><fmt:message key="WF.xmetadiss.URN" /> </td>
		        <td class="metavalue"><b><c:out value="${urn}" /></b> 
		         <br/> <small><i><fmt:message key="WF.publication.URN.Hinweis" /></i></small>
		      </td>
		   </tr>     
		</c:if>
</table>

<img title="" alt="" src="images/greenArrow.gif">
<a href="${WebApplicationBaseURL}nav?path=~thesis"><fmt:message key="WF.common.forwardToWorkflow" /></a>
<hr />
<p><fmt:message key="Webpage.intro.Service.Hinweis1" /></p>
<p>
     <mcr:getConfigProperty var="mail" prop="MCR.WorkflowEngine.contactemail.thesis" defaultValue="mycore@mycore.de" />
     <a href="mailto:${mail}">${mail}</a>
</p>
