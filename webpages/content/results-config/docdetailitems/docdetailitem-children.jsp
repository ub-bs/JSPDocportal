<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr"%>
<fmt:setLocale value='${requestScope.lang}' />
<fmt:setBundle basename='messages' />
<c:set var="host" value="local" />
<x:forEach select="$data">
 <x:set var="nameKey" select="string(./@name)" />
	<tr>
		<td class="metaname"><c:if test="${fn:length(nameKey) > 0 }">
			<fmt:message key="${nameKey}" />:
            </c:if></td>
		<td class="metavalue">
		<table border="0" cellpadding="0" cellspacing="4px" width="100%">
			<tr valign="top">
				<td><ul style="line-height:1.5em; list-style:none;list-style-position: inside;margin: 0px;padding: 0px;">
					<x:forEach select="./childs/child">
					      <x:set var="childID"  select="string(./@childID)" />							
								<jsp:include page="docdetailitem-children-sub.jsp" flush="true" >
				    				<jsp:param name="mcrid" value="${childID}" />
								</jsp:include>						     
					</x:forEach>
				
				</ul></td>
			</tr>
		</table>
		</td>
	</tr>
</x:forEach>
