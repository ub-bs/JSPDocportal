<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn" %>
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages'/>

<table class="bg_background">
<tr>
<td>
  <span class="subtitle"><fmt:message key="Author.Subtitle1" /></span>
  <br/>
  <p><fmt:message key="Author.Item1" /></p>
  <p><fmt:message key="Author.Item2" /></p>
  <p><fmt:message key="Author.Item3" /></p>
 
  <br/>
  <span class="subtitle"><fmt:message key="Author.Subtitle2" /></span>
  <br/>
  
	 <p><fmt:message key="Author.Item4" /></p>
	 <p><fmt:message key="Author.Item5" /></p>
	 <p><fmt:message key="Author.Item6" /></p>

  <br/>
  <span class="subtitle"><fmt:message key="Author.Subtitle3" /></span>
  <br/>
  <p><fmt:message key="Author.P1" /></p> 
  <table class="editor" >
		<tr><td><b><fmt:message key="Author.P2" /></b></td></tr>
		<tr>
			<td><fmt:message key="Author.P3" /></td>
			<td><fmt:message key="three-spaces" /></td>
			<td><fmt:message key="Author.P4" /></td>
		</tr>
   </table>
</td>
</tr>
</table>