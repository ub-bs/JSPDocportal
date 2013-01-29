<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.mycore.org/jspdocportal/base.tld" prefix="mcr" %>

<mcr:getWorkflowEngineVariable pid="${requestScope.pid}" var="urn" workflowVar="${applicationScope.constants.reservatedUrnWorkflowVariable}" /> 

<h2>
   <fmt:message key="WF.xmetadiss.begin" />
</h2>

<table>
	<c:if test="${not empty urn}">
		<tr>
			<th><fmt:message key="WF.xmetadiss.URN" />: </th>
		    <td><strong><c:out value="${urn}" /></strong> 
		       	<br/><em><fmt:message key="WF.publication.URN.Hinweis" /></em>
		    </td>
		</tr>     
	</c:if>
 </table>

<img title="" alt="" src="images/greenArrow.gif" />
<a href="${WebApplicationBaseURL}nav?path=~xmetadiss"><fmt:message key="WF.common.forwardToWorkflow" /></a>
<hr/>
<p><fmt:message key="Webpage.intro.Service.Hinweis1" /></p>
<p>
	<mcr:getConfigProperty var="mail" prop="MCR.WorkflowEngine.contactemail.xmetadiss" defaultValue="mycore@mycore.de" />
    <a href="mailto:${mail}">${mail}</a>
</p>
   