<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>
<fmt:setLocale value='${requestScope.lang}'/>
<fmt:setBundle basename='messages'/>

<x:forEach select="$data">
               
               <x:set var="nameKey" select="string(./@name)" />
               <tr>
                <td class="metaname"><fmt:message key="${nameKey}" />:</td>
                <td class="metavalue">
                   <x:set var="colnum" select="count(./metavalues)" />
                   <x:set var="rownum" select="count(./metavalues[1]/metavalue)" />
		           <x:set var="rownum2" select="count(./metavalues[2]/metavalue)" />
                   <c:if test="${rownum2 > rownum}" >
                      <x:set var="rownum" select="count(./metavalues[2]/metavalue)" />
                   </c:if>   
                   <table border="0" cellpadding="0" cellspacing="3px">
                      <!-- 
                      <x:if select="string-length(./metavalues/@introkey) > 0" >                         
                       <tr>
                       <c:forEach var="j" begin="1" end="${colnum}">
							<th>
		                      <x:if select="string-length(./metavalues[$j]/@introkey) > 0" >                         
	                            <x:set var="introkey" select="string(./metavalues[$j]/@introkey)" />                         
								<b><fmt:message key="${introkey}" /></b>							
								<hr align="left" width="100"/>
		              	      </x:if>
							</th>
                       </c:forEach>
                       </tr>
               	       </x:if>
               	        -->
                       <c:forEach var="i" begin="1" end="${rownum}">
                            <tr>
                              <c:forEach var="j" begin="1" end="${colnum}">                              
                                 <td valign="top">
                                 <c:choose>
	                                 <c:when test="${(j eq 1) and (colnum gt 1)}">
										 <div style="min-width:100px; padding-right:10px" > 
	                                 </c:when>
	                                 <c:otherwise>
	                                 	 <div style="min-width:110px" >
	                                 </c:otherwise>
                                 </c:choose>

                                
									<x:choose>
 	                                 <x:when select="./metavalues[$j]/@type = 'messagekey'">
		                               <x:set var="val" select="string(./metavalues[$j]/metavalue[$i]/@text)" />
			                           <x:set var="ikey" select="string(./metavalues[$j]/@introkey)" />   			                               
		                               <c:set var="messagekey" value="${ikey}.${val }" />
	      							   <fmt:message key="${messagekey}" />
									 </x:when>
									 <x:otherwise>
	                                   <x:out select="./metavalues[$j]/metavalue[$i]/@text" escapeXml="false" />
									 </x:otherwise>   
									</x:choose>       
									&nbsp;                          
                                 </div>
                                 </td>
                               </c:forEach>
                           </tr>
                       </c:forEach>
                    </table>
               </td>
              </tr>
</x:forEach>