<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>
<%@ page import="org.apache.log4j.Logger" %>

<c:catch var="e">
<c:set var="WebApplicationBaseURL" value="${applicationScope.WebApplicationBaseURL}" /> 
<c:set var="mcrid">
   <c:choose>
      <c:when test="${not empty requestScope.id}">${requestScope.id}</c:when>
      <c:otherwise>${param.id}</c:otherwise>
   </c:choose>
</c:set>
<c:set var="debug"  value="false" />  
<c:set var="rule"  value="${param.rule}" />  
<c:set var="returnPath" value="${param.returnPath}" />
<c:set var="processid" value="${param.processid}" />

<mcr:checkAccess var="isAllowed" permission="writedb" key="${mcrid}" />


<c:if test="${isAllowed}">
	<c:if test="${empty rule}">
	    <mcr:getAccessRulesTag var="rule" step="getCurrentRule" mcrid="${mcrid}" processid="${processid}" />
	</c:if>
	<fmt:message key="WF.AcessRuleEditor.headline" />:
	<c:catch var="e1">
		<mcr:receiveMcrObjAsJdom var="mycoreobject" mcrid="${mcrid}" fromWF="true" />
	</c:catch>
	<c:if test="${e1==null}">
     <div class="headline">
         <mcr:simpleXpath jdom="${mycoreobject}" xpath="/mycoreobject/metadata/titles/title[@xml:lang='${requestScope.lang}']" />
         <mcr:simpleXpath jdom="${mycoreobject}" xpath="/mycoreobject/metadata/names/name/fullname" />
	     <mcr:simpleXpath jdom="${mycoreobject}" xpath="/user/@ID" />
     </div>
     </c:if>
	<form name="accessrules" action="${WebApplicationBaseURL}servlets/MCRWorkflowAccessRules" method="get">
		<input type="hidden" name="path" value="~workflow-editaccess">
		<input type="hidden" name="id" value="${mcrid}">
		<input type="hidden" name="returnPath" value="${returnPath}">
		<input type="hidden" name="processid" value="${processid}">

		<table class="editor" cellspacing="0" cellpadding="0" border="0">
			<tr><td colspan="4" id="33" align="left" valign="middle" class="editorCellWithCompLinesOff">
					<fmt:message key="WF.AccessRuleEditor.selectRule" />
			</td></tr>
			<tr><td colspan="4" id="33" align="left" valign="middle" class="editorCellWithCompLinesOff">
			    <mcr:getAccessRulesTag var="rules" step="getRules" mcrid="${mcrid}" processid="${processid}" choosenRule="${rule}" />
				<select name="rule" onchange="document.accessrules.submit()">
					<x:forEach select="$rules//rule">
					   <x:set var="name" select="string(@name)"/>
					   <x:set var="value" select="string(@value)"/>
					   <x:set var="aktiv" select="string(@aktiv)"/>
					   <option value="${value}" ${aktiv}>${name}</option>
					</x:forEach>
				</select>
				<br/>
				<fmt:message key="WF.AccessRuleEditor.RuleHelpText.${rule}" />
				<br/>
			   </td>
			</tr>
		
			<c:if test="${fn:toLowerCase(rule)=='groups'}">
				<tr><td colspan="4" id="33" align="left" valign="middle" class="editorCellWithCompLinesOff"><hr/></td></tr>
				<tr><td colspan="4" id="33" align="left" valign="middle" class="editorCellWithCompLinesOff">
					<fmt:message key="WF.AccessRuleEditor.selectGroups" />
				</td></tr>
				<tr><td colspan="4" id="33" align="left" valign="middle" class="editorCellWithCompLinesOff">
				    <mcr:getAccessRulesTag var="groups" step="getGroups" mcrid="${mcrid}" processid="${processid}" />
					<select name="selectedGroups" size="5" multiple="multiple">
						<x:forEach select="$groups//group">
						   <x:set var="name" select="string(@name)"/>
						   <x:set var="value" select="string(@value)"/>
						   <x:set var="aktiv" select="string(@aktiv)"/>
						   <option value="${value}" ${aktiv}>${name}</option>
						</x:forEach>
					</select>
					<p>
						<x:forEach select="$groups//currGroup">
						    <x:set var="name" select="string(@name)"/>
							<c:out value="${name}, " ></c:out>								
						</x:forEach>
					</p>
				   </td>
				</tr>
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
</c:catch>
<c:if test="${e!=null}">
	An error occured, hava a look in the logFiles!
	<% 
	  Logger.getLogger("test.jsp").error("error", (Throwable) pageContext.getAttribute("e"));   
	%>
</c:if>

