<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn" %>
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages'/>

<table class="bg_background">
<tr>
<td>
  <span class="subtitle"><fmt:message key="RegisterAuthor.Subtitle1" /></span>
  <br/>
  <p><fmt:message key="RegisterAuthor.Item1" /></p>
  <p><fmt:message key="RegisterAuthor.Item2" /></p>
  <p><fmt:message key="RegisterAuthor.Item3" /></p>
 
  <br/>
  <span class="subtitle"><fmt:message key="RegisterAuthor.Subtitle2" /></span>
  <br/>
  
	 <p><fmt:message key="RegisterAuthor.Item4" /></p>
	 <p><fmt:message key="RegisterAuthor.Item5" /></p>
	 <p><fmt:message key="RegisterAuthor.Item6" /></p>

  <br/>
  <span class="subtitle"><fmt:message key="RegisterAuthor.Subtitle3" /></span>
  <br/>
  <p><fmt:message key="RegisterAuthor.P1" /></p> 
  <table class="editor" >
		<tr><td><b><fmt:message key="RegisterAuthor.P2" /></b></td></tr>
		<tr>
			<td><fmt:message key="RegisterAuthor.P3" /></td>
			<td><fmt:message key="three-spaces" /></td>
			<td><fmt:message key="RegisterAuthor.P4" /></td>
		</tr>
   </table>
</td>
</tr>
</table>