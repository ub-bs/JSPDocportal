<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages'/>

<div class="headline"><fmt:message key="Start.Start" /></div>
<p><fmt:message key="Start.ExampleApplication" /></p>
<p><fmt:message key="Start.ExplainSearch" /></p>
<p><fmt:message key="Start.TwoFundamentalTypes" /></p>
<p><fmt:message key="Start.TypesKnowMetadata" /></p>
<p>
<a href="<c:out value="${requestScope.WebApplicationBaseURL}" />nav?path=~searchstart">
      <strong><fmt:message key="Start.SearchData" /></strong>
    </a>
</p>
<p><fmt:message key="Start.SearchMasks" /></p>
<p><fmt:message key="Start.FulltextSearchDependencies" /></p>
<p>

      <a href="<c:out value="${requestScope.WebApplicationBaseURL}" />nav?path=~documentmanagement">
      <strong><fmt:message key="Start.TheDocumentManagement" /></strong>
      </a>
</p>
<p><fmt:message key="Start.AlsoTwoTypesForAuthors" /></p>
<p><fmt:message key="Start.DifferenceAuthorEditor" /></p>
<p><fmt:message key="Start.MoreAuthorData" /></p>
<p><fmt:message key="Start.Workflow" /></p>
<p style="text-align:right">
<a style="color:white" href="<c:out value="${requestScope.WebApplicationBaseURL}" />/index/robotd/index.html"><fmt:message key="Start.AccessForRobots" /> (<fmt:message key="documents" />)</a>
</p>
<p style="text-align:right">
<a style="color:white" href="<c:out value="${requestScope.WebApplicationBaseURL}" />/index/robotp/index.html"><fmt:message key="Start.AccessForRobots" /> (<fmt:message key="authors" />)</a>
</p>
<p style="text-align:right">
<a style="color:white" href="<c:out value="${requestScope.WebApplicationBaseURL}" />/index/roboti/index.html"><fmt:message key="Start.AccessForRobots" /> (<fmt:message key="institutions" />)</a>
</p>