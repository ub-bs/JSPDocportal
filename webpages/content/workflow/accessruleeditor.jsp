<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="org.mycore.frontend.workflowengine.jbpm.MCRWorkflowAccessRuleEditorUtils" %>
<c:catch var="e">
<fmt:setLocale value='${requestScope.lang}'/>
<fmt:setBundle basename='messages'/>
<c:set var="WebApplicationBaseURL" value="${applicationScope.WebApplicationBaseURL}" /> 
<c:set var="mcrid">
   <c:choose>
      <c:when test="${!empty(requestScope.id)}">${requestScope.id}</c:when>
      <c:otherwise>${param.id}</c:otherwise>
   </c:choose>
</c:set>
<c:set var="finish"  value="${param.finish}" />  
<c:set var="rule"  value="${param.rule}" />  
<c:set var="returnPath" value="${param.returnPath}" />
<c:set var="processid" value="${param.processid}" />

<c:choose>
<c:when test="${!empty(finish)}" > 
		<% MCRWorkflowAccessRuleEditorUtils.saveAccessRule((String)pageContext.getAttribute("mcrid"),request.getParameter("rule"), request.getParameter("processid"), request.getParameterValues("param")); %>
	<%-- Save and return --%>
	<%String ziel = (String)pageContext.getAttribute("WebApplicationBaseURL")+"?path="+(String)pageContext.getAttribute("returnPath"); 
		// response.setStatus(response.SC_MOVED_TEMPORARILY);
		// response.setHeader("Location", "?path="+(String)pageContext.getAttribute("returnPath")); 

	  %>
  		<a href="<%=ziel%>">Zurück</a>

	  <!-- break -->
</c:when>
<c:otherwise>
<mcr:checkAccess var="isAllowed" permission="writedb" key="${mcrid}" />
<c:if test="${isAllowed}">
	<c:if test="${empty(rule)}" >
		<c:set var="rule" value="<%= MCRWorkflowAccessRuleEditorUtils.getCurrentRule((String) pageContext.getAttribute("mcrid"), (String) pageContext.getAttribute("processid")) %>" />
	</c:if>
	<mcr:receiveMcrObjAsJdom var="mycoreobject" mcrid="${mcrid}" fromWForDB="workflow" />
     <div class="headline">
        <fmt:message key="WF.AcessRuleEditor.headline" />:
         <mcr:simpleXpath jdom="${mycoreobject}" xpath="/mycoreobject/metadata/titles/title[@xml:lang='${requestScope.lang}']" />
         <mcr:simpleXpath jdom="${mycoreobject}" xpath="/mycoreobject/metadata/names/name/fullname" />
	     <mcr:simpleXpath jdom="${mycoreobject}" xpath="/user/@ID" />
     </div>

	<form name="accessrules" action="nav" method="get">
		<input type="hidden" name="path" value="~workflow-editaccess">
		<input type="hidden" name="id" value="${mcrid}">
		<input type="hidden" name="returnPath" value="${returnPath}">
		<input type="hidden" name="processid" value="${processid}">

		<table class="editor" cellspacing="0" cellpadding="0" border="0">
			<tr><td colspan="4" id="33" align="left" valign="middle" class="editorCellWithCompLinesOff">
					<fmt:message key="WF.AccessRuleEditor.selectRule" />
			</td></tr>
			<tr><td colspan="4" id="33" align="left" valign="middle" class="editorCellWithCompLinesOff">
				<select name="rule" onchange="document.accessrules.submit()">
					<%MCRWorkflowAccessRuleEditorUtils.fillRulesCombobox((String)pageContext.getAttribute("rule"), out); %>
				</select>
			</td></tr>
		
			<c:if test="${fn:toLowerCase(rule)=='groups'}">
				<tr><td colspan="4" id="33" align="left" valign="middle" class="editorCellWithCompLinesOff"><hr/></td></tr>
				<tr><td colspan="4" id="33" align="left" valign="middle" class="editorCellWithCompLinesOff">
					<fmt:message key="WF.AccessRuleEditor.selectGroups" />
				</td></tr>
				<tr><td colspan="4" id="33" align="left" valign="middle" class="editorCellWithCompLinesOff">
					<select name="param" size="5" multiple="multiple">
						<%MCRWorkflowAccessRuleEditorUtils.fillGroupsListbox((String) pageContext.getAttribute("mcrid"), (String) pageContext.getAttribute("processid"),out);%>
					</select>
				</td></tr>
			</c:if>
	
			<tr><td colspan="4" id="33" align="left" valign="middle" class="editorCellWithCompLinesOff"><hr/></td></tr>
			<tr><td>
				<table class="editorPanel" cellspacing="0" cellpadding="0" border="0">
					<tr>
						<td colspan="1" align="left" valign="middle" class="editorCellWithCompLinesOff">
							<input class="editorButton" onClick="self.location.href='${WebApplicationBaseURL}?path=${returnPath}'" value="[Abbrechen]" type="button" style="width:100px"/> 
						 </td>
						<td colspan="1" id="153.3" align="left" valign="middle" class="editorCellWithCompLinesOff">
						  		<input class="editorButton" type="submit" name="finish" value="[Speichern]" style="width:100px"/> 
						</td> 
					</tr>
				</table>
			</td></tr>
		</table>
	</form>
</c:if>
</c:otherwise>
</c:choose>
</c:catch>
<c:if test="${e!=null}">
	An error occured, hava a look in the logFiles!
	<% 
	  Logger.getLogger("test.jsp").error("error", (Throwable) pageContext.getAttribute("e"));   
	%>
</c:if>

