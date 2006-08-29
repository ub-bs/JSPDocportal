<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages'/>

<div class="headline"><fmt:message key="Webpage.intro.Webpage.intro" /></div>
<mcr:includeWebContent file="intro.html" />
<p><fmt:message key="Webpage.intro.ExampleApplication" /></p>
<p><fmt:message key="Webpage.intro.ExplainSearch" /></p>
<p><fmt:message key="Webpage.intro.TwoFundamentalTypes" /></p>
<p><fmt:message key="Webpage.intro.TypesKnowMetadata" /></p>
<p>
<a href="<c:out value="${requestScope.WebApplicationBaseURL}" />nav?path=~searchstart">
      <strong><fmt:message key="Webpage.intro.SearchData" /></strong>
    </a>
</p>
<p><fmt:message key="Webpage.intro.SearchMasks" /></p>
<p><fmt:message key="Webpage.intro.FulltextSearchDependencies" /></p>
<p>

      <a href="<c:out value="${requestScope.WebApplicationBaseURL}" />nav?path=~documentmanagement">
      <strong><fmt:message key="Webpage.intro.TheDocumentManagement" /></strong>
      </a>
</p>
<p><fmt:message key="Webpage.intro.AlsoTwoTypesForAuthors" /></p>
<p><fmt:message key="Webpage.intro.DifferenceAuthorEditor" /></p>
<p><fmt:message key="Webpage.intro.MoreAuthorData" /></p>
<p><fmt:message key="Webpage.intro.Workflow" /></p>
<p style="text-align:right">
<a style="color:white" href="<c:out value="${requestScope.WebApplicationBaseURL}" />/index/robotd/index.html"><fmt:message key="Webpage.intro.AccessForRobots" /> (<fmt:message key="documents" />)</a>
</p>
<p style="text-align:right">
<a style="color:white" href="<c:out value="${requestScope.WebApplicationBaseURL}" />/index/robotp/index.html"><fmt:message key="Webpage.intro.AccessForRobots" /> (<fmt:message key="authors" />)</a>
</p>
<p style="text-align:right">
<a style="color:white" href="<c:out value="${requestScope.WebApplicationBaseURL}" />/index/roboti/index.html"><fmt:message key="Webpage.intro.AccessForRobots" /> (<fmt:message key="institutions" />)</a>
</p>
