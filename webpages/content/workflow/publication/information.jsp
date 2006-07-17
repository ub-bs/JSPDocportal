<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn" %>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages'/>

<table class="bg_background">
<tr>
<td>
  <span class="subtitle"><fmt:message key="Webpage.intro.publications.Subtitle1" /></span>
  <br/>
  <p><fmt:message key="Webpage.intro.publications.Text1" /></p>
  <br/>
  <span class="subtitle"><fmt:message key="Webpage.intro.publications.Subtitle2" /></span>
  <br/>
  <p><fmt:message key="Webpage.intro.publications.Text2" /></p>
  <br/>
  <span class="subtitle"><fmt:message key="Webpage.intro.publications.Subtitle3" /></span>
  <br/>
  <p><fmt:message key="Webpage.intro.publications.Text3" /></p>
  <br/>
  <p><fmt:message key="Webpage.intro.publications.P1" /></p> 

  <table class="editor" >
		<tr><td><b><fmt:message key="Webpage.intro.publications.P2" /></b></td></tr>
		<tr valin="top">
			<td><fmt:message key="Webpage.intro.publications.P3" /></td>
			<td><fmt:message key="WF.common.three-spaces" /></td>
			<td><fmt:message key="Webpage.intro.publications.P4" /></td>
		</tr>
   </table>
 </td>
</tr>
</table>
