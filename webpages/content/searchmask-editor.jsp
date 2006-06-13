<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn" %>
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages'/>
<div class="headline"><fmt:message key="Webpage.editor.title.${fn:replace(param.editor,'/','.')}" /></div>
<table >
 <tr>
    <td valign="top">
     <c:import url="${applicationScope.WebApplicationBaseURL}${param.editor}?XSL.editor.source.new=true&XSL.editor.cancel.url=${applicationScope.WebApplicationBaseURL}nav?path=${requestScope.path}" />
    </td>
 </tr>  
 <tr>
   <td>
   	<!--  Comments for using -->
   </td>
 </tr>
</table>