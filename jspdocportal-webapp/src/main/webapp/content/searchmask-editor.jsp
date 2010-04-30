<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="headline"><fmt:message key="Webpage.editor.title.${fn:replace(param.editor,'/','.')}" /></div>

<table >
 <tr>
    <td valign="top">
		<mcr:includeEditor editorPath="${param.editor}"/>
    </td>
 </tr>  
</table>
	 	