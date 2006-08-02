<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn" %>
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages'/>

<table class="bg_background">
<tr>
<td>
  <span class="subtitle"><fmt:message key="Webpage.intro.institution.Subtitle1" /></span>
  <br/>
  <p><fmt:message key="Webpage.intro.institution.Item1" /></p>
  <p><fmt:message key="Webpage.intro.institution.Item2" /></p>
  <p><fmt:message key="Webpage.intro.institution.Item3" /></p>
 
  <br/>
  <span class="subtitle"><fmt:message key="Webpage.intro.institution.Subtitle2" /></span>
  <br/>
  
	 <p><fmt:message key="Webpage.intro.institution.Item4" /></p>
	 <p><fmt:message key="Webpage.intro.institution.Item5" /></p>
	 <p><fmt:message key="Webpage.intro.institution.Item6" /></p>

  <br/>
  <span class="subtitle"><fmt:message key="Webpage.intro.institution.Subtitle3" /></span>
  <br/>
  <p><fmt:message key="Webpage.intro.institution.P1" /></p> 
  <table class="editor" >
		<tr><td><b><fmt:message key="Webpage.intro.institution.P2" /></b></td></tr>
		<tr valin="top">
			<td><fmt:message key="Webpage.intro.institution.P3" /></td>
			<td><fmt:message key="WF.common.three-spaces" /></td>
			<td><fmt:message key="Webpage.intro.institution.P4" /></td>
		</tr>
   </table>
</td>
</tr>
</table>
