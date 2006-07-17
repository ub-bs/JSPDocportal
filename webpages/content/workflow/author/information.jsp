<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn" %>
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages'/>

<table class="bg_background">
<tr>
<td>
  <span class="subtitle"><fmt:message key="Webpage.intro.author.Subtitle1" /></span>
  <br/>
  <p><fmt:message key="Webpage.intro.author.Item1" /></p>
  <p><fmt:message key="Webpage.intro.author.Item2" /></p>
  <p><fmt:message key="Webpage.intro.author.Item3" /></p>
 
  <br/>
  <span class="subtitle"><fmt:message key="Webpage.intro.author.Subtitle2" /></span>
  <br/>
  
	 <p><fmt:message key="Webpage.intro.author.Item4" /></p>
	 <p><fmt:message key="Webpage.intro.author.Item5" /></p>
	 <p><fmt:message key="Webpage.intro.author.Item6" /></p>

  <br/>
  <span class="subtitle"><fmt:message key="Webpage.intro.author.Subtitle3" /></span>
  <br/>
  <p><fmt:message key="Webpage.intro.author.P1" /></p> 
  <table class="editor" >
		<tr><td><b><fmt:message key="Webpage.intro.author.P2" /></b></td></tr>
		<tr valin="top">
			<td><fmt:message key="Webpage.intro.author.P3" /></td>
			<td><fmt:message key="WF.common.three-spaces" /></td>
			<td><fmt:message key="Webpage.intro.author.P4" /></td>
		</tr>
   </table>
</td>
</tr>
</table>
