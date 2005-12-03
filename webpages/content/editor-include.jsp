<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn" %>
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages'/>
<c:url var="url" value="${applicationScope.WebApplicationBaseURL}editor/workflow/${param.id}.xml">
	<c:param name="XSL.editor.session.id" value="${param['XSL.editor.session.id']}" />
</c:url>
<c:import url="${url}" />