<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>

<div class="headline"><fmt:message key="Webpage.intro.Webpage.intro" /></div>
<mcr:includeWebContent file="index_introtext.html" />

<p style="text-align:right">
<a style="color:white" href="<c:out value="${requestScope.WebApplicationBaseURL}" />/index/robotd/index.html"><fmt:message key="Webpage.intro.AccessForRobots" /> (<fmt:message key="documents" />)</a>
</p>
<p style="text-align:right">
<a style="color:white" href="<c:out value="${requestScope.WebApplicationBaseURL}" />/index/robotp/index.html"><fmt:message key="Webpage.intro.AccessForRobots" /> (<fmt:message key="authors" />)</a>
</p>
<p style="text-align:right">
<a style="color:white" href="<c:out value="${requestScope.WebApplicationBaseURL}" />/index/roboti/index.html"><fmt:message key="Webpage.intro.AccessForRobots" /> (<fmt:message key="institutions" />)</a>
</p>
