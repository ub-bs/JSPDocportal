<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn" %>
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages'/>

<table class="bg_background">
<tr>
<td>
  <b><fmt:message key="Webpage.intro.publications.Subtitle1" /></b>
  <br/>
  <p><fmt:message key="Webpage.intro.publications.Text1" /></p>
  <br/>
  <b><fmt:message key="Webpage.intro.publications.Subtitle2" /></b>
  <br/>
	  <p><fmt:message key="Webpage.intro.publications.Text2" /></p>
  <br/>
  <b><fmt:message key="Webpage.intro.publications.Subtitle3" /></b>
  <br/>
	  <p><fmt:message key="Webpage.intro.publications.Text3" /></p>
  <br/>
</td>
</tr>
</table>
