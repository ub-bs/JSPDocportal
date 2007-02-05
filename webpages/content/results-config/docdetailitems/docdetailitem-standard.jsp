<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr"%>
<fmt:setLocale value='${requestScope.lang}' />
<fmt:setBundle basename='messages' />
<x:forEach select="$data">
	<x:set var="nameKey" select="string(./@name)" />

	<tr>
		<td class="metaname"><c:if test="${fn:length(nameKey) > 0 }">
			<fmt:message key="${nameKey}" />:
            </c:if></td>
		<td class="metavalue">
		<table border="0" cellpadding="0" cellspacing="0">
			<tr valign="top"><td>
              <x:forEach select="./metavalues">
                     <x:set var="separator" select="./@separator" />
                     <x:set var="terminator" select="./@terminator" />
					<x:set var="introkey" select="string(./@introkey)" />
                        <x:forEach select="./metavalue">
                        <x:if select="generate-id(../metavalue[position() = 1]) != generate-id(.)">
                           <x:out select="$separator" escapeXml="false" />
                        </x:if>
                        <c:if test="${fn:length(introkey) > 0 }">
									<x:choose>
										<x:when select="../@type = 'messagekey'">
											<x:set var="val" select="string(./@text)" />
											<c:set var="messagekey" value="${introkey}.${val }" />
											<fmt:message key="${messagekey}" />
										</x:when>
										<x:otherwise>
											<fmt:message key="${introkey}" />
										</x:otherwise>
									</x:choose>
								</c:if>
                        <x:choose>
	                        <x:when select="../@type = 'messagekey'">
	                        	<!-- do nothing, allready dealt with in introkey -->
	                        </x:when>
                           <x:when select="../@type = 'BooleanValues'">
                              <x:set var="booleanKey" select="concat(./@type,'-',./@text)" />
                              <fmt:message key="OMD.${booleanKey}" />
                           </x:when>
                           <x:when select="../@type = 'AuthorJoin'">
                              <x:set var="authorjoinKey" select="concat(./@type,'-',./@text)" />
                              <x:set var="hrefquery" select="concat($WebApplicationBaseURL,'servlets/MCRJSPSearchServlet?mask=~searchstart-indexcreators&', ./@querytext) " />
                              <a href="${hrefquery}" target="<x:out select="./@target" />"><fmt:message key="OMD.${authorjoinKey}" /></a>                                 
                           </x:when>                                     
                           <x:when select="./@href != ''">
                              <a href="<x:out select="./@href" />" target="<x:out select="./@target" />"><x:out select="./@text" /></a>
                           </x:when>
                           <x:otherwise>
                              <x:out select="./@text" escapeXml="./@escapeXml" />
                           </x:otherwise>
                        </x:choose>
                     </x:forEach>
                   
                     <x:if select="generate-id(../metavalues[position() = last()]) != generate-id(.)">
                        <x:out select="$terminator" escapeXml="false" />
                     </x:if>                               
                  </x:forEach> 
                  </td>
                  </tr>
		</table>
		</td>
	</tr>
</x:forEach>
