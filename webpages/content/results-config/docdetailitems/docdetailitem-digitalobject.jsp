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
		<table border="0" cellpadding="0" cellspacing="0" width="100%">
			<tr valign="top">
				<td>
				<x:forEach select="./digitalobjects">
				    <c:set var="label"  value="dummy" />
					<table border="0" cellpadding="0" cellspacing="0" width="100%">
					
					<x:forEach select="./digitalobject">
					      <x:set var="actlabel"  select="string(./@derivlabel)" />
				      <x:set var="derivid"  select="string(./@derivid)" />
				      
					  <c:if test="${!fn:contains(label,actlabel)}">
                    <mcr:checkAccess permission="read" var="accessallowed" key="${derivid}" />
						  <tr>										  
							<td align="left" valign="bottom" >
								<div class="derivateHeading">
								   <br/><c:out value="${actlabel}" />
								</div>
							</td>	 
						   <c:if test="${accessallowed and empty(param.print)}">
							<td>
							 <a href="<x:out select="concat($WebApplicationBaseURL,'zip?id=',$derivid)" />"
								class="linkButton"><fmt:message key="OMD.zipgenerate" /></a>&#160;&#160;
							</td>
							<td>
               	                <a href="<x:out select="concat($WebApplicationBaseURL,'file/',$derivid,'/','?hosts=',$host)" />" target="_self"><fmt:message key="OMD.details" />&gt;&gt;</a>&#160;&#160; 
               	            </td>
               	            </c:if>
               	          </tr>   										  
               	      </c:if>   
               	      <c:choose>
					  <c:when test="${accessallowed}">
					  <tr>
						<td align="left" valign="top" colspan="3" >
								  <div class="derivate">
								  <x:set var="URL"	select="concat($WebApplicationBaseURL,'file/',./@derivid,'/',./@derivmain,'?hosts=',$host)" />
						  <x:set var="contentType" select="string(./@contentType)" />
						  <x:set var="size" select="string(./@size)" />
						  <x:set var="filename" select="string(./@derivmain)" />
						  <table>
							<tr>
								<td><a href="<x:out select="$URL" />" target="_blank">
												<c:choose>
													<c:when test="${fn:endsWith(filename, '.pdf')}">
														<img src="${WebApplicationBaseURL}images/derivate_pdf.gif" alt="Öffne PDF">
													</c:when>
													<c:when test="${fn:endsWith(filename, '.mp3')}">
														<img src="${WebApplicationBaseURL}images/derivate_mp3.gif" alt="Öffne PDF">
													</c:when>
													<c:when test="${fn:endsWith(filename, '.jpg') or fn:endsWith(filename, '.jpeg') }">
														<img src="${WebApplicationBaseURL}images/derivate_portrait.gif" alt="Öffne Bild" >
													</c:when>
													<c:when test="${fn:endsWith(filename, '.doc')}">
														<img src="${WebApplicationBaseURL}images/derivate_doc.gif" alt="Öffne Dokument" >
													</c:when>
													
													<c:otherwise>
														<img src="${WebApplicationBaseURL}images/derivate_unknown.gif" alt="Öffne Dokument" valign="middle">
													</c:otherwise>												
												</c:choose>
												<x:out select="./@derivmain" /></a>&#160;
								(<c:out value="${size}" /> Bytes)&#160;&#160;</td>
								<c:if test="${fn:contains('gif-jpeg-png', contentType) && size < 100000}">
									<td class="imageInResult"><a href="${URL}"><img	src="${URL}" width="100"></a></td>
								</c:if>
							</tr>
				 		  </table>
						  </div>
						</td>
						  </tr>
					  </c:when>
					  <c:otherwise>
					    <tr>
                   	     <td>
						  <div class="derivate">
                      	     	<x:out select="./@derivmain" />&#160;(<x:out select="./@size" /> Bytes)
                             	     	--- <fmt:message key="OMD.fileaccess.denied" />
                       	  </div>	
                   	     </td>
                       	 </tr>
                        </c:otherwise>
                       </c:choose>
				      <c:set var="label" value="${actlabel}" />	     
					</x:forEach>
				  </table>
				</x:forEach>
			</td>
			</tr>
		</table>
		</td>
	</tr>
</x:forEach>
