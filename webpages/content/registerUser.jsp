<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn" %>

<div class="headline">	<fmt:message key="Nav.Account" /> </div>


<mcr:includeWebContent file="registeruser.html" />	
<c:import url="${applicationScope.WebApplicationBaseURL}editor/workflow/editor-registeruser.xml?XSL.editor.source.new=true&XSL.editor.cancel.url=${applicationScope.WebApplicationBaseURL}nav?path=${requestScope.path}&usecase=register-user&page=nav?path=~registered" />    
