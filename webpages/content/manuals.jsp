<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn" %>
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages'/>
<div class="headline"><fmt:message key="Manuals" /></div>
<p><c:import url="content/node.jsp" /></p>
<p><fmt:message key="DocumentManagement.Applet" /></p>
<p><fmt:message key="DocumentManagement.FetchLogin" /></p>
<p><fmt:message key="DocumentManagement.InstitutionData" /></p>
<p><fmt:message key="DocumentManagement.AuthorData" /></p>
<p><fmt:message key="DocumentManagement.EditData" /></p>
<p><fmt:message key="DocumentManagement.UploadData" /></p>
