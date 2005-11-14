<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn" %>
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages'/>
<div class="headline"><fmt:message key="EditorPageTitle.${fn:replace(param.editor,'/','.')}" /></div>
<c:import url="${requestScope.WebApplicationBaseURL}${param.editor}?XSL.editor.source.new=true&XSL.editor.cancel.url=${requestScope.WebApplicationBaseURL}content/editor-cancel.jsp" />