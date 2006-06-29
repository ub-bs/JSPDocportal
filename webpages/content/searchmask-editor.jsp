<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>

<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages'/>
<div class="headline"><fmt:message key="Webpage.editor.title.${fn:replace(param.editor,'/','.')}" /></div>


<table >
 <tr>
    <td valign="top">
	<c:url var="url" value="${applicationScope.WebApplicationBaseURL}${param.editor}">
	    <c:param name="XSL.editor.source.new" value="true" />
	    <c:param name="XSL.editor.cancel.url" value="${WebApplicationBaseURL}" />
	    <c:param name="lang" value="${requestScope.lang}" />
	    <c:param name="MCRSessionID" value="${requestScope.sessionID}"/>
	  </c:url>
	<c:import url="${url}" />  
    </td>
 </tr>  
 <tr>
   <td>
   	<!--  Comments for using -->
   </td>
 </tr>
</table>