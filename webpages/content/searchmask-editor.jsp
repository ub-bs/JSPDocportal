<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn" %>
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages'/>
<div class="headline"><fmt:message key="EditorPageTitle.${fn:replace(param.editor,'/','.')}" /></div>

<table >
 <tr>
    <td valign="top">
     <c:import url="${applicationScope.WebApplicationBaseURL}${param.editor}?XSL.editor.source.new=true&XSL.editor.cancel.url=${applicationScope.WebApplicationBaseURL}nav?path=${requestScope.path}" />    
    </td>
	<c:if test="${fn:contains(param.editor,'AllCodices')}">
		<td width="30">&nbsp;</td>
	  	<td align="right" rowspan="2"><img src="http://www.uni-rostock.de/ub/SON300.gif" border="0" /></td> 		 	
	  	<td width="10">&nbsp;</td>
	</c:if>
 </tr>  	
  <c:if test="${fn:contains(param.editor,'AllCodices')}">
 	<tr valign="bottom" >
 	  <td height="200" valign="bottom" >
  	  <div class="textblock2">
  	  		<p><fmt:message key="Codice.Hinweis1" /></p>
			<p><fmt:message key="Codice.Hinweis2" /></p>
	  </div>		
 	  </td>
 	 </tr>
  </c:if>
</table>
	 	
	 	