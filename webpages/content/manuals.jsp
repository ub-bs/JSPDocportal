<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn" %>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages'/>
<div class="headline"><fmt:message key="Manuals" /></div>
<p><c:import url="content/node.jsp" /></p>
<mcr:includeWebContent file="manuals_introtext.jsp" />
<!-- 
<p><fmt:message key="Webpage.admin.DocumentManagement.Applet" /></p>
<p><fmt:message key="Webpage.admin.DocumentManagement.FetchLogin" /></p>
<p><fmt:message key="Webpage.admin.DocumentManagement.InstitutionData" /></p>
<p><fmt:message key="Webpage.admin.DocumentManagement.AuthorData" /></p>
<p><fmt:message key="Webpage.admin.DocumentManagement.EditData" /></p>
<p><fmt:message key="Webpage.admin.DocumentManagement.UploadData" /></p> -->
