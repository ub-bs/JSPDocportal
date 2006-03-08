<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn" %>
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages'/>
<div class="headline"><fmt:message key="Nav.Application.dissertation" /></div>
<p><c:import url="content/node.jsp" /></p>

<table class="bg_background">
<tr>
<td>
  <span class="subtitle"><fmt:message key="Dissertation.Subtitle1" /></span>
  <br/>
  <p><fmt:message key="Dissertation.Item1" /></p>
  <p><fmt:message key="Dissertation.Item2" /></p>
  <p><fmt:message key="Dissertation.Item3" /></p>
 
  <br/>
  <span class="subtitle"><fmt:message key="Dissertation.Subtitle2" /></span>
  <br/>
  
	 <p><fmt:message key="Dissertation.Item4" /></p>
	 <p><fmt:message key="Dissertation.Item5" /></p>
	 <p><fmt:message key="Dissertation.Item6" /></p>
	 <p><fmt:message key="Dissertation.Item7" /></p>
  <br/>
  <span class="subtitle"><fmt:message key="Dissertation.Subtitle3" /></span>
  <br/>
  <p><fmt:message key="Dissertation.P1" /></p> 
  <table class="editor" >
		<tr><td><b><fmt:message key="Dissertation.P2" /></b></td></tr>
		<tr>
			<td><fmt:message key="Dissertation.P3" /></td>
			<td><fmt:message key="three-spaces" /></td>
			<td><fmt:message key="Dissertation.P4" /></td>
		</tr>
   </table>
</td>
</tr>
</table>