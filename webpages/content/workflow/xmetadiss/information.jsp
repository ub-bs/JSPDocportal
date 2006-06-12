<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn" %>
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages'/>

<table class="bg_background">
<tr>
<td>
  <span class="subtitle"><fmt:message key="Webpage.intro.xmetadiss.Subtitle1" /></span>
  <br/>
  <p><fmt:message key="Webpage.intro.xmetadiss.Item1" /></p>
  <p><fmt:message key="Webpage.intro.xmetadiss.Item2" /></p>
  <p><fmt:message key="Webpage.intro.xmetadiss.Item3" /></p>
 
  <br/>
  <span class="subtitle"><fmt:message key="Webpage.intro.xmetadiss.Subtitle2" /></span>
  <br/>
  
	 <p><fmt:message key="Webpage.intro.xmetadiss.Item4" /></p>
	 <p><fmt:message key="Webpage.intro.xmetadiss.Item5" /></p>
	 <p><fmt:message key="Webpage.intro.xmetadiss.Item6" /></p>
	 <p><fmt:message key="Webpage.intro.xmetadiss.Item7" /></p>
  <br/>
  <span class="subtitle"><fmt:message key="Webpage.intro.xmetadiss.Subtitle3" /></span>
  <br/>
  <p><fmt:message key="Webpage.intro.xmetadiss.P1" /></p> 
  <table class="editor" >
		<tr><td><b><fmt:message key="Webpage.intro.xmetadiss.P2" /></b></td></tr>
		<tr>
			<td><fmt:message key="Webpage.intro.xmetadiss.P3" /></td>
			<td><fmt:message key="WF.common.three-spaces" /></td>
			<td><fmt:message key="Webpage.intro.xmetadiss.P4" /></td>
		</tr>
   </table>
</td>
</tr>
</table>
