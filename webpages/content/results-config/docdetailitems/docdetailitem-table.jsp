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
            <td class="metaname">
             <c:if test="${! empty(nameKey)}">
	             <fmt:message key="${nameKey}" />:
             </c:if>
            </td>
            <td class="metavalue">
               <x:set var="colnum" select="count(./metavalues)" />
               <x:set var="rownum" select="count(./metavalues[1]/metavalue)" />
	           <x:set var="rownum2" select="count(./metavalues[2]/metavalue)" />
               <c:if test="${rownum2 > rownum}" >
                  <x:set var="rownum" select="count(./metavalues[2]/metavalue)" />
               </c:if>   
               <table border="0" cellpadding="0" cellspacing="4px">
                   <c:forEach var="i" begin="1" end="${rownum}">
                     <tr>
                      <c:forEach var="j" begin="1" end="${colnum}">                              
                        <c:if test="${j >1}">
						 	<td width="15px" /> 
                        </c:if>
                        <td valign="top">
                          <div style="min-width:60px" >
                          <x:set var="ikey" select="string(./metavalues[$j]/@introkey)" />   			                               
					      <x:choose>
                             <x:when select="./metavalues[$j]/@type = 'messagekey'">
                              <x:set var="val" select="string(./metavalues[$j]/metavalue[$i]/@text)" />
                              <c:set var="messagekey" value="${ikey}.${val }" />
   							  <fmt:message key="${messagekey}" />
					 		 </x:when>
					         <x:otherwise>
								<c:if test="${!empty(ikey)}">   			                               					         
			                        <fmt:message key="${ikey}" />
	                            </c:if>    
                                <x:out select="./metavalues[$j]/metavalue[$i]/@text" escapeXml="false" />
							 </x:otherwise>   
						  </x:choose>                              
                          </div>
                         </td>
                        </c:forEach>
                       </tr>
                   </c:forEach>
                </table>
           </td>
          </tr>
</x:forEach>