<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages'/>

<div class="headline"><fmt:message key="Webpage.browse.generalTitle" /></div>
<p><c:import url="content/node.jsp" /></p>
<p><fmt:message key="Browse.Text1" /></p>
<p><fmt:message key="Browse.Text2" /></p>

