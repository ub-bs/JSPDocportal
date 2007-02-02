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
			<tr valign="top">
				<x:forEach select="./metavalues">
					<td valign="top" align="left">
					<x:set var="introkey" select="string(./@introkey)" />
					<table border="0" cellpadding="0" cellspacing="3px">
						<x:forEach select="./metavalue">
							<tr valign="top">
								<td valign="top" align="left"><c:if
									test="${fn:length(introkey) > 0 }">
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
								</c:if> <x:choose>
									<x:when select="../@type = 'messagekey'">
										<!-- ist schon im introkey behandelt -->
									</x:when>
									<x:when select="../@type = 'BooleanValues'">
										<x:set var="booleanKey" select="concat(./@type,'-',./@text)" />
										<fmt:message key="${booleanKey}" />
									</x:when>
									<x:when select="../@type = 'AuthorJoin'">
										<x:set var="authorjoinKey"
											select="concat(./@type,'-',./@text)" />
										<a href="<x:out select="./@href" />"
											target="<x:out select="./@target" />"><fmt:message
											key="${authorjoinKey}" /></a>
									</x:when>
									<x:when select="./@href != ''">
										<a href="<x:out select="./@href" />"
											target="<x:out select="./@target" />"><x:out
											select="./@text" /></a>
									</x:when>
									<x:otherwise>
										<x:out select="./@text" escapeXml="false" />
									</x:otherwise>
								</x:choose></td>
							</tr>
						</x:forEach>
					</table>
					</td>
				</x:forEach>
			</tr>
		</table>
		</td>
	</tr>
</x:forEach>
